package com.opdev.request;

import com.opdev.company.dto.RequestCreateDto;
import com.opdev.company.dto.TermCreateDto;
import com.opdev.company.service.CompanyService;
import com.opdev.exception.ApiBadRequestException;
import com.opdev.exception.ApiBadRequestStatusException;
import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.mail.NullHireMailSender;
import com.opdev.message.AvailableChatService;
import com.opdev.model.company.Company;
import com.opdev.model.request.Request;
import com.opdev.model.request.RequestStatus;
import com.opdev.model.request.TalentTermRequest;
import com.opdev.model.request.TalentTermRequestStatus;
import com.opdev.model.request.TalentTermSnapshot;
import com.opdev.model.talent.Talent;
import com.opdev.model.term.TalentTerm;
import com.opdev.model.user.Notification;
import com.opdev.model.user.User;
import com.opdev.notification.NotificationFactory;
import com.opdev.notification.NotificationService;
import com.opdev.repository.RequestRepository;
import com.opdev.talent.TalentService;
import com.opdev.util.encoding.TalentIdEncoder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository repository;
    private final CompanyService companyService;
    private final AvailableChatService availableChatService;
    private final TalentService talentService;
    private final TalentIdEncoder talentIdEncoder;
    private final NotificationService notificationService;
    private final NullHireMailSender mailSender;

    @Override
    @Transactional
    public Request create(@NonNull RequestCreateDto newRequestDto,
                          @NonNull String username) {
        Company foundCompany = companyService.getByUsername(username);
        Long talentId = talentIdEncoder.decode(newRequestDto.getTalentId());
        Talent foundTalent = talentService.getById(talentId);
        validateAcceptedOrPendingNotExists(foundTalent, foundCompany);
        List<TalentTerm> talentTerms = foundTalent.getTalentTerms();

        validateTerms(talentTerms.stream().map(TalentTerm::getId).collect(Collectors.toSet()),
                newRequestDto.getTerms().stream().map(TermCreateDto::getTermId).collect(Collectors.toSet()));

        Request newRequest = Request.builder()
                .talent(foundTalent)
                .status(RequestStatus.PENDING)
                .company(foundCompany)
                .note(newRequestDto.getNote())
                .seenByCompany(true)
                .jobDescription(newRequestDto.getJobDescription())
                .build();

        Map<Long, TalentTerm> talentTermsMap = talentTerms.stream()
                .collect(Collectors.toMap(TalentTerm::getId, Function.identity()));
        List<TalentTermRequest> newTalentTermRequests = new ArrayList<>();

        for (TermCreateDto term : newRequestDto.getTerms()) {
            TalentTerm talentTerm = talentTermsMap.get(term.getTermId());
            checkIfMandatoryTermIsAccepted(talentTerm, term);

            TalentTermRequestStatus status = term.getCounterOffer() != null && !term.getCounterOffer().trim().isEmpty() ? TalentTermRequestStatus.COUNTER_OFFER_COMPANY : term.getStatus();
            if (status.equals(TalentTermRequestStatus.COUNTER_OFFER_COMPANY)) {
                newRequest.setStatus(RequestStatus.COUNTER_OFFER_COMPANY);
            }
            TalentTermRequest newTalentTermRequest = TalentTermRequest.builder()
                    .talentTermSnapshot(TalentTermSnapshot.fromTalentTerm(talentTerm))
                    .request(newRequest)
                    .counterOffer(term.getCounterOffer())
                    .status(status)
                    .build();
            newTalentTermRequests.add(newTalentTermRequest);
        }
        newRequest.setTalentTermRequests(newTalentTermRequests);
        newRequest.setCreatedBy(foundCompany.getUser());
        newRequest.setCreatedOn(Instant.now());
        return repository.save(newRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Request> findByStatusForCompany(@NonNull final String username,
                                                @NonNull final List<RequestStatus> statuses) {
        Company foundCompany = companyService.getByUsername(username);
        return repository.findByCompanyAndStatusIn(foundCompany, statuses);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Request> findByStatusForTalent(@NonNull final String username,
                                               @NonNull final List<RequestStatus> statuses,
                                               @NonNull final Pageable pageable) {
        Talent foundTalent = talentService.getByUsername(username);
        return repository.findByTalentAndStatusIn(foundTalent, statuses, pageable);
    }

    @Override
    @Transactional
    public Request editStatusForTalent(@NonNull final String username, @NonNull final Long id, @NonNull final RequestStatus newStatus) {
        validateStatus(newStatus);
        Talent foundTalent = talentService.getByUsername(username);
        Request found = getByIdAndTalent(id, foundTalent);
        found.setStatus(newStatus);
        if (newStatus == RequestStatus.ACCEPTED) {
            availableChatService.create(found.getCompany(), foundTalent);
            Notification acceptedNotification = NotificationFactory.createAcceptedNotificationForCompany(found.getId(), found.getCompany().getUser(), found.getNote(), foundTalent.getFullName());
            notificationService.create(acceptedNotification);
            Notification acceptedNotificationForTalent = NotificationFactory.createAcceptedNotificationForTalent(found.getId(), foundTalent.getUser(), found.getCompany().getName());
            notificationService.create(acceptedNotificationForTalent);
            mailSender.sendRequestAcceptedEmail(found.getCompany().getUser().getUsername(), foundTalent.getFullName(), found.getNote());
        } else if (newStatus == RequestStatus.REJECTED) {
            Notification rejectedNotification = NotificationFactory.createRejectedNotificationForCompany(found.getId(), found.getCompany().getUser(), found.getNote());
            notificationService.create(rejectedNotification);
        }
        return edit(found, foundTalent.getUser());
    }

    @Override
    @Transactional
    public Request rejectByCompany(final String username, final Long id) {
        Company foundCompany = companyService.getByUsername(username);
        Request found = getByIdAndCompany(id, foundCompany);
        found.setStatus(RequestStatus.REJECTED);
        return edit(found, foundCompany.getUser());
    }

    @Override
    @Transactional
    public void removeRequestForCompany(@NonNull final Long id, @NonNull final String username) {
        Request found = getByIdAndCompany(id, username);
        removeRequest(found);
    }

    @Override
    @Transactional
    public void removeRequestForTalent(@NonNull final Long id, @NonNull final String username) {
        Request found = getByIdAndTalent(id, username);
        removeRequest(found);
    }

    @Override
    @Transactional(readOnly = true)
    public Request getByIdAndCompany(@NonNull Long id, @NonNull String username) {
        Company foundCompany = companyService.getByUsername(username);
        return repository.findByIdAndCompany(id, foundCompany).orElseThrow(() -> ApiEntityNotFoundException.builder()
                .entity(Request.class.getSimpleName()).id(id + "_" + username).build());
    }

    @Override
    @Transactional(readOnly = true)
    public Request getByIdAndTalent(@NonNull Long id, @NonNull String username) {
        Talent foundTalent = talentService.getByUsername(username);
        return repository.findByIdAndTalent(id, foundTalent).orElseThrow(() -> ApiEntityNotFoundException.builder()
                .entity(Request.class.getSimpleName()).id(id + "_" + username).build());
    }

    @Override
    @Transactional
    public void updateAsSeenByCompany(@NonNull Long id) {
        repository.updateSeenByCompany(id, true);
    }

    @Override
    @Transactional
    public void updateAsSeenByTalent(@NonNull Long id) {
        repository.updateSeenByTalent(id, true);
    }

    @Override
    @Transactional
    public Request editRequestNote(Long id, String username, String note) {
        Company foundCompany = companyService.getByUsername(username);
        Request found = getByIdAndCompany(id, foundCompany);

        found.setNote(note);
        edit(found, foundCompany.getUser());

        return repository.save(found);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Request> findPreviousByTalentAndCompany(@NonNull final Long talentId, @NonNull final Long companyId) {
        return repository.findTop1ByTalentIdAndCompanyIdOrderByCreatedOn(talentId, companyId);
    }

    @Override
    @Transactional
    public Request edit(@NonNull final Request request, @NonNull final User user) {
        request.setModifiedBy(user);
        request.setModifiedOn(Instant.now());
        return repository.save(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> findAcceptedOrPendingTalentIdsForCompany(String companyUserName) {
        return findByStatusForCompany(
                companyUserName,
                List.of(
                        RequestStatus.PENDING,
                        RequestStatus.ACCEPTED,
                        RequestStatus.COUNTER_OFFER_COMPANY,
                        RequestStatus.COUNTER_OFFER_TALENT
                )
        ).stream().map(request -> request.getTalent().getId()).collect(Collectors.toList());
    }

    private void validateStatus(RequestStatus status) {
        if (!(status.equals(RequestStatus.ACCEPTED) || status.equals(RequestStatus.REJECTED))) {
            throw new ApiBadRequestStatusException("Invalid request status forwarded");
        }
    }

    private void validateTerms(Set<Long> talentTermIds, Set<Long> feedbackTermIds) {
        if (!talentTermIds.containsAll(feedbackTermIds)) {
            throw new ApiBadRequestException("Invalid terms");
        }
    }

    private void checkIfMandatoryTermIsAccepted(TalentTerm talentTerm, TermCreateDto termCreateDto) {
        if (!talentTerm.getNegotiable() && !TalentTermRequestStatus.ACCEPTED.equals(termCreateDto.getStatus())) {
            throw new ApiBadRequestException(String.format("Term %s must be accepted!", termCreateDto.getTermId()));
        }
    }

    private Request getByIdAndTalent(Long id, Talent talent) {
        return repository.findByIdAndTalent(id, talent).orElseThrow(() -> ApiEntityNotFoundException.builder()
                .entity(Request.class.getSimpleName()).id(id.toString()).build());
    }

    private Request getByIdAndCompany(Long id, Company company) {
        return repository.findByIdAndCompany(id, company).orElseThrow(() -> ApiEntityNotFoundException.builder()
                .entity(Request.class.getSimpleName()).id(id.toString()).build());
    }

    private void removeRequest(Request request) {
        availableChatService.removeByTalentAndCompany(request.getTalent(), request.getCompany());
        repository.delete(request);
        LOGGER.info(
                "Removed request between talent {} and company {}",
                request.getTalent().getUser().getUsername(),
                request.getCompany().getUser().getUsername()
        );
    }

    private void validateAcceptedOrPendingNotExists(Talent talent, Company company) {
        if (repository.existsByTalentAndCompanyAndStatusIn(
                talent,
                company,
                List.of(
                        RequestStatus.PENDING,
                        RequestStatus.ACCEPTED,
                        RequestStatus.COUNTER_OFFER_COMPANY,
                        RequestStatus.COUNTER_OFFER_TALENT
                )
        )) {
            throw ApiBadRequestException.message("Active request already exists");
        }
    }

}

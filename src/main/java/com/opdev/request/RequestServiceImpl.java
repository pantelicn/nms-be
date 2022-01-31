package com.opdev.request;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.opdev.model.user.User;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opdev.company.service.CompanyService;
import com.opdev.company.dto.RequestCreateDto;
import com.opdev.company.dto.TermCreateDto;
import com.opdev.exception.ApiBadRequestException;
import com.opdev.exception.ApiBadRequestStatusException;
import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.model.company.Company;
import com.opdev.model.request.Request;
import com.opdev.model.request.RequestStatus;
import com.opdev.model.request.TalentTermRequest;
import com.opdev.model.request.TalentTermRequestStatus;
import com.opdev.model.talent.Talent;
import com.opdev.model.term.TalentTerm;
import com.opdev.repository.RequestRepository;
import com.opdev.talent.TalentService;
import com.opdev.util.encoding.TalentIdEncoder;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository repository;
    private final CompanyService companyService;
    private final TalentService talentService;
    private final TalentIdEncoder talentIdEncoder;

    @Override
    @Transactional
    public Request create(@NonNull RequestCreateDto newRequestDto,
                          @NonNull String username) {
        Company foundCompany = companyService.getByUsername(username);
        Long talentId = talentIdEncoder.decode(newRequestDto.getTalentId());
        Talent foundTalent = talentService.getById(talentId);
        List<TalentTerm> talentTerms = foundTalent.getTerms();

        validateTerms(talentTerms.stream().map(TalentTerm::getId).collect(Collectors.toList()),
                      newRequestDto.getTerms().stream().map(TermCreateDto::getTermId).collect(Collectors.toList()));

        Request newRequest = Request.builder()
                .talent(foundTalent)
                .status(RequestStatus.PENDING)
                .company(foundCompany)
                .note(newRequestDto.getNote())
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
                    .talentTerm(talentTerm)
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
    public Page<Request> findByStatusForCompany(@NonNull final String username,
                                                @NonNull final RequestStatus status,
                                                @NonNull final Pageable pageable) {
        Company foundCompany = companyService.getByUsername(username);
        return repository.findByCompanyAndStatus(foundCompany, status, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Request> findByStatusForTalent(@NonNull final String username,
                                               @NonNull final RequestStatus status,
                                               @NonNull final Pageable pageable) {
        Talent foundTalent = talentService.getByUsername(username);
        return repository.findByTalentAndStatus(foundTalent, status, pageable);
    }

    @Override
    @Transactional
    public Request editStatusForTalent(@NonNull final String username, @NonNull final Long id, @NonNull final RequestStatus newStatus) {
        validateStatus(newStatus);
        Talent foundTalent = talentService.getByUsername(username);
        Request found = getByIdAndTalent(id, foundTalent);
        found.setStatus(newStatus);
        return edit(found, foundTalent.getUser());
    }

    @Override
    @Transactional
    public void removeRequestForCompany(@NonNull final Long id, @NonNull final String username) {
        Company foundCompany = companyService.getByUsername(username);
        Request found = getByIdAndCompany(id, foundCompany);
        repository.delete(found);
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
    public Request edit(@NonNull final Request request, @NonNull final User user) {
        request.setModifiedBy(user);
        request.setModifiedOn(Instant.now());
        return repository.save(request);
    }

    private void validateStatus(RequestStatus status) {
        if (!(status.equals(RequestStatus.ACCEPTED) || status.equals(RequestStatus.REJECTED))) {
            throw new ApiBadRequestStatusException("Invalid request status forwarded");
        }
    }

    private void validateTerms(List<Long> talentTermIds, List<Long> feedbackTermIds) {
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

}

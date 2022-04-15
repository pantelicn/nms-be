package com.opdev.talent.term;

import com.opdev.company.dto.TalentTermRequestEditDto;
import com.opdev.exception.ApiBadRequestException;
import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.exception.ApiValidationException;
import com.opdev.model.request.Request;
import com.opdev.model.request.RequestStatus;
import com.opdev.model.request.TalentTermRequest;
import com.opdev.model.request.TalentTermRequestStatus;
import com.opdev.model.user.User;
import com.opdev.model.user.UserType;
import com.opdev.request.RequestService;
import com.opdev.request.dto.RequestResponseDto;
import com.opdev.term.validation.TalentTermValidator;
import com.opdev.user.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@Slf4j
@Service
@RequiredArgsConstructor
public class TalentTermRequestServiceImpl implements TalentTermRequestService {

    private final RequestService requestService;
    private final UserService userService;

    @Transactional
    @Override
    public Request editByCompany(@NonNull RequestResponseDto requestResponse, @NonNull String username) {
        Request foundRequest = requestService.getByIdAndCompany(requestResponse.getRequestId(), username);
        return edit(requestResponse, username, foundRequest);
    }

    @Transactional
    @Override
    public Request editByTalent(@NonNull RequestResponseDto requestResponse, @NonNull String username) {
        Request foundRequest = requestService.getByIdAndTalent(requestResponse.getRequestId(), username);
        return edit(requestResponse, username, foundRequest);
    }

    private Request edit(RequestResponseDto requestResponse,
                         String username,
                         Request request) {
        ApiBadRequestException.message("Invalid request status").throwIf(request::isFinal);
        validateRequestIsUpToDate(request.getModifiedOn(), requestResponse.getModifiedOn());
        User user = userService.getByUsername(username);

        TalentTermRequestEditDto newTermRequest = requestResponse.getNewTermRequest();

        TalentTermRequest foundRequest = request.getTalentTermRequests()
                .stream()
                .filter(e -> e.getId().equals(newTermRequest.getId()))
                .findFirst()
                .orElseThrow(() -> ApiEntityNotFoundException
                        .builder()
                        .entity(TalentTermRequest.class.getSimpleName())
                        .id(newTermRequest.getId().toString())
                        .build());

        validateTalentTermRequestStatus(foundRequest);
        updateTalentTermRequest(foundRequest, newTermRequest, user);

        request.setStatus(user.getType() == UserType.COMPANY
                ? RequestStatus.COUNTER_OFFER_COMPANY : RequestStatus.COUNTER_OFFER_TALENT);

        LOGGER.info("{} {} responded to {}", user.getType(), user.getUsername(), request);

        return requestService.edit(request, user);
    }

    private void updateTalentTermRequest(TalentTermRequest currentTermRequest,
                                         TalentTermRequestEditDto newTermRequest,
                                         User user) {
        TalentTermRequestStatus newStatus = getStatusFromNewTermRequestByUserType(newTermRequest, user.getType());
        currentTermRequest.setStatus(newStatus);
        if (currentTermRequest.isStatusCounterOffer()) {
            validateNewTermRequestCounterOffer(currentTermRequest, newTermRequest);
            currentTermRequest.setCounterOffer(newTermRequest.getCounterOffer());
        }
        currentTermRequest.setModifiedBy(user);
        currentTermRequest.setModifiedOn(Instant.now());
    }

    private void validateNewTermRequestCounterOffer(TalentTermRequest currentTermRequest,
                                                    TalentTermRequestEditDto newTermRequest) {
        ApiBadRequestException.message("New counter offer cannot be the same as current counter offer")
                .throwIf(() -> currentTermRequest.getCounterOffer().equals(newTermRequest.getCounterOffer()));
        try {
            TalentTermValidator.validate(currentTermRequest.getTalentTerm().getTerm().getType(),
                    newTermRequest.getCounterOffer(), currentTermRequest.getTalentTerm().getUnitOfMeasure());
        } catch (ApiValidationException e) {
            throw ApiBadRequestException.message("Invalid term request counter offer");
        }
    }

    private TalentTermRequestStatus getStatusFromNewTermRequestByUserType(TalentTermRequestEditDto newTermRequest,
                                                                          UserType type) {
        ApiBadRequestException.message("Invalid term request status")
                .throwIf(() -> isEmpty(newTermRequest.getCounterOffer())
                        && newTermRequest.getStatus() != TalentTermRequestStatus.ACCEPTED);

        TalentTermRequestStatus counterOfferStatus = type == UserType.COMPANY ?
                TalentTermRequestStatus.COUNTER_OFFER_COMPANY : TalentTermRequestStatus.COUNTER_OFFER_TALENT;

        return isNotEmpty(newTermRequest.getCounterOffer()) ? counterOfferStatus : TalentTermRequestStatus.ACCEPTED;
    }

    private void validateRequestIsUpToDate(Instant expected, Instant actual) {
        expected = expected.truncatedTo(ChronoUnit.MILLIS);
        actual = actual.truncatedTo(ChronoUnit.MILLIS);

        if (!actual.equals(expected)) {
            throw new ApiBadRequestException("Request state has changed");
        }

    }

    private void validateTalentTermRequestStatus(TalentTermRequest talentTermRequest) {
        ApiBadRequestException.message("Invalid term request status")
                .throwIf(() -> talentTermRequest.getStatus() == TalentTermRequestStatus.ACCEPTED);
    }

}

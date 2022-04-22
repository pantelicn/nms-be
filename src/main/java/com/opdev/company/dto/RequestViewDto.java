package com.opdev.company.dto;

import com.opdev.model.request.Request;
import com.opdev.model.request.RequestStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class RequestViewDto {

    private Long id;

    private RequestStatus status;

    private String note;

    private Instant modifiedOn;

    private boolean seenByCompany;

    private boolean seenByTalent;

    public RequestViewDto(Request request) {
        id = request.getId();
        status = request.getStatus();
        note = request.getNote();
        modifiedOn = request.getModifiedOn();
        seenByCompany = request.isSeenByCompany();
        seenByTalent = request.isSeenByTalent();
    }

}

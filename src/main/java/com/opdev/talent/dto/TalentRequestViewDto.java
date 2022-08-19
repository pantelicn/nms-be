package com.opdev.talent.dto;

import java.time.Instant;

import com.opdev.model.request.Request;
import com.opdev.model.request.RequestStatus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class TalentRequestViewDto {

    private Long id;

    private RequestStatus status;

    private String company;

    private Instant modifiedOn;

    private boolean seenByTalent;

    public TalentRequestViewDto(Request request) {
        id = request.getId();
        status = request.getStatus();
        company = request.getCompany().getName();
        modifiedOn = request.getModifiedOn();
        seenByTalent = request.isSeenByTalent();
    }

}

package com.opdev.company.dto;

import com.opdev.model.request.Request;
import com.opdev.model.request.RequestStatus;

import com.opdev.request.dto.TalentTermRequestViewDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class RequestViewDto {

    private Long id;

    private RequestStatus status;

    private String note;

    private List<TalentTermRequestViewDto> talentTermRequests;

    private Instant modifiedOn;

    public RequestViewDto(Request request) {
        id = request.getId();
        status = request.getStatus();
        note = request.getNote();
        talentTermRequests = request.getTalentTermRequests().stream()
                .map(TalentTermRequestViewDto::new)
                .collect(Collectors.toList());
        modifiedOn = request.getModifiedOn();
    }


}

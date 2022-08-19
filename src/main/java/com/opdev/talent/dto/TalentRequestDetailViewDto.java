package com.opdev.talent.dto;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import com.opdev.model.request.Request;
import com.opdev.model.request.RequestStatus;
import com.opdev.request.dto.TalentTermRequestViewDto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TalentRequestDetailViewDto {

    private Long id;

    private RequestStatus status;

    private String company;

    private List<TalentTermRequestViewDto> talentTermRequests;

    private Instant modifiedOn;

    public TalentRequestDetailViewDto(Request request) {
        id = request.getId();
        status = request.getStatus();
        company = request.getCompany().getName();
        talentTermRequests = request.getTalentTermRequests().stream()
                .map(TalentTermRequestViewDto::new)
                .collect(Collectors.toList());
        modifiedOn = request.getModifiedOn();
    }

}
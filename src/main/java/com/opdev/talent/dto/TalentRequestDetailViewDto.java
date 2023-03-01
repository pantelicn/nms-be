package com.opdev.talent.dto;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import com.opdev.benefit.dto.BenefitViewDto;
import com.opdev.dto.CompanyViewDto;
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

    private CompanyViewDto company;

    private List<TalentTermRequestViewDto> talentTermRequests;

    private Instant modifiedOn;

    private List<BenefitViewDto> benefits;

    private String jobDescription;

    public TalentRequestDetailViewDto(Request request) {
        id = request.getId();
        status = request.getStatus();
        company = new CompanyViewDto(request.getCompany());
        talentTermRequests = request.getTalentTermRequests().stream()
                .map(TalentTermRequestViewDto::new)
                .collect(Collectors.toList());
        modifiedOn = request.getModifiedOn();
        benefits = request.getCompany().getBenefits().stream().map(BenefitViewDto::new).collect(Collectors.toList());
        jobDescription = request.getJobDescription();
    }

}

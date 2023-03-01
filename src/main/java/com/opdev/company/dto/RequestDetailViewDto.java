package com.opdev.company.dto;

import com.opdev.dto.CompanyViewDto;
import com.opdev.model.request.Request;
import com.opdev.model.request.RequestStatus;
import com.opdev.position.dto.PositionViewDto;
import com.opdev.request.dto.TalentTermRequestViewDto;
import com.opdev.skill.dto.SkillViewDto;
import com.opdev.talent.dto.ProjectViewDto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
public class RequestDetailViewDto {

    private Long id;

    private RequestStatus status;

    private String note;

    private List<TalentTermRequestViewDto> talentTermRequests;

    private Instant modifiedOn;

    private CompanyViewDto company;

    private List<SkillViewDto> skills;

    private List<PositionViewDto> positions;

    private List<RestrictedProjectViewDto> projects = new ArrayList<>();

    public RequestDetailViewDto(Request request) {
        id = request.getId();
        status = request.getStatus();
        note = request.getNote();
        talentTermRequests = request.getTalentTermRequests().stream()
                .map(TalentTermRequestViewDto::new)
                .collect(Collectors.toList());
        modifiedOn = request.getModifiedOn();
        company = new CompanyViewDto(request.getCompany());
        skills = request.getTalent().getTalentSkills().stream().map(talentSkill -> new SkillViewDto(talentSkill.getSkill())).collect(Collectors.toList());
        positions = request.getTalent().getTalentPositions().stream().map(talentPosition -> new PositionViewDto(talentPosition.getPosition())).collect(
                Collectors.toList());
        request.getTalent().getProjects().forEach(project -> projects.add(new RestrictedProjectViewDto(project)));
    }

}

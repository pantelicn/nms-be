package com.opdev.company.dto;

import com.opdev.model.request.Request;
import com.opdev.model.request.RequestStatus;
import com.opdev.position.dto.PositionViewDto;
import com.opdev.request.dto.TalentTermRequestViewDto;
import com.opdev.skill.dto.SkillViewDto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
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

    private List<SkillViewDto> skills;

    private List<PositionViewDto> positions;

    public RequestDetailViewDto(Request request) {
        id = request.getId();
        status = request.getStatus();
        note = request.getNote();
        talentTermRequests = request.getTalentTermRequests().stream()
                .map(TalentTermRequestViewDto::new)
                .collect(Collectors.toList());
        modifiedOn = request.getModifiedOn();
        skills = request.getTalent().getTalentSkills().stream().map(talentSkill -> new SkillViewDto(talentSkill.getSkill())).collect(Collectors.toList());
        positions = request.getTalent().getTalentPositions().stream().map(talentPosition -> new PositionViewDto(talentPosition.getPosition())).collect(
                Collectors.toList());
    }

}

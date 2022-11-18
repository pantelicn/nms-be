package com.opdev.talent.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.opdev.model.request.Request;
import com.opdev.model.talent.Talent;
import com.opdev.position.dto.PositionViewDto;
import com.opdev.skill.dto.SkillViewDto;
import com.opdev.util.encoding.TalentIdEncoder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class TalentViewSearchDto {

    private String talentId;

    private List<TalentTermViewDto> terms = new ArrayList<>();

    private List<SkillViewDto> skills = new ArrayList<>();

    private List<PositionViewDto> positions = new ArrayList<>();

    private Instant lastTimeSentOn;

    public TalentViewSearchDto(final Talent talent, final TalentIdEncoder encoder, final Long companyId, final Request request) {
        talentId = encoder.encode(talent.getId(), companyId);
        talent.getTalentTerms().forEach(term -> terms.add(new TalentTermViewDto(term)));
        talent.getTalentSkills().forEach(talentSkill -> skills.add(new SkillViewDto(talentSkill.getSkill())));
        talent.getTalentPositions().forEach(talentPosition -> positions.add(new PositionViewDto(talentPosition.getPosition())));
        if (request != null) {
            lastTimeSentOn = request.getCreatedOn();
        }
    }

}

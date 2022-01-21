package com.opdev.talent.dto;

import com.opdev.model.talent.TalentSkill;
import com.opdev.skill.dto.SkillViewDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TalentSkillsViewDto {

    private TalentViewDto talent;

    private List<SkillViewDto> skills = new ArrayList<>();

    public TalentSkillsViewDto(final List<TalentSkill> talentSkills) {
        Objects.requireNonNull(talentSkills);
        talentSkills.stream()
                .findFirst()
                .ifPresent(talentSkill -> talent = new TalentViewDto(talentSkill.getTalent()));
        talentSkills.forEach(talentSkill -> skills.add(new SkillViewDto(talentSkill.getSkill())));
    }

}
package com.opdev.position.dto;

import com.opdev.model.talent.PositionSkill;
import com.opdev.skill.dto.SkillViewDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PositionSkillsViewDto {

    private PositionViewDto position;

    private List<SkillViewDto> skills = new ArrayList<>();

    public PositionSkillsViewDto(final List<PositionSkill> positionSkills) {
        Objects.requireNonNull(positionSkills);
        positionSkills.stream() //
                .findFirst() //
                .ifPresent(positionSkill -> position = new PositionViewDto(positionSkill.getPosition()));
        positionSkills.forEach(positionSkill -> skills.add(new SkillViewDto(positionSkill.getSkill())));
    }


}

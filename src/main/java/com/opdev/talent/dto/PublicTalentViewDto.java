package com.opdev.talent.dto;

import com.opdev.model.talent.Talent;
import com.opdev.position.dto.PositionViewDto;
import com.opdev.skill.dto.SkillViewDto;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PublicTalentViewDto {

    private final Integer experienceYears;

    private final List<AvailableLocationViewDto> availableLocations;

    private final List<SkillViewDto> skills;

    private final List<PositionViewDto> positions;

    public  PublicTalentViewDto(Talent talent) {
        this.experienceYears = talent.getExperienceYears();
        this.availableLocations = talent.getAvailableLocations().stream()
                .map(AvailableLocationViewDto::new)
                .collect(Collectors.toList());
        this.skills = talent.getTalentSkills().stream()
                .map(talentSkill -> new SkillViewDto(talentSkill.getSkill()))
                .collect(Collectors.toList());
        this.positions = talent.getTalentPositions().stream()
                .map(talentPosition -> new PositionViewDto(talentPosition.getPosition()))
                .collect(Collectors.toList());
    }

}

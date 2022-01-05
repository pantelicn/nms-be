package com.opdev.skill.dto;

import com.opdev.model.talent.Skill;
import com.opdev.model.talent.SkillStatus;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class SkillViewDto {

    private Long id;
    private String name;
    private SkillStatus status;
    private String code;

    public SkillViewDto(Skill skill) {
        id = skill.getId();
        name = skill.getName();
        status = skill.getStatus();
        code = skill.getCode();
    }

}

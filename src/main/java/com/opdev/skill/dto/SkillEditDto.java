package com.opdev.skill.dto;

import com.opdev.model.talent.Skill;
import com.opdev.model.talent.SkillStatus;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SkillEditDto {

    @NonNull
    @NotNull
    private Long id;

    @NonNull
    @NotEmpty
    private String name;

    @NonNull
    @NotEmpty
    private String code;

    public Skill asSkill() {
        return Skill.builder()
                .id(id)
                .name(name)
                .status(SkillStatus.PENDING)
                .code(code).build();
    }

}

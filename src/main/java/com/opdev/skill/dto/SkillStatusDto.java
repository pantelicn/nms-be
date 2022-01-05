package com.opdev.skill.dto;

import com.opdev.model.talent.SkillStatus;
import lombok.*;

import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SkillStatusDto {

    @NonNull
    @NotNull
    private SkillStatus status;

}

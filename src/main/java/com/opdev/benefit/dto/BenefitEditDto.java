package com.opdev.benefit.dto;

import com.opdev.model.company.Benefit;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class BenefitEditDto {

    @NonNull
    @NotNull
    private Long id;

    @NonNull
    @NotEmpty
    private String name;

    @NonNull
    @NotEmpty
    private String description;

    @NonNull
    @NotNull
    private Boolean isDefault;

    public Benefit asBenefit() {
        return Benefit.builder()
                .id(id)
                .name(name)
                .description(description)
                .isDefault(isDefault).build();
    }

}

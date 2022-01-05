package com.opdev.benefit.dto;

import com.opdev.model.company.Benefit;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CompanyBenefitAddDto {

    @NonNull
    @NotBlank
    private String name;

    @NonNull
    @NotBlank
    private String description;

    @NonNull
    @NotNull
    private Boolean isDefault;

    public Benefit asBenefit() {
        return Benefit.builder()
                .name(name)
                .description(description)
                .isDefault(isDefault).build();
    }

}

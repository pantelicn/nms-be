package com.opdev.offers.plan.dto;

import com.opdev.common.dto.MoneyDto;
import com.opdev.model.subscription.Plan;
import com.opdev.model.subscription.PlanType;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@Getter
public class PlanDto {

    @NotBlank
    @NonNull
    private String name;

    @NonNull
    @NotNull
    private PlanType type;

    @NotBlank
    @NonNull
    private String description;

    @NonNull
    @NotNull
    private Integer durationInMonths;

    @NonNull
    @NotNull
    private MoneyDto price;

    public Plan asPlan() {
        return Plan
                .builder()
                .name(name)
                .type(type)
                .durationInMonths(durationInMonths)
                .description(description)
                .price(price.asMoney())
                .build();
    }

}

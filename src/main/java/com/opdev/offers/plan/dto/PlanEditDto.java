package com.opdev.offers.plan.dto;

import com.opdev.common.dto.MoneyDto;
import com.opdev.model.subscription.Plan;
import com.opdev.model.subscription.PlanType;
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
public class PlanEditDto {

    @NonNull
    @NotNull
    private Long id;

    @NotBlank
    @NonNull
    private String name;

    @NonNull
    @NotNull
    private PlanType type;

    @NonNull
    @NotNull
    private Integer durationInMonths;

    @NotBlank
    @NonNull
    private String description;

    @NonNull
    @NotNull
    private MoneyDto price;

    public Plan asPlan() {
        return Plan
                .builder()
                .id(id)
                .name(name)
                .type(type)
                .durationInMonths(durationInMonths)
                .description(description)
                .price(price.asMoney())
                .build();
    }
}

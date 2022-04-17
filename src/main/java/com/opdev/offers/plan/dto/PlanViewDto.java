package com.opdev.offers.plan.dto;

import com.opdev.common.dto.MoneyDto;
import com.opdev.model.subscription.Plan;
import com.opdev.model.subscription.PlanType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class PlanViewDto {

    private Long id;

    private String name;

    private PlanType type;

    private String description;

    private Integer durationInMonths;

    private MoneyDto price;

    public PlanViewDto(Plan createdPlan) {
        id = createdPlan.getId();
        name = createdPlan.getName();
        type = createdPlan.getType();
        description = createdPlan.getDescription();
        durationInMonths = createdPlan.getDurationInMonths();
        price = new MoneyDto(createdPlan.getPrice());
    }
}

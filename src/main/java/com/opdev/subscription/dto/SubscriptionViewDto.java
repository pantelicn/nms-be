package com.opdev.subscription.dto;

import com.opdev.model.subscription.Subscription;
import com.opdev.offers.plan.dto.PlanViewDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class SubscriptionViewDto {

    private Long id;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean autoRenewal;

    private PlanViewDto plan;

    public SubscriptionViewDto(Subscription subscription) {
        id = subscription.getId();
        startDate = subscription.getStartDate();
        endDate = subscription.getEndDate();
        autoRenewal = subscription.getAutoRenewal();
        plan = new PlanViewDto(subscription.getPlan());
    }
}

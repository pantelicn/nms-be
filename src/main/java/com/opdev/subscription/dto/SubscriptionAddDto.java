package com.opdev.subscription.dto;

import com.opdev.model.company.Company;
import com.opdev.model.subscription.Plan;
import com.opdev.model.subscription.Subscription;
import com.opdev.model.subscription.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.Period;

@AllArgsConstructor
@Getter
public class SubscriptionAddDto {

    private Long planId;

    private Boolean autoRenewal;

    public Subscription asSubscription(Plan plan, Company company) {
        return Subscription
                .builder()
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(plan.getDurationInMonths()))
                .period(Period.ofMonths(plan.getDurationInMonths()))
                .subscriptionStatus(SubscriptionStatus.ACTIVE)
                .autoRenewal(autoRenewal)
                .company(company)
                .plan(plan)
                .build();
    }
}

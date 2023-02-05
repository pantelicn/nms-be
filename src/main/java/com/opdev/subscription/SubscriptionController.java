package com.opdev.subscription;

import com.opdev.company.service.CompanyService;
import com.opdev.config.security.Roles;
import com.opdev.model.company.Company;
import com.opdev.model.subscription.Plan;
import com.opdev.model.subscription.Subscription;
import com.opdev.offers.plan.PlanService;
import com.opdev.subscription.dto.SubscriptionAddDto;
import com.opdev.subscription.dto.SubscriptionViewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final PlanService planService;
    private final CompanyService companyService;


    @PostMapping("/{username}")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))")
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionViewDto subscribe(@Valid @RequestBody SubscriptionAddDto subscriptionAddDto,
                                         @PathVariable final String username) {
        Plan foundPlan = planService.getById(subscriptionAddDto.getPlanId());
        Company foundCompany = companyService.getByUsername(username);

        Subscription newSubscription =
                subscriptionService.subscribe(subscriptionAddDto.asSubscription(foundPlan, foundCompany));

        return new SubscriptionViewDto(newSubscription);
    }

    @GetMapping("/{username}")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))")
    @ResponseStatus(HttpStatus.OK)
    public SubscriptionViewDto get(@PathVariable final String username) {
        Subscription found = subscriptionService.get(username);
        return new SubscriptionViewDto(found);
    }

}

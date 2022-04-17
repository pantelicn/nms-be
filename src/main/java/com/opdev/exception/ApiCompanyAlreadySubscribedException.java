package com.opdev.exception;

import com.opdev.model.company.Company;
import com.opdev.model.subscription.Plan;

public class ApiCompanyAlreadySubscribedException extends RuntimeException {

    public ApiCompanyAlreadySubscribedException(Company company, Plan plan) {
        super(String.format("Company %s is already subscribed to a %s plan", company.getName(), plan.getName()));
    }

}

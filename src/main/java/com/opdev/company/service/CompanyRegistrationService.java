package com.opdev.company.service;

import com.opdev.model.company.Company;

public interface CompanyRegistrationService {

    Company register(final Company company, final String password);

}

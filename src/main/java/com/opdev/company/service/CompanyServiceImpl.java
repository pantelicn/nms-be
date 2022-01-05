package com.opdev.company.service;

import java.util.Objects;
import java.util.Optional;

import com.opdev.authentication.UserService;
import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.model.company.Company;
import com.opdev.model.user.User;
import com.opdev.repository.CompanyRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    @Override
    public Company view(final String username) {
        Objects.requireNonNull(username);

        final Company company = getByUsername(username);
        if (!userService.isAdminLoggedIn()) {   // FIXME Why check for enabled user here instead of the filter?
            userService.ensureIsEnabled(company.getUser());
        }

        return company;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Company> view(final Pageable pageable) {
        Objects.requireNonNull(pageable);
        return companyRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Company getByUsername(final String username) {
        return findByUsername(username).orElseThrow(() -> ApiEntityNotFoundException.builder()
                .message("Entity.not.found").entity("Company").id(username).build());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Company> findByUsername(final String username) {
        Objects.requireNonNull(username);
        return companyRepository.findByUserUsername(username);
    }

    @Transactional
    @Override
    public Company update(final Company updatedCompany) {
        Objects.requireNonNull(updatedCompany);
        if (!userService.isAdminLoggedIn()) {
            userService.ensureIsEnabled(updatedCompany.getUser());
        }
        return companyRepository.save(updatedCompany);
    }

    @Transactional
    @Override
    public void disable(final String username) {
        Objects.requireNonNull(username);

        final Company company = getByUsername(username);

        final User updatedUser = company.getUser().toBuilder().enabled(Boolean.FALSE).build();
        updatedUser.setModifiedBy(userService.resolveModifiedBy(updatedUser));

        final Company disabledCompany = company.toBuilder().user(updatedUser).build();
        userService.save(updatedUser);
        companyRepository.save(disabledCompany);
    }

    @Transactional
    @Override
    public void delete(final String username) {
        Objects.requireNonNull(username);

        final Company company = getByUsername(username);

        if (!userService.isAdminLoggedIn()) {
            userService.ensureIsEnabled(company.getUser());
        }

        companyRepository.delete(company);
        userService.delete(username);
    }
}

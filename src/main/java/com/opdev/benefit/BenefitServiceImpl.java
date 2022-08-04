package com.opdev.benefit;

import com.opdev.company.service.CompanyService;
import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.model.company.Benefit;
import com.opdev.model.company.Company;
import com.opdev.model.user.User;
import com.opdev.repository.BenefitRepository;
import com.opdev.user.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class BenefitServiceImpl implements BenefitService {

    private final BenefitRepository repository;
    private final UserService userService;
    private final CompanyService companyService;

    @Override
    @Transactional
    public Benefit addBenefitToCompany(final String companyUsername, final Benefit newBenefit) {
        Objects.requireNonNull(newBenefit);
        final Company found = companyService.getByUsername(companyUsername);
        final User loggedUser = userService.getLoggedInUser();
        newBenefit.setCompany(found);
        newBenefit.setCreatedBy(loggedUser);
        newBenefit.setModifiedBy(loggedUser);
        final Benefit created = repository.save(newBenefit);
        LOGGER.info("New benefit {} has been added.", created);
        return created;
    }

    @Override
    @Transactional(readOnly = true)
    public Benefit get(final Long id) {
        Objects.requireNonNull(id);
        return repository.findById(id).orElseThrow(() -> ApiEntityNotFoundException.builder()
                .message("Entity.not.found")
                .entity("Benefit")
                .id(id.toString()).build());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Benefit> get(final Specification<Benefit> specification, final Pageable pageable) {
        Objects.requireNonNull(pageable);
        return repository.findAll(specification, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Benefit> getByCompany(final String username) {
        Objects.requireNonNull(username);
        return repository.findByCompanyUserUsername(username);
    }

    @Override
    @Transactional
    public List<Benefit> edit(final String username, final List<Benefit> modified) {
        final User loggedUser = userService.getLoggedInUser();
        final Company found = companyService.getByUsername(username);
        modified.forEach(benefit -> {
            benefit.setCompany(found);
            benefit.setCreatedBy(loggedUser);
            benefit.setModifiedBy(loggedUser);
        });
        repository.deleteByCompanyUser(loggedUser);
        return repository.saveAll(modified);
    }

    @Override
    @Transactional
    public void remove(final Long id) {
        Objects.requireNonNull(id);
        get(id);
        repository.deleteById(id);
        LOGGER.info("Benefit with id {} has been removed.", id);
    }

}

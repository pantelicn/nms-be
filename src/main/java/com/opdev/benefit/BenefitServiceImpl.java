package com.opdev.benefit;

import com.opdev.authentication.UserService;
import com.opdev.company.service.CompanyService;
import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.model.company.Benefit;
import com.opdev.model.company.Company;
import com.opdev.model.user.User;
import com.opdev.repository.BenefitRepository;
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
    public Benefit edit(final Benefit modified) {
        Objects.requireNonNull(modified);
        final Benefit oldBenefit = get(modified.getId());
        modified.setCompany(oldBenefit.getCompany());
        final User loggedUser = userService.getLoggedInUser();
        modified.setModifiedBy(loggedUser);
        Benefit newBenefit = repository.save(modified);
        LOGGER.info("Benefit with id {} is modified {}", modified.getId(), modified);
        return newBenefit;
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

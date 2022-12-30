package com.opdev.company.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import java.util.Optional;

import com.opdev.exception.ApiEmailExistsException;
import com.opdev.exception.UserAlreadyExistsException;
import com.opdev.model.company.Company;
import com.opdev.model.subscription.Plan;
import com.opdev.model.subscription.PlanType;
import com.opdev.model.subscription.Subscription;
import com.opdev.model.subscription.SubscriptionStatus;
import com.opdev.model.user.User;
import com.opdev.model.user.UserRole;
import com.opdev.offers.plan.PlanService;
import com.opdev.repository.CompanyRepository;
import com.opdev.repository.UserRepository;
import com.opdev.repository.VerificationTokenRepository;
import com.opdev.subscription.SubscriptionService;
import com.opdev.user.role.RoleService;
import com.opdev.user.userole.UserRoleService;

import com.opdev.user.verification.VerificationTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
class CompanyRegistrationServiceImpl implements CompanyRegistrationService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final RoleService roleService;
    private final UserRoleService userRoleService;
    private final SubscriptionService subscriptionService;
    private final PlanService planService;
    private final VerificationTokenService verificationTokenService;

    @Transactional
    @Override
    public Company register(Company company) {
        validateCompany(company);
        User companyUser = userRepository.save(company.getUser());
        verificationTokenService.create(companyUser.getVerificationToken());
        UserRole companyUserRole = UserRole.builder()
                .role(roleService.getCompanyRole())
                .user(companyUser)
                .build();
        userRoleService.create(companyUserRole);
        Company created = companyRepository.save(company);
        createTrialSubscription(created);
        return created;
    }

    private void validateCompany(final Company company) throws ApiEmailExistsException {
        Objects.requireNonNull(company);

        final Optional<User> existingUser = userRepository.findByUsername(company.getUser().getUsername());
        if (existingUser.isPresent()) {
            LOGGER.info("The username already exists: {}", company.getUser().getUsername());
            throw new UserAlreadyExistsException();
        }
    }

    private void createTrialSubscription(Company company) {
        Plan trialPlan = planService.findByType(PlanType.TRIAL);
        LocalDate firstJuly = LocalDate.of(2023, 7, 1);
        Period period = Period.between(LocalDate.now(), firstJuly);
        Subscription subscription = Subscription.builder()
                .autoRenewal(false)
                .company(company)
                .subscriptionStatus(SubscriptionStatus.ACTIVE)
                .startDate(LocalDate.now())
                .endDate(firstJuly)
                .period(period)
                .plan(trialPlan)
                .build();

        subscriptionService.subscribe(subscription);
    }

}

package com.opdev.offers.plan;


import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.model.subscription.Plan;
import com.opdev.model.subscription.PlanType;
import com.opdev.model.user.User;
import com.opdev.repository.PlanRepository;
import com.opdev.user.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;
    private final UserService userService;

    @Override
    @Transactional
    public Plan save(@NonNull Plan plan) {
        User loggedUser = userService.getLoggedInUser();

        plan.setCreatedBy(loggedUser);
        plan.setCreatedOn(Instant.now());

        return planRepository.save(plan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Plan> findAll() {
        return planRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Plan getById(Long planId) {
        return findById(planId)
                .orElseThrow(() ->
                        ApiEntityNotFoundException
                                .builder()
                                .entity(Plan.class.getSimpleName())
                                .id(planId.toString())
                                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Plan> findById(Long planId) {
        return planRepository.findById(planId);
    }

    @Override
    @Transactional
    public Plan update(Plan modified) {
        final Plan foundPlan = getById(modified.getId());
        User loggedUser = userService.getLoggedInUser();
        foundPlan.update(modified, loggedUser);
        return planRepository.save(foundPlan);
    }

    @Override
    @Transactional
    public void delete(Long planId) {
        final Plan foundPlan = getById(planId);
        planRepository.delete(foundPlan);
    }

    @Override
    @Transactional(readOnly = true)
    public Plan findByType(final PlanType type) {
        return planRepository.findByType(type);
    }
}

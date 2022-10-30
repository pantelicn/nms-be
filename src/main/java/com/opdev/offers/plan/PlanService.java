package com.opdev.offers.plan;

import com.opdev.model.subscription.Plan;
import com.opdev.model.subscription.PlanType;

import java.util.List;
import java.util.Optional;

public interface PlanService {

    Plan save(Plan plan);

    List<Plan> findAll();

    Plan getById(Long planId);

    Optional<Plan> findById(Long planId);

    Plan update(Plan modified);

    void delete(Long planId);

    Plan findByType(PlanType type);

}

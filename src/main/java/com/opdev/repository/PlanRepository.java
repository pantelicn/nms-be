package com.opdev.repository;

import com.opdev.model.subscription.Plan;
import com.opdev.model.subscription.PlanType;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    Plan findByType(PlanType type);

}

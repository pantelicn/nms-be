package com.opdev.repository;

import com.opdev.model.subscription.PlanProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlanProductRepository extends JpaRepository<PlanProduct, Long> {

    List<PlanProduct> findByPlanId(Long planId);

    Optional<PlanProduct> findByPlanIdAndProductId(Long planId, Long productId);
    
}

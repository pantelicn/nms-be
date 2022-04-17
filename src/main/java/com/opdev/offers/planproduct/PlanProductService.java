package com.opdev.offers.planproduct;

import com.opdev.model.subscription.PlanProduct;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface PlanProductService {

    PlanProduct save(Long planId, Long productId, PlanProduct newPlanProduct);

    List<PlanProduct> findAll();

    List<PlanProduct> findAll(Long planId);

    Optional<PlanProduct> find(Long planId, Long productId);

    PlanProduct get(Long planProductId);

    PlanProduct update(@NonNull PlanProduct modified);

    void delete(Long planProductId);

}

package com.opdev.offers.planproduct;

import com.opdev.exception.ApiBadRequestException;
import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.model.subscription.Plan;
import com.opdev.model.subscription.PlanProduct;
import com.opdev.model.subscription.Product;
import com.opdev.model.user.User;
import com.opdev.offers.plan.PlanService;
import com.opdev.repository.PlanProductRepository;
import com.opdev.offers.product.ProductService;
import com.opdev.user.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlanProductServiceImpl implements PlanProductService {

    private final PlanProductRepository planProductRepository;
    private final PlanService planService;
    private final ProductService productService;
    private final UserService userService;

    @Override
    @Transactional
    public PlanProduct save(@NonNull Long planId, @NonNull Long productId, @NonNull PlanProduct newPlanProduct) {
        validate(newPlanProduct);

        final Plan foundPlan = planService.getById(planId);
        final Product foundProduct = productService.getById(productId);
        final User loggedUser = userService.getLoggedInUser();

        newPlanProduct.setPlan(foundPlan);
        newPlanProduct.setProduct(foundProduct);
        newPlanProduct.setCreatedBy(loggedUser);
        newPlanProduct.setCreatedOn(Instant.now());

        return planProductRepository.save(newPlanProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public PlanProduct get(@NonNull Long planProductId) {
        return planProductRepository
                .findById(planProductId)
                .orElseThrow(() -> ApiEntityNotFoundException
                        .builder()
                        .entity(PlanProduct.class.getSimpleName())
                        .id(planProductId.toString())
                        .build());

    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanProduct> findAll() {
        return planProductRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanProduct> findAll(@NonNull Long planId) {
        return planProductRepository.findByPlanId(planId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PlanProduct> find(@NonNull Long planId, @NonNull Long productId) {
        return planProductRepository
                .findByPlanIdAndProductId(planId, productId);
    }


    @Override
    @Transactional
    public PlanProduct update(@NonNull PlanProduct modified) {
        validate(modified);

        final PlanProduct foundPlanProduct = get(modified.getId());
        final User loggedUser = userService.getLoggedInUser();
        foundPlanProduct.update(modified, loggedUser);
        return planProductRepository.save(foundPlanProduct);
    }

    @Override
    @Transactional
    public void delete(@NotNull Long planProductId) {
        final PlanProduct foundPlanProduct = get(planProductId);
        planProductRepository.delete(foundPlanProduct);
    }

    private void validate(PlanProduct newPlanProduct) {
        if ((!newPlanProduct.getLimited() && newPlanProduct.getQuantity() != null) ||
                (newPlanProduct.getLimited() && newPlanProduct.getQuantity() == null)) {
            throw new ApiBadRequestException("Invalid data provided for a PlanProduct entity");
        }
    }

}

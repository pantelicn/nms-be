package com.opdev.offers.planproduct.dto;

import com.opdev.model.subscription.PlanProduct;
import com.opdev.offers.product.dto.ProductViewDto;
import com.opdev.offers.plan.dto.PlanViewDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlanProductViewDto {

    private Long id;

    private Integer quantity;

    private Boolean limited;

    private PlanViewDto plan;

    private ProductViewDto product;

    public PlanProductViewDto(PlanProduct createdPlanProduct) {
        id = createdPlanProduct.getId();
        quantity = createdPlanProduct.getQuantity();
        limited = createdPlanProduct.getLimited();
        plan = new PlanViewDto(createdPlanProduct.getPlan());
        product = new ProductViewDto(createdPlanProduct.getProduct());
    }
}

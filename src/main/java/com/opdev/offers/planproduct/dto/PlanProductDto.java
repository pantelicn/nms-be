package com.opdev.offers.planproduct.dto;

import com.opdev.model.subscription.PlanProduct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class PlanProductDto {

    private Integer quantity;

    @NotNull
    private Boolean limited;

    @NotNull
    private Long planId;

    @NotNull
    private Long productId;

    public PlanProduct asPlanProduct() {
        return PlanProduct
                .builder()
                .limited(limited)
                .quantity(quantity)
                .build();
    }

}

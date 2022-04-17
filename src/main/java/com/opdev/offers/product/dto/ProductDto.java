package com.opdev.offers.product.dto;


import com.opdev.model.subscription.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Builder
@Getter
@ToString(callSuper = true)
public class ProductDto {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    public Product asProduct() {
        return Product
                .builder()
                .name(name)
                .description(description)
                .build();
    }

}

package com.opdev.offers.product.dto;

import com.opdev.model.subscription.Product;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class ProductViewDto {

    private Long id;

    private String name;

    private String description;

    public ProductViewDto(Product product) {
        id = product.getId();
        name = product.getName();
        description = product.getDescription();
    }

}

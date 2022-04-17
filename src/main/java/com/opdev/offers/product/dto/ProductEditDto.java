package com.opdev.offers.product.dto;

import com.opdev.model.subscription.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ProductEditDto {

    @NonNull
    @NotNull
    private Long id;

    @NonNull
    @NotBlank
    private String name;

    @NonNull
    @NotBlank
    private String description;

    public Product asProduct() {
        return Product
                .builder()
                .id(id)
                .name(name)
                .description(description)
                .build();
    }

}

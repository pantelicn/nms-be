package com.opdev.subscription.usage.dto;

import com.opdev.dto.CompanyViewDto;
import com.opdev.model.subscription.ProductUsage;
import com.opdev.offers.product.dto.ProductViewDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.Period;

@Getter
@AllArgsConstructor
public class ProductUsageViewDto {

    private Long id;

    private Integer remaining;

    private Integer total;

    private Boolean limited;

    private LocalDate startDate;

    private LocalDate endDate;

    private Period period;

    private ProductViewDto product;

    public ProductUsageViewDto(ProductUsage productUsage) {
        id = productUsage.getId();
        remaining = productUsage.getRemaining();
        limited = productUsage.getLimited();
        startDate = productUsage.getStartDate();
        endDate = productUsage.getEndDate();
        period = productUsage.getPeriod();
        total = productUsage.getProduct().getPlanProduct().getQuantity();
        product = new ProductViewDto(productUsage.getProduct());
    }
}

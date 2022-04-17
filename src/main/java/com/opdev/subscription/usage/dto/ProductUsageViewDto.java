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

    private Boolean limited;

    private LocalDate startDate;

    private LocalDate endDate;

    private Period period;

    private CompanyViewDto company;

    private ProductViewDto product;

    public ProductUsageViewDto(ProductUsage productUsage) {
        id = productUsage.getId();
        remaining = productUsage.getRemaining();
        limited = productUsage.getLimited();
        startDate = productUsage.getStartDate();
        endDate = productUsage.getEndDate();
        period = productUsage.getPeriod();
        company = new CompanyViewDto(productUsage.getCompany());
        product = new ProductViewDto(productUsage.getProduct());
    }
}

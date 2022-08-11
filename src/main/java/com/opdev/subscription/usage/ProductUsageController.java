package com.opdev.subscription.usage;

import com.opdev.config.security.Roles;
import com.opdev.model.subscription.ProductUsage;
import com.opdev.subscription.usage.dto.ProductUsageViewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/product-usages")
@RequiredArgsConstructor
public class ProductUsageController {

    private final ProductUsageService productUsageService;

    @GetMapping("company/{companyUsername}")
    @PreAuthorize("hasRole('" + Roles.COMPANY + "') || hasRole('" + Roles.ADMIN + "')")
    public List<ProductUsageViewDto> findAllByCompany(@PathVariable final String companyUsername) {
        List<ProductUsage> foundProductUsages = productUsageService.findAllByCompany(companyUsername);

        return foundProductUsages
                .stream()
                .map(ProductUsageViewDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("company/{companyUsername}/remaining-posts")
    @PreAuthorize("hasRole('" + Roles.COMPANY + "') || hasRole('" + Roles.ADMIN + "')")
    public Integer findRemainingPosts(@PathVariable final String companyUsername) {
        return productUsageService.findRemainingPosts(companyUsername);
    }

}

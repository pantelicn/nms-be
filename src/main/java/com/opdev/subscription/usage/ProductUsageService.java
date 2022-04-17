package com.opdev.subscription.usage;

import com.opdev.model.subscription.ProductUsage;
import com.opdev.model.subscription.Subscription;

import java.util.List;

public interface ProductUsageService {

    ProductUsage save(ProductUsage productUsage);

    List<ProductUsage> saveAll(List<ProductUsage> productUsages);

    List<ProductUsage> findAllByCompany(String companyUsername);

    List<ProductUsage> createFromSubscription(Subscription subscription);
}

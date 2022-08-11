package com.opdev.repository;

import com.opdev.model.subscription.Product;
import com.opdev.model.subscription.ProductUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductUsageRepository extends JpaRepository<ProductUsage, Long> {

    List<ProductUsage> findAllByCompanyUserUsername(String companyUsername);

    ProductUsage findByCompanyUserUsernameAndProduct(String companyUsername, Product product);

}

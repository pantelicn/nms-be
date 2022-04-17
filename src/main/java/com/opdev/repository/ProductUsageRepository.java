package com.opdev.repository;

import com.opdev.model.subscription.ProductUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductUsageRepository extends JpaRepository<ProductUsage, Long> {

    List<ProductUsage> findAllByCompanyUserUsername(String companyUsername);

}

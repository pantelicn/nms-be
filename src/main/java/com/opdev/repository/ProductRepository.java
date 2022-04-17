package com.opdev.repository;

import com.opdev.model.subscription.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
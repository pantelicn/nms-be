package com.opdev.repository;

import java.util.Optional;

import com.opdev.model.subscription.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByName(String name);

}
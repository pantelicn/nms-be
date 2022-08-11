package com.opdev.offers.product;

import com.opdev.model.subscription.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    Product save(Product product);

    List<Product> findAll();

    Product getById(Long productId);

    Optional<Product> findById(Long productId);

    Product update(Product modified);

    void delete(Long productId);

    Product findByName(String name);

}

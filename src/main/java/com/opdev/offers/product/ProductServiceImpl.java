package com.opdev.offers.product;

import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.model.subscription.Product;
import com.opdev.model.user.User;
import com.opdev.repository.ProductRepository;
import com.opdev.user.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserService userService;

    @Override
    @Transactional
    public Product save(@NonNull Product product) {
        User loggedUser = userService.getLoggedInUser();

        product.setCreatedOn(Instant.now());
        product.setCreatedBy(loggedUser);

        return productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Product getById(@NonNull Long productId) {
        return findById(productId)
                .orElseThrow(() ->
                        ApiEntityNotFoundException
                                .builder()
                                .entity(Product.class.getSimpleName())
                                .id(productId.toString())
                                .build());
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findById(@NonNull Long productId) {
        return productRepository.findById(productId);
    }

    @Override
    @Transactional
    public Product update(@NonNull Product modified) {
        final Product foundProduct = getById(modified.getId());
        User loggedUser = userService.getLoggedInUser();
        foundProduct.update(modified, loggedUser);
        return productRepository.save(foundProduct);
    }

    @Override
    @Transactional
    public void delete(@NonNull Long productId) {
        final Product foundProduct = getById(productId);
        productRepository.delete(foundProduct);
    }

}

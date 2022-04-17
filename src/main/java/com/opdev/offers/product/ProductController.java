package com.opdev.offers.product;

import com.opdev.config.security.Roles;
import com.opdev.model.subscription.Product;
import com.opdev.offers.product.dto.ProductDto;
import com.opdev.offers.product.dto.ProductEditDto;
import com.opdev.offers.product.dto.ProductViewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/products")
@PreAuthorize("hasRole('" + Roles.ADMIN + "')")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductViewDto create(@Valid @RequestBody ProductDto product) {
        final Product newProduct = product.asProduct();
        final Product createdProduct = productService.save(newProduct);

        return new ProductViewDto(createdProduct);
    }

    @GetMapping
    public ResponseEntity<List<ProductViewDto>> findAll() {
        final List<Product> foundProducts = productService.findAll();
        final List<ProductViewDto> response = foundProducts
                .stream()
                .map(ProductViewDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("{productId}")
    public ResponseEntity<ProductViewDto> get(@PathVariable Long productId) {
        final Product foundProduct = productService.getById(productId);

        return ResponseEntity.ok(new ProductViewDto(foundProduct));
    }

    @PutMapping
    public ResponseEntity<ProductViewDto> update(@Valid @RequestBody ProductEditDto modified) {
        final Product updated = productService.update(modified.asProduct());

        return ResponseEntity.ok(new ProductViewDto(updated));
    }

    @DeleteMapping("{productId}")
    public ResponseEntity<Void> remove(@PathVariable Long productId) {
        productService.delete(productId);

        return ResponseEntity.noContent().build();
    }

}
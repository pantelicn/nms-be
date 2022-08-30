package com.opdev.subscription.usage;


import com.opdev.exception.ApiBadRequestException;
import com.opdev.model.subscription.PlanProduct;
import com.opdev.model.subscription.Product;
import com.opdev.model.subscription.ProductUsage;
import com.opdev.model.subscription.Subscription;
import com.opdev.offers.planproduct.PlanProductService;
import com.opdev.offers.product.ProductService;
import com.opdev.repository.ProductUsageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ProductUsageServiceImpl implements ProductUsageService {

    private final ProductUsageRepository productUsageRepository;
    private final PlanProductService planProductService;
    private final ProductService productService;

    @Override
    @Transactional
    public ProductUsage save(ProductUsage productUsage) {
        return productUsageRepository.save(productUsage);
    }

    @Override
    @Transactional
    public List<ProductUsage> saveAll(List<ProductUsage> productUsages) {
        return productUsageRepository.saveAll(productUsages);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductUsage> findAllByCompany(String companyUsername) {
        return productUsageRepository.findAllByCompanyUserUsername(companyUsername);
    }

    @Override
    @Transactional
    public List<ProductUsage> createFromSubscription(Subscription subscription) {
        List<PlanProduct> planProducts = planProductService.findAll(subscription.getPlan().getId());

        List<ProductUsage> usages = new ArrayList<>();
        planProducts.forEach(planProduct -> usages.add(buildProductUsage(subscription, planProduct)));

        return usages;
    }

    @Override
    @Transactional
    public Integer findRemainingPosts(String companyUsername) {
        ProductUsage postProductUsage = getPostProductUsage(companyUsername);
        return postProductUsage.getRemaining();
    }

    @Override
    @Transactional
    public void decreaseNumberOfRemainingPosts(final String companyUsername) {
        ProductUsage postProductUsage = getPostProductUsage(companyUsername);
        if (postProductUsage.getRemaining() == 0) {
            throw new ApiBadRequestException("You are not able to publish new post because you have reached the limit from your package.");
        }
        postProductUsage.setRemaining(postProductUsage.getRemaining() - 1);
        productUsageRepository.save(postProductUsage);
    }

    private ProductUsage getPostProductUsage(String companyUsername) {
        Product postProduct = productService.findByName("Post");
        return  productUsageRepository.findByCompanyUserUsernameAndProduct(companyUsername, postProduct);
    }

    private ProductUsage buildProductUsage(Subscription subscription, PlanProduct planProduct) {
        return ProductUsage
                .builder()
                .remaining(planProduct.getQuantity())
                .limited(planProduct.getLimited())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .period(subscription.getPeriod())
                .company(subscription.getCompany())
                .product(planProduct.getProduct())
                .build();
    }
}

package com.opdev.subscription;

import com.opdev.exception.ApiCompanyAlreadySubscribedException;
import com.opdev.model.subscription.ProductUsage;
import com.opdev.model.subscription.Subscription;
import com.opdev.repository.SubscriptionRepository;
import com.opdev.subscription.usage.ProductUsageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final ProductUsageService productUsageService;

    @Override
    @Transactional
    public Subscription subscribe(Subscription newSubscription) {
        validateIfAlreadySubscribed(newSubscription);

        Subscription created = subscriptionRepository.save(newSubscription);

        List<ProductUsage> productUsages = productUsageService.createFromSubscription(created);
        productUsageService.saveAll(productUsages);

        return created;
    }

    private void validateIfAlreadySubscribed(Subscription created) {
        Optional<Subscription> latestActiveSubscription = subscriptionRepository
                .findLatestActiveByCompany(created.getCompany());

        if (latestActiveSubscription.isPresent()) {
            throw new ApiCompanyAlreadySubscribedException(created.getCompany(), created.getPlan());
        }
    }

}

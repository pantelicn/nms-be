package com.opdev.subscription;

import com.opdev.company.service.CompanyService;
import com.opdev.exception.ApiCompanyAlreadySubscribedException;
import com.opdev.exception.SubscriptionNotFoundException;
import com.opdev.model.company.Company;
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

    private final SubscriptionRepository repository;
    private final ProductUsageService productUsageService;
    private final CompanyService companyService;

    @Override
    @Transactional
    public Subscription subscribe(Subscription newSubscription) {
        validateIfAlreadySubscribed(newSubscription);

        Subscription created = repository.save(newSubscription);

        List<ProductUsage> productUsages = productUsageService.createFromSubscription(created);
        productUsageService.saveAll(productUsages);

        return created;
    }

    @Override
    @Transactional(readOnly = true)
    public Subscription get(final String companyUsername) {
        Company found = companyService.getByUsername(companyUsername);
        return repository.findLatestActiveByCompany(found).orElseThrow(() -> new SubscriptionNotFoundException(companyUsername));
    }

    private void validateIfAlreadySubscribed(Subscription created) {
        Optional<Subscription> latestActiveSubscription = repository
                .findLatestActiveByCompany(created.getCompany());

        if (latestActiveSubscription.isPresent()) {
            throw new ApiCompanyAlreadySubscribedException(created.getCompany(), created.getPlan());
        }
    }

}

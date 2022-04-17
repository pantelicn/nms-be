package com.opdev.repository;

import com.opdev.model.company.Company;
import com.opdev.model.subscription.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @Query(value = "SELECT s FROM Subscription s WHERE s.company = ?1 AND s.subscriptionStatus = com.opdev.model.subscription.SubscriptionStatus.ACTIVE")
    Optional<Subscription> findLatestActiveByCompany(Company company);
}

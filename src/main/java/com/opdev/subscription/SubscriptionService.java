package com.opdev.subscription;

import com.opdev.model.subscription.Subscription;

public interface SubscriptionService {

    Subscription subscribe(Subscription created);

    Subscription get(String companyUsername);
    
}

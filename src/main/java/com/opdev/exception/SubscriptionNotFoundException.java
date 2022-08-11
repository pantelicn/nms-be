package com.opdev.exception;

public class SubscriptionNotFoundException extends RuntimeException {

    public SubscriptionNotFoundException(String username) {
        super(String.format("Subscription for company with username %s not found", username));
    }

}

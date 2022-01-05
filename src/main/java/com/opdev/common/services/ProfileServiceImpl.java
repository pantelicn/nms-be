package com.opdev.common.services;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class ProfileServiceImpl implements ProfileService {

    private final ConfigurableEnvironment env;

    @Override
    public boolean isAnyActive(final List<String> profiles) {
        Objects.requireNonNull(profiles);
        return getActive().stream().anyMatch(profiles::contains);
    }

    @Override
    public List<String> getActive() {
        return Arrays.asList(env.getActiveProfiles());
    }

    @Override
    public boolean isProduction() {
        return isAnyActive(Arrays.asList(Profiles.PROD_PROFILE));
    }
}

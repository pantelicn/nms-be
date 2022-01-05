package com.opdev.common.services;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Profiles {
    public static final String DEV_PROFILE = "dev";
    public static final String PROD_PROFILE = "prod";

    public static final String TEST_PROFILE = "test";
    public static final String TEST_VERIFICATION_TOKEN_PROFILE = TEST_PROFILE + "-verification-token";
}

package com.opdev.common.services;

import java.util.List;

public interface ProfileService {
    boolean isAnyActive(List<String> profiles);

    List<String> getActive();

    boolean isProduction();
}

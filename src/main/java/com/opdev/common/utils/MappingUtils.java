package com.opdev.common.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MappingUtils {
    public static boolean shouldUpdate(final Object newValue, final Object oldValue) {
        return null != newValue && !newValue.equals(oldValue);
    }
}

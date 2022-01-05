package com.opdev.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CodeGenerator {

    public static String generate(String name) {
        return name.replace(' ', '-').toUpperCase();
    }

}

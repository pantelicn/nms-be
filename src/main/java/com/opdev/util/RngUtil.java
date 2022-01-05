package com.opdev.util;

import java.security.SecureRandom;

public class RngUtil {

    /**
     * Generates a random byte array of the given length.
     * @param length Length of the byte array to be generated.
     * @return Byte array with randomly generated elements.
     */
    public static byte[] generateRandomBytes(int length) {
        byte[] bytes = new byte[length];
        (new SecureRandom()).nextBytes(bytes);
        return bytes;
    }

}

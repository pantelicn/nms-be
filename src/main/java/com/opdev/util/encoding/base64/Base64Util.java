package com.opdev.util.encoding.base64;

import java.util.Base64;

public class Base64Util {

    /**
     * Encodes the given byte array to a Base64 string.
     * @param bytes Byte array to be encoded.
     * @return Base64 string representation of the given byte array parameter.
     */
    public static String encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Decode the given Base64 string to a byte array.
     * @param base64String Base64 string to be decoded.
     * @return Byte array which corresponds to the given Base64 string input
     */
    public static byte[] decode(String base64String) {
        return Base64.getDecoder().decode(base64String);
    }

}

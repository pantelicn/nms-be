package com.opdev.exception;

public class InvalidAESCipherException extends ApiRuntimeException {

    public InvalidAESCipherException() {
        super("AES cipher decryption failed. Malformed cipher encountered.");
    }

}

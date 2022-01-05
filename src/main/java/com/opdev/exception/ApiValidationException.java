package com.opdev.exception;

public class ApiValidationException extends ApiBadRequestException {

    public ApiValidationException(final String msg) {
        super(msg);
    }

}

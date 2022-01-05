package com.opdev.exception;

import lombok.Getter;

@Getter
public class ApiVerificationTokenInvalidException extends ApiRuntimeException {

    private static final long serialVersionUID = 1L;

    private final String token;

    public ApiVerificationTokenInvalidException(final String message, final String token) {
        super(message);
        this.token = token;
    }

}

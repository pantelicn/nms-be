package com.opdev.exception;

import lombok.Getter;

@Getter
public class ApiVerificationTokenAlreadyUsed extends ApiRuntimeException {

    private static final long serialVersionUID = 1L;

    private final String token;

    public ApiVerificationTokenAlreadyUsed(final String message, final String token) {
        super(message);
        this.token = token;
    }

}

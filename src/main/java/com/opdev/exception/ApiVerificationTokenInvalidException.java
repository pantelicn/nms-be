package com.opdev.exception;

import lombok.Getter;

@Getter
public class ApiVerificationTokenInvalidException extends ApiRuntimeException {

    private static final long serialVersionUID = 1L;

    private final String activationCode;

    public ApiVerificationTokenInvalidException(final String message, final String activationCode) {
        super(message);
        this.activationCode = activationCode;
    }

}

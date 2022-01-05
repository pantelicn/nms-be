package com.opdev.exception;

public class ApiBadRequestException extends ApiRuntimeException {

    private static final long serialVersionUID = 1L;

    public ApiBadRequestException(final String msg) {
        super(msg);
    }

    public static ApiBadRequestException message(final String message) {
        return new ApiBadRequestException(message);
    }

}

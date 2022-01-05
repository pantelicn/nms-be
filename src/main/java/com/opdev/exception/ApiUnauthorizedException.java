package com.opdev.exception;

public class ApiUnauthorizedException extends ApiRuntimeException {

    private static final long serialVersionUID = 1L;

    public ApiUnauthorizedException() {
        super("Access is denied");
    }

    public ApiUnauthorizedException(String message) {
        super(message);
    }

}

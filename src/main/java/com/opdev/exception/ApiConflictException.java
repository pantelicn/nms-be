package com.opdev.exception;

public class ApiConflictException extends ApiRuntimeException {

    public ApiConflictException(String msg) {
        super(msg);
    }

    public static ApiConflictException message(final String message) {
        return new ApiConflictException(message);
    }

}

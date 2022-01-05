package com.opdev.exception;

public class ApiUserNotLoggedException extends ApiUnauthorizedException {

    private static final long serialVersionUID = 1L;

    public ApiUserNotLoggedException() {
        super("User is not logged in.");
    }

}

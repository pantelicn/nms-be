package com.opdev.exception;

public class ResetPasswordRequestNotFound extends RuntimeException {

    public ResetPasswordRequestNotFound() {
        super("Token not found");
    }

}

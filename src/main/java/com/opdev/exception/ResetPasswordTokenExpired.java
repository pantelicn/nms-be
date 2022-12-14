package com.opdev.exception;

public class ResetPasswordTokenExpired extends RuntimeException {

    public ResetPasswordTokenExpired() {
        super("Token expired");
    }

}

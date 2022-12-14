package com.opdev.exception;

public class PasswordsNotSameException extends RuntimeException {

    public PasswordsNotSameException() {
        super("Password and confirm password are not same!");
    }

}

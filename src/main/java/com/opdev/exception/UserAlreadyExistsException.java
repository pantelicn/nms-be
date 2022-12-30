package com.opdev.exception;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException() {
        super("User already exists!");
    }

}

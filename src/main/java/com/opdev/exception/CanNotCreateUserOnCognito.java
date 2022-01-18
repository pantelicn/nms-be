package com.opdev.exception;

public class CanNotCreateUserOnCognito extends RuntimeException {

    public CanNotCreateUserOnCognito(String username) {
        super(String.format("Can not create user on cognito with username %s!", username));
    }

}

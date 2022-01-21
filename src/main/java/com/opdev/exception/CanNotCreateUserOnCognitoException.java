package com.opdev.exception;

public class CanNotCreateUserOnCognitoException extends RuntimeException {

    public CanNotCreateUserOnCognitoException(String username) {
        super(String.format("Can not create user on cognito with username %s!", username));
    }

}

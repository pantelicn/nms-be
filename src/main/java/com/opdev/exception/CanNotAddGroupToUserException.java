package com.opdev.exception;

public class CanNotAddGroupToUserException extends RuntimeException {

    public CanNotAddGroupToUserException(String username) {
        super(String.format("Can not add group to user for username %s", username));
    }

}

package com.opdev.exception;

public class AlreadyFollowException extends RuntimeException {

    public AlreadyFollowException(Long companyId) {
        super(String.format("User already follow company with id %s", companyId.toString()));
    }

}

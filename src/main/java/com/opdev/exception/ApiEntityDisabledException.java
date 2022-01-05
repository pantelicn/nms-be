package com.opdev.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class ApiEntityDisabledException extends ApiRuntimeException {

    private static final long serialVersionUID = 1L;

    @NonNull
    private final String message;

    @NonNull
    private final String entity;

    /**
     * Either a Long ID or an username.
     */
    @NonNull
    private final String id;

}

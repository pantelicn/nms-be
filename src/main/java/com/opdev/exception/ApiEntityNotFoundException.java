package com.opdev.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class ApiEntityNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = 1L;

    @NonNull
    @Builder.Default
    private final String message = "Entity.not.found";

    @NonNull
    private final String entity;

    /**
     * Either a Long ID or a username.
     */
    @NonNull
    private final String id;
}

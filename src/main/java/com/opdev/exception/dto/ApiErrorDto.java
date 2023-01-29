package com.opdev.exception.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
@Getter
public class ApiErrorDto {

    private final String message;
    private final String requestId;

}

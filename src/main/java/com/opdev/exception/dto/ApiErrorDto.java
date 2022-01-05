package com.opdev.exception.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
// @AllArgsConstructor
// @NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class ApiErrorDto {

    private final String message;

}

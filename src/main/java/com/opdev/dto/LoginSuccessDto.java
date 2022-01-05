package com.opdev.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
@Getter
public class LoginSuccessDto {
    @NonNull
    private final String username;
    @NonNull
    private final String token;
    @NonNull
    @Builder.Default
    private List<String> roles = new ArrayList<>();
}

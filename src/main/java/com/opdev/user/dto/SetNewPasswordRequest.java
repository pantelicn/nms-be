package com.opdev.user.dto;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@AllArgsConstructor
public class SetNewPasswordRequest {

    @NotNull
    private String newPassword;

    @NotNull
    private String newPasswordConfirmation;

    @NonNull
    private UUID validityToken;

}

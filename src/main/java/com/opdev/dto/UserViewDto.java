package com.opdev.dto;

import java.util.Objects;

import com.opdev.model.user.User;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString(callSuper = true)
public class UserViewDto {

    private Long id;

    private String username;

    public UserViewDto(final User user) {
        this.asView(user);
    }

    private void asView(final User user) {
        Objects.requireNonNull(user);

        this.id = user.getId();
        this.username = user.getUsername();
    }

}

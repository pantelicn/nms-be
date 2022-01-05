package com.opdev.authentication.dto;

import java.util.Objects;

import com.opdev.dto.UserViewDto;
import com.opdev.model.talent.Talent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@ToString(callSuper = true)
public class TalentViewDto {

    private String firstName;

    private String lastName;

    private UserViewDto user;

    public TalentViewDto(final Talent talent) {
        this.asView(talent);
    }

    private void asView(final Talent talent) {
        Objects.requireNonNull(talent);

        this.firstName = talent.getFirstName();
        this.lastName = talent.getLastName();

        this.user = new UserViewDto(talent.getUser());
    }

}

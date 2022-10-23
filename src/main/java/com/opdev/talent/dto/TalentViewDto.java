package com.opdev.talent.dto;

import com.opdev.dto.LocationDto;
import com.opdev.dto.UserViewDto;
import com.opdev.model.talent.Talent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@ToString(callSuper = true)
public class TalentViewDto {

    private String firstName;

    private String lastName;

    private UserViewDto user;

    private LocationDto location;

    public TalentViewDto(Talent talent) {
        asView(talent);
    }

    private void asView(@NonNull Talent talent) {
        this.firstName = talent.getFirstName();
        this.lastName = talent.getLastName();
        this.user = new UserViewDto(talent.getUser());
        this.location = new LocationDto(talent.getCurrentLocation());
    }

}

package com.opdev.talent.dto;

import javax.validation.constraints.NotNull;

import com.opdev.model.location.TalentAvailableLocation;
import com.opdev.model.talent.Talent;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString
public class AvailableLocationCreateDto {

    @NotNull
    private String country;

    @NotNull
    private Long countryId;

    public TalentAvailableLocation asAvailableLocation(Talent talent) {
        return TalentAvailableLocation.builder().country(country).countryId(countryId).talent(talent).build();
    }

}

package com.opdev.talent.dto;

import com.opdev.model.location.AvailableLocation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString
public class AvailableLocationUpdateDto {

    @NotNull
    private String country;
    private Set<String> cities;

    public AvailableLocation asAvailableLocation() {
        return AvailableLocation.builder().country(country).cities(cities).build();
    }

}

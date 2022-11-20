package com.opdev.talent.dto;

import com.opdev.model.location.AvailableLocation;
import lombok.Getter;

import java.util.Set;

@Getter
public class AvailableLocationViewDto {

    private final Long id;
    private final String country;
    private final Set<String> cities;

    public AvailableLocationViewDto(AvailableLocation availableLocation) {
        id = availableLocation.getId();
        country = availableLocation.getCountry();
        cities = availableLocation.getCities();
    }

}

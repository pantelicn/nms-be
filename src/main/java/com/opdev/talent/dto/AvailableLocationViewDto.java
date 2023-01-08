package com.opdev.talent.dto;

import com.opdev.model.location.TalentAvailableLocation;
import lombok.Getter;

import java.util.Set;

@Getter
public class AvailableLocationViewDto {

    private final Long id;
    private final String country;
    private final Long countryId;
    private final Set<String> cities;

    public AvailableLocationViewDto(TalentAvailableLocation availableLocation) {
        id = availableLocation.getId();
        country = availableLocation.getCountry();
        cities = availableLocation.getCities();
        countryId = availableLocation.getCountryId();
    }

}

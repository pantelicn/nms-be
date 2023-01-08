package com.opdev.search.dto;

import java.util.Set;

import com.opdev.model.location.SearchTemplateAvailableLocation;

import lombok.Getter;

@Getter
public class SearchTemplateAvailableLocationViewDto {

    private final Long id;
    private final String country;
    private final Set<String> cities;

    public SearchTemplateAvailableLocationViewDto(SearchTemplateAvailableLocation availableLocation) {
        id = availableLocation.getId();
        country = availableLocation.getCountry();
        cities = availableLocation.getCities();
    }

}

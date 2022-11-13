package com.opdev.location.dto;

import com.opdev.model.location.City;
import lombok.Getter;

@Getter
public class CityViewDto {

    private final String name;
    private final Float latitude;
    private final Float longitude;

    public CityViewDto(City city) {
        name = city.getName();
        latitude = city.getLatitude();
        longitude = city.getLongitude();
    }

}

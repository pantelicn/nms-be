package com.opdev.location.dto;

import com.opdev.model.location.City;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CityAddDto {

    @NotBlank
    private String name;

    private Float latitude;

    private Float longitude;

    public City toCity() {
        return City.builder().name(name).latitude(latitude).longitude(longitude).build();
    }

}

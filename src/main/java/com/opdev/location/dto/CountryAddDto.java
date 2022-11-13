package com.opdev.location.dto;

import com.opdev.model.location.City;
import com.opdev.model.location.Country;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CountryAddDto {

    @NotBlank
    private String name;

    @NotNull
    private List<CityAddDto> cities;

    private String code;

    public Country toCountry() {
        List<City> cityEntities = this.cities.stream()
                .map(city -> City.builder().name(city.getName()).latitude(city.getLatitude()).longitude(city.getLongitude()).build())
                .collect(Collectors.toList());
        return Country.builder()
                .name(name)
                .cities(cityEntities)
                .code(code)
                .build();
    }

}

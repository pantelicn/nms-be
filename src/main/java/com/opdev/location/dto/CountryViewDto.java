package com.opdev.location.dto;

import com.opdev.model.location.Country;
import lombok.Getter;

@Getter
public class CountryViewDto {

    private final Long id;
    private final String name;
    private final String code;

    public CountryViewDto(Country country) {
        id = country.getId();
        name = country.getName();
        code = country.getCode();
    }

}

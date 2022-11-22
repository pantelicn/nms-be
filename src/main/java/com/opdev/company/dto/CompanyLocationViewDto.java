package com.opdev.company.dto;

import com.opdev.location.dto.CityViewDto;
import com.opdev.location.dto.CountryViewDto;
import com.opdev.model.location.CompanyLocation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CompanyLocationViewDto {

    private Long id;

    private CountryViewDto country;

    private String province;

    private CityViewDto city;

    private String address;

    public CompanyLocationViewDto(CompanyLocation location) {
        id = location.getId();
        country = new CountryViewDto(location.getCountry());
        province = location.getProvince();
        city = new CityViewDto(location.getCity());
        address = location.getAddress();
    }

}

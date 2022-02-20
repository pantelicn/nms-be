package com.opdev.company.dto;

import com.opdev.model.location.CompanyLocation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CompanyLocationViewDto {

    private Long id;

    private String country;

    private String province;

    private String city;

    private String countryCode;

    private String address;

    public CompanyLocationViewDto(CompanyLocation location) {
        id = location.getId();
        country = location.getCountry();
        province = location.getProvince();
        city = location.getCity();
        countryCode = location.getCountryCode();
        address = location.getAddress();
    }

}

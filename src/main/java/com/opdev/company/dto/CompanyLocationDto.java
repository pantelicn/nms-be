package com.opdev.company.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import com.opdev.model.location.City;
import com.opdev.model.location.CompanyLocation;
import com.opdev.model.location.Country;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CompanyLocationDto {

    @NonNull
    @NotEmpty
    private Long countryId;

    private String province;

    @NonNull
    @NotEmpty
    private Long cityId;

    @NotBlank
    @NotEmpty
    private String address;

    public CompanyLocation asCompanyLocation(Country country, City city) {
        return CompanyLocation.builder()
                .country(country)
                .province(province)
                .city(city)
                .address(address)
                .build();
    }

}

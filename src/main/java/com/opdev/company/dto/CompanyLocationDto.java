package com.opdev.company.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import com.opdev.model.location.CompanyLocation;

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
    private String country;

    private String province;

    @NonNull
    @NotEmpty
    private String city;

    @NonNull
    @NotEmpty
    private String countryCode;

    @NotBlank
    @NotEmpty
    private String address;

    public CompanyLocation asCompanyLocation() {
        return CompanyLocation.builder()
                .country(country)
                .province(province)
                .city(city)
                .countryCode(countryCode)
                .address(address)
                .build();
    }

}

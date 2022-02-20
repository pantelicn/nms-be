package com.opdev.company.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.opdev.model.location.CompanyLocation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString(callSuper = true)
public class UpdateCompanyLocationDto {

    @NotNull
    private Long id;

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
                .id(id)
                .country(country)
                .province(province)
                .city(city)
                .countryCode(countryCode)
                .address(address)
                .build();
    }

}

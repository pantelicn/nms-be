package com.opdev.dto;

import com.opdev.model.location.Location;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString(callSuper = true)
public class LocationDto {

    @NonNull
    private String country;

    // TODO: add an enum for provinces / states
    private String province;

    @NonNull
    private String city;

    @NonNull
    // TODO: add an enum for country codes
    private String countryCode;

    public LocationDto(@NonNull Location location) {
        this.country = location.getCountry();
        this.city = location.getCity();
        this.province = location.getProvince();
        this.countryCode = location.getCountryCode();
    }

}

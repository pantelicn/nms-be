package com.opdev.talent.dto;

import com.opdev.model.search.LocationFilter;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class LocationFilterDto {

    @NotNull
    private String country;
    private List<String> cities;

    public LocationFilter asLocationFilter() {
        return new LocationFilter(country, cities);
    }
}

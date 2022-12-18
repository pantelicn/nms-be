package com.opdev.talent.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TalentSearchDto {

    List<FacetSpecifierDto> facets;
    List<LocationFilterDto> locations;
    Integer experienceYears;

}

package com.opdev.search.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.opdev.model.search.SearchTemplate;

import com.opdev.talent.dto.AvailableLocationViewDto;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class SearchTemplateViewDto {

    private Long id;
    private String name;
    private List<FacetViewDto> facets = new ArrayList<>();
    private Integer experienceYears;
    private List<SearchTemplateAvailableLocationViewDto> availableLocations = new ArrayList<>();

    public SearchTemplateViewDto(@NonNull SearchTemplate model) {
        id = model.getId();
        name = model.getName();
        model.getFacets().forEach(facet -> facets.add(new FacetViewDto(facet)));
        experienceYears = model.getExperienceYears();
        availableLocations = model.getAvailableLocations().stream()
                .map(SearchTemplateAvailableLocationViewDto::new)
                .collect(Collectors.toList());
    }

}

package com.opdev.search.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

import com.opdev.model.search.SearchTemplate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SearchTemplateAddDto {

    @NonNull
    @NotEmpty
    private String name;
    private List<FacetAddDto> facets = new ArrayList<>();
    @NonNull
    @Min(0)
    @Max(99)
    private Integer experienceYears;
    @NonNull
    private List<SearchTemplateAvailableLocationUpdateDto> availableLocations = new ArrayList<>();

    public SearchTemplate asSearchTemplate() {
        SearchTemplate searchTemplate =  SearchTemplate.builder()
                .name(name)
                .facets(facets.stream().map(FacetAddDto::asFacet).collect(Collectors.toList()))
                .experienceYears(experienceYears)
                .build();

        searchTemplate.setAvailableLocations(availableLocations.stream()
                                    .map(availableLocation -> availableLocation.asAvailableLocation(searchTemplate))
                                    .collect(Collectors.toList()));
        return searchTemplate;
    }

}

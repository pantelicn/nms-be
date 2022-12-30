package com.opdev.search.dto;

import com.opdev.model.search.SearchTemplate;
import com.opdev.talent.dto.AvailableLocationUpdateDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SearchTemplateEditDto {

    @NonNull
    private Long id;

    @NonNull
    @NotEmpty
    private String name;

    @NonNull
    @Min(0)
    @Max(99)
    private Integer experienceYears;

    @NonNull
    private List<AvailableLocationUpdateDto> availableLocations = new ArrayList<>();
    private List<FacetEditDto> facets = new ArrayList<>();


    public SearchTemplate asSearchTemplate(SearchTemplate searchTemplate) {
        return SearchTemplate
                .builder()
                .id(id)
                .name(name)
                .facets(facets
                        .stream()
                        .map(e -> e.asFacet(searchTemplate))
                        .collect(Collectors.toList()))
                .experienceYears(experienceYears)
                .availableLocations(availableLocations.stream()
                        .map(AvailableLocationUpdateDto::asAvailableLocation)
                        .collect(Collectors.toList()))
                .build();
    }

}

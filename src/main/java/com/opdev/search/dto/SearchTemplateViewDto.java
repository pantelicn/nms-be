package com.opdev.search.dto;

import java.util.ArrayList;
import java.util.List;

import com.opdev.model.search.SearchTemplate;

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

    public SearchTemplateViewDto(@NonNull SearchTemplate model) {
        id = model.getId();
        name = model.getName();
        model.getFacets().forEach(facet -> facets.add(new FacetViewDto(facet)));
    }

}

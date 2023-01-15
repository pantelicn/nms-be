package com.opdev.search.dto;

import java.util.HashSet;
import java.util.Set;

import com.opdev.model.location.SearchTemplateAvailableLocation;
import com.opdev.model.search.SearchTemplate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString
public class SearchTemplateAvailableLocationUpdateDto {

    @NotNull
    private String country;

    @NonNull
    private Set<String> cities = new HashSet<>();

    public SearchTemplateAvailableLocation asAvailableLocation(SearchTemplate searchTemplate) {
        return SearchTemplateAvailableLocation.builder().country(country).cities(cities).searchTemplate(searchTemplate).build();
    }

}

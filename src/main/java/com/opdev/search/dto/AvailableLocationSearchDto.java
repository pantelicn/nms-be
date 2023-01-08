package com.opdev.search.dto;

import javax.validation.constraints.NotNull;

import com.opdev.model.location.SearchTemplateAvailableLocation;
import com.opdev.model.location.TalentAvailableLocation;
import com.opdev.model.search.SearchTemplate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString
public class AvailableLocationSearchDto {

    @NotNull
    private String country;

    public SearchTemplateAvailableLocation asAvailableLocation(SearchTemplate searchTemplate) {
        return SearchTemplateAvailableLocation.builder().country(country).searchTemplate(searchTemplate).build();
    }

}

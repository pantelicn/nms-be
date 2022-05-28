package com.opdev.talent.search;

import com.opdev.model.search.Facet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class TalentSearchCriteria {

    private final List<Facet> facets = new ArrayList<>();

}

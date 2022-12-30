package com.opdev.search;

import java.util.List;

import com.opdev.model.search.Facet;
import com.opdev.model.search.SearchTemplate;

public interface SearchTemplateService {

    SearchTemplate add(SearchTemplate searchTemplate, String companyUsername);

    SearchTemplate edit(SearchTemplate modified, String companyUsername);

    List<SearchTemplate> findAllForCompany(String companyUsername);

    SearchTemplate get(Long id, String companyUsername);

    void remove(Long id, String companyUsername);

    Facet addFacet(Long id, Facet newFacet, String companyUsername);

    Facet editFacet(Long id, Facet newFacet, String companyUsername);

    void removeFacet(Long id, Long facetId, String companyUsername);

}

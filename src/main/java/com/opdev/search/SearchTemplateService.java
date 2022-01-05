package com.opdev.search;

import java.util.List;

import com.opdev.model.search.Facet;
import com.opdev.model.search.SearchTemplate;

public interface SearchTemplateService {

    SearchTemplate add(String name, List<Facet> facets, String companyUsername);

    SearchTemplate edit(Long id, String newName, String companyUsername);

    List<SearchTemplate> findAllForCompany(String companyUsername);

    SearchTemplate get(Long id, String companyUsername);

    void remove(Long id, String companyUsername);

    Facet addFacet(Long id, Facet newFacet, String companyUsername);

    Facet editFacet(Long id, Facet newFacet, String companyUsername);

    void removeFacet(Long id, Long facetId, String companyUsername);

}

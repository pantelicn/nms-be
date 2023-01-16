package com.opdev.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.opdev.model.location.SearchTemplateAvailableLocation;
import com.opdev.model.search.SearchTemplate;

public interface SearchTemplateAvailableLocationRepository extends JpaRepository<SearchTemplateAvailableLocation, Long> {

    long deleteBySearchTemplate(SearchTemplate searchTemplate);

}

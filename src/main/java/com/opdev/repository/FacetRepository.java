package com.opdev.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.opdev.model.search.Facet;
import com.opdev.model.search.SearchTemplate;

public interface FacetRepository extends JpaRepository<Facet, Long> {

    Optional<Facet> findByIdAndSearchTemplate(Long id, SearchTemplate searchTemplate);

}

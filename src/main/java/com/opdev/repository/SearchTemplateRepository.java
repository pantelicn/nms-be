package com.opdev.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.opdev.model.company.Company;
import com.opdev.model.search.SearchTemplate;

public interface SearchTemplateRepository extends JpaRepository<SearchTemplate, Long> {

    Optional<SearchTemplate> findByIdAndCompany(Long id, Company company);

    List<SearchTemplate> findByCompany(Company company);

}

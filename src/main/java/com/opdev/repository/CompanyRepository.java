package com.opdev.repository;

import java.util.List;
import java.util.Optional;

import com.opdev.model.company.Company;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByUserUsername(final String username);

    /**
     * Returns all entities sorted by the given options.
     *
     * @param sort
     * @return all entities sorted by the given options
     */
    @Override
    @Query("SELECT c FROM Company c where c.user.enabled = true")
    List<Company> findAll(final Sort sort);

    /**
     * Returns a {@link Page} of entities meeting the paging restriction provided in
     * the {@code Pageable} object.
     *
     * @param pageable
     * @return a page of entities
     */
    @Override
    @Query("SELECT c FROM Company c where c.user.enabled = true")
    Page<Company> findAll(final Pageable pageable);

    Page<Company> findByNameContainsIgnoreCase(String nameStarts, Pageable pageable);

}

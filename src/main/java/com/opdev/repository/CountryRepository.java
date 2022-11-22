package com.opdev.repository;

import java.util.Optional;

import com.opdev.model.location.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {

    Optional<Country> findByName(String name);

}

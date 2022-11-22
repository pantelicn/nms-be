package com.opdev.repository;

import java.util.Optional;

import com.opdev.model.location.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {

    Optional<City> findByIdAndCountryId(Long id, Long countryId);

}

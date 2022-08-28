package com.opdev.repository;

import com.opdev.model.location.AvailableLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvailableLocationRepository extends JpaRepository<AvailableLocation, Long> {
}

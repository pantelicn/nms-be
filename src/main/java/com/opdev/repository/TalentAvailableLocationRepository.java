package com.opdev.repository;

import java.util.Optional;

import com.opdev.model.location.TalentAvailableLocation;
import com.opdev.model.talent.Talent;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TalentAvailableLocationRepository extends JpaRepository<TalentAvailableLocation, Long> {

    Optional<TalentAvailableLocation> findByIdAndTalent(Long id, Talent talent);

    void deleteByIdAndTalent(Long id, Talent talent);

}

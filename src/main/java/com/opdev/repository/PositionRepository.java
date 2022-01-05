package com.opdev.repository;

import com.opdev.model.talent.Position;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PositionRepository extends JpaRepository<Position, Long> {

    Optional<Position> findByCode(final String code);

    Long deleteByCode(final String code);

}

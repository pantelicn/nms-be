package com.opdev.repository;

import java.util.Optional;

import com.opdev.model.talent.Talent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TalentRepository extends JpaRepository<Talent, Long>, JpaSpecificationExecutor<Talent> {

    Optional<Talent> findByUserUsername(final String username);

}

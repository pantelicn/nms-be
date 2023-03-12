package com.opdev.repository;

import java.util.Optional;

import com.opdev.model.talent.Talent;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface TalentRepository extends JpaRepository<Talent, Long>, JpaSpecificationExecutor<Talent> {

    Optional<Talent> findByUserUsername(final String username);

    @Query("select t from Talent t where t.available = true and t.talentSkills.size > 0")
    Page<Talent> findAvailableWithSkills(Pageable pageable);

}

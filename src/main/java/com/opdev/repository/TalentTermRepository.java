package com.opdev.repository;

import com.opdev.model.term.TalentTerm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TalentTermRepository extends JpaRepository<TalentTerm, Long> {

    List<TalentTerm> findByTalentUserUsername(String username);

    Optional<TalentTerm> findByIdAndTalentUserUsername(Long id, String username);

}

package com.opdev.repository;

import com.opdev.model.term.Term;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TermRepository extends JpaRepository<Term, Long> {

    Optional<Term> findByCode(final String code);

}

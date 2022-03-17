package com.opdev.repository;

import com.opdev.model.term.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TermRepository extends JpaRepository<Term, Long>, JpaSpecificationExecutor<Term> {

    Optional<Term> findByCode(final String code);

    Term getByCode(String code);
}

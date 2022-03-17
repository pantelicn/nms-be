package com.opdev.term;

import com.opdev.model.term.Term;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

public interface TermService {

    Term add(final Term newTerm);

    Term get(final String code);

    List<Term> findAll(Specification<Term> termSpec);

    Term edit(final Term modified);

    void remove(final String code);

}

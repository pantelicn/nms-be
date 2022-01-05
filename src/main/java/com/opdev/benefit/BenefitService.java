package com.opdev.benefit;

import com.opdev.model.company.Benefit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface BenefitService {

    Benefit addBenefitToCompany(String companyUsername, final Benefit newBenefit);

    Benefit get(final Long id);

    Page<Benefit> get(final Specification<Benefit> specification, final Pageable pageable);

    List<Benefit> getByCompany(final String username);

    Benefit edit(final Benefit modified);

    void remove(final Long id);

}

package com.opdev.repository;

import com.opdev.model.company.Benefit;
import com.opdev.model.user.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface BenefitRepository extends JpaRepository<Benefit, Long>, JpaSpecificationExecutor<Benefit> {

    List<Benefit> findByCompanyUserUsername(final String username);

    Long deleteByCompanyUser(final User username);

}

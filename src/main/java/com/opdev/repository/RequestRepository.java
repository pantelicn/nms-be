package com.opdev.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.opdev.model.company.Company;
import com.opdev.model.request.Request;
import com.opdev.model.request.RequestStatus;
import com.opdev.model.talent.Talent;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Page<Request> findByCompanyAndStatus(Company company, RequestStatus status, Pageable pageable);

    Page<Request> findByTalentAndStatus(Talent talent, RequestStatus status, Pageable pageable);

    Optional<Request> findByIdAndTalent(Long id, Talent talent);

    Optional<Request> findByIdAndCompany(Long id, Company company);

}

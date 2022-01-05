package com.opdev.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.opdev.model.company.Company;
import com.opdev.model.request.Request;
import com.opdev.model.request.RequestStatus;
import com.opdev.model.talent.Talent;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByCompanyAndStatus(Company company, RequestStatus status);

    List<Request> findByTalentAndStatus(Talent talent, RequestStatus status);

    Optional<Request> findByIdAndTalent(Long id, Talent talent);

    Optional<Request> findByIdAndCompany(Long id, Company company);

}

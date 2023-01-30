package com.opdev.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.opdev.model.company.Company;
import com.opdev.model.request.Request;
import com.opdev.model.request.RequestStatus;
import com.opdev.model.talent.Talent;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByCompanyAndStatusIn(Company company, List<RequestStatus> statuses);

    Page<Request> findByTalentAndStatusIn(Talent talent, List<RequestStatus> statuses, Pageable pageable);

    Optional<Request> findByIdAndTalent(Long id, Talent talent);

    Optional<Request> findByIdAndCompany(Long id, Company company);

    @Modifying
    @Query("update Request r set r.seenByCompany = :seen where r.id = :id")
    void updateSeenByCompany(@Param("id") Long id, @Param("seen") boolean seen);

    @Modifying
    @Query("update Request r set r.seenByTalent = :seen where r.id = :id")
    void updateSeenByTalent(@Param("id") Long id, @Param("seen") boolean seen);

    Optional<Request> findTop1ByTalentIdAndCompanyIdOrderByCreatedOn(Long talentId, Long companyId);

    boolean existsByTalentAndCompanyAndStatusIn(Talent talent, Company company, List<RequestStatus> requestStatuses);

}

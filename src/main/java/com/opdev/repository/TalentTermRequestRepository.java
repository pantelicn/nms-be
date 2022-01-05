package com.opdev.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.opdev.model.request.Request;
import com.opdev.model.request.TalentTermRequest;
import com.opdev.model.request.TalentTermRequestStatus;

public interface TalentTermRequestRepository extends JpaRepository<TalentTermRequest, Long> {

    List<TalentTermRequest> findByRequestAndStatusIn(Request request, List<TalentTermRequestStatus> statuses);

}

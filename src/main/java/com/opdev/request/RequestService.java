package com.opdev.request;

import java.util.List;
import java.util.Optional;

import com.opdev.company.dto.RequestCreateDto;
import com.opdev.model.request.Request;
import com.opdev.model.request.RequestStatus;
import com.opdev.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RequestService {

    Request create(RequestCreateDto newRequest, String username);

    List<Request> findByStatusForCompany(String username, List<RequestStatus> statuses);

    Page<Request> findByStatusForTalent(String username, List<RequestStatus> statuses, Pageable pageable);

    Request editStatusForTalent(String username, Long id, RequestStatus newStatus);

    Request rejectByCompany(String username, Long id);

    void removeRequestForCompany(Long id, String username);

    Request getByIdAndCompany(Long id, String username);

    Request getByIdAndTalent(Long id, String username);

    void updateAsSeenByCompany(Long id);

    void updateAsSeenByTalent(Long id);

    Request edit(Request request, User user);

    List<Long> findAcceptedOrPendingTalentIdsForCompany(String companyUserName);

    Request editRequestNote(Long id, String username, String note);

    Optional<Request> findPreviousByTalentAndCompany(Long talentId, Long companyId);

}

package com.opdev.request;

import java.util.List;

import com.opdev.company.dto.RequestCreateDto;
import com.opdev.model.request.Request;
import com.opdev.model.request.RequestStatus;
import com.opdev.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RequestService {

    Request create(RequestCreateDto newRequest, String username);

    Page<Request> findByStatusForCompany(String username, List<RequestStatus> statuses, Pageable pageable);

    Page<Request> findByStatusForTalent(String username, List<RequestStatus> statuses, Pageable pageable);

    Request editStatusForTalent(String username, Long id, RequestStatus newStatus);

    Request rejectByCompany(String username, Long id);

    void removeRequestForCompany(Long id, String username);

    void removeRequestForTalent(Long id, String username);

    Request getByIdAndCompany(Long id, String username);

    Request getByIdAndTalent(Long id, String username);

    void updateAsSeenByCompany(Long id);

    void updateAsSeenByTalent(Long id);

    Request edit(Request request, User user);

    Request editRequestNote(Long id, String username, String note);

    Request findRejectedByTalentAndCompany(Long talentId, Long companyId);
}

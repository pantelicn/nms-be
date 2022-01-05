package com.opdev.request;

import java.util.List;

import com.opdev.company.dto.RequestCreateDto;
import com.opdev.model.request.Request;
import com.opdev.model.request.RequestStatus;
import com.opdev.model.user.User;

public interface RequestService {

    Request create(RequestCreateDto newRequest, String username);

    List<Request> findByStatusForCompany(String username, RequestStatus status);

    List<Request> findByStatusForTalent(String username, RequestStatus status);

    Request editStatusForTalent(String username, Long id, RequestStatus newStatus);

    void removeRequestForCompany(Long id, String username);

    Request getByIdAndCompany(Long id, String username);

    Request getByIdAndTalent(Long id, String username);

    Request edit(Request request, User user);

}

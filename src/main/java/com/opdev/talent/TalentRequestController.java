package com.opdev.talent;

import com.opdev.company.dto.RequestViewDto;
import com.opdev.config.security.Roles;
import com.opdev.model.request.Request;
import com.opdev.model.request.RequestStatus;
import com.opdev.request.RequestService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/talents/{username}/requests")
@RequiredArgsConstructor
public class TalentRequestController {

    private static final int MAX_REQUESTS_PER_PAGE = 30;
    private final RequestService service;

    @GetMapping("pending")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.TALENT + "'))")
    public Page<RequestViewDto> findWithStatusPending(@PathVariable String username,
                                                      @RequestParam(defaultValue = "0") Integer page) {
        Pageable pageable = PageRequest.of(page, MAX_REQUESTS_PER_PAGE);
        final Page<Request> found = service.findByStatusForTalent(username, RequestStatus.PENDING, pageable);
        return found.map(RequestViewDto::new);
    }

    @GetMapping("counter-offers")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.TALENT + "'))")
    public Page<RequestViewDto> findWithStatusCounterOfferCompany(@PathVariable String username,
                                                                  @RequestParam(defaultValue = "0") Integer page) {
        Pageable pageable = PageRequest.of(page, MAX_REQUESTS_PER_PAGE);
        final Page<Request> found = service.findByStatusForTalent(username, RequestStatus.COUNTER_OFFER_COMPANY, pageable);
        return found.map(RequestViewDto::new);
    }

    @GetMapping("accepted")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.TALENT + "'))")
    public Page<RequestViewDto> findWithStatusAccepted(@PathVariable String username,
                                                       @RequestParam(defaultValue = "0") Integer page) {
        Pageable pageable = PageRequest.of(page, MAX_REQUESTS_PER_PAGE);
        final Page<Request> found = service.findByStatusForTalent(username, RequestStatus.ACCEPTED, pageable);
        return found.map(RequestViewDto::new);
    }

    @PutMapping("{id}")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.TALENT + "'))")
    public void editStatus(@PathVariable String username, @PathVariable Long id, @RequestParam RequestStatus status) {
        service.editStatusForTalent(username, id, status);
    }

}

package com.opdev.talent;

import java.util.List;

import com.opdev.company.dto.RequestViewDto;
import com.opdev.config.security.Roles;
import com.opdev.model.request.Request;
import com.opdev.model.request.RequestStatus;
import com.opdev.request.RequestService;
import com.opdev.talent.dto.TalentRequestDetailViewDto;
import com.opdev.talent.dto.TalentRequestViewDto;

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

    @GetMapping("active")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.TALENT + "'))")
    public Page<TalentRequestViewDto> findWithStatusPending(@PathVariable String username,
                                                            @RequestParam(defaultValue = "0") Integer page) {
        Pageable pageable = PageRequest.of(page, MAX_REQUESTS_PER_PAGE);
        List<RequestStatus> requiredStatuses = List.of(RequestStatus.PENDING, RequestStatus.COUNTER_OFFER_TALENT, RequestStatus.COUNTER_OFFER_COMPANY);
        final Page<Request> found = service.findByStatusForTalent(username, requiredStatuses, pageable);
        return found.map(TalentRequestViewDto::new);
    }

    @GetMapping("counter-offers")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.TALENT + "'))")
    public Page<RequestViewDto> findWithStatusCounterOfferCompany(@PathVariable String username,
                                                                  @RequestParam(defaultValue = "0") Integer page) {
        Pageable pageable = PageRequest.of(page, MAX_REQUESTS_PER_PAGE);
        final Page<Request> found = service.findByStatusForTalent(username, List.of(RequestStatus.COUNTER_OFFER_COMPANY), pageable);
        return found.map(RequestViewDto::new);
    }

    @GetMapping("accepted")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.TALENT + "'))")
    public Page<RequestViewDto> findWithStatusAccepted(@PathVariable String username,
                                                       @RequestParam(defaultValue = "0") Integer page) {
        Pageable pageable = PageRequest.of(page, MAX_REQUESTS_PER_PAGE);
        final Page<Request> found = service.findByStatusForTalent(username, List.of(RequestStatus.ACCEPTED), pageable);
        return found.map(RequestViewDto::new);
    }

    @GetMapping("{id}")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.TALENT + "'))")
    public TalentRequestDetailViewDto find(@PathVariable String username, @PathVariable Long id) {
        final Request found = service.getByIdAndTalent(id, username);
        service.updateAsSeenByTalent(id);
        return new TalentRequestDetailViewDto(found);
    }

    @PutMapping("{id}")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.TALENT + "'))")
    public void editStatus(@PathVariable String username, @PathVariable Long id, @RequestParam RequestStatus status) {
        service.editStatusForTalent(username, id, status);
    }

}

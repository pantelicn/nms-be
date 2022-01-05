package com.opdev.talent;

import com.opdev.company.dto.RequestViewDto;
import com.opdev.config.security.Roles;
import com.opdev.model.request.Request;
import com.opdev.model.request.RequestStatus;
import com.opdev.request.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("v1/talents/{username}/requests")
@RequiredArgsConstructor
public class TalentRequestController {

    private final RequestService service;

    @GetMapping("pending")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.TALENT + "'))")
    public List<RequestViewDto> findWithStatusPending(@PathVariable String username) {
        final List<Request> found = service.findByStatusForTalent(username, RequestStatus.PENDING);
        return found.stream().map(RequestViewDto::new).collect(Collectors.toList());
    }

    @GetMapping("counter-offers")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.TALENT + "'))")
    public List<RequestViewDto> findWithStatusCounterOfferCompany(@PathVariable String username) {
        final List<Request> found = service.findByStatusForTalent(username, RequestStatus.COUNTER_OFFER_COMPANY);
        return found.stream().map(RequestViewDto::new).collect(Collectors.toList());
    }

    @GetMapping("accepted")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.TALENT + "'))")
    public List<RequestViewDto> findWithStatusAccepted(@PathVariable String username) {
        final List<Request> found = service.findByStatusForTalent(username, RequestStatus.ACCEPTED);
        return found.stream().map(RequestViewDto::new).collect(Collectors.toList());
    }

    @PutMapping("{id}")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.TALENT + "'))")
    public void editStatus(@PathVariable String username, @PathVariable Long id, @RequestParam RequestStatus status) {
        service.editStatusForTalent(username, id, status);
    }

}

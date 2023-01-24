package com.opdev.company.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.opdev.company.dto.RequestCreateDto;
import com.opdev.company.dto.RequestDetailViewDto;
import com.opdev.company.dto.RequestNoteEditDto;
import com.opdev.company.dto.RequestViewDto;
import com.opdev.config.security.Roles;
import com.opdev.mail.NullHireMailSender;
import com.opdev.model.request.Request;
import com.opdev.model.request.RequestStatus;
import com.opdev.notification.NotificationFactory;
import com.opdev.notification.NotificationService;
import com.opdev.request.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/companies/{username}/requests")
@RequiredArgsConstructor
public class CompanyRequestController {

    private final RequestService service;

    private final NotificationService notificationService;

    private final NullHireMailSender mailSender;

    @PostMapping
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDetailViewDto create(@RequestBody @Valid RequestCreateDto newRequest,
                                 @PathVariable String username) {
        final Request created = service.create(newRequest, username);
        notificationService.createOrUpdate(NotificationFactory.createRequestNotification(created.getId(), created.getTalent().getUser(), created.getCompany().getName()));
        mailSender.sendRequestReceivedEmail(created.getTalent().getUser().getUsername(), created.getId(), created.getCompany().getName());
        return new RequestDetailViewDto(created);
    }

    @GetMapping("active")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestViewDto> findWithStatusPending(@PathVariable String username) {
        List<RequestStatus> requiredStatuses = List.of(RequestStatus.PENDING, RequestStatus.COUNTER_OFFER_TALENT, RequestStatus.COUNTER_OFFER_COMPANY);
        final List<Request> found = service.findByStatusForCompany(username, requiredStatuses);
        return found.stream().map(RequestViewDto::new).collect(Collectors.toList());
    }

    @GetMapping("accepted")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestViewDto> findWithStatusAccepted(@PathVariable String username) {
        List<RequestStatus> requiredStatuses = List.of(RequestStatus.ACCEPTED);
        final List<Request> found = service.findByStatusForCompany(username, requiredStatuses);
        return found.stream().map(RequestViewDto::new).collect(Collectors.toList());
    }

    @GetMapping("{id}")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))")
    public RequestDetailViewDto find(@PathVariable String username, @PathVariable Long id) {
        final Request found = service.getByIdAndCompany(id, username);
        service.updateAsSeenByCompany(id);
        return new RequestDetailViewDto(found);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))")
    public void remove(@PathVariable Long id, @PathVariable String username) {
        service.removeRequestForCompany(id, username);
    }

    @PatchMapping("{id}/note")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))")
    public ResponseEntity<RequestViewDto> editNote(@PathVariable Long id,
                                                   @PathVariable String username,
                                                   @RequestBody @Valid RequestNoteEditDto note) {
        Request updatedRequest = service.editRequestNote(id, username, note.getNote());
        return ResponseEntity.ok(new RequestViewDto(updatedRequest));
    }

    @PutMapping("{id}/reject")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))")
    public void editStatus(@PathVariable String username, @PathVariable Long id) {
        service.rejectByCompany(username, id);
    }

}

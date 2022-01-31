package com.opdev.company.controller;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.opdev.company.dto.RequestCreateDto;
import com.opdev.company.dto.RequestViewDto;
import com.opdev.config.security.Roles;
import com.opdev.model.request.Request;
import com.opdev.model.request.RequestStatus;
import com.opdev.request.RequestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/companies/{username}/requests")
@RequiredArgsConstructor
public class CompanyRequestController {

    private final static int MAX_REQUESTS_PER_PAGE = 30;

    private final RequestService service;

    @PostMapping
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestViewDto create(@RequestBody @Valid RequestCreateDto newRequest,
                                 @PathVariable String username) {
        final Request created = service.create(newRequest, username);
        return new RequestViewDto(created);
    }

    @GetMapping("pending")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))")
    @ResponseStatus(HttpStatus.OK)
    public Page<RequestViewDto> findWithStatusPending(@PathVariable String username,
                                                      @RequestParam(defaultValue = "0") Integer page) {
        Pageable pageable = PageRequest.of(page, MAX_REQUESTS_PER_PAGE);
        final Page<Request> found = service.findByStatusForCompany(username, RequestStatus.PENDING, pageable);
        return found.map(RequestViewDto::new);
    }

    @GetMapping("counter-offers")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))")
    @ResponseStatus(HttpStatus.OK)
    public Page<RequestViewDto> findWithStatusCounterOfferTalent(@PathVariable String username,
                                                                 @RequestParam(defaultValue = "0") Integer page) {
        Pageable pageable = PageRequest.of(page, MAX_REQUESTS_PER_PAGE);
        final Page<Request> found = service.findByStatusForCompany(username, RequestStatus.COUNTER_OFFER_TALENT, pageable);
        return found.map(RequestViewDto::new);
    }

    @GetMapping("accepted")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))")
    @ResponseStatus(HttpStatus.OK)
    public Page<RequestViewDto> findWithStatusAccepted(@PathVariable String username,
                                                       @RequestParam(defaultValue = "0") Integer page) {
        Pageable pageable = PageRequest.of(page, MAX_REQUESTS_PER_PAGE);
        final Page<Request> found = service.findByStatusForCompany(username, RequestStatus.ACCEPTED, pageable);
        return found.map(RequestViewDto::new);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))")
    public void remove(@PathVariable Long id, @PathVariable String username) {
        service.removeRequestForCompany(id, username);
    }

}

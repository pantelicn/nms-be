package com.opdev.company.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.opdev.company.service.CompanyRegistrationService;
import com.opdev.company.service.CompanyService;
import com.opdev.company.dto.CompanyRegistrationDto;
import com.opdev.company.dto.CompanyUpdateDto;
import com.opdev.config.security.Roles;
import com.opdev.dto.CompanyViewDto;
import com.opdev.dto.paging.PageDto;
import com.opdev.model.company.Company;
import com.opdev.model.user.User;
import com.opdev.user.UserService;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/companies")
@RequiredArgsConstructor
class CompanyController {

    private final ApplicationEventPublisher eventPublisher;
    private final CompanyRegistrationService companyRegistrationService;
    private final CompanyService companyService;
    private final UserService userService;

    @PostMapping
    @PreAuthorize("permitAll()")
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyViewDto add(@Valid @RequestBody final CompanyRegistrationDto companyRegistrationDto) {
        LOGGER.info("Registering a new company: {}", companyRegistrationDto.getUsername());

        final Company company = companyRegistrationDto.asCompany();
        final Company registeredCompany = companyRegistrationService.register(company);

        return new CompanyViewDto(registeredCompany);
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public PageDto<CompanyViewDto> viewMultiple(@PageableDefault Pageable pageable) {
        LOGGER.info("Viewing all companies");

        final Page<Company> results = companyService.view(pageable);

        final List<CompanyViewDto> content = results.getContent().stream().map(CompanyViewDto::new)
                .collect(Collectors.toList());
        return PageDto.from(results, content);
    }

    @GetMapping("/{username}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<CompanyViewDto> view(@PathVariable final String username) {
        LOGGER.info("Viewing the company: {}", username);

        final Company company = companyService.view(username);
        return ResponseEntity.ok(new CompanyViewDto(company));
    }

    @PutMapping("/{username}")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "')) " + //
            "|| hasRole('" + Roles.ADMIN + "')")
    public ResponseEntity<CompanyViewDto> update(@PathVariable final String username,
            @Valid @RequestBody final CompanyUpdateDto companyUpdateDto) {
        LOGGER.info("Updating the company: {}", username);

        final Company oldCompany = companyService.getByUsername(username);
        User admin = null;
        if (userService.isAdminLoggedIn()) {
            admin = userService.getLoggedInUser();
        }

        final Company company = companyService.update(companyUpdateDto.asCompany(oldCompany, admin));
        return ResponseEntity.ok(new CompanyViewDto(company));
    }

    @DeleteMapping("/{username}")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "')) " + //
            "|| hasRole('" + Roles.ADMIN + "')")
    public void delete(@PathVariable final String username, @RequestParam(required = false) final boolean disable) {
        if (disable) {
            LOGGER.info("Disabling the company: {}", username);
            companyService.disable(username);
            return;
        }

        LOGGER.info("Deleting the company: {}", username);
        companyService.delete(username);
    }

}

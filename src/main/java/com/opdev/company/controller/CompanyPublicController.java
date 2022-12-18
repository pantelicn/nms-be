package com.opdev.company.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.opdev.company.service.CompanyService;
import com.opdev.dto.CompanyPublicViewDto;
import com.opdev.model.company.Company;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/public/companies")
@RequiredArgsConstructor
public class CompanyPublicController {

    private static final int MAX_COMPANIES_PER_PAGE = 30;
    private final CompanyService service;

    @GetMapping("{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<CompanyPublicViewDto> getById(@PathVariable Long id) {
        Company found = service.getById(id);
        return ResponseEntity.ok(new CompanyPublicViewDto(found));
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public Page<CompanyPublicViewDto> findAllByName(@RequestParam String nameStartsWith, @RequestParam(defaultValue = "0") Integer page) {
        Pageable pageable = PageRequest.of(page, MAX_COMPANIES_PER_PAGE);
        Page<Company> found = service.findByNameStarts(nameStartsWith, pageable);
        return found.map(CompanyPublicViewDto::new);
    }

}

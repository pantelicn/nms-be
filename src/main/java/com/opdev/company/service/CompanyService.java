package com.opdev.company.service;

import com.opdev.model.company.Company;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface CompanyService {

    Company view(final String username);

    Page<Company> view(final Pageable pageable);

    Company getByUsername(final String username);

    Optional<Company> findByUsername(final String username);

    Company update(final Company updatedCompany);

    void disable(final String username);

    void delete(final String username);

    String uploadProfileImage(String companyUsername, MultipartFile image);

    Company getById(Long id);

    Company save(Company company);

}

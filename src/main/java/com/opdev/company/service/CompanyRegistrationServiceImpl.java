package com.opdev.company.service;

import java.util.Objects;
import java.util.Optional;

import com.opdev.exception.ApiEmailExistsException;
import com.opdev.model.company.Company;
import com.opdev.model.user.User;
import com.opdev.repository.CompanyRepository;
import com.opdev.repository.UserRepository;
import com.opdev.repository.UserRoleRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
class CompanyRegistrationServiceImpl implements CompanyRegistrationService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final UserRoleRepository userRoleRepository;

    @Transactional
    @Override
    public Company register(Company company) {
        validateCompany(company);

        userRepository.save(company.getUser());
        company.getUser().getUserRoles().forEach(userRoleRepository::save);
        return companyRepository.save(company);
    }

    private void validateCompany(final Company company) throws ApiEmailExistsException {
        Objects.requireNonNull(company);

        final Optional<User> existingUser = userRepository.findByUsername(company.getUser().getUsername());
        if (existingUser.isPresent()) {
            LOGGER.info("The username already exists: {}", company.getUser().getUsername());
            // TODO: handle this exception in the global message handler, and return 201
            throw ApiEmailExistsException.builder().message("User.already.exists").entity("Company")
                    .id(company.getUser().getUsername()).build();
        }
    }
}

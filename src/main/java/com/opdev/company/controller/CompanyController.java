package com.opdev.company.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.opdev.company.dto.ImageUploadedDto;
import com.opdev.company.service.CompanyRegistrationService;
import com.opdev.company.service.CompanyService;
import com.opdev.company.dto.CompanyRegistrationDto;
import com.opdev.company.dto.CompanyUpdateDto;
import com.opdev.config.security.Roles;
import com.opdev.dto.CompanyViewDto;
import com.opdev.dto.paging.PageDto;
import com.opdev.location.LocationService;
import com.opdev.mail.NullHireMailSender;
import com.opdev.model.company.Company;
import com.opdev.model.location.City;
import com.opdev.model.location.Country;
import com.opdev.model.user.User;
import com.opdev.user.UserService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/companies")
@RequiredArgsConstructor
class CompanyController {

    private final CompanyRegistrationService companyRegistrationService;
    private final CompanyService companyService;
    private final UserService userService;
    private final NullHireMailSender nullHireMailSender;
    private final PasswordEncoder passwordEncoder;
    private final LocationService locationService;

    @PostMapping
    @PreAuthorize("permitAll()")
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyViewDto add(@Valid @RequestBody final CompanyRegistrationDto companyRegistrationDto) {
        LOGGER.info("Registering a new company: {}", companyRegistrationDto.getUsername());
        final Country foundCountry = locationService.findByCountryId(companyRegistrationDto.getLocation().getCountryId());
        final City foundCity = locationService.findByCityIdAndCountryId(companyRegistrationDto.getLocation().getCountryId(), companyRegistrationDto.getLocation().getCityId());
        final Company company = companyRegistrationDto.asCompany(passwordEncoder, foundCountry, foundCity);
        final Company registeredCompany = companyRegistrationService.register(company);
        nullHireMailSender.sendRegistrationEmail(companyRegistrationDto.getUsername(), company.getUser().getVerificationToken());
        return new CompanyViewDto(registeredCompany);
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public Page<CompanyViewDto> viewMultiple(@PageableDefault Pageable pageable) {
        LOGGER.info("Viewing all companies");
        return companyService.view(pageable).map(CompanyViewDto::new);
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
        final Country foundCountry = locationService.findByCountryId(companyUpdateDto.getNewLocation().getCountryId());
        final City foundCity = locationService.findByCityIdAndCountryId(companyUpdateDto.getNewLocation().getCountryId(), companyUpdateDto.getNewLocation().getCityId());

        final Company company = companyService.update(companyUpdateDto.asCompany(oldCompany, admin, foundCountry, foundCity));
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

    @PutMapping("{username}/profile-image")
    public ImageUploadedDto uploadProfileImage(@PathVariable String username, @RequestParam("image") MultipartFile file) {
        String path = companyService.uploadProfileImage(username, file);
        return ImageUploadedDto.builder().path(path).build();
    }

}

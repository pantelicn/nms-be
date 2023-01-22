package com.opdev.company.service;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.opdev.exception.ApiBadRequestException;
import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.model.company.Company;
import com.opdev.model.subscription.PlanType;
import com.opdev.model.subscription.Subscription;
import com.opdev.model.user.Notification;
import com.opdev.model.user.User;
import com.opdev.notification.NotificationFactory;
import com.opdev.notification.NotificationService;
import com.opdev.repository.CompanyRepository;
import com.opdev.user.UserService;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final UserService userService;
    private final NotificationService notificationService;
    private static final List<String> VALID_PROFILE_IMAGE_EXTENSIONS = List.of("png", "jpg", "jpeg");

    @Value("${nullhire.profile-images-dir}")
    private String profileImagesDir;

    @Transactional(readOnly = true)
    @Override
    public Company view(final String username) {
        Objects.requireNonNull(username);

        final Company company = getByUsername(username);
        if (!userService.isAdminLoggedIn()) {   // FIXME Why check for enabled user here instead of the filter?
            userService.ensureIsEnabled(company.getUser());
        }

        return company;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Company> view(final Pageable pageable) {
        Objects.requireNonNull(pageable);
        return companyRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Company getByUsername(final String username) {
        return findByUsername(username).orElseThrow(() -> ApiEntityNotFoundException.builder()
                .message("Entity.not.found").entity("Company").id(username).build());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Company> findByUsername(final String username) {
        Objects.requireNonNull(username);
        return companyRepository.findByUserUsername(username);
    }

    @Transactional
    @Override
    public Company update(final Company updatedCompany) {
        Objects.requireNonNull(updatedCompany);
        if (!userService.isAdminLoggedIn()) {
            userService.ensureIsEnabled(updatedCompany.getUser());
        }
        return companyRepository.save(updatedCompany);
    }

    @Transactional
    @Override
    public void disable(final String username) {
        Objects.requireNonNull(username);

        final Company company = getByUsername(username);

        final User updatedUser = company.getUser().toBuilder().enabled(Boolean.FALSE).build();
        updatedUser.setModifiedBy(userService.resolveModifiedBy(updatedUser));

        final Company disabledCompany = company.toBuilder().user(updatedUser).build();
        userService.save(updatedUser);
        companyRepository.save(disabledCompany);
    }

    @Transactional
    @Override
    public void delete(final String username) {
        Objects.requireNonNull(username);

        final Company company = getByUsername(username);

        if (!userService.isAdminLoggedIn()) {
            userService.ensureIsEnabled(company.getUser());
        }

        companyRepository.delete(company);
        userService.delete(username);
    }

    @Override
    @Transactional
    public String uploadProfileImage(final String companyUsername, final MultipartFile image) {
        validateProfileImageExtension(image.getOriginalFilename());
        Company found = getByUsername(companyUsername);
        String fullPath = generateFullPath(image.getOriginalFilename(), found.getName());
        found.setProfileImage(fullPath);
        Path fileNameAndPath = Paths.get(URI.create(profileImagesDir + "/" + fullPath).getPath());
        try {
            Files.write(fileNameAndPath, image.getBytes());
            companyRepository.save(found);
            return fileNameAndPath.toString();
        } catch (IOException e) {
            LOGGER.error("Unable to upload profile image: {}", e.getMessage(), e);
            throw ApiBadRequestException.message("Unable to upload profile image.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Company getById(@NonNull final Long id) {
        return companyRepository.findById(id).orElseThrow(() -> ApiEntityNotFoundException.builder()
                .message("Entity.not.found").entity("Company").id(id.toString()).build());
    }

    @Override
    @Transactional
    public Company save(final Company company) {
        return companyRepository.save(company);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Company> findByNameStarts(@NonNull final String nameStarts, @NonNull final Pageable pageable) {
        return companyRepository.findByNameContainsIgnoreCase(nameStarts, pageable);
    }

    @Override
    @Transactional
    public void createWelcomeNotification(final String companyUsername) {
        Notification accountActivateNotification;
        Company foundCompany = getByUsername(companyUsername);
        Subscription companySubscription = foundCompany.getSubscriptions().get(0);
        if (companySubscription.getPlan().getType() == PlanType.TRIAL) {
            accountActivateNotification = NotificationFactory.createWelcomeNotificationCompanyTrialPeriod(foundCompany);
        } else {
            accountActivateNotification = NotificationFactory.createWelcomeNotificationCompanyTrialPeriod(foundCompany);
        }

        notificationService.create(accountActivateNotification);
    }

    private String generateFullPath(String originalFileName, String companyName) {
        String fileExtension = FilenameUtils.getExtension(originalFileName);
        StringBuilder sb = new StringBuilder();
        return sb.append(UUID.randomUUID())
                .append('-')
                .append(companyName.replaceAll(" ", "-").toLowerCase(Locale.ROOT))
                .append('.')
                .append(fileExtension.toLowerCase(Locale.ROOT)).toString();
    }

    private void validateProfileImageExtension(String originalFileName) {
        String fileExtension = FilenameUtils.getExtension(originalFileName);
        if (!VALID_PROFILE_IMAGE_EXTENSIONS.contains(fileExtension.toLowerCase(Locale.ROOT))) {
            throw ApiBadRequestException.message("Invalid profile image extension.");
        }
    }

}

package com.opdev.user;

import com.opdev.company.service.CompanyService;
import com.opdev.config.security.Roles;
import com.opdev.exception.ApiEntityDisabledException;
import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.exception.ApiUserNotLoggedException;
import com.opdev.exception.ApiVerificationTokenInvalidException;
import com.opdev.exception.PasswordsNotSameException;
import com.opdev.exception.ResetPasswordTokenExpired;
import com.opdev.mail.NullHireMailSender;
import com.opdev.model.company.Company;
import com.opdev.model.subscription.PlanType;
import com.opdev.model.subscription.Subscription;
import com.opdev.model.user.Notification;
import com.opdev.model.user.ResetPasswordRequest;
import com.opdev.model.user.User;
import com.opdev.model.user.UserType;
import com.opdev.notification.NotificationFactory;
import com.opdev.notification.NotificationService;
import com.opdev.repository.UserRepository;
import com.opdev.user.resetpasswordrequest.ResetPasswordRequestService;
import com.opdev.user.verification.VerificationTokenService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final VerificationTokenService verificationTokenService;
    private final ResetPasswordRequestService resetPasswordRequestService;
    private final NullHireMailSender nullHireMailSender;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findByUsername(@NonNull String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    @Override
    public User getByUsername(@NonNull String username) {
        return findByUsername(username).orElseThrow(() -> ApiEntityNotFoundException.builder()
                .entity("User").id(username).message("Entity.not.found").build());
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isAdminLoggedIn() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final List<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return authentication.isAuthenticated() && roles.contains(Roles.ADMIN);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findLoggedInUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated()) {
            return userRepository.findByUsername(authentication.getName());
        }

        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public User getLoggedInUser() {
        return findLoggedInUser().orElseThrow(ApiUserNotLoggedException::new);
    }

    @Transactional
    @Override
    public void delete(String username) {
        final User user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        verificationTokenService.deleteByUser(user);
        userRepository.delete(user);
    }

    @Transactional
    @Override
    public User save(final User user) {
        Objects.requireNonNull(user);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    @Override
    public User resolveModifiedBy(final User user) {
        Objects.requireNonNull(user);

        User modifiedBy = user;
        if (isAdminLoggedIn()) {
            modifiedBy = getLoggedInUser();
        }
        return modifiedBy;
    }

    @Transactional(readOnly = true)
    @Override
    public void ensureIsEnabled(User user) {
        Objects.requireNonNull(user);
        if (Boolean.FALSE.equals(user.getEnabled())) {
            throw ApiEntityDisabledException.builder().message("Entity.disabled").entity("User").id(user.getUsername())
                    .build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUserByUsernameAndType(@NonNull final String talentUsername, @NonNull final UserType type) {
        return userRepository.findByUsernameAndType(talentUsername, type);
    }

    @Override
    @Transactional
    public User activateUser(final UUID activationCode) {
        Optional<User> found = userRepository.findByVerificationTokenActivationCode(activationCode);
        if (found.isEmpty()) {
            throw new ApiVerificationTokenInvalidException("User with a verification code not found",
                    activationCode.toString());
        }
        User foundUser = found.get();
        verificationTokenService.use(foundUser.getVerificationToken());
        foundUser.setEnabled(true);
        return userRepository.save(foundUser);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        final User user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        user.getUserRoles()
                .forEach(userRole -> authorities.add(new SimpleGrantedAuthority(userRole.getRole().getName())));

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                                                                      user.getEnabled(), true, true, true, authorities);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("permitAll()")
    public void resetPasswordBegin(final String email) {
        final User foundUser = findByUsername(email).orElseThrow(() -> new UsernameNotFoundException(email));
        ResetPasswordRequest resetPasswordRequest = resetPasswordRequestService.create(foundUser);
        nullHireMailSender.sendResetPasswordEmail(foundUser.getUsername(), resetPasswordRequest.getValidityToken());
    }

    @Override
    @Transactional
    @PreAuthorize("permitAll()")
    public void resetPasswordFinish(@NonNull final UUID validityToken, @NonNull final String newPassword, @NonNull final String newPasswordConfirmation) {
        ResetPasswordRequest foundResetPasswordRequest = resetPasswordRequestService.findByValidityToken(validityToken);
        if (foundResetPasswordRequest.getValidTo().isBefore(Instant.now())) {
            throw new ResetPasswordTokenExpired();
        }
        if (!newPassword.equals(newPasswordConfirmation)) {
            throw new PasswordsNotSameException();
        }
        User user = foundResetPasswordRequest.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        foundResetPasswordRequest.setUsed(true);
    }

}

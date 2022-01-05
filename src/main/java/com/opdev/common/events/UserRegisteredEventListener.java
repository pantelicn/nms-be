package com.opdev.common.events;

import java.util.Objects;

import javax.servlet.http.HttpServletResponse;

import com.opdev.authentication.VerificationTokenService;
import com.opdev.model.user.User;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.RequiredArgsConstructor;

/**
 *
 * Triggered when the {@link UserRegisteredEvent} is fired.
 *
 * Creates a location header and sets {@link HttpStatus#CREATED} code to a
 * response.
 *
 */
@Component
@RequiredArgsConstructor
class UserRegisteredEventListener implements CommonApplicationEventListener<UserRegisteredEvent> {

    private final VerificationTokenService verificationTokenService;

    @Override
    public void onApplicationEvent(final UserRegisteredEvent event) {
        Objects.requireNonNull(event);

        prepareResponse();
        sendConfirmationEmail(event.getUser());
    }

    private void prepareResponse() {
        final String uriLocation = ServletUriComponentsBuilder.fromCurrentContextPath().path("/login").toUriString();

        final HttpServletResponse response = getResponse();
        response.setHeader(HttpHeaders.LOCATION, uriLocation);
        response.setStatus(HttpStatus.CREATED.value());
    }

    private void sendConfirmationEmail(final User user) {
        Objects.requireNonNull(user);

        final String endpoint = ServletUriComponentsBuilder.fromCurrentContextPath().path("/v1/userverification")
                .replaceQueryParam("token", "").build().encode().toUriString();
        verificationTokenService.sendWelcomeVerificationEmail(endpoint, user);
    }

}
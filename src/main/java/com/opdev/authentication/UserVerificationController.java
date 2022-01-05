package com.opdev.authentication;

import java.net.URI;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/userverification")
class UserVerificationController {

    private final VerificationTokenService verificationTokenService;

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<Void> enable(@RequestParam final String token) {
        Objects.requireNonNull(token);
        LOGGER.info("Verifying token {}", token);

        this.verificationTokenService.enableUser(token);

        return ResponseEntity.ok().location(createLoginUri()).build();
    }

    @PostMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<Void> resend(@RequestParam final String token) {
        Objects.requireNonNull(token);
        LOGGER.info("Token {} expired, re-sending an email...", token);

        final String endpoint = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString()
                + "/v1/userverification?token=";
        this.verificationTokenService.resendVerificationEmail(endpoint, token);

        return ResponseEntity.ok().location(createLoginUri()).build();
    }

    protected URI createLoginUri() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/login").encode()
                .build().toUri();
    }

}

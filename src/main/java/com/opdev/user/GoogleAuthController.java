package com.opdev.user;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.auth.SessionCookieOptions;
import com.opdev.dto.LoginSuccessDto;
import com.opdev.user.dto.GoogleAuthRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("v1/google-talents")
@RequiredArgsConstructor
public class GoogleAuthController {

    private final GoogleAuthService service;
    @Value("${nullhire.domain}")
    private String nullhireDomain;

    @PostMapping
    public ResponseEntity<LoginSuccessDto> signInOrUp(@Valid @RequestBody GoogleAuthRequest req, HttpServletResponse response) {
        LoginSuccessDto responseBody = service.singInOrUp(req.getCredential());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", nullhireDomain);
        response.addHeader(HttpHeaders.SET_COOKIE, createSessionCookie(responseBody.getToken()).toString());
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    private ResponseCookie createSessionCookie(String jwt) {
        int cookieValidityInSeconds = 5 * 24 * 60 * 60; //take it from variables
        return buildCookie("token", jwt, cookieValidityInSeconds);
    }

    private ResponseCookie buildCookie(String name, String value, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .maxAge(maxAge)
                .secure(true)
                .httpOnly(true)
                .sameSite("Lax")
                .domain("localhost")
                .path("/") // global cookie accessible everywhere
                .build();

        return cookie;
    }

}

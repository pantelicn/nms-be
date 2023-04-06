package com.opdev.user;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.opdev.dto.LoginSuccessDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("v1/google-talents")
@RequiredArgsConstructor
public class GoogleAuthController {

    private static final String GOOGLE_AUTH_PATH = "/google-talents";
    private static final String TOKEN_COOKIE_NAME = "token";
    private final GoogleAuthService service;

    @Value("${nullhire.domain}/" + GOOGLE_AUTH_PATH)
    private String authRedirectUrl;

    @Value("${nullhire.google.cookie.secure}")
    private boolean cookieSecure;

    @Value("${nullhire.google.cookie.domain}")
    private String cookieDomain;

    @Value("${nullhire.google.cookie.validity-in-seconds}")
    private int validityInSeconds;


    @ResponseStatus(HttpStatus.FOUND)
    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void signInOrUp(@RequestParam(name = "credential") String idToken, HttpServletResponse response) {
        LoginSuccessDto responseBody = service.singInOrUp(idToken);
        response.addHeader(HttpHeaders.LOCATION, authRedirectUrl);
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie(responseBody.getToken()).toString());
    }

    private ResponseCookie buildCookie(String value) {
        return ResponseCookie.from(TOKEN_COOKIE_NAME, value)
                .maxAge(validityInSeconds)
                .secure(cookieSecure)
                .httpOnly(true)
                .sameSite("Lax")
                .domain(cookieDomain)
                .path("/") // global cookie accessible everywhere
                .build();
    }

}

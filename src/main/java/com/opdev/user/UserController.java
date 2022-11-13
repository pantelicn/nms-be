package com.opdev.user;

import java.net.URI;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    @Value("${nullhire.domain}")
    private String domain;

    private final UserService userService;

    @GetMapping("/activate")
    public ResponseEntity<Void> accountActivation(@RequestParam UUID activationCode) {
        userService.activateUser(activationCode);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(domain + "/activation")).build();
    }

}
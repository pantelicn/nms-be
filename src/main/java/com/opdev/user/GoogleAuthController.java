package com.opdev.user;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.opdev.dto.LoginSuccessDto;
import com.opdev.user.dto.GoogleAuthRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("v1/google-talents")
@RequiredArgsConstructor
public class GoogleAuthController {

    private final GoogleAuthService service;

    @PostMapping
    public ResponseEntity<LoginSuccessDto> signInOrUp(@Valid @RequestBody GoogleAuthRequest req) {
        LoginSuccessDto responseBody = service.singInOrUp(req.getIdToken());
        return ResponseEntity.ok(responseBody);
    }

}

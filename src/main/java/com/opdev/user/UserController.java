package com.opdev.user;

import java.net.URI;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.opdev.company.service.CompanyService;
import com.opdev.model.user.User;
import com.opdev.model.user.UserType;
import com.opdev.user.dto.SetNewPasswordRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    @Value("${nullhire.domain}")
    private String domain;

    private final UserService userService;
    private final CompanyService companyService;

    @GetMapping("/activate")
    public ResponseEntity<Void> accountActivation(@RequestParam UUID activationCode) {
        User activatedUser = userService.activateUser(activationCode);
        if (activatedUser.isCompany()) {
            companyService.createWelcomeNotification(activatedUser.getUsername());
        }
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(domain + "/activation?userType=" + (activatedUser.getType().equals(UserType.COMPANY) ? "COMPANY" : "TALENT"))).build();
    }

    @GetMapping("/reset-password/begin")
    public void resetPasswordBegin(@RequestParam String email) {
        userService.resetPasswordBegin(email);
    }

    @PutMapping("reset-password/finish")
    public void resetPasswordFinish(@Valid @RequestBody SetNewPasswordRequest request) {
        userService.resetPasswordFinish(request.getValidityToken(), request.getNewPassword(), request.getNewPasswordConfirmation());
    }

}

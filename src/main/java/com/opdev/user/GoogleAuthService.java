package com.opdev.user;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.opdev.config.security.JWTUtil;
import com.opdev.config.security.Roles;
import com.opdev.dto.LoginSuccessDto;
import com.opdev.exception.ApiUnauthorizedException;
import com.opdev.model.talent.Talent;
import com.opdev.model.user.AuthType;
import com.opdev.model.user.User;
import com.opdev.model.user.UserRole;
import com.opdev.talent.TalentService;
import com.opdev.user.role.RoleService;
import com.opdev.user.userole.UserRoleService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GoogleAuthService {

    private final UserService userService;
    private final RoleService roleService;
    private final UserRoleService userRoleService;
    private final TalentService talentService;

    @Value("${nullhire.google.client-id}")
    private String clientId;

    @Transactional
    public LoginSuccessDto singInOrUp(String credential) {
        String username = verifyIdTokenAndGetEmail(credential);
        Optional<User> found = userService.findByUsername(username);
        if (found.isEmpty()) {
            User newUser = User.builder()
                    .enabled(true)
                    .username(username)
                    .authType(AuthType.GOOGLE)
                    .build();
            userService.save(newUser);
            UserRole talentUserRole = UserRole.builder()
                    .role(roleService.getTalentRole())
                    .user(newUser)
                    .build();
            userRoleService.create(talentUserRole);
            Talent newTalent = Talent.builder()
                    .user(newUser)
                    .availabilityChangeDate(Instant.now()).build();
            talentService.save(newTalent);
            found = Optional.of(newUser);
        }
        username = found.get().getUsername();
        String generatedToken = JWTUtil.generateToken(Roles.TALENT, username);
        return LoginSuccessDto.builder()
                .token(generatedToken)
                .username(username)
                .roles(List.of(Roles.TALENT))
                .build();
    }


    private String verifyIdTokenAndGetEmail(String idToken) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(List.of(clientId)).build();
        try {
            return verifier.verify(idToken).getPayload().getEmail();
        } catch (IOException | GeneralSecurityException e) {
            throw new ApiUnauthorizedException("Authentication failed");
        }
    }

}

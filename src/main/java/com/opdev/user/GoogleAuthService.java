package com.opdev.user;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.opdev.config.security.JWTUtil;
import com.opdev.config.security.Roles;
import com.opdev.dto.LoginSuccessDto;
import com.opdev.exception.ApiUnauthorizedException;
import com.opdev.model.talent.Talent;
import com.opdev.model.user.AuthType;
import com.opdev.model.user.Role;
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

    @Transactional
    public LoginSuccessDto singInOrUp(String idToken) {
        String username = verifyIdTokenAndGetEmail(idToken);
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
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            return decodedToken.getEmail();
        } catch (FirebaseAuthException e) {
            throw new ApiUnauthorizedException("Authentication failed");
        }
    }

}

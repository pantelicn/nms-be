package com.opdev.authentication;

import java.util.Optional;

import com.opdev.model.user.User;
import com.opdev.model.user.VerificationToken;

public interface VerificationTokenService {

    void sendWelcomeVerificationEmail(String endpoint, User user);

    void resendVerificationEmail(String endpoint, String token);

    VerificationToken createAndSaveVerificationToken(User user);

    boolean isTokenUnique(String token);

    Optional<VerificationToken> getToken(String token);

    void enableUser(String token);
}

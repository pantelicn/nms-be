package com.opdev.config.security;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

import java.time.Instant;
import java.util.Date;

import com.auth0.jwt.JWT;

public class JWTUtil {

    public static String generateToken(String authorities, String username) {
        return JWT.create().withClaim("roles", authorities)
                .withSubject(username)
                .withExpiresAt(Date.from(Instant.now().plusMillis(SecurityConfig.EXPIRATION_TIME)))
                .sign(HMAC512(SecurityConfig.SECRET.getBytes()));
    }

}

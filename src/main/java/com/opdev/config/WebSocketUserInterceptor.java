package com.opdev.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.opdev.config.security.SecurityConfig;
import com.opdev.exception.ApiUnauthorizedException;
import lombok.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class WebSocketUserInterceptor implements ChannelInterceptor {

    /**
     * Token header passed when initiating websocket connection via STOMP client
     */
    private static final String TOKEN_HEADER = "token";

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> tokenHeader = accessor.getNativeHeader(TOKEN_HEADER);
            if (tokenHeader == null || tokenHeader.isEmpty()) {
                throw new ApiUnauthorizedException();
            }
            accessor.setUser(getPrincipalFromToken(tokenHeader.get(0)));
        }
        return message;
    }

    private Principal getPrincipalFromToken(String token) {
        final DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(SecurityConfig.SECRET.getBytes())).build()
                .verify(token);
        if (decodedJWT.getSubject() != null) {
            return new UsernamePasswordAuthenticationToken(decodedJWT.getSubject(), null,
                                                           getAuthorities(decodedJWT));
        }
        throw new RuntimeException("Invalid token");
    }

    private Collection<SimpleGrantedAuthority> getAuthorities(final DecodedJWT decodedJWT) {
        final Claim roles = decodedJWT.getClaim("roles");
        List<SimpleGrantedAuthority> authorities = Collections.emptyList();
        if (roles != null) {
            authorities = Stream.of(roles.asString().split(",")).map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
        return authorities;
    }

}

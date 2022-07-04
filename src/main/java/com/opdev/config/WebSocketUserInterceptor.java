package com.opdev.config;

import com.opdev.exception.ApiUnauthorizedException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Component
public class WebSocketUserInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;

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
            accessor.setUser(getPrincipalFromIdToken(tokenHeader.get(0)));
        }
        return message;
    }

    private Principal getPrincipalFromIdToken(String idToken) {
        Jwt jwt = jwtDecoder.decode(idToken);
        return jwtAuthenticationConverter.convert(jwt);
    }

}

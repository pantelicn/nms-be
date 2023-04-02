package com.opdev.config.security;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opdev.dto.LoginSuccessDto;
import com.opdev.exception.ApiBadRequestException;
import com.opdev.model.user.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper mapper = new ObjectMapper();
    private final AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(final HttpServletRequest req, final HttpServletResponse res)
            throws AuthenticationException {
        try {
            final User user = mapper.readValue(req.getInputStream(), User.class);

            return authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        } catch (final IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ApiBadRequestException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(final HttpServletRequest req, final HttpServletResponse res,
            final FilterChain chain, final Authentication auth) throws IOException {
        final String authorities = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        final String token = JWTUtil.generateToken(authorities, ((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername());

        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding(StandardCharsets.UTF_8.toString());

        try (final PrintWriter out = res.getWriter()) {
            out.write(generateResponse(token, auth.getName(), auth.getAuthorities()));
        }
    }

    private String generateResponse(final String token, final String username,
            final Collection<? extends GrantedAuthority> authorities) throws JsonProcessingException {
        final List<String> roles = authorities.stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        final LoginSuccessDto successDto = LoginSuccessDto.builder().username(username).token(token).roles(roles)
                .build();
        return mapper.writeValueAsString(successDto);
    }

}

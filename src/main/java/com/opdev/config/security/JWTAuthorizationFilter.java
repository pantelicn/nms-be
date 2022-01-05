package com.opdev.config.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opdev.exception.dto.ApiErrorDto;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class JWTAuthorizationFilter extends FilterSecurityInterceptor {

    public JWTAuthorizationFilter(final AuthenticationManager authManager) {
        super();
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        final HttpServletRequest req = (HttpServletRequest) request;
        final String header = req.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith(SecurityConfig.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            final UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } catch (final TokenExpiredException e) {
            LOGGER.error(e.getMessage(), e);
            handleExpiredToken((HttpServletResponse) response);
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(final HttpServletRequest request) {
        final String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token != null) {
            final DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(SecurityConfig.SECRET.getBytes())).build()
                    .verify(token.replace(SecurityConfig.TOKEN_PREFIX, ""));
            if (decodedJWT.getSubject() != null) {
                return new UsernamePasswordAuthenticationToken(decodedJWT.getSubject(), null,
                        getAuthorities(decodedJWT));
            }
        }
        return null;
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

    protected void handleExpiredToken(final HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());

        final ApiErrorDto apiError = ApiErrorDto.builder().message("token.expired").build();
        try {
            final String apiErrorJson = new ObjectMapper().writeValueAsString(apiError);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(apiErrorJson);
        } catch (final IOException e) {
            final String ioErrorMessage = String.format(
                    "Error while mapping '%s' to json, OR while writing it to the response: %s", apiError.toString(),
                    e.getMessage());
            LOGGER.error(ioErrorMessage, e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

    }
}

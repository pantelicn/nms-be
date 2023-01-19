package com.opdev.config.security;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class SecurityConfig {

    @Value("${nullhire.allowed-origins}")
    private String[] allowedOrigins;

    public static final String SECRET = "SecretKeyToGenJWTs";
    static final long EXPIRATION_TIME = 864_000_000; // 10 days
    // static final long EXPIRATION_TIME = 100; // very quick 😏
    public static final String TOKEN_PREFIX = "Bearer ";

    @Bean
    public CorsFilter corsFilter() {
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(allowedOrigins));
        config.addAllowedHeader("*"); // this allows all headers
        config.setAllowCredentials(true);

        final List<String> allowedHttpMethods = Stream.of( //
                HttpMethod.OPTIONS, //
                HttpMethod.HEAD, //
                HttpMethod.GET, //
                HttpMethod.PUT, //
                HttpMethod.POST, //
                HttpMethod.DELETE, //
                HttpMethod.PATCH //
        ).map(httpMethod -> httpMethod.name()).collect(Collectors.toList());
        config.setAllowedMethods(allowedHttpMethods);

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

package com.opdev.config.security;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public static final String SECRET = "SecretKeyToGenJWTs";
    static final long EXPIRATION_TIME = 864_000_000; // 10 days
    // static final long EXPIRATION_TIME = 100; // very quick 😏
    public static final String TOKEN_PREFIX = "Bearer ";

    // TODO: @GoranNS90 this is a temp. configuration for development purposes.
    // TODO: @GoranNS90 Please make it production ready.
    @Bean
    public CorsFilter corsFilter() {
        final CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:4200"); // STOMP doesn't allow wildcard for allowed origins
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

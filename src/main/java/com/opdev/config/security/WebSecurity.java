package com.opdev.config.security;

import com.opdev.common.services.ProfileService;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true, prePostEnabled = true)
class WebSecurity extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final ProfileService profileService;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        final HttpSecurity builder = http.cors().and();
        if (!profileService.isProduction()) {
            builder.authorizeRequests() //
                    .antMatchers("/h2-console/**").permitAll() //
                    .antMatchers(HttpMethod.GET, "/swagger-ui/**", "/v2/api-docs").permitAll() //
                    .antMatchers("/swagger-resources/**").permitAll() //
                    .and();
        }
        builder.csrf().disable().addFilter(new JWTAuthenticationFilter(authenticationManager())) //
                .addFilter(new JWTAuthorizationFilter(authenticationManager())) //
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        if (!profileService.isProduction()) {
            // required for the h2 DB
            http.headers().frameOptions().disable();
        }
    }

    @Override
    public void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        // TODO: path should be replaced with appropriate value
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

}

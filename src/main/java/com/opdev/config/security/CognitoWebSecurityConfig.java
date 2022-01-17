package com.opdev.config.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;

@Configuration
@EnableGlobalMethodSecurity(jsr250Enabled = true, prePostEnabled = true)
public class CognitoWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${cognito.userpool.nms}")
    private String cognitoUrl;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().disable()
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .oauth2ResourceServer(oauth2ResourceServer -> {
                    oauth2ResourceServer.authenticationManagerResolver(getAuthenticationManagerResolver());
                });
    }

    private JwtIssuerAuthenticationManagerResolver getAuthenticationManagerResolver() {
        Map<String, AuthenticationManager> authenticationManagers = new HashMap<>();

        addManager(authenticationManagers, cognitoUrl);

        return new JwtIssuerAuthenticationManagerResolver(authenticationManagers::get);
    }

    public void addManager(Map<String, AuthenticationManager> authenticationManagers, String issuer) {
        JwtAuthenticationProvider authenticationProvider = new JwtAuthenticationProvider(JwtDecoders.fromOidcIssuerLocation(issuer));
        authenticationProvider.setJwtAuthenticationConverter(getJwtAuthenticationConverter());
        authenticationManagers.put(issuer, authenticationProvider::authenticate);
    }

    private JwtAuthenticationConverter getJwtAuthenticationConverter() {
        JwtAuthenticationConverter conv = new JwtAuthenticationConverter();
        conv.setJwtGrantedAuthoritiesConverter(jwt -> {

            List<String> cognitoGroups = (List<String>) jwt.getClaims().get("cognito:groups");
            if(cognitoGroups == null) {
                return new ArrayList<>();
            }

            return cognitoGroups.stream()
                    .map(group -> new SimpleGrantedAuthority("ROLE_" + group))
                    .collect(Collectors.toList());
        });
        conv.setPrincipalClaimName("email");
        return conv;
    }

}

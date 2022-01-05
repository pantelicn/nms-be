package com.opdev;

import java.util.HashMap;
import java.util.Map;

import com.opdev.company.dto.CompanyRegistrationDto;
import com.opdev.dto.LoginSuccessDto;
import com.opdev.dto.TalentRegistrationDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public abstract class AbstractIntegrationTest {

    protected static final String API_PREFIX = "/v1";
    protected static final String COMPANIES_API = "/companies/";
    protected static final String TALENTS_API = "/talents/";
    protected static final String VERIFICATION_API = "/userverification/";

    protected static final String ADMIN_NIKOLA = "znikola@xxx.xxx";
    protected static final String ADMIN_NIKOLA_PASSWORD = "corolla";
    protected static final String ADMIN_GORAN = "gox69@opdev.rs";
    protected static final String ADMIN_GORAN_PASSWORD = "rav4";

    protected static final String DEFAULT_TALENT_FIRST_NAME = "Java";
    protected static final String DEFAULT_TALENT_LAST_NAME = "Dev";
    protected static final String DEFAULT_TALENT_USERNAME = "xxx@java.com";
    protected static final String DEFAULT_TALENT_PASSWORD = "java4life";

    protected static final String DEFAULT_COMPANY_NAME = "opdev";
    protected static final String DEFAULT_COMPANY_DESCRIPTION = "the best.";
    protected static final String DEFAULT_COMPANY_USERNAME = "gox@opdev.rs";
    protected static final String DEFAULT_COMPANY_PASSWORD = "rav4life";
    protected static final String DEFAULT_COMPANY_ADDRESS_1 = "Olge Petrov, no parking though";
    protected static final String DEFAULT_COMPANY_ADDRESS_2 = "Apt. 69.";
    protected static final String DEFAULT_COMPANY_UPDATED_ADDRESS_1 = "Olge Petrov, no parking though. Sometimes, if you're lucky.";

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    protected String generateNewEmail() {
        final String username = Long.toString(System.nanoTime());
        return username + "@xxx.xxx";
    }

    protected TalentRegistrationDto createNewTalent(final String username) {
        final String talentUsername = StringUtils.hasText(username) ? username : generateNewEmail();

        return TalentRegistrationDto.builder().firstName(DEFAULT_TALENT_FIRST_NAME).lastName(DEFAULT_TALENT_LAST_NAME)
                .username(talentUsername).password(DEFAULT_TALENT_PASSWORD).passwordConfirmed(DEFAULT_TALENT_PASSWORD)
                .build();
    }

    protected CompanyRegistrationDto createNewCompany(final String username) {
        final String companyUsername = StringUtils.hasText(username) ? username : generateNewEmail();

        return CompanyRegistrationDto.builder().name(DEFAULT_COMPANY_NAME).description(DEFAULT_COMPANY_DESCRIPTION)
                .address1(DEFAULT_COMPANY_ADDRESS_1).username(companyUsername).password(DEFAULT_COMPANY_PASSWORD)
                .passwordConfirmed(DEFAULT_COMPANY_PASSWORD).build();
    }

    protected HttpHeaders createAuthHeaders(final String token) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        return headers;
    }

    protected ResponseEntity<Void> registerTalent(final TalentRegistrationDto dto) {
        return restTemplate.postForEntity(API_PREFIX + TALENTS_API, dto, Void.class);
    }

    protected ResponseEntity<LoginSuccessDto> login(final String username, final String password) {
        final LoginDto loginDto = LoginDto.builder().username(username).password(password).build();
        return restTemplate.postForEntity("/login", loginDto, LoginSuccessDto.class);
    }

    protected String talentRegisterAndLogin(final TalentRegistrationDto dto) {
        registerTalent(dto);
      final  ResponseEntity<LoginSuccessDto> loginResponse = login(dto.getUsername(), dto.getPassword());
      return loginResponse.getBody().getToken();
    }

    protected ResponseEntity<Void> disableTalent(final String token, final String username) {
        return disable(token, username, TALENTS_API);
    }

    protected ResponseEntity<Void> deleteTalent(final String token, final String username) {
        return delete(token, username, TALENTS_API);
    }

    protected ResponseEntity<Void> registerCompany(final CompanyRegistrationDto dto) {
        return restTemplate.postForEntity(API_PREFIX + COMPANIES_API, dto, Void.class);
    }

    protected ResponseEntity<Void> disableCompany(final String token, final String username) {
        return disable(token, username, COMPANIES_API);
    }

    protected ResponseEntity<Void> deleteCompany(final String token, final String username) {
        return delete(token, username, COMPANIES_API);
    }

    protected HttpHeaders getAdminHeaders() {
        ResponseEntity<LoginSuccessDto> loginResponse = login("gox69@opdev.rs", "rav4");
        assertThat(loginResponse.getBody(), is(notNullValue()));
        final String token = loginResponse.getBody().getToken();
        return createAuthHeaders(token);
    }

    private ResponseEntity<Void> delete(final String token, final String username, final String api) {
        final HttpEntity<Void> httpEntity = new HttpEntity<>(createAuthHeaders(token));

        final String endpoint = new StringBuilder().append(API_PREFIX).append(api).append(username).toString();

        return restTemplate.exchange(endpoint, HttpMethod.DELETE, httpEntity, Void.class);
    }

    private ResponseEntity<Void> disable(final String token, final String username, final String api) {
        final HttpEntity<Void> httpEntity = new HttpEntity<>(createAuthHeaders(token));
        final Map<String, String> urlParams = new HashMap<>();
        urlParams.put("disable", "true");

        final String endpoint = new StringBuilder().append(API_PREFIX).append(api).append(username)
                .append("?disable={disable}").toString();

        return restTemplate.exchange(endpoint, HttpMethod.DELETE, httpEntity, Void.class, urlParams);
    }

    @Builder
    @RequiredArgsConstructor
    @Getter
    protected static class LoginDto {
        private final String username;
        private final String password;
    }

}

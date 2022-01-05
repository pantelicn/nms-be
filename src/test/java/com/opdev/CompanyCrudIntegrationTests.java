package com.opdev;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.List;

import com.opdev.common.services.Profiles;
import com.opdev.company.dto.CompanyRegistrationDto;
import com.opdev.company.dto.CompanyUpdateDto;
import com.opdev.config.security.Roles;
import com.opdev.dto.CompanyViewDto;
import com.opdev.dto.LoginSuccessDto;
import com.opdev.dto.paging.PageDto;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(Profiles.TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Rollback
class CompanyCrudIntegrationTests extends AbstractIntegrationTest {

	@Test
	void registerLoginViewUpdateDelete() {
		final CompanyRegistrationDto newCompany = createNewCompany(DEFAULT_COMPANY_USERNAME);
		final ResponseEntity<Void> registerResponse = registerCompany(newCompany);
		assertThat(registerResponse.getStatusCode(), is(equalTo(HttpStatus.CREATED)));
		assertThat(registerResponse.getHeaders().getLocation().getRawPath(), containsString("login"));

		final ResponseEntity<LoginSuccessDto> loginResponse = login(newCompany.getUsername(), newCompany.getPassword());
		final String token = loginResponse.getBody().getToken();

		assertThat(loginResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
		assertThat(loginResponse.getBody().getUsername(), is(equalTo(newCompany.getUsername())));
		assertThat(loginResponse.getBody().getRoles(), is(equalTo(List.of(Roles.COMPANY))));

		final ResponseEntity<CompanyViewDto> viewResponse = view(newCompany.getUsername());
		assertThat(viewResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
		assertThat(viewResponse.getBody().getUser().getUsername(), is(equalTo(newCompany.getUsername())));
		assertThat(viewResponse.getBody().getName(), is(equalTo(newCompany.getName())));
		assertThat(viewResponse.getBody().getDescription(), is(equalTo(newCompany.getDescription())));

		final ResponseEntity<Void> updateResponse = update(viewResponse.getBody(), token);
		assertThat(updateResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
		final ResponseEntity<CompanyViewDto> viewUpdatedResponse = view(newCompany.getUsername());
		assertThat(viewUpdatedResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
		assertThat(viewUpdatedResponse.getBody().getName(), is(equalTo(newCompany.getName())));
		assertThat(viewUpdatedResponse.getBody().getDescription(), is(equalTo(newCompany.getDescription())));
		assertThat(viewUpdatedResponse.getBody().getAddress1(), is(equalTo(DEFAULT_COMPANY_UPDATED_ADDRESS_1)));
		assertThat(viewUpdatedResponse.getBody().getAddress2(), is(equalTo(DEFAULT_COMPANY_ADDRESS_2)));

		// TODO: should we allow the deletion of a disabled company?
		final ResponseEntity<Void> deletionResponse = deleteCompany(token, newCompany.getUsername());
		assertThat(deletionResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));

		final ResponseEntity<CompanyViewDto> viewDeletedCompany = view(newCompany.getUsername());
		assertThat(viewDeletedCompany.getStatusCode(), is(equalTo(HttpStatus.NOT_FOUND)));
	}

	@Test
	void disable() {
		final CompanyRegistrationDto newCompany = createNewCompany(null);
		registerCompany(newCompany);
		final ResponseEntity<LoginSuccessDto> loginResponse = login(newCompany.getUsername(), newCompany.getPassword());
		final String token = loginResponse.getBody().getToken();

		final ResponseEntity<Void> disablingResponse = disableCompany(token, newCompany.getUsername());
		assertThat(disablingResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));

		final ResponseEntity<CompanyViewDto> viewDisabledCompany = view(newCompany.getUsername());
		assertThat(viewDisabledCompany.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
	}

	@Test
	void tryToDisableAndDelete() {
		final CompanyRegistrationDto newCompany = createNewCompany(null);
		registerCompany(newCompany);
		final ResponseEntity<LoginSuccessDto> loginResponse = login(newCompany.getUsername(), newCompany.getPassword());
		final String token = loginResponse.getBody().getToken();

		final ResponseEntity<Void> disablingResponse = disableCompany(token, newCompany.getUsername());
		assertThat(disablingResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
		final ResponseEntity<Void> deletionResponse = deleteCompany(token, newCompany.getUsername());
		assertThat(deletionResponse.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
	}

	@Test
	void disableAndDeleteAdmin() {
		final CompanyRegistrationDto newCompany = createNewCompany(null);
		registerCompany(newCompany);
		final ResponseEntity<LoginSuccessDto> loginResponse = login(ADMIN_NIKOLA, ADMIN_NIKOLA_PASSWORD);
		final String adminToken = loginResponse.getBody().getToken();

		final ResponseEntity<Void> disablingResponse = disableCompany(adminToken, newCompany.getUsername());
		assertThat(disablingResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
		final ResponseEntity<Void> deletionResponse = deleteCompany(adminToken, newCompany.getUsername());
		// an admin can always delete an entity, despite it being disabled
		assertThat(deletionResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
	}

	@Test
	void tryLoginAfterDisabled() {
		final CompanyRegistrationDto newCompany = createNewCompany(null);
		registerCompany(newCompany);
		final ResponseEntity<LoginSuccessDto> loginResponse = login(newCompany.getUsername(), newCompany.getPassword());
		final String token = loginResponse.getBody().getToken();

		final ResponseEntity<Void> disablingResponse = disableCompany(token, newCompany.getUsername());
		assertThat(disablingResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));

		final LoginDto loginDto = LoginDto.builder().username(newCompany.getUsername())
				.password(newCompany.getPassword()).build();
		final ResponseEntity<?> loginDisabledResponse = restTemplate.postForEntity("/login", loginDto, Object.class);
		assertThat(loginDisabledResponse.getStatusCode(), is(equalTo(HttpStatus.UNAUTHORIZED)));
	}

	private ResponseEntity<CompanyViewDto> view(final String username) {
		return restTemplate.getForEntity("/v1/companies/" + username, CompanyViewDto.class);
	}

	private ResponseEntity<Void> update(final CompanyViewDto company, final String token) {
		final CompanyUpdateDto updateDto = CompanyUpdateDto.builder().name(company.getName())
				.description(company.getDescription()).address1(DEFAULT_COMPANY_UPDATED_ADDRESS_1)
				.address2(DEFAULT_COMPANY_ADDRESS_2).build();

		final HttpEntity<CompanyUpdateDto> httpEntity = new HttpEntity<>(updateDto, createAuthHeaders(token));
		return restTemplate.exchange("/v1/companies/" + company.getUser().getUsername(), HttpMethod.PUT, httpEntity,
				Void.class);
	}

	private ResponseEntity<PageDto<CompanyViewDto>> viewMultiple() {
		return restTemplate.exchange("/v1/companies/", HttpMethod.GET,
				HttpEntity.EMPTY, ParameterizedTypeReference.forType(PageDto.class));
	}

}

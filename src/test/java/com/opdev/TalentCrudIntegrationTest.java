package com.opdev;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import com.opdev.authentication.dto.TalentBasicInfoUpdateDto;
import com.opdev.authentication.dto.TalentViewDto;
import com.opdev.common.services.Profiles;
import com.opdev.dto.LoginSuccessDto;
import com.opdev.dto.TalentRegistrationDto;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(Profiles.TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Rollback
class TalentCrudIntegrationTest extends AbstractIntegrationTest {

	@Test
	void register() {
		final ResponseEntity<Void> response = registerTalent(createNewTalent(null));

		assertThat(response.getStatusCode(), is(equalTo(HttpStatus.CREATED)));
		assertThat(response.getHeaders().getLocation().getRawPath(), containsString("login"));
	}

	@Test
	void view() {
		final TalentRegistrationDto newTalent = createNewTalent(null);
		final String token = talentRegisterAndLogin(newTalent);

		final ResponseEntity<TalentViewDto> viewResponse = view(newTalent.getUsername(), token);
		assertThat(viewResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
		assertThat(viewResponse.getBody().getUser().getUsername(), is(equalTo(newTalent.getUsername())));
		assertThat(viewResponse.getBody().getFirstName(), is(equalTo(newTalent.getFirstName())));
		assertThat(viewResponse.getBody().getLastName(), is(equalTo(newTalent.getLastName())));
	}

	@Test
	void update() {
		final TalentRegistrationDto newTalent = createNewTalent(null);
		final String token = talentRegisterAndLogin(newTalent);

		final ResponseEntity<TalentViewDto> viewResponse = view(newTalent.getUsername(), token);
		assertThat(viewResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
		assertThat(viewResponse.getBody().getUser().getUsername(), is(equalTo(newTalent.getUsername())));
		assertThat(viewResponse.getBody().getFirstName(), is(equalTo(newTalent.getFirstName())));
		assertThat(viewResponse.getBody().getLastName(), is(equalTo(newTalent.getLastName())));

		final String newFirstName = "New First Name";
		final String newLastName = "New Last Name";
		final TalentBasicInfoUpdateDto modifiedPositionDto = new TalentBasicInfoUpdateDto(newFirstName, newLastName);
		final HttpEntity<TalentBasicInfoUpdateDto> httpEntityPUT = new HttpEntity<>(modifiedPositionDto,
				createAuthHeaders((token)));

		final ResponseEntity<TalentViewDto> editResponse = restTemplate.exchange(
				API_PREFIX + TALENTS_API + newTalent.getUsername(), HttpMethod.PUT, httpEntityPUT, TalentViewDto.class);
		assertThat(editResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
		assertThat(editResponse.getBody().getUser().getUsername(), is(equalTo(newTalent.getUsername())));
		assertThat(editResponse.getBody().getFirstName(), is(equalTo(newFirstName)));
		assertThat(editResponse.getBody().getLastName(), is(equalTo(newLastName)));
	}

	@Test
	void disable() {
		final TalentRegistrationDto newTalent = createNewTalent(null);
		registerTalent(newTalent);
		final ResponseEntity<LoginSuccessDto> loginResponse = login(newTalent.getUsername(), newTalent.getPassword());
		final String token = loginResponse.getBody().getToken();

		final ResponseEntity<Void> disablingResponse = disableTalent(token, newTalent.getUsername());
		assertThat(disablingResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));

		final ResponseEntity<TalentViewDto> viewDisabledTalentResponse = view(newTalent.getUsername(), token);
		assertThat(viewDisabledTalentResponse.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
	}

	@Test
	void tryLoginAfterDisabled() {
		final TalentRegistrationDto newTalent = createNewTalent(null);
		registerTalent(newTalent);
		final ResponseEntity<LoginSuccessDto> loginResponse = login(newTalent.getUsername(), newTalent.getPassword());
		final String token = loginResponse.getBody().getToken();

		final ResponseEntity<Void> disablingResponse = disableTalent(token, newTalent.getUsername());
		assertThat(disablingResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));

		final LoginDto loginDto = LoginDto.builder().username(newTalent.getUsername()).password(newTalent.getPassword())
				.build();
		final ResponseEntity<?> loginDisabledResponse = restTemplate.postForEntity("/login", loginDto, Object.class);
		assertThat(loginDisabledResponse.getStatusCode(), is(equalTo(HttpStatus.UNAUTHORIZED)));
	}

	@Test
	void tryToDisableAndDelete() {
		final TalentRegistrationDto newTalent = createNewTalent(null);
		registerTalent(newTalent);
		final ResponseEntity<LoginSuccessDto> loginResponse = login(newTalent.getUsername(), newTalent.getPassword());
		final String token = loginResponse.getBody().getToken();

		final ResponseEntity<Void> disablingResponse = disableTalent(token, newTalent.getUsername());
		assertThat(disablingResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
		final ResponseEntity<Void> deletionResponse = deleteTalent(token, newTalent.getUsername());
		assertThat(deletionResponse.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
	}

	@Test
	void disableAndDeleteAdmin() {
		final TalentRegistrationDto newTalent = createNewTalent(null);
		registerTalent(newTalent);
		final ResponseEntity<LoginSuccessDto> loginResponse = login(ADMIN_NIKOLA, ADMIN_NIKOLA_PASSWORD);
		final String adminToken = loginResponse.getBody().getToken();

		final ResponseEntity<Void> disablingResponse = disableTalent(adminToken, newTalent.getUsername());
		assertThat(disablingResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
		final ResponseEntity<Void> deletionResponse = deleteTalent(adminToken, newTalent.getUsername());
		// an admin can always delete an entity, despite it being disabled
		assertThat(deletionResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
	}

	private ResponseEntity<TalentViewDto> view(final String username, final String token) {
		final HttpEntity<Void> httpEntity = new HttpEntity<>(createAuthHeaders(token));
		return restTemplate.exchange(API_PREFIX + TALENTS_API + username, HttpMethod.GET, httpEntity,
				TalentViewDto.class);
	}
}

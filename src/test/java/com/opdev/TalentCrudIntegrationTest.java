package com.opdev;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.Optional;

import com.opdev.common.services.Profiles;
import com.opdev.model.talent.Talent;
import com.opdev.repository.TalentRepository;
import com.opdev.talent.dto.TalentBasicInfoUpdateDto;
import com.opdev.talent.dto.TalentViewDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(Profiles.TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TalentCrudIntegrationTest extends AbstractIntegrationTest {

	private Talent goran;

	@Autowired
	private TalentRepository talentRepository;

	@BeforeEach
	public void init() {
		Optional<Talent> found = talentRepository.findByUserUsername(TALENT_GORAN);
		goran = found.orElseGet(() -> createTalent(TALENT_GORAN));
	}

	@Test
	@DirtiesContext
	void view() {
		final String token = getTokenForTalentGoran();

		final ResponseEntity<TalentViewDto> viewResponse = view(TALENT_GORAN, token);
		assertThat(viewResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
		assertThat(viewResponse.getBody().getUser().getUsername(), is(equalTo(TALENT_GORAN)));
		assertThat(viewResponse.getBody().getFirstName(), is(equalTo(goran.getFirstName())));
		assertThat(viewResponse.getBody().getLastName(), is(equalTo(goran.getLastName())));
	}

	@Test
	@DirtiesContext
	void update() {
		final String token = getTokenForTalentGoran();

		final ResponseEntity<TalentViewDto> viewResponse = view(TALENT_GORAN, token);
		assertThat(viewResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
		assertThat(viewResponse.getBody().getUser().getUsername(), is(equalTo(TALENT_GORAN)));
		assertThat(viewResponse.getBody().getFirstName(), is(equalTo(goran.getFirstName())));
		assertThat(viewResponse.getBody().getLastName(), is(equalTo(goran.getLastName())));

		final String newFirstName = "New First Name";
		final String newLastName = "New Last Name";
		final TalentBasicInfoUpdateDto modifiedPositionDto = new TalentBasicInfoUpdateDto(newFirstName, newLastName, 0);
		final HttpEntity<TalentBasicInfoUpdateDto> httpEntityPUT = new HttpEntity<>(modifiedPositionDto,
				createAuthHeaders((token)));

		final ResponseEntity<TalentViewDto> editResponse = restTemplate.exchange(
				API_PREFIX + TALENTS_API + TALENT_GORAN, HttpMethod.PUT, httpEntityPUT, TalentViewDto.class);
		assertThat(editResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
		assertThat(editResponse.getBody().getUser().getUsername(), is(equalTo(TALENT_GORAN)));
		assertThat(editResponse.getBody().getFirstName(), is(equalTo(newFirstName)));
		assertThat(editResponse.getBody().getLastName(), is(equalTo(newLastName)));
	}

	@Test
	@DirtiesContext
	void disable() {
		final String token = getTokenForTalentGoran();

		final ResponseEntity<Void> disablingResponse = disableTalent(token, TALENT_GORAN);
		assertThat(disablingResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));

		final ResponseEntity<TalentViewDto> viewDisabledTalentResponse = view(TALENT_GORAN, token);
		assertThat(viewDisabledTalentResponse.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
	}

	private ResponseEntity<TalentViewDto> view(final String username, final String token) {
		final HttpEntity<Void> httpEntity = new HttpEntity<>(createAuthHeaders(token));
		return restTemplate.exchange(API_PREFIX + TALENTS_API + username, HttpMethod.GET, httpEntity,
				TalentViewDto.class);
	}
}

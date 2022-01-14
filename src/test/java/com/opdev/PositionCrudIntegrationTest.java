package com.opdev;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;

import com.opdev.common.services.Profiles;
import com.opdev.position.dto.PositionCreateDto;
import com.opdev.position.dto.PositionUpdateDto;
import com.opdev.position.dto.PositionViewDto;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(Profiles.TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PositionCrudIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DirtiesContext
    public void addUpdateGetDeleteTest() {
        createAdmin();
        final String token = getTokenForAdmin();
        final PositionCreateDto newPositionDto = new PositionCreateDto("Backend", "Backend developer", "BACKEND_DEV");
        final HttpHeaders headers = createAuthHeaders(token);
        final HttpEntity<PositionCreateDto> httpEntityPOST = new HttpEntity<>(newPositionDto, headers);
        final ResponseEntity<PositionViewDto> addResponse = restTemplate.exchange("/v1/positions", HttpMethod.POST,
                httpEntityPOST, PositionViewDto.class);

        assertThat(addResponse.getStatusCode(), is(equalTo(HttpStatus.CREATED)));
        assertThat(addResponse.getBody(), is(notNullValue()));
        assertThat(addResponse.getBody().getCode(), is(equalTo(newPositionDto.getCode())));
        assertThat(addResponse.getBody().getName(), is(equalTo(newPositionDto.getName())));
        assertThat(addResponse.getBody().getDescription(), is(equalTo(newPositionDto.getDescription())));

        ResponseEntity<List<PositionViewDto>> findAllResponse = restTemplate.exchange("/v1/positions", HttpMethod.GET,
                HttpEntity.EMPTY, new ParameterizedTypeReference<List<PositionViewDto>>() {
                });

        assertThat(findAllResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(findAllResponse.getBody(), is(notNullValue()));
        assertThat(findAllResponse.getBody().size(), is(1));

        final ResponseEntity<PositionViewDto> getResponse = restTemplate.exchange(
                "/v1/positions/" + newPositionDto.getCode(), HttpMethod.GET, HttpEntity.EMPTY, PositionViewDto.class);

        assertThat(getResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(getResponse.getBody(), is(notNullValue()));
        assertThat(getResponse.getBody().getCode(), is(equalTo(newPositionDto.getCode())));
        assertThat(getResponse.getBody().getName(), is(equalTo(newPositionDto.getName())));
        assertThat(getResponse.getBody().getDescription(), is(equalTo(newPositionDto.getDescription())));

        final PositionUpdateDto modifiedPositionDto = new PositionUpdateDto(getResponse.getBody().getId(),
                getResponse.getBody().getName(), "Backend developer of IT", getResponse.getBody().getCode());
        final HttpEntity<PositionUpdateDto> httpEntityPUT = new HttpEntity<>(modifiedPositionDto, headers);

        final ResponseEntity<PositionViewDto> editResponse = restTemplate.exchange("/v1/positions", HttpMethod.PUT,
                httpEntityPUT, PositionViewDto.class);

        assertThat(editResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(editResponse.getBody(), is(notNullValue()));
        assertThat(editResponse.getBody().getCode(), is(equalTo(modifiedPositionDto.getCode())));
        assertThat(editResponse.getBody().getName(), is(equalTo(modifiedPositionDto.getName())));
        assertThat(editResponse.getBody().getDescription(), is(equalTo(modifiedPositionDto.getDescription())));

        final HttpEntity<Object> httpEntityDELETE = new HttpEntity<>(headers);

        final ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/v1/positions/" + editResponse.getBody().getCode(), HttpMethod.DELETE, httpEntityDELETE, Void.class);

        assertThat(deleteResponse.getStatusCode(), is(equalTo(HttpStatus.NO_CONTENT)));

        findAllResponse = restTemplate.exchange("/v1/positions", HttpMethod.GET, HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<PositionViewDto>>() {
                });

        assertThat(findAllResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(findAllResponse.getBody(), is(notNullValue()));
        assertThat(findAllResponse.getBody().size(), is(0));

    }

}

package com.opdev;

import com.opdev.benefit.dto.BenefitAddDto;
import com.opdev.benefit.dto.BenefitEditDto;
import com.opdev.benefit.dto.BenefitViewDto;
import com.opdev.common.services.Profiles;
import com.opdev.contact.dto.ContactViewDto;

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

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@ActiveProfiles(Profiles.TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BenefitCrudIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DirtiesContext
    void addUpdateGetDeleteTest() {
        final String token = getTokenForCompanyGoogle();
        createCompany(COMPANY_GOOGLE);
        final BenefitAddDto newBenefitDto = new BenefitAddDto("Insurance", "Health insurance", false, "google@gmail.com");
        final HttpHeaders headers = createAuthHeaders(token);
        final HttpEntity<BenefitAddDto> httpEntityPOST = new HttpEntity<>(newBenefitDto, headers);
        final ResponseEntity<BenefitViewDto> addResponse = restTemplate.exchange("/v1/companies/" + COMPANY_GOOGLE + "/benefits", HttpMethod.POST,
                httpEntityPOST, BenefitViewDto.class);

        assertThat(addResponse.getStatusCode(), is(equalTo(HttpStatus.CREATED)));
        assertThat(addResponse.getBody(), is(notNullValue()));
        assertThat(addResponse.getBody().getName(), is(equalTo(newBenefitDto.getName())));
        assertThat(addResponse.getBody().getDescription(), is(equalTo(newBenefitDto.getDescription())));
        assertThat(addResponse.getBody().getIsDefault(), is(equalTo(newBenefitDto.getIsDefault())));
        final Long benefitId = addResponse.getBody().getId();

        final HttpEntity<Void> httpEntityGET = new HttpEntity<>(headers);
        final ResponseEntity<BenefitViewDto> getResponse = restTemplate.exchange("/v1/companies/" + COMPANY_GOOGLE + "/benefits/" + benefitId,
                HttpMethod.GET, httpEntityGET, BenefitViewDto.class);

        assertThat(getResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(getResponse.getBody(), is(notNullValue()));
        assertThat(getResponse.getBody().getName(), is(equalTo(newBenefitDto.getName())));
        assertThat(getResponse.getBody().getDescription(), is(equalTo(newBenefitDto.getDescription())));
        assertThat(getResponse.getBody().getIsDefault(), is(equalTo(newBenefitDto.getIsDefault())));

        ResponseEntity<List<BenefitViewDto>> findAllResponse = restTemplate.exchange("/v1/companies/" + COMPANY_GOOGLE + "/benefits", HttpMethod.GET,
                httpEntityGET, ParameterizedTypeReference.forType(List.class));
        assertThat(findAllResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(findAllResponse.getBody(), is(notNullValue()));
        assertThat(findAllResponse.getBody().size(), is(1));

        final BenefitEditDto modifiedBenefitDto = new BenefitEditDto(benefitId, "Health insurance", "Insurance", true);
        final HttpEntity<List<BenefitEditDto>> httpEntityPUT = new HttpEntity<>(List.of(modifiedBenefitDto), headers);
        final ResponseEntity<List<BenefitViewDto>> updateResponse = restTemplate.exchange("/v1/companies/" + COMPANY_GOOGLE + "/benefits", HttpMethod.PUT,
                httpEntityPUT, new ParameterizedTypeReference<List<BenefitViewDto>>() {});

        assertThat(updateResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(updateResponse.getBody(), is(notNullValue()));
        assertThat(updateResponse.getBody().get(0).getName(), is(equalTo(modifiedBenefitDto.getName())));
        assertThat(updateResponse.getBody().get(0).getDescription(), is(equalTo(modifiedBenefitDto.getDescription())));
        assertThat(updateResponse.getBody().get(0).getIsDefault(), is(equalTo(modifiedBenefitDto.getIsDefault())));

        final HttpEntity<Void> httpEntityDELETE = new HttpEntity<>(headers);
        final ResponseEntity<Void> deleteResponse = restTemplate.exchange("/v1/companies/" + COMPANY_GOOGLE + "/benefits/" + updateResponse.getBody().get(0).getId(),
                HttpMethod.DELETE, httpEntityDELETE, Void.class);

        assertThat(deleteResponse.getStatusCode(), is(equalTo(HttpStatus.NO_CONTENT)));

        findAllResponse = restTemplate.exchange("/v1/companies/" + COMPANY_GOOGLE + "/benefits", HttpMethod.GET,
                httpEntityGET, ParameterizedTypeReference.forType(List.class));
        assertThat(findAllResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(findAllResponse.getBody(), is(notNullValue()));
        assertThat(findAllResponse.getBody().size(), is(0));
    }

}

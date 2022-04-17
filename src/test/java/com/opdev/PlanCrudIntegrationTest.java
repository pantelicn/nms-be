package com.opdev;

import com.opdev.common.dto.MoneyDto;
import com.opdev.common.services.Profiles;
import com.opdev.model.subscription.PlanType;
import com.opdev.offers.plan.dto.PlanDto;
import com.opdev.offers.plan.dto.PlanEditDto;
import com.opdev.offers.plan.dto.PlanViewDto;
import org.junit.jupiter.api.BeforeEach;
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

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@ActiveProfiles(Profiles.TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PlanCrudIntegrationTest extends AbstractIntegrationTest {

    private final PlanDto planDto;
    private final HttpHeaders adminHeaders;

    public PlanCrudIntegrationTest() {
        planDto = new PlanDto(
                "Basic",
                PlanType.BASIC,
                "This is a basic plan",
                1,
                new MoneyDto("USD", BigDecimal.valueOf(200.00)));
        adminHeaders = createAuthHeaders(getTokenForAdmin());
    }

    @BeforeEach
    public void init() {
        createAdmin();
    }

    @Test
    @DirtiesContext
    public void createPlanTest() {
        final ResponseEntity<PlanViewDto> createResponse = createPlan();

        assertThat(createResponse.getStatusCode(), is(equalTo(HttpStatus.CREATED)));
        assertThat(createResponse.getBody(), is(notNullValue()));
        assertThat(createResponse.getBody().getName(), is(equalTo(planDto.getName())));
        assertThat(createResponse.getBody().getPrice().getAmount().floatValue(), is(equalTo(planDto.getPrice().getAmount().floatValue())));
        assertThat(createResponse.getBody().getPrice().getCurrencyCode(), is(equalTo(planDto.getPrice().getCurrencyCode())));
        assertThat(createResponse.getBody().getDurationInMonths(), is(equalTo(planDto.getDurationInMonths())));
        assertThat(createResponse.getBody().getDescription(), is(equalTo(planDto.getDescription())));
    }


    @Test
    @DirtiesContext
    public void findAllPlansTest() {
        createPlan();

        final HttpEntity<Void> httpEntityGET = new HttpEntity<>(adminHeaders);
        ResponseEntity<List<PlanViewDto>> response = restTemplate
                .exchange("/v1/plans/",
                        HttpMethod.GET,
                        httpEntityGET,
                        ParameterizedTypeReference.forType(List.class));

        assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().size(), is(equalTo(1)));
    }

    @Test
    @DirtiesContext
    public void getPlanTest() {
        final ResponseEntity<PlanViewDto> createResponse = createPlan();
        final Long planId = createResponse.getBody().getId();

        final HttpEntity<Void> httpEntityGET = new HttpEntity<>(adminHeaders);
        final ResponseEntity<PlanViewDto> getResponse = restTemplate
                .exchange("/v1/plans/" + planId, HttpMethod.GET, httpEntityGET, PlanViewDto.class);

        assertThat(getResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(getResponse.getBody(), is(notNullValue()));
        assertThat(getResponse.getBody().getName(), is(equalTo(planDto.getName())));
        assertThat(getResponse.getBody().getType(), is(equalTo(planDto.getType())));
        assertThat(getResponse.getBody().getPrice().getCurrencyCode(), is(equalTo(planDto.getPrice().getCurrencyCode())));
        assertThat(getResponse.getBody().getDurationInMonths(), is(equalTo(planDto.getDurationInMonths())));
        assertThat(getResponse.getBody().getPrice().getAmount().floatValue(), is(equalTo(planDto.getPrice().getAmount().floatValue())));
    }

    @Test
    @DirtiesContext
    public void updatePlanTest() {
        final ResponseEntity<PlanViewDto> createResponse = createPlan();
        final PlanEditDto planEdit = new PlanEditDto(
                createResponse.getBody().getId(),
                "Pro",
                PlanType.PRO,
                1,
                "This is a pro plan",
                new MoneyDto("USD", BigDecimal.valueOf(350.00)));

        final HttpEntity<PlanEditDto> httpEntityPUT = new HttpEntity<>(planEdit, adminHeaders);
        final ResponseEntity<PlanViewDto> putResponse = restTemplate
                .exchange("/v1/plans", HttpMethod.PUT, httpEntityPUT, PlanViewDto.class);

        assertThat(putResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(putResponse.getBody(), is(notNullValue()));
        assertThat(putResponse.getBody().getName(), is(equalTo(planEdit.getName())));
        assertThat(putResponse.getBody().getType(), is(equalTo(planEdit.getType())));
        assertThat(putResponse.getBody().getPrice().getCurrencyCode(), is(equalTo(planEdit.getPrice().getCurrencyCode())));
        assertThat(putResponse.getBody().getDurationInMonths(), is(equalTo(planEdit.getDurationInMonths())));
        assertThat(putResponse.getBody().getPrice().getAmount().floatValue(), is(equalTo(planEdit.getPrice().getAmount().floatValue())));
    }

    @Test
    @DirtiesContext
    public void deletePlanTest() {
        final ResponseEntity<PlanViewDto> createResponse = createPlan();
        final Long planId = createResponse.getBody().getId();

        final HttpEntity<Void> httpEntityDELETE = new HttpEntity<>(adminHeaders);
        final ResponseEntity<Void> deleteResponse = restTemplate
                .exchange("/v1/plans/" + planId, HttpMethod.DELETE, httpEntityDELETE, Void.class);

        assertThat(deleteResponse.getStatusCode(), is(equalTo(HttpStatus.NO_CONTENT)));

        final HttpEntity<Void> httpEntityGET = new HttpEntity<>(adminHeaders);
        final ResponseEntity<PlanViewDto> getResponse = restTemplate
                .exchange("/v1/plans/" + planId, HttpMethod.GET, httpEntityGET, PlanViewDto.class);

        assertThat(getResponse.getStatusCode(), is(equalTo(HttpStatus.NOT_FOUND)));
    }


    private ResponseEntity<PlanViewDto> createPlan() {
        final HttpEntity<PlanDto> httpEntityPOST = new HttpEntity<>(planDto, adminHeaders);
        final ResponseEntity<PlanViewDto> createResponse = restTemplate
                .exchange("/v1/plans", HttpMethod.POST, httpEntityPOST, PlanViewDto.class);
        return createResponse;
    }

}

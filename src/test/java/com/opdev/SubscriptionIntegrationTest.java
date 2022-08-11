package com.opdev;

import com.opdev.common.dto.MoneyDto;
import com.opdev.common.services.Profiles;
import com.opdev.model.subscription.PlanType;
import com.opdev.offers.plan.dto.PlanDto;
import com.opdev.offers.plan.dto.PlanViewDto;
import com.opdev.offers.planproduct.dto.PlanProductDto;
import com.opdev.offers.planproduct.dto.PlanProductViewDto;
import com.opdev.offers.product.dto.ProductDto;
import com.opdev.offers.product.dto.ProductViewDto;
import com.opdev.subscription.dto.SubscriptionAddDto;
import com.opdev.subscription.dto.SubscriptionViewDto;
import com.opdev.subscription.usage.dto.ProductUsageViewDto;
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
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@ActiveProfiles(Profiles.TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SubscriptionIntegrationTest extends AbstractIntegrationTest {

    private PlanProductViewDto planProductViewDto;

    @BeforeEach
    public void init() {
        createAdmin();
        HttpHeaders adminHeaders = createAuthHeaders(getTokenForAdmin());

        final ResponseEntity<PlanViewDto> createPlanResponse = createPlan(adminHeaders);
        final ResponseEntity<ProductViewDto> createProductResponse = createProduct(adminHeaders);
        final ResponseEntity<PlanProductViewDto> planProductResponse =
                createPlanProduct(adminHeaders,
                        createPlanResponse.getBody().getId(),
                        createProductResponse.getBody().getId());

        planProductViewDto = planProductResponse.getBody();
    }

    @Test
    @DirtiesContext
    void subscriptionTest() {
        createCompany(COMPANY_GOOGLE);
        final HttpHeaders companyHeaders = createAuthHeaders(getTokenForCompanyGoogle());

        final SubscriptionAddDto subscriptionAddDto = new SubscriptionAddDto(planProductViewDto.getPlan().getId(), false);
        final HttpEntity<SubscriptionAddDto> httpEntityPOST = new HttpEntity<>(subscriptionAddDto, companyHeaders);

        final ResponseEntity<SubscriptionViewDto> successfulResponse =
                restTemplate.exchange("/v1/subscriptions/" + COMPANY_GOOGLE, HttpMethod.POST, httpEntityPOST, SubscriptionViewDto.class);

        assertThat(successfulResponse.getStatusCode(), is(equalTo(HttpStatus.CREATED)));
        assertThat(successfulResponse.getBody(), is(notNullValue()));

        SubscriptionViewDto successfulResponseBody = successfulResponse.getBody();

        assertThat(successfulResponseBody.getStartDate(), is(equalTo(LocalDate.now())));
        assertThat(successfulResponseBody.getEndDate(), is(equalTo(LocalDate.now().plusMonths(1))));
        assertThat(successfulResponseBody.getAutoRenewal(), is(equalTo(subscriptionAddDto.getAutoRenewal())));
        assertThat(successfulResponseBody.getPlan().getId(), is(equalTo(subscriptionAddDto.getPlanId())));

        final HttpEntity<Void> httpEntityGET = new HttpEntity<>(companyHeaders);

        ResponseEntity<List<ProductUsageViewDto>> companyUsagesResponse = restTemplate
                .exchange("/v1/product-usages/company/" + COMPANY_GOOGLE,
                        HttpMethod.GET,
                        httpEntityGET,
                        new ParameterizedTypeReference<>() {
                        });

        assertThat(companyUsagesResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(companyUsagesResponse.getBody(), is(notNullValue()));

        ProductUsageViewDto companyUsage = companyUsagesResponse.getBody().stream().findFirst().get();

        assertThat(companyUsage.getRemaining(), is(equalTo(planProductViewDto.getQuantity())));
        assertThat(companyUsage.getLimited(), is(equalTo(planProductViewDto.getLimited())));
        assertThat(companyUsage.getStartDate(), is(equalTo(successfulResponseBody.getStartDate())));
        assertThat(companyUsage.getEndDate(), is(equalTo(successfulResponseBody.getEndDate())));
        assertThat(companyUsage.getPeriod(), is(equalTo(Period.between(successfulResponseBody.getStartDate(),
                successfulResponseBody.getEndDate()))));
        assertThat(companyUsage.getProduct().getId(), is(equalTo(planProductViewDto.getProduct().getId())));

        final ResponseEntity<SubscriptionViewDto> unsuccessfulResponse =
                restTemplate.exchange("/v1/subscriptions/" + COMPANY_GOOGLE, HttpMethod.POST, httpEntityPOST, SubscriptionViewDto.class);

        assertThat(unsuccessfulResponse.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));

    }

    private ResponseEntity<PlanProductViewDto> createPlanProduct(HttpHeaders adminHeaders, Long planId, Long productId) {

        PlanProductDto validPlanProductDto = new PlanProductDto(
                5,
                true,
                planId,
                productId);

        final HttpEntity<PlanProductDto> httpEntityPOST =
                new HttpEntity<>(validPlanProductDto, adminHeaders);
        final ResponseEntity<PlanProductViewDto> createPlanProductResponse =
                restTemplate.exchange("/v1/plans/products", HttpMethod.POST, httpEntityPOST, PlanProductViewDto.class);

        return createPlanProductResponse;
    }

    private ResponseEntity<PlanViewDto> createPlan(HttpHeaders adminHeaders) {
        final PlanDto planDto = new PlanDto(
                "Basic",
                PlanType.BASIC,
                "This is a basic plan",
                1,
                new MoneyDto("USD", BigDecimal.valueOf(200.00)));

        final HttpEntity<PlanDto> httpEntityPOST = new HttpEntity<>(planDto, adminHeaders);
        final ResponseEntity<PlanViewDto> createResponse = restTemplate
                .exchange("/v1/plans", HttpMethod.POST, httpEntityPOST, PlanViewDto.class);
        return createResponse;
    }

    private ResponseEntity<ProductViewDto> createProduct(HttpHeaders adminHeaders) {
        final ProductDto productDto = new ProductDto(
                "Search templates",
                "Search templates");

        final HttpEntity<ProductDto> httpEntityPOST = new HttpEntity<>(productDto, adminHeaders);
        final ResponseEntity<ProductViewDto> createResponse = restTemplate
                .exchange("/v1/products", HttpMethod.POST, httpEntityPOST, ProductViewDto.class);

        return createResponse;
    }

}

package com.opdev;

import com.opdev.common.dto.MoneyDto;
import com.opdev.common.services.Profiles;
import com.opdev.model.subscription.PlanType;
import com.opdev.offers.plan.dto.PlanDto;
import com.opdev.offers.plan.dto.PlanViewDto;
import com.opdev.offers.planproduct.dto.PlanProductDto;
import com.opdev.offers.planproduct.dto.PlanProductEditDto;
import com.opdev.offers.planproduct.dto.PlanProductViewDto;
import com.opdev.offers.product.dto.ProductDto;
import com.opdev.offers.product.dto.ProductViewDto;
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
public class PlanProductCrudIntegrationTest extends AbstractIntegrationTest {

    private final HttpHeaders adminHeaders;
    private PlanProductDto validPlanProductDto;
    private PlanProductDto invalidPlanProductDto;

    public PlanProductCrudIntegrationTest() {
        adminHeaders = createAuthHeaders(getTokenForAdmin());
    }

    @BeforeEach
    public void init() {
        createAdmin();
        final ResponseEntity<PlanViewDto> createPlanResponse = createFirstPlan();
        final ResponseEntity<ProductViewDto> createProductResponse = createFirstProduct();
        validPlanProductDto = new PlanProductDto(
                5,
                true,
                createPlanResponse.getBody().getId(),
                createProductResponse.getBody().getId());

        invalidPlanProductDto = new PlanProductDto(
                5,
                false,
                createPlanResponse.getBody().getId(),
                createProductResponse.getBody().getId());
    }

    @Test
    @DirtiesContext
    public void savePlanProductSuccessfulTest() {
        final ResponseEntity<PlanProductViewDto> createResponse = createValidPlanProduct();

        assertThat(createResponse.getStatusCode(), is(equalTo(HttpStatus.CREATED)));
        assertThat(createResponse.getBody(), is(notNullValue()));
        assertThat(createResponse.getBody().getLimited(), is(equalTo(validPlanProductDto.getLimited())));
        assertThat(createResponse.getBody().getQuantity(), is(equalTo(validPlanProductDto.getQuantity())));
        assertThat(createResponse.getBody().getProduct().getId(), is(equalTo(validPlanProductDto.getProductId())));
        assertThat(createResponse.getBody().getPlan().getId(), is(equalTo(validPlanProductDto.getPlanId())));
    }

    @Test
    @DirtiesContext
    public void savePlanProductUnsuccessfulTest() {
        final ResponseEntity<PlanProductViewDto> createResponse = createInvalidPlanProduct();

        assertThat(createResponse.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
    }

    @Test
    @DirtiesContext
    public void findAllPlanProductTest() {
        createValidPlanProduct();

        final HttpEntity<Void> httpEntityGet = new HttpEntity<>(adminHeaders);
        ResponseEntity<List<PlanProductViewDto>> getResponse = restTemplate
                .exchange("/v1/plans/products", HttpMethod.GET, httpEntityGet, ParameterizedTypeReference.forType(List.class));

        assertThat(getResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(getResponse.getBody(), is(notNullValue()));
        assertThat(getResponse.getBody().size(), is(equalTo(1)));
    }

    @Test
    @DirtiesContext
    public void getByIdTest() {
        final ResponseEntity<PlanProductViewDto> createResponse = createValidPlanProduct();

        final HttpEntity<Void> httpEntityGet = new HttpEntity<>(adminHeaders);
        ResponseEntity<PlanProductViewDto> getResponse = restTemplate
                .exchange("/v1/plans/products/" + createResponse.getBody().getId(),
                        HttpMethod.GET,
                        httpEntityGet,
                        PlanProductViewDto.class);

        assertThat(getResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(getResponse.getBody(), is(notNullValue()));
        assertThat(getResponse.getBody().getLimited(), is(equalTo(validPlanProductDto.getLimited())));
        assertThat(getResponse.getBody().getQuantity(), is(equalTo(validPlanProductDto.getQuantity())));
        assertThat(getResponse.getBody().getProduct().getId(), is(equalTo(validPlanProductDto.getProductId())));
        assertThat(getResponse.getBody().getPlan().getId(), is(equalTo(validPlanProductDto.getPlanId())));

    }

    @Test
    @DirtiesContext
    public void findAllByPlanIdTest() {
        final ResponseEntity<PlanProductViewDto> createResponse = createValidPlanProduct();
        final HttpEntity<Void> httpEntityGet = new HttpEntity<>(adminHeaders);

        final Long planId = createResponse.getBody().getPlan().getId();

        ResponseEntity<List<PlanProductViewDto>> getResponse = restTemplate
                .exchange("/v1/plans/" + planId + "/products/",
                        HttpMethod.GET,
                        httpEntityGet,
                        ParameterizedTypeReference.forType(List.class));

        assertThat(getResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(getResponse.getBody(), is(notNullValue()));
        assertThat(getResponse.getBody().size(), is(equalTo(1)));
    }

    @Test
    @DirtiesContext
    public void updatePlanProductSuccessfulTest() {
        final ResponseEntity<PlanProductViewDto> createPlanProductResponse = createValidPlanProduct();
        PlanProductViewDto responseBody = createPlanProductResponse.getBody();

        final ResponseEntity<PlanViewDto> createPlanResponse = createSecondPlan();
        final ResponseEntity<ProductViewDto> createProductResponse = createSecondProduct();

        final PlanProductEditDto planProductEdit = new PlanProductEditDto(
                responseBody.getId(),
                10,
                true,
                createPlanResponse.getBody().getId(),
                createProductResponse.getBody().getId()
        );

        final HttpEntity<PlanProductEditDto> httpEntityPut = new HttpEntity<>(planProductEdit, adminHeaders);
        final ResponseEntity<PlanProductViewDto> putResponse = restTemplate
                .exchange("/v1/plans/products", HttpMethod.PUT, httpEntityPut, PlanProductViewDto.class);

        assertThat(putResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(putResponse.getBody(), is(notNullValue()));
        assertThat(putResponse.getBody().getQuantity(), is(equalTo(planProductEdit.getQuantity())));
        assertThat(putResponse.getBody().getLimited(), is(equalTo(planProductEdit.getLimited())));
    }

    @Test
    @DirtiesContext
    public void updatePlanProductUnsuccessfulTest() {
        final ResponseEntity<PlanProductViewDto> createPlanProductResponse = createValidPlanProduct();
        PlanProductViewDto responseBody = createPlanProductResponse.getBody();

        final ResponseEntity<PlanViewDto> createPlanResponse = createSecondPlan();
        final ResponseEntity<ProductViewDto> createProductResponse = createSecondProduct();

        final PlanProductEditDto planProductEdit = new PlanProductEditDto(
                responseBody.getId(),
                null,
                true,
                createPlanResponse.getBody().getId(),
                createProductResponse.getBody().getId()
        );


        final HttpEntity<PlanProductEditDto> httpEntityPut = new HttpEntity<>(planProductEdit, adminHeaders);
        final ResponseEntity<PlanProductViewDto> putResponse = restTemplate
                .exchange("/v1/plans/products", HttpMethod.PUT, httpEntityPut, PlanProductViewDto.class);

        assertThat(putResponse.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
    }

    @Test
    @DirtiesContext
    public void deletePlanProductTest() {
        final ResponseEntity<PlanProductViewDto> createResponse = createValidPlanProduct();
        final Long planProductId = createResponse.getBody().getId();

        final HttpEntity<Void> httpEntityDELETE = new HttpEntity<>(adminHeaders);
        final ResponseEntity<Void> deleteResponse = restTemplate
                .exchange("/v1/plans/products/" + planProductId, HttpMethod.DELETE, httpEntityDELETE, Void.class);

        assertThat(deleteResponse.getStatusCode(), is(equalTo(HttpStatus.NO_CONTENT)));

        final HttpEntity<Void> httpEntityGET = new HttpEntity<>(adminHeaders);
        final ResponseEntity<PlanProductViewDto> getResponse = restTemplate
                .exchange("/v1/plans/products/" + planProductId, HttpMethod.GET, httpEntityGET, PlanProductViewDto.class);

        assertThat(getResponse.getStatusCode(), is(equalTo(HttpStatus.NOT_FOUND)));
    }

    private ResponseEntity<PlanProductViewDto> createValidPlanProduct() {
        final HttpEntity<PlanProductDto> httpEntityPOST =
                new HttpEntity<>(validPlanProductDto, adminHeaders);
        final ResponseEntity<PlanProductViewDto> createPlanProductResponse =
                restTemplate.exchange("/v1/plans/products", HttpMethod.POST, httpEntityPOST, PlanProductViewDto.class);

        return createPlanProductResponse;
    }

    private ResponseEntity<PlanProductViewDto> createInvalidPlanProduct() {
        final HttpEntity<PlanProductDto> httpEntityPOST =
                new HttpEntity<>(invalidPlanProductDto, adminHeaders);
        final ResponseEntity<PlanProductViewDto> createPlanProductResponse =
                restTemplate.exchange("/v1/plans/products", HttpMethod.POST, httpEntityPOST, PlanProductViewDto.class);

        return createPlanProductResponse;
    }

    private ResponseEntity<ProductViewDto> createFirstProduct() {
        final ProductDto productDto = new ProductDto(
                "Search templates",
                "Search templates");

        final HttpEntity<ProductDto> httpEntityPOST = new HttpEntity<>(productDto, adminHeaders);
        final ResponseEntity<ProductViewDto> createResponse = restTemplate
                .exchange("/v1/products", HttpMethod.POST, httpEntityPOST, ProductViewDto.class);

        return createResponse;
    }

    private ResponseEntity<ProductViewDto> createSecondProduct() {
        final ProductDto productDto = new ProductDto(
                "Posts",
                "Posts");

        final HttpEntity<ProductDto> httpEntityPOST = new HttpEntity<>(productDto, adminHeaders);
        final ResponseEntity<ProductViewDto> createResponse = restTemplate
                .exchange("/v1/products", HttpMethod.POST, httpEntityPOST, ProductViewDto.class);

        return createResponse;
    }

    private ResponseEntity<PlanViewDto> createFirstPlan() {
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

    private ResponseEntity<PlanViewDto> createSecondPlan() {
        final PlanDto planDto = new PlanDto(
                "Pro",
                PlanType.PRO,
                "This is a PRO plan",
                1,
                new MoneyDto("USD", BigDecimal.valueOf(400.00)));

        final HttpEntity<PlanDto> httpEntityPOST = new HttpEntity<>(planDto, adminHeaders);
        final ResponseEntity<PlanViewDto> createResponse = restTemplate
                .exchange("/v1/plans", HttpMethod.POST, httpEntityPOST, PlanViewDto.class);
        return createResponse;
    }


}

package com.opdev;

import com.opdev.common.services.Profiles;
import com.opdev.offers.product.dto.ProductDto;
import com.opdev.offers.product.dto.ProductEditDto;
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

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;


@ActiveProfiles(Profiles.TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductCrudIntegrationTest extends AbstractIntegrationTest {

    private final ProductDto productDto;
    private final HttpHeaders adminHeaders;

    public ProductCrudIntegrationTest() {
        productDto = new ProductDto("Search templates", "Search templates");
        adminHeaders = createAuthHeaders(getTokenForAdmin());
    }

    @BeforeEach
    public void init() {
        createAdmin();
    }

    @Test
    @DirtiesContext
    public void createProductTest() {
        final ResponseEntity<ProductViewDto> createResponse = createProduct();

        assertThat(createResponse.getStatusCode(), is(equalTo(HttpStatus.CREATED)));
        assertThat(createResponse.getBody(), is(notNullValue()));
        assertThat(createResponse.getBody().getName(), is(equalTo(productDto.getName())));
        assertThat(createResponse.getBody().getDescription(), is(equalTo(productDto.getDescription())));
    }

    @Test
    @DirtiesContext
    public void findAllProductsTest() {
        createProduct();

        final HttpEntity<Void> httpEntityGET = new HttpEntity<>(adminHeaders);
        ResponseEntity<List<ProductViewDto>> response = restTemplate
                .exchange("/v1/products", HttpMethod.GET, httpEntityGET, ParameterizedTypeReference.forType(List.class));

        assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().size(), is(equalTo(1)));
    }

    @Test
    @DirtiesContext
    public void getProductTest() {
        final ResponseEntity<ProductViewDto> createResponse = createProduct();
        final Long productId = createResponse.getBody().getId();

        final HttpEntity<Void> httpEntityGet = new HttpEntity<>(adminHeaders);
        final ResponseEntity<ProductViewDto> getResponse = restTemplate
                .exchange("/v1/products/" + productId, HttpMethod.GET, httpEntityGet, ProductViewDto.class);

        assertThat(getResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(getResponse.getBody(), is(notNullValue()));
        assertThat(getResponse.getBody().getName(), is(equalTo(productDto.getName())));
        assertThat(getResponse.getBody().getDescription(), is(equalTo(productDto.getDescription())));
    }

    @Test
    @DirtiesContext
    public void updateProductTest() {
        final ResponseEntity<ProductViewDto> createResponse = createProduct();
        final ProductEditDto productEdit = new ProductEditDto(
                createResponse.getBody().getId(),
                "Updated Product",
                "Updated Product"
        );

        final HttpEntity<ProductEditDto> httpEntityPUT = new HttpEntity<>(productEdit, adminHeaders);
        final ResponseEntity<ProductViewDto> putResponse = restTemplate
                .exchange("/v1/products", HttpMethod.PUT, httpEntityPUT, ProductViewDto.class);

        assertThat(putResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(putResponse.getBody(), is(notNullValue()));
        assertThat(putResponse.getBody().getName(), is(equalTo(productEdit.getName())));
        assertThat(putResponse.getBody().getDescription(), is(equalTo(productEdit.getDescription())));
    }

    @Test
    @DirtiesContext
    public void deleteProductTest() {
        final ResponseEntity<ProductViewDto> createResponse = createProduct();
        final Long productId = createResponse.getBody().getId();

        final HttpEntity<Void> httpEntityDELETE = new HttpEntity<>(adminHeaders);
        final ResponseEntity<Void> deleteResponse = restTemplate
                .exchange("/v1/products/" + productId, HttpMethod.DELETE, httpEntityDELETE, Void.class);

        assertThat(deleteResponse.getStatusCode(), is(equalTo(HttpStatus.NO_CONTENT)));

        final HttpEntity<Void> httpEntityGET = new HttpEntity<>(adminHeaders);
        final ResponseEntity<ProductViewDto> getResponse = restTemplate
                .exchange("/v1/products/" + productId, HttpMethod.GET, httpEntityGET, ProductViewDto.class);
        assertThat(getResponse.getStatusCode(), is(equalTo(HttpStatus.NOT_FOUND)));
    }

    private ResponseEntity<ProductViewDto> createProduct() {
        final HttpEntity<ProductDto> httpEntityPOST = new HttpEntity<>(productDto, adminHeaders);
        final ResponseEntity<ProductViewDto> createResponse = restTemplate
                .exchange("/v1/products", HttpMethod.POST, httpEntityPOST, ProductViewDto.class);

        return createResponse;
    }

}

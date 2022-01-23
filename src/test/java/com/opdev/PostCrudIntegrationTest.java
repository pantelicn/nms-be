package com.opdev;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opdev.common.services.Profiles;
import com.opdev.model.company.Company;
import com.opdev.post.dto.PostAddDto;
import com.opdev.post.dto.PostViewDto;

@ActiveProfiles(Profiles.TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostCrudIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ObjectMapper mapper;

    @Test
    @DirtiesContext
    public void fetchPosts() {
        Company googleCompany = createCompany(COMPANY_GOOGLE);
        HttpHeaders headers = createAuthHeaders(getTokenForCompanyGoogle());

        PostAddDto post1 = new PostAddDto("some description 1", "google.com");
        PostAddDto post2 = new PostAddDto("some description 2", "google.com");

        final HttpEntity<PostAddDto> post1Http = new HttpEntity<>(post1, headers);
        restTemplate.exchange("/v1/companies/" + COMPANY_GOOGLE + "/posts", HttpMethod.POST, post1Http, PostViewDto.class);

        final HttpEntity<PostAddDto> post2Http = new HttpEntity<>(post2, headers);
        restTemplate.exchange("/v1/companies/" + COMPANY_GOOGLE + "/posts", HttpMethod.POST, post2Http, PostViewDto.class);

        final HttpEntity<Void> getPostsForCompany = new HttpEntity<>(headers);
        final ResponseEntity<Object> getPostsForCompanyResponse = restTemplate.exchange("/v1/posts?company=" + googleCompany.getId(), HttpMethod.GET, getPostsForCompany, Object.class);

        Map mapResponse = (Map)getPostsForCompanyResponse.getBody();
        assertThat(getPostsForCompanyResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(mapResponse.get("totalElements"), is(equalTo(2)));
        assertThat(mapResponse.get("totalPages"), is(equalTo(1)));
    }

}

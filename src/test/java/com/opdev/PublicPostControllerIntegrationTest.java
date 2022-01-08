package com.opdev;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import com.opdev.common.services.Profiles;
import com.opdev.company.dto.CompanyRegistrationDto;
import com.opdev.dto.LoginSuccessDto;
import com.opdev.post.dto.PostAddDto;
import com.opdev.post.dto.PostViewDto;
import com.opdev.repository.PostRepository;
import com.opdev.skill.dto.SkillViewDto;

@ActiveProfiles(Profiles.TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Rollback
public class PublicPostControllerIntegrationTest extends AbstractIntegrationTest {

    private PostRepository postRepository;

    @Test
    public void fetchLatestPosts() {
        CompanyRegistrationDto opdev = createNewCompany("opdev@opdev.com");
        registerCompany(opdev);
        ResponseEntity<LoginSuccessDto> login = login(opdev.getUsername(), opdev.getPassword());
        HttpHeaders headers = createAuthHeaders(login.getBody().getToken());

        PostAddDto post1 = new PostAddDto("some description 1", "google.com");

        final HttpEntity<PostAddDto> post1Http = new HttpEntity<>(post1, headers);
        final ResponseEntity<PostViewDto> createdPostResponse = restTemplate.exchange("/v1/companies/" + opdev.getUsername() + "/posts", HttpMethod.POST, post1Http, PostViewDto.class);

        assertThat(createdPostResponse.getStatusCode(), is(equalTo(HttpStatus.CREATED)));
        assertThat(createdPostResponse.getBody().getDescription(), is(equalTo(post1.getDescription())));
        assertThat(createdPostResponse.getBody().getUrl(), is(equalTo(post1.getUrl())));

        PostAddDto post2 = new PostAddDto("some description 2", "google.com");
        final HttpEntity<PostAddDto> post2Http = new HttpEntity<>(post2, headers);
        restTemplate.exchange("/v1/companies/" + opdev.getUsername() + "/posts", HttpMethod.POST, post2Http, PostViewDto.class);

        PostAddDto post3 = new PostAddDto("some description 3", "google.com");
        final HttpEntity<PostAddDto> post3Http = new HttpEntity<>(post3, headers);
        restTemplate.exchange("/v1/companies/" + opdev.getUsername() + "/posts", HttpMethod.POST, post3Http, PostViewDto.class);

        PostAddDto post4 = new PostAddDto("some description 4", "google.com");
        final HttpEntity<PostAddDto> post4Http = new HttpEntity<>(post4, headers);
        restTemplate.exchange("/v1/companies/" + opdev.getUsername() + "/posts", HttpMethod.POST, post4Http, PostViewDto.class);

        PostAddDto post5 = new PostAddDto("some description 5", "google.com");
        final HttpEntity<PostAddDto> post5Http = new HttpEntity<>(post5, headers);
        restTemplate.exchange("/v1/companies/" + opdev.getUsername() + "/posts", HttpMethod.POST, post5Http, PostViewDto.class);

        PostAddDto post6 = new PostAddDto("some description 6", "google.com");
        final HttpEntity<PostAddDto> post6Http = new HttpEntity<>(post6, headers);
        restTemplate.exchange("/v1/companies/" + opdev.getUsername() + "/posts", HttpMethod.POST, post6Http, PostViewDto.class);

        PostAddDto post7 = new PostAddDto("some description 7", "google.com");
        final HttpEntity<PostAddDto> post7Http = new HttpEntity<>(post7, headers);
        restTemplate.exchange("/v1/companies/" + opdev.getUsername() + "/posts", HttpMethod.POST, post7Http, PostViewDto.class);

        PostAddDto post8 = new PostAddDto("some description 8", "google.com");
        final HttpEntity<PostAddDto> post8Http = new HttpEntity<>(post8, headers);
        restTemplate.exchange("/v1/companies/" + opdev.getUsername() + "/posts", HttpMethod.POST, post8Http, PostViewDto.class);

        PostAddDto post9 = new PostAddDto("some description 9", "google.com");
        final HttpEntity<PostAddDto> post9Http = new HttpEntity<>(post9, headers);
        restTemplate.exchange("/v1/companies/" + opdev.getUsername() + "/posts", HttpMethod.POST, post9Http, PostViewDto.class);

        PostAddDto post10 = new PostAddDto("some description 10", "google.com");
        final HttpEntity<PostAddDto> post10Http = new HttpEntity<>(post10, headers);
        restTemplate.exchange("/v1/companies/" + opdev.getUsername() + "/posts", HttpMethod.POST, post10Http, PostViewDto.class);

        PostAddDto post11 = new PostAddDto("some description 11", "google.com");
        final HttpEntity<PostAddDto> post11Http = new HttpEntity<>(post11, headers);
        restTemplate.exchange("/v1/companies/" + opdev.getUsername() + "/posts", HttpMethod.POST, post11Http, PostViewDto.class);


        final HttpEntity<Void> getLatest10Http = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<List<PostViewDto>> getLatest10PostsResponse = restTemplate.exchange("/v1/public/posts?country=Serbia", HttpMethod.GET,
                                                                                     getLatest10Http, new ParameterizedTypeReference<List<PostViewDto>>() {});

        assertThat(getLatest10PostsResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(getLatest10PostsResponse.getBody().size(), is(equalTo(10)));
        assertThat(getLatest10PostsResponse.getBody().get(0).getDescription(), is(equalTo(post11.getDescription())));
        assertThat(getLatest10PostsResponse.getBody().get(9).getDescription(), is(equalTo(post2.getDescription())));
    }
}

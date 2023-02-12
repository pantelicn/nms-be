package com.opdev;

import com.opdev.common.services.Profiles;
import com.opdev.model.company.Company;
import com.opdev.model.post.Post;
import com.opdev.model.post.ReactionType;
import com.opdev.model.talent.Talent;
import com.opdev.post.dto.PostReactionDto;
import com.opdev.post.dto.PostReactionViewDto;
import com.opdev.repository.PostRepository;
import com.opdev.repository.TalentRepository;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;


@ActiveProfiles(Profiles.TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PostReactionIntegrationTest extends AbstractIntegrationTest {

    private Talent talent;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TalentRepository talentRepository;

    @BeforeEach
    public void init() {
        talent = getTalent();
    }

    @Test
    @DirtiesContext
    void addRemoveReactionTest() {
        HttpHeaders goranHeaders = createAuthHeaders(getTokenForTalentGoran());
        Post post = createPost();
        PostReactionDto postReactionDto = new PostReactionDto(ReactionType.LIKE);

        final HttpEntity<PostReactionDto> postReactionHttp = new HttpEntity<>(postReactionDto, goranHeaders);
        final ResponseEntity<PostReactionViewDto> receivedMessage =
                restTemplate.exchange("/v1/posts/" + post.getId() + "/reactions", HttpMethod.PUT, postReactionHttp, PostReactionViewDto.class);
        final PostReactionViewDto responseBody = receivedMessage.getBody();

        assertThat(responseBody, is(notNullValue()));
        assertThat(receivedMessage.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(responseBody.getPostId(), is(equalTo(post.getId())));
        assertThat(responseBody.getReaction(), is(equalTo(ReactionType.LIKE)));

        restTemplate.exchange("/v1/posts/" + post.getId() + "/reactions", HttpMethod.DELETE, postReactionHttp, Void.class);

        assertThat(getPost(post.getId()).getLikes(), is(equalTo(0)));
    }

    @Test
    @DirtiesContext
    void removeNonExistentReactionTest() {
        HttpHeaders goranHeaders = createAuthHeaders(getTokenForTalentGoran());
        Post post = createPost();
        PostReactionDto postReactionDto = new PostReactionDto(ReactionType.LIKE);

        final HttpEntity<PostReactionDto> postReactionHttp = new HttpEntity<>(postReactionDto, goranHeaders);
        final ResponseEntity<Void> receivedMessage =
                restTemplate.exchange("/v1/posts/" + post.getId() + "/reactions", HttpMethod.DELETE, postReactionHttp, Void.class);

        assertThat(receivedMessage.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
        assertThat(getPost(post.getId()).getLikes(), is(equalTo(0)));
    }

    private Post createPost() {
        Company google = createCompany(COMPANY_GOOGLE);
        Post post = Post.builder()
                .company(google)
                .content("description")
                .title("title")
                .country(google.getLocation().getCountry())
                .url("url")
                .build();
        return postRepository.save(post);
    }

    private Talent getTalent() {
        Optional<Talent> found = talentRepository.findByUserUsername(TALENT_GORAN);
        return found.orElseGet(() -> createTalent(TALENT_GORAN));
    }

    private Post getPost(Long id) {
        return postRepository.findById(id).orElseGet(() -> createPost());
    }

}
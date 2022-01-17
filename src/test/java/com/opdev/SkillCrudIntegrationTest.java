package com.opdev;

import com.opdev.common.services.Profiles;
import com.opdev.model.talent.SkillStatus;
import com.opdev.skill.dto.SkillAddDto;
import com.opdev.skill.dto.SkillEditDto;
import com.opdev.skill.dto.SkillStatusDto;
import com.opdev.skill.dto.SkillViewDto;
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
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

@ActiveProfiles(Profiles.TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SkillCrudIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DirtiesContext
    public void crudTest() {
        createAdmin();
        final String token = getTokenForAdmin();
        final SkillAddDto newSkillDto = new SkillAddDto("Java", "JAVA");
        final HttpHeaders headers = createAuthHeaders(token);
        final HttpEntity<SkillAddDto> httpEntityPOST = new HttpEntity<>(newSkillDto, headers);
        final ResponseEntity<SkillViewDto> addSkillResponse = restTemplate.exchange("/v1/skills", HttpMethod.POST, httpEntityPOST, SkillViewDto.class);

        final SkillViewDto createdSkill = addSkillResponse.getBody();
        assertThat(addSkillResponse.getStatusCode(), is(equalTo(HttpStatus.CREATED)));
        assertThat(createdSkill, is(notNullValue()));
        assertThat(createdSkill.getName(), is(equalTo(newSkillDto.getName())));
        assertThat(createdSkill.getCode(), is(equalTo(newSkillDto.getCode())));
        assertThat(createdSkill.getStatus(), is(equalTo(SkillStatus.PENDING)));

        findAll(1);
        findAllWhereStatusIsPending(1);
        findAllWhereStatusIsApproved(0);

        final SkillEditDto skillEditDto = new SkillEditDto(createdSkill.getId(), "Java language", "JAVA");
        final HttpEntity<SkillEditDto> httpEntityPUT = new HttpEntity<>(skillEditDto, headers);

        final ResponseEntity<SkillViewDto> modifiedSkillResponse = restTemplate.exchange("/v1/skills", HttpMethod.PUT, httpEntityPUT, SkillViewDto.class);

        final SkillViewDto modifiedSkill = modifiedSkillResponse.getBody();
        assertThat(modifiedSkillResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(modifiedSkill, is(notNullValue()));
        assertThat(modifiedSkill.getId(), is(equalTo(skillEditDto.getId())));
        assertThat(modifiedSkill.getName(), is(equalTo(skillEditDto.getName())));
        assertThat(modifiedSkill.getCode(), is(equalTo(skillEditDto.getCode())));
        assertThat(modifiedSkill.getStatus(), is(equalTo(SkillStatus.PENDING)));

        final ResponseEntity<SkillViewDto> getSkillResponse = restTemplate.exchange("/v1/skills/" + modifiedSkill.getCode(), HttpMethod.GET, HttpEntity.EMPTY, SkillViewDto.class);

        final SkillViewDto foundSkill = getSkillResponse.getBody();

        assertThat(getSkillResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(foundSkill, is(notNullValue()));
        assertThat(foundSkill.getId(), is(equalTo(skillEditDto.getId())));
        assertThat(foundSkill.getName(), is(equalTo(skillEditDto.getName())));
        assertThat(foundSkill.getCode(), is(equalTo(skillEditDto.getCode())));
        assertThat(foundSkill.getStatus(), is(equalTo(SkillStatus.PENDING)));

        final SkillStatusDto skillStatusDto = new SkillStatusDto(SkillStatus.APPROVED);
        final HttpEntity<SkillStatusDto> httpEntityPATCH = new HttpEntity<>(skillStatusDto, headers);
        final ResponseEntity<SkillViewDto> changeStatusResponse = restTemplate.exchange("/v1/skills/" + foundSkill.getCode() + "/status", HttpMethod.PATCH, httpEntityPATCH, SkillViewDto.class);
        final SkillViewDto skillWithApprovedStatus = changeStatusResponse.getBody();

        assertThat(changeStatusResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(skillWithApprovedStatus, is(notNullValue()));
        assertThat(skillWithApprovedStatus.getStatus(), is(equalTo(SkillStatus.APPROVED)));

        findAllWhereStatusIsPending(0);
        findAllWhereStatusIsApproved(1);

        final HttpEntity<SkillAddDto> httpEntityDELETE = new HttpEntity<>(headers);
        final ResponseEntity<Void> removeSkillResponse = restTemplate.exchange("/v1/skills/" + foundSkill.getCode(), HttpMethod.DELETE, httpEntityDELETE, Void.class);

        assertThat(removeSkillResponse.getStatusCode(), is(equalTo(HttpStatus.NO_CONTENT)));

        findAll(0);
    }

    private void findAll(int expectedSkills) {
        final ResponseEntity<List<SkillViewDto>> findAllResponse = restTemplate.exchange("/v1/skills", HttpMethod.GET, HttpEntity.EMPTY, new ParameterizedTypeReference<List<SkillViewDto>>() {});
        findAllChecker(findAllResponse, expectedSkills);
    }

    private void findAllWhereStatusIsPending(int expectedSkills) {
        final ResponseEntity<List<SkillViewDto>> findAllResponse = restTemplate.exchange("/v1/skills?status=PENDING", HttpMethod.GET, HttpEntity.EMPTY, new ParameterizedTypeReference<List<SkillViewDto>>() {});
        findAllChecker(findAllResponse, expectedSkills);
    }

    private void findAllWhereStatusIsApproved(int expectedSkills) {
        final ResponseEntity<List<SkillViewDto>> findAllResponse = restTemplate.exchange("/v1/skills?status=APPROVED", HttpMethod.GET, HttpEntity.EMPTY, new ParameterizedTypeReference<List<SkillViewDto>>() {});
        findAllChecker(findAllResponse, expectedSkills);
    }

    private void findAllChecker(final ResponseEntity<List<SkillViewDto>> response, int expectedSkills) {
        assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().size(), is(expectedSkills));
    }

}

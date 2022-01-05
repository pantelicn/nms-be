package com.opdev;

import com.opdev.common.services.Profiles;
import com.opdev.dto.LoginSuccessDto;
import com.opdev.model.talent.SkillStatus;
import com.opdev.position.dto.PositionCreateDto;
import com.opdev.position.dto.PositionSkillsViewDto;
import com.opdev.position.dto.PositionViewDto;
import com.opdev.skill.dto.SkillAddDto;
import com.opdev.skill.dto.SkillStatusDto;
import com.opdev.skill.dto.SkillViewDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ActiveProfiles(Profiles.TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PositionSkillIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DirtiesContext
    public void testCrud() {
        ResponseEntity<LoginSuccessDto> loginResponse = login(ADMIN_GORAN, ADMIN_GORAN_PASSWORD);
        assertThat(loginResponse.getBody(), is(notNullValue()));
        final String token = loginResponse.getBody().getToken();
        final HttpHeaders headers = createAuthHeaders(token);

        PositionViewDto createdPosition = addPosition(headers);

        SkillViewDto javaSkill = addSkill(headers, "Java", "JAVA");

        approveSkill(headers, javaSkill.getCode());

        SkillViewDto phpSkill = addSkill(headers, "PHP", "PHP");

        approveSkill(headers, phpSkill.getCode());

        addRelation(headers, createdPosition.getCode(), phpSkill.getCode(), javaSkill.getCode());

        getRelations(headers, createdPosition.getCode(), phpSkill.getCode(), javaSkill.getCode());

        removeSkillFromPosition(headers, createdPosition.getCode(), phpSkill.getCode());

        getRelations(headers, createdPosition.getCode(), javaSkill.getCode());
    }

    private PositionViewDto addPosition(final HttpHeaders headers) {
        final PositionCreateDto newPositionDto = new PositionCreateDto("Backend", "Backend developer", "BACKEND_DEV");
        final HttpEntity<PositionCreateDto> positionHttpEntityPOST = new HttpEntity<>(newPositionDto, headers);
        final ResponseEntity<PositionViewDto> addResponse = restTemplate.exchange("/v1/positions", HttpMethod.POST,
                positionHttpEntityPOST, PositionViewDto.class);

        final PositionViewDto createdPosition = addResponse.getBody();
        assertThat(addResponse.getStatusCode(), is(equalTo(HttpStatus.CREATED)));
        assertThat(createdPosition, is(notNullValue()));
        assertThat(createdPosition.getCode(), is(equalTo(newPositionDto.getCode())));
        assertThat(createdPosition.getName(), is(equalTo(newPositionDto.getName())));
        assertThat(createdPosition.getDescription(), is(equalTo(newPositionDto.getDescription())));

        return createdPosition;
    }

    private SkillViewDto addSkill(final HttpHeaders headers, final String skillName, final String skillCode) {
        final SkillAddDto newSkillDto = new SkillAddDto(skillName, skillCode);
        final HttpEntity<SkillAddDto> skillHttpEntityPOST = new HttpEntity<>(newSkillDto, headers);
        final ResponseEntity<SkillViewDto> addSkillResponse = restTemplate.exchange("/v1/skills", HttpMethod.POST, skillHttpEntityPOST, SkillViewDto.class);

        final SkillViewDto createdSkill = addSkillResponse.getBody();
        assertThat(addSkillResponse.getStatusCode(), is(equalTo(HttpStatus.CREATED)));
        assertThat(createdSkill, is(notNullValue()));
        assertThat(createdSkill.getName(), is(equalTo(newSkillDto.getName())));
        assertThat(createdSkill.getCode(), is(equalTo(newSkillDto.getCode())));
        assertThat(createdSkill.getStatus(), is(equalTo(SkillStatus.PENDING)));

        return createdSkill;
    }

    private void approveSkill(final HttpHeaders headers, String skillCode) {
        final SkillStatusDto skillStatusDto = new SkillStatusDto(SkillStatus.APPROVED);
        final HttpEntity<SkillStatusDto> httpEntityPATCH = new HttpEntity<>(skillStatusDto, headers);
        final ResponseEntity<SkillViewDto> changeStatusResponse = restTemplate.exchange("/v1/skills/" + skillCode + "/status", HttpMethod.PATCH, httpEntityPATCH, SkillViewDto.class);
        final SkillViewDto skillWithApprovedStatus = changeStatusResponse.getBody();

        assertThat(changeStatusResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(skillWithApprovedStatus, is(notNullValue()));
        assertThat(skillWithApprovedStatus.getStatus(), is(equalTo(SkillStatus.APPROVED)));
    }

    public void addRelation(final HttpHeaders headers, final String positionCode, final String ... skills) {
        final List<String> skillCodes = Arrays.asList(skills);
        final HttpEntity<List<String>> httpEntityPOST = new HttpEntity<>(skillCodes, headers);
        final ResponseEntity<PositionSkillsViewDto> addResponse = restTemplate.exchange("/v1/positions/" + positionCode + "/skills", HttpMethod.POST,
                httpEntityPOST, PositionSkillsViewDto.class);

        final PositionSkillsViewDto positionSkills = addResponse.getBody();

        assertThat(addResponse.getStatusCode(), is(equalTo(HttpStatus.CREATED)));
        assertThat(positionSkills, is(notNullValue()));
        assertThat(positionSkills.getPosition(), is(notNullValue()));
        assertThat(positionSkills.getPosition().getCode(), is(equalTo(positionCode)));
        assertThat(positionSkills.getSkills().size(), is(equalTo(2)));
        assertThat(positionSkills.getSkills().stream()
                .filter(skill -> skillCodes.contains(skill.getCode())).count(), is(2L));
    }

    private void getRelations(final HttpHeaders headers, final String positionCode, String ...expectedSkills) {
        final List<String> skillCodes = Arrays.asList(expectedSkills);
        final HttpEntity<List<String>> httpEntityGET = new HttpEntity<>(headers);
        final ResponseEntity<PositionSkillsViewDto> getResponse = restTemplate.exchange("/v1/positions/" + positionCode + "/skills", HttpMethod.GET,
                httpEntityGET, PositionSkillsViewDto.class);

        final PositionSkillsViewDto positionSkills = getResponse.getBody();

        assertThat(getResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(positionSkills, is(notNullValue()));
        assertThat(positionSkills.getPosition(), is(notNullValue()));
        assertThat(positionSkills.getPosition().getCode(), is(equalTo(positionCode)));
        assertThat(positionSkills.getSkills().size(), is(equalTo(expectedSkills.length)));
        assertThat(positionSkills.getSkills().stream()
                .filter(skill -> skillCodes.contains(skill.getCode())).count(), is((long) skillCodes.size()));
    }

    private void removeSkillFromPosition(final HttpHeaders headers, final String positionCode, final String skillCode) {
        final HttpEntity<List<String>> httpEntityDELETE = new HttpEntity<>(headers);
        final ResponseEntity<Void> deleteResponse = restTemplate.exchange("/v1/positions/" + positionCode + "/skills/" + skillCode, HttpMethod.DELETE,
                httpEntityDELETE, Void.class);
        assertThat(deleteResponse.getStatusCode(), is(equalTo(HttpStatus.NO_CONTENT)));
    }

}

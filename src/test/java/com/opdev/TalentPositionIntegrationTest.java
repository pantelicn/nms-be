package com.opdev;

import com.opdev.common.services.Profiles;
import com.opdev.dto.LoginSuccessDto;
import com.opdev.dto.TalentRegistrationDto;
import com.opdev.model.talent.SkillStatus;
import com.opdev.position.dto.PositionCreateDto;
import com.opdev.position.dto.PositionSkillsViewDto;
import com.opdev.position.dto.PositionViewDto;
import com.opdev.skill.dto.SkillAddDto;
import com.opdev.skill.dto.SkillStatusDto;
import com.opdev.skill.dto.SkillViewDto;
import com.opdev.talent.dto.TalentSkillsViewDto;
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

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@ActiveProfiles(Profiles.TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Rollback
class TalentPositionIntegrationTest extends AbstractIntegrationTest {

    @Test
    void addGetTest() {
        ResponseEntity<LoginSuccessDto> loginResponse = login("gox69@opdev.rs", "rav4");
        assertThat(loginResponse.getBody(), is(notNullValue()));
        final String token = loginResponse.getBody().getToken();
        final HttpHeaders headers = createAuthHeaders(token);

        final String talentGogara = "gogara@gmail.com";
        final TalentRegistrationDto gogara = createNewTalent(talentGogara);
        registerTalent(gogara);

        PositionViewDto frontendPosition = addPosition(headers);

        SkillViewDto htmlSkill = addSkill(headers);

        approveSkill(headers, htmlSkill.getCode());

        addPositionSkills(headers, frontendPosition.getCode(), htmlSkill.getCode());

        addSkillToTalent(List.of(htmlSkill.getCode()), talentGogara, headers);

        List<PositionViewDto> gogaraPositions = getTalentPositions(headers, talentGogara);

        assertThat(gogaraPositions, hasSize(1));
    }

    private List<PositionViewDto> getTalentPositions(HttpHeaders headers, String username) {
        final HttpEntity<Void> httpEntityGET = new HttpEntity<>(headers);
        ResponseEntity<List<PositionViewDto>> talentPositionsResponse = restTemplate
                .exchange("/v1/talents/" + username + "/positions", HttpMethod.GET,
                        httpEntityGET, new ParameterizedTypeReference<>() {});

        List<PositionViewDto> talentPositions = talentPositionsResponse.getBody();

        assertThat(talentPositionsResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(talentPositions, is(notNullValue()));

        return talentPositions;
    }

    private PositionViewDto addPosition(final HttpHeaders headers) {
        final PositionCreateDto newPositionDto = new PositionCreateDto("Frontend", "Frontend developer", "FRONTEND_DEV");
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

    private SkillViewDto addSkill(final HttpHeaders headers) {
        final SkillAddDto newSkillDto = new SkillAddDto("html", "HTML");
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

    public void addPositionSkills(final HttpHeaders headers, final String positionCode, final String ... skills) {
        final List<String> skillCodes = Arrays.asList(skills);
        final HttpEntity<List<String>> httpEntityPOST = new HttpEntity<>(skillCodes, headers);
        final ResponseEntity<PositionSkillsViewDto> addResponse = restTemplate.exchange("/v1/positions/" + positionCode + "/skills", HttpMethod.POST,
                httpEntityPOST, PositionSkillsViewDto.class);

        final PositionSkillsViewDto positionSkills = addResponse.getBody();

        assertThat(addResponse.getStatusCode(), is(equalTo(HttpStatus.CREATED)));
        assertThat(positionSkills, is(notNullValue()));
        assertThat(positionSkills.getPosition(), is(notNullValue()));
        assertThat(positionSkills.getPosition().getCode(), is(equalTo(positionCode)));
        assertThat(positionSkills.getSkills().size(), is(equalTo(1)));
        assertThat(positionSkills.getSkills().stream()
                .filter(skill -> skillCodes.contains(skill.getCode())).count(), is(1L));
    }

    private void addSkillToTalent(List<String> skillCodes, String username, HttpHeaders headers) {
        final HttpEntity<List<String>> httpEntityPost = new HttpEntity<>(skillCodes, headers);
        final ResponseEntity<TalentSkillsViewDto> addSkillsResponse = restTemplate.exchange("/v1/talents/" + username + "/skills", HttpMethod.POST, httpEntityPost, TalentSkillsViewDto.class);
        final TalentSkillsViewDto addSkills = addSkillsResponse.getBody();

        assertThat(addSkillsResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(addSkills, is(notNullValue()));
        assertThat(addSkills.getSkills().size(), is(skillCodes.size()));
    }

}

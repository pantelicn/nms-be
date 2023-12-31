package com.opdev;

import com.opdev.common.services.Profiles;
import com.opdev.model.talent.SkillStatus;
import com.opdev.skill.dto.SkillAddDto;
import com.opdev.skill.dto.SkillStatusDto;
import com.opdev.skill.dto.SkillViewDto;
import com.opdev.talent.dto.TalentSkillsViewDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

@ActiveProfiles(Profiles.TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TalentSkillControllerTest extends AbstractIntegrationTest {

    @Test
    @DirtiesContext
    public void testCrud() {
        createTalent(TALENT_GORAN);
        createAdmin();
        final String token = getTokenForTalentGoran();
        final HttpHeaders headers = createAuthHeaders(token);

        List<SkillViewDto> created = createSkills();

        addSkillToTalent(created.stream().map(SkillViewDto::getCode).collect(Collectors.toList()), TALENT_GORAN, headers);

        TalentSkillsViewDto found = getSkills(TALENT_GORAN, headers);
        assertThat(found.getSkills().containsAll(created), is(equalTo(true)));

        SkillViewDto skillToBeRemoved = found.getSkills().stream().findFirst().get();

        removeSkill(skillToBeRemoved.getCode(), TALENT_GORAN, headers);

        found = getSkills(TALENT_GORAN, headers);
        assertThat(found.getSkills(), not(contains(created.toArray())));

    }

    private List<SkillViewDto> createSkills() {
        final String token = getTokenForAdmin();
        final HttpHeaders headers = createAuthHeaders(token);

        SkillViewDto javaSkill = addSkill(headers, ".NET", ".NET");

        javaSkill = approveSkill(headers, javaSkill.getCode());

        SkillViewDto phpSkill = addSkill(headers, "PHP", "PHP");

        phpSkill = approveSkill(headers, phpSkill.getCode());

        return Arrays.asList(javaSkill, phpSkill);
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

    private SkillViewDto approveSkill(final HttpHeaders headers, String skillCode) {
        final SkillStatusDto skillStatusDto = new SkillStatusDto(SkillStatus.APPROVED);
        final HttpEntity<SkillStatusDto> httpEntityPATCH = new HttpEntity<>(skillStatusDto, headers);
        final ResponseEntity<SkillViewDto> changeStatusResponse = restTemplate.exchange("/v1/skills/" + skillCode + "/status", HttpMethod.PATCH, httpEntityPATCH, SkillViewDto.class);
        final SkillViewDto skillWithApprovedStatus = changeStatusResponse.getBody();

        assertThat(changeStatusResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(skillWithApprovedStatus, is(notNullValue()));
        assertThat(skillWithApprovedStatus.getStatus(), is(equalTo(SkillStatus.APPROVED)));

        return skillWithApprovedStatus;
    }

    private void addSkillToTalent(List<String> skillCodes, String username, HttpHeaders headers) {
        final HttpEntity<List<String>> httpEntityPost = new HttpEntity<>(skillCodes, headers);
        final ResponseEntity<TalentSkillsViewDto> addSkillsResponse = restTemplate.exchange("/v1/talents/" + username + "/skills", HttpMethod.POST, httpEntityPost, TalentSkillsViewDto.class);
        final TalentSkillsViewDto addSkills = addSkillsResponse.getBody();

        assertThat(addSkillsResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(addSkills.getSkills().size(), is(skillCodes.size()));
    }

    private TalentSkillsViewDto getSkills(String username, HttpHeaders headers) {
        final HttpEntity<TalentSkillsViewDto> httpEntityGet = new HttpEntity<>(headers);
        final ResponseEntity<TalentSkillsViewDto> getSkillsResponse = restTemplate.exchange("/v1/talents/" + username + "/skills", HttpMethod.GET, httpEntityGet, TalentSkillsViewDto.class);
        final TalentSkillsViewDto foundSkills = getSkillsResponse.getBody();

        assertThat(getSkillsResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));

        return foundSkills;
    }

    private void removeSkill(String skillCode, String username, HttpHeaders headers) {
        final HttpEntity<TalentSkillsViewDto> httpEntityDelete = new HttpEntity<>(headers);
        final ResponseEntity<Void> removeSkillResponse = restTemplate.exchange("/v1/talents/" + username + "/skills/" + skillCode, HttpMethod.DELETE, httpEntityDelete, Void.class);

        assertThat(removeSkillResponse.getStatusCode(), is(equalTo(HttpStatus.NO_CONTENT)));
    }

}

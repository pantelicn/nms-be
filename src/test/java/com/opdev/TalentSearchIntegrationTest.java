package com.opdev;

import com.opdev.common.services.Profiles;
import com.opdev.model.search.OperatorType;
import com.opdev.model.search.TableName;
import com.opdev.model.talent.SkillStatus;
import com.opdev.model.term.TermType;
import com.opdev.model.term.UnitOfMeasure;
import com.opdev.position.dto.PositionCreateDto;
import com.opdev.position.dto.PositionSkillsViewDto;
import com.opdev.position.dto.PositionViewDto;
import com.opdev.skill.dto.SkillAddDto;
import com.opdev.skill.dto.SkillStatusDto;
import com.opdev.skill.dto.SkillViewDto;
import com.opdev.talent.dto.FacetSpecifierDto;
import com.opdev.talent.dto.TalentSkillsViewDto;
import com.opdev.talent.dto.TalentTermAddDto;
import com.opdev.talent.dto.TalentTermViewDto;
import com.opdev.talent.dto.TalentViewDto;
import com.opdev.term.dto.TermAddDto;
import com.opdev.term.dto.TermViewDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@ActiveProfiles(Profiles.TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TalentSearchIntegrationTest extends AbstractIntegrationTest {

    @BeforeEach
    public void init() {
        createTalent(TALENT_GORAN);
        createAdmin();

        final HttpHeaders adminHeaders = createAuthHeaders(getTokenForAdmin());
        final HttpHeaders goranHeaders = createAuthHeaders(getTokenForTalentGoran());

        PositionViewDto frontendPosition = addPosition(adminHeaders);

        List<SkillViewDto> created = createSkills(adminHeaders);
        addPositionSkills(adminHeaders, frontendPosition.getCode(), created.stream().map(SkillViewDto::getCode).collect(Collectors.toList()));
        addSkillToTalent(created.stream().map(SkillViewDto::getCode).collect(Collectors.toList()), TALENT_GORAN, goranHeaders);


        final TermViewDto salary = addTerm(new TermAddDto("Salary", "Salary", TermType.INT), adminHeaders);
        final TermViewDto vacationDays = addTerm(new TermAddDto("Vacation", "Vacation", TermType.INT), adminHeaders);

        addTalentTerms(
                List.of(
                        new TalentTermAddDto("1600", true, salary.getCode(), UnitOfMeasure.EURO),
                        new TalentTermAddDto("20", true, vacationDays.getCode(), UnitOfMeasure.DAYS)
                ), goranHeaders);
    }

    @Test
    @DirtiesContext
    void testTalentsSearchWithResultsExpected() {
        List<FacetSpecifierDto> specifiers = List.of(
                new FacetSpecifierDto(TableName.SKILL, "HTML", "HTML", OperatorType.EQ),
                new FacetSpecifierDto(TableName.SKILL, "CSS", "CSS", OperatorType.EQ),
                new FacetSpecifierDto(TableName.POSITION, "FRONTEND_DEV", "FRONTEND_DEV", OperatorType.EQ),
                new FacetSpecifierDto(TableName.TERM, "SALARY", "1500", OperatorType.GTE),
                new FacetSpecifierDto(TableName.TERM, "VACATION", "15", OperatorType.GTE)
        );

        final HttpEntity<List<FacetSpecifierDto>> httpEntityPOST = new HttpEntity<>(specifiers, createAuthHeaders(getTokenForAdmin()));

        final ResponseEntity<List<TalentViewDto>> findTalentResponse = restTemplate
                .exchange("/v1/talents/find",
                        HttpMethod.POST,
                        httpEntityPOST,
                        new ParameterizedTypeReference<>() {
                        });

        assertThat(findTalentResponse.getBody().size(), is(equalTo(1)));
    }

    @Test
    @DirtiesContext
    void testTalentsSearchWithoutResultsExpected() {
        List<FacetSpecifierDto> specifiers = List.of(
                new FacetSpecifierDto(TableName.POSITION, "FRONTEND_DEV", "FRONTEND_DEV", OperatorType.EQ),
                new FacetSpecifierDto(TableName.TERM, "SALARY", "2000", OperatorType.GTE),
                new FacetSpecifierDto(TableName.TERM, "VACATION", "30", OperatorType.GTE)
        );

        final HttpEntity<List<FacetSpecifierDto>> httpEntityPOST = new HttpEntity<>(specifiers, createAuthHeaders(getTokenForAdmin()));

        final ResponseEntity<List<TalentViewDto>> findTalentResponse = restTemplate
                .exchange("/v1/talents/find",
                        HttpMethod.POST,
                        httpEntityPOST,
                        new ParameterizedTypeReference<>() {
                        });

        assertThat(findTalentResponse.getBody().size(), is(equalTo(0)));
    }

    private List<SkillViewDto> createSkills(HttpHeaders headers) {

        SkillViewDto htmlSkill = addSkill(headers, "HTML", "HTML");

        htmlSkill = approveSkill(headers, htmlSkill.getCode());

        SkillViewDto cssSkill = addSkill(headers, "CSS", "CSS");

        cssSkill = approveSkill(headers, cssSkill.getCode());

        return Arrays.asList(htmlSkill, cssSkill);
    }

    private SkillViewDto addSkill(final HttpHeaders headers, final String skillName, final String skillCode) {
        final SkillAddDto newSkillDto = new SkillAddDto(skillName, skillCode);
        final HttpEntity<SkillAddDto> skillHttpEntityPOST = new HttpEntity<>(newSkillDto, headers);
        final ResponseEntity<SkillViewDto> addSkillResponse = restTemplate
                .exchange("/v1/skills",
                        HttpMethod.POST,
                        skillHttpEntityPOST,
                        SkillViewDto.class);

        return addSkillResponse.getBody();
    }

    private SkillViewDto approveSkill(final HttpHeaders headers, String skillCode) {
        final SkillStatusDto skillStatusDto = new SkillStatusDto(SkillStatus.APPROVED);
        final HttpEntity<SkillStatusDto> httpEntityPATCH = new HttpEntity<>(skillStatusDto, headers);
        final ResponseEntity<SkillViewDto> changeStatusResponse = restTemplate
                .exchange("/v1/skills/" + skillCode + "/status",
                        HttpMethod.PATCH,
                        httpEntityPATCH,
                        SkillViewDto.class);

        return changeStatusResponse.getBody();
    }

    private void addSkillToTalent(List<String> skillCodes, String username, HttpHeaders headers) {
        final HttpEntity<List<String>> httpEntityPost = new HttpEntity<>(skillCodes, headers);
        restTemplate
                .exchange("/v1/talents/" + username + "/skills",
                        HttpMethod.POST,
                        httpEntityPost,
                        TalentSkillsViewDto.class);
    }

    private TermViewDto addTerm(TermAddDto newTerm, HttpHeaders headers) {
        final HttpEntity<TermAddDto> httpEntityPOST = new HttpEntity<>(newTerm, headers);
        final ResponseEntity<TermViewDto> addTermResponse = restTemplate
                .exchange("/v1/terms",
                        HttpMethod.POST,
                        httpEntityPOST,
                        TermViewDto.class);

        return addTermResponse.getBody();
    }

    private List<TalentTermViewDto> addTalentTerms(List<TalentTermAddDto> newTalentTerms, HttpHeaders headers) {
        final HttpEntity<List<TalentTermAddDto>> httpEntityPOST = new HttpEntity<>(newTalentTerms, headers);
        final ResponseEntity<List<TalentTermViewDto>> addTalentTermResponse = restTemplate
                .exchange("/v1/talents/" + TALENT_GORAN + "/terms",
                        HttpMethod.POST,
                        httpEntityPOST,
                        new ParameterizedTypeReference<>() {
                        });

        return addTalentTermResponse.getBody();
    }

    private PositionViewDto addPosition(final HttpHeaders headers) {
        final PositionCreateDto newPositionDto =
                new PositionCreateDto("Frontend", "Frontend developer", "FRONTEND_DEV");
        final HttpEntity<PositionCreateDto> positionHttpEntityPOST = new HttpEntity<>(newPositionDto, headers);
        final ResponseEntity<PositionViewDto> addResponse = restTemplate
                .exchange("/v1/positions",
                        HttpMethod.POST,
                        positionHttpEntityPOST,
                        PositionViewDto.class);

        return addResponse.getBody();
    }

    public void addPositionSkills(final HttpHeaders headers, final String positionCode, final List<String> skillCodes) {
        final HttpEntity<List<String>> httpEntityPOST = new HttpEntity<>(skillCodes, headers);
        restTemplate
                .exchange("/v1/positions/" + positionCode + "/skills",
                        HttpMethod.POST,
                        httpEntityPOST,
                        PositionSkillsViewDto.class);

    }
    
}

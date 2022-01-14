package com.opdev;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.opdev.common.services.Profiles;
import com.opdev.model.search.OperatorType;
import com.opdev.model.search.TableName;
import com.opdev.model.talent.Position;
import com.opdev.model.talent.Skill;
import com.opdev.model.talent.SkillStatus;
import com.opdev.model.term.Term;
import com.opdev.model.term.TermType;
import com.opdev.repository.PositionRepository;
import com.opdev.repository.SkillRepository;
import com.opdev.repository.TermRepository;
import com.opdev.search.dto.FacetAddDto;
import com.opdev.search.dto.FacetEditDto;
import com.opdev.search.dto.FacetViewDto;
import com.opdev.search.dto.SearchTemplateAddDto;
import com.opdev.search.dto.SearchTemplateEditDto;
import com.opdev.search.dto.SearchTemplateViewDto;

@ActiveProfiles(Profiles.TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SearchTemplateCrudIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private SkillRepository skillRepository;
    @Autowired
    private TermRepository termRepository;

    @BeforeEach
    public void init() {
        Position backendDeveloper = Position.builder()
                .code("BACKEND_DEVELOPER")
                .description("backend developer")
                .name("backend developer")
                .build();

        Skill java = Skill.builder()
                .code("JAVA")
                .name("java")
                .status(SkillStatus.APPROVED)
                .build();

        Skill sql = Skill.builder()
                .code("SQL")
                .name("sql")
                .status(SkillStatus.APPROVED)
                .build();

        Term salary = Term.builder()
                .code("SALARY")
                .description("monthly salary")
                .name("salary")
                .type(TermType.INT)
                .build();

        positionRepository.save(backendDeveloper);
        skillRepository.save(java);
        skillRepository.save(sql);
        termRepository.save(salary);
    }

    @Test
    @DirtiesContext
    public void searchTemplateCrud() {
        createCompany(COMPANY_GOOGLE);
        String token = getTokenForCompanyGoogle();
        HttpHeaders headers = createAuthHeaders(token);

        FacetAddDto backendDev = new FacetAddDto(TableName.POSITION, "BACKEND_DEVELOPER", "BACKEND_DEVELOPER", OperatorType.EQ);
        FacetAddDto java = new FacetAddDto(TableName.SKILL, "JAVA", "JAVA", OperatorType.EQ);
        FacetAddDto minSalary = new FacetAddDto(TableName.TERM, "SALARY", "1500", OperatorType.GTE);
        FacetAddDto maxSalary = new FacetAddDto(TableName.TERM, "SALARY", "2000", OperatorType.LTE);
        List<FacetAddDto> facets = new ArrayList<>(List.of(backendDev, java, minSalary, maxSalary));
        SearchTemplateAddDto javaDeveloperTemplate = new SearchTemplateAddDto("Senior java developer", facets);

        final HttpEntity<SearchTemplateAddDto> addJavaDevTemplate = new HttpEntity<>(javaDeveloperTemplate, headers);
        final ResponseEntity<SearchTemplateViewDto> javaDevTemplateCreatedResponse = restTemplate.exchange("/v1/companies/" + COMPANY_GOOGLE + "/search-templates", HttpMethod.POST, addJavaDevTemplate, SearchTemplateViewDto.class);

        assertThat(javaDevTemplateCreatedResponse.getStatusCode(), is(equalTo(HttpStatus.CREATED)));

        SearchTemplateViewDto javaDevTemplateBody =  javaDevTemplateCreatedResponse.getBody();

        assertThat(javaDevTemplateBody.getName(), is(equalTo(javaDeveloperTemplate.getName())));
        assertThat(javaDevTemplateBody.getFacets().size(), is(equalTo(4)));


        final HttpEntity<SearchTemplateEditDto> editJavaDevTemplate = new HttpEntity<>(new SearchTemplateEditDto(javaDevTemplateBody.getId(), "Senior java developer for SH"), headers);
        final ResponseEntity<SearchTemplateViewDto> javaDevTemplateModifiedResponse = restTemplate.exchange("/v1/companies/" + COMPANY_GOOGLE + "/search-templates", HttpMethod.PUT, editJavaDevTemplate, SearchTemplateViewDto.class);

        assertThat(javaDevTemplateModifiedResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(javaDevTemplateModifiedResponse.getBody().getName(), is(equalTo("Senior java developer for SH")));

        HttpEntity<Void> getJavaDevTemplate = new HttpEntity<>(headers);
        ResponseEntity<SearchTemplateViewDto> javaDevTemplateResponse = restTemplate.exchange("/v1/companies/" + COMPANY_GOOGLE + "/search-templates/" + javaDevTemplateBody.getId(), HttpMethod.GET, getJavaDevTemplate, SearchTemplateViewDto.class);

        assertThat(javaDevTemplateResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(javaDevTemplateResponse.getBody().getName(), is(equalTo("Senior java developer for SH")));
        assertThat(javaDevTemplateResponse.getBody().getFacets().size(), is(equalTo(4)));

        HttpEntity<Void> getAllTemplates = new HttpEntity<>(headers);
        final ResponseEntity<List<SearchTemplateViewDto>> allTemplatesResponse = restTemplate.exchange("/v1/companies/" + COMPANY_GOOGLE + "/search-templates/", HttpMethod.GET, getAllTemplates,  new ParameterizedTypeReference<>() {});

        assertThat(allTemplatesResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(allTemplatesResponse.getBody().size(), is(equalTo(1)));

        HttpEntity<FacetAddDto> addSqlFacet = new HttpEntity<>(new FacetAddDto(TableName.SKILL, "SQL", "SQL", OperatorType.EQ), headers);
        final ResponseEntity<FacetViewDto> addSqlFacetResponse = restTemplate.exchange("/v1/companies/" + COMPANY_GOOGLE + "/search-templates/" + javaDevTemplateBody.getId() + "/facets", HttpMethod.POST, addSqlFacet, FacetViewDto.class);

        assertThat(addSqlFacetResponse.getStatusCode(), is(equalTo(HttpStatus.CREATED)));

        javaDevTemplateResponse = restTemplate.exchange("/v1/companies/" + COMPANY_GOOGLE + "/search-templates/" + javaDevTemplateBody.getId(), HttpMethod.GET, getJavaDevTemplate, SearchTemplateViewDto.class);

        assertThat(javaDevTemplateResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(javaDevTemplateResponse.getBody().getName(), is(equalTo("Senior java developer for SH")));
        assertThat(javaDevTemplateResponse.getBody().getFacets().size(), is(equalTo(5)));

        Long maxSalaryFacetId = javaDevTemplateBody.getFacets().stream().filter(facetViewDto -> facetViewDto.getCode().equals("SALARY") && facetViewDto.getValue().equals("2000")).findFirst().get().getId();
        FacetEditDto maxSalaryModified = new FacetEditDto(maxSalaryFacetId, TableName.TERM, "SALARY", "2500", OperatorType.LTE);
        HttpEntity<FacetEditDto> editMaxSalary = new HttpEntity<>(maxSalaryModified, headers);

        final ResponseEntity<FacetViewDto> editSqlFacetResponse = restTemplate.exchange("/v1/companies/" + COMPANY_GOOGLE + "/search-templates/" + javaDevTemplateBody.getId() + "/facets", HttpMethod.PUT, editMaxSalary, FacetViewDto.class);

        assertThat(editSqlFacetResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));

        Long minSalaryFacetId = javaDevTemplateBody.getFacets().stream().filter(facetViewDto -> facetViewDto.getCode().equals("SALARY") && facetViewDto.getValue().equals("1500")).findFirst().get().getId();
        HttpEntity<Void> removeMinSalary = new HttpEntity<>(headers);
        final ResponseEntity<Void> removeMinSalaryResponse = restTemplate.exchange("/v1/companies/" + COMPANY_GOOGLE + "/search-templates/" + javaDevTemplateBody.getId() + "/facets/" + minSalaryFacetId, HttpMethod.DELETE, removeMinSalary, Void.class);
        assertThat(removeMinSalaryResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));


        javaDevTemplateResponse = restTemplate.exchange("/v1/companies/" + COMPANY_GOOGLE + "/search-templates/" + javaDevTemplateBody.getId(), HttpMethod.GET, getJavaDevTemplate, SearchTemplateViewDto.class);

        assertThat(javaDevTemplateResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(javaDevTemplateResponse.getBody().getFacets().size(), is(equalTo(4)));

    }

}

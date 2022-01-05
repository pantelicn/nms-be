package com.opdev;

import com.opdev.common.services.Profiles;
import com.opdev.dto.LoginSuccessDto;
import com.opdev.model.term.TermType;
import com.opdev.term.dto.TermAddDto;
import com.opdev.term.dto.TermEditDto;
import com.opdev.term.dto.TermViewDto;
import com.opdev.util.CodeGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ActiveProfiles(Profiles.TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TermCrudIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DirtiesContext
    void testCrud() {
        ResponseEntity<LoginSuccessDto> loginResponse = login(ADMIN_GORAN, ADMIN_GORAN_PASSWORD);
        assertThat(loginResponse.getBody(), is(notNullValue()));
        final String token = loginResponse.getBody().getToken();
        final HttpHeaders headers = createAuthHeaders(token);
        final TermAddDto vacationDays = new TermAddDto("Vacation days", "Total number of vacation days.", TermType.STRING);
        final TermAddDto salary = new TermAddDto("Salary", "Monthly salary", TermType.BIGINT);

        TermViewDto createdVacationDaysTerm = addTerm(vacationDays, headers);
        TermViewDto createSalaryTerm = addTerm(salary, headers);

        findAll(List.of(createdVacationDaysTerm, createSalaryTerm), headers);

        final TermEditDto modifiedSalaryTerm = new TermEditDto(createSalaryTerm.getCode(), "Salary", "Monthly salary", TermType.BIGINT);

        TermViewDto newSalaryTerm = edit(modifiedSalaryTerm, headers);

        remove(newSalaryTerm.getCode(), headers);

        findAll(List.of(createdVacationDaysTerm), headers);
    }

    private TermViewDto addTerm(TermAddDto newTerm, HttpHeaders headers) {
        final HttpEntity<TermAddDto> httpEntityPOST = new HttpEntity<>(newTerm, headers);
        final ResponseEntity<TermViewDto> addTermResponse = restTemplate.exchange("/v1/terms", HttpMethod.POST, httpEntityPOST, TermViewDto.class);

        final TermViewDto createdTerm = addTermResponse.getBody();

        assertThat(addTermResponse.getStatusCode(), is(equalTo(HttpStatus.CREATED)));
        assertThat(createdTerm, is(notNullValue()));
        assertThat(CodeGenerator.generate(newTerm.getName()), is(equalTo(createdTerm.getCode())));
        assertThat(newTerm.getDescription(), is(equalTo(createdTerm.getDescription())));
        assertThat(newTerm.getName(), is(createdTerm.getName()));

        return createdTerm;
    }

    private void findAll(List<TermViewDto> expectedTerms, HttpHeaders headers) {
        final HttpEntity<Void> httpEntityGET = new HttpEntity<>(headers);
        final ResponseEntity<List<TermViewDto>> findAllTermsResponse = restTemplate.exchange("/v1/terms", HttpMethod.GET, httpEntityGET, new ParameterizedTypeReference<List<TermViewDto>>() {});

        final List<TermViewDto> found = findAllTermsResponse.getBody();

        assertThat(findAllTermsResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(found, is(notNullValue()));
        assertThat(found.size(), is(expectedTerms.size()));
        assertThat(found, is(expectedTerms));
    }

    private TermViewDto edit(TermEditDto modifiedTerm, HttpHeaders headers) {
        final HttpEntity<TermEditDto> httpEntityPUT = new HttpEntity<TermEditDto>(modifiedTerm, headers);
        final ResponseEntity<TermViewDto> editTermResponse = restTemplate.exchange("/v1/terms", HttpMethod.PUT, httpEntityPUT, TermViewDto.class);

        final TermViewDto newTerm = editTermResponse.getBody();

        assertThat(editTermResponse.getStatusCode(), is(HttpStatus.OK));
        assertThat(newTerm, is(notNullValue()));
        assertThat(newTerm.getCode(), is(modifiedTerm.getCode()));
        assertThat(newTerm.getDescription(), is(modifiedTerm.getDescription()));
        assertThat(newTerm.getName(), is(modifiedTerm.getName()));
        assertThat(newTerm.getType(), is(modifiedTerm.getType()));

        return newTerm;
    }

    private void remove(String code, HttpHeaders headers) {
        final HttpEntity<Void> httpEntityDELETE = new HttpEntity<>(headers);
        final ResponseEntity<Void> removeTermResponse = restTemplate.exchange("/v1/terms/" + code, HttpMethod.DELETE, httpEntityDELETE, Void.class);

        assertThat(removeTermResponse.getStatusCode(), is(HttpStatus.NO_CONTENT));
    }

}

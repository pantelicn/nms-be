package com.opdev;

import com.opdev.common.services.Profiles;
import com.opdev.model.term.TermType;
import com.opdev.talent.dto.TalentTermAddDto;
import com.opdev.talent.dto.TalentTermEditDto;
import com.opdev.talent.dto.TalentTermViewDto;
import com.opdev.term.dto.TermAddDto;
import com.opdev.term.dto.TermViewDto;
import com.opdev.util.CodeGenerator;
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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@ActiveProfiles(Profiles.TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TalentTermIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DirtiesContext
    void testCrud() {
        createAdmin();
        final HttpHeaders adminHeaders = createAuthHeaders(getTokenForAdmin());
        createTalent(TALENT_GORAN);
        final String token = getTokenForTalentGoran();
        final HttpHeaders headers = createAuthHeaders(token);

        final TermViewDto insurance = addTerm(new TermAddDto("Insurance", "Insurance", TermType.BOOLEAN), adminHeaders);
        final TermViewDto remoteWork = addTerm(new TermAddDto("Remote work", "Remote work", TermType.BOOLEAN), adminHeaders);

        final List<TalentTermViewDto> pantelaTerms = addTalentTerms(
                List.of(
                    new TalentTermAddDto("true", false, insurance.getCode()),
                    new TalentTermAddDto("true", true, remoteWork.getCode())),
                headers);

        final TalentTermViewDto insuranceTalentTerm = pantelaTerms.get(0);
        final TalentTermViewDto remoteTalentTerm = pantelaTerms.get(1);

        List<TalentTermViewDto> foundTalentTerms = getTalentTerms(TALENT_GORAN, headers);
        assertThat(foundTalentTerms.containsAll(pantelaTerms), is(equalTo(true)));

        updateTalentTerm(new TalentTermEditDto(insuranceTalentTerm.getId(), "false", false), headers);

        final List<TalentTermViewDto> updatedTalentTerms = deleteTalentTerm(remoteTalentTerm, headers);

        foundTalentTerms = getTalentTermsAsAdmin(adminHeaders);
        assertThat(foundTalentTerms.containsAll(updatedTalentTerms), is(equalTo(true)));

        final TalentTermViewDto foundTalentTerm = getTalentTermAsAdmin(insuranceTalentTerm.getId(), adminHeaders);
        assertThat(foundTalentTerm, is(equalTo(foundTalentTerms.get(0))));
    }

    private List<TalentTermViewDto> getTalentTermsAsAdmin(HttpHeaders headers) {
        final HttpEntity<Void> httpEntityGET = new HttpEntity<>(headers);
        ResponseEntity<List<TalentTermViewDto>> getTalentTermsResponse = restTemplate
                .exchange("/v1/admin/talents/" + TALENT_GORAN + "/terms", HttpMethod.GET,
                        httpEntityGET, new ParameterizedTypeReference<>() {});

        assertThat(getTalentTermsResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));

        final List<TalentTermViewDto> createdTalentTerms = getTalentTermsResponse.getBody();
        assertThat(getTalentTermsResponse, is(notNullValue()));

        return createdTalentTerms;
    }

    private TalentTermViewDto getTalentTermAsAdmin(Long id, HttpHeaders headers) {
        final HttpEntity<Void> httpEntityGET = new HttpEntity<>(headers);
        final ResponseEntity<TalentTermViewDto> getTalentTermResponse = restTemplate
                .exchange("/v1/admin/talents/" + TALENT_GORAN + "/terms/" + id, HttpMethod.GET,
                        httpEntityGET, TalentTermViewDto.class);

        assertThat(getTalentTermResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        return getTalentTermResponse.getBody();
    }

    private List<TalentTermViewDto> deleteTalentTerm(TalentTermViewDto talentTerm, HttpHeaders headers) {
        List<TalentTermViewDto> foundTalentTerms = getTalentTerms(TALENT_GORAN, headers);
        assertThat(foundTalentTerms.contains(talentTerm), is(equalTo(true)));

        final HttpEntity<Void> httpEntityDELETE = new HttpEntity<>(headers);
        ResponseEntity<Void> deleteTalentTermsResponse = restTemplate
                .exchange("/v1/talents/" + TALENT_GORAN + "/terms/" + talentTerm.getId(), HttpMethod.DELETE,
                        httpEntityDELETE, Void.class);

        assertThat(deleteTalentTermsResponse.getStatusCode(), is(equalTo(HttpStatus.NO_CONTENT)));

        foundTalentTerms = getTalentTerms(TALENT_GORAN, headers);
        assertThat(foundTalentTerms.contains(talentTerm), is(equalTo(false)));

        return foundTalentTerms;
    }

    private void updateTalentTerm(TalentTermEditDto modifiedTalentTerm, HttpHeaders headers) {
        final HttpEntity<TalentTermEditDto> httpEntityPUT = new HttpEntity<>(modifiedTalentTerm, headers);
        final ResponseEntity<TalentTermViewDto> updateTalentTermResponse = restTemplate
                .exchange("/v1/talents/" + TALENT_GORAN + "/terms",
                        HttpMethod.PUT,
                        httpEntityPUT,
                        TalentTermViewDto.class);

        assertThat(updateTalentTermResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        final TalentTermViewDto updatedTalentTerm = updateTalentTermResponse.getBody();

        assertThat(updatedTalentTerm, is(notNullValue()));
        assertThat(updatedTalentTerm.getNegotiable(), is(equalTo(modifiedTalentTerm.getNegotiable())));
        assertThat(updatedTalentTerm.getValue(), is(equalTo(modifiedTalentTerm.getValue())));
    }

    private List<TalentTermViewDto> addTalentTerms(List<TalentTermAddDto> newTalentTerms, HttpHeaders headers) {
        final HttpEntity<List<TalentTermAddDto>> httpEntityPOST = new HttpEntity<>(newTalentTerms, headers);
        final ResponseEntity<List<TalentTermViewDto>> addTalentTermResponse = restTemplate
                .exchange("/v1/talents/" + TALENT_GORAN + "/terms",
                        HttpMethod.POST,
                        httpEntityPOST,
                        new ParameterizedTypeReference<>() {});

        assertThat(addTalentTermResponse.getStatusCode(), is(equalTo(HttpStatus.CREATED)));

        final List<TalentTermViewDto> createdTalentTerms = addTalentTermResponse.getBody();

        assertThat(createdTalentTerms, is(notNullValue()));
        assertThat(newTalentTerms.size(), is(equalTo(createdTalentTerms.size())));
        assertThat(newTalentTerms.get(0).getValue(), is(equalTo(createdTalentTerms.get(0).getValue())));
        assertThat(newTalentTerms.get(0).getCode(), is(equalTo(createdTalentTerms.get(0).getTerm().getCode())));

        return createdTalentTerms;
    }

    private List<TalentTermViewDto> getTalentTerms(String username, HttpHeaders headers) {
        final HttpEntity<Void> httpEntityGET = new HttpEntity<>(headers);
        ResponseEntity<List<TalentTermViewDto>> getTalentTermsResponse = restTemplate
                .exchange("/v1/talents/" + username + "/terms", HttpMethod.GET,
                    httpEntityGET, new ParameterizedTypeReference<>() {});

        assertThat(getTalentTermsResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));

        final List<TalentTermViewDto> createdTalentTerms = getTalentTermsResponse.getBody();
        assertThat(getTalentTermsResponse, is(notNullValue()));

        return createdTalentTerms;
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

}

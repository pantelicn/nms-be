package com.opdev;

import com.opdev.common.services.Profiles;
import com.opdev.company.dto.RequestCreateDto;
import com.opdev.company.dto.RequestViewDto;
import com.opdev.company.dto.TalentTermRequestEditDto;
import com.opdev.company.dto.TermCreateDto;
import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.model.company.Company;
import com.opdev.model.request.RequestStatus;
import com.opdev.model.request.TalentTermRequestStatus;
import com.opdev.model.talent.Talent;
import com.opdev.model.term.TermType;
import com.opdev.repository.CompanyRepository;
import com.opdev.repository.TalentRepository;
import com.opdev.request.dto.RequestResponseDto;
import com.opdev.talent.dto.TalentTermAddDto;
import com.opdev.talent.dto.TalentTermViewDto;
import com.opdev.term.dto.TermAddDto;
import com.opdev.term.dto.TermViewDto;
import com.opdev.util.CodeGenerator;
import com.opdev.util.encoding.TalentIdEncoder;
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

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@ActiveProfiles(Profiles.TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TalentTermRequestIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TalentRepository talentRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private TalentIdEncoder talentIdEncoder;

    @Test
    @DirtiesContext
    public void editByTalentTest() {
        createAdmin();
        createTalent(TALENT_GORAN);
        createCompany(COMPANY_GOOGLE);
        final HttpHeaders adminHeaders = createAuthHeaders(getTokenForAdmin());

        final String nikolicaToken = getTokenForTalentGoran();
        final String prikolicaToken = getTokenForCompanyGoogle();

        final HttpHeaders nikolicaHeaders = createAuthHeaders(nikolicaToken);
        final HttpHeaders prikolicaHeaders = createAuthHeaders(prikolicaToken);

        final TermViewDto slide = addTerm(new TermAddDto("Slide", "Slide", TermType.BOOLEAN), adminHeaders);

        final List<TalentTermViewDto> nikolicaTerms = addTalentTerms(
                List.of(new TalentTermAddDto("true", true, slide.getCode())),
                nikolicaHeaders);

        final TalentTermViewDto slideTalentTerm = nikolicaTerms.get(0);

        TermCreateDto newTerm = TermCreateDto.builder()
                .termId(slideTalentTerm.getId())
                .status(TalentTermRequestStatus.COUNTER_OFFER_COMPANY)
                .counterOffer("false").build();
        RequestCreateDto newRequest = RequestCreateDto.builder()
                .talentId(getEncodedId(TALENT_GORAN, COMPANY_GOOGLE))
                .note("Note")
                .terms(List.of(newTerm)).build();
        final RequestViewDto request = addRequest(newRequest, prikolicaHeaders);

        final TalentTermRequestEditDto newTermRequestByTalent = TalentTermRequestEditDto.builder()
                .id(request.getTalentTermRequests().get(0).getId())
                .counterOffer("true")
                .status(TalentTermRequestStatus.COUNTER_OFFER_TALENT).build();

        final RequestResponseDto requestResponseByTalent = RequestResponseDto.builder()
                .requestId(request.getId())
                .newTermRequest(newTermRequestByTalent)
                .modifiedOn(request.getModifiedOn()).build();
        RequestViewDto updatedRequest = editByTalent(requestResponseByTalent, nikolicaHeaders);

        final TalentTermRequestEditDto newTermRequestByCompany = TalentTermRequestEditDto.builder()
                .id(request.getTalentTermRequests().get(0).getId())
                .counterOffer("false")
                .status(TalentTermRequestStatus.COUNTER_OFFER_COMPANY).build();

        final RequestResponseDto requestResponseByCompany = RequestResponseDto.builder()
                .requestId(request.getId())
                .newTermRequest(newTermRequestByCompany)
                .modifiedOn(updatedRequest.getModifiedOn()).build();
        editByCompany(requestResponseByCompany, prikolicaHeaders);
    }

    RequestViewDto editByTalent(RequestResponseDto requestResponse, HttpHeaders headers) {
        final HttpEntity<RequestResponseDto> httpEntityPUT = new HttpEntity<>(requestResponse, headers);
        final ResponseEntity<RequestViewDto> editByTalentResponse = restTemplate
                .exchange("/v1/talents/" + TALENT_GORAN + "/talent-term-requests",
                        HttpMethod.PUT,
                        httpEntityPUT,
                        RequestViewDto.class);

        assertThat(editByTalentResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));

        final RequestViewDto updatedRequest = editByTalentResponse.getBody();

        assertThat(updatedRequest, is(notNullValue()));
        assertThat(updatedRequest.getStatus(), is(equalTo(RequestStatus.COUNTER_OFFER_TALENT)));
        assertThat(updatedRequest.getTalentTermRequests(), is(notNullValue()));
        assertThat(updatedRequest.getTalentTermRequests().get(0).getStatus(), is(equalTo(TalentTermRequestStatus.COUNTER_OFFER_TALENT)));
        assertThat(updatedRequest.getTalentTermRequests().get(0).getCounterOffer(), is(equalTo(requestResponse.getNewTermRequest().getCounterOffer())));

        return updatedRequest;
    }

    RequestViewDto editByCompany(RequestResponseDto requestResponse, HttpHeaders headers) {
        final HttpEntity<RequestResponseDto> httpEntityPUT = new HttpEntity<>(requestResponse, headers);
        final ResponseEntity<RequestViewDto> editByTalentResponse = restTemplate
                .exchange("/v1/companies/" + COMPANY_GOOGLE + "/talent-term-requests",
                        HttpMethod.PUT,
                        httpEntityPUT,
                        RequestViewDto.class);

        assertThat(editByTalentResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));

        final RequestViewDto updatedRequest = editByTalentResponse.getBody();

        assertThat(updatedRequest, is(notNullValue()));
        assertThat(updatedRequest.getStatus(), is(equalTo(RequestStatus.COUNTER_OFFER_COMPANY)));
        assertThat(updatedRequest.getTalentTermRequests(), is(notNullValue()));
        assertThat(updatedRequest.getTalentTermRequests().get(0).getStatus(), is(equalTo(TalentTermRequestStatus.COUNTER_OFFER_COMPANY)));
        assertThat(updatedRequest.getTalentTermRequests().get(0).getCounterOffer(), is(equalTo(requestResponse.getNewTermRequest().getCounterOffer())));

        return updatedRequest;
    }

    private String getEncodedId(String talentUsername, String companyUsername) {
        Talent talent = talentRepository.findByUserUsername(talentUsername)
                .orElseThrow(() -> ApiEntityNotFoundException.builder().build());
        Company company = companyRepository.findByUserUsername(companyUsername)
                .orElseThrow(() -> ApiEntityNotFoundException.builder().build());
        return talentIdEncoder.encode(talent.getId(), company.getId());
    }

    private List<TalentTermViewDto> addTalentTerms(List<TalentTermAddDto> newTalentTerms, HttpHeaders headers) {
        final HttpEntity<List<TalentTermAddDto>> httpEntityPOST = new HttpEntity<>(newTalentTerms, headers);
        final ResponseEntity<List<TalentTermViewDto>> addTalentTermResponse = restTemplate
                .exchange("/v1/talents/" + TALENT_GORAN + "/terms",
                        HttpMethod.POST,
                        httpEntityPOST,
                        new ParameterizedTypeReference<>() {
                        });

        assertThat(addTalentTermResponse.getStatusCode(), is(equalTo(HttpStatus.CREATED)));

        final List<TalentTermViewDto> createdTalentTerms = addTalentTermResponse.getBody();

        assertThat(createdTalentTerms, is(notNullValue()));
        assertThat(newTalentTerms.size(), is(equalTo(createdTalentTerms.size())));
        assertThat(newTalentTerms.get(0).getValue(), is(equalTo(createdTalentTerms.get(0).getValue())));
        assertThat(newTalentTerms.get(0).getCode(), is(equalTo(createdTalentTerms.get(0).getTerm().getCode())));

        return createdTalentTerms;
    }

    private RequestViewDto addRequest(RequestCreateDto newRequest, HttpHeaders companyHeaders) {
        final HttpEntity<RequestCreateDto> httpEntityPOST = new HttpEntity<>(newRequest, companyHeaders);
        final ResponseEntity<RequestViewDto> addRequestResponse = restTemplate
                .exchange("/v1/companies/" + COMPANY_GOOGLE + "/requests",
                        HttpMethod.POST,
                        httpEntityPOST,
                        RequestViewDto.class);
        assertThat(addRequestResponse.getStatusCode(), is(equalTo(HttpStatus.CREATED)));

        final RequestViewDto createdRequest = addRequestResponse.getBody();

        assertThat(createdRequest, is(notNullValue()));
        assertThat(createdRequest.getNote(), is(equalTo(newRequest.getNote())));

        return createdRequest;
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

package com.opdev;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.ArrayList;
import java.util.List;

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
import com.opdev.company.dto.RequestCreateDto;
import com.opdev.company.dto.RequestViewDto;
import com.opdev.company.dto.TermCreateDto;
import com.opdev.model.request.Request;
import com.opdev.model.request.RequestStatus;
import com.opdev.model.request.TalentTermRequestStatus;
import com.opdev.model.talent.Talent;
import com.opdev.model.term.TalentTerm;
import com.opdev.model.term.Term;
import com.opdev.model.term.TermType;
import com.opdev.repository.CompanyRepository;
import com.opdev.repository.RequestRepository;
import com.opdev.repository.TalentRepository;
import com.opdev.repository.TalentTermRepository;
import com.opdev.repository.TermRepository;
import com.opdev.util.SimplePageImpl;
import com.opdev.util.encoding.aes.AESTalentIdEncoder;

@ActiveProfiles(Profiles.TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompanyRequestControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TermRepository termRepository;

    @Autowired
    private TalentRepository talentRepository;

    @Autowired
    private AESTalentIdEncoder talentIdEncoder;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private TalentTermRepository talentTermRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Test
    @DirtiesContext
    public void createFindPendingAndRemoveRequest() {
        createTalent(TALENT_GORAN);
        createCompany(COMPANY_GOOGLE);

        String googleToken = getTokenForCompanyGoogle();

        Term sallaryTerm = Term.builder()
                .type(TermType.INT)
                .description("Sallary")
                .code("SALLARY")
                .name("Sallary")
                .build();

        termRepository.save(sallaryTerm);

        Talent talentGoran = talentRepository.findByUserUsername(TALENT_GORAN).get();
        TalentTerm talentTermSallary = TalentTerm.builder()
                .talent(talentGoran)
                .term(sallaryTerm)
                .negotiable(false)
                .value("1000")
                .build();

        talentTermSallary = talentTermRepository.save(talentTermSallary);

        talentGoran = talentRepository.save(talentGoran);

        RequestViewDto createdRequest = createRequest(talentGoran.getId(), COMPANY_GOOGLE, talentTermSallary.getId(), googleToken);

        getPendingRequests(COMPANY_GOOGLE, googleToken, 1);

        Request request = requestRepository.findById(createdRequest.getId()).get();
        request.setStatus(RequestStatus.COUNTER_OFFER_TALENT);
        request = requestRepository.save(request);

        getCounterOffer(COMPANY_GOOGLE, googleToken, 1);

        request.setStatus(RequestStatus.ACCEPTED);
        requestRepository.save(request);

        getAccepted(COMPANY_GOOGLE, googleToken, 1);

        removeRequest(COMPANY_GOOGLE, googleToken, createdRequest.getId());

        getPendingRequests(COMPANY_GOOGLE, googleToken, 0);
    }

    private void removeRequest(String companyUsername, String token, Long requestId) {
        HttpHeaders headers = createAuthHeaders(token);
        HttpEntity<RequestCreateDto> httpEntityDelete = new HttpEntity<>(headers);
        ResponseEntity<Void> deleteResponse = restTemplate.exchange("/v1/companies/" + companyUsername + "/requests/" + requestId, HttpMethod.DELETE, httpEntityDelete,
                                                                                 Void.class);

        assertThat(deleteResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
    }

    private List<RequestViewDto> getPendingRequests(String companyUsername, String token, int expectedRequests) {
        HttpHeaders headers = createAuthHeaders(token);
        HttpEntity<RequestCreateDto> httpEntityGet = new HttpEntity<>(headers);
        ResponseEntity<SimplePageImpl<RequestViewDto>> getResponse = restTemplate.exchange("/v1/companies/" + companyUsername + "/requests/pending", HttpMethod.GET, httpEntityGet,
                                                                                           new ParameterizedTypeReference<>() {});

        assertThat(getResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(getResponse.getBody(), is(notNullValue()));
        assertThat(getResponse.getBody().getContent().size(), is(equalTo(expectedRequests)));

        return getResponse.getBody().getContent();
    }

    private List<RequestViewDto> getCounterOffer(String companyUsername, String token, int expectedRequests) {
        HttpHeaders headers = createAuthHeaders(token);
        HttpEntity<RequestCreateDto> httpEntityGet = new HttpEntity<>(headers);
        ResponseEntity<SimplePageImpl<RequestViewDto>> getResponse = restTemplate.exchange("/v1/companies/" + companyUsername + "/requests/counter-offers", HttpMethod.GET, httpEntityGet,
                                                                                 new ParameterizedTypeReference<>() {});

        assertThat(getResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(getResponse.getBody(), is(notNullValue()));
        assertThat(getResponse.getBody().getContent().size(), is(equalTo(expectedRequests)));

        return getResponse.getBody().getContent();
    }

    private List<RequestViewDto> getAccepted(String companyUsername, String token, int expectedRequests) {
        HttpHeaders headers = createAuthHeaders(token);
        HttpEntity<RequestCreateDto> httpEntityGet = new HttpEntity<>(headers);
        ResponseEntity<SimplePageImpl<RequestViewDto>> getResponse = restTemplate.exchange("/v1/companies/" + companyUsername + "/requests/accepted", HttpMethod.GET, httpEntityGet,
                                                                                 new ParameterizedTypeReference<>() {});

        assertThat(getResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(getResponse.getBody(), is(notNullValue()));
        assertThat(getResponse.getBody().getContent().size(), is(equalTo(expectedRequests)));

        return getResponse.getBody().getContent();
    }

    private RequestViewDto createRequest(Long talentId, String companyUsername, Long talentTermId, String token) {
        List<TermCreateDto> terms = new ArrayList<>();
        TermCreateDto salarryTerm = TermCreateDto.builder()
                .termId(talentTermId)
                .status(TalentTermRequestStatus.ACCEPTED)
                .build();
        terms.add(salarryTerm);
        RequestCreateDto requestCreateDto = RequestCreateDto.builder()
                .note("Java developer")
                .talentId(talentIdEncoder.encode(talentId, companyRepository.findByUserUsername(companyUsername).get().getId()))
                .terms(terms)
                .build();

        HttpHeaders headers = createAuthHeaders(token);
        HttpEntity<RequestCreateDto> httpEntityPOST = new HttpEntity<>(requestCreateDto, headers);
        ResponseEntity<RequestViewDto> addResponse = restTemplate.exchange("/v1/companies/" + companyUsername + "/requests", HttpMethod.POST,
                                                                           httpEntityPOST, RequestViewDto.class);

        assertThat(addResponse.getStatusCode(), is(equalTo(HttpStatus.CREATED)));
        assertThat(addResponse.getBody(), is(notNullValue()));
        assertThat(addResponse.getBody().getStatus(), is(equalTo(RequestStatus.PENDING)));

        return addResponse.getBody();
    }

}
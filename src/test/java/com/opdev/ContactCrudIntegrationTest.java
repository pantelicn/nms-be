package com.opdev;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;

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

import com.opdev.common.services.Profiles;
import com.opdev.contact.dto.ContactAddDto;
import com.opdev.contact.dto.ContactEditDto;
import com.opdev.contact.dto.ContactViewDto;
import com.opdev.model.contact.ContactType;

@ActiveProfiles(Profiles.TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ContactCrudIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DirtiesContext
    void addUpdateGetDeleteTalentContactTest() {
        createTalent(TALENT_GORAN);
        String goranToken = getTokenForTalentGoran();

        HttpHeaders headers = createAuthHeaders(goranToken);

        ContactAddDto contactMobileMarko = ContactAddDto.builder()
                .type(ContactType.MOBILE_PHONE)
                .value("00381631758749").build();

        HttpEntity<ContactAddDto> httpEntityPOST = new HttpEntity<>(contactMobileMarko, headers);
        ResponseEntity<ContactViewDto> addResponse = restTemplate.exchange("/v1/contacts", HttpMethod.POST,
                                                                                 httpEntityPOST, ContactViewDto.class);

        assertThat(addResponse.getStatusCode(), is(equalTo(HttpStatus.CREATED)));
        assertThat(addResponse.getBody(), is(notNullValue()));
        assertThat(addResponse.getBody().getId(), is(notNullValue()));
        assertThat(addResponse.getBody().getType(), is(equalTo(contactMobileMarko.getType())));
        assertThat(addResponse.getBody().getValue(), is(equalTo(contactMobileMarko.getValue())));

        ContactEditDto contactMarkoEditDto = ContactEditDto.builder().id(addResponse.getBody().getId())
                .type(ContactType.MOBILE_PHONE)
                .value("00381631758733").build();

        HttpEntity<ContactEditDto> httpEntityPUT = new HttpEntity<>(contactMarkoEditDto, headers);
        ResponseEntity<ContactViewDto> editResponse = restTemplate.exchange("/v1/contacts", HttpMethod.PUT,
                                                                                 httpEntityPUT, ContactViewDto.class);

        assertThat(editResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(editResponse.getBody(), is(notNullValue()));
        assertThat(editResponse.getBody().getId(), is(equalTo(contactMarkoEditDto.getId())));
        assertThat(editResponse.getBody().getType(), is(equalTo(contactMarkoEditDto.getType())));
        assertThat(editResponse.getBody().getValue(), is(equalTo(contactMarkoEditDto.getValue())));

        createTalent(TALENT_NIKOLA);
        String nikolaToken = getTokenForTalentNikola();

        headers = createAuthHeaders(nikolaToken);

        httpEntityPUT = new HttpEntity<>(contactMarkoEditDto, headers);
        editResponse = restTemplate.exchange("/v1/contacts", HttpMethod.PUT, httpEntityPUT, ContactViewDto.class);

        assertThat(editResponse.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));

        headers = createAuthHeaders(goranToken);
        HttpEntity<List<ContactViewDto>> httpEntityGet = new HttpEntity<>(headers);
        ResponseEntity<List<ContactViewDto>> markoContacts = restTemplate.exchange("/v1/contacts", HttpMethod.GET, httpEntityGet,
                                            new ParameterizedTypeReference<List<ContactViewDto>>() {});

        assertThat(markoContacts.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(markoContacts.getBody(), is(notNullValue()));
        assertThat(markoContacts.getBody().size(), is(equalTo(1)));

        HttpEntity<Void> httpEntityDelete = new HttpEntity<>(headers);
        ResponseEntity<Void> deleteMarkoContactResponse = restTemplate.exchange("/v1/contacts/" + markoContacts.getBody().get(0).getId(), HttpMethod.DELETE, httpEntityDelete, Void.class);

        assertThat(deleteMarkoContactResponse.getStatusCode(), is(equalTo(HttpStatus.NO_CONTENT)));

        httpEntityGet = new HttpEntity<>(headers);
        markoContacts = restTemplate.exchange("/v1/contacts", HttpMethod.GET, httpEntityGet,
                                                                                   new ParameterizedTypeReference<List<ContactViewDto>>() {});

        assertThat(markoContacts.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(markoContacts.getBody(), is(notNullValue()));
        assertThat(markoContacts.getBody().size(), is(equalTo(0)));

    }

    @Test
    @DirtiesContext
    void addUpdateGetDeleteCompanyContactTest() {
        createCompany(COMPANY_GOOGLE);
        String token = getTokenForCompanyGoogle();

        HttpHeaders headers = createAuthHeaders(token);

        ContactAddDto contactHybridMobilePhone = ContactAddDto.builder()
                .type(ContactType.MOBILE_PHONE)
                .value("00381631758749").build();

        HttpEntity<ContactAddDto> httpEntityPOST = new HttpEntity<>(contactHybridMobilePhone, headers);
        ResponseEntity<ContactViewDto> addResponse = restTemplate.exchange("/v1/contacts", HttpMethod.POST,
                                                                           httpEntityPOST, ContactViewDto.class);

        assertThat(addResponse.getStatusCode(), is(equalTo(HttpStatus.CREATED)));
        assertThat(addResponse.getBody(), is(notNullValue()));
        assertThat(addResponse.getBody().getId(), is(notNullValue()));
        assertThat(addResponse.getBody().getType(), is(equalTo(contactHybridMobilePhone.getType())));
        assertThat(addResponse.getBody().getValue(), is(equalTo(contactHybridMobilePhone.getValue())));

        ContactEditDto hybridEditcontactDto = ContactEditDto.builder().id(addResponse.getBody().getId())
                .type(ContactType.MOBILE_PHONE)
                .value("00381631758733").build();

        HttpEntity<ContactEditDto> httpEntityPUT = new HttpEntity<>(hybridEditcontactDto, headers);
        ResponseEntity<ContactViewDto> editResponse = restTemplate.exchange("/v1/contacts", HttpMethod.PUT,
                                                                            httpEntityPUT, ContactViewDto.class);

        assertThat(editResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(editResponse.getBody(), is(notNullValue()));
        assertThat(editResponse.getBody().getId(), is(equalTo(hybridEditcontactDto.getId())));
        assertThat(editResponse.getBody().getType(), is(equalTo(hybridEditcontactDto.getType())));
        assertThat(editResponse.getBody().getValue(), is(equalTo(hybridEditcontactDto.getValue())));

        createTalent(TALENT_GORAN);

        headers = createAuthHeaders(getTokenForTalentGoran());

        httpEntityPUT = new HttpEntity<>(hybridEditcontactDto, headers);
        editResponse = restTemplate.exchange("/v1/contacts", HttpMethod.PUT, httpEntityPUT, ContactViewDto.class);

        assertThat(editResponse.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));

        headers = createAuthHeaders(token);
        HttpEntity<List<ContactViewDto>> httpEntityGet = new HttpEntity<>(headers);
        ResponseEntity<List<ContactViewDto>> hybridContacts = restTemplate.exchange("/v1/contacts", HttpMethod.GET, httpEntityGet,
                                                                                   new ParameterizedTypeReference<List<ContactViewDto>>() {});

        assertThat(hybridContacts.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(hybridContacts.getBody(), is(notNullValue()));
        assertThat(hybridContacts.getBody().size(), is(equalTo(1)));

        HttpEntity<Void> httpEntityDelete = new HttpEntity<>(headers);
        ResponseEntity<Void> deleteMarkoContactResponse = restTemplate.exchange("/v1/contacts/" + hybridContacts.getBody().get(0).getId(), HttpMethod.DELETE, httpEntityDelete, Void.class);

        assertThat(deleteMarkoContactResponse.getStatusCode(), is(equalTo(HttpStatus.NO_CONTENT)));

        httpEntityGet = new HttpEntity<>(headers);
        hybridContacts = restTemplate.exchange("/v1/contacts", HttpMethod.GET, httpEntityGet,
                                              new ParameterizedTypeReference<List<ContactViewDto>>() {});

        assertThat(hybridContacts.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(hybridContacts.getBody(), is(notNullValue()));
        assertThat(hybridContacts.getBody().size(), is(equalTo(0)));
    }

}

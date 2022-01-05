package com.opdev;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;

import com.opdev.company.dto.CompanyRegistrationDto;
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

import com.opdev.common.services.Profiles;
import com.opdev.company.message.dto.MessageSendDto;
import com.opdev.company.message.dto.MessageViewDto;
import com.opdev.dto.LoginSuccessDto;
import com.opdev.dto.TalentRegistrationDto;

@ActiveProfiles(Profiles.TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Rollback
public class ChatCrudIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void messageCrud() {
        final CompanyRegistrationDto googleCompany = createNewCompany(DEFAULT_COMPANY_USERNAME);
        ResponseEntity<Void> registerResponse = registerCompany(googleCompany);
        assertThat(registerResponse.getStatusCode(), is(equalTo(HttpStatus.CREATED)));
        assertThat(registerResponse.getHeaders().getLocation().getRawPath(), containsString("login"));

        ResponseEntity<LoginSuccessDto> loginResponse = login(googleCompany.getUsername(), googleCompany.getPassword());
        String token = loginResponse.getBody().getToken();
        HttpHeaders headers = createAuthHeaders(token);

        TalentRegistrationDto goranTalent = createNewTalent("goran@gmail.com");
        registerTalent(goranTalent);

        MessageViewDto sentMessage = companySendMessage(googleCompany.getUsername(), "job offer", goranTalent.getUsername(), headers);

        loginResponse = login(goranTalent.getUsername(), goranTalent.getPassword());
        token = loginResponse.getBody().getToken();
        headers = createAuthHeaders(token);

        List<com.opdev.talent.message.dto.MessageViewDto> talentChats = talentGetAllChats(goranTalent.getUsername(), headers, 1);

        talentGetChatContent(goranTalent.getUsername(), talentChats.get(0).getId(), headers, 1);

        talentSendMessage(goranTalent.getUsername(), "not interested", googleCompany.getUsername(), headers);

        loginResponse = login(googleCompany.getUsername(), googleCompany.getPassword());
        token = loginResponse.getBody().getToken();
        headers = createAuthHeaders(token);

        List<MessageViewDto> companyChats = companyGetAllChats(googleCompany.getUsername(), headers, 1);

        companyGetChatContent(googleCompany.getUsername(), companyChats.get(0).getId(), headers, 2);


        TalentRegistrationDto nikolaTalent = createNewTalent("nikola@gmail.com");
        registerTalent(nikolaTalent);

        sentMessage = companySendMessage(googleCompany.getUsername(), "job offer", nikolaTalent.getUsername(), headers);

        loginResponse = login(nikolaTalent.getUsername(), nikolaTalent.getPassword());
        token = loginResponse.getBody().getToken();
        headers = createAuthHeaders(token);

        talentChats = talentGetAllChats(nikolaTalent.getUsername(), headers, 1);

        talentGetChatContent(nikolaTalent.getUsername(), talentChats.get(0).getId(), headers, 1);

        talentSendMessage(nikolaTalent.getUsername(), "not interested", googleCompany.getUsername(), headers);

        loginResponse = login(googleCompany.getUsername(), googleCompany.getPassword());
        token = loginResponse.getBody().getToken();
        headers = createAuthHeaders(token);

        companyChats = companyGetAllChats(googleCompany.getUsername(), headers, 2);

        companyGetChatContent(googleCompany.getUsername(), companyChats.get(1).getId(), headers, 2);

        final CompanyRegistrationDto facebookCompany = createNewCompany("facebook@facebook.com");
        registerResponse = registerCompany(facebookCompany);
        assertThat(registerResponse.getStatusCode(), is(equalTo(HttpStatus.CREATED)));
        assertThat(registerResponse.getHeaders().getLocation().getRawPath(), containsString("login"));

        loginResponse = login(facebookCompany.getUsername(), facebookCompany.getPassword());
        token = loginResponse.getBody().getToken();
        headers = createAuthHeaders(token);

        sentMessage = companySendMessage(facebookCompany.getUsername(), "job offer", goranTalent.getUsername(), headers);

        loginResponse = login(goranTalent.getUsername(), goranTalent.getPassword());
        token = loginResponse.getBody().getToken();
        headers = createAuthHeaders(token);

        talentChats = talentGetAllChats(goranTalent.getUsername(), headers, 2);

        talentGetChatContent(goranTalent.getUsername(), talentChats.get(0).getId(), headers, 1);

        talentSendMessage(goranTalent.getUsername(), "not interested", facebookCompany.getUsername(), headers);

        loginResponse = login(facebookCompany.getUsername(), facebookCompany.getPassword());
        token = loginResponse.getBody().getToken();
        headers = createAuthHeaders(token);

        companyChats = companyGetAllChats(facebookCompany.getUsername(), headers, 1);

        companyGetChatContent(facebookCompany.getUsername(), companyChats.get(0).getId(), headers, 2);

        sentMessage = companySendMessage(facebookCompany.getUsername(), "job offer", nikolaTalent.getUsername(), headers);

        loginResponse = login(nikolaTalent.getUsername(), nikolaTalent.getPassword());
        token = loginResponse.getBody().getToken();
        headers = createAuthHeaders(token);

        talentChats = talentGetAllChats(nikolaTalent.getUsername(), headers, 2);

        talentGetChatContent(nikolaTalent.getUsername(), talentChats.get(0).getId(), headers, 1);

        talentSendMessage(nikolaTalent.getUsername(), "not interested", facebookCompany.getUsername(), headers);

        loginResponse = login(facebookCompany.getUsername(), facebookCompany.getPassword());
        token = loginResponse.getBody().getToken();
        headers = createAuthHeaders(token);

        companyChats = companyGetAllChats(facebookCompany.getUsername(), headers, 2);

        companyGetChatContent(facebookCompany.getUsername(), companyChats.get(1).getId(), headers, 2);
    }

    private MessageViewDto companySendMessage(String companyUsername, String messageContent, String talentUsername, HttpHeaders headers) {
        MessageSendDto message = new MessageSendDto(messageContent, talentUsername);

        final HttpEntity<MessageSendDto> httpEntityPOST = new HttpEntity<>(message, headers);
        final ResponseEntity<MessageViewDto> sentMessage = restTemplate.exchange("/v1/companies/" + companyUsername + "/chats", HttpMethod.POST, httpEntityPOST, MessageViewDto.class);

        MessageViewDto sentMessageBody = sentMessage.getBody();
        assertThat(sentMessage.getStatusCode(), is(equalTo(HttpStatus.CREATED)));
        assertThat(sentMessageBody, is(notNullValue()));
        assertThat(sentMessageBody.getContent(), is(equalTo(message.getContent())));
        assertThat(sentMessageBody.getTalentUsername(), is(equalTo(message.getTalentUsername())));

        return sentMessageBody;
    }

    private com.opdev.talent.message.dto.MessageViewDto talentSendMessage(String talentUsername, String messageContent, String companyUsername, HttpHeaders headers) {
        com.opdev.talent.message.dto.MessageSendDto talentMessage = new com.opdev.talent.message.dto.MessageSendDto(messageContent, companyUsername);
        HttpEntity<com.opdev.talent.message.dto.MessageSendDto> talentSendMessage = new HttpEntity<>(talentMessage, headers);

        ResponseEntity<com.opdev.talent.message.dto.MessageViewDto> talentSentMessage = restTemplate.exchange("/v1/talents/" + talentUsername + "/chats", HttpMethod.POST, talentSendMessage, com.opdev.talent.message.dto.MessageViewDto.class);

        assertThat(talentSentMessage, is(notNullValue()));
        assertThat(talentSentMessage.getStatusCode(), is(equalTo(HttpStatus.CREATED)));
        com.opdev.talent.message.dto.MessageViewDto talentSentMessageBody = talentSentMessage.getBody();
        assertThat(talentSentMessageBody, is(notNullValue()));
        assertThat(talentSentMessageBody.getContent(), is(equalTo(talentMessage.getContent())));
        assertThat(talentSentMessageBody.getCompanyUsername(), is(equalTo(talentMessage.getCompanyUsername())));

        return talentSentMessageBody;
    }

    private List<com.opdev.talent.message.dto.MessageViewDto> talentGetAllChats(String talentUsername, HttpHeaders headers, int expectedChats) {
        HttpEntity<Void> talentGetChats = new HttpEntity<>(headers);
        ResponseEntity<List<com.opdev.talent.message.dto.MessageViewDto>> talentMessagesResponse = restTemplate.exchange("/v1/talents/" + talentUsername + "/chats", HttpMethod.GET, talentGetChats, new ParameterizedTypeReference<List<com.opdev.talent.message.dto.MessageViewDto>>() {});

        List<com.opdev.talent.message.dto.MessageViewDto> talentChats = talentMessagesResponse.getBody();
        assertThat(talentMessagesResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(talentChats, is(notNullValue()));
        assertThat(talentChats.size(), is(equalTo(expectedChats)));

        return talentChats;
    }

    private List<MessageViewDto> companyGetAllChats(String companyUsername, HttpHeaders headers, int expectedNumberOfChats) {
        HttpEntity<Void> companyGetChats = new HttpEntity<>(headers);
        ResponseEntity<List<MessageViewDto>> companyMessagesResponse = restTemplate.exchange("/v1/companies/" + companyUsername + "/chats", HttpMethod.GET, companyGetChats, new ParameterizedTypeReference<List<MessageViewDto>>() {});

        List<MessageViewDto> companyChats = companyMessagesResponse.getBody();
        assertThat(companyMessagesResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(companyChats, is(notNullValue()));
        assertThat(companyChats.size(), is(equalTo(expectedNumberOfChats)));

        return companyChats;
    }

    private List<com.opdev.talent.message.dto.MessageViewDto> talentGetChatContent(String talentUsername, Long lastMessageId, HttpHeaders headers, int expectedNumberOfMessages) {
        HttpEntity<Void> talentGetChatContent = new HttpEntity<>(headers);
        ResponseEntity<List<com.opdev.talent.message.dto.MessageViewDto>> talentChatContentResponse = restTemplate.exchange("/v1/talents/" + talentUsername + "/chats/" + lastMessageId, HttpMethod.GET, talentGetChatContent, new ParameterizedTypeReference<List<com.opdev.talent.message.dto.MessageViewDto>>() {});

        assertThat(talentChatContentResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        List<com.opdev.talent.message.dto.MessageViewDto> talentChatContent = talentChatContentResponse.getBody();
        assertThat(talentChatContent, is(notNullValue()));
        assertThat(talentChatContent.size(), is(expectedNumberOfMessages));
        assertThat(talentChatContent.get(0).isSeen(), is(true));

        return talentChatContent;
    }

    private List<MessageViewDto> companyGetChatContent(String companyUsername, Long lastMessageId, HttpHeaders headers, int expectedNumberOfMessages) {
        HttpEntity<Void> companyGetChatContent = new HttpEntity<>(headers);
        ResponseEntity<List<MessageViewDto>> companyChatContentResponse = restTemplate.exchange("/v1/companies/" + companyUsername + "/chats/" + lastMessageId, HttpMethod.GET, companyGetChatContent, new ParameterizedTypeReference<List<MessageViewDto>>() {});

        assertThat(companyChatContentResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        List<MessageViewDto> companyChatContent = companyChatContentResponse.getBody();
        assertThat(companyChatContent, is(notNullValue()));
        assertThat(companyChatContent.size(), is(expectedNumberOfMessages));
        assertThat(companyChatContent.get(0).isSeen(), is(true));

        return companyChatContent;
    }

}

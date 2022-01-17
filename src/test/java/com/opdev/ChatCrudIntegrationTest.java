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
import com.opdev.company.message.dto.MessageSendDto;
import com.opdev.company.message.dto.MessageViewDto;

@ActiveProfiles(Profiles.TEST_PROFILE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatCrudIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DirtiesContext
    public void messageCrud() {
        createCompany(COMPANY_GOOGLE);
        String googleToken = getTokenForCompanyGoogle();
        HttpHeaders headers = createAuthHeaders(googleToken);

        createTalent(TALENT_GORAN);

        MessageViewDto sentMessage = companySendMessage(COMPANY_GOOGLE, "job offer", TALENT_GORAN, headers);

        String goranToken = getTokenForTalentGoran();
        headers = createAuthHeaders(goranToken);

        List<com.opdev.talent.message.dto.MessageViewDto> talentChats = talentGetAllChats(TALENT_GORAN, headers, 1);

        talentGetChatContent(TALENT_GORAN, talentChats.get(0).getId(), headers, 1);

        talentSendMessage(TALENT_GORAN, "not interested", COMPANY_GOOGLE, headers);

        headers = createAuthHeaders(googleToken);

        List<MessageViewDto> companyChats = companyGetAllChats(COMPANY_GOOGLE, headers, 1);

        companyGetChatContent(COMPANY_GOOGLE, companyChats.get(0).getId(), headers, 2);


        createTalent(TALENT_NIKOLA);

        sentMessage = companySendMessage(COMPANY_GOOGLE, "job offer", TALENT_NIKOLA, headers);

        String nikolaToken = getTokenForTalentNikola();
        headers = createAuthHeaders(nikolaToken);

        talentChats = talentGetAllChats(TALENT_NIKOLA, headers, 1);

        talentGetChatContent(TALENT_NIKOLA, talentChats.get(0).getId(), headers, 1);

        talentSendMessage(TALENT_NIKOLA, "not interested", COMPANY_GOOGLE, headers);

        headers = createAuthHeaders(googleToken);

        companyChats = companyGetAllChats(COMPANY_GOOGLE, headers, 2);

        companyGetChatContent(COMPANY_GOOGLE, companyChats.get(1).getId(), headers, 2);

        createCompany(COMPANY_FACEBOOK);

        String facebookToken = getTokenForCompanyFacebook();
        headers = createAuthHeaders(facebookToken);

        sentMessage = companySendMessage(COMPANY_FACEBOOK, "job offer", TALENT_GORAN, headers);

        headers = createAuthHeaders(goranToken);

        talentChats = talentGetAllChats(TALENT_GORAN, headers, 2);

        talentGetChatContent(TALENT_GORAN, talentChats.get(0).getId(), headers, 1);

        talentSendMessage(TALENT_GORAN, "not interested", COMPANY_FACEBOOK, headers);

        headers = createAuthHeaders(facebookToken);

        companyChats = companyGetAllChats(COMPANY_FACEBOOK, headers, 1);

        companyGetChatContent(COMPANY_FACEBOOK, companyChats.get(0).getId(), headers, 2);

        sentMessage = companySendMessage(COMPANY_FACEBOOK, "job offer", TALENT_NIKOLA, headers);

        headers = createAuthHeaders(nikolaToken);

        talentChats = talentGetAllChats(TALENT_NIKOLA, headers, 2);

        talentGetChatContent(TALENT_NIKOLA, talentChats.get(0).getId(), headers, 1);

        talentSendMessage(TALENT_NIKOLA, "not interested", COMPANY_FACEBOOK, headers);

        headers = createAuthHeaders(facebookToken);

        companyChats = companyGetAllChats(COMPANY_FACEBOOK, headers, 2);

        companyGetChatContent(COMPANY_FACEBOOK, companyChats.get(1).getId(), headers, 2);
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

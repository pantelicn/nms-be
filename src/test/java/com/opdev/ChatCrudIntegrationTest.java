package com.opdev;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;

import com.opdev.company.message.dto.LastMessageViewDto;
import com.opdev.model.chat.AvailableChat;
import com.opdev.model.company.Company;
import com.opdev.model.talent.Talent;
import com.opdev.model.user.UserType;
import com.opdev.repository.AvailableChatRepository;
import com.opdev.util.SimplePageImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
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
class ChatCrudIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private AvailableChatRepository availableChatRepository;

    @Test
    @DirtiesContext
    void messageCrud() {
        Company companyGoogle = createCompany(COMPANY_GOOGLE);
        String googleToken = getTokenForCompanyGoogle();
        HttpHeaders headers = createAuthHeaders(googleToken);

        Talent talentGoran = createTalent(TALENT_GORAN);

        addAvailableChat(companyGoogle, talentGoran);

        MessageViewDto sentMessage = companySendMessage(COMPANY_GOOGLE, "job offer", TALENT_GORAN, headers);

        String goranToken = getTokenForTalentGoran();
        headers = createAuthHeaders(goranToken);

        List<com.opdev.talent.message.dto.LastMessageViewDto> talentChats = talentGetAllChats(TALENT_GORAN, headers, 1);

        talentGetChatContent(TALENT_GORAN, talentChats.get(0).getMessage().getCompanyUsername(), headers, 1);

        talentSendMessage(TALENT_GORAN, "not interested", COMPANY_GOOGLE, headers);

        headers = createAuthHeaders(googleToken);

        List<LastMessageViewDto> companyChats = companyGetAllChats(COMPANY_GOOGLE, headers, 1);

        companyGetChatContent(COMPANY_GOOGLE, companyChats.get(0).getMessage().getTalentUsername(), headers, 2);

        Talent talentNikola = createTalent(TALENT_NIKOLA);

        addAvailableChat(companyGoogle, talentNikola);

        sentMessage = companySendMessage(COMPANY_GOOGLE, "job offer", TALENT_NIKOLA, headers);

        String nikolaToken = getTokenForTalentNikola();
        headers = createAuthHeaders(nikolaToken);

        talentChats = talentGetAllChats(TALENT_NIKOLA, headers, 1);

        talentGetChatContent(TALENT_NIKOLA, talentChats.get(0).getMessage().getCompanyUsername(), headers, 1);

        talentSendMessage(TALENT_NIKOLA, "not interested", COMPANY_GOOGLE, headers);

        headers = createAuthHeaders(googleToken);

        companyChats = companyGetAllChats(COMPANY_GOOGLE, headers, 2);

        companyGetChatContent(COMPANY_GOOGLE, companyChats.get(1).getMessage().getTalentUsername(), headers, 2);

        Company facebookCompany = createCompany(COMPANY_FACEBOOK);

        addAvailableChat(facebookCompany, talentGoran);
        addAvailableChat(facebookCompany, talentNikola);

        String facebookToken = getTokenForCompanyFacebook();
        headers = createAuthHeaders(facebookToken);

        sentMessage = companySendMessage(COMPANY_FACEBOOK, "job offer", TALENT_GORAN, headers);

        headers = createAuthHeaders(goranToken);

        talentChats = talentGetAllChats(TALENT_GORAN, headers, 2);

        talentGetChatContent(TALENT_GORAN, talentChats.get(0).getMessage().getCompanyUsername(), headers, 1);

        talentSendMessage(TALENT_GORAN, "not interested", COMPANY_FACEBOOK, headers);

        headers = createAuthHeaders(facebookToken);

        companyChats = companyGetAllChats(COMPANY_FACEBOOK, headers, 1);

        companyGetChatContent(COMPANY_FACEBOOK, companyChats.get(0).getMessage().getTalentUsername(), headers, 2);

        sentMessage = companySendMessage(COMPANY_FACEBOOK, "job offer", TALENT_NIKOLA, headers);

        headers = createAuthHeaders(nikolaToken);

        talentChats = talentGetAllChats(TALENT_NIKOLA, headers, 2);

        talentGetChatContent(TALENT_NIKOLA, talentChats.get(0).getMessage().getCompanyUsername(), headers, 1);

        talentSendMessage(TALENT_NIKOLA, "not interested", COMPANY_FACEBOOK, headers);

        headers = createAuthHeaders(facebookToken);

        companyChats = companyGetAllChats(COMPANY_FACEBOOK, headers, 2);

        companyGetChatContent(COMPANY_FACEBOOK, companyChats.get(1).getMessage().getTalentUsername(), headers, 2);
    }

    private void addAvailableChat(Company company, Talent talent) {
        AvailableChat availableChat = AvailableChat.builder()
                .talent(talent.getUser())
                .talentName(talent.getFullName())
                .talentUsername(talent.getUser().getUsername())
                .company(company.getUser())
                .companyName(company.getName())
                .companyUsername(company.getUser().getUsername())
                .build();
        availableChatRepository.save(availableChat);
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
        assertThat(sentMessageBody.getCreatedBy(), is(equalTo(UserType.COMPANY)));

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
        assertThat(talentSentMessageBody.getCreatedBy(), is(equalTo(UserType.TALENT)));

        return talentSentMessageBody;
    }

    private List<com.opdev.talent.message.dto.LastMessageViewDto> talentGetAllChats(String talentUsername, HttpHeaders headers, int expectedChats) {
        HttpEntity<Void> talentGetChats = new HttpEntity<>(headers);
        ResponseEntity<List<com.opdev.talent.message.dto.LastMessageViewDto>> talentMessagesResponse = restTemplate.exchange("/v1/talents/" + talentUsername + "/chats", HttpMethod.GET, talentGetChats, new ParameterizedTypeReference<>() {});

        List<com.opdev.talent.message.dto.LastMessageViewDto> talentChats = talentMessagesResponse.getBody();
        assertThat(talentMessagesResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(talentChats, is(notNullValue()));
        assertThat(talentChats.size(), is(equalTo(expectedChats)));

        return talentChats;
    }

    private List<LastMessageViewDto> companyGetAllChats(String companyUsername, HttpHeaders headers, int expectedNumberOfChats) {
        HttpEntity<Void> companyGetChats = new HttpEntity<>(headers);
        ResponseEntity<List<LastMessageViewDto>> companyMessagesResponse = restTemplate.exchange("/v1/companies/" + companyUsername + "/chats", HttpMethod.GET, companyGetChats, new ParameterizedTypeReference<>() {});

        List<LastMessageViewDto> companyChats = companyMessagesResponse.getBody();
        assertThat(companyMessagesResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(companyChats, is(notNullValue()));
        assertThat(companyChats.size(), is(equalTo(expectedNumberOfChats)));

        return companyChats;
    }

    private Page<com.opdev.talent.message.dto.MessageViewDto> talentGetChatContent(String talentUsername, String companyUsername, HttpHeaders headers, int expectedNumberOfMessages) {
        HttpEntity<Void> talentGetChatContent = new HttpEntity<>(headers);
        ResponseEntity<SimplePageImpl<com.opdev.talent.message.dto.MessageViewDto>> talentChatContentResponse = restTemplate.exchange("/v1/talents/" + talentUsername + "/chats/" + companyUsername, HttpMethod.GET, talentGetChatContent, new ParameterizedTypeReference<>() {});

        assertThat(talentChatContentResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        Page<com.opdev.talent.message.dto.MessageViewDto> talentChatContent = talentChatContentResponse.getBody();
        assertThat(talentChatContent, is(notNullValue()));
        assertThat((int) talentChatContent.getTotalElements(), is(expectedNumberOfMessages));
        assertThat(talentChatContent.getContent().get(0).isSeen(), is(true));

        return talentChatContent;
    }

    private Page<MessageViewDto> companyGetChatContent(String companyUsername, String talentUsername, HttpHeaders headers, int expectedNumberOfMessages) {
        HttpEntity<Void> companyGetChatContent = new HttpEntity<>(headers);
        ResponseEntity<SimplePageImpl<MessageViewDto>> companyChatContentResponse = restTemplate.exchange("/v1/companies/" + companyUsername + "/chats/" + talentUsername, HttpMethod.GET, companyGetChatContent, new ParameterizedTypeReference<>() {});

        assertThat(companyChatContentResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        Page<MessageViewDto> companyChatContent = companyChatContentResponse.getBody();
        assertThat(companyChatContent, is(notNullValue()));
        assertThat((int) companyChatContent.getTotalElements(), is(expectedNumberOfMessages));
        assertThat(companyChatContent.getContent().get(0).isSeen(), is(true));

        return companyChatContent;
    }

}

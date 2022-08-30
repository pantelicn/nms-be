package com.opdev.dto;

import com.opdev.model.chat.AvailableChat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AvailableChatViewDto {

    private long id;

    private String companyUsername;

    private String companyName;

    private String talentUsername;

    private String talentName;

    private UserViewDto company;

    private UserViewDto talent;

    public AvailableChatViewDto(AvailableChat availableChat) {
        id = availableChat.getId();
        companyUsername = availableChat.getCompanyUsername();
        companyName = availableChat.getCompanyName();
        talentUsername = availableChat.getTalentUsername();
        talentName = availableChat.getTalentName();
        company = new UserViewDto(availableChat.getCompany());
        talent = new UserViewDto(availableChat.getTalent());
    }

}

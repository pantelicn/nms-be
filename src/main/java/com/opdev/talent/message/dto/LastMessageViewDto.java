package com.opdev.talent.message.dto;

import com.opdev.model.request.LastMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class LastMessageViewDto {

    private MessageViewDto message;
    private String companyName;


    public LastMessageViewDto(LastMessage lastMessage) {
        message = new MessageViewDto(lastMessage.getLast());
        companyName = lastMessage.getCompanyName();
    }

}

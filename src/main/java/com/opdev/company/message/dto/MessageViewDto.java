package com.opdev.company.message.dto;

import com.opdev.model.request.Message;
import com.opdev.model.user.UserType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class MessageViewDto {

    private Long id;
    private String content;
    private String talentUsername;
    private UserType createdBy;
    private Instant createdOn;
    private boolean seen;

    public MessageViewDto(Message message) {
        id = message.getId();
        content = message.getContent();
        talentUsername = message.getCreatedBy().getType() == UserType.TALENT ? message.getCreatedBy().getUsername() : message.getTo().getUsername();
        createdBy = message.getCreatedBy().getType();
        createdOn = message.getCreatedOn();
        seen = message.getSeen();
    }

}

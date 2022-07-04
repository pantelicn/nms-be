package com.opdev.talent.message.dto;

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
    private String companyUsername;
    private UserType createdBy;
    private Instant createdOn;
    private boolean seen;

    public MessageViewDto(Message message) {
        id = message.getId();
        content = message.getContent();
        companyUsername = message.getCreatedBy().getType() == UserType.TALENT ? message.getTo().getUsername() : message.getCreatedBy().getUsername();
        createdBy = message.getCreatedBy().getType();
        createdOn = message.getCreatedOn();
        seen = message.getSeen();
    }

}

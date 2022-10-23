package com.opdev.notification.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class NotificationResponseDto {

    private long unseenRequests;

    private Long lastRequestId;

    private long unseenMessages;

    private Long lastMessageId;

}

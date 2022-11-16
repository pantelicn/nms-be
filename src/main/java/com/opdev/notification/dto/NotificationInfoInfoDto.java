package com.opdev.notification.dto;

import java.time.Instant;

import com.opdev.model.user.NotificationInfoType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class NotificationInfoInfoDto {

    private String message;
    private Instant createdOn;
    private NotificationInfoType type;

}

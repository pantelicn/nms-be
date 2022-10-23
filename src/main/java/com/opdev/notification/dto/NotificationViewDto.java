package com.opdev.notification.dto;

import com.opdev.model.user.Notification;
import com.opdev.model.user.NotificationType;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationViewDto {

    private Long id;

    private String description;

    private Boolean sean;

    private NotificationType type;

    private Long referenceId;

    public NotificationViewDto(Notification notification) {
        id = notification.getId();
        description = notification.getDescription();
        sean = notification.getSeen();
        type = notification.getType();
        referenceId = notification.getReferenceId();
    }

}

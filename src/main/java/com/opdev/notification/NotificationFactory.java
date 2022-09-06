package com.opdev.notification;

import com.opdev.model.user.Notification;
import com.opdev.model.user.NotificationType;
import com.opdev.model.user.User;

public class NotificationFactory {

    public static Notification createRequestNotification(Long referenceId, User notificationFor, String company) {
        return Notification.builder()
                .referenceId(referenceId)
                .seen(false)
                .description(String.format("You have received a request from %s", company))
                .type(NotificationType.REQUEST)
                .user(notificationFor)
                .build();
    }

}

package com.opdev.notification;

import com.opdev.model.user.Notification;
import com.opdev.model.user.NotificationInfoType;
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

    public static Notification editRequestByCompany(Long referenceId, User notificationFor, String company) {
        return Notification.builder()
                .referenceId(referenceId)
                .seen(false)
                .description(String.format("Request has been updated by %s", company))
                .type(NotificationType.REQUEST)
                .user(notificationFor)
                .build();
    }

    public static Notification editRequestByTalent(Long referenceId, User notificationFor, String companyNote) {
        return Notification.builder()
                .referenceId(referenceId)
                .seen(false)
                .description(String.format("%s - Request has been updated by talent", companyNote))
                .type(NotificationType.REQUEST)
                .user(notificationFor)
                .build();
    }

    public static Notification createMessageNotification(Long referenceId, User notificationFor, String sender) {
        return Notification.builder()
                .referenceId(referenceId)
                .seen(false)
                .description(String.format("You have been received new message from %s", sender))
                .type(NotificationType.MESSAGE)
                .user(notificationFor)
                .build();
    }

    public static Notification createRejectedNotificationForCompany(Long referenceId, User notificationFor, String requestNote) {
        return Notification.builder()
                .referenceId(referenceId)
                .seen(false)
                .description(String.format("Request under note %s has been rejected by talent", requestNote))
                .type(NotificationType.INFO)
                .infoType(NotificationInfoType.REQUEST)
                .user(notificationFor)
                .build();
    }

    public static Notification createAcceptedNotificationForCompany(Long referenceId, User notificationFor, String requestNote, String talentFullName) {
        return Notification.builder()
                .referenceId(referenceId)
                .seen(false)
                .description(String.format("Request under note %s has been accepted by talent %s. Find %s in messages and start conversation.", requestNote, talentFullName, talentFullName))
                .type(NotificationType.INFO)
                .infoType(NotificationInfoType.REQUEST)
                .user(notificationFor)
                .build();
    }

    public static Notification createAcceptedNotificationForTalent(Long referenceId, User notificationFor, String companyName) {
        return Notification.builder()
                .referenceId(referenceId)
                .seen(false)
                .description(String.format("You have accepted request from %s. Find %s in messages and start conversation.", companyName, companyName))
                .type(NotificationType.INFO)
                .infoType(NotificationInfoType.REQUEST)
                .user(notificationFor)
                .build();
    }

}

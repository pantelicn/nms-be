package com.opdev.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.opdev.model.user.Notification;
import com.opdev.model.user.NotificationType;
import com.opdev.notification.dto.NotificationResponseDto;

public interface NotificationService {

    Notification createOrUpdate(Notification newNotification);

    NotificationResponseDto findAll(String username, Pageable pageable);

    Page<Notification> findAllInfos(String username, Pageable pageable);

    void setSeenForNotificationType(Long requestId, String username, NotificationType type);

}

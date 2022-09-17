package com.opdev.notification;

import org.springframework.data.domain.Pageable;

import com.opdev.model.user.Notification;
import com.opdev.notification.dto.NotificationResponseDto;

public interface NotificationService {

    Notification create(Notification newNotification);

    NotificationResponseDto findAll(String username, Pageable pageable);

    void setSeenForRequests(Long requestId, String talentUsername);

}

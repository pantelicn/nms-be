package com.opdev.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.opdev.model.user.Notification;

public interface NotificationService {

    Notification create(Notification newNotification);

    Page<Notification> findAllForUsername(String companyUsername, Pageable pageable);

}

package com.opdev.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opdev.model.user.Notification;
import com.opdev.model.user.User;
import com.opdev.repository.NotificationRepository;
import com.opdev.user.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;
    private final UserService userService;

    @Override
    @Transactional
    public Notification create(final Notification newNotification) {
        return repository.save(newNotification);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Notification> findAllForUsername(final String username, final Pageable pageable) {
        User user = userService.getByUsername(username);
        return repository.findAllByUser(user, pageable);
    }

}

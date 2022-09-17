package com.opdev.notification;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opdev.model.user.Notification;
import com.opdev.model.user.NotificationType;
import com.opdev.model.user.User;
import com.opdev.notification.dto.NotificationResponseDto;
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
    public NotificationResponseDto findAll(final String username, final Pageable pageable) {
        User user = userService.getByUsername(username);
        long unseenRequests = 0;
        Notification lastUnseenRequestNotification = repository.findLastUnseenRequest(user.getId());
        if (lastUnseenRequestNotification != null) {
            unseenRequests = repository.countByUserAndTypeAndSeenIsFalse(user, NotificationType.REQUEST);
        }

        NotificationResponseDto response = NotificationResponseDto.builder()
                .lastRequestId(lastUnseenRequestNotification != null ? lastUnseenRequestNotification.getReferenceId() : null)
                .unseenRequests(unseenRequests)
                .build();

        return response;
    }

    @Override
    @Transactional
    public void setSeenForRequests(final Long requestId, final String talentUsername) {
        User user = userService.getByUsername(talentUsername);
        Notification foundNotification = repository.findByUserAndReferenceIdAndType(user, requestId, NotificationType.REQUEST).orElseThrow(() -> new RuntimeException(""));
        List<Notification> found = repository.findAllByUserAndTypeAndSeenIsFalseAndCreatedOnLessThanEqual(user, NotificationType.REQUEST, foundNotification.getCreatedOn());
        found.forEach(notification -> {
            notification.setSeen(Boolean.TRUE);
            repository.save(notification);
        });
    }

}

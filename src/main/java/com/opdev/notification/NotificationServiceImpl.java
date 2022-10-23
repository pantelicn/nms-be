package com.opdev.notification;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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
    public Notification createOrUpdate(final Notification newNotification) {
        Optional<Notification> foundExisting = repository.findByUserAndReferenceIdAndType(newNotification.getUser(), newNotification.getReferenceId(), newNotification.getType());
        if (foundExisting.isPresent()) {
            Notification existing = foundExisting.get();
            existing.setSeen(false);
            existing.setModifiedOn(Instant.now());
            return repository.save(existing);
        } else {
            return repository.save(newNotification);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponseDto findAll(final String username, final Pageable pageable) {
        User user = userService.getByUsername(username);
        long unseenRequests = 0;
        Notification lastUnseenRequestNotification = repository.findLastUnseen(user.getId(), NotificationType.REQUEST.name());
        if (lastUnseenRequestNotification != null) {
            unseenRequests = repository.countByUserAndTypeAndSeenIsFalse(user, NotificationType.REQUEST);
        }

        long unseenMessages = 0;
        Notification lastUnseenMessageNotification = repository.findLastUnseen(user.getId(), NotificationType.MESSAGE.name());
        if (lastUnseenMessageNotification != null) {
            unseenMessages = repository.countByUserAndTypeAndSeenIsFalse(user, NotificationType.MESSAGE);
        }

        NotificationResponseDto response = NotificationResponseDto.builder()
                .lastRequestId(lastUnseenRequestNotification != null ? lastUnseenRequestNotification.getReferenceId() : null)
                .unseenRequests(unseenRequests)
                .lastMessageId(lastUnseenMessageNotification != null ? lastUnseenMessageNotification.getReferenceId() : null)
                .unseenMessages(unseenMessages)
                .build();

        return response;
    }

    @Override
    @Transactional
    public void setSeenForNotificationType(final Long requestId, final String username, final NotificationType type) {
        User user = userService.getByUsername(username);
        Notification foundNotification = repository.findByUserAndReferenceIdAndType(user, requestId, type).orElseThrow(() -> new RuntimeException(""));
        List<Notification> found = repository.findAllByUserAndTypeAndSeenIsFalseAndCreatedOnLessThanEqual(user, type, foundNotification.getCreatedOn());
        found.forEach(notification -> {
            notification.setSeen(Boolean.TRUE);
            repository.save(notification);
        });
    }

}

package com.opdev.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.opdev.model.user.Notification;
import com.opdev.model.user.NotificationType;
import com.opdev.model.user.User;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query(value = "select * from notification where type = :type and user_id = :userId order by created_on desc limit 1", nativeQuery = true)
    Notification findLastUnseen(@Param("userId") Long userId, @Param("type") String type);

    long countByUserAndTypeAndSeenIsFalse(User user, NotificationType type);

    Page<Notification> findAllByUser(User user, Pageable pageable);

    List<Notification> findAllByUserAndTypeAndSeenIsFalseAndCreatedOnLessThanEqual(User user, NotificationType type, Instant creationDate);

    Optional<Notification> findByUserAndReferenceIdAndType(User user, Long referenceId, NotificationType type);

    Page<Notification> findAllByUserUsernameAndType(String username, NotificationType type, Pageable pageable);

}

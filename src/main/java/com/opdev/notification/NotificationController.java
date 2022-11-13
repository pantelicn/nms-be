package com.opdev.notification;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.opdev.config.security.Roles;
import com.opdev.model.user.Notification;
import com.opdev.model.user.NotificationType;
import com.opdev.notification.dto.NotificationInfoInfoDto;
import com.opdev.notification.dto.NotificationResponseDto;
import com.opdev.notification.dto.NotificationViewDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    private static final int perPageNotifications = 20;

    @GetMapping
    @PreAuthorize("(hasAnyRole('" + Roles.COMPANY + "', '" + Roles.TALENT + "'))")
    public NotificationResponseDto findAll(@RequestParam(defaultValue = "0") Integer page, Principal user) {
        Pageable pageable = PageRequest.of(page, perPageNotifications, Sort.by("createdOn").descending());
        return service.findAll(user.getName(), pageable);
    }

    @PutMapping("seen")
    @PreAuthorize("(hasAnyRole('" + Roles.COMPANY + "', '" + Roles.TALENT + "'))")
    public void setSeenForNotificationType(@RequestParam Long lastVisibleRequestId, @RequestParam NotificationType type, Principal user) {
        service.setSeenForNotificationType(lastVisibleRequestId, user.getName(), type);
    }

    @GetMapping("infos")
    @PreAuthorize("(hasAnyRole('" + Roles.COMPANY + "', '" + Roles.TALENT + "'))")
    public Page<NotificationInfoInfoDto> findAllInfos(@RequestParam(defaultValue = "0") Integer page, Principal user) {
        Pageable pageable = PageRequest.of(page, perPageNotifications, Sort.by("createdOn").descending());
        Page<Notification> found = service.findAllInfos(user.getName(), pageable);

        return found.map(notification -> new NotificationInfoInfoDto(notification.getDescription(), notification.getCreatedOn()));
    }

}

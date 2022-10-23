package com.opdev.talent.message;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.opdev.model.request.LastMessage;
import com.opdev.model.talent.Talent;
import com.opdev.model.user.Notification;
import com.opdev.notification.NotificationFactory;
import com.opdev.notification.NotificationService;
import com.opdev.talent.TalentService;
import com.opdev.talent.message.dto.LastMessageViewDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.opdev.config.security.Roles;
import com.opdev.message.LastMessageService;
import com.opdev.message.MessageService;
import com.opdev.model.request.Message;
import com.opdev.model.user.UserType;
import com.opdev.talent.message.dto.MessageSendDto;
import com.opdev.talent.message.dto.MessageViewDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/talents/{username}/chats")
@RequiredArgsConstructor
public class TalentChatsController {

    private final MessageService service;
    private final LastMessageService lastMessageService;
    private final NotificationService notificationService;
    private final TalentService talentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.TALENT + "'))")
    public MessageViewDto send(@Valid @RequestBody MessageSendDto newMessage, @PathVariable String username) {
        Message created = service.send(newMessage.getContent(), newMessage.getCompanyUsername(), UserType.COMPANY);
        Talent sender = talentService.getByUsername(username);
        Notification messageNotification = NotificationFactory.createMessageNotification(created.getId(), created.getTo(), sender.getFullName());
        notificationService.createOrUpdate(messageNotification);
        return MessageViewDto.builder()
                .id(created.getId())
                .content(created.getContent())
                .companyUsername(newMessage.getCompanyUsername())
                .createdBy(created.getCreatedBy().getType())
                .seen(created.getSeen())
                .build();
    }

    @GetMapping("{companyUsername}")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.TALENT + "'))")
    @ResponseStatus(HttpStatus.OK)
    public Page<MessageViewDto> get(@PathVariable String username,
                                    @PathVariable String companyUsername,
                                    @RequestParam(required = false) Instant timestamp,
                                    @PageableDefault(sort = "createdOn", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Message> found = service.getPreviousMessages(username, companyUsername, timestamp, pageable);
        return found.map(MessageViewDto::new);
    }

    @GetMapping
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.TALENT + "'))")
    @ResponseStatus(HttpStatus.OK)
    public List<LastMessageViewDto> findAll(@PathVariable String username) {
        List<LastMessage> found = lastMessageService.getLastMessages(UserType.TALENT);
        return found.stream().map(LastMessageViewDto::new).collect(Collectors.toList());
    }

}

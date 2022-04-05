package com.opdev.talent.message;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.TALENT + "'))")
    public MessageViewDto send(@Valid @RequestBody MessageSendDto newMessage, @PathVariable String username) {
        Message created = service.send(newMessage.getContent(), newMessage.getCompanyUsername(), UserType.COMPANY);
        return MessageViewDto.builder()
                .id(created.getId())
                .content(created.getContent())
                .companyUsername(newMessage.getCompanyUsername())
                .createdBy(created.getCreatedBy().getType())
                .seen(created.getSeen())
                .build();
    }

    @GetMapping("{lastMessageId}")
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.TALENT + "'))")
    @ResponseStatus(HttpStatus.OK)
    public List<MessageViewDto> get(@PathVariable String username, @PathVariable Long lastMessageId) {
        List<Message> found = service.getPreviousMessages(lastMessageId, UserType.TALENT);
        return found.stream().map(message -> MessageViewDto.builder()
                .id(message.getId())
                .content(message.getContent())
                .companyUsername(message.getCreatedBy().getType() == UserType.TALENT ? message.getTo().getUsername() : message.getCreatedBy().getUsername())
                .createdBy(message.getCreatedBy().getType())
                .seen(message.getSeen())
                .build()).collect(Collectors.toList());
    }

    @GetMapping
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.TALENT + "'))")
    @ResponseStatus(HttpStatus.OK)
    public List<MessageViewDto> findAll(@PathVariable String username) {
        List<Message> found = lastMessageService.getLastMessages(UserType.TALENT);
        return found.stream().map(message -> MessageViewDto.builder()
                .id(message.getId())
                .content(message.getContent())
                .companyUsername(message.getCreatedBy().getType() == UserType.TALENT ? message.getTo().getUsername() : message.getCreatedBy().getUsername())
                .createdBy(message.getCreatedBy().getType())
                .seen(message.getSeen())
                .build()).collect(Collectors.toList());
    }

}

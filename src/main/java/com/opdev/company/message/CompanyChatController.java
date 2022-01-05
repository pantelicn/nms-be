package com.opdev.company.message;

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

import com.opdev.company.message.dto.MessageSendDto;
import com.opdev.company.message.dto.MessageViewDto;
import com.opdev.config.security.Roles;
import com.opdev.message.LastMessageService;
import com.opdev.message.MessageService;
import com.opdev.model.request.Message;
import com.opdev.model.user.UserType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/companies/{username}/chats")
@RequiredArgsConstructor
public class CompanyChatController {

    private final MessageService service;

    private final LastMessageService lastMessageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "')) " + //
                          "|| hasRole('" + Roles.ADMIN + "')")
    public MessageViewDto send(@Valid @RequestBody MessageSendDto newMessage, @PathVariable String username) {
        Message created = service.send(newMessage.getContent(), newMessage.getTalentUsername(), UserType.TALENT);
        return MessageViewDto.builder()
                .id(created.getId())
                .content(created.getContent())
                .talentUsername(newMessage.getTalentUsername())
                .seen(created.getSeen())
                .build();
    }

    @GetMapping("{lastMessageId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))")
    public List<MessageViewDto> get(@PathVariable String username, @PathVariable Long lastMessageId) {
        List<Message> found = service.getPreviousMessages(lastMessageId, UserType.COMPANY);
        return found.stream().map(message -> MessageViewDto.builder()
                .id(message.getId())
                .content(message.getContent())
                .talentUsername(message.getCreatedBy().getType() == UserType.COMPANY ? message.getTo().getUsername() : message.getCreatedBy().getUsername())
                .seen(message.getSeen())
                .build()).collect(Collectors.toList());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("(#username == authentication.name && hasRole('" + Roles.COMPANY + "'))")
    public List<MessageViewDto> findChatWith(@PathVariable String username) {
        List<Message> found = lastMessageService.getLastMessages(UserType.COMPANY);
        return found.stream().map(message -> MessageViewDto.builder()
                .id(message.getId())
                .content(message.getContent())
                .talentUsername(message.getCreatedBy().getType() == UserType.COMPANY ? message.getTo().getUsername() : message.getCreatedBy().getUsername())
                .seen(message.getSeen())
                .build()).collect(Collectors.toList());
    }

}

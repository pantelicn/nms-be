package com.opdev.message;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.model.request.LastMessage;
import com.opdev.model.request.Message;
import com.opdev.model.user.User;
import com.opdev.model.user.UserType;
import com.opdev.repository.MessageRepository;
import com.opdev.user.UserService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository repository;
    private final UserService userService;
    private final LastMessageService lastMessageService;

    @Override
    @Transactional
    public Message send(final @NonNull String content, final @NonNull String targetUsername, final @NonNull UserType type) {
        final User to = userService.findUserByUsernameAndType(targetUsername, type).orElseThrow(
                () -> ApiEntityNotFoundException.builder().message("Entity.not.found").entity("User").id(targetUsername).build());

        final User loggedUser = userService.getLoggedInUser();

        // TODO: add validation

        final Message newMessage = Message.builder()
                .content(content)
                .seen(false)
                .to(to)
                .build();
        newMessage.setCreatedBy(loggedUser);
        newMessage.setModifiedBy(loggedUser);
        Message created = repository.save(newMessage);
        if (type == UserType.TALENT) {
            lastMessageService.save(created, to, loggedUser);
        } else {
            lastMessageService.save(created, loggedUser, to);
        }
        return created;
    }

    @Override
    @Transactional
    public List<Message> getPreviousMessages(@NonNull final Long lastMessageId, @NonNull final UserType type) {
        User loggedUser = userService.getLoggedInUser();
        LastMessage lastMessage = lastMessageService.findByMessageAndUser(lastMessageId, loggedUser, type);
        List<Message> found = repository.findPreviousMessages(lastMessage.getTalent(), lastMessage.getCompany());
        found.forEach(message -> {
            if (!message.getSeen()) {
                message.setSeen(true);
                repository.save(message);
            }
        });
        return found;
    }
}

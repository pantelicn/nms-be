package com.opdev.message;

import com.opdev.exception.ApiBadRequestException;
import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.model.request.Message;
import com.opdev.model.user.User;
import com.opdev.model.user.UserType;
import com.opdev.repository.MessageRepository;
import com.opdev.user.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static java.lang.String.format;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository repository;
    private final UserService userService;
    private final LastMessageService lastMessageService;
    private final AvailableChatService availableChatService;

    @Override
    @Transactional
    public Message send(final @NonNull String content, final @NonNull String targetUsername, final @NonNull UserType type) {
        final User sender = userService.getLoggedInUser();
        return send(sender, content, targetUsername, type);
    }

    @Override
    @Transactional
    public Message send(User sender, String content, String targetUsername, UserType targetUserType) {
        final User to = userService.findUserByUsernameAndType(targetUsername, targetUserType).orElseThrow(
                () -> ApiEntityNotFoundException.builder().message("Entity.not.found").entity("User").id(targetUsername).build());

        validateChatIsAllowed(sender.getUsername(), targetUsername, targetUserType);

        final Message newMessage = Message.builder()
                .content(content)
                .seen(false)
                .to(to)
                .build();
        newMessage.setCreatedBy(sender);
        newMessage.setModifiedBy(sender);
        Message created = repository.save(newMessage);
        if (targetUserType == UserType.TALENT) {
            lastMessageService.save(created, to, sender);
        } else {
            lastMessageService.save(created, sender, to);
        }
        return created;
    }

    @Override
    @Transactional
    public Page<Message> getPreviousMessages(@NonNull String talentUsername,
                                             @NonNull String companyUsername,
                                             Instant timestamp,
                                             @NonNull Pageable pageable) {
        Page<Message> found = timestamp == null ?
                repository.findPreviousMessages(talentUsername, companyUsername, pageable)
                :
                repository.findPreviousMessagesOlderThan(talentUsername, companyUsername, timestamp, pageable);
        found.filter(message -> isNotTrue(message.getSeen())).forEach(this::setMessageSeen);
        return found;
    }

    private void setMessageSeen(Message seenMessage) {
        seenMessage.setSeen(true);
        repository.save(seenMessage);
    }

    private void validateChatIsAllowed(String senderUsername, String targetUsername, UserType targetUserType) {
        String talentUsername = targetUserType == UserType.TALENT ? targetUsername : senderUsername;
        String companyUsername = targetUserType == UserType.COMPANY ? targetUsername : senderUsername;
        if (!availableChatService.canChat(talentUsername, companyUsername)) {
            throw new ApiBadRequestException(format("User %s cannot chat with %s", talentUsername, companyUsername));
        }
    }

}

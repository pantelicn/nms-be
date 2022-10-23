package com.opdev.message;

import com.opdev.company.service.CompanyService;
import com.opdev.exception.ApiBadRequestException;
import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.model.company.Company;
import com.opdev.model.request.Message;
import com.opdev.model.talent.Talent;
import com.opdev.model.user.Notification;
import com.opdev.model.user.User;
import com.opdev.model.user.UserType;
import com.opdev.notification.NotificationFactory;
import com.opdev.notification.NotificationService;
import com.opdev.repository.MessageRepository;
import com.opdev.talent.TalentService;
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
    private final NotificationService notificationService;
    private final TalentService talentService;
    private final CompanyService companyService;

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
        String senderName;
        if (sender.getType() == UserType.TALENT) {
            Talent foundTalent = talentService.getByUsername(sender.getUsername());
            senderName = foundTalent.getFullName();
        } else {
            Company foundCompany = companyService.getByUsername(sender.getUsername());
            senderName = foundCompany.getName();
        }
        Notification messageNotification = NotificationFactory.createMessageNotification(created.getId(), created.getTo(), senderName);
        notificationService.createOrUpdate(messageNotification);
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

package com.opdev.message;

import com.opdev.message.dto.ChatMessageDto;
import com.opdev.model.user.User;
import com.opdev.model.user.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatServiceImpl implements ChatService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageService messageService;

    @Override
    public void send(User sender, ChatMessageDto message) {
        UserType targetUserType = sender.getType() == UserType.COMPANY ? UserType.TALENT : UserType.COMPANY;
        messageService.send(sender, message.getContent(), message.getTo(), targetUserType);
        simpMessagingTemplate.convertAndSendToUser(message.getTo(), "/queue/messages", message.getContent());
    }

}

package com.opdev.message;

import com.opdev.message.dto.ChatMessageDto;
import com.opdev.model.user.User;
import com.opdev.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RequiredArgsConstructor
@RestController
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;

    @MessageMapping("/chat")
    public void reply(@Payload ChatMessageDto message, Principal principal) {
        User user = userService.getByUsername(principal.getName());
        chatService.send(user, message);
    }

}

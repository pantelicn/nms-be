package com.opdev.message;

import com.opdev.message.dto.ChatMessageDto;
import com.opdev.model.user.User;

public interface ChatService {

    void send(User sender, ChatMessageDto message);

}

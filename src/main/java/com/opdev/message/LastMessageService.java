package com.opdev.message;

import java.util.List;

import com.opdev.model.request.LastMessage;
import com.opdev.model.request.Message;
import com.opdev.model.user.User;
import com.opdev.model.user.UserType;

public interface LastMessageService {

    List<Message> getLastMessages(UserType type);

    LastMessage save(Message message, User talent, User company);

    LastMessage findByMessageAndUser(Long lastMessageId, User user, UserType type);

}

package com.opdev.message;

import java.util.List;

import com.opdev.model.request.Message;
import com.opdev.model.user.UserType;

import lombok.NonNull;

public interface MessageService {

    Message send(String content, String username, UserType type);

    List<Message> getPreviousMessages(@NonNull Long lastMessageId, @NonNull UserType type);

}

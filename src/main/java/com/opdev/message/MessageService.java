package com.opdev.message;

import com.opdev.model.request.Message;
import com.opdev.model.user.User;
import com.opdev.model.user.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

public interface MessageService {

    Message send(String content, String targetUsername, UserType targetUserType);

    Message send(User sender, String content, String targetUsername, UserType targetUserType);

    Page<Message> getPreviousMessages(String talentUsername, String companyUsername, Instant instant, Pageable pageable);

}

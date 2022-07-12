package com.opdev.message;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.opdev.model.chat.AvailableChat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opdev.model.request.LastMessage;
import com.opdev.model.request.Message;
import com.opdev.model.user.User;
import com.opdev.model.user.UserType;
import com.opdev.repository.LastMessageRepository;
import com.opdev.user.UserService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LastMessageServiceImpl implements LastMessageService {

    private final LastMessageRepository repository;
    private final UserService userService;
    private final AvailableChatService availableChatService;

    @Override
    @Transactional
    public List<LastMessage> getLastMessages(@NonNull UserType type) {
        User loggedUser = userService.getLoggedInUser();
        if (type == UserType.TALENT) {
            return repository.findByTalentOrderByModifiedOnDesc(loggedUser);
        } else {
            return repository.findByCompanyOrderByModifiedOnDesc(loggedUser);
        }
    }

    @Override
    @Transactional
    public LastMessage save(final Message message, final User talent, final User company) {
        Optional<LastMessage> found = repository.findByTalentAndCompany(talent, company);
        if (found.isPresent()) {
            found.get().setLast(message);
            found.get().setModifiedOn(Instant.now());
            return repository.save(found.get());
        } else {
            AvailableChat availableChat = availableChatService.get(talent.getUsername(), company.getUsername());
            LastMessage lastMessage = LastMessage.builder()
                    .last(message)
                    .company(company)
                    .talent(talent)
                    .modifiedOn(Instant.now())
                    .companyName(availableChat.getCompanyName())
                    .talentName(availableChat.getTalentName())
                    .build();
            return repository.save(lastMessage);
        }
    }

}

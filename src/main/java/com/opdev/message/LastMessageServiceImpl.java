package com.opdev.message;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opdev.exception.ApiEntityNotFoundException;
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

    @Override
    @Transactional
    public List<Message> getLastMessages(@NonNull UserType type) {
        User loggedUser = userService.getLoggedInUser();
        List<LastMessage> found;
        if (type == UserType.TALENT) {
            found = repository.findByTalentOrderByModifiedOnDesc(loggedUser);
        } else {
            found = repository.findByCompanyOrderByModifiedOnDesc(loggedUser);
        }
        return found.stream().map(LastMessage::getLast).collect(Collectors.toList());
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
            LastMessage lastMessage = LastMessage.builder()
                    .last(message)
                    .company(company)
                    .talent(talent)
                    .modifiedOn(Instant.now())
                    .build();
            return repository.save(lastMessage);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public LastMessage findByMessageAndUser(@NonNull final Long lastMessageId, @NonNull final User user, @NonNull final UserType type) {
        Optional<LastMessage> found;
        if (type == UserType.COMPANY) {
            found = repository.findByLastIdAndCompany(lastMessageId, user);
        } else {
            found = repository.findByLastIdAndTalent(lastMessageId, user);
        }
        return found.orElseThrow(() -> ApiEntityNotFoundException.builder().message("Entity.not.found").entity("LastMessage").id(lastMessageId.toString()).build());
    }
}

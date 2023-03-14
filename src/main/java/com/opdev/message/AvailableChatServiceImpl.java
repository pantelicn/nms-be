package com.opdev.message;

import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.model.chat.AvailableChat;
import com.opdev.model.company.Company;
import com.opdev.model.talent.Talent;
import com.opdev.model.user.UserType;
import com.opdev.repository.AvailableChatRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AvailableChatServiceImpl implements AvailableChatService {

    private final AvailableChatRepository repository;

    @Transactional(readOnly = true)
    public Page<AvailableChat> searchAvailableChats(@NonNull String username,
                                                    @NonNull String searchQuery,
                                                    @NonNull UserType userType,
                                                    @NonNull Pageable pageable) {
        if (userType == UserType.COMPANY) {
            return repository.searchAvailableChatsForCompany(username, searchQuery, pageable);
        } else {
            return repository.searchAvailableChatsForTalent(username, searchQuery, pageable);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Page<AvailableChat> getAvailableChats(String username, UserType userType, Pageable pageable) {
        if (userType == UserType.COMPANY) {
            return repository.findByCompanyUsername(username, pageable);
        } else {
            return repository.findByTalentUsername(username, pageable);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public boolean canChat(String talentUsername, String companyUsername) {
        return repository.existsByTalentUsernameAndCompanyUsername(talentUsername, companyUsername);
    }

    @Transactional
    @Override
    public AvailableChat create(@NonNull Company company, @NonNull Talent talent) {
        AvailableChat newAvailableChat = AvailableChat.builder()
                .company(company.getUser())
                .companyUsername(company.getUser().getUsername())
                .companyName(company.getName())
                .talent(talent.getUser())
                .talentUsername(talent.getUser().getUsername())
                .talentName(talent.getFullName()).build();
        AvailableChat created = repository.save(newAvailableChat);
        LOGGER.info(
                "New chat available between company {} and talent {}",
                newAvailableChat.getCompanyUsername(),
                newAvailableChat.getTalentUsername()
        );

        return created;
    }

    @Transactional(readOnly = true)
    @Override
    public AvailableChat get(String talentUsername, String companyUsername) {
        return repository.findByTalentUsernameAndCompanyUsername(talentUsername, companyUsername)
                .orElseThrow(() -> ApiEntityNotFoundException.builder()
                        .entity("AvailableChat")
                        .message("Entity.not.found").build()
                );
    }

    @Transactional
    @Override
    public void removeByTalentAndCompany(Talent talent, Company company) {
        repository.removeByTalentUsernameAndCompanyUsername(talent.getUser().getUsername(), company.getUser().getUsername());
        LOGGER.info("Removed available chat between talent {} and company {}", talent.getUser().getUsername(), company.getUser().getUsername());
    }

}

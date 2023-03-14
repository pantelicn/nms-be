package com.opdev.message;

import com.opdev.model.chat.AvailableChat;
import com.opdev.model.company.Company;
import com.opdev.model.talent.Talent;
import com.opdev.model.user.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AvailableChatService {

    Page<AvailableChat> searchAvailableChats(String username, String searchQuery, UserType userType, Pageable pageable);

    Page<AvailableChat> getAvailableChats(String username, UserType userType, Pageable pageable);

    boolean canChat(String talentUsername, String companyUsername);

    AvailableChat create(Company company, Talent talent);

    AvailableChat get(String talentUsername, String companyUsername);

    void removeByTalentAndCompany(Talent talent, Company company);

}

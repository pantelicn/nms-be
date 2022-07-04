package com.opdev.repository;

import com.opdev.model.chat.AvailableChat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AvailableChatRepository extends JpaRepository<AvailableChat, Long> {

    @Query("select availableChat from AvailableChat availableChat where " +
            "availableChat.companyUsername = :companyUsername " +
            "and (lower(availableChat.talentName) like concat('%', lower(:searchQuery), '%') " +
            "or lower(availableChat.talentUsername) like concat('%', lower(:searchQuery), '%'))")
    Page<AvailableChat> searchAvailableChatsForCompany(@Param("companyUsername") String companyUsername,
                                                       @Param("searchQuery") String searchQuery,
                                                       Pageable pageable);

    @Query("select availableChat from AvailableChat availableChat where " +
            "availableChat.talentUsername = :talentUsername " +
            "and (lower(availableChat.companyName) like concat('%', lower(:searchQuery), '%') " +
            "or lower(availableChat.companyUsername) like concat('%', lower(:searchQuery), '%'))")
    Page<AvailableChat> searchAvailableChatsForTalent(@Param("talentUsername") String talentUsername,
                                                      @Param("searchQuery") String searchQuery,
                                                      Pageable pageable);

    Page<AvailableChat> findByTalentUsername(String talentUsername, Pageable pageable);

    Page<AvailableChat> findByCompanyUsername(String companyUsername, Pageable pageable);

    boolean existsByTalentUsernameAndCompanyUsername(String talentUsername, String companyUsername);

    Optional<AvailableChat> findByTalentUsernameAndCompanyUsername(String talentUsername, String companyUsername);

}

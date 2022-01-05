package com.opdev.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.opdev.model.request.LastMessage;
import com.opdev.model.user.User;

public interface LastMessageRepository extends JpaRepository<LastMessage, Long> {

    List<LastMessage> findByTalentOrderByModifiedOnDesc(User talent);

    List<LastMessage> findByCompanyOrderByModifiedOnDesc(User company);

    Optional<LastMessage> findByTalentAndCompany(User talent, User company);

    Optional<LastMessage> findByLastIdAndTalent(Long lastMessageId, User talent);

    Optional<LastMessage> findByLastIdAndCompany(Long lastMessageId, User company);

}

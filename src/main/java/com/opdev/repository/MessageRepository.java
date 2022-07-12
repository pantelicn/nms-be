package com.opdev.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.opdev.model.request.Message;

import java.time.Instant;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE " +
            "(m.to.username = :talentUsername and m.createdBy.username = :companyUsername) " +
            "or (m.to.username = :companyUsername and m.createdBy.username = :talentUsername)")
    Page<Message> findPreviousMessages(String talentUsername, String companyUsername, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE " +
            "m.createdOn < :timestamp and" +
            "((m.to.username = :talentUsername and m.createdBy.username = :companyUsername) " +
            "or (m.to.username = :companyUsername and m.createdBy.username = :talentUsername))")
    Page<Message> findPreviousMessagesOlderThan(String talentUsername, String companyUsername, Instant timestamp, Pageable pageable);

}

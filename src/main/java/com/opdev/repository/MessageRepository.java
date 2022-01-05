package com.opdev.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.opdev.model.request.Message;
import com.opdev.model.user.User;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE (m.to = :talent and m.createdBy = :company) or (m.to = :company and m.createdBy = :talent) ORDER BY m.createdOn DESC")
    List<Message> findPreviousMessages(User talent, User company);

}

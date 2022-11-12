package com.opdev.repository;

import java.util.Optional;
import java.util.UUID;

import com.opdev.model.user.User;
import com.opdev.model.user.UserType;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByUsername(final String username);

  Optional<User> findByUsernameAndType(String username, UserType type);

  Optional<User> findByVerificationTokenActivationCode(UUID activationCode);

}

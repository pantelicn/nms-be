package com.opdev.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.opdev.model.user.User;
import com.opdev.model.user.VerificationToken;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> getByActivationCode(UUID activationCode);

    boolean existsByActivationCode(UUID activationCode);

    List<VerificationToken> findByUser(User user);

}

package com.opdev.repository;

import java.util.List;
import java.util.Optional;

import com.opdev.model.user.User;
import com.opdev.model.user.VerificationToken;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> getByToken(String token);

    boolean existsByToken(String token);

    List<VerificationToken> findByUser(User user);

}

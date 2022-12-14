package com.opdev.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.opdev.model.user.ResetPasswordRequest;

public interface ResetPasswordRequestRepository extends JpaRepository<ResetPasswordRequest, Long> {

    Optional<ResetPasswordRequest> findByValidityTokenAndUsedIsFalse(UUID validityToken);

}

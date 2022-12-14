package com.opdev.user.resetpasswordrequest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opdev.exception.ResetPasswordRequestNotFound;
import com.opdev.model.user.ResetPasswordRequest;
import com.opdev.model.user.User;
import com.opdev.repository.ResetPasswordRequestRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResetPasswordRequestServiceImpl implements ResetPasswordRequestService {

    private final ResetPasswordRequestRepository repository;

    @Override
    @Transactional
    public ResetPasswordRequest create(final User user) {
        final ResetPasswordRequest newRequest = ResetPasswordRequest.builder()
                .validityToken(UUID.randomUUID())
                .validTo(Instant.now().plus(30, ChronoUnit.MINUTES))
                .user(user)
                .used(false)
                .build();
        return repository.save(newRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public ResetPasswordRequest findByValidityToken(final UUID validityToken) {
        return repository.findByValidityTokenAndUsedIsFalse(validityToken).orElseThrow(ResetPasswordRequestNotFound::new);
    }

}

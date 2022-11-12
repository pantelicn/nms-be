package com.opdev.user.verification;

import com.opdev.exception.ApiVerificationTokenAlreadyUsed;
import com.opdev.model.user.User;
import com.opdev.model.user.VerificationToken;
import com.opdev.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;

    @Override
    @Transactional
    public void deleteByUser(User user) {
        verificationTokenRepository.deleteAll(verificationTokenRepository.findByUser(user));
    }

    @Override
    @Transactional
    public void create(VerificationToken verificationToken) {
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public void use(VerificationToken verificationToken) {
        if(verificationToken.getUsed()){
            throw new ApiVerificationTokenAlreadyUsed("Verification token already used", verificationToken.getActivationCode().toString());
        }

        verificationToken.use();
        verificationTokenRepository.save(verificationToken);
    }

}

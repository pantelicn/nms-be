package com.opdev.user.verification;

import com.opdev.model.user.User;
import com.opdev.model.user.VerificationToken;

public interface VerificationTokenService {

    void deleteByUser(User user);

    void create(VerificationToken verificationToken);

    void use(VerificationToken verificationToken);

}

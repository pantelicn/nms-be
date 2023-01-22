package com.opdev.mail;

import com.opdev.model.user.VerificationToken;

import java.util.UUID;

public interface NullHireMailSender {

    void sendRegistrationEmail(String emailTo, VerificationToken verificationToken);

    void sendResetPasswordEmail(String emailTo, UUID validityToken);

    void sendRequestReceivedEmail(String emailTo, Long requestId, String companyName);

}

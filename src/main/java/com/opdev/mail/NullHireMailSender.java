package com.opdev.mail;

import com.opdev.model.user.VerificationToken;

import java.util.UUID;

public interface NullHireMailSender {

    void sendRegistrationEmail(String emailTo, VerificationToken verificationToken);

}

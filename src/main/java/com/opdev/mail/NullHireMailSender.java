package com.opdev.mail;

import com.opdev.model.user.VerificationToken;

import java.util.UUID;

public interface NullHireMailSender {

    void sendRegistrationEmail(String emailTo, VerificationToken verificationToken);

    void sendResetPasswordEmail(String emailTo, UUID validityToken);

    void sendRequestReceivedEmail(String emailTo, Long requestId, String companyName);

    void sendRequestAcceptedEmail(String emailTo, String talentFullName, String requestNote);

    void sendPostAward100Email(String emailTo, Long postId, String postTitle);

}

package com.opdev.user;

public interface MailService {

    void sendEmail(final String emailAddress, final String subject, final String body);

}

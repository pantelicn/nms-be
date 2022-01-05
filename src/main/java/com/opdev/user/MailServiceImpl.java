package com.opdev.user;

import java.util.Objects;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(final String emailAddress, final String subject, final String body) {
        Objects.requireNonNull(emailAddress);
        Objects.requireNonNull(subject);
        Objects.requireNonNull(body);

        final SimpleMailMessage email = createMail(emailAddress, subject, body);
        mailSender.send(email);
    }

    protected SimpleMailMessage createMail(final String emailAddress, final String subject, final String body) {
        Objects.requireNonNull(emailAddress);
        Objects.requireNonNull(subject);
        Objects.requireNonNull(body);

        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(emailAddress);
        email.setSubject(subject);
        email.setText(body);

        return email;
    }

}

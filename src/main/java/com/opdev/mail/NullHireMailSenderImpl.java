package com.opdev.mail;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.opdev.model.user.VerificationToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import freemarker.template.Configuration;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class NullHireMailSenderImpl implements NullHireMailSender {

    private final JavaMailSender javaMailSender;
    private final Configuration nullHireFreeMarkerConfiguration;
    @Value("${nullhire.base-url}")
    private String baseUrl;
    @Value("${nullhire.domain}")
    private String domain;
    private static final String REGISTRATION_TEMPLATE = "registration-email.flth";
    private static final String RESET_PASSWORD_TEMPLATE = "reset-password-email.flth";
    private static final String REQUEST_RECEIVED_TEMPLATE = "request-received.flth";
    private static final String REQUEST_ACCEPTED_TEMPLATE = "request-accepted.flth";
    private static final String POST_AWARD_100 = "post-award-100.flth";

    @Override
    public void sendRegistrationEmail(final String emailTo, final VerificationToken verificationToken) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            LOGGER.info("Sending verification email to {}", emailTo);
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setSubject("Nullhire activation");
            mimeMessageHelper.setFrom("noreply@nullhire.com");
            mimeMessageHelper.setTo(emailTo);
            Map<String, Object> model = new HashMap<>();
            model.put("baseUrl", baseUrl);
            model.put("activationCode", verificationToken.getActivationCode().toString());

            String content = geContentFromTemplate(model, REGISTRATION_TEMPLATE);

            mimeMessageHelper.setText(content, true);

            javaMailSender.send(mimeMessageHelper.getMimeMessage());
            LOGGER.info("Sent verification email to {}", emailTo);
        } catch (MessagingException e) {
            LOGGER.error("Error during sending verification email {}", e.getMessage(), e);
        }
    }

    @Override
    public void sendResetPasswordEmail(final String emailTo, final UUID validityToken) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            LOGGER.info("Sending password reset email to {}", emailTo);
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setSubject("Nullhire reset password");
            mimeMessageHelper.setFrom("noreply@nullhire.com");
            mimeMessageHelper.setTo(emailTo);
            Map<String, Object> model = new HashMap<>();
            model.put("domain", domain);
            model.put("validityToken", validityToken.toString());

            String content = geContentFromTemplate(model, RESET_PASSWORD_TEMPLATE);

            mimeMessageHelper.setText(content, true);

            javaMailSender.send(mimeMessageHelper.getMimeMessage());
            LOGGER.info("Sent password reset email to {}", emailTo);
        } catch (MessagingException e) {
            LOGGER.error("Error during sending verification email {}", e.getMessage(), e);
        }
    }

    @Async
    @Override
    public void sendRequestReceivedEmail(final String emailTo, final Long requestId, final String companyName) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            LOGGER.info("Sending request received email to {}", emailTo);
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setSubject(String.format("%s sent you connection request", companyName));
            mimeMessageHelper.setFrom("noreply@nullhire.com");
            mimeMessageHelper.setTo(emailTo);
            Map<String, Object> model = new HashMap<>();
            model.put("domain", domain);
            model.put("id", requestId.toString());
            model.put("company", companyName);

            String content = geContentFromTemplate(model, REQUEST_RECEIVED_TEMPLATE);

            mimeMessageHelper.setText(content, true);

            javaMailSender.send(mimeMessageHelper.getMimeMessage());
            LOGGER.info("Sent request received email to {}", emailTo);
        } catch (MessagingException e) {
            LOGGER.error("Error during sending request received email {}", e.getMessage(), e);
        }
    }

    @Async
    @Override
    public void sendRequestAcceptedEmail(final String emailTo, final String talentFullName, final String requestNote) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            LOGGER.info("Sending request accepted email to {}", emailTo);
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setSubject(String.format("Request under note %s has been accepted", requestNote));
            mimeMessageHelper.setFrom("noreply@nullhire.com");
            mimeMessageHelper.setTo(emailTo);
            Map<String, Object> model = new HashMap<>();
            model.put("domain", domain);
            model.put("requestNote", requestNote);
            model.put("talent", talentFullName);

            String content = geContentFromTemplate(model, REQUEST_ACCEPTED_TEMPLATE);

            mimeMessageHelper.setText(content, true);

            javaMailSender.send(mimeMessageHelper.getMimeMessage());
            LOGGER.info("Sent request accepted email to {}", emailTo);
        } catch (MessagingException e) {
            LOGGER.error("Error during sending request accepted email {}", e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void sendPostAward100Email(final String emailTo, final Long postId, final String postTitle) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            LOGGER.info("Sending post award email to {}", emailTo);
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setSubject("You have been awarded");
            mimeMessageHelper.setFrom("noreply@nullhire.com");
            mimeMessageHelper.setTo(emailTo);
            Map<String, Object> model = new HashMap<>();
            model.put("domain", domain);
            model.put("postId", postId);
            model.put("postTitle", postTitle);

            String content = geContentFromTemplate(model, POST_AWARD_100);

            mimeMessageHelper.setText(content, true);

            javaMailSender.send(mimeMessageHelper.getMimeMessage());
            LOGGER.info("Sent post award email to {}", emailTo);
        } catch (MessagingException e) {
            LOGGER.error("Error during sending post award email {}", e.getMessage(), e);
        }
    }

    public String geContentFromTemplate(Map<String, Object> model, String template) {
        StringBuilder content = new StringBuilder();
        try {
            content.append(FreeMarkerTemplateUtils.processTemplateIntoString(nullHireFreeMarkerConfiguration.getTemplate(template), model));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}

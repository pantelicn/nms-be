package com.opdev.mail;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.opdev.model.user.VerificationToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import freemarker.template.Configuration;

import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import lombok.RequiredArgsConstructor;

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

    @Override
    public void sendRegistrationEmail(final String emailTo, final VerificationToken verificationToken) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {

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
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendResetPasswordEmail(final String emailTo, final UUID validityToken) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {

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
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public String geContentFromTemplate(Map<String, Object> model, String template) {
        StringBuffer content = new StringBuffer();
        try {
            content.append(FreeMarkerTemplateUtils.processTemplateIntoString(nullHireFreeMarkerConfiguration.getTemplate(template), model));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}

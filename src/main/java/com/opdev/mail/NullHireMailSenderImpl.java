package com.opdev.mail;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import freemarker.template.Configuration;

import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NullHireMailSenderImpl implements NullHireMailSender {

    private final JavaMailSender javaMailSender;
    private final Configuration nullHireFreeMarkerConfiguration;
    @Value("${nullhire.base-url}")
    private String baseUrl;

    @Override
    public void sendRegistrationEmail(final String emailTo, final UUID activationCode) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setSubject("Nullhire activation");
            mimeMessageHelper.setFrom("noreply@nullhire.com");
            mimeMessageHelper.setTo(emailTo);
            Map<String, Object> model = new HashMap<>();
            model.put("baseUrl", baseUrl);
            model.put("activationCode", activationCode.toString());

            String content = geContentFromTemplate(model);

            mimeMessageHelper.setText(content, true);

            javaMailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public String geContentFromTemplate(Map<String, Object> model) {
        StringBuffer content = new StringBuffer();
        try {
            content.append(FreeMarkerTemplateUtils.processTemplateIntoString(nullHireFreeMarkerConfiguration.getTemplate("registration-email.flth"), model));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}

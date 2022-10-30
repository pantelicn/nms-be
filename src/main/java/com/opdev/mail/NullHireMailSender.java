package com.opdev.mail;

import java.util.UUID;

public interface NullHireMailSender {

    void sendRegistrationEmail(String emailTo, UUID activationCode);

}

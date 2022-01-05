package com.opdev.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
class LocalizationConfiguration {

  /**
   *
   * This has to be configured in order to read messages from the
   * `messages*.properties` files, instead from the ValidationMessages.properties.
   *
   * @param messageSource
   *                        a message source - injected and configured by Spring
   * @return an instance of {@link LocalValidatorFactoryBean}
   */
  @Autowired
  @Bean
  LocalValidatorFactoryBean validator(final MessageSource messageSource) {
    final LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
    validator.setValidationMessageSource(messageSource);
    return validator;
  }

}
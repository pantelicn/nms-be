package com.opdev.validation;

import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class PasswordValidator implements ConstraintValidator<Password, Object> {

    private Password constraintAnnotation;

    @Override
    public void initialize(final Password constraintAnnotation) {
        this.constraintAnnotation = Objects.requireNonNull(constraintAnnotation);
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        try {
            boolean isValid = true;

            final String password = (String) new BeanWrapperImpl(value)
                    .getPropertyValue(constraintAnnotation.password());
            final String passwordConfirmed = (String) new BeanWrapperImpl(value)
                    .getPropertyValue(constraintAnnotation.match());

            // TODO:#10 check, is it required?
            // // disables a generic error message related to the InnerTestDto instance
            // context.disableDefaultConstraintViolation();

            if (!StringUtils.hasText(password) && !password.equals(passwordConfirmed)) {
                isValid = false;

                context.buildConstraintViolationWithTemplate(constraintAnnotation.message())
                        .addPropertyNode(constraintAnnotation.password()).addConstraintViolation();
            }

            // TODO:#10 - validate the password strength
            // if (isWeak(password)) {
            // isValid = false;

            // context.buildConstraintViolationWithTemplate("Password.Weak")
            // .addPropertyNode(constraintAnnotation.password()).addConstraintViolation();
            // }

            return isValid;
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }
    }
}

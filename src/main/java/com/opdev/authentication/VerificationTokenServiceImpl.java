package com.opdev.authentication;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.opdev.common.services.ProfileService;
import com.opdev.common.services.Profiles;
import com.opdev.exception.ApiEntityNotFoundException;
import com.opdev.exception.ApiVerificationTokenExpiredException;
import com.opdev.exception.ApiVerificationTokenInvalidException;
import com.opdev.model.user.User;
import com.opdev.model.user.VerificationToken;
import com.opdev.repository.VerificationTokenRepository;
import com.opdev.user.MailService;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
class VerificationTokenServiceImpl implements VerificationTokenService {


        protected static final long TOKEN_EXPIRATION_IN_DAYS = 2;

        private final UserService userService;
        private final VerificationTokenRepository verificationTokenRepository;
        private final MailService mailService;
        private final MessageSource messageSource;
        private final ProfileService profileService;

        @Transactional
        @Override
        public void sendWelcomeVerificationEmail(final String endpoint, final User user) {
                sendVerificationEmail(endpoint, user, true);
        }

        @Override
        @Transactional
        public void resendVerificationEmail(final String endpoint, final String token) {
                Objects.requireNonNull(endpoint);
                Objects.requireNonNull(token);

                final VerificationToken verificationToken = verificationTokenRepository.getByToken(token)
                                .orElseThrow(() -> ApiEntityNotFoundException.builder()
                                                .message("VerificationToken.not.exists").entity("Verification.token")
                                                .id(token).build());
                sendVerificationEmail(endpoint, verificationToken.getUser(), false);
                verifyTokenExpiryDate(verificationToken);
        }

        @Transactional
        @Override
        public VerificationToken createAndSaveVerificationToken(User user) {
                Objects.requireNonNull(user);

                final User createdBy = userService.findLoggedInUser().orElse(user);
                final String token = generateVerificationToken();
                VerificationToken verificationToken = VerificationToken.builder().token(token) //
                                .user(user) //
                                .expiryDate(generateExpiryDate(Instant.now())) //
                                .build();
                verificationToken.setCreatedBy(createdBy);

                LOGGER.info("Generating token {} for user {}", verificationToken.toString(), user.toString());
                verificationTokenRepository.save(verificationToken);

                if (!profileService.isAnyActive(
                                Arrays.asList(Profiles.PROD_PROFILE, Profiles.TEST_VERIFICATION_TOKEN_PROFILE))) {
                        LOGGER.warn("As {} profile(s) are active, I'm going to deactivate verification token (ID={}), and enable the '{}' user.",
                                        profileService.getActive(), verificationToken.getId(), user.getUsername());
                        verificationToken = verificationToken.toBuilder().active(Boolean.FALSE).build();
                        verificationTokenRepository.save(verificationToken);

                        user = user.toBuilder().enabled(Boolean.TRUE).build();
                        userService.save(user);
                }

                return verificationToken;
        }

        @Transactional(readOnly = true)
        @Override
        public boolean isTokenUnique(final String token) {
                Objects.requireNonNull(token);
                // if the token doesn't exist, it means that it's unique
                return !verificationTokenRepository.existsByToken(token);
        }

        @Transactional(readOnly = true)
        @Override
        public Optional<VerificationToken> getToken(final String token) {
                Objects.requireNonNull(token);
                return verificationTokenRepository.getByToken(token);
        }

        @Transactional
        @Override
        public void enableUser(final String token) {
                Objects.requireNonNull(token);

                VerificationToken verificationToken = getToken(token).orElseThrow(
                                () -> ApiEntityNotFoundException.builder().message("VerificationToken.not.exists")
                                                .entity("Verification.token").id(token).build());
                if (Boolean.FALSE.equals(verificationToken.getActive())) {
                        throw new ApiVerificationTokenInvalidException("VerificationToken.already.consumed", token);
                }
                verifyTokenExpiryDate(verificationToken);

                final User user = verificationToken.getUser().toBuilder().enabled(Boolean.TRUE).build();
                userService.save(user);

                verificationToken = verificationToken.toBuilder().active(Boolean.FALSE).build();
                verificationTokenRepository.save(verificationToken);
        }

        protected void sendVerificationEmail(final String endpoint, final User user, final boolean isWelcomeEmail) {
                Objects.requireNonNull(endpoint);
                Objects.requireNonNull(user);

                final Locale locale = LocaleContextHolder.getLocale();
                final VerificationToken verificationToken = createAndSaveVerificationToken(user);

                final String subjectCode = isWelcomeEmail ? "VerificationToken.mail.title.registration.success"
                                : "VerificationToken.mail.title.new.token";
                final String bodyCode = isWelcomeEmail ? "VerificationToken.mail.body.registration.success"
                                : "VerificationToken.mail.body.new.token";

                final String subject = messageSource.getMessage(subjectCode, null, subjectCode, locale);
                final String message = new StringBuilder()
                                .append(messageSource.getMessage(bodyCode, null, bodyCode, locale)).append("\n") //
                                .append(endpoint) //
                                .append(verificationToken.getToken()) //
                                .toString();
                if (profileService.isAnyActive(Arrays.asList(Profiles.PROD_PROFILE, Profiles.TEST_VERIFICATION_TOKEN_PROFILE))) {
                        mailService.sendEmail(user.getUsername(), subject, message);
                }
        }
  
        protected Instant generateExpiryDate(final Instant startTime) {
                Objects.requireNonNull(startTime);
                return startTime.plus(TOKEN_EXPIRATION_IN_DAYS, ChronoUnit.DAYS);
        }

        protected String generateVerificationToken() {
                while (true) {
                        final String token = UUID.randomUUID().toString();
                        if (isTokenUnique(token)) {
                                return token;
                        }
                }
        }

        protected void verifyTokenExpiryDate(final VerificationToken verificationToken)
                        throws ApiVerificationTokenExpiredException {
                Objects.requireNonNull(verificationToken);

                if (verificationToken.getExpiryDate().isBefore(Instant.now())) {
                        throw new ApiVerificationTokenExpiredException("VerificationToken.token.expired",
                                        verificationToken.getToken());
                }
        }

}

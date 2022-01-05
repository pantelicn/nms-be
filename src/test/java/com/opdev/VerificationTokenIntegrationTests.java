package com.opdev;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.opdev.common.services.Profiles;
import com.opdev.company.dto.CompanyRegistrationDto;
import com.opdev.dto.LoginSuccessDto;
import com.opdev.dto.TalentRegistrationDto;
import com.opdev.dto.paging.RegistrationDto;
import com.opdev.model.user.User;
import com.opdev.model.user.VerificationToken;
import com.opdev.repository.UserRepository;
import com.opdev.repository.VerificationTokenRepository;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Rollback
@ActiveProfiles({Profiles.TEST_VERIFICATION_TOKEN_PROFILE, Profiles.TEST_PROFILE})
@Disabled
class VerificationTokenIntegrationTests extends AbstractIntegrationTest {

        @Autowired
        protected UserRepository userRepository;
        @Autowired
        protected VerificationTokenRepository verificationTokenRepository;

        @Test
        public void registerTalentAndTryToLoginWithoutVerification() {
                final TalentRegistrationDto newTalent = createNewTalent(null);
                registerTalent(newTalent);

                tryToLoginWithoutVerification(newTalent);
        }

        @Test
        public void registerTalentAndVerifyIt() {
                final TalentRegistrationDto newTalent = createNewTalent(null);
                registerTalent(newTalent);

                verifyEntity(newTalent);
        }

        @Test
        public void registerTalentAndAskForNewTokenAfterExpiration() {
                final TalentRegistrationDto newTalent = createNewTalent(null);
                registerTalent(newTalent);

                final User user = userRepository.findByUsername(newTalent.getUsername())
                                .orElseThrow(() -> new RuntimeException("The user wasn't registered."));
                List<VerificationToken> verificationTokens = verificationTokenRepository.findByUser(user);
                assertThat(verificationTokens, hasSize(1));

                final VerificationToken firstVerificationToken = verificationTokens.get(0).toBuilder()
                                .expiryDate(Instant.now().minus(10, ChronoUnit.DAYS)).build();
                verificationTokenRepository.saveAndFlush(firstVerificationToken);

                final String firstVerificationUrl = new StringBuilder().append(API_PREFIX) //
                                .append(VERIFICATION_API) //
                                .append("?token=") //
                                .append(firstVerificationToken.getToken()) //
                                .toString();
                final ResponseEntity<Void> firstVerificationResponse = restTemplate.getForEntity(firstVerificationUrl,
                                Void.class);
          
                assertThat(firstVerificationResponse.getStatusCode(), is(equalTo(HttpStatus.UNAUTHORIZED)));
                assertThat(firstVerificationResponse.getHeaders().getLocation(), is(notNullValue()));
                final String firstVerificationLocationHeader = firstVerificationResponse.getHeaders().getLocation()
                                .toASCIIString();
                assertThat(firstVerificationLocationHeader, containsString("/userverification?token="));

                final ResponseEntity<Void> generateNewTokenResponse = restTemplate
                                .postForEntity(firstVerificationLocationHeader, null, Void.class);
                assertThat(generateNewTokenResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));

                verificationTokens = verificationTokenRepository.findByUser(user);
                assertThat(verificationTokens, hasSize(2));

                final VerificationToken secondVerificationToken = verificationTokens.get(1);

                final String secondVerificationUrl = new StringBuilder().append(API_PREFIX) //
                                .append(VERIFICATION_API) //
                                .append("?token=") //
                                .append(secondVerificationToken.getToken()) //
                                .toString();

                final ResponseEntity<Void> secondVerificationResponse = restTemplate.getForEntity(secondVerificationUrl,
                                Void.class);
                assertThat(secondVerificationResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        }

        @Test
        public void registerCompanyAndTryToLoginWithoutVerification() {
                final CompanyRegistrationDto newCompany = createNewCompany(null);
                registerCompany(newCompany);

                tryToLoginWithoutVerification(newCompany);
        }

        @Test
        public void registerCompanyAndVerifyIt() {
                final CompanyRegistrationDto newCompany = createNewCompany(null);
                registerCompany(newCompany);

                verifyEntity(newCompany);
        }

        protected <T extends RegistrationDto> void tryToLoginWithoutVerification(final T newEntity) {
                final LoginDto loginDto = LoginDto.builder().username(newEntity.getUsername())
                                .password(newEntity.getPassword()).build();
                final ResponseEntity<Object> loginResponse = restTemplate.postForEntity("/login", loginDto,
                                Object.class);
                assertThat(loginResponse.getStatusCode(), is(equalTo(HttpStatus.UNAUTHORIZED)));
        }

        protected <T extends RegistrationDto> void verifyEntity(final T newEntity) {
                final User user = userRepository.findByUsername(newEntity.getUsername())
                                .orElseThrow(() -> new RuntimeException("The user wasn't registered."));
                final List<VerificationToken> verificationTokens = verificationTokenRepository.findByUser(user);
                assertThat(verificationTokens, hasSize(1));

                final String verificationUrl = new StringBuilder().append(API_PREFIX) //
                                .append(VERIFICATION_API) //
                                .append("?token=") //
                                .append(verificationTokens.get(0).getToken()) //
                                .toString();
                final ResponseEntity<Void> verificationResponse = restTemplate.getForEntity(verificationUrl,
                                Void.class);
                assertThat(verificationResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));

                final ResponseEntity<LoginSuccessDto> loginResponse = login(newEntity.getUsername(),
                                newEntity.getPassword());
                assertThat(loginResponse.getStatusCode(), is(equalTo(HttpStatus.OK)));
        }
}

package com.opdev;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.opdev.dto.TalentRegistrationDto;
import com.opdev.model.company.Company;
import com.opdev.model.location.CompanyLocation;
import com.opdev.model.talent.Talent;
import com.opdev.model.user.User;
import com.opdev.model.user.UserType;
import com.opdev.repository.CompanyRepository;
import com.opdev.repository.TalentRepository;
import com.opdev.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public abstract class AbstractIntegrationTest {

    protected static final String API_PREFIX = "/v1";
    protected static final String TALENTS_API = "/talents/";

    protected static final String DEFAULT_TALENT_FIRST_NAME = "Java";
    protected static final String DEFAULT_TALENT_LAST_NAME = "Dev";
    protected static final String DEFAULT_TALENT_PASSWORD = "java4life";

    protected static final String USER_POOL_CLIENT_ID = "3ci4jkku7sii8nb7ka12v5khpa";
    protected static final String USER_POOL_CLIENT_SECRET = "1gveddn2e7ch52uskkjbf8buci9p9ihjn5cg7up6t27o57dvha7";
    protected static final String USER_POOL_ID = "eu-central-1_46Qqd7W2Z";
    protected static final String AWS_ACCESS_KEY = "AKIAUMWR7AX6DECK2CEC";
    protected static final String AWS_SECRET_KEY = "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";
    protected static final String COMPANY_GOOGLE = "google@gmail.com";
    protected static final String COMPANY_FACEBOOK = "facebook@facebook.com";
    protected static final String TALENT_GORAN = "goransasic@gmail.com";
    protected static final String TALENT_NIKOLA = "nikola@gmail.com";
    protected static final String ADMIN = "admin@gmail.com";
    private final AWSCognitoIdentityProvider awsCognitoIDPClient  = AWSCognitoIdentityProviderClientBuilder.standard().withRegion(Regions.EU_CENTRAL_1)
            .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY)))
            .build();

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TalentRepository talentRepository;

    protected TalentRegistrationDto createNewTalent(final String username) {
        return TalentRegistrationDto.builder().firstName(DEFAULT_TALENT_FIRST_NAME).lastName(DEFAULT_TALENT_LAST_NAME)
                .username(username).password(DEFAULT_TALENT_PASSWORD).passwordConfirmed(DEFAULT_TALENT_PASSWORD)
                .build();
    }

    protected User createAdmin() {
        return userRepository.save(User.builder()
                                           .enabled(true)
                                           .type(UserType.ADMIN)
                                           .username(ADMIN)
                                           .build());
    }

    protected Talent createTalent(final String username) {
        User user = userRepository.save(User.builder()
                                                .enabled(true)
                                                .type(UserType.TALENT)
                                                .username(username)
                                                .build());
        Talent talent = Talent.builder()
                .firstName("goran")
                .lastName("sasic")
                .user(user)
                .available(true)
                .availabilityChangeDate(Instant.now())
                .build();

        return talentRepository.save(talent);
    }

    protected Company createCompany(final String username) {
        User user = userRepository.save(User.builder()
                .enabled(true)
                .type(UserType.COMPANY)
                .username(username)
                .build());
        CompanyLocation location = CompanyLocation.builder()
                .city("Novi Sad")
                .country("Serbia")
                .countryCode("RS")
                .address("Olge Petrov 27")
                .build();

        Company company = Company.builder()
                .description("google company")
                .name("Google")
                .location(location)
                .user(user)
                .build();
        return companyRepository.save(company);
    }

    protected HttpHeaders createAuthHeaders(final String token) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        return headers;
    }

    protected ResponseEntity<Void> disableTalent(final String token, final String username) {
        return disable(token, username, TALENTS_API);
    }

    private ResponseEntity<Void> delete(final String token, final String username, final String api) {
        final HttpEntity<Void> httpEntity = new HttpEntity<>(createAuthHeaders(token));

        final String endpoint = new StringBuilder().append(API_PREFIX).append(api).append(username).toString();

        return restTemplate.exchange(endpoint, HttpMethod.DELETE, httpEntity, Void.class);
    }

    private ResponseEntity<Void> disable(final String token, final String username, final String api) {
        final HttpEntity<Void> httpEntity = new HttpEntity<>(createAuthHeaders(token));
        final Map<String, String> urlParams = new HashMap<>();
        urlParams.put("disable", "true");

        final String endpoint = new StringBuilder().append(API_PREFIX).append(api).append(username)
                .append("?disable={disable}").toString();

        return restTemplate.exchange(endpoint, HttpMethod.DELETE, httpEntity, Void.class, urlParams);
    }

    protected String getTokenForAdmin() {
        String username = "admin@gmail.com";
        String password = "Admin12345!";
        Map<String,String> params = getCognitoAuthParams(username, password);
        return getTokenFromCognito(params);
    }

    protected String getTokenForTalentNikola() {
        String username = "nikola@gmail.com";
        String password = "Nikola12345!";
        Map<String,String> params = getCognitoAuthParams(username, password);
        return getTokenFromCognito(params);
    }

    protected String getTokenForTalentGoran() {
        String username = "goransasic@gmail.com";
        String password = "G0r@n123@1990";
        Map<String,String> params = getCognitoAuthParams(username, password);
        return getTokenFromCognito(params);
    }

    protected String getTokenForCompanyGoogle() {
        String username = "google@gmail.com";
        String password = "Google12345!";
        Map<String,String> params = getCognitoAuthParams(username, password);
        return getTokenFromCognito(params);
    }

    protected String getTokenForCompanyFacebook() {
        String username = "facebook@facebook.com";
        String password = "Facebook12345!";
        Map<String,String> params = getCognitoAuthParams(username, password);
        return getTokenFromCognito(params);
    }

    private String getTokenFromCognito(Map<String,String> params) {
        AdminInitiateAuthRequest initialRequest = new AdminInitiateAuthRequest()
                .withAuthFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
                .withAuthParameters(params)
                .withClientId(USER_POOL_CLIENT_ID)
                .withUserPoolId(USER_POOL_ID);

        AdminInitiateAuthResult initialResponse = awsCognitoIDPClient.adminInitiateAuth(initialRequest);
        return initialResponse.getAuthenticationResult().getIdToken();
    }

    private Map<String,String> getCognitoAuthParams(String username, String password) {
        Map<String,String>
                params =new HashMap<String,String>
                ();
        params.put("USERNAME", username);
        params.put("PASSWORD", password);
        params.put("SECRET_HASH", calculateSecretHash(USER_POOL_CLIENT_ID, USER_POOL_CLIENT_SECRET, username));
        return params;
    }

    private String calculateSecretHash(String userPoolClientId, String userPoolClientSecret, String userName) {
        final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

        SecretKeySpec signingKey = new SecretKeySpec(
                userPoolClientSecret.getBytes(StandardCharsets.UTF_8),
                HMAC_SHA256_ALGORITHM);
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(signingKey);
            mac.update(userName.getBytes(StandardCharsets.UTF_8));
            byte[] rawHmac = mac.doFinal(userPoolClientId.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Error while calculating ");
        }
    }

}

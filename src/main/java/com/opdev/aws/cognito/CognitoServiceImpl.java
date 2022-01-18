package com.opdev.aws.cognito;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupRequest;
import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupResult;
import com.amazonaws.services.cognitoidp.model.AdminDeleteUserRequest;
import com.amazonaws.services.cognitoidp.model.SignUpRequest;
import com.amazonaws.services.cognitoidp.model.SignUpResult;
import com.opdev.exception.CanNotAddGroupToUserException;
import com.opdev.exception.CanNotCreateUserOnCognito;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CognitoServiceImpl implements CognitoService {

    @Value("${nms.cognito.appClient.id}")
    private String userPoolClientId;
    @Value("${nms.cognito.appClient.secret}")
    private String userPoolClientSecret;
    @Value("${cognito.userpool.nms.poolId}")
    private String poolId;
    private final AWSCognitoIdentityProvider cognitoClient;
    private static final String COGNITO_GROUP_TALENT = "TALENT";
    private static final String COGNITO_GROUP_COMPANY = "COMPANY";

    @Override
    public void createTalent(final String username, final String password) {
        createUser(username, password, COGNITO_GROUP_TALENT);
    }

    @Override
    public void createCompany(final String username, final String password) {
        createUser(username, password, COGNITO_GROUP_COMPANY);
    }

    private void createUser(String username, String password, String group) {

        SignUpRequest signUpRequest = new SignUpRequest()
                .withClientId(userPoolClientId)
                .withSecretHash(calculateSecretHash(username))
                .withUsername(username)
                .withPassword(password);

        SignUpResult result = cognitoClient.signUp(signUpRequest);
        if (result.getSdkHttpMetadata().getHttpStatusCode() == 200) {
            addUserToGroup(username, group);
        } else {
            throw new CanNotCreateUserOnCognito(username);
        }
    }

    private void addUserToGroup(String username, String group) {
        AdminAddUserToGroupRequest addUserToGroupRequest = new AdminAddUserToGroupRequest()
                .withGroupName(group)
                .withUserPoolId(poolId)
                .withUsername(username);
        try {
            AdminAddUserToGroupResult result = cognitoClient.adminAddUserToGroup(addUserToGroupRequest);
            if (result.getSdkHttpMetadata().getHttpStatusCode() != 200) {
                removeUser(username);
                throw new CanNotAddGroupToUserException(username);
            }
        } catch (Exception e) {
            removeUser(username);
            throw new CanNotAddGroupToUserException(username);
        }
    }

    private void removeUser(String username) {
        AdminDeleteUserRequest request = new AdminDeleteUserRequest()
                .withUsername(username)
                .withUserPoolId(poolId);
        cognitoClient.adminDeleteUser(request);
    }

    private String calculateSecretHash(String userName) {
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

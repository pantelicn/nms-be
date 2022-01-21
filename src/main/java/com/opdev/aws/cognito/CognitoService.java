package com.opdev.aws.cognito;

public interface CognitoService {

    void createTalent(String username, String password);

    void createCompany(String username, String password);

}

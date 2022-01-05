package com.opdev.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * This exception should handle only the special case when:
 * 
 * 1. The user would like to register, and they enter their username (i.e.
 * email)
 * 
 * 2. The back-end checks if a user is already registered with the provided
 * email
 * 
 * 3. If it is, an exception is thrown saying that the user already exists with
 * the provided email.
 * 
 * This is a security concern called "Account Enumeration", that basically tells
 * attackers that the email is already present in our system, and that all they
 * need to do is to "guess" (i.e. brute-force) the password.
 * 
 * To prevent this, we should _always_ return 201 when the user registers
 * (regardless if the email is already taken.)
 * 
 * For more see
 * https://owasp.org/www-project-web-security-testing-guide/latest/4-Web_Application_Security_Testing/03-Identity_Management_Testing/04-Testing_for_Account_Enumeration_and_Guessable_User_Account
 * 
 */
@Builder
@Getter
public class ApiEmailExistsException extends ApiRuntimeException {

    private static final long serialVersionUID = 1L;

    @NonNull
    private final String message;

    @NonNull
    private final String entity;

    @NonNull
    private final String id;

}

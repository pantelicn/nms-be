package com.opdev.exception;

public class ApiSkillBadStatusException extends ApiRuntimeException {

    public ApiSkillBadStatusException(final String skillCode) {
        super(String.format("Skill with code %s is still in status PENDING!", skillCode));
    }

}

package com.opdev.exception;

import com.opdev.model.search.OperatorType;

public class FacetInvalidSkillException extends RuntimeException{

    public FacetInvalidSkillException(String code, OperatorType type) {
        super(String.format("Skill with code %s does not match with operator %s", code, type));
    }

}

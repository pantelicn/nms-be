package com.opdev.exception;

import com.opdev.model.search.OperatorType;

public class FacetInvalidTermException extends RuntimeException {

    public FacetInvalidTermException(String code, OperatorType type) {
        super(String.format("Term with code %s does not match with operator %s", code, type));
    }

}

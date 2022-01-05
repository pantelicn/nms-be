package com.opdev.exception;

import com.opdev.model.search.OperatorType;

public class FacetInvalidPositionException extends RuntimeException {

    public FacetInvalidPositionException(String code, OperatorType type) {
        super(String.format("Position with code %s does not match with operator %s", code, type));
    }

}

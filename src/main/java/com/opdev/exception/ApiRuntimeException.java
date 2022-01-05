package com.opdev.exception;

import java.util.function.BooleanSupplier;

public class ApiRuntimeException extends RuntimeException {

    public ApiRuntimeException() {
        super();
    }

    public ApiRuntimeException(String message) {
        super(message);
    }

    /**
     * If expression evaluates to true, throws the exception, otherwise does nothing.
     * @param supplier the supplying function that produces the boolean value to be evaluated
     */
    public void throwIf(BooleanSupplier supplier) {
        if (supplier.getAsBoolean()) {
            throw this;
        }
    }

}

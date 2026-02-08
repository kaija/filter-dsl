package com.filter.dsl.functions;

/**
 * Exception thrown when a function receives an argument of an incorrect type.
 */
public class TypeMismatchException extends RuntimeException {

    public TypeMismatchException(String message) {
        super(message);
    }

    public TypeMismatchException(String message, Throwable cause) {
        super(message, cause);
    }
}

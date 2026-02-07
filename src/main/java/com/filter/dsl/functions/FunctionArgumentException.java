package com.filter.dsl.functions;

/**
 * Exception thrown when a function receives an incorrect number of arguments.
 */
public class FunctionArgumentException extends RuntimeException {
    
    public FunctionArgumentException(String message) {
        super(message);
    }
    
    public FunctionArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}

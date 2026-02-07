package com.filter.dsl.evaluator;

/**
 * Result of evaluating a DSL expression.
 * 
 * Contains the evaluation outcome including:
 * - Success status
 * - Result value (Boolean or computed value)
 * - Error information if evaluation failed
 * - Performance metrics
 */
public class EvaluationResult {
    private final boolean success;
    private final Object value;
    private final String errorMessage;
    private final ErrorType errorType;
    private final long evaluationTimeMs;
    private final String expression;

    private EvaluationResult(Builder builder) {
        this.success = builder.success;
        this.value = builder.value;
        this.errorMessage = builder.errorMessage;
        this.errorType = builder.errorType;
        this.evaluationTimeMs = builder.evaluationTimeMs;
        this.expression = builder.expression;
    }

    /**
     * Check if the evaluation was successful.
     * 
     * @return true if the expression evaluated successfully
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Get the evaluation result value.
     * 
     * This can be:
     * - Boolean for filtering expressions
     * - Number for computed numeric values
     * - String for computed string values
     * - null if evaluation failed
     * 
     * @return The result value, or null if evaluation failed
     */
    public Object getValue() {
        return value;
    }

    /**
     * Get the error message if evaluation failed.
     * 
     * @return Error message, or null if successful
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Get the type of error that occurred.
     * 
     * @return ErrorType, or null if successful
     */
    public ErrorType getErrorType() {
        return errorType;
    }

    /**
     * Get the evaluation time in milliseconds.
     * 
     * @return Evaluation time in milliseconds
     */
    public long getEvaluationTimeMs() {
        return evaluationTimeMs;
    }

    /**
     * Get the expression that was evaluated.
     * 
     * @return The DSL expression string
     */
    public String getExpression() {
        return expression;
    }

    /**
     * Create a successful evaluation result.
     * 
     * @param expression The expression that was evaluated
     * @param value The result value
     * @param evaluationTimeMs The evaluation time in milliseconds
     * @return A successful EvaluationResult
     */
    public static EvaluationResult success(String expression, Object value, long evaluationTimeMs) {
        return new Builder()
            .success(true)
            .expression(expression)
            .value(value)
            .evaluationTimeMs(evaluationTimeMs)
            .build();
    }

    /**
     * Create a failed evaluation result.
     * 
     * @param expression The expression that was evaluated
     * @param errorType The type of error
     * @param errorMessage The error message
     * @param evaluationTimeMs The evaluation time in milliseconds
     * @return A failed EvaluationResult
     */
    public static EvaluationResult error(String expression, ErrorType errorType, 
                                        String errorMessage, long evaluationTimeMs) {
        return new Builder()
            .success(false)
            .expression(expression)
            .errorType(errorType)
            .errorMessage(errorMessage)
            .evaluationTimeMs(evaluationTimeMs)
            .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean success;
        private Object value;
        private String errorMessage;
        private ErrorType errorType;
        private long evaluationTimeMs;
        private String expression;

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder value(Object value) {
            this.value = value;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public Builder errorType(ErrorType errorType) {
            this.errorType = errorType;
            return this;
        }

        public Builder evaluationTimeMs(long evaluationTimeMs) {
            this.evaluationTimeMs = evaluationTimeMs;
            return this;
        }

        public Builder expression(String expression) {
            this.expression = expression;
            return this;
        }

        public EvaluationResult build() {
            return new EvaluationResult(this);
        }
    }

    @Override
    public String toString() {
        if (success) {
            return "EvaluationResult{success=true, value=" + value + 
                   ", time=" + evaluationTimeMs + "ms}";
        } else {
            return "EvaluationResult{success=false, errorType=" + errorType + 
                   ", error='" + errorMessage + "', time=" + evaluationTimeMs + "ms}";
        }
    }

    /**
     * Types of errors that can occur during evaluation.
     */
    public enum ErrorType {
        /**
         * Syntax error in the DSL expression.
         */
        SYNTAX_ERROR,
        
        /**
         * Validation error (undefined function, wrong argument count, etc.).
         */
        VALIDATION_ERROR,
        
        /**
         * Runtime error during expression evaluation.
         */
        RUNTIME_ERROR,
        
        /**
         * Error with the input user data.
         */
        DATA_ERROR,
        
        /**
         * Compilation error when compiling the expression.
         */
        COMPILATION_ERROR
    }
}

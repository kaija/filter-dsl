package com.filter.dsl.parser;

/**
 * Result of parsing a DSL expression.
 * Contains validation status, error information, and formatted expression.
 */
public class ParseResult {
    private final boolean valid;
    private final String errorMessage;
    private final Integer errorPosition;
    private final String formattedExpression;
    private final String originalExpression;

    private ParseResult(Builder builder) {
        this.valid = builder.valid;
        this.errorMessage = builder.errorMessage;
        this.errorPosition = builder.errorPosition;
        this.formattedExpression = builder.formattedExpression;
        this.originalExpression = builder.originalExpression;
    }

    /**
     * Check if the expression is valid.
     * 
     * @return true if the expression passed all validation checks
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Get the error message if validation failed.
     * 
     * @return Error message, or null if valid
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Get the position in the expression where the error occurred.
     * 
     * @return Error position (0-based index), or null if valid or position unknown
     */
    public Integer getErrorPosition() {
        return errorPosition;
    }

    /**
     * Get the formatted (pretty-printed) expression.
     * 
     * @return Formatted expression, or null if invalid
     */
    public String getFormattedExpression() {
        return formattedExpression;
    }

    /**
     * Get the original expression that was parsed.
     * 
     * @return Original expression string
     */
    public String getOriginalExpression() {
        return originalExpression;
    }

    /**
     * Create a successful parse result.
     * 
     * @param originalExpression The original expression
     * @param formattedExpression The formatted expression
     * @return A valid ParseResult
     */
    public static ParseResult success(String originalExpression, String formattedExpression) {
        return new Builder()
            .valid(true)
            .originalExpression(originalExpression)
            .formattedExpression(formattedExpression)
            .build();
    }

    /**
     * Create a failed parse result with error information.
     * 
     * @param originalExpression The original expression
     * @param errorMessage The error message
     * @param errorPosition The position where the error occurred (optional)
     * @return An invalid ParseResult
     */
    public static ParseResult error(String originalExpression, String errorMessage, Integer errorPosition) {
        return new Builder()
            .valid(false)
            .originalExpression(originalExpression)
            .errorMessage(errorMessage)
            .errorPosition(errorPosition)
            .build();
    }

    /**
     * Create a failed parse result without position information.
     * 
     * @param originalExpression The original expression
     * @param errorMessage The error message
     * @return An invalid ParseResult
     */
    public static ParseResult error(String originalExpression, String errorMessage) {
        return error(originalExpression, errorMessage, null);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean valid;
        private String errorMessage;
        private Integer errorPosition;
        private String formattedExpression;
        private String originalExpression;

        public Builder valid(boolean valid) {
            this.valid = valid;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public Builder errorPosition(Integer errorPosition) {
            this.errorPosition = errorPosition;
            return this;
        }

        public Builder formattedExpression(String formattedExpression) {
            this.formattedExpression = formattedExpression;
            return this;
        }

        public Builder originalExpression(String originalExpression) {
            this.originalExpression = originalExpression;
            return this;
        }

        public ParseResult build() {
            return new ParseResult(this);
        }
    }

    @Override
    public String toString() {
        if (valid) {
            return "ParseResult{valid=true, formatted=" + formattedExpression + "}";
        } else {
            return "ParseResult{valid=false, error='" + errorMessage + "'" +
                   (errorPosition != null ? ", position=" + errorPosition : "") + "}";
        }
    }
}

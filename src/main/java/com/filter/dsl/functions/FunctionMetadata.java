package com.filter.dsl.functions;

import java.util.ArrayList;
import java.util.List;

/**
 * Metadata describing a DSL function's signature and behavior.
 * Used for validation and documentation.
 */
public class FunctionMetadata {
    private final String name;
    private final int minArgs;
    private final int maxArgs;
    private final List<ArgumentType> argumentTypes;
    private final ReturnType returnType;
    private final String description;

    private FunctionMetadata(Builder builder) {
        this.name = builder.name;
        this.minArgs = builder.minArgs;
        this.maxArgs = builder.maxArgs;
        this.argumentTypes = builder.argumentTypes;
        this.returnType = builder.returnType;
        this.description = builder.description;
    }

    public String getName() {
        return name;
    }

    public int getMinArgs() {
        return minArgs;
    }

    public int getMaxArgs() {
        return maxArgs;
    }

    public List<ArgumentType> getArgumentTypes() {
        return argumentTypes;
    }

    public ReturnType getReturnType() {
        return returnType;
    }

    public String getDescription() {
        return description;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private int minArgs;
        private int maxArgs;
        private List<ArgumentType> argumentTypes = new ArrayList<>();
        private ReturnType returnType;
        private String description;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder minArgs(int minArgs) {
            this.minArgs = minArgs;
            return this;
        }

        public Builder maxArgs(int maxArgs) {
            this.maxArgs = maxArgs;
            return this;
        }

        public Builder argumentType(int index, ArgumentType type) {
            // Ensure list is large enough
            while (argumentTypes.size() <= index) {
                argumentTypes.add(ArgumentType.ANY);
            }
            argumentTypes.set(index, type);
            return this;
        }

        public Builder returnType(ReturnType returnType) {
            this.returnType = returnType;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public FunctionMetadata build() {
            return new FunctionMetadata(this);
        }
    }

    /**
     * Argument type categories for validation.
     */
    public enum ArgumentType {
        ANY,
        NUMBER,
        STRING,
        BOOLEAN,
        COLLECTION,
        DATE,
        OBJECT
    }

    /**
     * Return type categories for validation.
     */
    public enum ReturnType {
        ANY,
        NUMBER,
        STRING,
        BOOLEAN,
        COLLECTION,
        DATE,
        VOID
    }
}

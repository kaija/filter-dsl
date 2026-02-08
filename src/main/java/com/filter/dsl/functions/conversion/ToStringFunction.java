package com.filter.dsl.functions.conversion;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import com.googlecode.aviator.runtime.type.AviatorNil;

import java.util.Map;
import java.util.Collection;

/**
 * TO_STRING function - Converts any value to its string representation.
 *
 * Usage: TO_STRING(value)
 *
 * Examples:
 * - TO_STRING(123) -> "123"
 * - TO_STRING(45.67) -> "45.67"
 * - TO_STRING(true) -> "true"
 * - TO_STRING(false) -> "false"
 * - TO_STRING("hello") -> "hello" (already a string)
 * - TO_STRING(null) -> null
 * - TO_STRING([1, 2, 3]) -> "[1, 2, 3]"
 *
 * Conversion rules:
 * - Numbers: Converted to decimal string representation
 * - Booleans: "true" or "false"
 * - Strings: Returned as-is
 * - Collections/Arrays: String representation using toString()
 * - null: Returns null
 * - Other objects: Uses toString() method
 *
 * Requirements: 10.2
 */
public class ToStringFunction extends DSLFunction {

    @Override
    public String getName() {
        return "TO_STRING";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("TO_STRING")
            .minArgs(1)
            .maxArgs(1)
            .argumentType(0, ArgumentType.ANY)
            .returnType(ReturnType.STRING)
            .description("Converts any value to its string representation")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 1);

        Object value = getValue(args[0], env);

        // Handle null - return null as per design
        if (value == null) {
            return AviatorNil.NIL;
        }

        // Convert value to string
        String result = convertToString(value);

        return new AviatorString(result);
    }

    /**
     * Convert a value to its string representation.
     *
     * @param value The value to convert
     * @return String representation
     */
    private String convertToString(Object value) {
        // Already a string
        if (value instanceof String) {
            return (String) value;
        }

        // Boolean
        if (value instanceof Boolean) {
            return value.toString();
        }

        // Number - use appropriate formatting
        if (value instanceof Number) {
            Number num = (Number) value;

            // For integers, don't show decimal point
            if (num instanceof Integer || num instanceof Long) {
                return String.valueOf(num.longValue());
            }

            // For floating-point, use standard toString
            // This avoids scientific notation for reasonable values
            double d = num.doubleValue();

            // Check if it's effectively an integer
            if (d == Math.floor(d) && !Double.isInfinite(d)) {
                return String.valueOf((long) d);
            }

            return String.valueOf(d);
        }

        // Collection - use toString
        if (value instanceof Collection) {
            return value.toString();
        }

        // Array - convert to string
        if (value.getClass().isArray()) {
            return arrayToString(value);
        }

        // Default: use toString()
        return value.toString();
    }

    /**
     * Convert an array to string representation.
     *
     * @param array The array to convert
     * @return String representation
     */
    private String arrayToString(Object array) {
        int length = java.lang.reflect.Array.getLength(array);

        if (length == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            Object item = java.lang.reflect.Array.get(array, i);
            sb.append(item == null ? "null" : convertToString(item));
        }
        sb.append("]");

        return sb.toString();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        return call(env, new AviatorObject[]{arg1});
    }
}

package com.filter.dsl.functions.conversion;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorBoolean;

import java.util.Map;
import java.util.Collection;

/**
 * TO_BOOLEAN function - Converts values to boolean using standard truthiness rules.
 *
 * Usage: TO_BOOLEAN(value)
 *
 * Examples:
 * - TO_BOOLEAN(true) -> true
 * - TO_BOOLEAN(false) -> false
 * - TO_BOOLEAN(1) -> true
 * - TO_BOOLEAN(0) -> false
 * - TO_BOOLEAN(42) -> true (any non-zero number)
 * - TO_BOOLEAN("true") -> true
 * - TO_BOOLEAN("false") -> false
 * - TO_BOOLEAN("yes") -> true
 * - TO_BOOLEAN("no") -> false
 * - TO_BOOLEAN("") -> false (empty string)
 * - TO_BOOLEAN("hello") -> true (non-empty string)
 * - TO_BOOLEAN(null) -> false
 * - TO_BOOLEAN([]) -> false (empty collection)
 * - TO_BOOLEAN([1, 2]) -> true (non-empty collection)
 *
 * Truthiness rules:
 * - Booleans: true -> true, false -> false
 * - Numbers: 0 -> false, non-zero -> true
 * - Strings:
 *   - Empty string -> false
 *   - "false", "False", "FALSE", "no", "No", "NO", "0" -> false
 *   - "true", "True", "TRUE", "yes", "Yes", "YES", "1" -> true
 *   - Other non-empty strings -> true
 * - Collections/Arrays: empty -> false, non-empty -> true
 * - null: false
 * - Other objects: true (if not null)
 *
 * Requirements: 10.3
 */
public class ToBooleanFunction extends DSLFunction {

    @Override
    public String getName() {
        return "TO_BOOLEAN";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("TO_BOOLEAN")
            .minArgs(1)
            .maxArgs(1)
            .argumentType(0, ArgumentType.ANY)
            .returnType(ReturnType.BOOLEAN)
            .description("Converts values to boolean using standard truthiness rules")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 1);

        Object value = getValue(args[0], env);

        // Convert to boolean using truthiness rules
        boolean result = isTruthy(value);

        return AviatorBoolean.valueOf(result);
    }

    /**
     * Determine if a value is truthy according to standard truthiness rules.
     *
     * @param value The value to evaluate
     * @return true if the value is truthy, false otherwise
     */
    private boolean isTruthy(Object value) {
        // null is falsy
        if (value == null) {
            return false;
        }

        // Boolean - use value directly
        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        // Number - zero is falsy, non-zero is truthy
        if (value instanceof Number) {
            Number num = (Number) value;

            // Handle different number types
            if (num instanceof Double || num instanceof Float) {
                return num.doubleValue() != 0.0;
            } else {
                return num.longValue() != 0L;
            }
        }

        // String - empty or specific false values are falsy
        if (value instanceof String) {
            String str = ((String) value).trim();

            // Empty string is falsy
            if (str.isEmpty()) {
                return false;
            }

            // Check for explicit false values (case-insensitive)
            String lower = str.toLowerCase();
            if (lower.equals("false") || lower.equals("no") || lower.equals("0")) {
                return false;
            }

            // Check for explicit true values (case-insensitive)
            if (lower.equals("true") || lower.equals("yes") || lower.equals("1")) {
                return true;
            }

            // Any other non-empty string is truthy
            return true;
        }

        // Collection - empty is falsy, non-empty is truthy
        if (value instanceof Collection) {
            return !((Collection<?>) value).isEmpty();
        }

        // Array - empty is falsy, non-empty is truthy
        if (value.getClass().isArray()) {
            return java.lang.reflect.Array.getLength(value) > 0;
        }

        // Any other non-null object is truthy
        return true;
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        return call(env, new AviatorObject[]{arg1});
    }
}

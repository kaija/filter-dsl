package com.filter.dsl.functions.comparison;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorBoolean;

import java.util.Map;
import java.util.Objects;

/**
 * EQ (Equals) function - Returns true if operands are equal.
 * 
 * Usage: EQ(a, b)
 * 
 * Examples:
 * - EQ(5, 5) -> true
 * - EQ(5, 3) -> false
 * - EQ("hello", "hello") -> true
 * - EQ("hello", "world") -> false
 * - EQ(true, true) -> true
 * - EQ(10.0, 10) -> true
 * 
 * The function performs equality comparison with proper type handling.
 * Works with numbers, strings, booleans, and other comparable types.
 * For numeric comparisons, handles type coercion (e.g., 10.0 equals 10).
 */
public class EqualsFunction extends DSLFunction {

    @Override
    public String getName() {
        return "EQ";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("EQ")
            .minArgs(2)
            .maxArgs(2)
            .argumentType(0, ArgumentType.ANY)
            .argumentType(1, ArgumentType.ANY)
            .returnType(ReturnType.BOOLEAN)
            .description("Returns true if operands are equal")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        validateArgCount(new AviatorObject[]{arg1, arg2}, 2);
        
        Object value1 = getValue(arg1, env);
        Object value2 = getValue(arg2, env);
        
        // Handle null cases
        if (value1 == null && value2 == null) {
            return AviatorBoolean.TRUE;
        }
        if (value1 == null || value2 == null) {
            return AviatorBoolean.FALSE;
        }
        
        // Special handling for numeric comparisons to handle type coercion
        if (value1 instanceof Number && value2 instanceof Number) {
            double d1 = ((Number) value1).doubleValue();
            double d2 = ((Number) value2).doubleValue();
            return d1 == d2 ? AviatorBoolean.TRUE : AviatorBoolean.FALSE;
        }
        
        // For non-numeric types, use standard equals
        return Objects.equals(value1, value2) ? AviatorBoolean.TRUE : AviatorBoolean.FALSE;
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 2);
        return call(env, args[0], args[1]);
    }
}

package com.filter.dsl.functions.conversion;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.filter.dsl.functions.TypeMismatchException;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorDouble;
import com.googlecode.aviator.runtime.type.AviatorLong;

import java.util.Map;

/**
 * TO_NUMBER function - Converts string or boolean values to numbers.
 * 
 * Usage: TO_NUMBER(value)
 * 
 * Examples:
 * - TO_NUMBER("123") -> 123
 * - TO_NUMBER("45.67") -> 45.67
 * - TO_NUMBER(true) -> 1
 * - TO_NUMBER(false) -> 0
 * - TO_NUMBER(42) -> 42 (already a number)
 * - TO_NUMBER("not a number") -> Error
 * - TO_NUMBER(null) -> Error
 * 
 * Conversion rules:
 * - Strings: Parsed as decimal numbers (supports integers and floating-point)
 * - Booleans: true -> 1, false -> 0
 * - Numbers: Returned as-is
 * - Other types: Error
 * 
 * Requirements: 10.1, 10.7
 */
public class ToNumberFunction extends DSLFunction {

    @Override
    public String getName() {
        return "TO_NUMBER";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("TO_NUMBER")
            .minArgs(1)
            .maxArgs(1)
            .argumentType(0, ArgumentType.ANY)
            .returnType(ReturnType.NUMBER)
            .description("Converts string or boolean values to numbers")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 1);
        
        Object value = getValue(args[0], env);
        
        // Handle null - error as per Requirements 10.7
        if (value == null) {
            throw new TypeMismatchException(
                "TO_NUMBER cannot convert null to number"
            );
        }
        
        // Already a number - return as-is
        if (value instanceof Number) {
            Number num = (Number) value;
            // Return Long for integers, Double for floating-point
            if (num instanceof Integer || num instanceof Long) {
                return AviatorLong.valueOf(num.longValue());
            } else {
                return AviatorDouble.valueOf(num.doubleValue());
            }
        }
        
        // Boolean conversion: true -> 1, false -> 0
        if (value instanceof Boolean) {
            return AviatorLong.valueOf(((Boolean) value) ? 1L : 0L);
        }
        
        // String conversion
        if (value instanceof String) {
            String str = ((String) value).trim();
            
            if (str.isEmpty()) {
                throw new TypeMismatchException(
                    "TO_NUMBER cannot convert empty string to number"
                );
            }
            
            try {
                // Try to parse as integer first
                if (!str.contains(".") && !str.toLowerCase().contains("e")) {
                    return AviatorLong.valueOf(Long.parseLong(str));
                } else {
                    // Parse as floating-point
                    return AviatorDouble.valueOf(Double.parseDouble(str));
                }
            } catch (NumberFormatException e) {
                throw new TypeMismatchException(
                    "TO_NUMBER cannot convert string '" + str + "' to number: " + e.getMessage()
                );
            }
        }
        
        // Incompatible type - error as per Requirements 10.7
        throw new TypeMismatchException(
            "TO_NUMBER cannot convert " + value.getClass().getSimpleName() + " to number"
        );
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        return call(env, new AviatorObject[]{arg1});
    }
}

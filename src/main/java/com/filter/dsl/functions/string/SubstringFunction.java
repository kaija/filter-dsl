package com.filter.dsl.functions.string;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import com.googlecode.aviator.runtime.type.AviatorNil;

import java.util.Map;

/**
 * SUBSTRING function - Extracts a substring from a string.
 * 
 * Usage: SUBSTRING(string, start) or SUBSTRING(string, start, length)
 * 
 * Examples:
 * - SUBSTRING("hello world", 0) -> "hello world"
 * - SUBSTRING("hello world", 6) -> "world"
 * - SUBSTRING("hello world", 0, 5) -> "hello"
 * - SUBSTRING("hello world", 6, 3) -> "wor"
 * - SUBSTRING("hello", 10) -> ""
 * - SUBSTRING("hello", -1) -> "o" (negative index from end)
 * - SUBSTRING(null, 0) -> null
 * 
 * The start parameter is 0-based. If start is negative, it counts from the end.
 * If length is omitted, extracts to the end of the string.
 * If start or length are out of bounds, they are adjusted to valid ranges.
 * Null inputs are handled gracefully - returns null if the string is null.
 */
public class SubstringFunction extends DSLFunction {

    @Override
    public String getName() {
        return "SUBSTRING";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("SUBSTRING")
            .minArgs(2)
            .maxArgs(3)
            .argumentType(0, ArgumentType.STRING)
            .argumentType(1, ArgumentType.NUMBER)
            .argumentType(2, ArgumentType.NUMBER)
            .returnType(ReturnType.STRING)
            .description("Extracts a substring from a string starting at the given position with optional length")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        return call(env, new AviatorObject[]{arg1, arg2});
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3) {
        return call(env, new AviatorObject[]{arg1, arg2, arg3});
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCountRange(args, 2, 3);
        
        // Convert arguments
        String str = toString(args[0], env);
        
        // Handle null input gracefully
        if (str == null) {
            return AviatorNil.NIL;
        }
        
        Number startNum = toNumber(args[1], env);
        int start = startNum.intValue();
        
        // Handle negative start index (count from end)
        if (start < 0) {
            start = str.length() + start;
        }
        
        // Clamp start to valid range
        start = Math.max(0, Math.min(start, str.length()));
        
        // If length is provided
        if (args.length == 3) {
            Number lengthNum = toNumber(args[2], env);
            int length = lengthNum.intValue();
            
            // Clamp length to valid range
            int end = Math.min(start + length, str.length());
            
            String result = str.substring(start, end);
            return new AviatorString(result);
        } else {
            // Extract to end of string
            String result = str.substring(start);
            return new AviatorString(result);
        }
    }
}

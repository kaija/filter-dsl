package com.filter.dsl.functions.string;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorLong;
import com.googlecode.aviator.runtime.type.AviatorNil;

import java.util.Map;

/**
 * LENGTH function - Returns the length of a string.
 * 
 * Usage: LENGTH(string)
 * 
 * Examples:
 * - LENGTH("hello") -> 5
 * - LENGTH("hello world") -> 11
 * - LENGTH("") -> 0
 * - LENGTH("  ") -> 2
 * - LENGTH(null) -> null
 * 
 * The function returns the number of characters in the string.
 * Whitespace characters are counted.
 * Null inputs are handled gracefully - returns null if the argument is null.
 */
public class LengthFunction extends DSLFunction {

    @Override
    public String getName() {
        return "LENGTH";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("LENGTH")
            .minArgs(1)
            .maxArgs(1)
            .argumentType(0, ArgumentType.STRING)
            .returnType(ReturnType.NUMBER)
            .description("Returns the length of a string")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        validateArgCount(new AviatorObject[]{arg1}, 1);
        
        // Convert argument to string
        String str = toString(arg1, env);
        
        // Handle null input gracefully
        if (str == null) {
            return AviatorNil.NIL;
        }
        
        // Get string length
        int length = str.length();
        
        return AviatorLong.valueOf(length);
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 1);
        return call(env, args[0]);
    }
}

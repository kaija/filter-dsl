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
 * LOWER function - Converts a string to lowercase.
 * 
 * Usage: LOWER(string)
 * 
 * Examples:
 * - LOWER("HELLO") -> "hello"
 * - LOWER("Hello World") -> "hello world"
 * - LOWER("already lower") -> "already lower"
 * - LOWER("") -> ""
 * - LOWER(null) -> null
 * 
 * The function converts all characters in the string to lowercase using the default locale.
 * Null inputs are handled gracefully - returns null if the argument is null.
 */
public class LowerFunction extends DSLFunction {

    @Override
    public String getName() {
        return "LOWER";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("LOWER")
            .minArgs(1)
            .maxArgs(1)
            .argumentType(0, ArgumentType.STRING)
            .returnType(ReturnType.STRING)
            .description("Converts a string to lowercase")
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
        
        // Convert to lowercase
        String result = str.toLowerCase();
        
        return new AviatorString(result);
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 1);
        return call(env, args[0]);
    }
}

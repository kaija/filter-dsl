package com.filter.dsl.functions.string;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorRuntimeJavaType;
import com.googlecode.aviator.runtime.type.AviatorNil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * SPLIT function - Splits a string into an array by a delimiter.
 * 
 * Usage: SPLIT(string, delimiter)
 * 
 * Examples:
 * - SPLIT("hello,world", ",") -> ["hello", "world"]
 * - SPLIT("a-b-c", "-") -> ["a", "b", "c"]
 * - SPLIT("hello", ",") -> ["hello"]
 * - SPLIT("a,b,c,", ",") -> ["a", "b", "c", ""]
 * - SPLIT("", ",") -> [""]
 * - SPLIT(null, ",") -> null
 * 
 * The function splits the string by the delimiter and returns an array of strings.
 * If the delimiter is not found, returns an array with the original string.
 * If the delimiter is empty, splits into individual characters.
 * Null inputs are handled gracefully - returns null if the string is null.
 */
public class SplitFunction extends DSLFunction {

    @Override
    public String getName() {
        return "SPLIT";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("SPLIT")
            .minArgs(2)
            .maxArgs(2)
            .argumentType(0, ArgumentType.STRING)
            .argumentType(1, ArgumentType.STRING)
            .returnType(ReturnType.COLLECTION)
            .description("Splits a string into an array by a delimiter")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        validateArgCount(new AviatorObject[]{arg1, arg2}, 2);
        
        // Convert arguments to strings
        String str = toString(arg1, env);
        String delimiter = toString(arg2, env);
        
        // Handle null input gracefully
        if (str == null) {
            return AviatorNil.NIL;
        }
        
        // Handle null delimiter (treat as empty string)
        if (delimiter == null) {
            delimiter = "";
        }
        
        // Split the string
        String[] parts;
        if (delimiter.isEmpty()) {
            // Split into individual characters
            parts = str.split("");
        } else {
            // Split by delimiter (use literal split, not regex)
            parts = str.split(java.util.regex.Pattern.quote(delimiter), -1);
        }
        
        // Convert array to list
        List<String> result = Arrays.asList(parts);
        
        return AviatorRuntimeJavaType.valueOf(result);
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 2);
        return call(env, args[0], args[1]);
    }
}

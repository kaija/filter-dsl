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
 * REPLACE function - Replaces all occurrences of a search string with a replacement string.
 * 
 * Usage: REPLACE(string, search, replacement)
 * 
 * Examples:
 * - REPLACE("hello world", "world", "there") -> "hello there"
 * - REPLACE("hello hello", "hello", "hi") -> "hi hi"
 * - REPLACE("hello world", "foo", "bar") -> "hello world"
 * - REPLACE("hello", "", "x") -> "hello" (empty search returns original)
 * - REPLACE("", "hello", "world") -> ""
 * - REPLACE(null, "hello", "world") -> null
 * 
 * The function replaces all occurrences of the search string with the replacement string.
 * The replacement is case-sensitive.
 * Null inputs are handled gracefully - returns null if the string is null.
 */
public class ReplaceFunction extends DSLFunction {

    @Override
    public String getName() {
        return "REPLACE";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("REPLACE")
            .minArgs(3)
            .maxArgs(3)
            .argumentType(0, ArgumentType.STRING)
            .argumentType(1, ArgumentType.STRING)
            .argumentType(2, ArgumentType.STRING)
            .returnType(ReturnType.STRING)
            .description("Replaces all occurrences of a search string with a replacement string")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3) {
        return call(env, new AviatorObject[]{arg1, arg2, arg3});
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 3);
        
        // Convert arguments to strings
        String str = toString(args[0], env);
        String search = toString(args[1], env);
        String replacement = toString(args[2], env);
        
        // Handle null input gracefully
        if (str == null) {
            return AviatorNil.NIL;
        }
        
        // Handle null search or replacement (treat as empty string)
        if (search == null) {
            search = "";
        }
        if (replacement == null) {
            replacement = "";
        }
        
        // If search is empty, return original string
        if (search.isEmpty()) {
            return new AviatorString(str);
        }
        
        // Replace all occurrences
        String result = str.replace(search, replacement);
        
        return new AviatorString(result);
    }
}

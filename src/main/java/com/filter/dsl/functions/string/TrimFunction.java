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
 * TRIM function - Removes leading and trailing whitespace from a string.
 *
 * Usage: TRIM(string)
 *
 * Examples:
 * - TRIM("  hello  ") -> "hello"
 * - TRIM("hello") -> "hello"
 * - TRIM("  hello world  ") -> "hello world"
 * - TRIM("\t\nhello\n\t") -> "hello"
 * - TRIM("") -> ""
 * - TRIM(null) -> null
 *
 * The function removes all leading and trailing whitespace characters including
 * spaces, tabs, newlines, and other Unicode whitespace characters.
 * Null inputs are handled gracefully - returns null if the argument is null.
 */
public class TrimFunction extends DSLFunction {

    @Override
    public String getName() {
        return "TRIM";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("TRIM")
            .minArgs(1)
            .maxArgs(1)
            .argumentType(0, ArgumentType.STRING)
            .returnType(ReturnType.STRING)
            .description("Removes leading and trailing whitespace from a string")
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

        // Trim whitespace
        String result = str.trim();

        return new AviatorString(result);
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 1);
        return call(env, args[0]);
    }
}

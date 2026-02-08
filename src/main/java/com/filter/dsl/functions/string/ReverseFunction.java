package com.filter.dsl.functions.string;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;

import java.util.Map;

/**
 * REVERSE function - reverses a string.
 *
 * Usage: REVERSE("hello") returns "olleh"
 */
public class ReverseFunction extends DSLFunction {

    @Override
    public String getName() {
        return "REVERSE";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("REVERSE")
            .minArgs(1)
            .maxArgs(1)
            .argumentType(0, ArgumentType.STRING)
            .returnType(ReturnType.STRING)
            .description("Reverses a string")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        // Convert argument to string
        String input = toString(arg1, env);

        // Handle null input
        if (input == null) {
            return new AviatorString("");
        }

        // Reverse the string
        String reversed = new StringBuilder(input).reverse().toString();

        // Return as AviatorString
        return new AviatorString(reversed);
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        // Validate argument count
        validateArgCount(args, 1);

        // Delegate to the single-argument version
        return call(env, args[0]);
    }
}

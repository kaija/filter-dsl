package com.filter.dsl.functions.string;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorBoolean;

import java.util.Map;

/**
 * STARTS_WITH function - Returns true if the string starts with the prefix.
 *
 * Usage: STARTS_WITH(string, prefix)
 *
 * Examples:
 * - STARTS_WITH("hello world", "hello") -> true
 * - STARTS_WITH("hello world", "world") -> false
 * - STARTS_WITH("hello", "hello") -> true
 * - STARTS_WITH("hello", "Hello") -> false (case-sensitive)
 * - STARTS_WITH("", "test") -> false
 * - STARTS_WITH("test", "") -> true (any string starts with empty string)
 *
 * The function performs case-sensitive prefix matching.
 * Null inputs are handled gracefully - returns false if either argument is null.
 */
public class StartsWithFunction extends DSLFunction {

    @Override
    public String getName() {
        return "STARTS_WITH";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("STARTS_WITH")
            .minArgs(2)
            .maxArgs(2)
            .argumentType(0, ArgumentType.STRING)
            .argumentType(1, ArgumentType.STRING)
            .returnType(ReturnType.BOOLEAN)
            .description("Returns true if the string starts with the prefix")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        validateArgCount(new AviatorObject[]{arg1, arg2}, 2);

        // Convert arguments to strings
        String str = toString(arg1, env);
        String prefix = toString(arg2, env);

        // Handle null inputs gracefully
        if (str == null || prefix == null) {
            return AviatorBoolean.FALSE;
        }

        // Check if string starts with prefix (case-sensitive)
        boolean result = str.startsWith(prefix);

        return result ? AviatorBoolean.TRUE : AviatorBoolean.FALSE;
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 2);
        return call(env, args[0], args[1]);
    }
}

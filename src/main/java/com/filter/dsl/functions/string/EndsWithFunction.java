package com.filter.dsl.functions.string;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorBoolean;

import java.util.Map;

/**
 * ENDS_WITH function - Returns true if the string ends with the suffix.
 *
 * Usage: ENDS_WITH(string, suffix)
 *
 * Examples:
 * - ENDS_WITH("hello world", "world") -> true
 * - ENDS_WITH("hello world", "hello") -> false
 * - ENDS_WITH("hello", "hello") -> true
 * - ENDS_WITH("hello", "Hello") -> false (case-sensitive)
 * - ENDS_WITH("", "test") -> false
 * - ENDS_WITH("test", "") -> true (any string ends with empty string)
 *
 * The function performs case-sensitive suffix matching.
 * Null inputs are handled gracefully - returns false if either argument is null.
 */
public class EndsWithFunction extends DSLFunction {

    @Override
    public String getName() {
        return "ENDS_WITH";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("ENDS_WITH")
            .minArgs(2)
            .maxArgs(2)
            .argumentType(0, ArgumentType.STRING)
            .argumentType(1, ArgumentType.STRING)
            .returnType(ReturnType.BOOLEAN)
            .description("Returns true if the string ends with the suffix")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        validateArgCount(new AviatorObject[]{arg1, arg2}, 2);

        // Convert arguments to strings
        String str = toString(arg1, env);
        String suffix = toString(arg2, env);

        // Handle null inputs gracefully
        if (str == null || suffix == null) {
            return AviatorBoolean.FALSE;
        }

        // Check if string ends with suffix (case-sensitive)
        boolean result = str.endsWith(suffix);

        return result ? AviatorBoolean.TRUE : AviatorBoolean.FALSE;
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 2);
        return call(env, args[0], args[1]);
    }
}

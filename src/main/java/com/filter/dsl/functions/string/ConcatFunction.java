package com.filter.dsl.functions.string;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;

import java.util.Map;

/**
 * CONCAT function - Concatenates multiple strings together.
 *
 * Usage: CONCAT(str1, str2, ..., strN)
 *
 * Examples:
 * - CONCAT("hello", " ", "world") -> "hello world"
 * - CONCAT("a", "b", "c") -> "abc"
 * - CONCAT("hello") -> "hello"
 * - CONCAT("", "world") -> "world"
 * - CONCAT("hello", null, "world") -> "hellonullworld"
 *
 * The function concatenates all arguments into a single string.
 * Null arguments are converted to the string "null".
 * At least one argument is required.
 */
public class ConcatFunction extends DSLFunction {

    @Override
    public String getName() {
        return "CONCAT";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("CONCAT")
            .minArgs(1)
            .maxArgs(Integer.MAX_VALUE)
            .argumentType(0, ArgumentType.STRING)
            .returnType(ReturnType.STRING)
            .description("Concatenates multiple strings together")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        return call(env, new AviatorObject[]{arg1});
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
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                              AviatorObject arg4) {
        return call(env, new AviatorObject[]{arg1, arg2, arg3, arg4});
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                              AviatorObject arg4, AviatorObject arg5) {
        return call(env, new AviatorObject[]{arg1, arg2, arg3, arg4, arg5});
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                              AviatorObject arg4, AviatorObject arg5, AviatorObject arg6) {
        return call(env, new AviatorObject[]{arg1, arg2, arg3, arg4, arg5, arg6});
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                              AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7) {
        return call(env, new AviatorObject[]{arg1, arg2, arg3, arg4, arg5, arg6, arg7});
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                              AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7,
                              AviatorObject arg8) {
        return call(env, new AviatorObject[]{arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8});
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                              AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7,
                              AviatorObject arg8, AviatorObject arg9) {
        return call(env, new AviatorObject[]{arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9});
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                              AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7,
                              AviatorObject arg8, AviatorObject arg9, AviatorObject arg10) {
        return call(env, new AviatorObject[]{arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10});
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                              AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7,
                              AviatorObject arg8, AviatorObject arg9, AviatorObject arg10, AviatorObject arg11) {
        return call(env, new AviatorObject[]{arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11});
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                              AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7,
                              AviatorObject arg8, AviatorObject arg9, AviatorObject arg10, AviatorObject arg11,
                              AviatorObject arg12) {
        return call(env, new AviatorObject[]{arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12});
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                              AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7,
                              AviatorObject arg8, AviatorObject arg9, AviatorObject arg10, AviatorObject arg11,
                              AviatorObject arg12, AviatorObject arg13) {
        return call(env, new AviatorObject[]{arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13});
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                              AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7,
                              AviatorObject arg8, AviatorObject arg9, AviatorObject arg10, AviatorObject arg11,
                              AviatorObject arg12, AviatorObject arg13, AviatorObject arg14) {
        return call(env, new AviatorObject[]{arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14});
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                              AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7,
                              AviatorObject arg8, AviatorObject arg9, AviatorObject arg10, AviatorObject arg11,
                              AviatorObject arg12, AviatorObject arg13, AviatorObject arg14, AviatorObject arg15) {
        return call(env, new AviatorObject[]{arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15});
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                              AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7,
                              AviatorObject arg8, AviatorObject arg9, AviatorObject arg10, AviatorObject arg11,
                              AviatorObject arg12, AviatorObject arg13, AviatorObject arg14, AviatorObject arg15,
                              AviatorObject arg16) {
        return call(env, new AviatorObject[]{arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16});
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                              AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7,
                              AviatorObject arg8, AviatorObject arg9, AviatorObject arg10, AviatorObject arg11,
                              AviatorObject arg12, AviatorObject arg13, AviatorObject arg14, AviatorObject arg15,
                              AviatorObject arg16, AviatorObject arg17) {
        return call(env, new AviatorObject[]{arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17});
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                              AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7,
                              AviatorObject arg8, AviatorObject arg9, AviatorObject arg10, AviatorObject arg11,
                              AviatorObject arg12, AviatorObject arg13, AviatorObject arg14, AviatorObject arg15,
                              AviatorObject arg16, AviatorObject arg17, AviatorObject arg18) {
        return call(env, new AviatorObject[]{arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18});
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                              AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7,
                              AviatorObject arg8, AviatorObject arg9, AviatorObject arg10, AviatorObject arg11,
                              AviatorObject arg12, AviatorObject arg13, AviatorObject arg14, AviatorObject arg15,
                              AviatorObject arg16, AviatorObject arg17, AviatorObject arg18, AviatorObject arg19) {
        return call(env, new AviatorObject[]{arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19});
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3,
                              AviatorObject arg4, AviatorObject arg5, AviatorObject arg6, AviatorObject arg7,
                              AviatorObject arg8, AviatorObject arg9, AviatorObject arg10, AviatorObject arg11,
                              AviatorObject arg12, AviatorObject arg13, AviatorObject arg14, AviatorObject arg15,
                              AviatorObject arg16, AviatorObject arg17, AviatorObject arg18, AviatorObject arg19,
                              AviatorObject arg20) {
        return call(env, new AviatorObject[]{arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19, arg20});
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateMinArgCount(args, 1);

        // Build concatenated string
        StringBuilder result = new StringBuilder();

        for (AviatorObject arg : args) {
            String str = toString(arg, env);
            if (str != null) {
                result.append(str);
            } else {
                result.append("null");
            }
        }

        return new AviatorString(result.toString());
    }
}

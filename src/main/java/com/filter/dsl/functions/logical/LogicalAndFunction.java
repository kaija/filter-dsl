package com.filter.dsl.functions.logical;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionArgumentException;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorBoolean;

import java.util.Map;

/**
 * AND function - Returns true if all operands are true.
 *
 * Usage: AND(expr1, expr2, ...)
 *
 * Examples:
 * - AND(true, true) -> true
 * - AND(true, false) -> false
 * - AND(true, true, true) -> true
 * - AND(true, true, false) -> false
 *
 * The function supports variable argument counts (minimum 2).
 * Short-circuit evaluation: stops evaluating as soon as a false value is found.
 */
public class LogicalAndFunction extends DSLFunction {

    @Override
    public String getName() {
        return "AND";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("AND")
            .minArgs(2)
            .maxArgs(Integer.MAX_VALUE)
            .argumentType(0, ArgumentType.BOOLEAN)
            .argumentType(1, ArgumentType.BOOLEAN)
            .returnType(ReturnType.BOOLEAN)
            .description("Returns true if all operands are true")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        throw new FunctionArgumentException(getName() + " expects at least 2 arguments, got 1");
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        Boolean value1 = toBoolean(arg1, env);
        if (!value1) {
            return AviatorBoolean.FALSE;
        }
        Boolean value2 = toBoolean(arg2, env);
        return value2 ? AviatorBoolean.TRUE : AviatorBoolean.FALSE;
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
        validateMinArgCount(args, 2);

        // Short-circuit evaluation: return false as soon as we find a false value
        for (AviatorObject arg : args) {
            Boolean value = toBoolean(arg, env);
            if (!value) {
                return AviatorBoolean.FALSE;
            }
        }

        return AviatorBoolean.TRUE;
    }
}

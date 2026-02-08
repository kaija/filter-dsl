package com.filter.dsl.functions.math;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorDouble;

import java.util.Map;

/**
 * POW function - Returns base raised to the power of exponent.
 *
 * Usage: POW(base, exponent)
 *
 * Examples:
 * - POW(2, 3) -> 8.0 (2^3)
 * - POW(5, 2) -> 25.0 (5^2)
 * - POW(10, 0) -> 1.0 (10^0)
 * - POW(2, -1) -> 0.5 (2^-1)
 * - POW(4, 0.5) -> 2.0 (square root of 4)
 * - POW(27, 1/3) -> 3.0 (cube root of 27)
 *
 * The function requires exactly 2 numeric arguments.
 * Returns a numeric value (double) representing base^exponent.
 */
public class PowFunction extends DSLFunction {

    @Override
    public String getName() {
        return "POW";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("POW")
            .minArgs(2)
            .maxArgs(2)
            .argumentType(0, ArgumentType.NUMBER)
            .argumentType(1, ArgumentType.NUMBER)
            .returnType(ReturnType.NUMBER)
            .description("Returns base raised to the power of exponent")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        validateArgCount(new AviatorObject[]{arg1, arg2}, 2);

        Number base = toNumber(arg1, env);
        Number exponent = toNumber(arg2, env);

        double result = Math.pow(base.doubleValue(), exponent.doubleValue());

        return AviatorDouble.valueOf(result);
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 2);
        return call(env, args[0], args[1]);
    }
}

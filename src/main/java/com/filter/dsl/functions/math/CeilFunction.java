package com.filter.dsl.functions.math;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorDouble;

import java.util.Map;

/**
 * CEIL function - Rounds a number up to the nearest integer.
 *
 * Usage: CEIL(n)
 *
 * Examples:
 * - CEIL(5.1) -> 6.0
 * - CEIL(5.9) -> 6.0
 * - CEIL(5.0) -> 5.0
 * - CEIL(-5.1) -> -5.0
 * - CEIL(-5.9) -> -5.0
 * - CEIL(0) -> 0.0
 *
 * The function requires exactly 1 numeric argument.
 * Returns a numeric value (double) rounded up to the nearest integer.
 */
public class CeilFunction extends DSLFunction {

    @Override
    public String getName() {
        return "CEIL";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("CEIL")
            .minArgs(1)
            .maxArgs(1)
            .argumentType(0, ArgumentType.NUMBER)
            .returnType(ReturnType.NUMBER)
            .description("Rounds a number up to the nearest integer")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        validateArgCount(new AviatorObject[]{arg1}, 1);

        Number num = toNumber(arg1, env);
        double result = Math.ceil(num.doubleValue());

        return AviatorDouble.valueOf(result);
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 1);
        return call(env, args[0]);
    }
}

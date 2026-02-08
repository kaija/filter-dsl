package com.filter.dsl.functions.math;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorDouble;

import java.util.Map;

/**
 * EXP function - Returns e (Euler's number) raised to the power of n.
 *
 * Usage: EXP(n)
 *
 * Examples:
 * - EXP(0) -> 1.0 (e^0)
 * - EXP(1) -> 2.718281828... (e^1 = e)
 * - EXP(2) -> 7.389... (e^2)
 * - EXP(-1) -> 0.367... (e^-1)
 * - EXP(10) -> 22026.465... (e^10)
 *
 * The function requires exactly 1 numeric argument.
 * Returns a numeric value (double) representing e^n.
 *
 * Note: This is the inverse of the natural logarithm (LOG).
 * For any positive x: EXP(LOG(x)) = x
 */
public class ExpFunction extends DSLFunction {

    @Override
    public String getName() {
        return "EXP";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("EXP")
            .minArgs(1)
            .maxArgs(1)
            .argumentType(0, ArgumentType.NUMBER)
            .returnType(ReturnType.NUMBER)
            .description("Returns e (Euler's number) raised to the power of n")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        validateArgCount(new AviatorObject[]{arg1}, 1);

        Number num = toNumber(arg1, env);
        double result = Math.exp(num.doubleValue());

        return AviatorDouble.valueOf(result);
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 1);
        return call(env, args[0]);
    }
}

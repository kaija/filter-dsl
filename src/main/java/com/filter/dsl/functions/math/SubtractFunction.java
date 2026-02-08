package com.filter.dsl.functions.math;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorDouble;

import java.util.Map;

/**
 * SUBTRACT function - Returns the difference of two numeric operands.
 *
 * Usage: SUBTRACT(a, b)
 *
 * Examples:
 * - SUBTRACT(10, 3) -> 7
 * - SUBTRACT(5.5, 2.3) -> 3.2
 * - SUBTRACT(5, 10) -> -5
 * - SUBTRACT(0, 5) -> -5
 *
 * The function requires exactly 2 numeric arguments.
 * Returns a numeric value (double).
 */
public class SubtractFunction extends DSLFunction {

    @Override
    public String getName() {
        return "SUBTRACT";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("SUBTRACT")
            .minArgs(2)
            .maxArgs(2)
            .argumentType(0, ArgumentType.NUMBER)
            .argumentType(1, ArgumentType.NUMBER)
            .returnType(ReturnType.NUMBER)
            .description("Returns the difference of two numeric operands")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        validateArgCount(new AviatorObject[]{arg1, arg2}, 2);

        Number num1 = toNumber(arg1, env);
        Number num2 = toNumber(arg2, env);

        double result = num1.doubleValue() - num2.doubleValue();
        return AviatorDouble.valueOf(result);
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 2);
        return call(env, args[0], args[1]);
    }
}

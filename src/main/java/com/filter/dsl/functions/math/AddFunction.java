package com.filter.dsl.functions.math;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorDouble;

import java.util.Map;

/**
 * ADD function - Returns the sum of two numeric operands.
 *
 * Usage: ADD(a, b)
 *
 * Examples:
 * - ADD(5, 3) -> 8
 * - ADD(10.5, 2.3) -> 12.8
 * - ADD(-5, 10) -> 5
 * - ADD(0, 0) -> 0
 *
 * The function requires exactly 2 numeric arguments.
 * Returns a numeric value (double).
 */
public class AddFunction extends DSLFunction {

    @Override
    public String getName() {
        return "ADD";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("ADD")
            .minArgs(2)
            .maxArgs(2)
            .argumentType(0, ArgumentType.NUMBER)
            .argumentType(1, ArgumentType.NUMBER)
            .returnType(ReturnType.NUMBER)
            .description("Returns the sum of two numeric operands")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        validateArgCount(new AviatorObject[]{arg1, arg2}, 2);

        Number num1 = toNumber(arg1, env);
        Number num2 = toNumber(arg2, env);

        double result = num1.doubleValue() + num2.doubleValue();
        return AviatorDouble.valueOf(result);
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 2);
        return call(env, args[0], args[1]);
    }
}

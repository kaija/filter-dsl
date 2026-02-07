package com.filter.dsl.functions.math;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorDouble;

import java.util.Map;

/**
 * ABS function - Returns the absolute value of a numeric operand.
 * 
 * Usage: ABS(n)
 * 
 * Examples:
 * - ABS(5) -> 5
 * - ABS(-5) -> 5
 * - ABS(0) -> 0
 * - ABS(-10.5) -> 10.5
 * 
 * The function requires exactly 1 numeric argument.
 * Returns a numeric value (double) representing the absolute value.
 */
public class AbsFunction extends DSLFunction {

    @Override
    public String getName() {
        return "ABS";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("ABS")
            .minArgs(1)
            .maxArgs(1)
            .argumentType(0, ArgumentType.NUMBER)
            .returnType(ReturnType.NUMBER)
            .description("Returns the absolute value of a numeric operand")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        validateArgCount(new AviatorObject[]{arg1}, 1);
        
        Number num = toNumber(arg1, env);
        double result = Math.abs(num.doubleValue());
        
        return AviatorDouble.valueOf(result);
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 1);
        return call(env, args[0]);
    }
}

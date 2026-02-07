package com.filter.dsl.functions.math;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorDouble;
import com.googlecode.aviator.runtime.type.AviatorNil;

import java.util.Map;

/**
 * MOD function - Returns the remainder of division of two numeric operands.
 * 
 * Usage: MOD(a, b)
 * 
 * Examples:
 * - MOD(10, 3) -> 1.0
 * - MOD(15, 4) -> 3.0
 * - MOD(20, 5) -> 0.0
 * - MOD(7, 2) -> 1.0
 * - MOD(10, 0) -> null (modulo by zero returns null)
 * 
 * The function requires exactly 2 numeric arguments.
 * Returns a numeric value (double) or null if divisor is zero.
 * 
 * Modulo by zero handling:
 * When the divisor is zero, the function returns null instead of throwing an error.
 * This allows expressions to continue evaluating gracefully.
 */
public class ModFunction extends DSLFunction {

    @Override
    public String getName() {
        return "MOD";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("MOD")
            .minArgs(2)
            .maxArgs(2)
            .argumentType(0, ArgumentType.NUMBER)
            .argumentType(1, ArgumentType.NUMBER)
            .returnType(ReturnType.NUMBER)
            .description("Returns the remainder of division (returns null for modulo by zero)")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        validateArgCount(new AviatorObject[]{arg1, arg2}, 2);
        
        Number num1 = toNumber(arg1, env);
        Number num2 = toNumber(arg2, env);
        
        // Handle modulo by zero - return null as per requirements
        if (num2.doubleValue() == 0.0) {
            return AviatorNil.NIL;
        }
        
        double result = num1.doubleValue() % num2.doubleValue();
        return AviatorDouble.valueOf(result);
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 2);
        return call(env, args[0], args[1]);
    }
}

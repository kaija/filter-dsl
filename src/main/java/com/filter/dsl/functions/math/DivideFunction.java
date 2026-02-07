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
 * DIVIDE function - Returns the quotient of two numeric operands.
 * 
 * Usage: DIVIDE(a, b)
 * 
 * Examples:
 * - DIVIDE(10, 2) -> 5.0
 * - DIVIDE(7, 2) -> 3.5
 * - DIVIDE(15, 3) -> 5.0
 * - DIVIDE(10, 0) -> null (division by zero returns null)
 * 
 * The function requires exactly 2 numeric arguments.
 * Returns a numeric value (double) or null if divisor is zero.
 * 
 * Division by zero handling:
 * When the divisor is zero, the function returns null instead of throwing an error.
 * This allows expressions to continue evaluating gracefully.
 */
public class DivideFunction extends DSLFunction {

    @Override
    public String getName() {
        return "DIVIDE";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("DIVIDE")
            .minArgs(2)
            .maxArgs(2)
            .argumentType(0, ArgumentType.NUMBER)
            .argumentType(1, ArgumentType.NUMBER)
            .returnType(ReturnType.NUMBER)
            .description("Returns the quotient of two numeric operands (returns null for division by zero)")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        validateArgCount(new AviatorObject[]{arg1, arg2}, 2);
        
        Number num1 = toNumber(arg1, env);
        Number num2 = toNumber(arg2, env);
        
        // Handle division by zero - return null as per requirements
        if (num2.doubleValue() == 0.0) {
            return AviatorNil.NIL;
        }
        
        double result = num1.doubleValue() / num2.doubleValue();
        return AviatorDouble.valueOf(result);
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 2);
        return call(env, args[0], args[1]);
    }
}

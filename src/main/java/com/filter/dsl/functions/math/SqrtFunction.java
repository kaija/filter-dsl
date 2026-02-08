package com.filter.dsl.functions.math;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.filter.dsl.functions.FunctionArgumentException;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorDouble;

import java.util.Map;

/**
 * SQRT function - Returns the square root of a number.
 *
 * Usage: SQRT(n)
 *
 * Examples:
 * - SQRT(4) -> 2.0
 * - SQRT(9) -> 3.0
 * - SQRT(16) -> 4.0
 * - SQRT(2) -> 1.414...
 * - SQRT(0) -> 0.0
 * - SQRT(-1) -> Error (negative numbers not allowed)
 *
 * The function requires exactly 1 numeric argument.
 * Returns a numeric value (double) representing the square root.
 *
 * Edge case handling:
 * - Negative numbers throw an error as square root of negative numbers
 *   is not defined in real numbers (would require complex numbers).
 */
public class SqrtFunction extends DSLFunction {

    @Override
    public String getName() {
        return "SQRT";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("SQRT")
            .minArgs(1)
            .maxArgs(1)
            .argumentType(0, ArgumentType.NUMBER)
            .returnType(ReturnType.NUMBER)
            .description("Returns the square root of a number (error for negative numbers)")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        validateArgCount(new AviatorObject[]{arg1}, 1);

        Number num = toNumber(arg1, env);
        double value = num.doubleValue();

        // Handle edge case: negative numbers
        if (value < 0) {
            throw new FunctionArgumentException(
                "SQRT cannot compute square root of negative number: " + value
            );
        }

        double result = Math.sqrt(value);

        return AviatorDouble.valueOf(result);
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 1);
        return call(env, args[0]);
    }
}

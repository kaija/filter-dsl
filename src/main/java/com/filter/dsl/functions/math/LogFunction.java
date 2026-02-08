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
 * LOG function - Returns the logarithm of a number with specified or default base.
 *
 * Usage:
 * - LOG(n) - Natural logarithm (base e)
 * - LOG(n, base) - Logarithm with specified base
 *
 * Examples:
 * - LOG(2.718281828) -> 1.0 (natural log of e)
 * - LOG(10, 10) -> 1.0 (log base 10 of 10)
 * - LOG(8, 2) -> 3.0 (log base 2 of 8)
 * - LOG(100, 10) -> 2.0 (log base 10 of 100)
 * - LOG(1) -> 0.0 (log of 1 is always 0)
 * - LOG(0) -> Error (log of zero is undefined)
 * - LOG(-5) -> Error (log of negative numbers is undefined)
 *
 * The function requires 1 or 2 numeric arguments.
 * Returns a numeric value (double) representing the logarithm.
 *
 * Edge case handling:
 * - Zero or negative numbers throw an error as logarithm is only defined
 *   for positive real numbers.
 * - Base must be positive and not equal to 1.
 */
public class LogFunction extends DSLFunction {

    @Override
    public String getName() {
        return "LOG";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("LOG")
            .minArgs(1)
            .maxArgs(2)
            .argumentType(0, ArgumentType.NUMBER)
            .argumentType(1, ArgumentType.NUMBER)
            .returnType(ReturnType.NUMBER)
            .description("Returns the logarithm of a number with specified or default base (natural log)")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        Number num = toNumber(arg1, env);
        double value = num.doubleValue();

        // Handle edge case: zero or negative numbers
        if (value <= 0) {
            throw new FunctionArgumentException(
                "LOG cannot compute logarithm of non-positive number: " + value
            );
        }

        // Natural logarithm (base e)
        double result = Math.log(value);
        return AviatorDouble.valueOf(result);
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        Number num = toNumber(arg1, env);
        double value = num.doubleValue();

        // Handle edge case: zero or negative numbers
        if (value <= 0) {
            throw new FunctionArgumentException(
                "LOG cannot compute logarithm of non-positive number: " + value
            );
        }

        // Logarithm with specified base
        Number baseNum = toNumber(arg2, env);
        double base = baseNum.doubleValue();

        // Validate base
        if (base <= 0 || base == 1) {
            throw new FunctionArgumentException(
                "LOG base must be positive and not equal to 1: " + base
            );
        }

        // Change of base formula: log_b(x) = ln(x) / ln(b)
        double result = Math.log(value) / Math.log(base);
        return AviatorDouble.valueOf(result);
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCountRange(args, 1, 2);

        if (args.length == 1) {
            return call(env, args[0]);
        } else {
            return call(env, args[0], args[1]);
        }
    }
}

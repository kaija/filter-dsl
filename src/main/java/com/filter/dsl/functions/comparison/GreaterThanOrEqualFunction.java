package com.filter.dsl.functions.comparison;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorBoolean;

import java.util.Map;

/**
 * GTE (Greater Than or Equal) function - Returns true if the first operand is greater than or equal to the second.
 *
 * Usage: GTE(a, b)
 *
 * Examples:
 * - GTE(5, 3) -> true
 * - GTE(3, 5) -> false
 * - GTE(5, 5) -> true
 * - GTE(10.5, 10.5) -> true
 *
 * The function performs numeric comparison with proper type coercion.
 * Both arguments must be numeric values.
 */
public class GreaterThanOrEqualFunction extends DSLFunction {

    @Override
    public String getName() {
        return "GTE";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("GTE")
            .minArgs(2)
            .maxArgs(2)
            .argumentType(0, ArgumentType.NUMBER)
            .argumentType(1, ArgumentType.NUMBER)
            .returnType(ReturnType.BOOLEAN)
            .description("Returns true if the first operand is greater than or equal to the second")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        validateArgCount(new AviatorObject[]{arg1, arg2}, 2);

        Number value1 = toNumber(arg1, env);
        Number value2 = toNumber(arg2, env);

        // Convert to double for comparison to handle mixed integer/decimal types
        double d1 = value1.doubleValue();
        double d2 = value2.doubleValue();

        return d1 >= d2 ? AviatorBoolean.TRUE : AviatorBoolean.FALSE;
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 2);
        return call(env, args[0], args[1]);
    }
}

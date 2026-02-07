package com.filter.dsl.functions.math;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorDouble;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * ROUND function - Rounds a number to the nearest integer or specified decimal places.
 * 
 * Usage: 
 * - ROUND(n) - Rounds to nearest integer
 * - ROUND(n, decimals) - Rounds to specified decimal places
 * 
 * Examples:
 * - ROUND(5.4) -> 5.0
 * - ROUND(5.5) -> 6.0
 * - ROUND(5.6) -> 6.0
 * - ROUND(3.14159, 2) -> 3.14
 * - ROUND(3.14159, 0) -> 3.0
 * - ROUND(123.456, 1) -> 123.5
 * - ROUND(-5.5) -> -6.0
 * 
 * The function requires 1 or 2 numeric arguments.
 * Returns a numeric value (double) rounded to the specified precision.
 */
public class RoundFunction extends DSLFunction {

    @Override
    public String getName() {
        return "ROUND";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("ROUND")
            .minArgs(1)
            .maxArgs(2)
            .argumentType(0, ArgumentType.NUMBER)
            .argumentType(1, ArgumentType.NUMBER)
            .returnType(ReturnType.NUMBER)
            .description("Rounds a number to the nearest integer or specified decimal places")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        Number num = toNumber(arg1, env);
        
        // Use BigDecimal for precise rounding
        BigDecimal bd = BigDecimal.valueOf(num.doubleValue());
        bd = bd.setScale(0, RoundingMode.HALF_UP);
        
        return AviatorDouble.valueOf(bd.doubleValue());
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        Number num = toNumber(arg1, env);
        Number decimalArg = toNumber(arg2, env);
        int decimals = decimalArg.intValue();
        
        // Use BigDecimal for precise rounding
        BigDecimal bd = BigDecimal.valueOf(num.doubleValue());
        bd = bd.setScale(decimals, RoundingMode.HALF_UP);
        
        return AviatorDouble.valueOf(bd.doubleValue());
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

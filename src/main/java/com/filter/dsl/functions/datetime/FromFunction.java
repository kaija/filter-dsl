package com.filter.dsl.functions.datetime;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.filter.dsl.functions.TypeMismatchException;
import com.filter.dsl.models.TimeRange;
import com.filter.dsl.models.TimeUnit;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorJavaType;

import java.time.Instant;
import java.util.Map;

/**
 * FROM function - Defines the start of a relative time range.
 * 
 * Usage: FROM(n, unit)
 * 
 * Examples:
 * - FROM(30, "D") -> 30 days ago from now
 * - FROM(7, "W") -> 7 weeks ago from now
 * - FROM(12, "MO") -> 12 months ago from now
 * 
 * Supported units:
 * - D: Days
 * - H: Hours
 * - M: Minutes
 * - W: Weeks
 * - MO: Months
 * - Y: Years
 * 
 * This function is typically used with TO() to define a time range for filtering events.
 * The FROM value represents how far back in time to start the range.
 * 
 * Note: This function updates the time range in the evaluation context. It should be
 * used in conjunction with filtering functions like WHERE() or IF().
 */
public class FromFunction extends DSLFunction {

    @Override
    public String getName() {
        return "FROM";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("FROM")
            .minArgs(2)
            .maxArgs(2)
            .argumentType(0, ArgumentType.NUMBER)
            .argumentType(1, ArgumentType.STRING)
            .returnType(ReturnType.ANY)
            .description("Defines the start of a relative time range")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 2);
        
        Number value = toNumber(args[0], env);
        String unitStr = toString(args[1], env);
        
        if (value == null || unitStr == null) {
            throw new TypeMismatchException("FROM requires non-null arguments");
        }
        
        try {
            // Parse the time unit
            TimeUnit unit = TimeUnit.parse(unitStr);
            
            // Get the current time range from context, or create a new one
            TimeRange currentRange = getTimeRange(env);
            Instant now = getNow(env);
            
            // Create or update the time range with the FROM value
            TimeRange newRange;
            if (currentRange != null) {
                // Update existing range with new FROM value
                newRange = new TimeRange(
                    value.intValue(),
                    unit,
                    currentRange.getToValue(),
                    currentRange.getToUnit(),
                    now
                );
            } else {
                // Create new range with only FROM value (TO will be set later or default to now)
                newRange = new TimeRange(
                    value.intValue(),
                    unit,
                    null,
                    null,
                    now
                );
            }
            
            // Update the context with the new time range
            env.put("timeRange", newRange);
            
            // Return the time range as an AviatorObject
            return new AviatorJavaType("timeRange") {
                @Override
                public Object getValue(Map<String, Object> environment) {
                    return newRange;
                }
            };
            
        } catch (IllegalArgumentException e) {
            throw new TypeMismatchException(e.getMessage());
        }
    }
    
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        return call(env, new AviatorObject[]{arg1, arg2});
    }
}

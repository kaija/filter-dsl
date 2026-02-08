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
 * TO function - Defines the end of a relative time range.
 *
 * Usage: TO(n, unit)
 *
 * Examples:
 * - TO(0, "D") -> now (0 days ago)
 * - TO(7, "D") -> 7 days ago from now
 * - TO(1, "H") -> 1 hour ago from now
 *
 * Supported units:
 * - D: Days
 * - H: Hours
 * - M: Minutes
 * - W: Weeks
 * - MO: Months
 * - Y: Years
 *
 * This function is typically used with FROM() to define a time range for filtering events.
 * The TO value represents how far back in time to end the range.
 *
 * Common usage: FROM(30, "D"), TO(0, "D") means "from 30 days ago to now"
 *
 * Note: This function updates the time range in the evaluation context. It should be
 * used in conjunction with filtering functions like WHERE() or IF().
 */
public class ToFunction extends DSLFunction {

    @Override
    public String getName() {
        return "TO";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("TO")
            .minArgs(2)
            .maxArgs(2)
            .argumentType(0, ArgumentType.NUMBER)
            .argumentType(1, ArgumentType.STRING)
            .returnType(ReturnType.ANY)
            .description("Defines the end of a relative time range")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 2);

        Number value = toNumber(args[0], env);
        String unitStr = toString(args[1], env);

        if (value == null || unitStr == null) {
            throw new TypeMismatchException("TO requires non-null arguments");
        }

        try {
            // Parse the time unit
            TimeUnit unit = TimeUnit.parse(unitStr);

            // Get the current time range from context, or create a new one
            TimeRange currentRange = getTimeRange(env);
            Instant now = getNow(env);

            // Create or update the time range with the TO value
            TimeRange newRange;
            if (currentRange != null) {
                // Update existing range with new TO value
                newRange = new TimeRange(
                    currentRange.getFromValue(),
                    currentRange.getFromUnit(),
                    value.intValue(),
                    unit,
                    now
                );
            } else {
                // Create new range with only TO value (FROM will be set later or default to far past)
                newRange = new TimeRange(
                    null,
                    null,
                    value.intValue(),
                    unit,
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

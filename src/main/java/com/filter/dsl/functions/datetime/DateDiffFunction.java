package com.filter.dsl.functions.datetime;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.filter.dsl.functions.TypeMismatchException;
import com.filter.dsl.models.TimeUnit;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorLong;
import com.googlecode.aviator.runtime.type.AviatorDouble;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Map;

/**
 * DATE_DIFF function - Calculates the difference between two dates in the specified unit.
 * 
 * Usage: DATE_DIFF(date1, date2, unit)
 * 
 * Examples:
 * - DATE_DIFF("2023-01-20T00:00:00Z", "2023-01-15T00:00:00Z", "D") -> 5
 * - DATE_DIFF("2023-01-15T12:00:00Z", "2023-01-15T10:00:00Z", "H") -> 2
 * - DATE_DIFF("2023-01-15T10:30:00Z", "2023-01-15T10:00:00Z", "M") -> 30
 * 
 * Supported units:
 * - D: Days
 * - H: Hours
 * - M: Minutes
 * - W: Weeks
 * - MO: Months (approximate, 30 days)
 * - Y: Years (approximate, 365 days)
 * 
 * The result is date1 - date2, so a positive result means date1 is after date2.
 */
public class DateDiffFunction extends DSLFunction {

    @Override
    public String getName() {
        return "DATE_DIFF";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("DATE_DIFF")
            .minArgs(3)
            .maxArgs(3)
            .argumentType(0, ArgumentType.STRING)
            .argumentType(1, ArgumentType.STRING)
            .argumentType(2, ArgumentType.STRING)
            .returnType(ReturnType.NUMBER)
            .description("Calculates the difference between two dates in the specified unit")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 3);
        
        String date1Str = toString(args[0], env);
        String date2Str = toString(args[1], env);
        String unitStr = toString(args[2], env);
        
        if (date1Str == null || date2Str == null || unitStr == null) {
            throw new TypeMismatchException("DATE_DIFF requires non-null arguments");
        }
        
        try {
            // Parse the timestamps
            Instant instant1 = Instant.parse(date1Str);
            Instant instant2 = Instant.parse(date2Str);
            
            // Parse the time unit
            TimeUnit unit = TimeUnit.parse(unitStr);
            
            // Calculate the difference
            long diff = calculateDifference(instant1, instant2, unit);
            
            return AviatorLong.valueOf(diff);
            
        } catch (DateTimeParseException e) {
            throw new TypeMismatchException(
                "Invalid timestamp format. Expected ISO-8601 format (e.g., 2023-01-15T10:30:00Z)"
            );
        } catch (IllegalArgumentException e) {
            throw new TypeMismatchException(e.getMessage());
        }
    }
    
    /**
     * Calculate the difference between two instants in the specified unit.
     * 
     * @param instant1 The first instant
     * @param instant2 The second instant
     * @param unit The time unit for the result
     * @return The difference (instant1 - instant2) in the specified unit
     */
    private long calculateDifference(Instant instant1, Instant instant2, TimeUnit unit) {
        Duration duration = Duration.between(instant2, instant1);
        
        switch (unit) {
            case M:
                return duration.toMinutes();
            case H:
                return duration.toHours();
            case D:
                return duration.toDays();
            case W:
                return duration.toDays() / 7;
            case MO:
                // Approximate: 30 days per month
                return duration.toDays() / 30;
            case Y:
                // Approximate: 365 days per year
                return duration.toDays() / 365;
            default:
                throw new IllegalArgumentException("Unsupported time unit: " + unit);
        }
    }
    
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3) {
        return call(env, new AviatorObject[]{arg1, arg2, arg3});
    }
}

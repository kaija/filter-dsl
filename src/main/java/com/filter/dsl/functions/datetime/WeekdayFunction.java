package com.filter.dsl.functions.datetime;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.filter.dsl.functions.TypeMismatchException;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorLong;
import com.googlecode.aviator.runtime.type.AviatorNil;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Map;

/**
 * WEEKDAY function - Returns the day of week as a number (1=Monday, 7=Sunday).
 * 
 * Usage: WEEKDAY(timestamp)
 * 
 * Examples:
 * - WEEKDAY("2023-01-15T10:30:00Z") -> 7 (Sunday)
 * - WEEKDAY("2023-01-16T10:30:00Z") -> 1 (Monday)
 * - WEEKDAY("2023-01-20T10:30:00Z") -> 5 (Friday)
 * 
 * The function follows ISO-8601 standard where Monday=1 and Sunday=7.
 */
public class WeekdayFunction extends DSLFunction {

    @Override
    public String getName() {
        return "WEEKDAY";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("WEEKDAY")
            .minArgs(1)
            .maxArgs(1)
            .argumentType(0, ArgumentType.STRING)
            .returnType(ReturnType.NUMBER)
            .description("Returns the day of week (1=Monday, 7=Sunday)")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 1);
        
        String timestampStr = toString(args[0], env);
        
        if (timestampStr == null) {
            return AviatorNil.NIL;
        }
        
        try {
            // Parse the timestamp string to Instant
            Instant instant = Instant.parse(timestampStr);
            
            // Convert to ZonedDateTime in UTC to get day of week
            DayOfWeek dayOfWeek = instant.atZone(ZoneId.of("UTC")).getDayOfWeek();
            
            // Return ISO-8601 day number (Monday=1, Sunday=7)
            return AviatorLong.valueOf(dayOfWeek.getValue());
            
        } catch (DateTimeParseException e) {
            throw new TypeMismatchException(
                "Invalid timestamp format: " + timestampStr + ". Expected ISO-8601 format (e.g., 2023-01-15T10:30:00Z)"
            );
        }
    }
    
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        return call(env, new AviatorObject[]{arg1});
    }
}

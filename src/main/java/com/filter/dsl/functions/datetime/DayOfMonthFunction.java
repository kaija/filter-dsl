package com.filter.dsl.functions.datetime;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.filter.dsl.functions.TypeMismatchException;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorLong;
import com.googlecode.aviator.runtime.type.AviatorNil;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Map;

/**
 * DAY_OF_MONTH function - Returns the day of month (1-31).
 * 
 * Usage: DAY_OF_MONTH(timestamp)
 * 
 * Examples:
 * - DAY_OF_MONTH("2023-01-15T10:30:00Z") -> 15
 * - DAY_OF_MONTH("2023-01-01T10:30:00Z") -> 1
 * - DAY_OF_MONTH("2023-01-31T10:30:00Z") -> 31
 * 
 * The function extracts the day component from the timestamp.
 */
public class DayOfMonthFunction extends DSLFunction {

    @Override
    public String getName() {
        return "DAY_OF_MONTH";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("DAY_OF_MONTH")
            .minArgs(1)
            .maxArgs(1)
            .argumentType(0, ArgumentType.STRING)
            .returnType(ReturnType.NUMBER)
            .description("Returns the day of month (1-31)")
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
            
            // Convert to ZonedDateTime in UTC to get day of month
            int dayOfMonth = instant.atZone(ZoneId.of("UTC")).getDayOfMonth();
            
            return AviatorLong.valueOf(dayOfMonth);
            
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

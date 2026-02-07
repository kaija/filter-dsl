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
 * MONTH function - Returns the month (1-12).
 * 
 * Usage: MONTH(timestamp)
 * 
 * Examples:
 * - MONTH("2023-01-15T10:30:00Z") -> 1
 * - MONTH("2023-06-15T10:30:00Z") -> 6
 * - MONTH("2023-12-31T10:30:00Z") -> 12
 * 
 * The function extracts the month component from the timestamp.
 */
public class MonthFunction extends DSLFunction {

    @Override
    public String getName() {
        return "MONTH";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("MONTH")
            .minArgs(1)
            .maxArgs(1)
            .argumentType(0, ArgumentType.STRING)
            .returnType(ReturnType.NUMBER)
            .description("Returns the month (1-12)")
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
            
            // Convert to ZonedDateTime in UTC to get month
            int month = instant.atZone(ZoneId.of("UTC")).getMonthValue();
            
            return AviatorLong.valueOf(month);
            
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

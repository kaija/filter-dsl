package com.filter.dsl.functions.datetime;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.filter.dsl.functions.TypeMismatchException;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import com.googlecode.aviator.runtime.type.AviatorNil;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

/**
 * DATE_FORMAT function - Formats a timestamp according to a specified format pattern.
 * 
 * Usage: DATE_FORMAT(timestamp, format)
 * 
 * Examples:
 * - DATE_FORMAT("2023-01-15T10:30:00Z", "yyyy-MM-dd") -> "2023-01-15"
 * - DATE_FORMAT("2023-01-15T10:30:00Z", "HH:mm:ss") -> "10:30:00"
 * - DATE_FORMAT("2023-01-15T10:30:00Z", "EEEE, MMMM d, yyyy") -> "Sunday, January 15, 2023"
 * 
 * The format parameter uses Java DateTimeFormatter patterns.
 * Common patterns:
 * - yyyy: 4-digit year
 * - MM: 2-digit month
 * - dd: 2-digit day
 * - HH: 2-digit hour (24-hour)
 * - mm: 2-digit minute
 * - ss: 2-digit second
 * - EEEE: full day name
 * - MMMM: full month name
 */
public class DateFormatFunction extends DSLFunction {

    @Override
    public String getName() {
        return "DATE_FORMAT";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("DATE_FORMAT")
            .minArgs(2)
            .maxArgs(2)
            .argumentType(0, ArgumentType.STRING)
            .argumentType(1, ArgumentType.STRING)
            .returnType(ReturnType.STRING)
            .description("Formats a timestamp according to a specified format pattern")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 2);
        
        String timestampStr = toString(args[0], env);
        String formatPattern = toString(args[1], env);
        
        if (timestampStr == null || formatPattern == null) {
            return AviatorNil.NIL;
        }
        
        try {
            // Parse the timestamp string to Instant
            Instant instant = Instant.parse(timestampStr);
            
            // Create formatter with the specified pattern
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern)
                .withZone(ZoneId.of("UTC"));
            
            // Format the instant
            String formatted = formatter.format(instant);
            
            return new AviatorString(formatted);
            
        } catch (DateTimeParseException e) {
            throw new TypeMismatchException(
                "Invalid timestamp format: " + timestampStr + ". Expected ISO-8601 format (e.g., 2023-01-15T10:30:00Z)"
            );
        } catch (IllegalArgumentException e) {
            throw new TypeMismatchException(
                "Invalid date format pattern: " + formatPattern + ". " + e.getMessage()
            );
        }
    }
    
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        return call(env, new AviatorObject[]{arg1, arg2});
    }
}

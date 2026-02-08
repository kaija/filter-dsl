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
 * YEAR function - Returns the year.
 *
 * Usage: YEAR(timestamp)
 *
 * Examples:
 * - YEAR("2023-01-15T10:30:00Z") -> 2023
 * - YEAR("2024-06-15T10:30:00Z") -> 2024
 * - YEAR("2022-12-31T10:30:00Z") -> 2022
 *
 * The function extracts the year component from the timestamp.
 */
public class YearFunction extends DSLFunction {

    @Override
    public String getName() {
        return "YEAR";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("YEAR")
            .minArgs(1)
            .maxArgs(1)
            .argumentType(0, ArgumentType.STRING)
            .returnType(ReturnType.NUMBER)
            .description("Returns the year")
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

            // Convert to ZonedDateTime in UTC to get year
            int year = instant.atZone(ZoneId.of("UTC")).getYear();

            return AviatorLong.valueOf(year);

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

package com.filter.dsl.functions.datetime;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;

import java.time.Instant;
import java.util.Map;

/**
 * NOW function - Returns the current timestamp.
 *
 * Usage: NOW()
 *
 * Examples:
 * - NOW() -> "2023-01-15T10:30:00Z"
 *
 * This function returns the current evaluation time, which is typically set
 * at the start of expression evaluation. This ensures consistent "now" values
 * throughout a single evaluation.
 */
public class NowFunction extends DSLFunction {

    @Override
    public String getName() {
        return "NOW";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("NOW")
            .minArgs(0)
            .maxArgs(0)
            .returnType(ReturnType.STRING)
            .description("Returns the current timestamp")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 0);

        // Get the current timestamp from context (or use actual current time)
        Instant now = getNow(env);

        // Return as ISO-8601 formatted string
        return new AviatorString(now.toString());
    }

    // Override the no-argument call method for AviatorScript compatibility
    @Override
    public AviatorObject call(Map<String, Object> env) {
        return call(env, new AviatorObject[0]);
    }
}

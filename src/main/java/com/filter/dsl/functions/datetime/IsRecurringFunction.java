package com.filter.dsl.functions.datetime;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.filter.dsl.functions.TypeMismatchException;
import com.filter.dsl.models.Event;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorBoolean;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Map;

/**
 * IS_RECURRING function - Checks if an event occurs at least min_count times within a time window.
 *
 * Usage: IS_RECURRING(event_name, min_count, time_window_days)
 *
 * Examples:
 * - IS_RECURRING("purchase", 3, 90) -> true if user made 3+ purchases in past 90 days
 * - IS_RECURRING("login", 5, 30) -> true if user logged in 5+ times in past 30 days
 * - IS_RECURRING("view_product", 10, 7) -> true if user viewed products 10+ times in past week
 *
 * This function is useful for identifying recurring behaviors and engaged users.
 * The time window is measured in days from the current evaluation time.
 */
public class IsRecurringFunction extends DSLFunction {

    @Override
    public String getName() {
        return "IS_RECURRING";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("IS_RECURRING")
            .minArgs(3)
            .maxArgs(3)
            .argumentType(0, ArgumentType.STRING)
            .argumentType(1, ArgumentType.NUMBER)
            .argumentType(2, ArgumentType.NUMBER)
            .returnType(ReturnType.BOOLEAN)
            .description("Checks if an event occurs at least min_count times within time_window days")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 3);

        String eventName = toString(args[0], env);
        Number minCountNumber = toNumber(args[1], env);
        Number timeWindowNumber = toNumber(args[2], env);

        int minCount = minCountNumber.intValue();
        int timeWindowDays = timeWindowNumber.intValue();

        if (minCount < 0) {
            throw new TypeMismatchException("IS_RECURRING expects a non-negative min_count, got: " + minCount);
        }

        if (timeWindowDays < 0) {
            throw new TypeMismatchException("IS_RECURRING expects a non-negative time_window, got: " + timeWindowDays);
        }

        if (eventName == null) {
            return AviatorBoolean.FALSE;
        }

        // Get the current timestamp from context
        Instant now = getNow(env);

        // Calculate the cutoff time (time_window days ago)
        Instant cutoff = now.minus(timeWindowDays, ChronoUnit.DAYS);

        // Get the events collection from user data
        Object userData = getUserData(env);
        if (userData == null) {
            return AviatorBoolean.FALSE;
        }

        // Try to get events from userData
        Collection<?> events = null;
        try {
            if (userData instanceof Map) {
                Object eventsObj = ((Map<?, ?>) userData).get("events");
                if (eventsObj instanceof Collection) {
                    events = (Collection<?>) eventsObj;
                }
            } else {
                // Try reflection to get events field
                java.lang.reflect.Method getEventsMethod = userData.getClass().getMethod("getEvents");
                Object eventsObj = getEventsMethod.invoke(userData);
                if (eventsObj instanceof Collection) {
                    events = (Collection<?>) eventsObj;
                }
            }
        } catch (Exception e) {
            // If we can't get events, return false
            return AviatorBoolean.FALSE;
        }

        if (events == null) {
            return AviatorBoolean.FALSE;
        }

        // Count occurrences of the specified event within the time window
        int count = 0;
        for (Object eventObj : events) {
            if (eventObj instanceof Event) {
                Event event = (Event) eventObj;

                // Check if event name matches
                if (eventName.equals(event.getEventName())) {
                    String timestampStr = event.getTimestamp();

                    if (timestampStr != null) {
                        try {
                            Instant eventTime = Instant.parse(timestampStr);

                            // Include event if it's within the time window
                            if (!eventTime.isBefore(cutoff) && !eventTime.isAfter(now)) {
                                count++;

                                // Early exit if we've already met the threshold
                                if (count >= minCount) {
                                    return AviatorBoolean.TRUE;
                                }
                            }
                        } catch (DateTimeParseException e) {
                            // Skip events with invalid timestamps
                        }
                    }
                }
            }
        }

        // Return true if count meets or exceeds minimum
        return count >= minCount ? AviatorBoolean.TRUE : AviatorBoolean.FALSE;
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3) {
        return call(env, new AviatorObject[]{arg1, arg2, arg3});
    }
}

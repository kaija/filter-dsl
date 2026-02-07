package com.filter.dsl.functions.datetime;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.filter.dsl.functions.TypeMismatchException;
import com.filter.dsl.models.Event;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorJavaType;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * IN_RECENT_DAYS function - Filters events that occurred in the past N days.
 * 
 * Usage: IN_RECENT_DAYS(n)
 * 
 * Examples:
 * - IN_RECENT_DAYS(7) -> filters events from the past 7 days
 * - IN_RECENT_DAYS(30) -> filters events from the past 30 days
 * - IN_RECENT_DAYS(1) -> filters events from the past 24 hours
 * 
 * This function is typically used in conjunction with other filtering operations
 * to limit analysis to recent events. It uses the "now" timestamp from the
 * evaluation context to determine what "recent" means.
 */
public class InRecentDaysFunction extends DSLFunction {

    @Override
    public String getName() {
        return "IN_RECENT_DAYS";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("IN_RECENT_DAYS")
            .minArgs(1)
            .maxArgs(1)
            .argumentType(0, ArgumentType.NUMBER)
            .returnType(ReturnType.COLLECTION)
            .description("Filters events from the past N days")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 1);
        
        Number daysNumber = toNumber(args[0], env);
        int days = daysNumber.intValue();
        
        if (days < 0) {
            throw new TypeMismatchException("IN_RECENT_DAYS expects a non-negative number of days, got: " + days);
        }
        
        // Get the current timestamp from context
        Instant now = getNow(env);
        
        // Calculate the cutoff time (N days ago)
        Instant cutoff = now.minus(days, ChronoUnit.DAYS);
        
        // Get the events collection from user data
        Object userData = getUserData(env);
        if (userData == null) {
            List<Event> emptyList = new ArrayList<>();
            return new AviatorJavaType("recentEvents") {
                @Override
                public Object getValue(Map<String, Object> environment) {
                    return emptyList;
                }
            };
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
            // If we can't get events, return empty list
            List<Event> emptyList = new ArrayList<>();
            return new AviatorJavaType("recentEvents") {
                @Override
                public Object getValue(Map<String, Object> environment) {
                    return emptyList;
                }
            };
        }
        
        if (events == null) {
            List<Event> emptyList = new ArrayList<>();
            return new AviatorJavaType("recentEvents") {
                @Override
                public Object getValue(Map<String, Object> environment) {
                    return emptyList;
                }
            };
        }
        
        // Filter events that occurred after the cutoff time
        List<Event> recentEvents = new ArrayList<>();
        for (Object eventObj : events) {
            if (eventObj instanceof Event) {
                Event event = (Event) eventObj;
                String timestampStr = event.getTimestamp();
                
                if (timestampStr != null) {
                    try {
                        Instant eventTime = Instant.parse(timestampStr);
                        
                        // Include event if it's after the cutoff (within the past N days)
                        if (!eventTime.isBefore(cutoff) && !eventTime.isAfter(now)) {
                            recentEvents.add(event);
                        }
                    } catch (DateTimeParseException e) {
                        // Skip events with invalid timestamps
                    }
                }
            }
        }
        
        return new AviatorJavaType("recentEvents") {
            @Override
            public Object getValue(Map<String, Object> environment) {
                return recentEvents;
            }
        };
    }
    
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        return call(env, new AviatorObject[]{arg1});
    }
}

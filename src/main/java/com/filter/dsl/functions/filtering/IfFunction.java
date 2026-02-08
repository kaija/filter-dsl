package com.filter.dsl.functions.filtering;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.filter.dsl.models.Event;
import com.filter.dsl.models.TimeRange;
import com.filter.dsl.models.UserData;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorRuntimeJavaType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * IF function - Filter events based on a boolean condition.
 *
 * Usage: IF("condition_expression")
 *
 * Filters the events collection from userData to only include events where the condition
 * evaluates to true. The condition is evaluated for each event with that event set as
 * the current event in the context.
 *
 * NOTE: Due to AviatorScript's eager evaluation, the condition must be passed as a STRING
 * expression that will be compiled and evaluated lazily for each event.
 *
 * Integrates with time range context (FROM/TO) to apply time-based filtering in addition
 * to the condition.
 *
 * Examples:
 * - IF("EQ(EVENT(\"event_name\"), \"purchase\")") -> events where event_name is "purchase"
 * - IF("GT(EVENT(\"duration\"), 100)") -> events with duration > 100
 * - IF("EQ(EVENT(\"event_type\"), \"action\")") -> action events
 *
 * Returns: A filtered collection of events
 */
public class IfFunction extends DSLFunction {

    @Override
    public String getName() {
        return "IF";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("IF")
            .minArgs(1)
            .maxArgs(1)
            .argumentType(0, ArgumentType.STRING)
            .returnType(ReturnType.COLLECTION)
            .description("Filters events based on a boolean condition expression (passed as string)")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 1);

        // Get user data from context
        Object userDataObj = getUserData(env);
        if (userDataObj == null || !(userDataObj instanceof UserData)) {
            // No user data, return empty list
            return AviatorRuntimeJavaType.valueOf(new ArrayList<Event>());
        }

        UserData userData = (UserData) userDataObj;
        List<Event> allEvents = userData.getEvents();

        if (allEvents == null || allEvents.isEmpty()) {
            return AviatorRuntimeJavaType.valueOf(new ArrayList<Event>());
        }

        // Get the condition expression as a string
        Object conditionExprObj = getValue(args[0], env);
        if (!(conditionExprObj instanceof String)) {
            throw new com.filter.dsl.functions.TypeMismatchException(
                "IF expects a string expression as argument, got " +
                (conditionExprObj == null ? "null" : conditionExprObj.getClass().getSimpleName())
            );
        }

        String conditionExpr = (String) conditionExprObj;

        // Get the aviator instance from the environment
        Object aviatorObj = env.get("__aviator__");
        if (aviatorObj == null || !(aviatorObj instanceof AviatorEvaluatorInstance)) {
            throw new RuntimeException("AviatorScript instance not found in environment. This is a configuration error.");
        }

        AviatorEvaluatorInstance aviator = (AviatorEvaluatorInstance) aviatorObj;

        // Compile the condition expression once
        Expression compiledCondition;
        try {
            compiledCondition = aviator.compile(conditionExpr, true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to compile IF condition expression: " + conditionExpr, e);
        }

        // Get time range from context (if set by FROM/TO functions)
        TimeRange timeRange = getTimeRange(env);

        // Filter events based on condition
        List<Event> filteredEvents = new ArrayList<>();

        for (Event event : allEvents) {
            // Apply time range filter if present
            if (timeRange != null) {
                Instant eventTime = parseTimestamp(event.getTimestamp());
                if (eventTime != null && !timeRange.contains(eventTime)) {
                    continue; // Skip events outside time range
                }
            }

            // Create event-specific context
            Map<String, Object> eventEnv = new java.util.HashMap<>(env);
            eventEnv.put("currentEvent", event);

            // Evaluate condition for this event
            try {
                Object result = compiledCondition.execute(eventEnv);

                // Check if condition is true
                if (result instanceof Boolean && (Boolean) result) {
                    filteredEvents.add(event);
                }
            } catch (Exception e) {
                // If condition evaluation fails for this event, skip it
                // This allows graceful handling of missing fields, etc.
            }
        }

        return AviatorRuntimeJavaType.valueOf(filteredEvents);
    }

    /**
     * Parse timestamp string to Instant.
     * Supports ISO-8601 format.
     *
     * @param timestamp The timestamp string
     * @return The Instant, or null if parsing fails
     */
    private Instant parseTimestamp(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            return null;
        }

        try {
            return Instant.parse(timestamp);
        } catch (Exception e) {
            // Try parsing as epoch milliseconds
            try {
                long epochMilli = Long.parseLong(timestamp);
                return Instant.ofEpochMilli(epochMilli);
            } catch (Exception e2) {
                return null;
            }
        }
    }

    // Override the single-argument call method for AviatorScript compatibility
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        return call(env, new AviatorObject[]{arg1});
    }
}

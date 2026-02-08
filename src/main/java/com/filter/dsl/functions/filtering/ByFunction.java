package com.filter.dsl.functions.filtering;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.filter.dsl.models.Event;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorRuntimeJavaType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * BY function - Group collection items by field value or extract values using an expression.
 *
 * Usage: BY(expression)
 *
 * The BY function evaluates an expression for each item in the current collection context
 * and returns a collection of the resulting values. This is typically used to:
 * 1. Extract field values from events
 * 2. Transform events into derived values (e.g., formatted dates)
 * 3. Group events by a specific attribute
 *
 * The function works in conjunction with the collection context set by IF or WHERE functions.
 * When used with UNIQUE, it provides distinct values for grouping or counting.
 *
 * Examples:
 * - BY(EVENT("event_name")) -> Extract event names from events
 * - BY(DATE_FORMAT(ACTION_TIME(), "yyyy-MM-dd")) -> Extract dates from events
 * - BY(PARAM("category")) -> Extract category parameter values
 * - UNIQUE(BY(EVENT("event_type"))) -> Get distinct event types
 * - COUNT(UNIQUE(BY(DATE_FORMAT(...)))) -> Count distinct dates
 *
 * The function operates on the events collection from userData by default,
 * or on a collection provided through the context.
 *
 * Returns: A collection of values extracted/computed from each item
 */
public class ByFunction extends DSLFunction {

    @Override
    public String getName() {
        return "BY";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("BY")
            .minArgs(1)
            .maxArgs(1)
            .argumentType(0, ArgumentType.ANY) // Expression to evaluate per item
            .returnType(ReturnType.COLLECTION)
            .description("Groups collection items by field value or extracts values using an expression")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 1);

        // Get the collection to process from userData
        Object userData = getUserData(env);
        Collection<?> collection = null;

        if (userData != null) {
            // OPTIMIZED: Direct cast instead of try-catch
            if (userData instanceof com.filter.dsl.models.UserData) {
                com.filter.dsl.models.UserData ud = (com.filter.dsl.models.UserData) userData;
                collection = ud.getEvents();
            } else if (userData instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> userDataMap = (Map<String, Object>) userData;
                Object events = userDataMap.get("events");
                if (events instanceof Collection) {
                    collection = (Collection<?>) events;
                }
            }
        }

        // If no collection found, return empty list
        if (collection == null || collection.isEmpty()) {
            return AviatorRuntimeJavaType.valueOf(new ArrayList<>());
        }

        // OPTIMIZED: Pre-allocate list with exact size
        List<Object> extractedValues = new ArrayList<>(collection.size());

        // OPTIMIZED: Create HashMap ONCE and reuse it (instead of creating 100K times!)
        Map<String, Object> itemEnv = new java.util.HashMap<>(env);

        // Extract values by evaluating the expression for each item
        for (Object item : collection) {
            // OPTIMIZED: Just update currentEvent, don't recreate the entire map
            if (item instanceof Event) {
                itemEnv.put("currentEvent", item);
            }

            // Evaluate the expression for this item
            try {
                Object value = args[0].getValue(itemEnv);

                // Add the extracted value (can be null)
                extractedValues.add(value);
            } catch (Exception e) {
                // If expression evaluation fails for this item, add null
                // This allows graceful handling of missing fields
                extractedValues.add(null);
            }
        }

        return AviatorRuntimeJavaType.valueOf(extractedValues);
    }

    // Override the single-argument call method for AviatorScript compatibility
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        return call(env, new AviatorObject[]{arg1});
    }
}

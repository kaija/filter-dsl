package com.filter.dsl.functions;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.filter.dsl.models.Event;
import com.filter.dsl.models.Visit;
import com.filter.dsl.models.TimeRange;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;

/**
 * Abstract base class for all DSL functions.
 *
 * This class provides a foundation for implementing custom DSL functions with:
 * - Argument validation helpers
 * - Type conversion utilities
 * - Context access methods for user data, events, visits, and time ranges
 * - Metadata support for function validation
 *
 * Extension developers should:
 * 1. Extend this class
 * 2. Implement getName() to return the UPPERCASE function name
 * 3. Implement getFunctionMetadata() to provide function signature information
 * 4. Implement call() to define the function logic
 * 5. Register the function with FunctionRegistry
 *
 * Example:
 * <pre>
 * public class CountFunction extends DSLFunction {
 *     {@literal @}Override
 *     public String getName() {
 *         return "COUNT";
 *     }
 *
 *     {@literal @}Override
 *     public FunctionMetadata getFunctionMetadata() {
 *         return FunctionMetadata.builder()
 *             .name("COUNT")
 *             .minArgs(1)
 *             .maxArgs(1)
 *             .argumentType(0, ArgumentType.COLLECTION)
 *             .returnType(ReturnType.NUMBER)
 *             .build();
 *     }
 *
 *     {@literal @}Override
 *     public AviatorObject call(Map&lt;String, Object&gt; env, AviatorObject... args) {
 *         validateArgCount(args, 1);
 *         Collection&lt;?&gt; collection = toCollection(args[0], env);
 *         return AviatorLong.valueOf(collection.size());
 *     }
 * }
 * </pre>
 */
public abstract class DSLFunction extends AbstractFunction {

    /**
     * Get the UPPERCASE function name.
     * This name is used in DSL expressions.
     *
     * @return The function name (must be UPPERCASE)
     */
    @Override
    public abstract String getName();

    /**
     * Get function metadata for validation.
     * Metadata includes argument count, types, and return type.
     *
     * @return Function metadata
     */
    public abstract FunctionMetadata getFunctionMetadata();

    /**
     * Execute the function with the given arguments.
     *
     * @param env The evaluation environment containing user data and context
     * @param args The function arguments as AviatorObjects
     * @return The function result as an AviatorObject
     */
    public abstract AviatorObject call(Map<String, Object> env, AviatorObject... args);

    // ========== Argument Validation Helpers ==========

    /**
     * Validate that the function received exactly the expected number of arguments.
     *
     * @param args The function arguments
     * @param expected The expected argument count
     * @throws FunctionArgumentException if argument count doesn't match
     */
    protected void validateArgCount(AviatorObject[] args, int expected) {
        if (args.length != expected) {
            throw new FunctionArgumentException(
                getName() + " expects " + expected + " argument(s), got " + args.length
            );
        }
    }

    /**
     * Validate that the function received an argument count within the specified range.
     *
     * @param args The function arguments
     * @param min The minimum argument count (inclusive)
     * @param max The maximum argument count (inclusive)
     * @throws FunctionArgumentException if argument count is out of range
     */
    protected void validateArgCountRange(AviatorObject[] args, int min, int max) {
        if (args.length < min || args.length > max) {
            throw new FunctionArgumentException(
                getName() + " expects " + min + "-" + max + " argument(s), got " + args.length
            );
        }
    }

    /**
     * Validate that the function received at least the minimum number of arguments.
     *
     * @param args The function arguments
     * @param min The minimum argument count (inclusive)
     * @throws FunctionArgumentException if argument count is less than minimum
     */
    protected void validateMinArgCount(AviatorObject[] args, int min) {
        if (args.length < min) {
            throw new FunctionArgumentException(
                getName() + " expects at least " + min + " argument(s), got " + args.length
            );
        }
    }

    // ========== Context Access Methods ==========

    /**
     * Get the complete user data from the evaluation context.
     *
     * @param env The evaluation environment
     * @return The user data object, or null if not present
     */
    protected Object getUserData(Map<String, Object> env) {
        return env.get("userData");
    }

    /**
     * Get the current event being evaluated from the context.
     * This is set when evaluating expressions in an event-specific context.
     *
     * @param env The evaluation environment
     * @return The current event, or null if not in event context
     */
    protected Event getCurrentEvent(Map<String, Object> env) {
        return (Event) env.get("currentEvent");
    }

    /**
     * Get the current visit being evaluated from the context.
     * This is set when evaluating expressions in a visit-specific context.
     *
     * @param env The evaluation environment
     * @return The current visit, or null if not in visit context
     */
    protected Visit getCurrentVisit(Map<String, Object> env) {
        return (Visit) env.get("currentVisit");
    }

    /**
     * Get the current timestamp for evaluation.
     * This is typically set to the time when evaluation started.
     *
     * @param env The evaluation environment
     * @return The current timestamp, or Instant.now() if not set
     */
    protected Instant getNow(Map<String, Object> env) {
        Object now = env.get("now");
        return now instanceof Instant ? (Instant) now : Instant.now();
    }

    /**
     * Get the time range filter from the context.
     * This is set by FROM/TO functions to filter events by time.
     *
     * @param env The evaluation environment
     * @return The time range, or null if not set
     */
    protected TimeRange getTimeRange(Map<String, Object> env) {
        return (TimeRange) env.get("timeRange");
    }

    // ========== Type Conversion Helpers ==========

    /**
     * Convert an AviatorObject to a Number.
     *
     * @param obj The AviatorObject to convert
     * @param env The evaluation environment
     * @return The numeric value
     * @throws TypeMismatchException if the value is not a number
     */
    protected Number toNumber(AviatorObject obj, Map<String, Object> env) {
        Object value = obj.getValue(env);
        if (value instanceof Number) {
            return (Number) value;
        }
        throw new TypeMismatchException(
            "Expected number, got " + (value == null ? "null" : value.getClass().getSimpleName())
        );
    }

    /**
     * Convert an AviatorObject to a String.
     *
     * @param obj The AviatorObject to convert
     * @param env The evaluation environment
     * @return The string value, or null if the value is null
     */
    protected String toString(AviatorObject obj, Map<String, Object> env) {
        Object value = obj.getValue(env);
        return value == null ? null : value.toString();
    }

    /**
     * Convert an AviatorObject to a Boolean.
     *
     * @param obj The AviatorObject to convert
     * @param env The evaluation environment
     * @return The boolean value
     * @throws TypeMismatchException if the value is not a boolean
     */
    protected Boolean toBoolean(AviatorObject obj, Map<String, Object> env) {
        Object value = obj.getValue(env);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        throw new TypeMismatchException(
            "Expected boolean, got " + (value == null ? "null" : value.getClass().getSimpleName())
        );
    }

    /**
     * Convert an AviatorObject to a Collection.
     *
     * @param obj The AviatorObject to convert
     * @param env The evaluation environment
     * @return The collection value
     * @throws TypeMismatchException if the value is not a collection
     */
    protected Collection<?> toCollection(AviatorObject obj, Map<String, Object> env) {
        Object value = obj.getValue(env);
        if (value instanceof Collection) {
            return (Collection<?>) value;
        }
        throw new TypeMismatchException(
            "Expected collection, got " + (value == null ? "null" : value.getClass().getSimpleName())
        );
    }

    /**
     * Safely get a value from an AviatorObject, returning null if the object is null.
     *
     * @param obj The AviatorObject to get value from
     * @param env The evaluation environment
     * @return The value, or null if obj is null
     */
    protected Object getValue(AviatorObject obj, Map<String, Object> env) {
        return obj == null ? null : obj.getValue(env);
    }

    // ========== New Helper Methods for Simplified DSL ==========

    /**
     * Get events from userData in the evaluation context.
     * Returns an empty list if userData or events are not available.
     *
     * @param env The evaluation environment
     * @return Collection of events from userData
     */
    protected Collection<?> getUserDataEvents(Map<String, Object> env) {
        Object userData = env.get("userData");
        if (userData == null) {
            return java.util.Collections.emptyList();
        }

        try {
            // Use reflection to get events from UserData
            java.lang.reflect.Method getEventsMethod = userData.getClass().getMethod("getEvents");
            Object events = getEventsMethod.invoke(userData);
            if (events instanceof Collection) {
                return (Collection<?>) events;
            }
        } catch (Exception e) {
            // If we can't get events, return empty list
        }

        return java.util.Collections.emptyList();
    }

    /**
     * Filter a collection based on a condition expression.
     * The condition is evaluated for each item with that item set as the current context.
     *
     * @param collection The collection to filter
     * @param conditionExpr The condition expression as a string
     * @param env The evaluation environment
     * @return Filtered collection
     */
    protected Collection<?> filterCollection(Collection<?> collection, String conditionExpr, Map<String, Object> env) {
        if (collection == null || collection.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        // Get the aviator instance from the environment
        Object aviatorObj = env.get("__aviator__");
        if (aviatorObj == null || !(aviatorObj instanceof com.googlecode.aviator.AviatorEvaluatorInstance)) {
            throw new RuntimeException("AviatorScript instance not found in environment");
        }

        com.googlecode.aviator.AviatorEvaluatorInstance aviator = 
            (com.googlecode.aviator.AviatorEvaluatorInstance) aviatorObj;

        // Compile the condition expression once
        com.googlecode.aviator.Expression compiledCondition;
        try {
            compiledCondition = aviator.compile(conditionExpr, true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to compile filter condition: " + conditionExpr, e);
        }

        // Get time range from context (if set by FROM/TO functions)
        TimeRange timeRange = getTimeRange(env);

        // Filter collection based on condition
        java.util.List<Object> filteredItems = new java.util.ArrayList<>();

        for (Object item : collection) {
            // Apply time range filter if item is an Event
            if (timeRange != null && item instanceof Event) {
                Event event = (Event) item;
                Instant eventTime = parseTimestamp(event.getTimestamp());
                if (eventTime != null && !timeRange.contains(eventTime)) {
                    continue; // Skip events outside time range
                }
            }

            // Create item-specific context
            Map<String, Object> itemEnv = new java.util.HashMap<>(env);

            // If item is an Event, set it as currentEvent
            if (item instanceof Event) {
                itemEnv.put("currentEvent", item);
            }

            // Evaluate condition for this item
            try {
                Object result = compiledCondition.execute(itemEnv);

                // Check if condition is true
                if (result instanceof Boolean && (Boolean) result) {
                    filteredItems.add(item);
                }
            } catch (Exception e) {
                // If condition evaluation fails for this item, skip it
            }
        }

        return filteredItems;
    }

    /**
     * Convert an object to a collection.
     * Handles Collection, arrays, and null values.
     *
     * @param obj The object to convert
     * @return Collection representation
     */
    protected Collection<?> toCollection(Object obj) {
        if (obj == null) {
            return java.util.Collections.emptyList();
        }

        if (obj instanceof Collection) {
            return (Collection<?>) obj;
        }

        if (obj.getClass().isArray()) {
            java.util.List<Object> list = new java.util.ArrayList<>();
            int length = java.lang.reflect.Array.getLength(obj);
            for (int i = 0; i < length; i++) {
                list.add(java.lang.reflect.Array.get(obj, i));
            }
            return list;
        }

        throw new TypeMismatchException(
            "Expected collection or array, got " + obj.getClass().getSimpleName()
        );
    }

    /**
     * Parse timestamp string to Instant.
     * Supports ISO-8601 format and epoch milliseconds.
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
}

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
}

package com.filter.dsl.functions.aggregation;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorRuntimeJavaType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * UNIQUE function - Returns only distinct values from a collection, preserving order of first occurrence.
 *
 * Usage: UNIQUE(collection)
 *
 * Examples:
 * - UNIQUE([1, 2, 2, 3, 1, 4]) -> [1, 2, 3, 4]
 * - UNIQUE(["apple", "banana", "apple", "cherry"]) -> ["apple", "banana", "cherry"]
 * - UNIQUE([]) -> []
 * - UNIQUE(null) -> []
 *
 * The function removes duplicates while preserving the order of first occurrence.
 * Returns an empty collection for null or empty input as specified in Requirements 3.6.
 * Null values in the collection are preserved as a single null in the output.
 */
public class UniqueFunction extends DSLFunction {

    @Override
    public String getName() {
        return "UNIQUE";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("UNIQUE")
            .minArgs(0)
            .maxArgs(2)
            .argumentType(0, ArgumentType.ANY)
            .argumentType(1, ArgumentType.STRING)
            .returnType(ReturnType.COLLECTION)
            .description("Returns distinct values. Defaults to userData.events with optional filter condition.")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        // No arguments: UNIQUE() -> unique events from userData.events
        if (args.length == 0) {
            Collection<?> events = getUserDataEvents(env);
            return uniqueCollection(events);
        }

        Object firstArg = getValue(args[0], env);

        // Single string argument: UNIQUE("condition") -> filter and get unique
        if (args.length == 1 && firstArg instanceof String) {
            Collection<?> events = getUserDataEvents(env);
            Collection<?> filtered = filterCollection(events, (String) firstArg, env);
            return uniqueCollection(filtered);
        }

        // Single collection argument: UNIQUE(collection) -> legacy syntax
        if (args.length == 1) {
            if (firstArg == null) {
                return new AviatorRuntimeJavaType(new ArrayList<>());
            }

            if (firstArg instanceof Collection) {
                Collection<?> collection = (Collection<?>) firstArg;
                if (collection.isEmpty()) {
                    return new AviatorRuntimeJavaType(new ArrayList<>());
                }
                return uniqueCollection(collection);
            }

            if (firstArg.getClass().isArray()) {
                return uniqueArray(firstArg);
            }

            throw new com.filter.dsl.functions.TypeMismatchException(
                "UNIQUE expects a collection, array, or filter condition, got " + firstArg.getClass().getSimpleName()
            );
        }

        // Two arguments: UNIQUE(collection, "condition") -> legacy syntax with filter
        if (args.length == 2) {
            Object conditionObj = getValue(args[1], env);
            if (!(conditionObj instanceof String)) {
                throw new com.filter.dsl.functions.TypeMismatchException(
                    "UNIQUE condition must be a string expression"
                );
            }

            Collection<?> collection = toCollection(firstArg);
            Collection<?> filtered = filterCollection(collection, (String) conditionObj, env);
            return uniqueCollection(filtered);
        }

        throw new com.filter.dsl.functions.FunctionArgumentException(
            "UNIQUE expects 0-2 arguments, got " + args.length
        );
    }

    /**
     * Remove duplicates from a collection while preserving order of first occurrence.
     * Uses LinkedHashSet to maintain insertion order while ensuring uniqueness.
     *
     * @param collection The collection to process
     * @return AviatorObject containing a list of unique values
     */
    private AviatorObject uniqueCollection(Collection<?> collection) {
        // LinkedHashSet maintains insertion order and ensures uniqueness
        LinkedHashSet<Object> uniqueSet = new LinkedHashSet<>(collection);

        // Convert back to list to maintain collection type consistency
        List<Object> uniqueList = new ArrayList<>(uniqueSet);

        return new AviatorRuntimeJavaType(uniqueList);
    }

    /**
     * Remove duplicates from an array while preserving order of first occurrence.
     *
     * @param array The array to process
     * @return AviatorObject containing a list of unique values
     */
    private AviatorObject uniqueArray(Object array) {
        int length = java.lang.reflect.Array.getLength(array);

        // Empty array returns empty list
        if (length == 0) {
            return new AviatorRuntimeJavaType(new ArrayList<>());
        }

        // LinkedHashSet maintains insertion order and ensures uniqueness
        LinkedHashSet<Object> uniqueSet = new LinkedHashSet<>();

        for (int i = 0; i < length; i++) {
            Object item = java.lang.reflect.Array.get(array, i);
            uniqueSet.add(item);
        }

        // Convert to list
        List<Object> uniqueList = new ArrayList<>(uniqueSet);

        return new AviatorRuntimeJavaType(uniqueList);
    }

    // Override for AviatorScript compatibility
    @Override
    public AviatorObject call(Map<String, Object> env) {
        return call(env, new AviatorObject[]{});
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        return call(env, new AviatorObject[]{arg1});
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        return call(env, new AviatorObject[]{arg1, arg2});
    }
}

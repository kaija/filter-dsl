package com.filter.dsl.functions.aggregation;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorDouble;
import com.googlecode.aviator.runtime.type.AviatorNil;

import java.util.Collection;
import java.util.Map;

/**
 * AVG function - Returns the arithmetic mean of all numeric values in a collection.
 *
 * Usage: AVG(collection)
 *
 * Examples:
 * - AVG([1, 2, 3, 4, 5]) -> 3.0
 * - AVG([10, 20, 30]) -> 20.0
 * - AVG([1.5, 2.5]) -> 2.0
 * - AVG([]) -> null
 * - AVG(null) -> null
 *
 * The function calculates the sum of all values divided by the count.
 * Returns null for empty collections as specified in Requirements 3.7.
 * Null values in the collection are skipped (not counted).
 */
public class AvgFunction extends DSLFunction {

    @Override
    public String getName() {
        return "AVG";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("AVG")
            .minArgs(1)
            .maxArgs(1)
            .argumentType(0, ArgumentType.COLLECTION)
            .returnType(ReturnType.NUMBER)
            .description("Returns the arithmetic mean of all numeric values in a collection")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 1);

        Object value = getValue(args[0], env);

        // Handle null - return null as per Requirements 3.7
        if (value == null) {
            return AviatorNil.NIL;
        }

        // Handle collection
        if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;

            // Empty collection returns null as per Requirements 3.7
            if (collection.isEmpty()) {
                return AviatorNil.NIL;
            }

            return avgCollection(collection);
        }

        // Handle array
        if (value.getClass().isArray()) {
            return avgArray(value);
        }

        throw new com.filter.dsl.functions.TypeMismatchException(
            "AVG expects a collection or array of numbers, got " + value.getClass().getSimpleName()
        );
    }

    /**
     * Calculate the average of a collection of numbers.
     * Supports both direct numbers and Event objects (extracts numeric parameters).
     *
     * @param collection The collection to average
     * @return AviatorObject containing the average, or null if no valid numbers
     */
    private AviatorObject avgCollection(Collection<?> collection) {
        double sum = 0.0;
        int count = 0;

        for (Object item : collection) {
            if (item == null) {
                continue; // Skip null values
            }

            // Handle Event objects - extract numeric parameters
            if (item instanceof com.filter.dsl.models.Event) {
                com.filter.dsl.models.Event event = (com.filter.dsl.models.Event) item;
                Map<String, Object> params = event.getParameters();

                if (params != null) {
                    // Average all numeric parameters from the event
                    for (Object paramValue : params.values()) {
                        if (paramValue instanceof Number) {
                            Number num = (Number) paramValue;
                            sum += num.doubleValue();
                            count++;
                        }
                    }
                }
                continue;
            }

            if (item instanceof Number) {
                Number num = (Number) item;
                sum += num.doubleValue();
                count++;
            } else {
                throw new com.filter.dsl.functions.TypeMismatchException(
                    "AVG expects numeric values or Event objects, got " + item.getClass().getSimpleName()
                );
            }
        }

        // If all values were null, return null
        if (count == 0) {
            return AviatorNil.NIL;
        }

        // Calculate and return average
        return AviatorDouble.valueOf(sum / count);
    }

    /**
     * Calculate the average of an array of numbers.
     *
     * @param array The array to average
     * @return AviatorObject containing the average, or null if no valid numbers
     */
    private AviatorObject avgArray(Object array) {
        int length = java.lang.reflect.Array.getLength(array);

        // Empty array returns null
        if (length == 0) {
            return AviatorNil.NIL;
        }

        double sum = 0.0;
        int count = 0;

        for (int i = 0; i < length; i++) {
            Object item = java.lang.reflect.Array.get(array, i);

            if (item == null) {
                continue; // Skip null values
            }

            if (item instanceof Number) {
                Number num = (Number) item;
                sum += num.doubleValue();
                count++;
            } else {
                throw new com.filter.dsl.functions.TypeMismatchException(
                    "AVG expects numeric values, got " + item.getClass().getSimpleName()
                );
            }
        }

        // If all values were null, return null
        if (count == 0) {
            return AviatorNil.NIL;
        }

        // Calculate and return average
        return AviatorDouble.valueOf(sum / count);
    }

    // Override the single-argument call method for AviatorScript compatibility
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        return call(env, new AviatorObject[]{arg1});
    }
}

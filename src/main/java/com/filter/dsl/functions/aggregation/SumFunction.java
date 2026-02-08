package com.filter.dsl.functions.aggregation;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorDouble;
import com.googlecode.aviator.runtime.type.AviatorLong;

import java.util.Collection;
import java.util.Map;

/**
 * SUM function - Returns the sum of all numeric values in a collection.
 * 
 * Usage:
 * - SUM() -> Sum all events from userData.events
 * - SUM("condition") -> Sum events matching the condition
 * - SUM(collection) -> Sum items in the provided collection (legacy)
 * - SUM(collection, "condition") -> Sum items in collection matching condition (legacy)
 * 
 * Examples:
 * - SUM() -> Sum all event parameters
 * - SUM("EQ(EVENT(\"eventName\"), \"purchase\")") -> Sum purchase amounts
 * - SUM([1, 2, 3, 4, 5]) -> 15 (legacy)
 * 
 * The function handles both integer and floating-point numbers.
 * Returns 0 for empty collections as specified in Requirements 3.7.
 */
public class SumFunction extends DSLFunction {

    @Override
    public String getName() {
        return "SUM";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("SUM")
            .minArgs(0)
            .maxArgs(2)
            .argumentType(0, ArgumentType.ANY)
            .argumentType(1, ArgumentType.STRING)
            .returnType(ReturnType.NUMBER)
            .description("Returns the sum of numeric values. Defaults to userData.events with optional filter condition.")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        // No arguments: SUM() -> sum all events
        if (args.length == 0) {
            Collection<?> events = getUserDataEvents(env);
            return sumCollection(events);
        }

        Object firstArg = getValue(args[0], env);

        // Single string argument: SUM("condition") -> filter and sum events
        if (args.length == 1 && firstArg instanceof String) {
            Collection<?> events = getUserDataEvents(env);
            Collection<?> filtered = filterCollection(events, (String) firstArg, env);
            return sumCollection(filtered);
        }

        // Single collection argument: SUM(collection) -> legacy syntax
        if (args.length == 1) {
            if (firstArg == null) {
                return AviatorLong.valueOf(0);
            }

            if (firstArg instanceof Collection) {
                Collection<?> collection = (Collection<?>) firstArg;
                if (collection.isEmpty()) {
                    return AviatorLong.valueOf(0);
                }
                return sumCollection(collection);
            }

            if (firstArg.getClass().isArray()) {
                return sumArray(firstArg);
            }

            throw new com.filter.dsl.functions.TypeMismatchException(
                "SUM expects a collection, array, or filter condition, got " + firstArg.getClass().getSimpleName()
            );
        }

        // Two arguments: SUM(collection, "condition") -> legacy syntax with filter
        if (args.length == 2) {
            Object conditionObj = getValue(args[1], env);
            if (!(conditionObj instanceof String)) {
                throw new com.filter.dsl.functions.TypeMismatchException(
                    "SUM condition must be a string expression"
                );
            }

            Collection<?> collection = toCollection(firstArg);
            Collection<?> filtered = filterCollection(collection, (String) conditionObj, env);
            return sumCollection(filtered);
        }

        throw new com.filter.dsl.functions.FunctionArgumentException(
            "SUM expects 0-2 arguments, got " + args.length
        );
    }
    
    /**
     * Calculate the sum of a collection of numbers.
     * Supports both direct numbers and Event objects (extracts numeric parameters).
     * 
     * @param collection The collection to sum
     * @return AviatorObject containing the sum
     */
    private AviatorObject sumCollection(Collection<?> collection) {
        double sum = 0.0;
        boolean hasDouble = false;
        
        for (Object item : collection) {
            if (item == null) {
                continue; // Skip null values
            }
            
            // Handle Event objects - extract numeric parameters
            if (item instanceof com.filter.dsl.models.Event) {
                com.filter.dsl.models.Event event = (com.filter.dsl.models.Event) item;
                Map<String, Object> params = event.getParameters();
                
                if (params != null) {
                    // Sum all numeric parameters from the event
                    for (Object paramValue : params.values()) {
                        if (paramValue instanceof Number) {
                            Number num = (Number) paramValue;
                            sum += num.doubleValue();
                            
                            if (paramValue instanceof Double || paramValue instanceof Float) {
                                hasDouble = true;
                            }
                        }
                    }
                }
                continue;
            }
            
            if (item instanceof Number) {
                Number num = (Number) item;
                sum += num.doubleValue();
                
                // Track if we have any floating-point numbers
                if (item instanceof Double || item instanceof Float) {
                    hasDouble = true;
                }
            } else {
                throw new com.filter.dsl.functions.TypeMismatchException(
                    "SUM expects numeric values or Event objects, got " + item.getClass().getSimpleName()
                );
            }
        }
        
        // Return Long if all values were integers, otherwise return Double
        if (!hasDouble && sum == Math.floor(sum)) {
            return AviatorLong.valueOf((long) sum);
        } else {
            return AviatorDouble.valueOf(sum);
        }
    }
    
    /**
     * Calculate the sum of an array of numbers.
     * 
     * @param array The array to sum
     * @return AviatorObject containing the sum
     */
    private AviatorObject sumArray(Object array) {
        int length = java.lang.reflect.Array.getLength(array);
        
        // Empty array returns 0
        if (length == 0) {
            return AviatorLong.valueOf(0);
        }
        
        double sum = 0.0;
        boolean hasDouble = false;
        
        for (int i = 0; i < length; i++) {
            Object item = java.lang.reflect.Array.get(array, i);
            
            if (item == null) {
                continue; // Skip null values
            }
            
            if (item instanceof Number) {
                Number num = (Number) item;
                sum += num.doubleValue();
                
                // Track if we have any floating-point numbers
                if (item instanceof Double || item instanceof Float) {
                    hasDouble = true;
                }
            } else {
                throw new com.filter.dsl.functions.TypeMismatchException(
                    "SUM expects numeric values, got " + item.getClass().getSimpleName()
                );
            }
        }
        
        // Return Long if all values were integers, otherwise return Double
        if (!hasDouble && sum == Math.floor(sum)) {
            return AviatorLong.valueOf((long) sum);
        } else {
            return AviatorDouble.valueOf(sum);
        }
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

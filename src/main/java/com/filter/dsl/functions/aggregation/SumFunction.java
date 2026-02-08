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
 * Usage: SUM(collection)
 * 
 * Examples:
 * - SUM([1, 2, 3, 4, 5]) -> 15
 * - SUM([1.5, 2.5, 3.0]) -> 7.0
 * - SUM([]) -> 0
 * - SUM(null) -> 0
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
            .minArgs(1)
            .maxArgs(1)
            .argumentType(0, ArgumentType.COLLECTION)
            .returnType(ReturnType.NUMBER)
            .description("Returns the sum of all numeric values in a collection")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 1);
        
        Object value = getValue(args[0], env);
        
        // Handle null - return 0 as per Requirements 3.7
        if (value == null) {
            return AviatorLong.valueOf(0);
        }
        
        // Handle collection
        if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            
            // Empty collection returns 0 as per Requirements 3.7
            if (collection.isEmpty()) {
                return AviatorLong.valueOf(0);
            }
            
            return sumCollection(collection);
        }
        
        // Handle array
        if (value.getClass().isArray()) {
            return sumArray(value);
        }
        
        throw new com.filter.dsl.functions.TypeMismatchException(
            "SUM expects a collection or array of numbers, got " + value.getClass().getSimpleName()
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
    
    // Override the single-argument call method for AviatorScript compatibility
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        return call(env, new AviatorObject[]{arg1});
    }
}

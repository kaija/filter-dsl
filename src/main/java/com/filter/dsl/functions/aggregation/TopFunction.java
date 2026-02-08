package com.filter.dsl.functions.aggregation;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorRuntimeJavaType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TOP function - Returns the most frequently occurring value(s) from a collection.
 *
 * Usage: 
 * - TOP(collection) - Returns the single most frequent value
 * - TOP(collection, n) - Returns the top n most frequent values as a list
 * - TOP(collection, propertyName) - Returns the most frequent value of a property
 * - TOP(collection, propertyName, n) - Returns the top n most frequent values of a property
 *
 * Examples:
 * - TOP([1, 2, 2, 3, 2, 1]) -> 2
 * - TOP([1, 2, 2, 3, 2, 1], 2) -> [2, 1]
 * - TOP(userData.visits, "os") -> "Windows 10"
 * - TOP(userData.visits, "browser", 3) -> ["Chrome", "Safari", "Firefox"]
 * - TOP(userData.events, "eventName") -> "page_view"
 *
 * The function counts occurrences and returns the most frequent value(s).
 * For ties, values are returned in order of first occurrence.
 * Returns null for empty collections.
 */
public class TopFunction extends DSLFunction {

    @Override
    public String getName() {
        return "TOP";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("TOP")
            .minArgs(1)
            .maxArgs(3)
            .argumentType(0, ArgumentType.COLLECTION)
            .argumentType(1, ArgumentType.ANY)
            .argumentType(2, ArgumentType.NUMBER)
            .returnType(ReturnType.ANY)
            .description("Returns the most frequently occurring value(s) from a collection")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        if (args.length == 0) {
            throw new com.filter.dsl.functions.FunctionArgumentException(
                "TOP requires at least 1 argument: collection"
            );
        }

        Object firstArg = getValue(args[0], env);
        
        if (firstArg == null) {
            return new AviatorRuntimeJavaType(null);
        }

        Collection<?> collection = toCollection(firstArg);
        
        if (collection.isEmpty()) {
            return new AviatorRuntimeJavaType(null);
        }

        // TOP(collection) - most frequent value
        if (args.length == 1) {
            Object result = getMostFrequent(collection, 1).stream().findFirst().orElse(null);
            return new AviatorRuntimeJavaType(result);
        }

        Object secondArg = getValue(args[1], env);

        // TOP(collection, n) - top n values
        if (args.length == 2 && secondArg instanceof Number) {
            int n = ((Number) secondArg).intValue();
            List<Object> result = getMostFrequent(collection, n);
            return new AviatorRuntimeJavaType(result);
        }

        // TOP(collection, propertyName) - most frequent property value
        if (args.length == 2 && secondArg instanceof String) {
            String propertyName = (String) secondArg;
            List<Object> propertyValues = extractProperty(collection, propertyName);
            Object result = getMostFrequent(propertyValues, 1).stream().findFirst().orElse(null);
            return new AviatorRuntimeJavaType(result);
        }

        // TOP(collection, propertyName, n) - top n property values
        if (args.length == 3) {
            if (!(secondArg instanceof String)) {
                throw new com.filter.dsl.functions.TypeMismatchException(
                    "TOP second argument must be a property name (string) when using 3 arguments"
                );
            }
            
            Object thirdArg = getValue(args[2], env);
            if (!(thirdArg instanceof Number)) {
                throw new com.filter.dsl.functions.TypeMismatchException(
                    "TOP third argument must be a number"
                );
            }

            String propertyName = (String) secondArg;
            int n = ((Number) thirdArg).intValue();
            List<Object> propertyValues = extractProperty(collection, propertyName);
            List<Object> result = getMostFrequent(propertyValues, n);
            return new AviatorRuntimeJavaType(result);
        }

        throw new com.filter.dsl.functions.FunctionArgumentException(
            "TOP invalid arguments. Expected: TOP(collection[, propertyName][, n])"
        );
    }

    /**
     * Gets the top n most frequent values from a collection.
     */
    private List<Object> getMostFrequent(Collection<?> collection, int n) {
        if (collection == null || collection.isEmpty()) {
            return new ArrayList<>();
        }

        // Count occurrences
        Map<Object, Long> frequencyMap = collection.stream()
            .collect(Collectors.groupingBy(
                obj -> obj == null ? "null" : obj,
                LinkedHashMap::new,
                Collectors.counting()
            ));

        // Sort by frequency (descending) and return top n
        return frequencyMap.entrySet().stream()
            .sorted((e1, e2) -> {
                int freqCompare = Long.compare(e2.getValue(), e1.getValue());
                if (freqCompare != 0) {
                    return freqCompare;
                }
                // For ties, maintain insertion order (first occurrence)
                return 0;
            })
            .limit(n)
            .map(Map.Entry::getKey)
            .map(obj -> "null".equals(obj) ? null : obj)
            .collect(Collectors.toList());
    }

    /**
     * Extracts property values from a collection of objects.
     */
    private List<Object> extractProperty(Collection<?> collection, String propertyName) {
        List<Object> result = new ArrayList<>();
        
        for (Object item : collection) {
            if (item == null) {
                continue;
            }

            Object value = extractPropertyValue(item, propertyName);
            if (value != null) {
                result.add(value);
            }
        }

        return result;
    }

    /**
     * Extracts a property value from an object.
     */
    private Object extractPropertyValue(Object obj, String propertyName) {
        if (obj == null || propertyName == null) {
            return null;
        }

        try {
            // Try Map access
            if (obj instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) obj;
                return map.get(propertyName);
            }

            // Try getter method
            String getterName = "get" + capitalize(propertyName);
            try {
                java.lang.reflect.Method getter = obj.getClass().getMethod(getterName);
                return getter.invoke(obj);
            } catch (NoSuchMethodException e) {
                // Try field access
                try {
                    java.lang.reflect.Field field = obj.getClass().getDeclaredField(propertyName);
                    field.setAccessible(true);
                    return field.get(obj);
                } catch (NoSuchFieldException ex) {
                    // Property not found
                    return null;
                }
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Capitalizes the first letter of a string.
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    // Override for AviatorScript compatibility
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        return call(env, new AviatorObject[]{arg1});
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        return call(env, new AviatorObject[]{arg1, arg2});
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3) {
        return call(env, new AviatorObject[]{arg1, arg2, arg3});
    }
}

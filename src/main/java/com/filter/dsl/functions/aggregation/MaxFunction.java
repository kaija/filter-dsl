package com.filter.dsl.functions.aggregation;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorNil;
import com.googlecode.aviator.runtime.type.AviatorDouble;
import com.googlecode.aviator.runtime.type.AviatorLong;
import com.googlecode.aviator.runtime.type.AviatorString;
import com.googlecode.aviator.runtime.type.AviatorJavaType;

import java.util.Collection;
import java.util.Map;

/**
 * MAX function - Returns the maximum value from a collection of comparable values.
 * 
 * Usage: MAX(collection)
 * 
 * Examples:
 * - MAX([1, 2, 3, 4, 5]) -> 5
 * - MAX([5.5, 2.2, 8.8, 1.1]) -> 8.8
 * - MAX(["apple", "banana", "cherry"]) -> "cherry"
 * - MAX([]) -> null
 * - MAX(null) -> null
 * 
 * The function handles both numeric and string comparisons.
 * Returns null for empty collections as specified in Requirements 3.7.
 * Null values in the collection are skipped.
 */
public class MaxFunction extends DSLFunction {

    @Override
    public String getName() {
        return "MAX";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("MAX")
            .minArgs(0)
            .maxArgs(2)
            .argumentType(0, ArgumentType.ANY)
            .argumentType(1, ArgumentType.STRING)
            .returnType(ReturnType.ANY)
            .description("Returns the maximum value. Defaults to userData.events with optional filter condition.")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        // No arguments: MAX() -> max from all events
        if (args.length == 0) {
            Collection<?> events = getUserDataEvents(env);
            if (events.isEmpty()) {
                return AviatorNil.NIL;
            }
            return maxCollection(events);
        }

        Object firstArg = getValue(args[0], env);

        // Single string argument: MAX("condition") -> filter and find max
        if (args.length == 1 && firstArg instanceof String) {
            Collection<?> events = getUserDataEvents(env);
            Collection<?> filtered = filterCollection(events, (String) firstArg, env);
            if (filtered.isEmpty()) {
                return AviatorNil.NIL;
            }
            return maxCollection(filtered);
        }

        // Single collection argument: MAX(collection) -> legacy syntax
        if (args.length == 1) {
            if (firstArg == null) {
                return AviatorNil.NIL;
            }

            if (firstArg instanceof Collection) {
                Collection<?> collection = (Collection<?>) firstArg;
                if (collection.isEmpty()) {
                    return AviatorNil.NIL;
                }
                return maxCollection(collection);
            }

            if (firstArg.getClass().isArray()) {
                return maxArray(firstArg);
            }

            throw new com.filter.dsl.functions.TypeMismatchException(
                "MAX expects a collection, array, or filter condition, got " + firstArg.getClass().getSimpleName()
            );
        }

        // Two arguments: MAX(collection, "condition") -> legacy syntax with filter
        if (args.length == 2) {
            Object conditionObj = getValue(args[1], env);
            if (!(conditionObj instanceof String)) {
                throw new com.filter.dsl.functions.TypeMismatchException(
                    "MAX condition must be a string expression"
                );
            }

            Collection<?> collection = toCollection(firstArg);
            Collection<?> filtered = filterCollection(collection, (String) conditionObj, env);
            if (filtered.isEmpty()) {
                return AviatorNil.NIL;
            }
            return maxCollection(filtered);
        }

        throw new com.filter.dsl.functions.FunctionArgumentException(
            "MAX expects 0-2 arguments, got " + args.length
        );
    }
    
    /**
     * Find the maximum value in a collection.
     * 
     * @param collection The collection to find maximum from
     * @return AviatorObject containing the maximum value, or null if no valid values
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private AviatorObject maxCollection(Collection<?> collection) {
        Object maxValue = null;
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
                    // Find max among all numeric parameters from the event
                    for (Object paramValue : params.values()) {
                        if (paramValue instanceof Number) {
                            if (maxValue == null) {
                                maxValue = paramValue;
                                if (paramValue instanceof Double || paramValue instanceof Float) {
                                    hasDouble = true;
                                }
                            } else if (maxValue instanceof Number) {
                                int comparison = compareNumbers((Number) paramValue, (Number) maxValue);
                                if (comparison > 0) {
                                    maxValue = paramValue;
                                    if (paramValue instanceof Double || paramValue instanceof Float) {
                                        hasDouble = true;
                                    }
                                }
                            }
                        }
                    }
                }
                continue;
            }
            
            if (maxValue == null) {
                maxValue = item;
                if (item instanceof Double || item instanceof Float) {
                    hasDouble = true;
                }
            } else {
                // Check if items are comparable
                if (!(item instanceof Comparable)) {
                    throw new com.filter.dsl.functions.TypeMismatchException(
                        "MAX expects comparable values, got " + item.getClass().getSimpleName()
                    );
                }
                
                if (!(maxValue instanceof Comparable)) {
                    throw new com.filter.dsl.functions.TypeMismatchException(
                        "MAX expects comparable values, got " + maxValue.getClass().getSimpleName()
                    );
                }
                
                // Check if types are compatible for comparison
                if (!areComparable(maxValue, item)) {
                    throw new com.filter.dsl.functions.TypeMismatchException(
                        "MAX cannot compare " + maxValue.getClass().getSimpleName() + 
                        " with " + item.getClass().getSimpleName()
                    );
                }
                
                try {
                    int comparison;
                    if (item instanceof Number && maxValue instanceof Number) {
                        comparison = compareNumbers((Number) item, (Number) maxValue);
                    } else {
                        comparison = ((Comparable) item).compareTo(maxValue);
                    }
                    
                    if (comparison > 0) {
                        maxValue = item;
                        if (item instanceof Double || item instanceof Float) {
                            hasDouble = true;
                        }
                    }
                } catch (ClassCastException e) {
                    throw new com.filter.dsl.functions.TypeMismatchException(
                        "MAX cannot compare " + maxValue.getClass().getSimpleName() + 
                        " with " + item.getClass().getSimpleName()
                    );
                }
            }
        }
        
        // If all values were null, return null
        if (maxValue == null) {
            return AviatorNil.NIL;
        }
        
        // Return appropriate AviatorObject type
        return wrapValue(maxValue, hasDouble);
    }
    
    /**
     * Find the maximum value in an array.
     * 
     * @param array The array to find maximum from
     * @return AviatorObject containing the maximum value, or null if no valid values
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private AviatorObject maxArray(Object array) {
        int length = java.lang.reflect.Array.getLength(array);
        
        // Empty array returns null
        if (length == 0) {
            return AviatorNil.NIL;
        }
        
        Object maxValue = null;
        boolean hasDouble = false;
        
        for (int i = 0; i < length; i++) {
            Object item = java.lang.reflect.Array.get(array, i);
            
            if (item == null) {
                continue; // Skip null values
            }
            
            if (maxValue == null) {
                maxValue = item;
                if (item instanceof Double || item instanceof Float) {
                    hasDouble = true;
                }
            } else {
                // Check if items are comparable
                if (!(item instanceof Comparable)) {
                    throw new com.filter.dsl.functions.TypeMismatchException(
                        "MAX expects comparable values, got " + item.getClass().getSimpleName()
                    );
                }
                
                if (!(maxValue instanceof Comparable)) {
                    throw new com.filter.dsl.functions.TypeMismatchException(
                        "MAX expects comparable values, got " + maxValue.getClass().getSimpleName()
                    );
                }
                
                // Check if types are compatible for comparison
                if (!areComparable(maxValue, item)) {
                    throw new com.filter.dsl.functions.TypeMismatchException(
                        "MAX cannot compare " + maxValue.getClass().getSimpleName() + 
                        " with " + item.getClass().getSimpleName()
                    );
                }
                
                try {
                    int comparison;
                    if (item instanceof Number && maxValue instanceof Number) {
                        comparison = compareNumbers((Number) item, (Number) maxValue);
                    } else {
                        comparison = ((Comparable) item).compareTo(maxValue);
                    }
                    
                    if (comparison > 0) {
                        maxValue = item;
                        if (item instanceof Double || item instanceof Float) {
                            hasDouble = true;
                        }
                    }
                } catch (ClassCastException e) {
                    throw new com.filter.dsl.functions.TypeMismatchException(
                        "MAX cannot compare " + maxValue.getClass().getSimpleName() + 
                        " with " + item.getClass().getSimpleName()
                    );
                }
            }
        }
        
        // If all values were null, return null
        if (maxValue == null) {
            return AviatorNil.NIL;
        }
        
        // Return appropriate AviatorObject type
        return wrapValue(maxValue, hasDouble);
    }
    
    /**
     * Check if two values can be compared.
     * Numbers can be compared with other numbers.
     * Strings can be compared with other strings.
     * 
     * @param a First value
     * @param b Second value
     * @return true if values can be compared
     */
    private boolean areComparable(Object a, Object b) {
        // Both are numbers - all Number types can be compared
        if (a instanceof Number && b instanceof Number) {
            return true;
        }
        
        // Both are strings
        if (a instanceof String && b instanceof String) {
            return true;
        }
        
        // Both are same type and comparable
        if (a.getClass().equals(b.getClass()) && a instanceof Comparable) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Compare two Number objects.
     * 
     * @param a First number
     * @param b Second number
     * @return negative if a < b, zero if a == b, positive if a > b
     */
    @SuppressWarnings("unchecked")
    private int compareNumbers(Number a, Number b) {
        // Convert both to double for comparison
        return Double.compare(a.doubleValue(), b.doubleValue());
    }
    
    /**
     * Wrap a value in the appropriate AviatorObject type.
     * 
     * @param value The value to wrap
     * @param hasDouble Whether the collection contained any double values
     * @return AviatorObject wrapping the value
     */
    private AviatorObject wrapValue(Object value, boolean hasDouble) {
        if (value instanceof String) {
            return new AviatorString((String) value);
        } else if (value instanceof Number) {
            Number num = (Number) value;
            // Return Long if all values were integers, otherwise return Double
            if (!hasDouble && value instanceof Integer || value instanceof Long || 
                value instanceof Short || value instanceof Byte) {
                return AviatorLong.valueOf(num.longValue());
            } else {
                return AviatorDouble.valueOf(num.doubleValue());
            }
        } else {
            // For other comparable types, wrap as JavaType
            return new AviatorJavaType("maxValue") {
                @Override
                public Object getValue(Map<String, Object> environment) {
                    return value;
                }
            };
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

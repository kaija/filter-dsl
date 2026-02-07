package com.filter.dsl.functions.filtering;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.filter.dsl.models.TimeRange;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorRuntimeJavaType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * WHERE function - Filter a collection based on a boolean condition.
 * 
 * Usage: WHERE(collection, "condition_expression")
 * 
 * Filters the provided collection to only include items where the condition
 * evaluates to true. The condition is evaluated for each item with that item
 * set as the current context.
 * 
 * NOTE: Due to AviatorScript's eager evaluation, the condition must be passed as a STRING
 * expression that will be compiled and evaluated lazily for each item.
 * 
 * This is a more general version of IF that works on any collection, not just
 * the events from userData. It can be used to filter results from other functions.
 * 
 * Integrates with time range context (FROM/TO) when filtering event collections.
 * 
 * Examples:
 * - WHERE(events, "EQ(EVENT(\"event_name\"), \"purchase\")") -> filter events by name
 * - WHERE(IF("..."), "EQ(EVENT(\"event_type\"), \"action\")") -> chain filters
 * 
 * Returns: A filtered collection
 */
public class WhereFunction extends DSLFunction {

    @Override
    public String getName() {
        return "WHERE";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("WHERE")
            .minArgs(2)
            .maxArgs(2)
            .argumentType(0, ArgumentType.COLLECTION)
            .argumentType(1, ArgumentType.STRING)
            .returnType(ReturnType.COLLECTION)
            .description("Filters a collection based on a boolean condition expression (passed as string)")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 2);
        
        // Get the collection to filter
        Object collectionObj = getValue(args[0], env);
        
        if (collectionObj == null) {
            return AviatorRuntimeJavaType.valueOf(new ArrayList<>());
        }
        
        // Convert to collection
        Collection<?> collection;
        if (collectionObj instanceof Collection) {
            collection = (Collection<?>) collectionObj;
        } else if (collectionObj.getClass().isArray()) {
            // Convert array to list
            collection = arrayToList(collectionObj);
        } else {
            throw new com.filter.dsl.functions.TypeMismatchException(
                "WHERE expects a collection or array as first argument, got " + 
                collectionObj.getClass().getSimpleName()
            );
        }
        
        if (collection.isEmpty()) {
            return AviatorRuntimeJavaType.valueOf(new ArrayList<>());
        }
        
        // Get the condition expression as a string
        Object conditionExprObj = getValue(args[1], env);
        if (!(conditionExprObj instanceof String)) {
            throw new com.filter.dsl.functions.TypeMismatchException(
                "WHERE expects a string expression as second argument, got " + 
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
            throw new RuntimeException("Failed to compile WHERE condition expression: " + conditionExpr, e);
        }
        
        // Get time range from context (if set by FROM/TO functions)
        TimeRange timeRange = getTimeRange(env);
        
        // Filter collection based on condition
        List<Object> filteredItems = new ArrayList<>();
        
        for (Object item : collection) {
            // Apply time range filter if item is an Event
            if (timeRange != null && item instanceof com.filter.dsl.models.Event) {
                com.filter.dsl.models.Event event = (com.filter.dsl.models.Event) item;
                Instant eventTime = parseTimestamp(event.getTimestamp());
                if (eventTime != null && !timeRange.contains(eventTime)) {
                    continue; // Skip events outside time range
                }
            }
            
            // Create item-specific context
            Map<String, Object> itemEnv = new java.util.HashMap<>(env);
            
            // If item is an Event, set it as currentEvent
            if (item instanceof com.filter.dsl.models.Event) {
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
                // This allows graceful handling of missing fields, etc.
            }
        }
        
        return AviatorRuntimeJavaType.valueOf(filteredItems);
    }
    
    /**
     * Convert an array to a list.
     * 
     * @param array The array object
     * @return A list containing the array elements
     */
    private List<Object> arrayToList(Object array) {
        List<Object> list = new ArrayList<>();
        int length = java.lang.reflect.Array.getLength(array);
        for (int i = 0; i < length; i++) {
            list.add(java.lang.reflect.Array.get(array, i));
        }
        return list;
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
    
    // Override the two-argument call method for AviatorScript compatibility
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        return call(env, new AviatorObject[]{arg1, arg2});
    }
}

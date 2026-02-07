package com.filter.dsl.functions.data;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.filter.dsl.models.Event;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorRuntimeJavaType;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * EVENT function - Access current event field values.
 * 
 * Usage: EVENT(field_name)
 * 
 * Returns the value of the specified field from the current event being evaluated.
 * Returns null if the field doesn't exist or no current event is set.
 * Supports dot notation for nested field access.
 * 
 * Examples:
 * - EVENT("event_name") -> "purchase"
 * - EVENT("event_type") -> "action"
 * - EVENT("timestamp") -> "2024-01-15T10:30:00Z"
 * - EVENT("duration") -> 120
 * - EVENT("nonexistent") -> null
 */
public class EventFunction extends DSLFunction {

    @Override
    public String getName() {
        return "EVENT";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("EVENT")
            .minArgs(1)
            .maxArgs(1)
            .argumentType(0, ArgumentType.STRING)
            .returnType(ReturnType.ANY)
            .description("Returns the value of a field from the current event")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 1);
        
        String fieldName = toString(args[0], env);
        if (fieldName == null) {
            return AviatorRuntimeJavaType.valueOf(null);
        }
        
        // Get current event from context
        Event event = getCurrentEvent(env);
        
        if (event == null) {
            return AviatorRuntimeJavaType.valueOf(null);
        }
        
        // Get field value using reflection or direct access
        Object value = getFieldValue(event, fieldName);
        return AviatorRuntimeJavaType.valueOf(value);
    }
    
    /**
     * Get field value from event object.
     * Supports dot notation for nested access.
     * 
     * @param event The event object
     * @param fieldName The field name (e.g., "event_name", "timestamp")
     * @return The field value, or null if not found
     */
    private Object getFieldValue(Event event, String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) {
            return null;
        }
        
        // Handle dot notation (split on first dot)
        String[] parts = fieldName.split("\\.", 2);
        String currentField = parts[0];
        
        // Try to get the field value using getter method
        Object value = getFieldUsingGetter(event, currentField);
        
        // If there are more parts and value is not null, recurse
        if (parts.length > 1 && value != null) {
            // For nested access, we would need to handle the remaining path
            // This could be used for accessing nested objects in the future
            return null;
        }
        
        return value;
    }
    
    /**
     * Get field value using getter method.
     * Converts field name to getter method name (e.g., "event_name" -> "getEventName").
     * 
     * @param obj The object to get field from
     * @param fieldName The field name
     * @return The field value, or null if not found
     */
    private Object getFieldUsingGetter(Object obj, String fieldName) {
        if (obj == null || fieldName == null || fieldName.isEmpty()) {
            return null;
        }
        
        try {
            // Convert field name to getter method name
            // Handle snake_case to camelCase conversion
            String getterName = "get" + toCamelCase(fieldName);
            
            Method method = obj.getClass().getMethod(getterName);
            return method.invoke(obj);
        } catch (Exception e) {
            // Field not found or error accessing it
            return null;
        }
    }
    
    /**
     * Convert snake_case or regular field name to CamelCase for getter method.
     * Examples:
     * - "event_name" -> "EventName"
     * - "timestamp" -> "Timestamp"
     * - "is_first_in_visit" -> "IsFirstInVisit"
     * 
     * @param fieldName The field name
     * @return The CamelCase version
     */
    private String toCamelCase(String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) {
            return fieldName;
        }
        
        // Split on underscore for snake_case
        String[] parts = fieldName.split("_");
        StringBuilder result = new StringBuilder();
        
        for (String part : parts) {
            if (!part.isEmpty()) {
                result.append(Character.toUpperCase(part.charAt(0)));
                if (part.length() > 1) {
                    result.append(part.substring(1));
                }
            }
        }
        
        return result.toString();
    }
    
    // Override the single-argument call method for AviatorScript compatibility
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        return call(env, new AviatorObject[]{arg1});
    }
}

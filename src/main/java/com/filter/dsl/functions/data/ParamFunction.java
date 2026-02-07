package com.filter.dsl.functions.data;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.filter.dsl.models.Event;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorRuntimeJavaType;

import java.util.Map;

/**
 * PARAM function - Access event parameter values.
 * 
 * Usage: PARAM(parameter_name)
 * 
 * Returns the value of the specified parameter from the current event's parameters map.
 * Returns null if the parameter doesn't exist or no current event is set.
 * Supports dot notation for nested parameter access.
 * 
 * Examples:
 * - PARAM("amount") -> 99.99
 * - PARAM("product_id") -> "SKU-12345"
 * - PARAM("utm_source") -> "google"
 * - PARAM("nonexistent") -> null
 */
public class ParamFunction extends DSLFunction {

    @Override
    public String getName() {
        return "PARAM";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("PARAM")
            .minArgs(1)
            .maxArgs(1)
            .argumentType(0, ArgumentType.STRING)
            .returnType(ReturnType.ANY)
            .description("Returns the value of a parameter from the current event")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 1);
        
        String paramName = toString(args[0], env);
        if (paramName == null) {
            return AviatorRuntimeJavaType.valueOf(null);
        }
        
        // Get current event from context
        Event event = getCurrentEvent(env);
        if (event == null) {
            return AviatorRuntimeJavaType.valueOf(null);
        }
        
        // Get parameters map
        Map<String, Object> parameters = event.getParameters();
        if (parameters == null) {
            return AviatorRuntimeJavaType.valueOf(null);
        }
        
        // Get parameter value, supporting dot notation for nested access
        Object value = getParameterValue(parameters, paramName);
        return AviatorRuntimeJavaType.valueOf(value);
    }
    
    /**
     * Get parameter value from parameters map.
     * Supports dot notation for nested map/object access.
     * 
     * @param parameters The parameters map
     * @param paramName The parameter name (e.g., "amount", "user.id")
     * @return The parameter value, or null if not found
     */
    private Object getParameterValue(Map<String, Object> parameters, String paramName) {
        if (paramName == null || paramName.isEmpty()) {
            return null;
        }
        
        // Handle dot notation for nested access
        String[] parts = paramName.split("\\.", 2);
        String currentKey = parts[0];
        
        // Get value from map
        Object value = parameters.get(currentKey);
        
        // If there are more parts (nested access requested)
        if (parts.length > 1) {
            // Only recurse if value is a map
            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> nestedMap = (Map<String, Object>) value;
                return getParameterValue(nestedMap, parts[1]);
            } else {
                // Can't access nested property on non-map value
                return null;
            }
        }
        
        return value;
    }
    
    // Override the single-argument call method for AviatorScript compatibility
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        return call(env, new AviatorObject[]{arg1});
    }
}

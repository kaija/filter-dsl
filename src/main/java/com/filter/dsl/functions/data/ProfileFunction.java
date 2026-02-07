package com.filter.dsl.functions.data;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.filter.dsl.models.Profile;
import com.filter.dsl.models.UserData;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorRuntimeJavaType;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * PROFILE function - Access user profile field values.
 * 
 * Usage: PROFILE(field_name)
 * 
 * Returns the value of the specified field from the user profile.
 * Returns null if the field doesn't exist or the profile is not available.
 * Supports dot notation for nested field access.
 * 
 * Examples:
 * - PROFILE("country") -> "US"
 * - PROFILE("city") -> "New York"
 * - PROFILE("os") -> "Windows"
 * - PROFILE("nonexistent") -> null
 */
public class ProfileFunction extends DSLFunction {

    @Override
    public String getName() {
        return "PROFILE";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("PROFILE")
            .minArgs(1)
            .maxArgs(1)
            .argumentType(0, ArgumentType.STRING)
            .returnType(ReturnType.ANY)
            .description("Returns the value of a field from the user profile")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 1);
        
        String fieldName = toString(args[0], env);
        if (fieldName == null) {
            return AviatorRuntimeJavaType.valueOf(null);
        }
        
        // Get user data from context
        Object userDataObj = getUserData(env);
        if (!(userDataObj instanceof UserData)) {
            return AviatorRuntimeJavaType.valueOf(null);
        }
        
        UserData userData = (UserData) userDataObj;
        Profile profile = userData.getProfile();
        
        if (profile == null) {
            return AviatorRuntimeJavaType.valueOf(null);
        }
        
        // Get field value using reflection or direct access
        Object value = getFieldValue(profile, fieldName);
        return AviatorRuntimeJavaType.valueOf(value);
    }
    
    /**
     * Get field value from profile object.
     * Supports dot notation for nested access (though Profile is flat).
     * 
     * @param profile The profile object
     * @param fieldName The field name (e.g., "country", "city")
     * @return The field value, or null if not found
     */
    private Object getFieldValue(Profile profile, String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) {
            return null;
        }
        
        // Handle dot notation (split on first dot)
        String[] parts = fieldName.split("\\.", 2);
        String currentField = parts[0];
        
        // Try to get the field value using getter method
        Object value = getFieldUsingGetter(profile, currentField);
        
        // If there are more parts and value is not null, recurse
        if (parts.length > 1 && value != null) {
            // For nested access, we would need to handle the remaining path
            // For now, Profile is flat, so this won't be used
            return null;
        }
        
        return value;
    }
    
    /**
     * Get field value using getter method.
     * Converts field name to getter method name (e.g., "country" -> "getCountry").
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
     * - "country" -> "Country"
     * - "user_id" -> "UserId"
     * - "firstName" -> "FirstName"
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

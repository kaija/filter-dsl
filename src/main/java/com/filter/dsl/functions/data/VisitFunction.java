package com.filter.dsl.functions.data;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.filter.dsl.models.Visit;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorRuntimeJavaType;

import java.util.Map;

/**
 * VISIT function - Access visit (session) properties.
 * 
 * Usage: VISIT("property_name")
 * 
 * Visit properties include session-specific information:
 * - uuid: Visit unique identifier
 * - timestamp: Visit start timestamp
 * - landing_page: First page visited in session
 * - referrer_type: Type of referrer (direct, search, social, etc.)
 * - referrer_url: Full referrer URL
 * - referrer_query: Query parameters from referrer
 * - duration: Visit duration in seconds
 * - actions: Number of actions in visit
 * - is_first_visit: Whether this is the user's first visit
 * - os: Operating system (e.g., "Windows", "macOS", "iOS")
 * - browser: Browser name (e.g., "Chrome", "Safari", "Firefox")
 * - device: Device type (e.g., "Desktop", "Mobile", "Tablet")
 * - screen: Screen resolution (e.g., "1920x1080")
 * 
 * Examples:
 * - VISIT("os") -> "Windows"
 * - VISIT("browser") -> "Chrome"
 * - VISIT("device") -> "Desktop"
 * - VISIT("landing_page") -> "/home"
 * - VISIT("referrer_type") -> "search"
 * - VISIT("is_first_visit") -> true
 * 
 * Note: Device attributes (os, browser, device, screen) are session-specific
 * because users may switch devices between sessions.
 */
public class VisitFunction extends DSLFunction {

    @Override
    public String getName() {
        return "VISIT";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("VISIT")
            .minArgs(1)
            .maxArgs(1)
            .argumentType(0, ArgumentType.STRING)
            .returnType(ReturnType.ANY)
            .description("Access visit (session) properties including device attributes")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 1);
        
        String propertyName = toString(args[0], env);
        
        if (propertyName == null) {
            return AviatorRuntimeJavaType.valueOf(null);
        }
        
        // Get current visit from context
        Object currentVisit = env.get("currentVisit");
        
        // If no current visit, try to get from userData
        if (currentVisit == null) {
            Object userData = getUserData(env);
            if (userData != null) {
                // Get the first visit (most recent) from visits map
                try {
                    if (userData instanceof com.filter.dsl.models.UserData) {
                        Map<String, Visit> visits = ((com.filter.dsl.models.UserData) userData).getVisits();
                        if (visits != null && !visits.isEmpty()) {
                            // Get first visit (they're typically ordered by timestamp)
                            currentVisit = visits.values().iterator().next();
                        }
                    } else if (userData instanceof Map) {
                        Object visitsObj = ((Map<?, ?>) userData).get("visits");
                        if (visitsObj instanceof Map) {
                            Map<?, ?> visits = (Map<?, ?>) visitsObj;
                            if (!visits.isEmpty()) {
                                currentVisit = visits.values().iterator().next();
                            }
                        }
                    }
                } catch (Exception e) {
                    // If we can't get visit, return null
                    return AviatorRuntimeJavaType.valueOf(null);
                }
            }
        }
        
        if (currentVisit == null) {
            return AviatorRuntimeJavaType.valueOf(null);
        }
        
        // Extract property from visit
        Object value = null;
        
        if (currentVisit instanceof Visit) {
            Visit visit = (Visit) currentVisit;
            value = extractVisitProperty(visit, propertyName);
        } else if (currentVisit instanceof Map) {
            // Handle visit as map
            value = ((Map<?, ?>) currentVisit).get(propertyName);
        }
        
        return AviatorRuntimeJavaType.valueOf(value);
    }
    
    /**
     * Extract a property from a Visit object.
     * 
     * @param visit The visit object
     * @param propertyName The property name
     * @return The property value, or null if not found
     */
    private Object extractVisitProperty(Visit visit, String propertyName) {
        switch (propertyName.toLowerCase()) {
            case "uuid":
                return visit.getUuid();
            case "timestamp":
                return visit.getTimestamp();
            case "landing_page":
            case "landingpage":
                return visit.getLandingPage();
            case "referrer_type":
            case "referrertype":
                return visit.getReferrerType();
            case "referrer_url":
            case "referrerurl":
                return visit.getReferrerUrl();
            case "referrer_query":
            case "referrerquery":
                return visit.getReferrerQuery();
            case "duration":
                return visit.getDuration();
            case "actions":
                return visit.getActions();
            case "is_first_visit":
            case "isfirstvisit":
                return visit.getIsFirstVisit();
            // Device attributes (session-specific)
            case "os":
                return visit.getOs();
            case "browser":
                return visit.getBrowser();
            case "device":
                return visit.getDevice();
            case "screen":
                return visit.getScreen();
            default:
                return null;
        }
    }
    
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        return call(env, new AviatorObject[]{arg1});
    }
}

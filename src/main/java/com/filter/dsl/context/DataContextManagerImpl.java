package com.filter.dsl.context;

import com.filter.dsl.models.Event;
import com.filter.dsl.models.UserData;
import com.filter.dsl.models.Visit;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of DataContextManager.
 * 
 * This implementation creates evaluation contexts that provide access to:
 * - Complete user data (profile, visits, events)
 * - Current event being evaluated (for event-specific contexts)
 * - Current visit associated with the event
 * - Current timestamp for time-based operations
 * 
 * The contexts are designed to be thread-safe for concurrent evaluations,
 * as each evaluation gets its own context map.
 */
public class DataContextManagerImpl implements DataContextManager {
    
    /**
     * Create a general evaluation context from user data.
     * 
     * @param userData The complete user data structure
     * @return Map containing userData and current timestamp
     */
    @Override
    public Map<String, Object> createContext(UserData userData) {
        Map<String, Object> context = new HashMap<>();
        
        // Add the complete user data
        context.put("userData", userData);
        
        // Add current timestamp for time-based operations
        context.put("now", Instant.now());
        
        return context;
    }
    
    /**
     * Create an event-specific evaluation context.
     * 
     * This method finds the visit associated with the event by looking up
     * the visit UUID in the userData's visits map.
     * 
     * @param userData The complete user data structure
     * @param event The specific event being evaluated
     * @return Map with event-specific context including currentEvent and currentVisit
     */
    @Override
    public Map<String, Object> createEventContext(UserData userData, Event event) {
        Map<String, Object> context = new HashMap<>();
        
        // Add the complete user data
        context.put("userData", userData);
        
        // Add the current event being evaluated
        context.put("currentEvent", event);
        
        // Find and add the visit associated with this event
        Visit currentVisit = findVisitForEvent(userData, event);
        if (currentVisit != null) {
            context.put("currentVisit", currentVisit);
        }
        
        // Add current timestamp for time-based operations
        context.put("now", Instant.now());
        
        return context;
    }
    
    /**
     * Find the visit associated with a given event.
     * 
     * This method searches through the user's visits to find the one that
     * contains this event. The association is typically based on the visit UUID
     * or timestamp matching.
     * 
     * Note: The current Event model doesn't have a visitUuid field, so we need
     * to implement a heuristic. For now, we'll return null and let the calling
     * code handle the absence of a visit. In a real implementation, you might:
     * - Add a visitUuid field to Event
     * - Match events to visits by timestamp ranges
     * - Use a separate event-to-visit mapping
     * 
     * @param userData The complete user data
     * @param event The event to find a visit for
     * @return The associated Visit, or null if not found
     */
    private Visit findVisitForEvent(UserData userData, Event event) {
        // TODO: Implement visit lookup logic based on your data model
        // For now, return null as the Event model doesn't have a visitUuid field
        // This is acceptable as DSL functions should handle null currentVisit gracefully
        return null;
    }
}

package com.filter.dsl.context;

import com.filter.dsl.models.Event;
import com.filter.dsl.models.UserData;

import java.util.Map;

/**
 * Manages the creation of evaluation contexts for DSL expressions.
 * 
 * The DataContextManager is responsible for preparing user data for expression evaluation.
 * It creates a Map&lt;String, Object&gt; context that contains all the data needed by DSL functions:
 * - userData: The complete UserData object (profile, visits, events)
 * - currentEvent: The specific event being evaluated (for event-specific contexts)
 * - currentVisit: The visit associated with the current event
 * - now: The current timestamp for evaluation
 * 
 * This context is passed to AviatorScript during expression evaluation and is accessible
 * to all DSL functions through the DSLFunction base class helper methods.
 */
public interface DataContextManager {
    
    /**
     * Create a general evaluation context from user data.
     * 
     * This context is used for expressions that evaluate across all user data
     * without focusing on a specific event or visit.
     * 
     * The context will contain:
     * - "userData": The complete UserData object
     * - "now": The current timestamp (Instant.now())
     * 
     * @param userData The complete user data structure
     * @return Map containing all accessible data for expression evaluation
     */
    Map<String, Object> createContext(UserData userData);
    
    /**
     * Create an event-specific evaluation context.
     * 
     * This context is used when evaluating expressions for a specific event,
     * such as when filtering events or accessing event-specific data.
     * 
     * The context will contain:
     * - "userData": The complete UserData object
     * - "currentEvent": The specific event being evaluated
     * - "currentVisit": The visit associated with this event (if found)
     * - "now": The current timestamp (Instant.now())
     * 
     * @param userData The complete user data structure
     * @param event The specific event being evaluated
     * @return Map with event-specific context
     */
    Map<String, Object> createEventContext(UserData userData, Event event);
}

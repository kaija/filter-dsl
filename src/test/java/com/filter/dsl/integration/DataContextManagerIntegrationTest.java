package com.filter.dsl.integration;

import com.filter.dsl.context.DataContextManager;
import com.filter.dsl.context.DataContextManagerImpl;
import com.filter.dsl.functions.data.EventFunction;
import com.filter.dsl.functions.data.ProfileFunction;
import com.filter.dsl.models.Event;
import com.filter.dsl.models.Profile;
import com.filter.dsl.models.UserData;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests verifying DataContextManager works correctly with DSL functions.
 * 
 * These tests ensure that:
 * - Context created by DataContextManager is compatible with DSL functions
 * - DSL functions can access data through the context
 * - Event-specific and general contexts work as expected
 */
class DataContextManagerIntegrationTest {
    
    private DataContextManager contextManager;
    private ProfileFunction profileFunction;
    private EventFunction eventFunction;
    private UserData testUserData;
    private Event testEvent;
    
    @BeforeEach
    void setUp() {
        contextManager = new DataContextManagerImpl();
        profileFunction = new ProfileFunction();
        eventFunction = new EventFunction();
        
        // Create test data
        Profile profile = Profile.builder()
            .uuid("user-123")
            .country("USA")
            .city("San Francisco")
            .language("en")
            .build();
        
        testEvent = Event.builder()
            .uuid("event-789")
            .eventName("purchase")
            .eventType("action")
            .timestamp("2024-01-15T10:05:00Z")
            .parameter("amount", 99.99)
            .parameter("currency", "USD")
            .build();
        
        testUserData = UserData.builder()
            .profile(profile)
            .event(testEvent)
            .build();
    }
    
    @Test
    void testProfileFunction_WithGeneralContext() {
        // Create general context
        Map<String, Object> context = contextManager.createContext(testUserData);
        
        // Call PROFILE function
        AviatorObject result = profileFunction.call(
            context,
            new AviatorString("country")
        );
        
        assertNotNull(result, "Result should not be null");
        assertEquals("USA", result.getValue(context), "Should return profile country");
    }
    
    @Test
    void testProfileFunction_WithEventContext() {
        // Create event-specific context
        Map<String, Object> context = contextManager.createEventContext(testUserData, testEvent);
        
        // PROFILE function should still work in event context
        AviatorObject result = profileFunction.call(
            context,
            new AviatorString("city")
        );
        
        assertNotNull(result, "Result should not be null");
        assertEquals("San Francisco", result.getValue(context), "Should return profile city");
    }
    
    @Test
    void testEventFunction_WithEventContext() {
        // Create event-specific context
        Map<String, Object> context = contextManager.createEventContext(testUserData, testEvent);
        
        // Call EVENT function
        AviatorObject result = eventFunction.call(
            context,
            new AviatorString("event_name")
        );
        
        assertNotNull(result, "Result should not be null");
        assertEquals("purchase", result.getValue(context), "Should return event name");
    }
    
    @Test
    void testEventFunction_WithGeneralContext_ReturnsNull() {
        // Create general context (no current event)
        Map<String, Object> context = contextManager.createContext(testUserData);
        
        // Call EVENT function - should return null since no current event
        AviatorObject result = eventFunction.call(
            context,
            new AviatorString("event_name")
        );
        
        assertNotNull(result, "Result object should not be null");
        assertNull(result.getValue(context), "Should return null when no current event");
    }
    
    @Test
    void testMultipleFunctions_InSameContext() {
        // Create event-specific context
        Map<String, Object> context = contextManager.createEventContext(testUserData, testEvent);
        
        // Call multiple functions with same context
        AviatorObject profileResult = profileFunction.call(
            context,
            new AviatorString("country")
        );
        
        AviatorObject eventResult = eventFunction.call(
            context,
            new AviatorString("event_type")
        );
        
        assertEquals("USA", profileResult.getValue(context), "Should return profile country");
        assertEquals("action", eventResult.getValue(context), "Should return event type");
    }
    
    @Test
    void testContextIsolation_BetweenEvaluations() {
        // Create two separate contexts for different events
        Event event1 = Event.builder()
            .uuid("event-1")
            .eventName("purchase")
            .build();
        
        Event event2 = Event.builder()
            .uuid("event-2")
            .eventName("view")
            .build();
        
        Map<String, Object> context1 = contextManager.createEventContext(testUserData, event1);
        Map<String, Object> context2 = contextManager.createEventContext(testUserData, event2);
        
        // Call EVENT function with both contexts
        AviatorObject result1 = eventFunction.call(
            context1,
            new AviatorString("event_name")
        );
        
        AviatorObject result2 = eventFunction.call(
            context2,
            new AviatorString("event_name")
        );
        
        assertEquals("purchase", result1.getValue(context1), "First context should have purchase event");
        assertEquals("view", result2.getValue(context2), "Second context should have view event");
    }
    
    @Test
    void testProfileFunction_WithNonExistentField() {
        Map<String, Object> context = contextManager.createContext(testUserData);
        
        AviatorObject result = profileFunction.call(
            context,
            new AviatorString("nonexistent_field")
        );
        
        assertNotNull(result, "Result object should not be null");
        assertNull(result.getValue(context), "Should return null for non-existent field");
    }
    
    @Test
    void testEventFunction_WithNonExistentField() {
        Map<String, Object> context = contextManager.createEventContext(testUserData, testEvent);
        
        AviatorObject result = eventFunction.call(
            context,
            new AviatorString("nonexistent_field")
        );
        
        assertNotNull(result, "Result object should not be null");
        assertNull(result.getValue(context), "Should return null for non-existent field");
    }
    
    @Test
    void testContextContainsAllRequiredKeys() {
        Map<String, Object> eventContext = contextManager.createEventContext(testUserData, testEvent);
        
        // Verify all keys that DSL functions might need
        assertTrue(eventContext.containsKey("userData"), "Context should have userData");
        assertTrue(eventContext.containsKey("currentEvent"), "Context should have currentEvent");
        assertTrue(eventContext.containsKey("now"), "Context should have now timestamp");
        
        // Verify the values are of correct types
        assertInstanceOf(UserData.class, eventContext.get("userData"));
        assertInstanceOf(Event.class, eventContext.get("currentEvent"));
    }
}

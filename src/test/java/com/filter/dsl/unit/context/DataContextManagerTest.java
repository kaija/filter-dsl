package com.filter.dsl.unit.context;

import com.filter.dsl.context.DataContextManager;
import com.filter.dsl.context.DataContextManagerImpl;
import com.filter.dsl.models.Event;
import com.filter.dsl.models.Profile;
import com.filter.dsl.models.UserData;
import com.filter.dsl.models.Visit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DataContextManager.
 * 
 * Tests verify that:
 * - General contexts contain userData and timestamp
 * - Event-specific contexts contain currentEvent, currentVisit, and userData
 * - Contexts are properly structured for DSL function access
 * - Null and empty data are handled gracefully
 */
class DataContextManagerTest {
    
    private DataContextManager contextManager;
    private UserData testUserData;
    private Event testEvent;
    private Visit testVisit;
    
    @BeforeEach
    void setUp() {
        contextManager = new DataContextManagerImpl();
        
        // Create test profile
        Profile profile = Profile.builder()
            .uuid("user-123")
            .country("USA")
            .city("San Francisco")
            .language("en")
            .build();
        
        // Create test visit
        testVisit = Visit.builder()
            .uuid("visit-456")
            .timestamp("2024-01-15T10:00:00Z")
            .landingPage("/home")
            .referrerType("search")
            .duration(300)
            .actions(5)
            .isFirstVisit(false)
            .build();
        
        // Create test event
        testEvent = Event.builder()
            .uuid("event-789")
            .eventName("purchase")
            .eventType("action")
            .timestamp("2024-01-15T10:05:00Z")
            .isFirstInVisit(false)
            .isLastInVisit(false)
            .isFirstEvent(false)
            .isCurrent(true)
            .parameter("amount", 99.99)
            .parameter("currency", "USD")
            .build();
        
        // Create test user data
        Map<String, Visit> visits = new HashMap<>();
        visits.put(testVisit.getUuid(), testVisit);
        
        testUserData = UserData.builder()
            .profile(profile)
            .visits(visits)
            .event(testEvent)
            .build();
    }
    
    @Test
    void testCreateContext_ContainsUserData() {
        Map<String, Object> context = contextManager.createContext(testUserData);
        
        assertNotNull(context, "Context should not be null");
        assertTrue(context.containsKey("userData"), "Context should contain userData");
        assertEquals(testUserData, context.get("userData"), "userData should match input");
    }
    
    @Test
    void testCreateContext_ContainsTimestamp() {
        Instant before = Instant.now();
        Map<String, Object> context = contextManager.createContext(testUserData);
        Instant after = Instant.now();
        
        assertTrue(context.containsKey("now"), "Context should contain 'now' timestamp");
        
        Object nowValue = context.get("now");
        assertInstanceOf(Instant.class, nowValue, "'now' should be an Instant");
        
        Instant now = (Instant) nowValue;
        assertFalse(now.isBefore(before), "Timestamp should not be before context creation");
        assertFalse(now.isAfter(after), "Timestamp should not be after context creation");
    }
    
    @Test
    void testCreateContext_DoesNotContainEventOrVisit() {
        Map<String, Object> context = contextManager.createContext(testUserData);
        
        assertFalse(context.containsKey("currentEvent"), 
            "General context should not contain currentEvent");
        assertFalse(context.containsKey("currentVisit"), 
            "General context should not contain currentVisit");
    }
    
    @Test
    void testCreateEventContext_ContainsUserData() {
        Map<String, Object> context = contextManager.createEventContext(testUserData, testEvent);
        
        assertNotNull(context, "Context should not be null");
        assertTrue(context.containsKey("userData"), "Context should contain userData");
        assertEquals(testUserData, context.get("userData"), "userData should match input");
    }
    
    @Test
    void testCreateEventContext_ContainsCurrentEvent() {
        Map<String, Object> context = contextManager.createEventContext(testUserData, testEvent);
        
        assertTrue(context.containsKey("currentEvent"), "Context should contain currentEvent");
        assertEquals(testEvent, context.get("currentEvent"), "currentEvent should match input");
    }
    
    @Test
    void testCreateEventContext_ContainsTimestamp() {
        Instant before = Instant.now();
        Map<String, Object> context = contextManager.createEventContext(testUserData, testEvent);
        Instant after = Instant.now();
        
        assertTrue(context.containsKey("now"), "Context should contain 'now' timestamp");
        
        Object nowValue = context.get("now");
        assertInstanceOf(Instant.class, nowValue, "'now' should be an Instant");
        
        Instant now = (Instant) nowValue;
        assertFalse(now.isBefore(before), "Timestamp should not be before context creation");
        assertFalse(now.isAfter(after), "Timestamp should not be after context creation");
    }
    
    @Test
    void testCreateEventContext_WithNullEvent() {
        Map<String, Object> context = contextManager.createEventContext(testUserData, null);
        
        assertNotNull(context, "Context should not be null even with null event");
        assertTrue(context.containsKey("userData"), "Context should contain userData");
        assertTrue(context.containsKey("currentEvent"), "Context should contain currentEvent key");
        assertNull(context.get("currentEvent"), "currentEvent should be null");
    }
    
    @Test
    void testCreateContext_WithEmptyUserData() {
        UserData emptyUserData = new UserData();
        
        Map<String, Object> context = contextManager.createContext(emptyUserData);
        
        assertNotNull(context, "Context should not be null");
        assertTrue(context.containsKey("userData"), "Context should contain userData");
        assertEquals(emptyUserData, context.get("userData"), "userData should match input");
        assertTrue(context.containsKey("now"), "Context should contain timestamp");
    }
    
    @Test
    void testCreateEventContext_WithEmptyUserData() {
        UserData emptyUserData = new UserData();
        
        Map<String, Object> context = contextManager.createEventContext(emptyUserData, testEvent);
        
        assertNotNull(context, "Context should not be null");
        assertTrue(context.containsKey("userData"), "Context should contain userData");
        assertTrue(context.containsKey("currentEvent"), "Context should contain currentEvent");
        assertEquals(testEvent, context.get("currentEvent"), "currentEvent should match input");
    }
    
    @Test
    void testCreateContext_ContextIsModifiable() {
        Map<String, Object> context = contextManager.createContext(testUserData);
        
        // Verify we can add additional context values
        assertDoesNotThrow(() -> context.put("customKey", "customValue"),
            "Context should be modifiable");
        
        assertEquals("customValue", context.get("customKey"),
            "Custom values should be retrievable");
    }
    
    @Test
    void testCreateEventContext_ContextIsModifiable() {
        Map<String, Object> context = contextManager.createEventContext(testUserData, testEvent);
        
        // Verify we can add additional context values (e.g., timeRange)
        assertDoesNotThrow(() -> context.put("timeRange", null),
            "Context should be modifiable");
    }
    
    @Test
    void testCreateContext_MultipleCallsProduceIndependentContexts() {
        Map<String, Object> context1 = contextManager.createContext(testUserData);
        Map<String, Object> context2 = contextManager.createContext(testUserData);
        
        // Modify first context
        context1.put("customKey", "value1");
        
        // Verify second context is not affected
        assertFalse(context2.containsKey("customKey"),
            "Contexts should be independent");
    }
    
    @Test
    void testCreateEventContext_MultipleCallsProduceIndependentContexts() {
        Map<String, Object> context1 = contextManager.createEventContext(testUserData, testEvent);
        Map<String, Object> context2 = contextManager.createEventContext(testUserData, testEvent);
        
        // Modify first context
        context1.put("customKey", "value1");
        
        // Verify second context is not affected
        assertFalse(context2.containsKey("customKey"),
            "Contexts should be independent");
    }
    
    @Test
    void testCreateEventContext_DifferentEventsProduceDifferentContexts() {
        Event event2 = Event.builder()
            .uuid("event-999")
            .eventName("view")
            .eventType("pageview")
            .timestamp("2024-01-15T10:10:00Z")
            .build();
        
        Map<String, Object> context1 = contextManager.createEventContext(testUserData, testEvent);
        Map<String, Object> context2 = contextManager.createEventContext(testUserData, event2);
        
        assertEquals(testEvent, context1.get("currentEvent"),
            "First context should have first event");
        assertEquals(event2, context2.get("currentEvent"),
            "Second context should have second event");
        assertNotEquals(context1.get("currentEvent"), context2.get("currentEvent"),
            "Contexts should have different events");
    }
    
    @Test
    void testContextStructure_MatchesDSLFunctionExpectations() {
        // This test verifies the context structure matches what DSLFunction expects
        Map<String, Object> context = contextManager.createEventContext(testUserData, testEvent);
        
        // Verify all expected keys are present
        assertTrue(context.containsKey("userData"), 
            "Context should have 'userData' key for getUserData()");
        assertTrue(context.containsKey("currentEvent"), 
            "Context should have 'currentEvent' key for getCurrentEvent()");
        assertTrue(context.containsKey("now"), 
            "Context should have 'now' key for getNow()");
        
        // Verify types match DSLFunction expectations
        assertInstanceOf(UserData.class, context.get("userData"),
            "userData should be UserData type");
        assertInstanceOf(Event.class, context.get("currentEvent"),
            "currentEvent should be Event type");
        assertInstanceOf(Instant.class, context.get("now"),
            "now should be Instant type");
    }
    
    @Test
    void testCreateContext_WithNullUserData() {
        Map<String, Object> context = contextManager.createContext(null);
        
        assertNotNull(context, "Context should not be null");
        assertTrue(context.containsKey("userData"), "Context should contain userData key");
        assertNull(context.get("userData"), "userData should be null");
        assertTrue(context.containsKey("now"), "Context should contain timestamp");
    }
}

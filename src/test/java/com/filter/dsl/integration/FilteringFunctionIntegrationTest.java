package com.filter.dsl.integration;

import com.filter.dsl.functions.FunctionRegistry;
import com.filter.dsl.functions.filtering.ByFunction;
import com.filter.dsl.functions.filtering.IfFunction;
import com.filter.dsl.functions.filtering.WhereFunction;
import com.filter.dsl.models.Event;
import com.filter.dsl.models.UserData;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import com.googlecode.aviator.runtime.type.AviatorJavaType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for filtering functions with AviatorScript.
 * 
 * Note: These tests verify that the filtering functions are properly registered
 * and can be called. The full DSL expression evaluation with nested conditions
 * requires a more sophisticated approach due to AviatorScript's eager evaluation model.
 */
class FilteringFunctionIntegrationTest {

    private AviatorEvaluatorInstance aviator;
    private FunctionRegistry registry;
    private Map<String, Object> env;
    private UserData userData;

    @BeforeEach
    void setUp() {
        // Create AviatorScript instance
        aviator = AviatorEvaluator.newInstance();
        
        // Create and register functions
        registry = new FunctionRegistry();
        registry.discoverAndRegister("com.filter.dsl.functions");
        registry.registerAll(aviator);
        
        // Create test events
        List<Event> events = new ArrayList<>();
        
        events.add(Event.builder()
            .uuid("event-1")
            .eventName("purchase")
            .eventType("action")
            .timestamp("2024-01-15T10:30:00Z")
            .duration(120)
            .parameter("amount", 99.99)
            .build());
        
        events.add(Event.builder()
            .uuid("event-2")
            .eventName("page_view")
            .eventType("view")
            .timestamp("2024-01-15T10:25:00Z")
            .duration(30)
            .build());
        
        events.add(Event.builder()
            .uuid("event-3")
            .eventName("purchase")
            .eventType("action")
            .timestamp("2024-01-14T15:00:00Z")
            .duration(90)
            .parameter("amount", 49.99)
            .build());
        
        // Create user data
        userData = UserData.builder()
            .events(events)
            .build();
        
        // Set up environment
        env = new HashMap<>();
        env.put("userData", userData);
        env.put("now", Instant.parse("2024-01-15T12:00:00Z"));
        env.put("__aviator__", aviator);
    }

    @Test
    void testIfFunctionIsRegistered() {
        assertTrue(registry.hasFunction("IF"));
        assertNotNull(registry.getFunction("IF"));
        assertNotNull(registry.getMetadata("IF"));
    }

    @Test
    void testWhereFunctionIsRegistered() {
        assertTrue(registry.hasFunction("WHERE"));
        assertNotNull(registry.getFunction("WHERE"));
        assertNotNull(registry.getMetadata("WHERE"));
    }

    @Test
    void testIfFunctionCanBeCalled() {
        // Create a simple condition that always returns true
        AviatorObject alwaysTrue = new AviatorString("true");
        
        IfFunction ifFunc = new IfFunction();
        AviatorObject result = ifFunc.call(env, alwaysTrue);
        
        assertNotNull(result);
        Object value = result.getValue(env);
        assertTrue(value instanceof List);
        
        @SuppressWarnings("unchecked")
        List<Event> filteredEvents = (List<Event>) value;
        
        // Should return all events since condition is always true
        assertEquals(3, filteredEvents.size());
    }

    @Test
    void testWhereFunctionCanBeCalled() {
        // Create a simple condition that always returns true
        AviatorObject alwaysTrue = new AviatorString("true");
        
        AviatorObject collection = new AviatorJavaType("collection") {
            @Override
            public Object getValue(Map<String, Object> environment) {
                return userData.getEvents();
            }
        };
        
        WhereFunction whereFunc = new WhereFunction();
        AviatorObject result = whereFunc.call(env, collection, alwaysTrue);
        
        assertNotNull(result);
        Object value = result.getValue(env);
        assertTrue(value instanceof List);
        
        @SuppressWarnings("unchecked")
        List<Event> filteredEvents = (List<Event>) value;
        
        // Should return all events since condition is always true
        assertEquals(3, filteredEvents.size());
    }

    @Test
    void testIfFunctionWithSelectiveCondition() {
        // Create a condition that checks event name
        AviatorObject purchaseCondition = new AviatorString("EQ(EVENT(\"event_name\"), \"purchase\")");
        
        IfFunction ifFunc = new IfFunction();
        AviatorObject result = ifFunc.call(env, purchaseCondition);
        
        assertNotNull(result);
        Object value = result.getValue(env);
        assertTrue(value instanceof List);
        
        @SuppressWarnings("unchecked")
        List<Event> filteredEvents = (List<Event>) value;
        
        // Should return only purchase events
        assertEquals(2, filteredEvents.size());
        for (Event event : filteredEvents) {
            assertEquals("purchase", event.getEventName());
        }
    }

    @Test
    void testWhereFunctionWithSelectiveCondition() {
        // Create a condition that checks event type
        AviatorObject actionCondition = new AviatorString("EQ(EVENT(\"event_type\"), \"action\")");
        
        AviatorObject collection = new AviatorJavaType("collection") {
            @Override
            public Object getValue(Map<String, Object> environment) {
                return userData.getEvents();
            }
        };
        
        WhereFunction whereFunc = new WhereFunction();
        AviatorObject result = whereFunc.call(env, collection, actionCondition);
        
        assertNotNull(result);
        Object value = result.getValue(env);
        assertTrue(value instanceof List);
        
        @SuppressWarnings("unchecked")
        List<Event> filteredEvents = (List<Event>) value;
        
        // Should return only action events
        assertEquals(2, filteredEvents.size());
        for (Event event : filteredEvents) {
            assertEquals("action", event.getEventType());
        }
    }

    @Test
    void testIfFunctionWithEmptyResult() {
        // Create a condition that never matches
        AviatorObject neverMatches = new AviatorString("false");
        
        IfFunction ifFunc = new IfFunction();
        AviatorObject result = ifFunc.call(env, neverMatches);
        
        assertNotNull(result);
        Object value = result.getValue(env);
        assertTrue(value instanceof List);
        
        @SuppressWarnings("unchecked")
        List<Event> filteredEvents = (List<Event>) value;
        
        // Should return empty list
        assertEquals(0, filteredEvents.size());
    }

    @Test
    void testWhereFunctionWithEmptyResult() {
        // Create a condition that never matches
        AviatorObject neverMatches = new AviatorString("false");
        
        AviatorObject collection = new AviatorJavaType("collection") {
            @Override
            public Object getValue(Map<String, Object> environment) {
                return userData.getEvents();
            }
        };
        
        WhereFunction whereFunc = new WhereFunction();
        AviatorObject result = whereFunc.call(env, collection, neverMatches);
        
        assertNotNull(result);
        Object value = result.getValue(env);
        assertTrue(value instanceof List);
        
        @SuppressWarnings("unchecked")
        List<Event> filteredEvents = (List<Event>) value;
        
        // Should return empty list
        assertEquals(0, filteredEvents.size());
    }

    @Test
    void testFunctionsWorkWithEmptyUserData() {
        UserData emptyUserData = UserData.builder().events(new ArrayList<>()).build();
        Map<String, Object> emptyEnv = new HashMap<>();
        emptyEnv.put("userData", emptyUserData);
        emptyEnv.put("__aviator__", aviator);
        
        AviatorObject alwaysTrue = new AviatorString("true");
        
        IfFunction ifFunc = new IfFunction();
        AviatorObject result = ifFunc.call(emptyEnv, alwaysTrue);
        
        assertNotNull(result);
        Object value = result.getValue(emptyEnv);
        assertTrue(value instanceof List);
        
        @SuppressWarnings("unchecked")
        List<Event> filteredEvents = (List<Event>) value;
        
        assertEquals(0, filteredEvents.size());
    }

    @Test
    void testByFunctionIsRegistered() {
        // Verify BY function is registered
        assertTrue(registry.hasFunction("BY"));
        assertNotNull(registry.getFunction("BY"));
    }

    @Test
    void testByFunctionCanBeCalled() {
        // Create an expression that extracts event names
        AviatorObject expression = new AviatorJavaType("expression") {
            @Override
            public Object getValue(Map<String, Object> environment) {
                Event currentEvent = (Event) environment.get("currentEvent");
                return currentEvent != null ? currentEvent.getEventName() : null;
            }
        };
        
        ByFunction byFunc = new ByFunction();
        AviatorObject result = byFunc.call(env, expression);
        
        assertNotNull(result);
        Object value = result.getValue(env);
        assertTrue(value instanceof List);
        
        @SuppressWarnings("unchecked")
        List<String> eventNames = (List<String>) value;
        
        // Should extract all event names
        assertTrue(eventNames.size() > 0);
        assertTrue(eventNames.contains("purchase"));
    }
}

package com.filter.dsl.unit.functions;

import com.filter.dsl.functions.FunctionArgumentException;
import com.filter.dsl.functions.TypeMismatchException;
import com.filter.dsl.functions.filtering.ByFunction;
import com.filter.dsl.functions.filtering.IfFunction;
import com.filter.dsl.functions.filtering.WhereFunction;
import com.filter.dsl.models.Event;
import com.filter.dsl.models.TimeRange;
import com.filter.dsl.models.TimeUnit;
import com.filter.dsl.models.UserData;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorJavaType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for filtering functions: IF, WHERE, BY
 */
class FilteringFunctionsTest {

    private Map<String, Object> env;
    private UserData userData;
    private List<Event> events;
    private AviatorEvaluatorInstance aviator;
    private com.filter.dsl.functions.FunctionRegistry registry;

    @BeforeEach
    void setUp() {
        env = new HashMap<>();
        
        // Create aviator instance and register necessary functions
        aviator = AviatorEvaluator.newInstance();
        registry = new com.filter.dsl.functions.FunctionRegistry();
        
        // Register functions needed for tests
        registry.register(new com.filter.dsl.functions.data.EventFunction());
        registry.register(new com.filter.dsl.functions.comparison.EqualsFunction());
        registry.register(new com.filter.dsl.functions.comparison.GreaterThanFunction());
        registry.registerAll(aviator);
        
        env.put("__aviator__", aviator);
        
        // Create test events
        events = new ArrayList<>();
        
        // Event 1: purchase event
        events.add(Event.builder()
            .uuid("event-1")
            .eventName("purchase")
            .eventType("action")
            .timestamp("2024-01-15T10:30:00Z")
            .duration(120)
            .parameter("amount", 99.99)
            .parameter("product_id", "SKU-001")
            .build());
        
        // Event 2: view event
        events.add(Event.builder()
            .uuid("event-2")
            .eventName("page_view")
            .eventType("view")
            .timestamp("2024-01-15T10:25:00Z")
            .duration(30)
            .parameter("page", "/products")
            .build());
        
        // Event 3: another purchase event
        events.add(Event.builder()
            .uuid("event-3")
            .eventName("purchase")
            .eventType("action")
            .timestamp("2024-01-14T15:00:00Z")
            .duration(90)
            .parameter("amount", 49.99)
            .parameter("product_id", "SKU-002")
            .build());
        
        // Event 4: click event
        events.add(Event.builder()
            .uuid("event-4")
            .eventName("button_click")
            .eventType("action")
            .timestamp("2024-01-15T10:20:00Z")
            .duration(5)
            .parameter("button_id", "add-to-cart")
            .build());
        
        // Event 5: old event (outside typical time range)
        events.add(Event.builder()
            .uuid("event-5")
            .eventName("purchase")
            .eventType("action")
            .timestamp("2023-12-01T10:00:00Z")
            .duration(100)
            .parameter("amount", 199.99)
            .parameter("product_id", "SKU-003")
            .build());
        
        // Create test user data
        userData = UserData.builder()
            .events(events)
            .build();
        
        // Set up environment
        env.put("userData", userData);
        env.put("now", Instant.parse("2024-01-15T12:00:00Z"));
    }

    // Helper method to create AviatorObject from a value
    private AviatorObject createAviatorObject(Object value) {
        return new AviatorJavaType("value") {
            @Override
            public Object getValue(Map<String, Object> environment) {
                return value;
            }
        };
    }

    // Helper method to create a condition that checks event name
    private AviatorObject createEventNameCondition(String expectedName) {
        return new AviatorJavaType("condition") {
            @Override
            public Object getValue(Map<String, Object> environment) {
                Event currentEvent = (Event) environment.get("currentEvent");
                if (currentEvent == null) {
                    return false;
                }
                return expectedName.equals(currentEvent.getEventName());
            }
        };
    }

    // Helper method to create a condition that checks event type
    private AviatorObject createEventTypeCondition(String expectedType) {
        return new AviatorJavaType("condition") {
            @Override
            public Object getValue(Map<String, Object> environment) {
                Event currentEvent = (Event) environment.get("currentEvent");
                if (currentEvent == null) {
                    return false;
                }
                return expectedType.equals(currentEvent.getEventType());
            }
        };
    }

    // Helper method to create a condition that checks duration
    private AviatorObject createDurationCondition(int minDuration) {
        return new AviatorJavaType("condition") {
            @Override
            public Object getValue(Map<String, Object> environment) {
                Event currentEvent = (Event) environment.get("currentEvent");
                if (currentEvent == null || currentEvent.getDuration() == null) {
                    return false;
                }
                return currentEvent.getDuration() > minDuration;
            }
        };
    }

    // ========== IF Function Tests ==========

    @Test
    void testIfFilterByEventName() {
        IfFunction ifFunc = new IfFunction();
        AviatorObject condition = new AviatorString("EQ(EVENT(\"event_name\"), \"purchase\")");
        
        AviatorObject result = ifFunc.call(env, condition);
        List<?> filteredEvents = (List<?>) result.getValue(env);
        
        assertNotNull(filteredEvents);
        assertEquals(3, filteredEvents.size());
        
        // Verify all filtered events are purchases
        for (Object obj : filteredEvents) {
            Event event = (Event) obj;
            assertEquals("purchase", event.getEventName());
        }
    }

    @Test
    void testIfFilterByEventType() {
        IfFunction ifFunc = new IfFunction();
        AviatorObject condition = new AviatorString("EQ(EVENT(\"event_type\"), \"action\")");
        
        AviatorObject result = ifFunc.call(env, condition);
        List<?> filteredEvents = (List<?>) result.getValue(env);
        
        assertNotNull(filteredEvents);
        assertEquals(4, filteredEvents.size());
        
        // Verify all filtered events are actions
        for (Object obj : filteredEvents) {
            Event event = (Event) obj;
            assertEquals("action", event.getEventType());
        }
    }

    @Test
    void testIfFilterByDuration() {
        IfFunction ifFunc = new IfFunction();
        AviatorObject condition = new AviatorString("GT(EVENT(\"duration\"), 50)");
        
        AviatorObject result = ifFunc.call(env, condition);
        List<?> filteredEvents = (List<?>) result.getValue(env);
        
        assertNotNull(filteredEvents);
        assertEquals(3, filteredEvents.size());
        
        // Verify all filtered events have duration > 50
        for (Object obj : filteredEvents) {
            Event event = (Event) obj;
            assertTrue(event.getDuration() > 50);
        }
    }

    @Test
    void testIfWithNoMatchingEvents() {
        IfFunction ifFunc = new IfFunction();
        AviatorObject condition = new AviatorString("EQ(EVENT(\"event_name\"), \"nonexistent\")");
        
        AviatorObject result = ifFunc.call(env, condition);
        List<?> filteredEvents = (List<?>) result.getValue(env);
        
        assertNotNull(filteredEvents);
        assertEquals(0, filteredEvents.size());
    }

    @Test
    void testIfWithEmptyEvents() {
        IfFunction ifFunc = new IfFunction();
        UserData emptyUserData = UserData.builder().events(new ArrayList<>()).build();
        Map<String, Object> emptyEnv = new HashMap<>();
        emptyEnv.put("userData", emptyUserData);
        emptyEnv.put("__aviator__", aviator);
        
        AviatorObject condition = new AviatorString("EQ(EVENT(\"event_name\"), \"purchase\")");
        AviatorObject result = ifFunc.call(emptyEnv, condition);
        List<?> filteredEvents = (List<?>) result.getValue(emptyEnv);
        
        assertNotNull(filteredEvents);
        assertEquals(0, filteredEvents.size());
    }

    @Test
    void testIfWithNullUserData() {
        IfFunction ifFunc = new IfFunction();
        Map<String, Object> nullEnv = new HashMap<>();
        nullEnv.put("__aviator__", aviator);
        
        AviatorObject condition = new AviatorString("EQ(EVENT(\"event_name\"), \"purchase\")");
        AviatorObject result = ifFunc.call(nullEnv, condition);
        List<?> filteredEvents = (List<?>) result.getValue(nullEnv);
        
        assertNotNull(filteredEvents);
        assertEquals(0, filteredEvents.size());
    }

    @Test
    void testIfWithTimeRange() {
        IfFunction ifFunc = new IfFunction();
        
        // Set time range: from 2 days ago to now
        Instant now = Instant.parse("2024-01-15T12:00:00Z");
        TimeRange timeRange = new TimeRange(2, TimeUnit.D, 0, TimeUnit.D, now);
        env.put("timeRange", timeRange);
        
        AviatorObject condition = new AviatorString("EQ(EVENT(\"event_name\"), \"purchase\")");
        AviatorObject result = ifFunc.call(env, condition);
        List<?> filteredEvents = (List<?>) result.getValue(env);
        
        assertNotNull(filteredEvents);
        // Should only include purchases from Jan 14-15, not the one from Dec 1
        assertEquals(2, filteredEvents.size());
    }

    @Test
    void testIfWithConditionThatThrowsException() {
        IfFunction ifFunc = new IfFunction();
        
        // Create a condition that will throw an exception when evaluated
        // Using an invalid expression that references a non-existent function
        AviatorObject badCondition = new AviatorString("NONEXISTENT_FUNCTION()");
        
        AviatorObject result = ifFunc.call(env, badCondition);
        List<?> filteredEvents = (List<?>) result.getValue(env);
        
        // Should return empty list when condition fails
        assertNotNull(filteredEvents);
        assertEquals(0, filteredEvents.size());
    }

    @Test
    void testIfWithWrongArgumentCount() {
        IfFunction ifFunc = new IfFunction();
        assertThrows(IllegalArgumentException.class, () -> {
            ifFunc.call(env, AviatorBoolean.TRUE, AviatorBoolean.FALSE);
        });
    }

    @Test
    void testIfWithNoArguments() {
        IfFunction ifFunc = new IfFunction();
        assertThrows(FunctionArgumentException.class, () -> {
            ifFunc.call(env, new AviatorObject[0]);
        });
    }

    @Test
    void testIfMetadata() {
        IfFunction ifFunc = new IfFunction();
        assertEquals("IF", ifFunc.getName());
        assertEquals(1, ifFunc.getFunctionMetadata().getMinArgs());
        assertEquals(1, ifFunc.getFunctionMetadata().getMaxArgs());
        assertEquals("Filters events based on a boolean condition expression (passed as string)", 
            ifFunc.getFunctionMetadata().getDescription());
    }

    // ========== WHERE Function Tests ==========

    @Test
    void testWhereFilterCollection() {
        WhereFunction whereFunc = new WhereFunction();
        
        // Create a collection to filter
        List<Event> collection = new ArrayList<>(events);
        AviatorObject collectionObj = createAviatorObject(collection);
        AviatorObject condition = new AviatorString("EQ(EVENT(\"event_name\"), \"purchase\")");
        
        AviatorObject result = whereFunc.call(env, collectionObj, condition);
        List<?> filteredEvents = (List<?>) result.getValue(env);
        
        assertNotNull(filteredEvents);
        assertEquals(3, filteredEvents.size());
        
        // Verify all filtered events are purchases
        for (Object obj : filteredEvents) {
            Event event = (Event) obj;
            assertEquals("purchase", event.getEventName());
        }
    }

    @Test
    void testWhereFilterByEventType() {
        WhereFunction whereFunc = new WhereFunction();
        
        List<Event> collection = new ArrayList<>(events);
        AviatorObject collectionObj = createAviatorObject(collection);
        AviatorObject condition = new AviatorString("EQ(EVENT(\"event_type\"), \"view\")");
        
        AviatorObject result = whereFunc.call(env, collectionObj, condition);
        List<?> filteredEvents = (List<?>) result.getValue(env);
        
        assertNotNull(filteredEvents);
        assertEquals(1, filteredEvents.size());
        assertEquals("page_view", ((Event) filteredEvents.get(0)).getEventName());
    }

    @Test
    void testWhereWithEmptyCollection() {
        WhereFunction whereFunc = new WhereFunction();
        
        List<Event> emptyCollection = new ArrayList<>();
        AviatorObject collectionObj = createAviatorObject(emptyCollection);
        AviatorObject condition = new AviatorString("EQ(EVENT(\"event_name\"), \"purchase\")");
        
        AviatorObject result = whereFunc.call(env, collectionObj, condition);
        List<?> filteredEvents = (List<?>) result.getValue(env);
        
        assertNotNull(filteredEvents);
        assertEquals(0, filteredEvents.size());
    }

    @Test
    void testWhereWithNullCollection() {
        WhereFunction whereFunc = new WhereFunction();
        
        AviatorObject collectionObj = createAviatorObject(null);
        AviatorObject condition = new AviatorString("EQ(EVENT(\"event_name\"), \"purchase\")");
        
        AviatorObject result = whereFunc.call(env, collectionObj, condition);
        List<?> filteredEvents = (List<?>) result.getValue(env);
        
        assertNotNull(filteredEvents);
        assertEquals(0, filteredEvents.size());
    }

    @Test
    void testWhereWithArray() {
        WhereFunction whereFunc = new WhereFunction();
        
        // Create an array instead of a list
        Event[] eventArray = events.toArray(new Event[0]);
        AviatorObject collectionObj = createAviatorObject(eventArray);
        AviatorObject condition = new AviatorString("EQ(EVENT(\"event_name\"), \"purchase\")");
        
        AviatorObject result = whereFunc.call(env, collectionObj, condition);
        List<?> filteredEvents = (List<?>) result.getValue(env);
        
        assertNotNull(filteredEvents);
        assertEquals(3, filteredEvents.size());
    }

    @Test
    void testWhereWithNonCollectionThrowsException() {
        WhereFunction whereFunc = new WhereFunction();
        
        AviatorObject nonCollection = createAviatorObject("not a collection");
        AviatorObject condition = AviatorBoolean.TRUE;
        
        assertThrows(TypeMismatchException.class, () -> {
            whereFunc.call(env, nonCollection, condition);
        });
    }

    @Test
    void testWhereWithTimeRange() {
        WhereFunction whereFunc = new WhereFunction();
        
        // Set time range: from 2 days ago to now
        Instant now = Instant.parse("2024-01-15T12:00:00Z");
        TimeRange timeRange = new TimeRange(2, TimeUnit.D, 0, TimeUnit.D, now);
        env.put("timeRange", timeRange);
        
        List<Event> collection = new ArrayList<>(events);
        AviatorObject collectionObj = createAviatorObject(collection);
        AviatorObject condition = new AviatorString("EQ(EVENT(\"event_name\"), \"purchase\")");
        
        AviatorObject result = whereFunc.call(env, collectionObj, condition);
        List<?> filteredEvents = (List<?>) result.getValue(env);
        
        assertNotNull(filteredEvents);
        // Should only include purchases from Jan 14-15, not the one from Dec 1
        assertEquals(2, filteredEvents.size());
    }

    @Test
    void testWhereWithConditionThatThrowsException() {
        WhereFunction whereFunc = new WhereFunction();
        
        List<Event> collection = new ArrayList<>(events);
        AviatorObject collectionObj = createAviatorObject(collection);
        
        // Create a condition that will throw an exception when evaluated
        // Using an invalid expression that references a non-existent function
        AviatorObject badCondition = new AviatorString("NONEXISTENT_FUNCTION()");
        
        AviatorObject result = whereFunc.call(env, collectionObj, badCondition);
        List<?> filteredEvents = (List<?>) result.getValue(env);
        
        // Should return empty list when condition fails for all items
        assertNotNull(filteredEvents);
        assertEquals(0, filteredEvents.size());
    }

    @Test
    void testWhereWithNonEventCollection() {
        WhereFunction whereFunc = new WhereFunction();
        
        // Create a collection of strings
        List<String> stringCollection = Arrays.asList("apple", "banana", "cherry");
        AviatorObject collectionObj = createAviatorObject(stringCollection);
        
        // Create a condition that always returns false
        AviatorObject condition = new AviatorString("false");
        
        AviatorObject result = whereFunc.call(env, collectionObj, condition);
        List<?> filteredItems = (List<?>) result.getValue(env);
        
        assertNotNull(filteredItems);
        assertEquals(0, filteredItems.size());
    }

    @Test
    void testWhereWithWrongArgumentCount() {
        WhereFunction whereFunc = new WhereFunction();
        assertThrows(IllegalArgumentException.class, () -> {
            whereFunc.call(env, createAviatorObject(events));
        });
    }

    @Test
    void testWhereWithNoArguments() {
        WhereFunction whereFunc = new WhereFunction();
        assertThrows(FunctionArgumentException.class, () -> {
            whereFunc.call(env, new AviatorObject[0]);
        });
    }

    @Test
    void testWhereMetadata() {
        WhereFunction whereFunc = new WhereFunction();
        assertEquals("WHERE", whereFunc.getName());
        assertEquals(2, whereFunc.getFunctionMetadata().getMinArgs());
        assertEquals(2, whereFunc.getFunctionMetadata().getMaxArgs());
        assertEquals("Filters a collection based on a boolean condition expression (passed as string)", 
            whereFunc.getFunctionMetadata().getDescription());
    }

    // ========== Integration Tests ==========

    @Test
    void testIfAndWhereProduceSimilarResults() {
        // IF and WHERE should produce similar results when filtering events
        IfFunction ifFunc = new IfFunction();
        WhereFunction whereFunc = new WhereFunction();
        
        AviatorObject condition = new AviatorString("EQ(EVENT(\"event_name\"), \"purchase\")");
        
        // Use IF to filter
        AviatorObject ifResult = ifFunc.call(env, condition);
        List<?> ifFiltered = (List<?>) ifResult.getValue(env);
        
        // Use WHERE to filter the same events
        AviatorObject collectionObj = createAviatorObject(events);
        AviatorObject whereResult = whereFunc.call(env, collectionObj, condition);
        List<?> whereFiltered = (List<?>) whereResult.getValue(env);
        
        // Both should return the same number of events
        assertEquals(ifFiltered.size(), whereFiltered.size());
        assertEquals(3, ifFiltered.size());
    }

    @Test
    void testChainedFiltering() {
        // Test filtering the result of a previous filter
        WhereFunction whereFunc = new WhereFunction();
        
        // First filter: get all action events
        List<Event> collection = new ArrayList<>(events);
        AviatorObject collectionObj = createAviatorObject(collection);
        AviatorObject actionCondition = new AviatorString("EQ(EVENT(\"event_type\"), \"action\")");
        
        AviatorObject firstResult = whereFunc.call(env, collectionObj, actionCondition);
        List<?> actionEvents = (List<?>) firstResult.getValue(env);
        
        assertEquals(4, actionEvents.size());
        
        // Second filter: from action events, get only purchases
        AviatorObject actionEventsObj = createAviatorObject(actionEvents);
        AviatorObject purchaseCondition = new AviatorString("EQ(EVENT(\"event_name\"), \"purchase\")");
        
        AviatorObject secondResult = whereFunc.call(env, actionEventsObj, purchaseCondition);
        List<?> purchaseActionEvents = (List<?>) secondResult.getValue(env);
        
        assertEquals(3, purchaseActionEvents.size());
        
        // Verify all are purchases
        for (Object obj : purchaseActionEvents) {
            Event event = (Event) obj;
            assertEquals("purchase", event.getEventName());
            assertEquals("action", event.getEventType());
        }
    }

    @Test
    void testFilteringWithComplexConditions() {
        // Test filtering with multiple conditions combined
        WhereFunction whereFunc = new WhereFunction();
        
        List<Event> collection = new ArrayList<>(events);
        AviatorObject collectionObj = createAviatorObject(collection);
        
        // Create a complex condition: action type AND duration > 50
        AviatorObject complexCondition = new AviatorString("EQ(EVENT(\"event_type\"), \"action\") && GT(EVENT(\"duration\"), 50)");
        
        AviatorObject result = whereFunc.call(env, collectionObj, complexCondition);
        List<?> filteredEvents = (List<?>) result.getValue(env);
        
        assertNotNull(filteredEvents);
        assertEquals(3, filteredEvents.size());
        
        // Verify all match the complex condition
        for (Object obj : filteredEvents) {
            Event event = (Event) obj;
            assertEquals("action", event.getEventType());
            assertTrue(event.getDuration() > 50);
        }
    }

    // ========== BY Function Tests ==========

    @Test
    void testByExtractEventNames() {
        ByFunction byFunc = new ByFunction();
        
        // Create an expression that extracts event names
        AviatorObject expression = new AviatorJavaType("expression") {
            @Override
            public Object getValue(Map<String, Object> environment) {
                Event currentEvent = (Event) environment.get("currentEvent");
                return currentEvent != null ? currentEvent.getEventName() : null;
            }
        };
        
        AviatorObject result = byFunc.call(env, expression);
        List<?> extractedValues = (List<?>) result.getValue(env);
        
        assertNotNull(extractedValues);
        assertEquals(5, extractedValues.size());
        
        // Verify extracted event names
        assertEquals("purchase", extractedValues.get(0));
        assertEquals("page_view", extractedValues.get(1));
        assertEquals("purchase", extractedValues.get(2));
        assertEquals("button_click", extractedValues.get(3));
        assertEquals("purchase", extractedValues.get(4));
    }

    @Test
    void testByExtractEventTypes() {
        ByFunction byFunc = new ByFunction();
        
        // Create an expression that extracts event types
        AviatorObject expression = new AviatorJavaType("expression") {
            @Override
            public Object getValue(Map<String, Object> environment) {
                Event currentEvent = (Event) environment.get("currentEvent");
                return currentEvent != null ? currentEvent.getEventType() : null;
            }
        };
        
        AviatorObject result = byFunc.call(env, expression);
        List<?> extractedValues = (List<?>) result.getValue(env);
        
        assertNotNull(extractedValues);
        assertEquals(5, extractedValues.size());
        
        // Verify extracted event types
        assertEquals("action", extractedValues.get(0));
        assertEquals("view", extractedValues.get(1));
        assertEquals("action", extractedValues.get(2));
        assertEquals("action", extractedValues.get(3));
        assertEquals("action", extractedValues.get(4));
    }

    @Test
    void testByExtractDurations() {
        ByFunction byFunc = new ByFunction();
        
        // Create an expression that extracts durations
        AviatorObject expression = new AviatorJavaType("expression") {
            @Override
            public Object getValue(Map<String, Object> environment) {
                Event currentEvent = (Event) environment.get("currentEvent");
                return currentEvent != null ? currentEvent.getDuration() : null;
            }
        };
        
        AviatorObject result = byFunc.call(env, expression);
        List<?> extractedValues = (List<?>) result.getValue(env);
        
        assertNotNull(extractedValues);
        assertEquals(5, extractedValues.size());
        
        // Verify extracted durations
        assertEquals(120, extractedValues.get(0));
        assertEquals(30, extractedValues.get(1));
        assertEquals(90, extractedValues.get(2));
        assertEquals(5, extractedValues.get(3));
        assertEquals(100, extractedValues.get(4));
    }

    @Test
    void testByWithEmptyEvents() {
        ByFunction byFunc = new ByFunction();
        UserData emptyUserData = UserData.builder().events(new ArrayList<>()).build();
        Map<String, Object> emptyEnv = new HashMap<>();
        emptyEnv.put("userData", emptyUserData);
        
        AviatorObject expression = new AviatorJavaType("expression") {
            @Override
            public Object getValue(Map<String, Object> environment) {
                Event currentEvent = (Event) environment.get("currentEvent");
                return currentEvent != null ? currentEvent.getEventName() : null;
            }
        };
        
        AviatorObject result = byFunc.call(emptyEnv, expression);
        List<?> extractedValues = (List<?>) result.getValue(emptyEnv);
        
        assertNotNull(extractedValues);
        assertEquals(0, extractedValues.size());
    }

    @Test
    void testByWithNullUserData() {
        ByFunction byFunc = new ByFunction();
        Map<String, Object> nullEnv = new HashMap<>();
        
        AviatorObject expression = new AviatorJavaType("expression") {
            @Override
            public Object getValue(Map<String, Object> environment) {
                Event currentEvent = (Event) environment.get("currentEvent");
                return currentEvent != null ? currentEvent.getEventName() : null;
            }
        };
        
        AviatorObject result = byFunc.call(nullEnv, expression);
        List<?> extractedValues = (List<?>) result.getValue(nullEnv);
        
        assertNotNull(extractedValues);
        assertEquals(0, extractedValues.size());
    }

    @Test
    void testByWithExpressionThatThrowsException() {
        ByFunction byFunc = new ByFunction();
        
        // Create an expression that throws an exception
        AviatorObject badExpression = new AviatorJavaType("expression") {
            @Override
            public Object getValue(Map<String, Object> environment) {
                throw new RuntimeException("Test exception");
            }
        };
        
        AviatorObject result = byFunc.call(env, badExpression);
        List<?> extractedValues = (List<?>) result.getValue(env);
        
        // Should return list with nulls when expression fails
        assertNotNull(extractedValues);
        assertEquals(5, extractedValues.size());
        
        // All values should be null since expression failed
        for (Object value : extractedValues) {
            assertNull(value);
        }
    }

    @Test
    void testByExtractParameterValues() {
        ByFunction byFunc = new ByFunction();
        
        // Create an expression that extracts parameter values
        AviatorObject expression = new AviatorJavaType("expression") {
            @Override
            public Object getValue(Map<String, Object> environment) {
                Event currentEvent = (Event) environment.get("currentEvent");
                if (currentEvent == null || currentEvent.getParameters() == null) {
                    return null;
                }
                return currentEvent.getParameters().get("amount");
            }
        };
        
        AviatorObject result = byFunc.call(env, expression);
        List<?> extractedValues = (List<?>) result.getValue(env);
        
        assertNotNull(extractedValues);
        assertEquals(5, extractedValues.size());
        
        // Verify extracted amounts (some events don't have amount parameter)
        assertEquals(99.99, extractedValues.get(0));
        assertNull(extractedValues.get(1)); // page_view has no amount
        assertEquals(49.99, extractedValues.get(2));
        assertNull(extractedValues.get(3)); // button_click has no amount
        assertEquals(199.99, extractedValues.get(4));
    }

    @Test
    void testByWithConstantExpression() {
        ByFunction byFunc = new ByFunction();
        
        // Create an expression that returns a constant
        AviatorObject expression = new AviatorJavaType("expression") {
            @Override
            public Object getValue(Map<String, Object> environment) {
                return "constant_value";
            }
        };
        
        AviatorObject result = byFunc.call(env, expression);
        List<?> extractedValues = (List<?>) result.getValue(env);
        
        assertNotNull(extractedValues);
        assertEquals(5, extractedValues.size());
        
        // All values should be the constant
        for (Object value : extractedValues) {
            assertEquals("constant_value", value);
        }
    }

    @Test
    void testByWithWrongArgumentCount() {
        ByFunction byFunc = new ByFunction();
        assertThrows(IllegalArgumentException.class, () -> {
            byFunc.call(env, AviatorBoolean.TRUE, AviatorBoolean.FALSE);
        });
    }

    @Test
    void testByWithNoArguments() {
        ByFunction byFunc = new ByFunction();
        assertThrows(FunctionArgumentException.class, () -> {
            byFunc.call(env, new AviatorObject[0]);
        });
    }

    @Test
    void testByMetadata() {
        ByFunction byFunc = new ByFunction();
        assertEquals("BY", byFunc.getName());
        assertEquals(1, byFunc.getFunctionMetadata().getMinArgs());
        assertEquals(1, byFunc.getFunctionMetadata().getMaxArgs());
        assertEquals("Groups collection items by field value or extracts values using an expression", 
            byFunc.getFunctionMetadata().getDescription());
    }

    // ========== Integration Tests with BY ==========

    @Test
    void testByWithUniquePattern() {
        // Simulate the pattern: UNIQUE(BY(expression))
        ByFunction byFunc = new ByFunction();
        
        // Extract event types
        AviatorObject expression = new AviatorJavaType("expression") {
            @Override
            public Object getValue(Map<String, Object> environment) {
                Event currentEvent = (Event) environment.get("currentEvent");
                return currentEvent != null ? currentEvent.getEventType() : null;
            }
        };
        
        AviatorObject result = byFunc.call(env, expression);
        List<?> extractedValues = (List<?>) result.getValue(env);
        
        // We have 4 "action" and 1 "view" event types
        assertEquals(5, extractedValues.size());
        
        // Manually apply UNIQUE logic (in real usage, UNIQUE function would be called)
        Set<Object> uniqueValues = new LinkedHashSet<>(extractedValues);
        assertEquals(2, uniqueValues.size());
        assertTrue(uniqueValues.contains("action"));
        assertTrue(uniqueValues.contains("view"));
    }

    @Test
    void testByExtractTimestamps() {
        ByFunction byFunc = new ByFunction();
        
        // Create an expression that extracts timestamps
        AviatorObject expression = new AviatorJavaType("expression") {
            @Override
            public Object getValue(Map<String, Object> environment) {
                Event currentEvent = (Event) environment.get("currentEvent");
                return currentEvent != null ? currentEvent.getTimestamp() : null;
            }
        };
        
        AviatorObject result = byFunc.call(env, expression);
        List<?> extractedValues = (List<?>) result.getValue(env);
        
        assertNotNull(extractedValues);
        assertEquals(5, extractedValues.size());
        
        // Verify all timestamps are present
        for (Object value : extractedValues) {
            assertNotNull(value);
            assertTrue(value instanceof String);
        }
    }
}

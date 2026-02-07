package com.filter.dsl.integration;

import com.filter.dsl.context.DataContextManager;
import com.filter.dsl.context.DataContextManagerImpl;
import com.filter.dsl.evaluator.DSLEvaluator;
import com.filter.dsl.evaluator.DSLEvaluatorImpl;
import com.filter.dsl.evaluator.EvaluationResult;
import com.filter.dsl.functions.FunctionRegistry;
import com.filter.dsl.models.BucketDefinition;
import com.filter.dsl.models.Event;
import com.filter.dsl.models.Profile;
import com.filter.dsl.models.UserData;
import com.filter.dsl.parser.DSLParser;
import com.filter.dsl.parser.DSLParserImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DebugIfTest {
    
    private DSLEvaluator evaluator;
    
    @BeforeEach
    void setUp() {
        FunctionRegistry registry = new FunctionRegistry();
        registry.discoverAndRegister("com.filter.dsl.functions");
        
        DSLParser parser = new DSLParserImpl(registry);
        DataContextManager contextManager = new DataContextManagerImpl();
        
        evaluator = new DSLEvaluatorImpl(parser, registry, contextManager);
    }
    
    @Test
    void testSimpleIfExpression() {
        // Create a simple user with one purchase event
        Profile profile = Profile.builder()
            .uuid("test-user")
            .country("US")
            .build();
        
        Map<String, Object> params = new HashMap<>();
        params.put("amount", 100.0);
        
        Event event = Event.builder()
            .uuid("purchase-1")
            .eventName("purchase")
            .eventType("purchase")
            .timestamp(Instant.now().toString())
            .parameters(params)
            .build();
        
        UserData userData = UserData.builder()
            .profile(profile)
            .events(Arrays.asList(event))
            .visits(new HashMap<>())
            .build();
        
        // Test the IF expression
        String expression = "COUNT(IF(\"EQ(EVENT(\\\"event_name\\\"), \\\"purchase\\\")\"))";
        
        EvaluationResult result = evaluator.evaluate(expression, userData);
        
        System.out.println("Expression: " + expression);
        System.out.println("Success: " + result.isSuccess());
        System.out.println("Value: " + result.getValue());
        System.out.println("Error: " + result.getErrorMessage());
        System.out.println("Error Type: " + result.getErrorType());
        
        assertTrue(result.isSuccess(), "Expression should evaluate successfully: " + result.getErrorMessage());
        assertEquals(1L, result.getValue(), "Should find 1 purchase event");
    }
    
    @Test
    @org.junit.jupiter.api.Disabled("BUCKET with context variables not yet implemented")
    void testBucketWithIfExpression() {
        // Create a simple user with one purchase event
        Profile profile = Profile.builder()
            .uuid("test-user")
            .country("US")
            .build();
        
        Map<String, Object> params = new HashMap<>();
        params.put("amount", 100.0);
        
        Event event = Event.builder()
            .uuid("purchase-1")
            .eventName("purchase")
            .eventType("purchase")
            .timestamp(Instant.now().toString())
            .parameters(params)
            .build();
        
        UserData userData = UserData.builder()
            .profile(profile)
            .events(Arrays.asList(event))
            .visits(new HashMap<>())
            .build();
        
        // Create bucket definition
        BucketDefinition purchaseBuckets = BucketDefinition.builder()
            .range(0.0, 2.0, "low")
            .range(2.0, 5.0, "medium")
            .range(5.0, 10.0, "high")
            .range(10.0, 100.0, "vip")
            .defaultLabel("other")
            .build();
        
        // Test the BUCKET expression with IF
        String expression = "BUCKET(" +
            "COUNT(" +
                "IF(\"EQ(EVENT(\\\"event_name\\\"), \\\"purchase\\\")\")" +
            "), " +
            "purchaseBuckets" +
        ")";
        
        // Note: This test is disabled because BUCKET with context variables is not yet implemented
        // The evaluator doesn't support passing additional context variables
        EvaluationResult result = evaluator.evaluate(expression, userData);
        
        System.out.println("Expression: " + expression);
        System.out.println("Success: " + result.isSuccess());
        System.out.println("Value: " + result.getValue());
        System.out.println("Error: " + result.getErrorMessage());
        System.out.println("Error Type: " + result.getErrorType());
        
        assertTrue(result.isSuccess(), "Expression should evaluate successfully: " + result.getErrorMessage());
        assertEquals("low", result.getValue(), "User with 1 purchase should be in 'low' segment");
    }
    
    @Test
    void testWeekdayFilter() {
        // Create a user with a weekend event (Saturday)
        Profile profile = Profile.builder()
            .uuid("test-user")
            .country("US")
            .build();
        
        // Find the next Saturday in UTC (to match WEEKDAY function's UTC timezone)
        Instant now = Instant.now();
        int currentDayValue = now.atZone(java.time.ZoneId.of("UTC")).getDayOfWeek().getValue();
        int daysUntilSaturday = (6 - currentDayValue + 7) % 7;
        if (daysUntilSaturday == 0) daysUntilSaturday = 7; // If today is Saturday, use next Saturday
        Instant saturday = now.plus(daysUntilSaturday, ChronoUnit.DAYS);
        
        Event event = Event.builder()
            .uuid("weekend-event-1")
            .eventName("action")
            .eventType("action")
            .timestamp(saturday.toString())
            .parameters(new HashMap<>())
            .build();
        
        UserData userData = UserData.builder()
            .profile(profile)
            .events(Arrays.asList(event))
            .visits(new HashMap<>())
            .build();
        
        // Test the weekday filter expression
        String expression = "GT(" +
            "COUNT(" +
                "IF(" +
                    "\"OR(" +
                        "EQ(WEEKDAY(EVENT(\\\"timestamp\\\")), 6), " +
                        "EQ(WEEKDAY(EVENT(\\\"timestamp\\\")), 7)" +
                    ")\"" +
                ")" +
            "), " +
            "0" +
        ")";
        
        EvaluationResult result = evaluator.evaluate(expression, userData);
        
        System.out.println("Expression: " + expression);
        System.out.println("Success: " + result.isSuccess());
        System.out.println("Value: " + result.getValue());
        System.out.println("Error: " + result.getErrorMessage());
        System.out.println("Error Type: " + result.getErrorType());
        
        assertTrue(result.isSuccess(), "Expression should evaluate successfully: " + result.getErrorMessage());
        assertEquals(true, result.getValue(), "User with weekend events should match");
    }
}

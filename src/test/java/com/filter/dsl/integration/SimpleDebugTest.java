package com.filter.dsl.integration;

import com.filter.dsl.context.DataContextManager;
import com.filter.dsl.context.DataContextManagerImpl;
import com.filter.dsl.evaluator.DSLEvaluator;
import com.filter.dsl.evaluator.DSLEvaluatorImpl;
import com.filter.dsl.evaluator.EvaluationResult;
import com.filter.dsl.functions.FunctionRegistry;
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

class SimpleDebugTest {
    
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
    void testSimpleEventAccess() {
        UserData userData = createUserWithPurchases(3);
        
        // Test 1: Can we access event_name?
        String expr1 = "EVENT(\"event_name\")";
        EvaluationResult result1 = evaluator.evaluate(expr1, userData);
        System.out.println("EVENT(\"event_name\") = " + result1.getValue());
        System.out.println("Success: " + result1.isSuccess());
        System.out.println("Error: " + result1.getErrorMessage());
        
        // Test 2: Can we compare event_name?
        String expr2 = "EQ(EVENT(\"event_name\"), \"purchase\")";
        EvaluationResult result2 = evaluator.evaluate(expr2, userData);
        System.out.println("\nEQ(EVENT(\"event_name\"), \"purchase\") = " + result2.getValue());
        System.out.println("Success: " + result2.isSuccess());
        System.out.println("Error: " + result2.getErrorMessage());
        
        // Test 3: Can we filter with IF?
        String expr3 = "IF(EQ(EVENT(\"event_name\"), \"purchase\"))";
        EvaluationResult result3 = evaluator.evaluate(expr3, userData);
        System.out.println("\nIF(EQ(EVENT(\"event_name\"), \"purchase\")) = " + result3.getValue());
        System.out.println("Type: " + (result3.getValue() != null ? result3.getValue().getClass() : "null"));
        if (result3.getValue() instanceof List) {
            System.out.println("Size: " + ((List<?>)result3.getValue()).size());
        }
        System.out.println("Success: " + result3.isSuccess());
        System.out.println("Error: " + result3.getErrorMessage());
        
        // Test 4: Can we count the filtered events?
        String expr4 = "COUNT(IF(EQ(EVENT(\"event_name\"), \"purchase\")))";
        EvaluationResult result4 = evaluator.evaluate(expr4, userData);
        System.out.println("\nCOUNT(IF(EQ(EVENT(\"event_name\"), \"purchase\"))) = " + result4.getValue());
        System.out.println("Success: " + result4.isSuccess());
        System.out.println("Error: " + result4.getErrorMessage());
    }
    
    private UserData createUserWithPurchases(int count) {
        Profile profile = Profile.builder()
            .uuid("user-test")
            .country("US")
            .build();
        
        List<Event> events = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Map<String, Object> params = new HashMap<>();
            params.put("amount", 100.0);
            
            Event event = Event.builder()
                .uuid("purchase-" + i)
                .eventName("purchase")
                .eventType("purchase")
                .timestamp(Instant.now().minus(i, ChronoUnit.DAYS).toString())
                .parameters(params)
                .build();
            events.add(event);
            
            System.out.println("Created event: " + event.getEventName() + ", type: " + event.getEventType());
        }
        
        return UserData.builder()
            .profile(profile)
            .events(events)
            .visits(new HashMap<>())
            .build();
    }
}

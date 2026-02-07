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

class DebugUseCaseTest {
    
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
    void debugSimpleCount() {
        // Create user with 7 purchases
        UserData userData = createUserWithPurchases(7);
        
        // Print the actual event data
        System.out.println("=== Event Data ===");
        for (Event e : userData.getEvents()) {
            System.out.println("Event: " + e.getEventName() + ", Type: " + e.getEventType());
        }
        
        // Test with IF function instead of WHERE
        System.out.println("\n=== Test with IF ===");
        String expr1 = "IF(EQ(EVENT(\"event_name\"), \"purchase\"))";
        EvaluationResult result1 = evaluator.evaluate(expr1, userData);
        System.out.println("IF(EQ(EVENT(\"event_name\"), \"purchase\")) count = " + 
            (result1.getValue() instanceof List ? ((List<?>)result1.getValue()).size() : "not a list"));
        System.out.println("Success: " + result1.isSuccess());
        
        // Test COUNT with IF
        System.out.println("\n=== Test COUNT(IF(...)) ===");
        String expr2 = "COUNT(IF(EQ(EVENT(\"event_name\"), \"purchase\")))";
        EvaluationResult result2 = evaluator.evaluate(expr2, userData);
        System.out.println("COUNT(IF(...)) = " + result2.getValue());
        System.out.println("Success: " + result2.isSuccess());
        
        // Test full expression with IF
        System.out.println("\n=== Test GT(COUNT(IF(...)), 5) ===");
        String expr3 = "GT(COUNT(IF(EQ(EVENT(\"event_name\"), \"purchase\"))), 5)";
        EvaluationResult result3 = evaluator.evaluate(expr3, userData);
        System.out.println("GT(COUNT(IF(...)), 5) = " + result3.getValue());
        System.out.println("Success: " + result3.isSuccess());
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
        }
        
        return UserData.builder()
            .profile(profile)
            .events(events)
            .visits(new HashMap<>())
            .build();
    }
}

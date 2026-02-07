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

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DebugWeekdayTest {
    
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
    void testWeekdayFunction() {
        // Create a user with a Saturday event
        Profile profile = Profile.builder()
            .uuid("test-user")
            .country("US")
            .build();
        
        // Create a specific Saturday timestamp in UTC (to match WEEKDAY function's UTC timezone)
        Instant now = Instant.now();
        DayOfWeek currentDay = now.atZone(ZoneId.of("UTC")).getDayOfWeek();
        System.out.println("Current day: " + currentDay + " (value=" + currentDay.getValue() + ")");
        
        // Calculate days until Saturday (6)
        int daysUntilSaturday = (DayOfWeek.SATURDAY.getValue() - currentDay.getValue() + 7) % 7;
        if (daysUntilSaturday == 0) daysUntilSaturday = 7; // If today is Saturday, use next Saturday
        
        Instant saturday = now.plus(daysUntilSaturday, ChronoUnit.DAYS);
        DayOfWeek saturdayDay = saturday.atZone(ZoneId.of("UTC")).getDayOfWeek();
        System.out.println("Saturday timestamp: " + saturday);
        System.out.println("Saturday day: " + saturdayDay + " (value=" + saturdayDay.getValue() + ")");
        
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
        
        // Test WEEKDAY function directly
        String expression1 = "WEEKDAY(\"" + saturday.toString() + "\")";
        EvaluationResult result1 = evaluator.evaluate(expression1, userData);
        System.out.println("WEEKDAY result: " + result1.getValue());
        
        // Test with EVENT function
        String expression2 = "COUNT(IF(\"EQ(WEEKDAY(EVENT(\\\"timestamp\\\")), " + saturdayDay.getValue() + ")\"))";
        EvaluationResult result2 = evaluator.evaluate(expression2, userData);
        System.out.println("IF with WEEKDAY result: " + result2.getValue());
        
        assertTrue(result2.isSuccess(), "Expression should evaluate successfully: " + result2.getErrorMessage());
        assertEquals(1L, result2.getValue(), "Should find 1 weekend event");
    }
}

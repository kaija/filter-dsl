package com.filter.dsl.unit.functions;

import com.filter.dsl.functions.FunctionRegistry;
import com.filter.dsl.functions.datetime.*;
import com.filter.dsl.models.Event;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for registering date/time functions with FunctionRegistry and AviatorScript.
 */
class DateTimeFunctionsRegistrationTest {

    private FunctionRegistry registry;
    private AviatorEvaluatorInstance aviator;

    @BeforeEach
    void setUp() {
        registry = new FunctionRegistry();
        aviator = AviatorEvaluator.newInstance();
    }

    @Test
    void testRegisterDateTimeFunctions() {
        // Register all date/time functions
        registry.register(new ActionTimeFunction());
        registry.register(new DateFormatFunction());
        registry.register(new DateDiffFunction());
        registry.register(new FromFunction());
        registry.register(new ToFunction());
        registry.register(new NowFunction());
        registry.register(new WeekdayFunction());
        registry.register(new DayOfMonthFunction());
        registry.register(new MonthFunction());
        registry.register(new YearFunction());
        registry.register(new InRecentDaysFunction());
        registry.register(new IsRecurringFunction());

        // Verify registration
        assertTrue(registry.hasFunction("ACTION_TIME"));
        assertTrue(registry.hasFunction("DATE_FORMAT"));
        assertTrue(registry.hasFunction("DATE_DIFF"));
        assertTrue(registry.hasFunction("FROM"));
        assertTrue(registry.hasFunction("TO"));
        assertTrue(registry.hasFunction("NOW"));
        assertTrue(registry.hasFunction("WEEKDAY"));
        assertTrue(registry.hasFunction("DAY_OF_MONTH"));
        assertTrue(registry.hasFunction("MONTH"));
        assertTrue(registry.hasFunction("YEAR"));
        assertTrue(registry.hasFunction("IN_RECENT_DAYS"));
        assertTrue(registry.hasFunction("IS_RECURRING"));
        assertEquals(12, registry.size());
    }

    @Test
    void testActionTimeWithAviatorScript() {
        // Register function
        registry.register(new ActionTimeFunction());
        registry.registerAll(aviator);

        Map<String, Object> env = new HashMap<>();
        Event event = Event.builder()
            .timestamp("2023-01-15T10:30:00Z")
            .eventName("purchase")
            .build();
        env.put("currentEvent", event);

        // Test ACTION_TIME() expression
        Object result = aviator.execute("ACTION_TIME()", env);
        assertEquals("2023-01-15T10:30:00Z", result);
    }

    @Test
    void testDateFormatWithAviatorScript() {
        // Register function
        registry.register(new DateFormatFunction());
        registry.registerAll(aviator);

        Map<String, Object> env = new HashMap<>();

        // Test DATE_FORMAT expression
        Object result = aviator.execute("DATE_FORMAT('2023-01-15T10:30:00Z', 'yyyy-MM-dd')", env);
        assertEquals("2023-01-15", result);
    }

    @Test
    void testDateDiffWithAviatorScript() {
        // Register function
        registry.register(new DateDiffFunction());
        registry.registerAll(aviator);

        Map<String, Object> env = new HashMap<>();

        // Test DATE_DIFF expression
        Object result = aviator.execute("DATE_DIFF('2023-01-20T00:00:00Z', '2023-01-15T00:00:00Z', 'D')", env);
        assertEquals(5L, result);
    }

    @Test
    void testFromAndToWithAviatorScript() {
        // Register functions
        registry.register(new FromFunction());
        registry.register(new ToFunction());
        registry.registerAll(aviator);

        Map<String, Object> env = new HashMap<>();
        Instant now = Instant.parse("2023-01-15T10:00:00Z");
        env.put("now", now);

        // Test FROM and TO expressions
        aviator.execute("FROM(30, 'D')", env);
        aviator.execute("TO(0, 'D')", env);

        // Verify time range was set in context
        assertNotNull(env.get("timeRange"));
    }

    @Test
    void testCombinedDateTimeFunctions() {
        // Register all date/time functions
        registry.register(new ActionTimeFunction());
        registry.register(new DateFormatFunction());
        registry.register(new DateDiffFunction());
        registry.register(new FromFunction());
        registry.register(new ToFunction());
        registry.registerAll(aviator);

        Map<String, Object> env = new HashMap<>();
        Event event = Event.builder()
            .timestamp("2023-01-15T10:30:00Z")
            .eventName("purchase")
            .build();
        env.put("currentEvent", event);

        // Test combined expression: DATE_FORMAT(ACTION_TIME(), 'yyyy-MM-dd')
        Object result = aviator.execute("DATE_FORMAT(ACTION_TIME(), 'yyyy-MM-dd')", env);
        assertEquals("2023-01-15", result);
    }

    @Test
    void testDateDiffWithVariables() {
        // Register function
        registry.register(new DateDiffFunction());
        registry.registerAll(aviator);

        Map<String, Object> env = new HashMap<>();
        env.put("date1", "2023-01-20T00:00:00Z");
        env.put("date2", "2023-01-15T00:00:00Z");
        env.put("unit", "D");

        // Test DATE_DIFF with variables
        Object result = aviator.execute("DATE_DIFF(date1, date2, unit)", env);
        assertEquals(5L, result);
    }

    @Test
    void testDateFormatWithDifferentPatterns() {
        // Register function
        registry.register(new DateFormatFunction());
        registry.registerAll(aviator);

        Map<String, Object> env = new HashMap<>();
        env.put("timestamp", "2023-01-15T10:30:45Z");

        // Test different format patterns
        Object result1 = aviator.execute("DATE_FORMAT(timestamp, 'yyyy-MM-dd')", env);
        assertEquals("2023-01-15", result1);

        Object result2 = aviator.execute("DATE_FORMAT(timestamp, 'HH:mm:ss')", env);
        assertEquals("10:30:45", result2);

        Object result3 = aviator.execute("DATE_FORMAT(timestamp, 'MM/dd/yyyy')", env);
        assertEquals("01/15/2023", result3);
    }

    @Test
    void testDateDiffWithDifferentUnits() {
        // Register function
        registry.register(new DateDiffFunction());
        registry.registerAll(aviator);

        Map<String, Object> env = new HashMap<>();
        String date1 = "2023-01-20T12:00:00Z";
        String date2 = "2023-01-15T10:00:00Z";
        env.put("date1", date1);
        env.put("date2", date2);

        // Test different units
        Object days = aviator.execute("DATE_DIFF(date1, date2, 'D')", env);
        assertEquals(5L, days);

        Object hours = aviator.execute("DATE_DIFF(date1, date2, 'H')", env);
        assertEquals(122L, hours); // 5 days * 24 + 2 hours
    }

    @Test
    void testFunctionMetadataAccessibility() {
        // Register functions
        registry.register(new ActionTimeFunction());
        registry.register(new DateFormatFunction());
        registry.register(new DateDiffFunction());
        registry.register(new FromFunction());
        registry.register(new ToFunction());

        // Verify metadata is accessible
        assertNotNull(registry.getMetadata("ACTION_TIME"));
        assertNotNull(registry.getMetadata("DATE_FORMAT"));
        assertNotNull(registry.getMetadata("DATE_DIFF"));
        assertNotNull(registry.getMetadata("FROM"));
        assertNotNull(registry.getMetadata("TO"));

        // Verify metadata details
        assertEquals(0, registry.getMetadata("ACTION_TIME").getMinArgs());
        assertEquals(2, registry.getMetadata("DATE_FORMAT").getMinArgs());
        assertEquals(3, registry.getMetadata("DATE_DIFF").getMinArgs());
        assertEquals(2, registry.getMetadata("FROM").getMinArgs());
        assertEquals(2, registry.getMetadata("TO").getMinArgs());
    }
}

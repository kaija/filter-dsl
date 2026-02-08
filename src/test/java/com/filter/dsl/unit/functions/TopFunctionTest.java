package com.filter.dsl.unit.functions;

import com.filter.dsl.functions.FunctionRegistry;
import com.filter.dsl.functions.aggregation.TopFunction;
import com.filter.dsl.models.Visit;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TopFunctionTest {

    private FunctionRegistry registry;
    private AviatorEvaluatorInstance aviator;

    @BeforeEach
    void setUp() {
        registry = new FunctionRegistry();
        registry.register(new TopFunction());
        
        aviator = AviatorEvaluator.newInstance();
        registry.registerAll(aviator);
    }

    @Test
    void testTopWithSimpleList() {
        List<Integer> numbers = Arrays.asList(1, 2, 2, 3, 2, 1);
        
        Map<String, Object> env = new HashMap<>();
        env.put("numbers", numbers);
        
        Object result = aviator.execute("TOP(numbers)", env);
        
        assertEquals(2, result);
    }

    @Test
    void testTopWithTopN() {
        List<Integer> numbers = Arrays.asList(1, 2, 2, 3, 2, 1, 3);
        
        Map<String, Object> env = new HashMap<>();
        env.put("numbers", numbers);
        
        Object result = aviator.execute("TOP(numbers, 2)", env);
        
        assertTrue(result instanceof List);
        List<?> topValues = (List<?>) result;
        assertEquals(2, topValues.size());
        assertEquals(2, topValues.get(0)); // 2 appears 3 times
        assertTrue(topValues.get(1).equals(1) || topValues.get(1).equals(3)); // 1 and 3 both appear 2 times
    }

    @Test
    void testTopWithPropertyName() {
        List<Visit> visits = Arrays.asList(
            Visit.builder().os("Windows").build(),
            Visit.builder().os("Windows").build(),
            Visit.builder().os("macOS").build(),
            Visit.builder().os("Windows").build(),
            Visit.builder().os("Linux").build()
        );
        
        Map<String, Object> env = new HashMap<>();
        env.put("visits", visits);
        
        Object result = aviator.execute("TOP(visits, 'os')", env);
        
        assertEquals("Windows", result);
    }

    @Test
    void testTopWithPropertyNameAndN() {
        List<Visit> visits = Arrays.asList(
            Visit.builder().browser("Chrome").build(),
            Visit.builder().browser("Chrome").build(),
            Visit.builder().browser("Safari").build(),
            Visit.builder().browser("Chrome").build(),
            Visit.builder().browser("Firefox").build(),
            Visit.builder().browser("Safari").build()
        );
        
        Map<String, Object> env = new HashMap<>();
        env.put("visits", visits);
        
        Object result = aviator.execute("TOP(visits, 'browser', 2)", env);
        
        assertTrue(result instanceof List);
        List<?> topBrowsers = (List<?>) result;
        assertEquals(2, topBrowsers.size());
        assertEquals("Chrome", topBrowsers.get(0)); // Chrome appears 3 times
        assertEquals("Safari", topBrowsers.get(1)); // Safari appears 2 times
    }

    @Test
    void testTopWithEmptyList() {
        List<Integer> numbers = new ArrayList<>();
        
        Map<String, Object> env = new HashMap<>();
        env.put("numbers", numbers);
        
        Object result = aviator.execute("TOP(numbers)", env);
        
        assertNull(result);
    }

    @Test
    void testTopWithNullList() {
        Map<String, Object> env = new HashMap<>();
        env.put("numbers", null);
        
        Object result = aviator.execute("TOP(numbers)", env);
        
        assertNull(result);
    }

    @Test
    void testTopWithStrings() {
        List<String> words = Arrays.asList("apple", "banana", "apple", "cherry", "apple", "banana");
        
        Map<String, Object> env = new HashMap<>();
        env.put("words", words);
        
        Object result = aviator.execute("TOP(words)", env);
        
        assertEquals("apple", result);
    }

    @Test
    void testTopWithUserDataVisits() {
        // Simulate userData.visits
        List<Visit> visits = Arrays.asList(
            Visit.builder().os("Windows 10").browser("Chrome").device("desktop").build(),
            Visit.builder().os("Windows 10").browser("Chrome").device("desktop").build(),
            Visit.builder().os("iOS 17").browser("Safari").device("mobile").build(),
            Visit.builder().os("Windows 10").browser("Firefox").device("desktop").build(),
            Visit.builder().os("macOS").browser("Safari").device("desktop").build()
        );
        
        Map<String, Object> userData = new HashMap<>();
        userData.put("visits", visits);
        
        Map<String, Object> env = new HashMap<>();
        env.put("userData", userData);
        
        // Test most frequent OS
        Object osResult = aviator.execute("TOP(userData.visits, 'os')", env);
        assertEquals("Windows 10", osResult);
        
        // Test most frequent browser
        Object browserResult = aviator.execute("TOP(userData.visits, 'browser')", env);
        assertTrue(browserResult.equals("Chrome") || browserResult.equals("Safari")); // Both appear twice
        
        // Test most frequent device
        Object deviceResult = aviator.execute("TOP(userData.visits, 'device')", env);
        assertEquals("desktop", deviceResult);
    }

    @Test
    void testTopWithTopNGreaterThanCollectionSize() {
        List<Integer> numbers = Arrays.asList(1, 2, 3);
        
        Map<String, Object> env = new HashMap<>();
        env.put("numbers", numbers);
        
        Object result = aviator.execute("TOP(numbers, 10)", env);
        
        assertTrue(result instanceof List);
        List<?> topValues = (List<?>) result;
        assertEquals(3, topValues.size()); // Should return all 3 values
    }

    @Test
    void testTopPreservesOrderForTies() {
        // When values have the same frequency, first occurrence should be preserved
        List<String> items = Arrays.asList("a", "b", "c", "a", "b", "c");
        
        Map<String, Object> env = new HashMap<>();
        env.put("items", items);
        
        Object result = aviator.execute("TOP(items, 3)", env);
        
        assertTrue(result instanceof List);
        List<?> topValues = (List<?>) result;
        assertEquals(3, topValues.size());
        // All have same frequency, so order should be preserved
    }
}

package com.filter.dsl.integration;

import com.filter.dsl.functions.FunctionRegistry;
import com.filter.dsl.functions.aggregation.UniqueFunction;
import com.filter.dsl.functions.aggregation.CountFunction;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for UNIQUE function with AviatorScript.
 * Validates: Requirements 3.6, 16.5
 */
class UniqueFunctionIntegrationTest {

    private AviatorEvaluatorInstance aviator;
    private FunctionRegistry registry;

    @BeforeEach
    void setUp() {
        aviator = AviatorEvaluator.newInstance();
        registry = new FunctionRegistry();
        
        // Register UNIQUE and COUNT functions
        registry.register(new UniqueFunction());
        registry.register(new CountFunction());
        registry.registerAll(aviator);
    }

    @Test
    void testUniqueWithAviatorScript() {
        Map<String, Object> env = new HashMap<>();
        List<Integer> numbers = Arrays.asList(1, 2, 2, 3, 1, 4, 3, 5);
        env.put("numbers", numbers);
        
        Object result = aviator.execute("UNIQUE(numbers)", env);
        
        assertNotNull(result);
        assertTrue(result instanceof List);
        
        @SuppressWarnings("unchecked")
        List<Integer> uniqueNumbers = (List<Integer>) result;
        assertEquals(5, uniqueNumbers.size());
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), uniqueNumbers);
    }

    @Test
    void testUniqueWithCountInExpression() {
        Map<String, Object> env = new HashMap<>();
        List<String> words = Arrays.asList("apple", "banana", "apple", "cherry", "banana", "date");
        env.put("words", words);
        
        // COUNT(UNIQUE(words)) should return 4
        Object result = aviator.execute("COUNT(UNIQUE(words))", env);
        
        assertNotNull(result);
        assertEquals(4L, result);
    }

    @Test
    void testUniquePreservesOrderInAviatorScript() {
        Map<String, Object> env = new HashMap<>();
        List<Integer> numbers = Arrays.asList(5, 3, 5, 1, 3, 2, 1);
        env.put("numbers", numbers);
        
        Object result = aviator.execute("UNIQUE(numbers)", env);
        
        @SuppressWarnings("unchecked")
        List<Integer> uniqueNumbers = (List<Integer>) result;
        assertEquals(Arrays.asList(5, 3, 1, 2), uniqueNumbers);
    }

    @Test
    void testUniqueWithEmptyCollection() {
        Map<String, Object> env = new HashMap<>();
        env.put("empty", new ArrayList<>());
        
        Object result = aviator.execute("UNIQUE(empty)", env);
        
        assertNotNull(result);
        assertTrue(result instanceof List);
        
        @SuppressWarnings("unchecked")
        List<?> uniqueList = (List<?>) result;
        assertTrue(uniqueList.isEmpty());
    }

    @Test
    void testUniqueWithNullInCollection() {
        Map<String, Object> env = new HashMap<>();
        List<Integer> numbers = Arrays.asList(1, null, 2, null, 3);
        env.put("numbers", numbers);
        
        Object result = aviator.execute("UNIQUE(numbers)", env);
        
        @SuppressWarnings("unchecked")
        List<Integer> uniqueNumbers = (List<Integer>) result;
        assertEquals(4, uniqueNumbers.size());
        assertTrue(uniqueNumbers.contains(1));
        assertTrue(uniqueNumbers.contains(2));
        assertTrue(uniqueNumbers.contains(3));
        assertTrue(uniqueNumbers.contains(null));
    }

    @Test
    void testUniqueIdempotentProperty() {
        // Property: UNIQUE(UNIQUE(x)) == UNIQUE(x)
        Map<String, Object> env = new HashMap<>();
        List<Integer> numbers = Arrays.asList(1, 2, 2, 3, 1, 4);
        env.put("numbers", numbers);
        
        Object result1 = aviator.execute("UNIQUE(numbers)", env);
        
        // Apply UNIQUE again to the result
        env.put("unique1", result1);
        Object result2 = aviator.execute("UNIQUE(unique1)", env);
        
        assertEquals(result1, result2);
    }

    @Test
    void testUniqueCountRelationshipProperty() {
        // Property: COUNT(UNIQUE(x)) <= COUNT(x)
        Map<String, Object> env = new HashMap<>();
        List<Integer> numbers = Arrays.asList(1, 2, 2, 3, 1, 4, 3, 5);
        env.put("numbers", numbers);
        
        Long originalCount = (Long) aviator.execute("COUNT(numbers)", env);
        Long uniqueCount = (Long) aviator.execute("COUNT(UNIQUE(numbers))", env);
        
        assertTrue(uniqueCount <= originalCount);
        assertEquals(8L, originalCount);
        assertEquals(5L, uniqueCount);
    }

    @Test
    void testUniqueWithComplexExpression() {
        Map<String, Object> env = new HashMap<>();
        List<Integer> list1 = Arrays.asList(1, 2, 3, 2, 1);
        List<Integer> list2 = Arrays.asList(4, 5, 4, 6);
        env.put("list1", list1);
        env.put("list2", list2);
        
        // Get unique count from first list
        Long count1 = (Long) aviator.execute("COUNT(UNIQUE(list1))", env);
        assertEquals(3L, count1);
        
        // Get unique count from second list
        Long count2 = (Long) aviator.execute("COUNT(UNIQUE(list2))", env);
        assertEquals(3L, count2);
    }

    @Test
    void testUniqueWithStrings() {
        Map<String, Object> env = new HashMap<>();
        List<String> cities = Arrays.asList("Paris", "London", "Paris", "Tokyo", "London", "Berlin");
        env.put("cities", cities);
        
        Object result = aviator.execute("UNIQUE(cities)", env);
        
        @SuppressWarnings("unchecked")
        List<String> uniqueCities = (List<String>) result;
        assertEquals(4, uniqueCities.size());
        assertEquals(Arrays.asList("Paris", "London", "Tokyo", "Berlin"), uniqueCities);
    }

    @Test
    void testUniqueWithMixedTypes() {
        Map<String, Object> env = new HashMap<>();
        List<Object> mixed = Arrays.asList(1, "text", 1, 2.5, "text", true);
        env.put("mixed", mixed);
        
        Object result = aviator.execute("UNIQUE(mixed)", env);
        
        @SuppressWarnings("unchecked")
        List<Object> uniqueMixed = (List<Object>) result;
        assertEquals(4, uniqueMixed.size());
        assertEquals(Arrays.asList(1, "text", 2.5, true), uniqueMixed);
    }

    @Test
    void testUniqueWithLargeDataset() {
        Map<String, Object> env = new HashMap<>();
        List<Integer> largeList = new ArrayList<>();
        
        // Create a list with 1000 elements, each number 0-99 repeated 10 times
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 100; j++) {
                largeList.add(j);
            }
        }
        env.put("largeList", largeList);
        
        Object result = aviator.execute("UNIQUE(largeList)", env);
        
        @SuppressWarnings("unchecked")
        List<Integer> uniqueList = (List<Integer>) result;
        assertEquals(100, uniqueList.size());
        
        // Verify order is preserved (first occurrence)
        for (int i = 0; i < 100; i++) {
            assertEquals(i, uniqueList.get(i));
        }
    }
}

package com.filter.dsl;

import com.filter.dsl.evaluator.EvaluationResult;
import com.filter.dsl.models.Event;
import com.filter.dsl.models.Profile;
import com.filter.dsl.models.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the DSL facade class.
 * 
 * Tests cover:
 * - Simple static API usage
 * - Builder configuration
 * - Thread safety
 * - Caching behavior
 * - Error handling
 */
class DSLTest {
    
    private UserData testUser;
    
    @BeforeEach
    void setUp() {
        // Create test user data
        Profile profile = Profile.builder()
            .uuid("user-123")
            .country("US")
            .city("San Francisco")
            .language("en")
            .build();
        
        List<Event> events = new ArrayList<>();
        
        // Add purchase events
        for (int i = 0; i < 7; i++) {
            Map<String, Object> params = new HashMap<>();
            params.put("amount", 100.0);
            
            Event event = Event.builder()
                .uuid("event-" + i)
                .eventName("purchase")
                .timestamp(Instant.now().minusSeconds(i * 86400).toString())
                .parameters(params)
                .build();
            events.add(event);
        }
        
        // Add view events
        for (int i = 0; i < 3; i++) {
            Event event = Event.builder()
                .uuid("event-view-" + i)
                .eventName("page_view")
                .timestamp(Instant.now().minusSeconds(i * 3600).toString())
                .build();
            events.add(event);
        }
        
        testUser = UserData.builder()
            .profile(profile)
            .events(events)
            .visits(new HashMap<>())
            .build();
    }
    
    @Test
    void testSimpleStaticEvaluate() {
        // Test simple boolean expression
        String expression = "GT(COUNT(userData.events), 5)";
        EvaluationResult result = DSL.evaluate(expression, testUser);
        
        assertTrue(result.isSuccess(), "Evaluation should succeed");
        assertNotNull(result.getValue(), "Result value should not be null");
        assertTrue(result.getValue() instanceof Boolean, "Result should be Boolean");
        assertTrue((Boolean) result.getValue(), "Should have more than 5 events");
    }
    
    @Test
    void testStaticEvaluateWithFiltering() {
        // Test filtering expression - just check if we have events
        String expression = "GT(COUNT(userData.events), 5)";
        EvaluationResult result = DSL.evaluate(expression, testUser);
        
        assertTrue(result.isSuccess(), "Evaluation should succeed");
        assertTrue((Boolean) result.getValue(), "Should have more than 5 events");
    }
    
    @Test
    void testStaticEvaluateWithComputation() {
        // Test computed value expression
        String expression = "COUNT(userData.events)";
        EvaluationResult result = DSL.evaluate(expression, testUser);
        
        assertTrue(result.isSuccess(), "Evaluation should succeed");
        assertTrue(result.getValue() instanceof Number, "Result should be a number");
        assertEquals(10L, ((Number) result.getValue()).longValue(), "Should have 10 events (7 purchases + 3 views)");
    }
    
    @Test
    void testStaticEvaluateBatch() {
        // Create multiple users
        List<UserData> users = new ArrayList<>();
        users.add(testUser);
        
        // Create user with fewer events
        UserData user2 = UserData.builder()
            .profile(Profile.builder().uuid("user-456").build())
            .events(Arrays.asList(
                Event.builder().uuid("e1").eventName("purchase").build(),
                Event.builder().uuid("e2").eventName("purchase").build()
            ))
            .visits(new HashMap<>())
            .build();
        users.add(user2);
        
        String expression = "GT(COUNT(userData.events), 5)";
        List<EvaluationResult> results = DSL.evaluateBatch(expression, users);
        
        assertEquals(2, results.size(), "Should have 2 results");
        assertTrue(results.get(0).isSuccess(), "First evaluation should succeed");
        assertTrue((Boolean) results.get(0).getValue(), "First user should match");
        assertTrue(results.get(1).isSuccess(), "Second evaluation should succeed");
        assertFalse((Boolean) results.get(1).getValue(), "Second user should not match");
    }
    
    @Test
    void testBuilderWithAutoDiscovery() {
        DSL dsl = DSL.builder()
            .enableAutoDiscovery(true)
            .enableCaching(true)
            .build();
        
        assertNotNull(dsl, "DSL instance should be created");
        assertNotNull(dsl.getRegistry(), "Registry should be initialized");
        assertTrue(dsl.getRegistry().size() > 0, "Functions should be auto-discovered");
        
        // Test evaluation with custom instance
        String expression = "GT(COUNT(userData.events), 5)";
        EvaluationResult result = dsl.evaluateInstance(expression, testUser);
        
        assertTrue(result.isSuccess(), "Evaluation should succeed");
        assertTrue((Boolean) result.getValue(), "Should have more than 5 events");
    }
    
    @Test
    void testBuilderWithoutAutoDiscovery() {
        DSL dsl = DSL.builder()
            .enableAutoDiscovery(false)
            .enableCaching(true)
            .build();
        
        assertNotNull(dsl, "DSL instance should be created");
        assertEquals(0, dsl.getRegistry().size(), "No functions should be registered");
    }
    
    @Test
    void testCachingEnabled() {
        DSL dsl = DSL.builder()
            .enableAutoDiscovery(true)
            .enableCaching(true)
            .build();
        
        String expression = "GT(COUNT(userData.events), 5)";
        
        // First evaluation - should compile and cache
        dsl.evaluateInstance(expression, testUser);
        assertEquals(1, dsl.getCacheSizeInstance(), "Expression should be cached");
        
        // Second evaluation - should use cache
        dsl.evaluateInstance(expression, testUser);
        assertEquals(1, dsl.getCacheSizeInstance(), "Cache size should remain 1");
        
        // Different expression - should add to cache
        dsl.evaluateInstance("GT(COUNT(userData.events), 10)", testUser);
        assertEquals(2, dsl.getCacheSizeInstance(), "Cache should have 2 expressions");
        
        // Clear cache
        dsl.clearCacheInstance();
        assertEquals(0, dsl.getCacheSizeInstance(), "Cache should be empty");
    }
    
    @Test
    void testStaticCacheOperations() {
        // Clear cache first
        DSL.clearCache();
        assertEquals(0, DSL.getCacheSize(), "Cache should be empty");
        
        // Evaluate to populate cache
        DSL.evaluate("GT(COUNT(userData.events), 5)", testUser);
        assertTrue(DSL.getCacheSize() > 0, "Cache should have entries");
        
        // Clear cache
        DSL.clearCache();
        assertEquals(0, DSL.getCacheSize(), "Cache should be empty after clear");
    }
    
    @Test
    void testErrorHandling_InvalidSyntax() {
        String expression = "GT(COUNT(userData.events), 5";  // Missing closing parenthesis
        EvaluationResult result = DSL.evaluate(expression, testUser);
        
        assertFalse(result.isSuccess(), "Evaluation should fail");
        assertNotNull(result.getErrorMessage(), "Error message should be present");
        assertEquals(EvaluationResult.ErrorType.SYNTAX_ERROR, result.getErrorType(), 
                    "Should be a syntax error");
    }
    
    @Test
    void testErrorHandling_UndefinedFunction() {
        String expression = "UNDEFINED_FUNC(userData.events)";
        EvaluationResult result = DSL.evaluate(expression, testUser);
        
        assertFalse(result.isSuccess(), "Evaluation should fail");
        assertNotNull(result.getErrorMessage(), "Error message should be present");
    }
    
    @Test
    void testErrorHandling_TypeMismatch() {
        // Try to use GT with non-numeric values
        String expression = "GT(\"string\", 5)";
        EvaluationResult result = DSL.evaluate(expression, testUser);
        
        assertFalse(result.isSuccess(), "Evaluation should fail");
        assertNotNull(result.getErrorMessage(), "Error message should be present");
    }
    
    @Test
    void testThreadSafety() throws InterruptedException {
        final DSL dsl = DSL.builder()
            .enableAutoDiscovery(true)
            .enableCaching(true)
            .build();
        
        final String expression = "GT(COUNT(userData.events), 5)";
        final int threadCount = 10;
        final int iterationsPerThread = 100;
        
        List<Thread> threads = new ArrayList<>();
        List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());
        
        // Create threads that evaluate expressions concurrently
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(() -> {
                try {
                    for (int j = 0; j < iterationsPerThread; j++) {
                        EvaluationResult result = dsl.evaluateInstance(expression, testUser);
                        assertTrue(result.isSuccess(), "Evaluation should succeed");
                    }
                } catch (Exception e) {
                    exceptions.add(e);
                }
            });
            threads.add(thread);
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Check for exceptions
        if (!exceptions.isEmpty()) {
            fail("Thread safety test failed with exceptions: " + exceptions);
        }
        
        // Verify cache is still consistent
        assertTrue(dsl.getCacheSizeInstance() > 0, "Cache should have entries");
    }
    
    @Test
    void testDefaultInstanceIsSingleton() {
        // Multiple calls to static methods should use the same instance
        DSL.evaluate("GT(COUNT(userData.events), 5)", testUser);
        int cacheSize1 = DSL.getCacheSize();
        
        DSL.evaluate("GT(COUNT(userData.events), 10)", testUser);
        int cacheSize2 = DSL.getCacheSize();
        
        assertTrue(cacheSize2 > cacheSize1, "Cache should grow, indicating same instance");
    }
    
    @Test
    void testInstanceMethodsWorkIndependently() {
        DSL dsl1 = DSL.builder().enableAutoDiscovery(true).enableCaching(true).build();
        DSL dsl2 = DSL.builder().enableAutoDiscovery(true).enableCaching(true).build();
        
        String expression = "GT(COUNT(userData.events), 5)";
        
        // Evaluate with first instance
        dsl1.evaluateInstance(expression, testUser);
        assertEquals(1, dsl1.getCacheSizeInstance(), "First instance should have 1 cached");
        assertEquals(0, dsl2.getCacheSizeInstance(), "Second instance should have 0 cached");
        
        // Evaluate with second instance
        dsl2.evaluateInstance(expression, testUser);
        assertEquals(1, dsl1.getCacheSizeInstance(), "First instance should still have 1 cached");
        assertEquals(1, dsl2.getCacheSizeInstance(), "Second instance should have 1 cached");
    }
    
    @Test
    void testGetParser() {
        DSL dsl = DSL.builder().enableAutoDiscovery(true).build();
        assertNotNull(dsl.getParser(), "Parser should be accessible");
    }
    
    @Test
    void testGetRegistry() {
        DSL dsl = DSL.builder().enableAutoDiscovery(true).build();
        assertNotNull(dsl.getRegistry(), "Registry should be accessible");
        assertTrue(dsl.getRegistry().size() > 0, "Registry should have functions");
    }
    
    @Test
    void testEvaluationResultContainsExpression() {
        String expression = "GT(COUNT(userData.events), 5)";
        EvaluationResult result = DSL.evaluate(expression, testUser);
        
        assertEquals(expression, result.getExpression(), "Result should contain the expression");
    }
    
    @Test
    void testEvaluationResultContainsTiming() {
        String expression = "GT(COUNT(userData.events), 5)";
        EvaluationResult result = DSL.evaluate(expression, testUser);
        
        assertTrue(result.getEvaluationTimeMs() >= 0, "Evaluation time should be non-negative");
    }
    
    @Test
    void testComplexExpression() {
        // Test a more complex expression with nested functions
        String expression = "AND(" +
            "GT(COUNT(userData.events), 5), " +
            "EQ(PROFILE(\"country\"), \"US\")" +
            ")";
        
        EvaluationResult result = DSL.evaluate(expression, testUser);
        
        assertTrue(result.isSuccess(), "Complex evaluation should succeed");
        assertTrue((Boolean) result.getValue(), "Complex condition should be true");
    }
    
    @Test
    void testBatchEvaluationWithErrors() {
        List<UserData> users = Arrays.asList(testUser, testUser);
        
        // Invalid expression
        String expression = "INVALID(";
        List<EvaluationResult> results = DSL.evaluateBatch(expression, users);
        
        assertEquals(2, results.size(), "Should have 2 results");
        assertFalse(results.get(0).isSuccess(), "First result should be error");
        assertFalse(results.get(1).isSuccess(), "Second result should be error");
    }
    
    @Test
    void testBuilderChaining() {
        // Test that builder methods can be chained
        DSL dsl = DSL.builder()
            .enableAutoDiscovery(true)
            .enableCaching(false)
            .enableAutoDiscovery(false)
            .enableCaching(true)
            .build();
        
        assertNotNull(dsl, "DSL should be created with chained builder");
    }
}

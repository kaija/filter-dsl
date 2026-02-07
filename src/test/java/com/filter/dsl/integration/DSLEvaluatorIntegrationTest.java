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
import com.filter.dsl.models.Visit;
import com.filter.dsl.parser.DSLParser;
import com.filter.dsl.parser.DSLParserImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for DSLEvaluator demonstrating real-world use cases.
 * 
 * These tests validate the complete evaluation pipeline from expression parsing
 * through compilation and execution with realistic user data.
 */
class DSLEvaluatorIntegrationTest {
    
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
    void testComplexFilteringExpression() {
        // Test: Users from US or UK with more than 2 events
        String expression = "AND(" +
            "OR(EQ(PROFILE(\"country\"), \"US\"), EQ(PROFILE(\"country\"), \"UK\"))," +
            "GT(COUNT(userData.events), 2)" +
        ")";
        
        // User from US with 3 events - should match
        UserData user1 = createUserWithEvents("US", 3);
        EvaluationResult result1 = evaluator.evaluate(expression, user1);
        assertTrue(result1.isSuccess());
        assertEquals(true, result1.getValue());
        
        // User from UK with 1 event - should not match
        UserData user2 = createUserWithEvents("UK", 1);
        EvaluationResult result2 = evaluator.evaluate(expression, user2);
        assertTrue(result2.isSuccess());
        assertEquals(false, result2.getValue());
        
        // User from FR with 5 events - should not match
        UserData user3 = createUserWithEvents("FR", 5);
        EvaluationResult result3 = evaluator.evaluate(expression, user3);
        assertTrue(result3.isSuccess());
        assertEquals(false, result3.getValue());
    }
    
    @Test
    void testAggregationWithFiltering() {
        // Test: Count of purchase events
        String expression = "COUNT(userData.events)";
        
        UserData userData = createUserWithPurchases(5);
        EvaluationResult result = evaluator.evaluate(expression, userData);
        
        assertTrue(result.isSuccess());
        assertEquals(5L, result.getValue());
    }
    
    @Test
    void testStringOperations() {
        // Test: Check if country name starts with 'U'
        String expression = "STARTS_WITH(PROFILE(\"country\"), \"U\")";
        
        UserData user1 = createUserWithCountry("US");
        EvaluationResult result1 = evaluator.evaluate(expression, user1);
        assertTrue(result1.isSuccess());
        assertEquals(true, result1.getValue());
        
        UserData user2 = createUserWithCountry("France");
        EvaluationResult result2 = evaluator.evaluate(expression, user2);
        assertTrue(result2.isSuccess());
        assertEquals(false, result2.getValue());
    }
    
    @Test
    void testMathematicalComputations() {
        // Test: Calculate average of values
        String expression = "DIVIDE(ADD(ADD(10, 20), 30), 3)";
        
        UserData userData = createBasicUserData();
        EvaluationResult result = evaluator.evaluate(expression, userData);
        
        assertTrue(result.isSuccess());
        assertEquals(20.0, result.getValue());
    }
    
    @Test
    void testNestedFunctionCalls() {
        // Test: Complex nested expression
        String expression = "GT(" +
            "MULTIPLY(ADD(5, 3), 2)," +
            "SUBTRACT(20, 5)" +
        ")";
        
        UserData userData = createBasicUserData();
        EvaluationResult result = evaluator.evaluate(expression, userData);
        
        assertTrue(result.isSuccess());
        // (5 + 3) * 2 = 16, 20 - 5 = 15, 16 > 15 = true
        assertEquals(true, result.getValue());
    }
    
    @Test
    void testBatchEvaluationPerformance() {
        // Test: Batch evaluation is more efficient than individual evaluations
        String expression = "AND(" +
            "EQ(PROFILE(\"country\"), \"US\")," +
            "GT(COUNT(userData.events), 1)" +
        ")";
        
        // Create 100 users
        List<UserData> users = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            users.add(createUserWithEvents("US", i % 5));
        }
        
        // Batch evaluation
        long batchStart = System.currentTimeMillis();
        List<EvaluationResult> batchResults = evaluator.evaluateBatch(expression, users);
        long batchTime = System.currentTimeMillis() - batchStart;
        
        // Verify results
        assertEquals(100, batchResults.size());
        for (EvaluationResult result : batchResults) {
            assertTrue(result.isSuccess());
        }
        
        // Individual evaluations
        long individualStart = System.currentTimeMillis();
        for (UserData user : users) {
            evaluator.evaluate(expression, user);
        }
        long individualTime = System.currentTimeMillis() - individualStart;
        
        // Batch should be faster or comparable (due to expression caching)
        System.out.println("Batch time: " + batchTime + "ms, Individual time: " + individualTime + "ms");
        // Note: With caching, both should be fast, but batch has less overhead
    }
    
    @Test
    void testExpressionCaching() {
        // Test: Expression caching improves performance
        String expression = "ADD(MULTIPLY(5, 3), DIVIDE(20, 4))";
        UserData userData = createBasicUserData();
        
        // Clear cache
        evaluator.clearCache();
        assertEquals(0, evaluator.getCacheSize());
        
        // First evaluation - compiles and caches
        long firstStart = System.currentTimeMillis();
        EvaluationResult result1 = evaluator.evaluate(expression, userData);
        long firstTime = System.currentTimeMillis() - firstStart;
        
        assertTrue(result1.isSuccess());
        assertEquals(1, evaluator.getCacheSize());
        
        // Second evaluation - uses cache
        long secondStart = System.currentTimeMillis();
        EvaluationResult result2 = evaluator.evaluate(expression, userData);
        long secondTime = System.currentTimeMillis() - secondStart;
        
        assertTrue(result2.isSuccess());
        assertEquals(1, evaluator.getCacheSize());
        
        // Results should be identical
        assertEquals(result1.getValue(), result2.getValue());
        
        System.out.println("First eval: " + firstTime + "ms, Second eval: " + secondTime + "ms");
    }
    
    @Test
    void testErrorHandlingInComplexExpression() {
        // Test: Error in nested expression is handled gracefully
        String expression = "AND(" +
            "EQ(PROFILE(\"country\"), \"US\")," +
            "GT(UNDEFINED_FUNC(), 5)" +  // This will cause an error
        ")";
        
        UserData userData = createBasicUserData();
        EvaluationResult result = evaluator.evaluate(expression, userData);
        
        assertFalse(result.isSuccess());
        assertNotNull(result.getErrorMessage());
        assertNotNull(result.getErrorType());
    }
    
    @Test
    void testMultipleDataSourceAccess() {
        // Test: Access profile, events, and visits in one expression
        String expression = "AND(" +
            "EQ(PROFILE(\"country\"), \"US\")," +
            "GT(COUNT(userData.events), 0)" +
        ")";
        
        UserData userData = createCompleteUserData();
        EvaluationResult result = evaluator.evaluate(expression, userData);
        
        assertTrue(result.isSuccess());
        assertEquals(true, result.getValue());
    }
    
    @Test
    void testComparisonOperations() {
        // Test: All comparison operations
        UserData userData = createBasicUserData();
        
        // Greater than
        EvaluationResult gt = evaluator.evaluate("GT(10, 5)", userData);
        assertTrue(gt.isSuccess());
        assertEquals(true, gt.getValue());
        
        // Less than
        EvaluationResult lt = evaluator.evaluate("LT(5, 10)", userData);
        assertTrue(lt.isSuccess());
        assertEquals(true, lt.getValue());
        
        // Greater than or equal
        EvaluationResult gte = evaluator.evaluate("GTE(10, 10)", userData);
        assertTrue(gte.isSuccess());
        assertEquals(true, gte.getValue());
        
        // Less than or equal
        EvaluationResult lte = evaluator.evaluate("LTE(5, 5)", userData);
        assertTrue(lte.isSuccess());
        assertEquals(true, lte.getValue());
        
        // Equal
        EvaluationResult eq = evaluator.evaluate("EQ(5, 5)", userData);
        assertTrue(eq.isSuccess());
        assertEquals(true, eq.getValue());
        
        // Not equal
        EvaluationResult neq = evaluator.evaluate("NEQ(5, 10)", userData);
        assertTrue(neq.isSuccess());
        assertEquals(true, neq.getValue());
    }
    
    @Test
    void testLogicalOperations() {
        // Test: Logical operations
        UserData userData = createBasicUserData();
        
        // AND
        EvaluationResult and = evaluator.evaluate("AND(true, true)", userData);
        assertTrue(and.isSuccess());
        assertEquals(true, and.getValue());
        
        // OR
        EvaluationResult or = evaluator.evaluate("OR(false, true)", userData);
        assertTrue(or.isSuccess());
        assertEquals(true, or.getValue());
        
        // NOT
        EvaluationResult not = evaluator.evaluate("NOT(false)", userData);
        assertTrue(not.isSuccess());
        assertEquals(true, not.getValue());
    }
    
    // Helper methods
    
    private UserData createBasicUserData() {
        Profile profile = Profile.builder()
            .uuid("user-123")
            .country("US")
            .city("New York")
            .build();
        
        return UserData.builder()
            .profile(profile)
            .events(new ArrayList<>())
            .visits(new HashMap<>())
            .build();
    }
    
    private UserData createUserWithCountry(String country) {
        Profile profile = Profile.builder()
            .uuid("user-" + country)
            .country(country)
            .build();
        
        return UserData.builder()
            .profile(profile)
            .events(new ArrayList<>())
            .visits(new HashMap<>())
            .build();
    }
    
    private UserData createUserWithEvents(String country, int eventCount) {
        Profile profile = Profile.builder()
            .uuid("user-" + country)
            .country(country)
            .build();
        
        List<Event> events = new ArrayList<>();
        for (int i = 0; i < eventCount; i++) {
            events.add(createEvent("event-" + i, "action"));
        }
        
        return UserData.builder()
            .profile(profile)
            .events(events)
            .visits(new HashMap<>())
            .build();
    }
    
    private UserData createUserWithPurchases(int purchaseCount) {
        Profile profile = Profile.builder()
            .uuid("user-purchases")
            .country("US")
            .build();
        
        List<Event> events = new ArrayList<>();
        for (int i = 0; i < purchaseCount; i++) {
            events.add(createEvent("purchase-" + i, "purchase"));
        }
        
        return UserData.builder()
            .profile(profile)
            .events(events)
            .visits(new HashMap<>())
            .build();
    }
    
    private UserData createCompleteUserData() {
        Profile profile = Profile.builder()
            .uuid("user-complete")
            .country("US")
            .city("San Francisco")
            .language("en")
            .build();
        
        List<Event> events = new ArrayList<>();
        events.add(createEvent("event-1", "purchase"));
        events.add(createEvent("event-2", "view"));
        
        Map<String, Visit> visits = new HashMap<>();
        Visit visit = Visit.builder()
            .uuid("visit-1")
            .timestamp(Instant.now().toString())
            .landingPage("/home")
            .build();
        visits.put("visit-1", visit);
        
        return UserData.builder()
            .profile(profile)
            .events(events)
            .visits(visits)
            .build();
    }
    
    private Event createEvent(String uuid, String eventName) {
        return Event.builder()
            .uuid(uuid)
            .eventName(eventName)
            .timestamp(Instant.now().minus(1, ChronoUnit.DAYS).toString())
            .parameters(new HashMap<>())
            .build();
    }
}

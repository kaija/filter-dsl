package com.filter.dsl.unit.evaluator;

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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DSLEvaluator.
 * 
 * Tests the integration of parser, compiler, and context manager,
 * as well as error handling and caching behavior.
 */
class DSLEvaluatorTest {
    
    private DSLEvaluator evaluator;
    private FunctionRegistry registry;
    private DSLParser parser;
    private DataContextManager contextManager;
    
    @BeforeEach
    void setUp() {
        // Initialize components
        registry = new FunctionRegistry();
        registry.discoverAndRegister("com.filter.dsl.functions");
        
        parser = new DSLParserImpl(registry);
        contextManager = new DataContextManagerImpl();
        
        evaluator = new DSLEvaluatorImpl(parser, registry, contextManager);
    }
    
    @Test
    void testEvaluateSimpleExpression() {
        // Simple arithmetic expression
        String expression = "ADD(5, 3)";
        UserData userData = createTestUserData();
        
        EvaluationResult result = evaluator.evaluate(expression, userData);
        
        assertTrue(result.isSuccess());
        // AviatorScript returns Double for arithmetic operations
        assertEquals(8.0, result.getValue());
        assertTrue(result.getEvaluationTimeMs() >= 0);
    }
    
    @Test
    void testEvaluateBooleanExpression() {
        // Boolean expression
        String expression = "GT(10, 5)";
        UserData userData = createTestUserData();
        
        EvaluationResult result = evaluator.evaluate(expression, userData);
        
        assertTrue(result.isSuccess());
        assertEquals(true, result.getValue());
    }
    
    @Test
    void testEvaluateWithProfileData() {
        // Access profile data
        String expression = "EQ(PROFILE(\"country\"), \"US\")";
        UserData userData = createTestUserData();
        
        EvaluationResult result = evaluator.evaluate(expression, userData);
        
        assertTrue(result.isSuccess());
        assertEquals(true, result.getValue());
    }
    
    @Test
    void testEvaluateComplexExpression() {
        // Complex nested expression
        String expression = "AND(GT(ADD(5, 3), 7), LT(MULTIPLY(2, 3), 10))";
        UserData userData = createTestUserData();
        
        EvaluationResult result = evaluator.evaluate(expression, userData);
        
        assertTrue(result.isSuccess());
        assertEquals(true, result.getValue());
    }
    
    @Test
    void testEvaluateSyntaxError() {
        // Expression with syntax error (missing closing parenthesis)
        String expression = "ADD(5, 3";
        UserData userData = createTestUserData();
        
        EvaluationResult result = evaluator.evaluate(expression, userData);
        
        assertFalse(result.isSuccess());
        assertEquals(EvaluationResult.ErrorType.SYNTAX_ERROR, result.getErrorType());
        assertNotNull(result.getErrorMessage());
    }
    
    @Test
    void testEvaluateUndefinedFunction() {
        // Expression with undefined function
        String expression = "UNDEFINED_FUNC(5)";
        UserData userData = createTestUserData();
        
        EvaluationResult result = evaluator.evaluate(expression, userData);
        
        assertFalse(result.isSuccess());
        assertEquals(EvaluationResult.ErrorType.SYNTAX_ERROR, result.getErrorType());
        assertTrue(result.getErrorMessage().contains("UNDEFINED_FUNC"));
    }
    
    @Test
    void testEvaluateRuntimeError() {
        // Expression that causes runtime error (division by zero)
        // Note: DIVIDE returns null for division by zero, not an error
        // Let's test with a different runtime error - type mismatch in comparison
        String expression = "GT(\"string\", 5)";
        UserData userData = createTestUserData();
        
        EvaluationResult result = evaluator.evaluate(expression, userData);
        
        assertFalse(result.isSuccess());
        assertEquals(EvaluationResult.ErrorType.RUNTIME_ERROR, result.getErrorType());
        assertNotNull(result.getErrorMessage());
    }
    
    @Test
    void testEvaluateTypeError() {
        // Expression with type mismatch
        String expression = "ADD(\"string\", 5)";
        UserData userData = createTestUserData();
        
        EvaluationResult result = evaluator.evaluate(expression, userData);
        
        assertFalse(result.isSuccess());
        assertEquals(EvaluationResult.ErrorType.RUNTIME_ERROR, result.getErrorType());
    }
    
    @Test
    void testEvaluateBatch() {
        // Batch evaluation with multiple users
        String expression = "EQ(PROFILE(\"country\"), \"US\")";
        
        List<UserData> users = new ArrayList<>();
        users.add(createUserDataWithCountry("US"));
        users.add(createUserDataWithCountry("UK"));
        users.add(createUserDataWithCountry("US"));
        
        List<EvaluationResult> results = evaluator.evaluateBatch(expression, users);
        
        assertEquals(3, results.size());
        assertTrue(results.get(0).isSuccess());
        assertEquals(true, results.get(0).getValue());
        assertTrue(results.get(1).isSuccess());
        assertEquals(false, results.get(1).getValue());
        assertTrue(results.get(2).isSuccess());
        assertEquals(true, results.get(2).getValue());
    }
    
    @Test
    void testEvaluateBatchWithError() {
        // Batch evaluation with syntax error
        String expression = "INVALID(";
        
        List<UserData> users = new ArrayList<>();
        users.add(createTestUserData());
        users.add(createTestUserData());
        
        List<EvaluationResult> results = evaluator.evaluateBatch(expression, users);
        
        assertEquals(2, results.size());
        assertFalse(results.get(0).isSuccess());
        assertFalse(results.get(1).isSuccess());
        assertEquals(EvaluationResult.ErrorType.SYNTAX_ERROR, results.get(0).getErrorType());
    }
    
    @Test
    void testExpressionCaching() {
        // Test that expressions are cached
        String expression = "ADD(5, 3)";
        UserData userData = createTestUserData();
        
        assertEquals(0, evaluator.getCacheSize());
        
        // First evaluation - should compile and cache
        EvaluationResult result1 = evaluator.evaluate(expression, userData);
        assertTrue(result1.isSuccess());
        assertEquals(1, evaluator.getCacheSize());
        
        // Second evaluation - should use cache
        EvaluationResult result2 = evaluator.evaluate(expression, userData);
        assertTrue(result2.isSuccess());
        assertEquals(1, evaluator.getCacheSize());
        
        // Different expression - should compile and cache
        String expression2 = "MULTIPLY(2, 4)";
        EvaluationResult result3 = evaluator.evaluate(expression2, userData);
        assertTrue(result3.isSuccess());
        assertEquals(2, evaluator.getCacheSize());
    }
    
    @Test
    void testClearCache() {
        // Test cache clearing
        String expression = "ADD(5, 3)";
        UserData userData = createTestUserData();
        
        evaluator.evaluate(expression, userData);
        assertEquals(1, evaluator.getCacheSize());
        
        evaluator.clearCache();
        assertEquals(0, evaluator.getCacheSize());
        
        // Should still work after clearing cache
        EvaluationResult result = evaluator.evaluate(expression, userData);
        assertTrue(result.isSuccess());
        assertEquals(1, evaluator.getCacheSize());
    }
    
    @Test
    void testEvaluateWithAggregation() {
        // Test with aggregation function - COUNT needs a collection
        // The events field needs to be accessed properly
        String expression = "COUNT(userData.events)";
        UserData userData = createTestUserData();
        
        EvaluationResult result = evaluator.evaluate(expression, userData);
        
        assertTrue(result.isSuccess());
        assertEquals(3L, result.getValue());
    }
    
    @Test
    void testEvaluateWithStringFunction() {
        // Test with string function
        String expression = "UPPER(PROFILE(\"country\"))";
        UserData userData = createTestUserData();
        
        EvaluationResult result = evaluator.evaluate(expression, userData);
        
        assertTrue(result.isSuccess());
        assertEquals("US", result.getValue());
    }
    
    @Test
    void testEvaluateWithLogicalOperations() {
        // Test with logical operations
        String expression = "OR(EQ(PROFILE(\"country\"), \"US\"), EQ(PROFILE(\"country\"), \"UK\"))";
        UserData userData = createTestUserData();
        
        EvaluationResult result = evaluator.evaluate(expression, userData);
        
        assertTrue(result.isSuccess());
        assertEquals(true, result.getValue());
    }
    
    @Test
    void testEvaluateNullUserData() {
        // Test with null user data - should handle gracefully
        String expression = "ADD(5, 3)";
        
        EvaluationResult result = evaluator.evaluate(expression, null);
        
        // Should still work for expressions that don't access user data
        assertTrue(result.isSuccess());
        // AviatorScript returns Double for arithmetic operations
        assertEquals(8.0, result.getValue());
    }
    
    @Test
    void testEvaluationResultToString() {
        // Test EvaluationResult toString
        String expression = "ADD(5, 3)";
        UserData userData = createTestUserData();
        
        EvaluationResult result = evaluator.evaluate(expression, userData);
        String str = result.toString();
        
        assertTrue(str.contains("success=true"));
        assertTrue(str.contains("value=8"));
    }
    
    @Test
    void testErrorResultToString() {
        // Test error result toString
        String expression = "INVALID(";
        UserData userData = createTestUserData();
        
        EvaluationResult result = evaluator.evaluate(expression, userData);
        String str = result.toString();
        
        assertTrue(str.contains("success=false"));
        assertTrue(str.contains("errorType="));
    }
    
    // Helper methods
    
    private UserData createTestUserData() {
        Profile profile = Profile.builder()
            .uuid("user-123")
            .country("US")
            .city("New York")
            .language("en")
            .build();
        
        List<Event> events = new ArrayList<>();
        events.add(createEvent("event-1", "purchase"));
        events.add(createEvent("event-2", "view"));
        events.add(createEvent("event-3", "purchase"));
        
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
    
    private UserData createUserDataWithCountry(String country) {
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
    
    private Event createEvent(String uuid, String eventName) {
        return Event.builder()
            .uuid(uuid)
            .eventName(eventName)
            .timestamp(Instant.now().toString())
            .parameters(new HashMap<>())
            .build();
    }
}

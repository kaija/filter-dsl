package com.filter.dsl.unit.functions;

import com.filter.dsl.functions.FunctionRegistry;
import com.filter.dsl.functions.string.*;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import com.googlecode.aviator.exception.ExpressionRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for string matching functions with FunctionRegistry and AviatorScript
 */
class StringFunctionsRegistrationTest {

    private FunctionRegistry registry;
    private AviatorEvaluatorInstance aviator;

    @BeforeEach
    void setUp() {
        registry = new FunctionRegistry();
        aviator = AviatorEvaluator.newInstance();
    }

    @Test
    void testRegisterStringMatchingFunctions() {
        // Register all string matching functions
        registry.register(new ContainsFunction());
        registry.register(new StartsWithFunction());
        registry.register(new EndsWithFunction());
        registry.register(new RegexMatchFunction());

        // Verify registration
        assertTrue(registry.hasFunction("CONTAINS"));
        assertTrue(registry.hasFunction("STARTS_WITH"));
        assertTrue(registry.hasFunction("ENDS_WITH"));
        assertTrue(registry.hasFunction("REGEX_MATCH"));
        assertEquals(4, registry.size());
    }

    @Test
    void testRegisterStringManipulationFunctions() {
        // Register all string manipulation functions
        registry.register(new UpperFunction());
        registry.register(new LowerFunction());
        registry.register(new TrimFunction());
        registry.register(new SubstringFunction());
        registry.register(new ReplaceFunction());
        registry.register(new LengthFunction());
        registry.register(new ConcatFunction());
        registry.register(new SplitFunction());

        // Verify registration
        assertTrue(registry.hasFunction("UPPER"));
        assertTrue(registry.hasFunction("LOWER"));
        assertTrue(registry.hasFunction("TRIM"));
        assertTrue(registry.hasFunction("SUBSTRING"));
        assertTrue(registry.hasFunction("REPLACE"));
        assertTrue(registry.hasFunction("LENGTH"));
        assertTrue(registry.hasFunction("CONCAT"));
        assertTrue(registry.hasFunction("SPLIT"));
        assertEquals(8, registry.size());
    }

    @Test
    void testStringMatchingFunctionsWithAviatorScript() {
        // Register functions
        registry.register(new ContainsFunction());
        registry.register(new StartsWithFunction());
        registry.register(new EndsWithFunction());
        registry.register(new RegexMatchFunction());
        registry.registerAll(aviator);

        Map<String, Object> env = new HashMap<>();

        // Test CONTAINS
        Object containsResult = aviator.execute("CONTAINS('hello world', 'world')", env);
        assertEquals(Boolean.TRUE, containsResult);

        Object containsResult2 = aviator.execute("CONTAINS('hello world', 'foo')", env);
        assertEquals(Boolean.FALSE, containsResult2);

        // Test STARTS_WITH
        Object startsWithResult = aviator.execute("STARTS_WITH('hello world', 'hello')", env);
        assertEquals(Boolean.TRUE, startsWithResult);

        Object startsWithResult2 = aviator.execute("STARTS_WITH('hello world', 'world')", env);
        assertEquals(Boolean.FALSE, startsWithResult2);

        // Test ENDS_WITH
        Object endsWithResult = aviator.execute("ENDS_WITH('hello world', 'world')", env);
        assertEquals(Boolean.TRUE, endsWithResult);

        Object endsWithResult2 = aviator.execute("ENDS_WITH('hello world', 'hello')", env);
        assertEquals(Boolean.FALSE, endsWithResult2);

        // Test REGEX_MATCH
        Object regexResult = aviator.execute("REGEX_MATCH('hello123', '\\\\w+')", env);
        assertEquals(Boolean.TRUE, regexResult);

        Object regexResult2 = aviator.execute("REGEX_MATCH('hello', '^[0-9]+$')", env);
        assertEquals(Boolean.FALSE, regexResult2);
    }

    @Test
    void testNestedStringExpressions() {
        // Register functions
        registry.register(new ContainsFunction());
        registry.register(new StartsWithFunction());
        registry.registerAll(aviator);

        Map<String, Object> env = new HashMap<>();

        // Test nested: CONTAINS('hello world', STARTS_WITH('hello world', 'hello') ? 'hello' : 'foo')
        // STARTS_WITH returns true, so we check CONTAINS('hello world', 'hello')
        Object result = aviator.execute(
            "CONTAINS('hello world', STARTS_WITH('hello world', 'hello') ? 'hello' : 'foo')", 
            env);
        assertEquals(Boolean.TRUE, result);
    }

    @Test
    void testStringMatchingWithVariables() {
        // Register functions
        registry.register(new ContainsFunction());
        registry.register(new StartsWithFunction());
        registry.register(new EndsWithFunction());
        registry.registerAll(aviator);

        Map<String, Object> env = new HashMap<>();
        env.put("text", "hello world");
        env.put("prefix", "hello");
        env.put("suffix", "world");
        env.put("substring", "lo wo");

        // Test with variables
        Object containsResult = aviator.execute("CONTAINS(text, substring)", env);
        assertEquals(Boolean.TRUE, containsResult);

        Object startsWithResult = aviator.execute("STARTS_WITH(text, prefix)", env);
        assertEquals(Boolean.TRUE, startsWithResult);

        Object endsWithResult = aviator.execute("ENDS_WITH(text, suffix)", env);
        assertEquals(Boolean.TRUE, endsWithResult);
    }

    @Test
    void testCaseSensitivity() {
        // Register functions
        registry.register(new ContainsFunction());
        registry.register(new StartsWithFunction());
        registry.register(new EndsWithFunction());
        registry.registerAll(aviator);

        Map<String, Object> env = new HashMap<>();

        // All string matching functions should be case-sensitive
        Object containsResult = aviator.execute("CONTAINS('Hello World', 'world')", env);
        assertEquals(Boolean.FALSE, containsResult);

        Object startsWithResult = aviator.execute("STARTS_WITH('Hello World', 'hello')", env);
        assertEquals(Boolean.FALSE, startsWithResult);

        Object endsWithResult = aviator.execute("ENDS_WITH('Hello World', 'WORLD')", env);
        assertEquals(Boolean.FALSE, endsWithResult);
    }

    @Test
    void testEmptyStringHandling() {
        // Register functions
        registry.register(new ContainsFunction());
        registry.register(new StartsWithFunction());
        registry.register(new EndsWithFunction());
        registry.registerAll(aviator);

        Map<String, Object> env = new HashMap<>();

        // Empty substring should be contained in any string
        Object containsResult = aviator.execute("CONTAINS('test', '')", env);
        assertEquals(Boolean.TRUE, containsResult);

        // Any string starts with empty string
        Object startsWithResult = aviator.execute("STARTS_WITH('test', '')", env);
        assertEquals(Boolean.TRUE, startsWithResult);

        // Any string ends with empty string
        Object endsWithResult = aviator.execute("ENDS_WITH('test', '')", env);
        assertEquals(Boolean.TRUE, endsWithResult);

        // Empty string should not contain non-empty substring
        Object containsResult2 = aviator.execute("CONTAINS('', 'test')", env);
        assertEquals(Boolean.FALSE, containsResult2);
    }

    @Test
    void testRegexMatchWithComplexPatterns() {
        // Register function
        registry.register(new RegexMatchFunction());
        registry.registerAll(aviator);

        Map<String, Object> env = new HashMap<>();

        // Email pattern
        Object emailResult = aviator.execute(
            "REGEX_MATCH('test@example.com', '^[\\\\w.]+@[\\\\w.]+$')", 
            env);
        assertEquals(Boolean.TRUE, emailResult);

        // Phone pattern
        Object phoneResult = aviator.execute(
            "REGEX_MATCH('123-456-7890', '^\\\\d{3}-\\\\d{3}-\\\\d{4}$')", 
            env);
        assertEquals(Boolean.TRUE, phoneResult);

        // URL pattern
        Object urlResult = aviator.execute(
            "REGEX_MATCH('https://example.com', '^https?://.*')", 
            env);
        assertEquals(Boolean.TRUE, urlResult);
    }

    @Test
    void testRegexMatchWithInvalidPattern() {
        // Register function
        registry.register(new RegexMatchFunction());
        registry.registerAll(aviator);

        Map<String, Object> env = new HashMap<>();

        // Invalid regex pattern should throw exception
        assertThrows(ExpressionRuntimeException.class, () -> {
            aviator.execute("REGEX_MATCH('test', '[invalid(')", env);
        });
    }

    @Test
    void testFunctionMetadata() {
        ContainsFunction contains = new ContainsFunction();
        StartsWithFunction startsWith = new StartsWithFunction();
        EndsWithFunction endsWith = new EndsWithFunction();
        RegexMatchFunction regexMatch = new RegexMatchFunction();

        // Verify metadata
        assertEquals("CONTAINS", contains.getFunctionMetadata().getName());
        assertEquals(2, contains.getFunctionMetadata().getMinArgs());
        assertEquals(2, contains.getFunctionMetadata().getMaxArgs());

        assertEquals("STARTS_WITH", startsWith.getFunctionMetadata().getName());
        assertEquals(2, startsWith.getFunctionMetadata().getMinArgs());
        assertEquals(2, startsWith.getFunctionMetadata().getMaxArgs());

        assertEquals("ENDS_WITH", endsWith.getFunctionMetadata().getName());
        assertEquals(2, endsWith.getFunctionMetadata().getMinArgs());
        assertEquals(2, endsWith.getFunctionMetadata().getMaxArgs());

        assertEquals("REGEX_MATCH", regexMatch.getFunctionMetadata().getName());
        assertEquals(2, regexMatch.getFunctionMetadata().getMinArgs());
        assertEquals(2, regexMatch.getFunctionMetadata().getMaxArgs());
    }

    @Test
    void testStringMatchingConsistency() {
        // Register functions
        registry.register(new ContainsFunction());
        registry.register(new StartsWithFunction());
        registry.register(new EndsWithFunction());
        registry.registerAll(aviator);

        Map<String, Object> env = new HashMap<>();

        // If STARTS_WITH is true, CONTAINS should also be true
        Object startsWithResult = aviator.execute("STARTS_WITH('hello world', 'hello')", env);
        Object containsResult = aviator.execute("CONTAINS('hello world', 'hello')", env);
        
        assertEquals(Boolean.TRUE, startsWithResult);
        assertEquals(Boolean.TRUE, containsResult);

        // If ENDS_WITH is true, CONTAINS should also be true
        Object endsWithResult = aviator.execute("ENDS_WITH('hello world', 'world')", env);
        Object containsResult2 = aviator.execute("CONTAINS('hello world', 'world')", env);
        
        assertEquals(Boolean.TRUE, endsWithResult);
        assertEquals(Boolean.TRUE, containsResult2);
    }

    @Test
    void testRegexEquivalenceToOtherFunctions() {
        // Register functions
        registry.register(new ContainsFunction());
        registry.register(new StartsWithFunction());
        registry.register(new EndsWithFunction());
        registry.register(new RegexMatchFunction());
        registry.registerAll(aviator);

        Map<String, Object> env = new HashMap<>();

        // REGEX_MATCH with ".*substring.*" should be equivalent to CONTAINS
        Object containsResult = aviator.execute("CONTAINS('hello world', 'world')", env);
        Object regexContainsResult = aviator.execute("REGEX_MATCH('hello world', '.*world.*')", env);
        assertEquals(containsResult, regexContainsResult);

        // REGEX_MATCH with "^prefix.*" should be equivalent to STARTS_WITH
        Object startsWithResult = aviator.execute("STARTS_WITH('hello world', 'hello')", env);
        Object regexStartsResult = aviator.execute("REGEX_MATCH('hello world', '^hello.*')", env);
        assertEquals(startsWithResult, regexStartsResult);

        // REGEX_MATCH with ".*suffix$" should be equivalent to ENDS_WITH
        Object endsWithResult = aviator.execute("ENDS_WITH('hello world', 'world')", env);
        Object regexEndsResult = aviator.execute("REGEX_MATCH('hello world', '.*world$')", env);
        assertEquals(endsWithResult, regexEndsResult);
    }

    @Test
    void testCombinedStringOperations() {
        // Register functions
        registry.register(new ContainsFunction());
        registry.register(new StartsWithFunction());
        registry.register(new EndsWithFunction());
        registry.registerAll(aviator);

        Map<String, Object> env = new HashMap<>();

        // Test: string starts with 'hello' AND ends with 'world'
        Object result = aviator.execute(
            "STARTS_WITH('hello world', 'hello') && ENDS_WITH('hello world', 'world')", 
            env);
        assertEquals(Boolean.TRUE, result);

        // Test: string contains 'lo' OR starts with 'foo'
        Object result2 = aviator.execute(
            "CONTAINS('hello world', 'lo') || STARTS_WITH('hello world', 'foo')", 
            env);
        assertEquals(Boolean.TRUE, result2);
    }
}

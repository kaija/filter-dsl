package com.filter.dsl.integration;

import com.filter.dsl.functions.FunctionRegistry;
import com.filter.dsl.functions.string.ReverseFunction;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test to verify extensibility by adding a custom function.
 * This test validates that the DSL framework allows easy addition of new functions.
 */
class CustomFunctionExtensibilityTest {

    private AviatorEvaluatorInstance aviator;
    private FunctionRegistry registry;

    @BeforeEach
    void setUp() {
        // Create a new AviatorScript instance
        aviator = AviatorEvaluator.newInstance();
        
        // Create registry and register the custom function
        registry = new FunctionRegistry();
        registry.register(new ReverseFunction());
        
        // Register all functions with AviatorScript
        registry.registerAll(aviator);
    }

    @Test
    void testCustomReverseFunction_BasicUsage() {
        // Test basic string reversal
        String expression = "REVERSE(\"hello\")";
        Object result = aviator.execute(expression);
        
        assertEquals("olleh", result);
    }

    @Test
    void testCustomReverseFunction_EmptyString() {
        // Test with empty string
        String expression = "REVERSE(\"\")";
        Object result = aviator.execute(expression);
        
        assertEquals("", result);
    }

    @Test
    void testCustomReverseFunction_SingleCharacter() {
        // Test with single character
        String expression = "REVERSE(\"a\")";
        Object result = aviator.execute(expression);
        
        assertEquals("a", result);
    }

    @Test
    void testCustomReverseFunction_WithNumbers() {
        // Test with numeric string
        String expression = "REVERSE(\"12345\")";
        Object result = aviator.execute(expression);
        
        assertEquals("54321", result);
    }

    @Test
    void testCustomReverseFunction_WithSpecialCharacters() {
        // Test with special characters
        String expression = "REVERSE(\"!@#$%\")";
        Object result = aviator.execute(expression);
        
        assertEquals("%$#@!", result);
    }

    @Test
    void testCustomReverseFunction_CombinedWithOtherFunctions() {
        // Test combining REVERSE with other functions
        // Note: This assumes UPPER function exists in the system
        // For this test, we'll just test REVERSE twice
        String expression = "REVERSE(REVERSE(\"test\"))";
        Object result = aviator.execute(expression);
        
        assertEquals("test", result);
    }

    @Test
    void testCustomReverseFunction_WithSpaces() {
        // Test with spaces
        String expression = "REVERSE(\"hello world\")";
        Object result = aviator.execute(expression);
        
        assertEquals("dlrow olleh", result);
    }

    @Test
    void testCustomReverseFunction_Palindrome() {
        // Test with palindrome
        String expression = "REVERSE(\"racecar\")";
        Object result = aviator.execute(expression);
        
        assertEquals("racecar", result);
    }

    @Test
    void testFunctionMetadata() {
        // Verify the function is properly registered
        ReverseFunction function = new ReverseFunction();
        
        assertEquals("REVERSE", function.getName());
        assertNotNull(function.getFunctionMetadata());
        assertEquals("REVERSE", function.getFunctionMetadata().getName());
        assertEquals(1, function.getFunctionMetadata().getMinArgs());
        assertEquals(1, function.getFunctionMetadata().getMaxArgs());
    }

    @Test
    void testExtensibilityPattern() {
        // Verify that the function can be discovered and registered
        FunctionRegistry newRegistry = new FunctionRegistry();
        
        // Discover functions in the string package
        newRegistry.discoverAndRegister("com.filter.dsl.functions.string");
        
        // Verify REVERSE was discovered
        assertTrue(newRegistry.hasFunction("REVERSE"));
        
        // Register with a new Aviator instance
        AviatorEvaluatorInstance newAviator = AviatorEvaluator.newInstance();
        newRegistry.registerAll(newAviator);
        
        // Test that it works
        Object result = newAviator.execute("REVERSE(\"DSL\")");
        assertEquals("LSD", result);
    }
}

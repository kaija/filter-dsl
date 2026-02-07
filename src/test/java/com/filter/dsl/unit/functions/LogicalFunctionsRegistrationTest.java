package com.filter.dsl.unit.functions;

import com.filter.dsl.functions.FunctionRegistry;
import com.filter.dsl.functions.logical.LogicalAndFunction;
import com.filter.dsl.functions.logical.LogicalNotFunction;
import com.filter.dsl.functions.logical.LogicalOrFunction;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for logical functions with FunctionRegistry and AviatorScript
 */
class LogicalFunctionsRegistrationTest {

    private FunctionRegistry registry;
    private AviatorEvaluatorInstance aviator;

    @BeforeEach
    void setUp() {
        registry = new FunctionRegistry();
        aviator = AviatorEvaluator.newInstance();
    }

    @Test
    void testRegisterLogicalFunctions() {
        // Register all logical functions
        registry.register(new LogicalAndFunction());
        registry.register(new LogicalOrFunction());
        registry.register(new LogicalNotFunction());

        // Verify registration
        assertTrue(registry.hasFunction("AND"));
        assertTrue(registry.hasFunction("OR"));
        assertTrue(registry.hasFunction("NOT"));
        assertEquals(3, registry.size());
    }

    @Test
    void testLogicalFunctionsWithAviatorScript() {
        // Register functions
        registry.register(new LogicalAndFunction());
        registry.register(new LogicalOrFunction());
        registry.register(new LogicalNotFunction());
        registry.registerAll(aviator);

        Map<String, Object> env = new HashMap<>();

        // Test AND
        Object andResult = aviator.execute("AND(true, true)", env);
        assertEquals(Boolean.TRUE, andResult);

        Object andResult2 = aviator.execute("AND(true, false)", env);
        assertEquals(Boolean.FALSE, andResult2);

        // Test OR
        Object orResult = aviator.execute("OR(true, false)", env);
        assertEquals(Boolean.TRUE, orResult);

        Object orResult2 = aviator.execute("OR(false, false)", env);
        assertEquals(Boolean.FALSE, orResult2);

        // Test NOT
        Object notResult = aviator.execute("NOT(true)", env);
        assertEquals(Boolean.FALSE, notResult);

        Object notResult2 = aviator.execute("NOT(false)", env);
        assertEquals(Boolean.TRUE, notResult2);
    }

    @Test
    void testNestedLogicalExpressions() {
        // Register functions
        registry.register(new LogicalAndFunction());
        registry.register(new LogicalOrFunction());
        registry.register(new LogicalNotFunction());
        registry.registerAll(aviator);

        Map<String, Object> env = new HashMap<>();

        // Test AND(OR(true, false), NOT(false))
        // = AND(true, true) = true
        Object result = aviator.execute("AND(OR(true, false), NOT(false))", env);
        assertEquals(Boolean.TRUE, result);

        // Test OR(AND(true, false), NOT(true))
        // = OR(false, false) = false
        Object result2 = aviator.execute("OR(AND(true, false), NOT(true))", env);
        assertEquals(Boolean.FALSE, result2);

        // Test NOT(AND(true, false))
        // = NOT(false) = true
        Object result3 = aviator.execute("NOT(AND(true, false))", env);
        assertEquals(Boolean.TRUE, result3);
    }

    @Test
    void testMultipleArgumentsWithAviatorScript() {
        // Register functions
        registry.register(new LogicalAndFunction());
        registry.register(new LogicalOrFunction());
        registry.registerAll(aviator);

        Map<String, Object> env = new HashMap<>();

        // Test AND with 3 arguments
        Object andResult = aviator.execute("AND(true, true, true)", env);
        assertEquals(Boolean.TRUE, andResult);

        Object andResult2 = aviator.execute("AND(true, true, false)", env);
        assertEquals(Boolean.FALSE, andResult2);

        // Test OR with 3 arguments
        Object orResult = aviator.execute("OR(false, false, true)", env);
        assertEquals(Boolean.TRUE, orResult);

        Object orResult2 = aviator.execute("OR(false, false, false)", env);
        assertEquals(Boolean.FALSE, orResult2);
    }

    @Test
    void testFunctionMetadata() {
        LogicalAndFunction and = new LogicalAndFunction();
        LogicalOrFunction or = new LogicalOrFunction();
        LogicalNotFunction not = new LogicalNotFunction();

        // Verify metadata
        assertEquals("AND", and.getFunctionMetadata().getName());
        assertEquals(2, and.getFunctionMetadata().getMinArgs());

        assertEquals("OR", or.getFunctionMetadata().getName());
        assertEquals(2, or.getFunctionMetadata().getMinArgs());

        assertEquals("NOT", not.getFunctionMetadata().getName());
        assertEquals(1, not.getFunctionMetadata().getMinArgs());
        assertEquals(1, not.getFunctionMetadata().getMaxArgs());
    }
}

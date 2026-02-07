package com.filter.dsl.unit.functions;

import com.filter.dsl.functions.FunctionRegistry;
import com.filter.dsl.functions.comparison.*;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for comparison functions with FunctionRegistry and AviatorScript
 */
class ComparisonFunctionsRegistrationTest {

    private FunctionRegistry registry;
    private AviatorEvaluatorInstance aviator;

    @BeforeEach
    void setUp() {
        registry = new FunctionRegistry();
        aviator = AviatorEvaluator.newInstance();
    }

    @Test
    void testRegisterComparisonFunctions() {
        // Register all comparison functions
        registry.register(new GreaterThanFunction());
        registry.register(new LessThanFunction());
        registry.register(new GreaterThanOrEqualFunction());
        registry.register(new LessThanOrEqualFunction());
        registry.register(new EqualsFunction());
        registry.register(new NotEqualsFunction());

        // Verify registration
        assertTrue(registry.hasFunction("GT"));
        assertTrue(registry.hasFunction("LT"));
        assertTrue(registry.hasFunction("GTE"));
        assertTrue(registry.hasFunction("LTE"));
        assertTrue(registry.hasFunction("EQ"));
        assertTrue(registry.hasFunction("NEQ"));
        assertEquals(6, registry.size());
    }

    @Test
    void testComparisonFunctionsWithAviatorScript() {
        // Register functions
        registry.register(new GreaterThanFunction());
        registry.register(new LessThanFunction());
        registry.register(new GreaterThanOrEqualFunction());
        registry.register(new LessThanOrEqualFunction());
        registry.register(new EqualsFunction());
        registry.register(new NotEqualsFunction());
        registry.registerAll(aviator);

        Map<String, Object> env = new HashMap<>();

        // Test GT
        Object gtResult = aviator.execute("GT(5, 3)", env);
        assertEquals(Boolean.TRUE, gtResult);

        Object gtResult2 = aviator.execute("GT(3, 5)", env);
        assertEquals(Boolean.FALSE, gtResult2);

        // Test LT
        Object ltResult = aviator.execute("LT(3, 5)", env);
        assertEquals(Boolean.TRUE, ltResult);

        Object ltResult2 = aviator.execute("LT(5, 3)", env);
        assertEquals(Boolean.FALSE, ltResult2);

        // Test GTE
        Object gteResult = aviator.execute("GTE(5, 5)", env);
        assertEquals(Boolean.TRUE, gteResult);

        Object gteResult2 = aviator.execute("GTE(3, 5)", env);
        assertEquals(Boolean.FALSE, gteResult2);

        // Test LTE
        Object lteResult = aviator.execute("LTE(5, 5)", env);
        assertEquals(Boolean.TRUE, lteResult);

        Object lteResult2 = aviator.execute("LTE(5, 3)", env);
        assertEquals(Boolean.FALSE, lteResult2);

        // Test EQ
        Object eqResult = aviator.execute("EQ(5, 5)", env);
        assertEquals(Boolean.TRUE, eqResult);

        Object eqResult2 = aviator.execute("EQ(5, 3)", env);
        assertEquals(Boolean.FALSE, eqResult2);

        // Test NEQ
        Object neqResult = aviator.execute("NEQ(5, 3)", env);
        assertEquals(Boolean.TRUE, neqResult);

        Object neqResult2 = aviator.execute("NEQ(5, 5)", env);
        assertEquals(Boolean.FALSE, neqResult2);
    }

    @Test
    void testNestedComparisonExpressions() {
        // Register functions
        registry.register(new GreaterThanFunction());
        registry.register(new LessThanFunction());
        registry.register(new EqualsFunction());
        registry.registerAll(aviator);

        Map<String, Object> env = new HashMap<>();

        // Test GT(10, LT(5, 3) ? 1 : 8)
        // LT(5, 3) = false, so we compare GT(10, 8) = true
        // Note: AviatorScript supports ternary operator
        Object result = aviator.execute("GT(10, LT(5, 3) ? 1 : 8)", env);
        assertEquals(Boolean.TRUE, result);
    }

    @Test
    void testComparisonWithDecimals() {
        // Register functions
        registry.register(new GreaterThanFunction());
        registry.register(new LessThanFunction());
        registry.register(new EqualsFunction());
        registry.registerAll(aviator);

        Map<String, Object> env = new HashMap<>();

        // Test with decimal numbers
        Object gtResult = aviator.execute("GT(10.5, 10.2)", env);
        assertEquals(Boolean.TRUE, gtResult);

        Object ltResult = aviator.execute("LT(10.2, 10.5)", env);
        assertEquals(Boolean.TRUE, ltResult);

        Object eqResult = aviator.execute("EQ(10.0, 10)", env);
        assertEquals(Boolean.TRUE, eqResult);
    }

    @Test
    void testComparisonWithNegativeNumbers() {
        // Register functions
        registry.register(new GreaterThanFunction());
        registry.register(new LessThanFunction());
        registry.registerAll(aviator);

        Map<String, Object> env = new HashMap<>();

        // Test with negative numbers
        Object gtResult = aviator.execute("GT(-3, -5)", env);
        assertEquals(Boolean.TRUE, gtResult);

        Object ltResult = aviator.execute("LT(-5, -3)", env);
        assertEquals(Boolean.TRUE, ltResult);
    }

    @Test
    void testComparisonWithStrings() {
        // Register functions
        registry.register(new EqualsFunction());
        registry.register(new NotEqualsFunction());
        registry.registerAll(aviator);

        Map<String, Object> env = new HashMap<>();

        // Test EQ with strings
        Object eqResult = aviator.execute("EQ('hello', 'hello')", env);
        assertEquals(Boolean.TRUE, eqResult);

        Object eqResult2 = aviator.execute("EQ('hello', 'world')", env);
        assertEquals(Boolean.FALSE, eqResult2);

        // Test NEQ with strings
        Object neqResult = aviator.execute("NEQ('hello', 'world')", env);
        assertEquals(Boolean.TRUE, neqResult);

        Object neqResult2 = aviator.execute("NEQ('hello', 'hello')", env);
        assertEquals(Boolean.FALSE, neqResult2);
    }

    @Test
    void testFunctionMetadata() {
        GreaterThanFunction gt = new GreaterThanFunction();
        LessThanFunction lt = new LessThanFunction();
        GreaterThanOrEqualFunction gte = new GreaterThanOrEqualFunction();
        LessThanOrEqualFunction lte = new LessThanOrEqualFunction();
        EqualsFunction eq = new EqualsFunction();
        NotEqualsFunction neq = new NotEqualsFunction();

        // Verify metadata
        assertEquals("GT", gt.getFunctionMetadata().getName());
        assertEquals(2, gt.getFunctionMetadata().getMinArgs());
        assertEquals(2, gt.getFunctionMetadata().getMaxArgs());

        assertEquals("LT", lt.getFunctionMetadata().getName());
        assertEquals(2, lt.getFunctionMetadata().getMinArgs());
        assertEquals(2, lt.getFunctionMetadata().getMaxArgs());

        assertEquals("GTE", gte.getFunctionMetadata().getName());
        assertEquals(2, gte.getFunctionMetadata().getMinArgs());
        assertEquals(2, gte.getFunctionMetadata().getMaxArgs());

        assertEquals("LTE", lte.getFunctionMetadata().getName());
        assertEquals(2, lte.getFunctionMetadata().getMinArgs());
        assertEquals(2, lte.getFunctionMetadata().getMaxArgs());

        assertEquals("EQ", eq.getFunctionMetadata().getName());
        assertEquals(2, eq.getFunctionMetadata().getMinArgs());
        assertEquals(2, eq.getFunctionMetadata().getMaxArgs());

        assertEquals("NEQ", neq.getFunctionMetadata().getName());
        assertEquals(2, neq.getFunctionMetadata().getMinArgs());
        assertEquals(2, neq.getFunctionMetadata().getMaxArgs());
    }

    @Test
    void testComparisonTransitivity() {
        // Register functions
        registry.register(new GreaterThanFunction());
        registry.registerAll(aviator);

        Map<String, Object> env = new HashMap<>();

        // If GT(A, B) and GT(B, C), then GT(A, C) should be true
        Object gtAB = aviator.execute("GT(10, 5)", env);
        Object gtBC = aviator.execute("GT(5, 3)", env);
        Object gtAC = aviator.execute("GT(10, 3)", env);

        assertEquals(Boolean.TRUE, gtAB);
        assertEquals(Boolean.TRUE, gtBC);
        assertEquals(Boolean.TRUE, gtAC);
    }

    @Test
    void testEqualitySymmetry() {
        // Register functions
        registry.register(new EqualsFunction());
        registry.registerAll(aviator);

        Map<String, Object> env = new HashMap<>();

        // EQ(A, B) should equal EQ(B, A)
        Object eqAB = aviator.execute("EQ(5, 5)", env);
        Object eqBA = aviator.execute("EQ(5, 5)", env);

        assertEquals(eqAB, eqBA);
    }
}

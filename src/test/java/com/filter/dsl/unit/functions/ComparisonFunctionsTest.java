package com.filter.dsl.unit.functions;

import com.filter.dsl.functions.FunctionArgumentException;
import com.filter.dsl.functions.TypeMismatchException;
import com.filter.dsl.functions.comparison.*;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorDouble;
import com.googlecode.aviator.runtime.type.AviatorLong;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for comparison functions: GT, LT, GTE, LTE, EQ, NEQ
 */
class ComparisonFunctionsTest {

    private final Map<String, Object> env = new HashMap<>();

    // ========== GT (Greater Than) Function Tests ==========

    @Test
    void testGtWithFirstGreater() {
        GreaterThanFunction gt = new GreaterThanFunction();
        AviatorObject result = gt.call(env, AviatorLong.valueOf(5), AviatorLong.valueOf(3));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testGtWithFirstLess() {
        GreaterThanFunction gt = new GreaterThanFunction();
        AviatorObject result = gt.call(env, AviatorLong.valueOf(3), AviatorLong.valueOf(5));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testGtWithEqual() {
        GreaterThanFunction gt = new GreaterThanFunction();
        AviatorObject result = gt.call(env, AviatorLong.valueOf(5), AviatorLong.valueOf(5));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testGtWithDecimals() {
        GreaterThanFunction gt = new GreaterThanFunction();
        AviatorObject result = gt.call(env, AviatorDouble.valueOf(10.5), AviatorDouble.valueOf(10.2));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testGtWithMixedTypes() {
        GreaterThanFunction gt = new GreaterThanFunction();
        AviatorObject result = gt.call(env, AviatorDouble.valueOf(10.5), AviatorLong.valueOf(10));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testGtWithNegativeNumbers() {
        GreaterThanFunction gt = new GreaterThanFunction();
        AviatorObject result = gt.call(env, AviatorLong.valueOf(-3), AviatorLong.valueOf(-5));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testGtWithWrongArgumentCount() {
        GreaterThanFunction gt = new GreaterThanFunction();
        assertThrows(IllegalArgumentException.class, () -> {
            gt.call(env, AviatorLong.valueOf(5));
        });
    }

    @Test
    void testGtWithNonNumericArgument() {
        GreaterThanFunction gt = new GreaterThanFunction();
        assertThrows(TypeMismatchException.class, () -> {
            gt.call(env, AviatorLong.valueOf(5), new AviatorString("not a number"));
        });
    }

    @Test
    void testGtMetadata() {
        GreaterThanFunction gt = new GreaterThanFunction();
        assertEquals("GT", gt.getName());
        assertEquals(2, gt.getFunctionMetadata().getMinArgs());
        assertEquals(2, gt.getFunctionMetadata().getMaxArgs());
    }

    // ========== LT (Less Than) Function Tests ==========

    @Test
    void testLtWithFirstLess() {
        LessThanFunction lt = new LessThanFunction();
        AviatorObject result = lt.call(env, AviatorLong.valueOf(3), AviatorLong.valueOf(5));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testLtWithFirstGreater() {
        LessThanFunction lt = new LessThanFunction();
        AviatorObject result = lt.call(env, AviatorLong.valueOf(5), AviatorLong.valueOf(3));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testLtWithEqual() {
        LessThanFunction lt = new LessThanFunction();
        AviatorObject result = lt.call(env, AviatorLong.valueOf(5), AviatorLong.valueOf(5));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testLtWithDecimals() {
        LessThanFunction lt = new LessThanFunction();
        AviatorObject result = lt.call(env, AviatorDouble.valueOf(10.2), AviatorDouble.valueOf(10.5));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testLtWithMixedTypes() {
        LessThanFunction lt = new LessThanFunction();
        AviatorObject result = lt.call(env, AviatorLong.valueOf(10), AviatorDouble.valueOf(10.5));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testLtWithNegativeNumbers() {
        LessThanFunction lt = new LessThanFunction();
        AviatorObject result = lt.call(env, AviatorLong.valueOf(-5), AviatorLong.valueOf(-3));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testLtWithWrongArgumentCount() {
        LessThanFunction lt = new LessThanFunction();
        assertThrows(IllegalArgumentException.class, () -> {
            lt.call(env, AviatorLong.valueOf(5));
        });
    }

    @Test
    void testLtWithNonNumericArgument() {
        LessThanFunction lt = new LessThanFunction();
        assertThrows(TypeMismatchException.class, () -> {
            lt.call(env, new AviatorString("not a number"), AviatorLong.valueOf(5));
        });
    }

    @Test
    void testLtMetadata() {
        LessThanFunction lt = new LessThanFunction();
        assertEquals("LT", lt.getName());
        assertEquals(2, lt.getFunctionMetadata().getMinArgs());
        assertEquals(2, lt.getFunctionMetadata().getMaxArgs());
    }

    // ========== GTE (Greater Than or Equal) Function Tests ==========

    @Test
    void testGteWithFirstGreater() {
        GreaterThanOrEqualFunction gte = new GreaterThanOrEqualFunction();
        AviatorObject result = gte.call(env, AviatorLong.valueOf(5), AviatorLong.valueOf(3));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testGteWithEqual() {
        GreaterThanOrEqualFunction gte = new GreaterThanOrEqualFunction();
        AviatorObject result = gte.call(env, AviatorLong.valueOf(5), AviatorLong.valueOf(5));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testGteWithFirstLess() {
        GreaterThanOrEqualFunction gte = new GreaterThanOrEqualFunction();
        AviatorObject result = gte.call(env, AviatorLong.valueOf(3), AviatorLong.valueOf(5));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testGteWithDecimals() {
        GreaterThanOrEqualFunction gte = new GreaterThanOrEqualFunction();
        AviatorObject result = gte.call(env, AviatorDouble.valueOf(10.5), AviatorDouble.valueOf(10.5));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testGteWithMixedTypes() {
        GreaterThanOrEqualFunction gte = new GreaterThanOrEqualFunction();
        AviatorObject result = gte.call(env, AviatorDouble.valueOf(10.0), AviatorLong.valueOf(10));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testGteWithWrongArgumentCount() {
        GreaterThanOrEqualFunction gte = new GreaterThanOrEqualFunction();
        assertThrows(IllegalArgumentException.class, () -> {
            gte.call(env, AviatorLong.valueOf(5));
        });
    }

    @Test
    void testGteWithNonNumericArgument() {
        GreaterThanOrEqualFunction gte = new GreaterThanOrEqualFunction();
        assertThrows(TypeMismatchException.class, () -> {
            gte.call(env, AviatorLong.valueOf(5), new AviatorString("not a number"));
        });
    }

    @Test
    void testGteMetadata() {
        GreaterThanOrEqualFunction gte = new GreaterThanOrEqualFunction();
        assertEquals("GTE", gte.getName());
        assertEquals(2, gte.getFunctionMetadata().getMinArgs());
        assertEquals(2, gte.getFunctionMetadata().getMaxArgs());
    }

    // ========== LTE (Less Than or Equal) Function Tests ==========

    @Test
    void testLteWithFirstLess() {
        LessThanOrEqualFunction lte = new LessThanOrEqualFunction();
        AviatorObject result = lte.call(env, AviatorLong.valueOf(3), AviatorLong.valueOf(5));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testLteWithEqual() {
        LessThanOrEqualFunction lte = new LessThanOrEqualFunction();
        AviatorObject result = lte.call(env, AviatorLong.valueOf(5), AviatorLong.valueOf(5));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testLteWithFirstGreater() {
        LessThanOrEqualFunction lte = new LessThanOrEqualFunction();
        AviatorObject result = lte.call(env, AviatorLong.valueOf(5), AviatorLong.valueOf(3));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testLteWithDecimals() {
        LessThanOrEqualFunction lte = new LessThanOrEqualFunction();
        AviatorObject result = lte.call(env, AviatorDouble.valueOf(10.2), AviatorDouble.valueOf(10.2));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testLteWithMixedTypes() {
        LessThanOrEqualFunction lte = new LessThanOrEqualFunction();
        AviatorObject result = lte.call(env, AviatorLong.valueOf(10), AviatorDouble.valueOf(10.0));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testLteWithWrongArgumentCount() {
        LessThanOrEqualFunction lte = new LessThanOrEqualFunction();
        assertThrows(IllegalArgumentException.class, () -> {
            lte.call(env, AviatorLong.valueOf(5));
        });
    }

    @Test
    void testLteWithNonNumericArgument() {
        LessThanOrEqualFunction lte = new LessThanOrEqualFunction();
        assertThrows(TypeMismatchException.class, () -> {
            lte.call(env, new AviatorString("not a number"), AviatorLong.valueOf(5));
        });
    }

    @Test
    void testLteMetadata() {
        LessThanOrEqualFunction lte = new LessThanOrEqualFunction();
        assertEquals("LTE", lte.getName());
        assertEquals(2, lte.getFunctionMetadata().getMinArgs());
        assertEquals(2, lte.getFunctionMetadata().getMaxArgs());
    }

    // ========== EQ (Equals) Function Tests ==========

    @Test
    void testEqWithEqualIntegers() {
        EqualsFunction eq = new EqualsFunction();
        AviatorObject result = eq.call(env, AviatorLong.valueOf(5), AviatorLong.valueOf(5));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testEqWithUnequalIntegers() {
        EqualsFunction eq = new EqualsFunction();
        AviatorObject result = eq.call(env, AviatorLong.valueOf(5), AviatorLong.valueOf(3));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testEqWithEqualStrings() {
        EqualsFunction eq = new EqualsFunction();
        AviatorObject result = eq.call(env, new AviatorString("hello"), new AviatorString("hello"));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testEqWithUnequalStrings() {
        EqualsFunction eq = new EqualsFunction();
        AviatorObject result = eq.call(env, new AviatorString("hello"), new AviatorString("world"));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testEqWithEqualBooleans() {
        EqualsFunction eq = new EqualsFunction();
        AviatorObject result = eq.call(env, AviatorBoolean.TRUE, AviatorBoolean.TRUE);
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testEqWithUnequalBooleans() {
        EqualsFunction eq = new EqualsFunction();
        AviatorObject result = eq.call(env, AviatorBoolean.TRUE, AviatorBoolean.FALSE);
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testEqWithMixedNumericTypes() {
        EqualsFunction eq = new EqualsFunction();
        AviatorObject result = eq.call(env, AviatorDouble.valueOf(10.0), AviatorLong.valueOf(10));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testEqWithDecimals() {
        EqualsFunction eq = new EqualsFunction();
        AviatorObject result = eq.call(env, AviatorDouble.valueOf(10.5), AviatorDouble.valueOf(10.5));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testEqWithWrongArgumentCount() {
        EqualsFunction eq = new EqualsFunction();
        assertThrows(IllegalArgumentException.class, () -> {
            eq.call(env, AviatorLong.valueOf(5));
        });
    }

    @Test
    void testEqMetadata() {
        EqualsFunction eq = new EqualsFunction();
        assertEquals("EQ", eq.getName());
        assertEquals(2, eq.getFunctionMetadata().getMinArgs());
        assertEquals(2, eq.getFunctionMetadata().getMaxArgs());
    }

    // ========== NEQ (Not Equals) Function Tests ==========

    @Test
    void testNeqWithUnequalIntegers() {
        NotEqualsFunction neq = new NotEqualsFunction();
        AviatorObject result = neq.call(env, AviatorLong.valueOf(5), AviatorLong.valueOf(3));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testNeqWithEqualIntegers() {
        NotEqualsFunction neq = new NotEqualsFunction();
        AviatorObject result = neq.call(env, AviatorLong.valueOf(5), AviatorLong.valueOf(5));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testNeqWithUnequalStrings() {
        NotEqualsFunction neq = new NotEqualsFunction();
        AviatorObject result = neq.call(env, new AviatorString("hello"), new AviatorString("world"));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testNeqWithEqualStrings() {
        NotEqualsFunction neq = new NotEqualsFunction();
        AviatorObject result = neq.call(env, new AviatorString("hello"), new AviatorString("hello"));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testNeqWithUnequalBooleans() {
        NotEqualsFunction neq = new NotEqualsFunction();
        AviatorObject result = neq.call(env, AviatorBoolean.TRUE, AviatorBoolean.FALSE);
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testNeqWithEqualBooleans() {
        NotEqualsFunction neq = new NotEqualsFunction();
        AviatorObject result = neq.call(env, AviatorBoolean.TRUE, AviatorBoolean.TRUE);
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testNeqWithMixedNumericTypes() {
        NotEqualsFunction neq = new NotEqualsFunction();
        AviatorObject result = neq.call(env, AviatorDouble.valueOf(10.0), AviatorLong.valueOf(10));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testNeqWithDecimals() {
        NotEqualsFunction neq = new NotEqualsFunction();
        AviatorObject result = neq.call(env, AviatorDouble.valueOf(10.5), AviatorDouble.valueOf(10.2));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testNeqWithWrongArgumentCount() {
        NotEqualsFunction neq = new NotEqualsFunction();
        assertThrows(IllegalArgumentException.class, () -> {
            neq.call(env, AviatorLong.valueOf(5));
        });
    }

    @Test
    void testNeqMetadata() {
        NotEqualsFunction neq = new NotEqualsFunction();
        assertEquals("NEQ", neq.getName());
        assertEquals(2, neq.getFunctionMetadata().getMinArgs());
        assertEquals(2, neq.getFunctionMetadata().getMaxArgs());
    }

    // ========== Integration Tests ==========

    @Test
    void testComparisonTransitivity() {
        // If GT(A, B) and GT(B, C), then GT(A, C) should be true
        GreaterThanFunction gt = new GreaterThanFunction();
        
        AviatorLong a = AviatorLong.valueOf(10);
        AviatorLong b = AviatorLong.valueOf(5);
        AviatorLong c = AviatorLong.valueOf(3);
        
        AviatorObject gtAB = gt.call(env, a, b);
        AviatorObject gtBC = gt.call(env, b, c);
        AviatorObject gtAC = gt.call(env, a, c);
        
        assertEquals(Boolean.TRUE, gtAB.getValue(env));
        assertEquals(Boolean.TRUE, gtBC.getValue(env));
        assertEquals(Boolean.TRUE, gtAC.getValue(env));
    }

    @Test
    void testEqualitySymmetry() {
        // EQ(A, B) should equal EQ(B, A)
        EqualsFunction eq = new EqualsFunction();
        
        AviatorLong a = AviatorLong.valueOf(5);
        AviatorLong b = AviatorLong.valueOf(5);
        
        AviatorObject eqAB = eq.call(env, a, b);
        AviatorObject eqBA = eq.call(env, b, a);
        
        assertEquals(eqAB.getValue(env), eqBA.getValue(env));
    }

    @Test
    void testNeqEquivalentToNotEq() {
        // NEQ(A, B) should equal NOT(EQ(A, B))
        EqualsFunction eq = new EqualsFunction();
        NotEqualsFunction neq = new NotEqualsFunction();
        
        AviatorLong a = AviatorLong.valueOf(5);
        AviatorLong b = AviatorLong.valueOf(3);
        
        AviatorObject neqResult = neq.call(env, a, b);
        AviatorObject eqResult = eq.call(env, a, b);
        
        // NEQ should be true, EQ should be false
        assertEquals(Boolean.TRUE, neqResult.getValue(env));
        assertEquals(Boolean.FALSE, eqResult.getValue(env));
    }

    @Test
    void testGteEquivalentToGtOrEq() {
        // GTE(A, B) should be true if GT(A, B) OR EQ(A, B)
        GreaterThanFunction gt = new GreaterThanFunction();
        GreaterThanOrEqualFunction gte = new GreaterThanOrEqualFunction();
        EqualsFunction eq = new EqualsFunction();
        
        AviatorLong a = AviatorLong.valueOf(5);
        AviatorLong b = AviatorLong.valueOf(5);
        
        AviatorObject gteResult = gte.call(env, a, b);
        AviatorObject gtResult = gt.call(env, a, b);
        AviatorObject eqResult = eq.call(env, a, b);
        
        // GTE should be true, GT should be false, EQ should be true
        assertEquals(Boolean.TRUE, gteResult.getValue(env));
        assertEquals(Boolean.FALSE, gtResult.getValue(env));
        assertEquals(Boolean.TRUE, eqResult.getValue(env));
    }

    @Test
    void testLteEquivalentToLtOrEq() {
        // LTE(A, B) should be true if LT(A, B) OR EQ(A, B)
        LessThanFunction lt = new LessThanFunction();
        LessThanOrEqualFunction lte = new LessThanOrEqualFunction();
        EqualsFunction eq = new EqualsFunction();
        
        AviatorLong a = AviatorLong.valueOf(5);
        AviatorLong b = AviatorLong.valueOf(5);
        
        AviatorObject lteResult = lte.call(env, a, b);
        AviatorObject ltResult = lt.call(env, a, b);
        AviatorObject eqResult = eq.call(env, a, b);
        
        // LTE should be true, LT should be false, EQ should be true
        assertEquals(Boolean.TRUE, lteResult.getValue(env));
        assertEquals(Boolean.FALSE, ltResult.getValue(env));
        assertEquals(Boolean.TRUE, eqResult.getValue(env));
    }
}

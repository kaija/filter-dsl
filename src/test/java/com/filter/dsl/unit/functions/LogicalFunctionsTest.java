package com.filter.dsl.unit.functions;

import com.filter.dsl.functions.FunctionArgumentException;
import com.filter.dsl.functions.TypeMismatchException;
import com.filter.dsl.functions.logical.LogicalAndFunction;
import com.filter.dsl.functions.logical.LogicalNotFunction;
import com.filter.dsl.functions.logical.LogicalOrFunction;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for logical functions: AND, OR, NOT
 */
class LogicalFunctionsTest {

    private final Map<String, Object> env = new HashMap<>();

    // ========== AND Function Tests ==========

    @Test
    void testAndWithTwoTrueValues() {
        LogicalAndFunction and = new LogicalAndFunction();
        AviatorObject result = and.call(env, AviatorBoolean.TRUE, AviatorBoolean.TRUE);
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testAndWithTrueFalse() {
        LogicalAndFunction and = new LogicalAndFunction();
        AviatorObject result = and.call(env, AviatorBoolean.TRUE, AviatorBoolean.FALSE);
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testAndWithFalseTrue() {
        LogicalAndFunction and = new LogicalAndFunction();
        AviatorObject result = and.call(env, AviatorBoolean.FALSE, AviatorBoolean.TRUE);
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testAndWithTwoFalseValues() {
        LogicalAndFunction and = new LogicalAndFunction();
        AviatorObject result = and.call(env, AviatorBoolean.FALSE, AviatorBoolean.FALSE);
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testAndWithMultipleTrueValues() {
        LogicalAndFunction and = new LogicalAndFunction();
        AviatorObject result = and.call(env, 
            AviatorBoolean.TRUE, 
            AviatorBoolean.TRUE, 
            AviatorBoolean.TRUE
        );
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testAndWithMultipleValuesOneFalse() {
        LogicalAndFunction and = new LogicalAndFunction();
        AviatorObject result = and.call(env, 
            AviatorBoolean.TRUE, 
            AviatorBoolean.TRUE, 
            AviatorBoolean.FALSE
        );
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testAndWithInsufficientArguments() {
        LogicalAndFunction and = new LogicalAndFunction();
        assertThrows(FunctionArgumentException.class, () -> {
            and.call(env, AviatorBoolean.TRUE);
        });
    }

    @Test
    void testAndWithNonBooleanArgument() {
        LogicalAndFunction and = new LogicalAndFunction();
        assertThrows(TypeMismatchException.class, () -> {
            and.call(env, AviatorBoolean.TRUE, new AviatorString("not a boolean"));
        });
    }

    @Test
    void testAndMetadata() {
        LogicalAndFunction and = new LogicalAndFunction();
        assertEquals("AND", and.getName());
        assertEquals(2, and.getFunctionMetadata().getMinArgs());
        assertEquals(Integer.MAX_VALUE, and.getFunctionMetadata().getMaxArgs());
    }

    // ========== OR Function Tests ==========

    @Test
    void testOrWithTwoTrueValues() {
        LogicalOrFunction or = new LogicalOrFunction();
        AviatorObject result = or.call(env, AviatorBoolean.TRUE, AviatorBoolean.TRUE);
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testOrWithTrueFalse() {
        LogicalOrFunction or = new LogicalOrFunction();
        AviatorObject result = or.call(env, AviatorBoolean.TRUE, AviatorBoolean.FALSE);
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testOrWithFalseTrue() {
        LogicalOrFunction or = new LogicalOrFunction();
        AviatorObject result = or.call(env, AviatorBoolean.FALSE, AviatorBoolean.TRUE);
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testOrWithTwoFalseValues() {
        LogicalOrFunction or = new LogicalOrFunction();
        AviatorObject result = or.call(env, AviatorBoolean.FALSE, AviatorBoolean.FALSE);
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testOrWithMultipleFalseValuesOneTrue() {
        LogicalOrFunction or = new LogicalOrFunction();
        AviatorObject result = or.call(env, 
            AviatorBoolean.FALSE, 
            AviatorBoolean.FALSE, 
            AviatorBoolean.TRUE
        );
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testOrWithMultipleFalseValues() {
        LogicalOrFunction or = new LogicalOrFunction();
        AviatorObject result = or.call(env, 
            AviatorBoolean.FALSE, 
            AviatorBoolean.FALSE, 
            AviatorBoolean.FALSE
        );
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testOrWithInsufficientArguments() {
        LogicalOrFunction or = new LogicalOrFunction();
        assertThrows(FunctionArgumentException.class, () -> {
            or.call(env, AviatorBoolean.TRUE);
        });
    }

    @Test
    void testOrWithNonBooleanArgument() {
        LogicalOrFunction or = new LogicalOrFunction();
        assertThrows(TypeMismatchException.class, () -> {
            or.call(env, AviatorBoolean.FALSE, new AviatorString("not a boolean"));
        });
    }

    @Test
    void testOrMetadata() {
        LogicalOrFunction or = new LogicalOrFunction();
        assertEquals("OR", or.getName());
        assertEquals(2, or.getFunctionMetadata().getMinArgs());
        assertEquals(Integer.MAX_VALUE, or.getFunctionMetadata().getMaxArgs());
    }

    // ========== NOT Function Tests ==========

    @Test
    void testNotWithTrue() {
        LogicalNotFunction not = new LogicalNotFunction();
        AviatorObject result = not.call(env, AviatorBoolean.TRUE);
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testNotWithFalse() {
        LogicalNotFunction not = new LogicalNotFunction();
        AviatorObject result = not.call(env, AviatorBoolean.FALSE);
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testDoubleNegation() {
        LogicalNotFunction not = new LogicalNotFunction();
        AviatorObject result1 = not.call(env, AviatorBoolean.TRUE);
        AviatorObject result2 = not.call(env, result1);
        assertEquals(Boolean.TRUE, result2.getValue(env));
    }

    @Test
    void testNotWithWrongArgumentCount() {
        LogicalNotFunction not = new LogicalNotFunction();
        assertThrows(FunctionArgumentException.class, () -> {
            not.call(env, AviatorBoolean.TRUE, AviatorBoolean.FALSE);
        });
    }

    @Test
    void testNotWithNonBooleanArgument() {
        LogicalNotFunction not = new LogicalNotFunction();
        assertThrows(TypeMismatchException.class, () -> {
            not.call(env, new AviatorString("not a boolean"));
        });
    }

    @Test
    void testNotMetadata() {
        LogicalNotFunction not = new LogicalNotFunction();
        assertEquals("NOT", not.getName());
        assertEquals(1, not.getFunctionMetadata().getMinArgs());
        assertEquals(1, not.getFunctionMetadata().getMaxArgs());
    }

    // ========== Integration Tests ==========

    @Test
    void testAndOrCombination() {
        LogicalAndFunction and = new LogicalAndFunction();
        LogicalOrFunction or = new LogicalOrFunction();
        
        // OR(false, true) = true
        AviatorObject orResult = or.call(env, AviatorBoolean.FALSE, AviatorBoolean.TRUE);
        
        // AND(true, orResult) = AND(true, true) = true
        AviatorObject result = and.call(env, AviatorBoolean.TRUE, orResult);
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testNotAndCombination() {
        LogicalNotFunction not = new LogicalNotFunction();
        LogicalAndFunction and = new LogicalAndFunction();
        
        // AND(true, false) = false
        AviatorObject andResult = and.call(env, AviatorBoolean.TRUE, AviatorBoolean.FALSE);
        
        // NOT(andResult) = NOT(false) = true
        AviatorObject result = not.call(env, andResult);
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testDeMorgansLaw() {
        // NOT(AND(A, B)) should equal OR(NOT(A), NOT(B))
        LogicalNotFunction not = new LogicalNotFunction();
        LogicalAndFunction and = new LogicalAndFunction();
        LogicalOrFunction or = new LogicalOrFunction();
        
        AviatorBoolean a = AviatorBoolean.TRUE;
        AviatorBoolean b = AviatorBoolean.FALSE;
        
        // Left side: NOT(AND(A, B))
        AviatorObject andResult = and.call(env, a, b);
        AviatorObject leftSide = not.call(env, andResult);
        
        // Right side: OR(NOT(A), NOT(B))
        AviatorObject notA = not.call(env, a);
        AviatorObject notB = not.call(env, b);
        AviatorObject rightSide = or.call(env, notA, notB);
        
        assertEquals(leftSide.getValue(env), rightSide.getValue(env));
    }
}

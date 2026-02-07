package com.filter.dsl.unit.functions;

import com.filter.dsl.functions.FunctionArgumentException;
import com.filter.dsl.functions.TypeMismatchException;
import com.filter.dsl.functions.math.*;
import com.googlecode.aviator.runtime.type.AviatorDouble;
import com.googlecode.aviator.runtime.type.AviatorLong;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for basic arithmetic functions: ADD, SUBTRACT, MULTIPLY, DIVIDE, MOD
 */
class MathFunctionsTest {

    private final Map<String, Object> env = new HashMap<>();

    // ========== ADD Function Tests ==========

    @Test
    void testAddPositiveIntegers() {
        AddFunction add = new AddFunction();
        AviatorObject result = add.call(env, AviatorLong.valueOf(5), AviatorLong.valueOf(3));
        assertEquals(8.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testAddPositiveDecimals() {
        AddFunction add = new AddFunction();
        AviatorObject result = add.call(env, AviatorDouble.valueOf(10.5), AviatorDouble.valueOf(2.3));
        assertEquals(12.8, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testAddNegativeNumbers() {
        AddFunction add = new AddFunction();
        AviatorObject result = add.call(env, AviatorLong.valueOf(-5), AviatorLong.valueOf(10));
        assertEquals(5.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testAddZeros() {
        AddFunction add = new AddFunction();
        AviatorObject result = add.call(env, AviatorLong.valueOf(0), AviatorLong.valueOf(0));
        assertEquals(0.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testAddWithZero() {
        AddFunction add = new AddFunction();
        AviatorObject result = add.call(env, AviatorLong.valueOf(42), AviatorLong.valueOf(0));
        assertEquals(42.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testAddCommutative() {
        // ADD(a, b) should equal ADD(b, a)
        AddFunction add = new AddFunction();
        AviatorObject result1 = add.call(env, AviatorLong.valueOf(7), AviatorLong.valueOf(13));
        AviatorObject result2 = add.call(env, AviatorLong.valueOf(13), AviatorLong.valueOf(7));
        assertEquals(result1.getValue(env), result2.getValue(env));
    }

    @Test
    void testAddWithWrongArgumentCount() {
        AddFunction add = new AddFunction();
        // AviatorScript throws IllegalArgumentException for wrong argument count
        assertThrows(IllegalArgumentException.class, () -> {
            add.call(env, AviatorLong.valueOf(5));
        });
    }

    @Test
    void testAddWithNonNumericArgument() {
        AddFunction add = new AddFunction();
        assertThrows(TypeMismatchException.class, () -> {
            add.call(env, AviatorLong.valueOf(5), new AviatorString("not a number"));
        });
    }

    @Test
    void testAddMetadata() {
        AddFunction add = new AddFunction();
        assertEquals("ADD", add.getName());
        assertEquals(2, add.getFunctionMetadata().getMinArgs());
        assertEquals(2, add.getFunctionMetadata().getMaxArgs());
    }

    // ========== SUBTRACT Function Tests ==========

    @Test
    void testSubtractPositiveNumbers() {
        SubtractFunction subtract = new SubtractFunction();
        AviatorObject result = subtract.call(env, AviatorLong.valueOf(10), AviatorLong.valueOf(3));
        assertEquals(7.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testSubtractDecimals() {
        SubtractFunction subtract = new SubtractFunction();
        AviatorObject result = subtract.call(env, AviatorDouble.valueOf(5.5), AviatorDouble.valueOf(2.3));
        assertEquals(3.2, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testSubtractResultingInNegative() {
        SubtractFunction subtract = new SubtractFunction();
        AviatorObject result = subtract.call(env, AviatorLong.valueOf(5), AviatorLong.valueOf(10));
        assertEquals(-5.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testSubtractZero() {
        SubtractFunction subtract = new SubtractFunction();
        AviatorObject result = subtract.call(env, AviatorLong.valueOf(42), AviatorLong.valueOf(0));
        assertEquals(42.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testSubtractFromZero() {
        SubtractFunction subtract = new SubtractFunction();
        AviatorObject result = subtract.call(env, AviatorLong.valueOf(0), AviatorLong.valueOf(5));
        assertEquals(-5.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testSubtractSameNumber() {
        SubtractFunction subtract = new SubtractFunction();
        AviatorObject result = subtract.call(env, AviatorLong.valueOf(42), AviatorLong.valueOf(42));
        assertEquals(0.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testSubtractWithWrongArgumentCount() {
        SubtractFunction subtract = new SubtractFunction();
        // AviatorScript throws IllegalArgumentException for wrong argument count
        assertThrows(IllegalArgumentException.class, () -> {
            subtract.call(env, AviatorLong.valueOf(5));
        });
    }

    @Test
    void testSubtractWithNonNumericArgument() {
        SubtractFunction subtract = new SubtractFunction();
        assertThrows(TypeMismatchException.class, () -> {
            subtract.call(env, AviatorLong.valueOf(10), new AviatorString("not a number"));
        });
    }

    @Test
    void testSubtractMetadata() {
        SubtractFunction subtract = new SubtractFunction();
        assertEquals("SUBTRACT", subtract.getName());
        assertEquals(2, subtract.getFunctionMetadata().getMinArgs());
        assertEquals(2, subtract.getFunctionMetadata().getMaxArgs());
    }

    // ========== MULTIPLY Function Tests ==========

    @Test
    void testMultiplyPositiveIntegers() {
        MultiplyFunction multiply = new MultiplyFunction();
        AviatorObject result = multiply.call(env, AviatorLong.valueOf(5), AviatorLong.valueOf(3));
        assertEquals(15.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testMultiplyDecimals() {
        MultiplyFunction multiply = new MultiplyFunction();
        AviatorObject result = multiply.call(env, AviatorDouble.valueOf(2.5), AviatorLong.valueOf(4));
        assertEquals(10.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testMultiplyNegativeNumbers() {
        MultiplyFunction multiply = new MultiplyFunction();
        AviatorObject result = multiply.call(env, AviatorLong.valueOf(-5), AviatorLong.valueOf(3));
        assertEquals(-15.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testMultiplyByZero() {
        MultiplyFunction multiply = new MultiplyFunction();
        AviatorObject result = multiply.call(env, AviatorLong.valueOf(100), AviatorLong.valueOf(0));
        assertEquals(0.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testMultiplyByOne() {
        MultiplyFunction multiply = new MultiplyFunction();
        AviatorObject result = multiply.call(env, AviatorLong.valueOf(42), AviatorLong.valueOf(1));
        assertEquals(42.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testMultiplyCommutative() {
        // MULTIPLY(a, b) should equal MULTIPLY(b, a)
        MultiplyFunction multiply = new MultiplyFunction();
        AviatorObject result1 = multiply.call(env, AviatorLong.valueOf(7), AviatorLong.valueOf(13));
        AviatorObject result2 = multiply.call(env, AviatorLong.valueOf(13), AviatorLong.valueOf(7));
        assertEquals(result1.getValue(env), result2.getValue(env));
    }

    @Test
    void testMultiplyWithWrongArgumentCount() {
        MultiplyFunction multiply = new MultiplyFunction();
        // AviatorScript throws IllegalArgumentException for wrong argument count
        assertThrows(IllegalArgumentException.class, () -> {
            multiply.call(env, AviatorLong.valueOf(5));
        });
    }

    @Test
    void testMultiplyWithNonNumericArgument() {
        MultiplyFunction multiply = new MultiplyFunction();
        assertThrows(TypeMismatchException.class, () -> {
            multiply.call(env, AviatorLong.valueOf(5), new AviatorString("not a number"));
        });
    }

    @Test
    void testMultiplyMetadata() {
        MultiplyFunction multiply = new MultiplyFunction();
        assertEquals("MULTIPLY", multiply.getName());
        assertEquals(2, multiply.getFunctionMetadata().getMinArgs());
        assertEquals(2, multiply.getFunctionMetadata().getMaxArgs());
    }

    // ========== DIVIDE Function Tests ==========

    @Test
    void testDivideIntegers() {
        DivideFunction divide = new DivideFunction();
        AviatorObject result = divide.call(env, AviatorLong.valueOf(10), AviatorLong.valueOf(2));
        assertEquals(5.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testDivideWithRemainder() {
        DivideFunction divide = new DivideFunction();
        AviatorObject result = divide.call(env, AviatorLong.valueOf(7), AviatorLong.valueOf(2));
        assertEquals(3.5, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testDivideDecimals() {
        DivideFunction divide = new DivideFunction();
        AviatorObject result = divide.call(env, AviatorDouble.valueOf(15.0), AviatorLong.valueOf(3));
        assertEquals(5.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testDivideByZero() {
        // Division by zero should return null
        DivideFunction divide = new DivideFunction();
        AviatorObject result = divide.call(env, AviatorLong.valueOf(10), AviatorLong.valueOf(0));
        assertNull(result.getValue(env));
    }

    @Test
    void testDivideZeroByNumber() {
        DivideFunction divide = new DivideFunction();
        AviatorObject result = divide.call(env, AviatorLong.valueOf(0), AviatorLong.valueOf(5));
        assertEquals(0.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testDivideByOne() {
        DivideFunction divide = new DivideFunction();
        AviatorObject result = divide.call(env, AviatorLong.valueOf(42), AviatorLong.valueOf(1));
        assertEquals(42.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testDivideNegativeNumbers() {
        DivideFunction divide = new DivideFunction();
        AviatorObject result = divide.call(env, AviatorLong.valueOf(-10), AviatorLong.valueOf(2));
        assertEquals(-5.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testDivideWithWrongArgumentCount() {
        DivideFunction divide = new DivideFunction();
        // AviatorScript throws IllegalArgumentException for wrong argument count
        assertThrows(IllegalArgumentException.class, () -> {
            divide.call(env, AviatorLong.valueOf(10));
        });
    }

    @Test
    void testDivideWithNonNumericArgument() {
        DivideFunction divide = new DivideFunction();
        assertThrows(TypeMismatchException.class, () -> {
            divide.call(env, AviatorLong.valueOf(10), new AviatorString("not a number"));
        });
    }

    @Test
    void testDivideMetadata() {
        DivideFunction divide = new DivideFunction();
        assertEquals("DIVIDE", divide.getName());
        assertEquals(2, divide.getFunctionMetadata().getMinArgs());
        assertEquals(2, divide.getFunctionMetadata().getMaxArgs());
    }

    // ========== MOD Function Tests ==========

    @Test
    void testModBasic() {
        ModFunction mod = new ModFunction();
        AviatorObject result = mod.call(env, AviatorLong.valueOf(10), AviatorLong.valueOf(3));
        assertEquals(1.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testModEvenDivision() {
        ModFunction mod = new ModFunction();
        AviatorObject result = mod.call(env, AviatorLong.valueOf(20), AviatorLong.valueOf(5));
        assertEquals(0.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testModWithRemainder() {
        ModFunction mod = new ModFunction();
        AviatorObject result = mod.call(env, AviatorLong.valueOf(15), AviatorLong.valueOf(4));
        assertEquals(3.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testModWithSmallDividend() {
        ModFunction mod = new ModFunction();
        AviatorObject result = mod.call(env, AviatorLong.valueOf(7), AviatorLong.valueOf(2));
        assertEquals(1.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testModByZero() {
        // Modulo by zero should return null
        ModFunction mod = new ModFunction();
        AviatorObject result = mod.call(env, AviatorLong.valueOf(10), AviatorLong.valueOf(0));
        assertNull(result.getValue(env));
    }

    @Test
    void testModZeroByNumber() {
        ModFunction mod = new ModFunction();
        AviatorObject result = mod.call(env, AviatorLong.valueOf(0), AviatorLong.valueOf(5));
        assertEquals(0.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testModNegativeNumbers() {
        ModFunction mod = new ModFunction();
        AviatorObject result = mod.call(env, AviatorLong.valueOf(-10), AviatorLong.valueOf(3));
        // Java's % operator: -10 % 3 = -1
        assertEquals(-1.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testModWithWrongArgumentCount() {
        ModFunction mod = new ModFunction();
        // AviatorScript throws IllegalArgumentException for wrong argument count
        assertThrows(IllegalArgumentException.class, () -> {
            mod.call(env, AviatorLong.valueOf(10));
        });
    }

    @Test
    void testModWithNonNumericArgument() {
        ModFunction mod = new ModFunction();
        assertThrows(TypeMismatchException.class, () -> {
            mod.call(env, AviatorLong.valueOf(10), new AviatorString("not a number"));
        });
    }

    @Test
    void testModMetadata() {
        ModFunction mod = new ModFunction();
        assertEquals("MOD", mod.getName());
        assertEquals(2, mod.getFunctionMetadata().getMinArgs());
        assertEquals(2, mod.getFunctionMetadata().getMaxArgs());
    }

    // ========== Integration Tests ==========

    @Test
    void testArithmeticCombination() {
        // Test: ADD(MULTIPLY(5, 3), SUBTRACT(10, 2)) = ADD(15, 8) = 23
        MultiplyFunction multiply = new MultiplyFunction();
        SubtractFunction subtract = new SubtractFunction();
        AddFunction add = new AddFunction();
        
        AviatorObject multiplyResult = multiply.call(env, AviatorLong.valueOf(5), AviatorLong.valueOf(3));
        AviatorObject subtractResult = subtract.call(env, AviatorLong.valueOf(10), AviatorLong.valueOf(2));
        AviatorObject result = add.call(env, multiplyResult, subtractResult);
        
        assertEquals(23.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testDivideAndMultiplyInverse() {
        // Test: MULTIPLY(DIVIDE(a, b), b) should equal a (when b != 0)
        DivideFunction divide = new DivideFunction();
        MultiplyFunction multiply = new MultiplyFunction();
        
        AviatorLong a = AviatorLong.valueOf(42);
        AviatorLong b = AviatorLong.valueOf(7);
        
        AviatorObject divideResult = divide.call(env, a, b);
        AviatorObject result = multiply.call(env, divideResult, b);
        
        assertEquals(42.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testSubtractAndAddInverse() {
        // Test: ADD(SUBTRACT(a, b), b) should equal a
        SubtractFunction subtract = new SubtractFunction();
        AddFunction add = new AddFunction();
        
        AviatorLong a = AviatorLong.valueOf(100);
        AviatorLong b = AviatorLong.valueOf(37);
        
        AviatorObject subtractResult = subtract.call(env, a, b);
        AviatorObject result = add.call(env, subtractResult, b);
        
        assertEquals(100.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testModuloWithDivision() {
        // Test: a = MULTIPLY(FLOOR(DIVIDE(a, b)), b) + MOD(a, b)
        // This verifies the relationship between division and modulo
        // Note: We need to use floor division for this property to hold
        DivideFunction divide = new DivideFunction();
        MultiplyFunction multiply = new MultiplyFunction();
        ModFunction mod = new ModFunction();
        AddFunction add = new AddFunction();
        
        AviatorLong a = AviatorLong.valueOf(17);
        AviatorLong b = AviatorLong.valueOf(5);
        
        // floor(quotient) * divisor
        AviatorObject divideResult = divide.call(env, a, b);
        double quotient = ((Number) divideResult.getValue(env)).doubleValue();
        long floorQuotient = (long) Math.floor(quotient);
        AviatorObject multiplyResult = multiply.call(env, AviatorLong.valueOf(floorQuotient), b);
        
        // remainder
        AviatorObject modResult = mod.call(env, a, b);
        
        // floor(quotient) * divisor + remainder should equal original
        AviatorObject result = add.call(env, multiplyResult, modResult);
        
        assertEquals(17.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testDistributiveProperty() {
        // Test: MULTIPLY(a, ADD(b, c)) = ADD(MULTIPLY(a, b), MULTIPLY(a, c))
        MultiplyFunction multiply = new MultiplyFunction();
        AddFunction add = new AddFunction();
        
        AviatorLong a = AviatorLong.valueOf(3);
        AviatorLong b = AviatorLong.valueOf(4);
        AviatorLong c = AviatorLong.valueOf(5);
        
        // Left side: a * (b + c)
        AviatorObject addResult = add.call(env, b, c);
        AviatorObject leftSide = multiply.call(env, a, addResult);
        
        // Right side: (a * b) + (a * c)
        AviatorObject ab = multiply.call(env, a, b);
        AviatorObject ac = multiply.call(env, a, c);
        AviatorObject rightSide = add.call(env, ab, ac);
        
        assertEquals(
            ((Number) leftSide.getValue(env)).doubleValue(),
            ((Number) rightSide.getValue(env)).doubleValue(),
            0.0001
        );
    }

    // ========== ABS Function Tests ==========

    @Test
    void testAbsPositiveNumber() {
        AbsFunction abs = new AbsFunction();
        AviatorObject result = abs.call(env, AviatorLong.valueOf(5));
        assertEquals(5.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testAbsNegativeNumber() {
        AbsFunction abs = new AbsFunction();
        AviatorObject result = abs.call(env, AviatorLong.valueOf(-5));
        assertEquals(5.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testAbsZero() {
        AbsFunction abs = new AbsFunction();
        AviatorObject result = abs.call(env, AviatorLong.valueOf(0));
        assertEquals(0.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testAbsNegativeDecimal() {
        AbsFunction abs = new AbsFunction();
        AviatorObject result = abs.call(env, AviatorDouble.valueOf(-10.5));
        assertEquals(10.5, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testAbsWithWrongArgumentCount() {
        AbsFunction abs = new AbsFunction();
        assertThrows(IllegalArgumentException.class, () -> {
            abs.call(env, AviatorLong.valueOf(5), AviatorLong.valueOf(3));
        });
    }

    @Test
    void testAbsWithNonNumericArgument() {
        AbsFunction abs = new AbsFunction();
        assertThrows(TypeMismatchException.class, () -> {
            abs.call(env, new AviatorString("not a number"));
        });
    }

    @Test
    void testAbsMetadata() {
        AbsFunction abs = new AbsFunction();
        assertEquals("ABS", abs.getName());
        assertEquals(1, abs.getFunctionMetadata().getMinArgs());
        assertEquals(1, abs.getFunctionMetadata().getMaxArgs());
    }

    // ========== ROUND Function Tests ==========

    @Test
    void testRoundDown() {
        RoundFunction round = new RoundFunction();
        AviatorObject result = round.call(env, AviatorDouble.valueOf(5.4));
        assertEquals(5.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testRoundUp() {
        RoundFunction round = new RoundFunction();
        AviatorObject result = round.call(env, AviatorDouble.valueOf(5.6));
        assertEquals(6.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testRoundHalf() {
        RoundFunction round = new RoundFunction();
        AviatorObject result = round.call(env, AviatorDouble.valueOf(5.5));
        assertEquals(6.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testRoundWithDecimals() {
        RoundFunction round = new RoundFunction();
        AviatorObject result = round.call(env, AviatorDouble.valueOf(3.14159), AviatorLong.valueOf(2));
        assertEquals(3.14, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testRoundToZeroDecimals() {
        RoundFunction round = new RoundFunction();
        AviatorObject result = round.call(env, AviatorDouble.valueOf(3.14159), AviatorLong.valueOf(0));
        assertEquals(3.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testRoundToOneDecimal() {
        RoundFunction round = new RoundFunction();
        AviatorObject result = round.call(env, AviatorDouble.valueOf(123.456), AviatorLong.valueOf(1));
        assertEquals(123.5, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testRoundNegativeNumber() {
        RoundFunction round = new RoundFunction();
        AviatorObject result = round.call(env, AviatorDouble.valueOf(-5.5));
        assertEquals(-6.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testRoundWithNonNumericArgument() {
        RoundFunction round = new RoundFunction();
        assertThrows(TypeMismatchException.class, () -> {
            round.call(env, new AviatorString("not a number"));
        });
    }

    @Test
    void testRoundMetadata() {
        RoundFunction round = new RoundFunction();
        assertEquals("ROUND", round.getName());
        assertEquals(1, round.getFunctionMetadata().getMinArgs());
        assertEquals(2, round.getFunctionMetadata().getMaxArgs());
    }

    // ========== CEIL Function Tests ==========

    @Test
    void testCeilPositiveDecimal() {
        CeilFunction ceil = new CeilFunction();
        AviatorObject result = ceil.call(env, AviatorDouble.valueOf(5.1));
        assertEquals(6.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testCeilPositiveDecimalNearInteger() {
        CeilFunction ceil = new CeilFunction();
        AviatorObject result = ceil.call(env, AviatorDouble.valueOf(5.9));
        assertEquals(6.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testCeilInteger() {
        CeilFunction ceil = new CeilFunction();
        AviatorObject result = ceil.call(env, AviatorDouble.valueOf(5.0));
        assertEquals(5.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testCeilNegativeDecimal() {
        CeilFunction ceil = new CeilFunction();
        AviatorObject result = ceil.call(env, AviatorDouble.valueOf(-5.1));
        assertEquals(-5.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testCeilNegativeDecimalNearInteger() {
        CeilFunction ceil = new CeilFunction();
        AviatorObject result = ceil.call(env, AviatorDouble.valueOf(-5.9));
        assertEquals(-5.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testCeilZero() {
        CeilFunction ceil = new CeilFunction();
        AviatorObject result = ceil.call(env, AviatorLong.valueOf(0));
        assertEquals(0.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testCeilWithWrongArgumentCount() {
        CeilFunction ceil = new CeilFunction();
        assertThrows(IllegalArgumentException.class, () -> {
            ceil.call(env, AviatorDouble.valueOf(5.5), AviatorLong.valueOf(2));
        });
    }

    @Test
    void testCeilWithNonNumericArgument() {
        CeilFunction ceil = new CeilFunction();
        assertThrows(TypeMismatchException.class, () -> {
            ceil.call(env, new AviatorString("not a number"));
        });
    }

    @Test
    void testCeilMetadata() {
        CeilFunction ceil = new CeilFunction();
        assertEquals("CEIL", ceil.getName());
        assertEquals(1, ceil.getFunctionMetadata().getMinArgs());
        assertEquals(1, ceil.getFunctionMetadata().getMaxArgs());
    }

    // ========== FLOOR Function Tests ==========

    @Test
    void testFloorPositiveDecimal() {
        FloorFunction floor = new FloorFunction();
        AviatorObject result = floor.call(env, AviatorDouble.valueOf(5.1));
        assertEquals(5.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testFloorPositiveDecimalNearInteger() {
        FloorFunction floor = new FloorFunction();
        AviatorObject result = floor.call(env, AviatorDouble.valueOf(5.9));
        assertEquals(5.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testFloorInteger() {
        FloorFunction floor = new FloorFunction();
        AviatorObject result = floor.call(env, AviatorDouble.valueOf(5.0));
        assertEquals(5.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testFloorNegativeDecimal() {
        FloorFunction floor = new FloorFunction();
        AviatorObject result = floor.call(env, AviatorDouble.valueOf(-5.1));
        assertEquals(-6.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testFloorNegativeDecimalNearInteger() {
        FloorFunction floor = new FloorFunction();
        AviatorObject result = floor.call(env, AviatorDouble.valueOf(-5.9));
        assertEquals(-6.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testFloorZero() {
        FloorFunction floor = new FloorFunction();
        AviatorObject result = floor.call(env, AviatorLong.valueOf(0));
        assertEquals(0.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testFloorWithWrongArgumentCount() {
        FloorFunction floor = new FloorFunction();
        assertThrows(IllegalArgumentException.class, () -> {
            floor.call(env, AviatorDouble.valueOf(5.5), AviatorLong.valueOf(2));
        });
    }

    @Test
    void testFloorWithNonNumericArgument() {
        FloorFunction floor = new FloorFunction();
        assertThrows(TypeMismatchException.class, () -> {
            floor.call(env, new AviatorString("not a number"));
        });
    }

    @Test
    void testFloorMetadata() {
        FloorFunction floor = new FloorFunction();
        assertEquals("FLOOR", floor.getName());
        assertEquals(1, floor.getFunctionMetadata().getMinArgs());
        assertEquals(1, floor.getFunctionMetadata().getMaxArgs());
    }

    // ========== POW Function Tests ==========

    @Test
    void testPowBasic() {
        PowFunction pow = new PowFunction();
        AviatorObject result = pow.call(env, AviatorLong.valueOf(2), AviatorLong.valueOf(3));
        assertEquals(8.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testPowSquare() {
        PowFunction pow = new PowFunction();
        AviatorObject result = pow.call(env, AviatorLong.valueOf(5), AviatorLong.valueOf(2));
        assertEquals(25.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testPowZeroExponent() {
        PowFunction pow = new PowFunction();
        AviatorObject result = pow.call(env, AviatorLong.valueOf(10), AviatorLong.valueOf(0));
        assertEquals(1.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testPowNegativeExponent() {
        PowFunction pow = new PowFunction();
        AviatorObject result = pow.call(env, AviatorLong.valueOf(2), AviatorLong.valueOf(-1));
        assertEquals(0.5, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testPowFractionalExponent() {
        PowFunction pow = new PowFunction();
        AviatorObject result = pow.call(env, AviatorLong.valueOf(4), AviatorDouble.valueOf(0.5));
        assertEquals(2.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testPowCubeRoot() {
        PowFunction pow = new PowFunction();
        AviatorObject result = pow.call(env, AviatorLong.valueOf(27), AviatorDouble.valueOf(1.0/3.0));
        assertEquals(3.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testPowWithWrongArgumentCount() {
        PowFunction pow = new PowFunction();
        assertThrows(IllegalArgumentException.class, () -> {
            pow.call(env, AviatorLong.valueOf(2));
        });
    }

    @Test
    void testPowWithNonNumericArgument() {
        PowFunction pow = new PowFunction();
        assertThrows(TypeMismatchException.class, () -> {
            pow.call(env, AviatorLong.valueOf(2), new AviatorString("not a number"));
        });
    }

    @Test
    void testPowMetadata() {
        PowFunction pow = new PowFunction();
        assertEquals("POW", pow.getName());
        assertEquals(2, pow.getFunctionMetadata().getMinArgs());
        assertEquals(2, pow.getFunctionMetadata().getMaxArgs());
    }

    // ========== SQRT Function Tests ==========

    @Test
    void testSqrtPerfectSquare() {
        SqrtFunction sqrt = new SqrtFunction();
        AviatorObject result = sqrt.call(env, AviatorLong.valueOf(4));
        assertEquals(2.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testSqrtNine() {
        SqrtFunction sqrt = new SqrtFunction();
        AviatorObject result = sqrt.call(env, AviatorLong.valueOf(9));
        assertEquals(3.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testSqrtSixteen() {
        SqrtFunction sqrt = new SqrtFunction();
        AviatorObject result = sqrt.call(env, AviatorLong.valueOf(16));
        assertEquals(4.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testSqrtNonPerfectSquare() {
        SqrtFunction sqrt = new SqrtFunction();
        AviatorObject result = sqrt.call(env, AviatorLong.valueOf(2));
        assertEquals(1.41421356, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testSqrtZero() {
        SqrtFunction sqrt = new SqrtFunction();
        AviatorObject result = sqrt.call(env, AviatorLong.valueOf(0));
        assertEquals(0.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testSqrtNegativeNumber() {
        SqrtFunction sqrt = new SqrtFunction();
        assertThrows(FunctionArgumentException.class, () -> {
            sqrt.call(env, AviatorLong.valueOf(-1));
        });
    }

    @Test
    void testSqrtWithWrongArgumentCount() {
        SqrtFunction sqrt = new SqrtFunction();
        assertThrows(IllegalArgumentException.class, () -> {
            sqrt.call(env, AviatorLong.valueOf(4), AviatorLong.valueOf(2));
        });
    }

    @Test
    void testSqrtWithNonNumericArgument() {
        SqrtFunction sqrt = new SqrtFunction();
        assertThrows(TypeMismatchException.class, () -> {
            sqrt.call(env, new AviatorString("not a number"));
        });
    }

    @Test
    void testSqrtMetadata() {
        SqrtFunction sqrt = new SqrtFunction();
        assertEquals("SQRT", sqrt.getName());
        assertEquals(1, sqrt.getFunctionMetadata().getMinArgs());
        assertEquals(1, sqrt.getFunctionMetadata().getMaxArgs());
    }

    // ========== LOG Function Tests ==========

    @Test
    void testLogNaturalOfE() {
        LogFunction log = new LogFunction();
        AviatorObject result = log.call(env, AviatorDouble.valueOf(Math.E));
        assertEquals(1.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testLogBase10() {
        LogFunction log = new LogFunction();
        AviatorObject result = log.call(env, AviatorLong.valueOf(10), AviatorLong.valueOf(10));
        assertEquals(1.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testLogBase2() {
        LogFunction log = new LogFunction();
        AviatorObject result = log.call(env, AviatorLong.valueOf(8), AviatorLong.valueOf(2));
        assertEquals(3.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testLogBase10Of100() {
        LogFunction log = new LogFunction();
        AviatorObject result = log.call(env, AviatorLong.valueOf(100), AviatorLong.valueOf(10));
        assertEquals(2.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testLogOfOne() {
        LogFunction log = new LogFunction();
        AviatorObject result = log.call(env, AviatorLong.valueOf(1));
        assertEquals(0.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testLogOfZero() {
        LogFunction log = new LogFunction();
        assertThrows(FunctionArgumentException.class, () -> {
            log.call(env, AviatorLong.valueOf(0));
        });
    }

    @Test
    void testLogOfNegativeNumber() {
        LogFunction log = new LogFunction();
        assertThrows(FunctionArgumentException.class, () -> {
            log.call(env, AviatorLong.valueOf(-5));
        });
    }

    @Test
    void testLogWithInvalidBase() {
        LogFunction log = new LogFunction();
        assertThrows(FunctionArgumentException.class, () -> {
            log.call(env, AviatorLong.valueOf(10), AviatorLong.valueOf(1));
        });
    }

    @Test
    void testLogWithNegativeBase() {
        LogFunction log = new LogFunction();
        assertThrows(FunctionArgumentException.class, () -> {
            log.call(env, AviatorLong.valueOf(10), AviatorLong.valueOf(-2));
        });
    }

    @Test
    void testLogWithNonNumericArgument() {
        LogFunction log = new LogFunction();
        assertThrows(TypeMismatchException.class, () -> {
            log.call(env, new AviatorString("not a number"));
        });
    }

    @Test
    void testLogMetadata() {
        LogFunction log = new LogFunction();
        assertEquals("LOG", log.getName());
        assertEquals(1, log.getFunctionMetadata().getMinArgs());
        assertEquals(2, log.getFunctionMetadata().getMaxArgs());
    }

    // ========== EXP Function Tests ==========

    @Test
    void testExpZero() {
        ExpFunction exp = new ExpFunction();
        AviatorObject result = exp.call(env, AviatorLong.valueOf(0));
        assertEquals(1.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testExpOne() {
        ExpFunction exp = new ExpFunction();
        AviatorObject result = exp.call(env, AviatorLong.valueOf(1));
        assertEquals(Math.E, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testExpTwo() {
        ExpFunction exp = new ExpFunction();
        AviatorObject result = exp.call(env, AviatorLong.valueOf(2));
        assertEquals(7.389056, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testExpNegativeOne() {
        ExpFunction exp = new ExpFunction();
        AviatorObject result = exp.call(env, AviatorLong.valueOf(-1));
        assertEquals(0.367879, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testExpTen() {
        ExpFunction exp = new ExpFunction();
        AviatorObject result = exp.call(env, AviatorLong.valueOf(10));
        assertEquals(22026.465, ((Number) result.getValue(env)).doubleValue(), 0.01);
    }

    @Test
    void testExpWithWrongArgumentCount() {
        ExpFunction exp = new ExpFunction();
        assertThrows(IllegalArgumentException.class, () -> {
            exp.call(env, AviatorLong.valueOf(1), AviatorLong.valueOf(2));
        });
    }

    @Test
    void testExpWithNonNumericArgument() {
        ExpFunction exp = new ExpFunction();
        assertThrows(TypeMismatchException.class, () -> {
            exp.call(env, new AviatorString("not a number"));
        });
    }

    @Test
    void testExpMetadata() {
        ExpFunction exp = new ExpFunction();
        assertEquals("EXP", exp.getName());
        assertEquals(1, exp.getFunctionMetadata().getMinArgs());
        assertEquals(1, exp.getFunctionMetadata().getMaxArgs());
    }

    // ========== Advanced Math Integration Tests ==========

    @Test
    void testExpLogInverse() {
        // Test: EXP(LOG(x)) should equal x for positive x
        ExpFunction exp = new ExpFunction();
        LogFunction log = new LogFunction();
        
        AviatorDouble x = AviatorDouble.valueOf(42.0);
        
        AviatorObject logResult = log.call(env, x);
        AviatorObject result = exp.call(env, logResult);
        
        assertEquals(42.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testSqrtPowInverse() {
        // Test: POW(SQRT(x), 2) should equal x for non-negative x
        SqrtFunction sqrt = new SqrtFunction();
        PowFunction pow = new PowFunction();
        
        AviatorLong x = AviatorLong.valueOf(25);
        
        AviatorObject sqrtResult = sqrt.call(env, x);
        AviatorObject result = pow.call(env, sqrtResult, AviatorLong.valueOf(2));
        
        assertEquals(25.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testAbsWithNegativeResult() {
        // Test: ABS(SUBTRACT(a, b)) where a < b
        AbsFunction abs = new AbsFunction();
        SubtractFunction subtract = new SubtractFunction();
        
        AviatorObject subtractResult = subtract.call(env, AviatorLong.valueOf(5), AviatorLong.valueOf(10));
        AviatorObject result = abs.call(env, subtractResult);
        
        assertEquals(5.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }

    @Test
    void testRoundCeilFloorRelationship() {
        // For positive numbers: FLOOR(x) <= ROUND(x) <= CEIL(x)
        RoundFunction round = new RoundFunction();
        CeilFunction ceil = new CeilFunction();
        FloorFunction floor = new FloorFunction();
        
        AviatorDouble x = AviatorDouble.valueOf(5.4);
        
        double floorResult = ((Number) floor.call(env, x).getValue(env)).doubleValue();
        double roundResult = ((Number) round.call(env, x).getValue(env)).doubleValue();
        double ceilResult = ((Number) ceil.call(env, x).getValue(env)).doubleValue();
        
        assertTrue(floorResult <= roundResult);
        assertTrue(roundResult <= ceilResult);
    }

    @Test
    void testLogPowRelationship() {
        // Test: LOG(POW(base, exp), base) should equal exp
        LogFunction log = new LogFunction();
        PowFunction pow = new PowFunction();
        
        AviatorLong base = AviatorLong.valueOf(2);
        AviatorLong exponent = AviatorLong.valueOf(5);
        
        AviatorObject powResult = pow.call(env, base, exponent);
        AviatorObject result = log.call(env, powResult, base);
        
        assertEquals(5.0, ((Number) result.getValue(env)).doubleValue(), 0.0001);
    }
}

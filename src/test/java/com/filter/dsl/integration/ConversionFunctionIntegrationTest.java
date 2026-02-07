package com.filter.dsl.integration;

import com.filter.dsl.functions.FunctionRegistry;
import com.filter.dsl.functions.conversion.*;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for conversion functions with AviatorScript.
 * Tests the functions in realistic DSL expression scenarios.
 * 
 * Requirements: 10.1, 10.2, 10.3, 10.4, 10.5, 10.6, 10.8
 */
class ConversionFunctionIntegrationTest {

    private AviatorEvaluatorInstance aviator;
    private Map<String, Object> env;

    @BeforeEach
    void setUp() {
        aviator = AviatorEvaluator.newInstance();
        
        // Register conversion functions
        FunctionRegistry registry = new FunctionRegistry();
        registry.register(new ToNumberFunction());
        registry.register(new ToStringFunction());
        registry.register(new ToBooleanFunction());
        registry.register(new ConvertUnitFunction());
        registry.registerAll(aviator);
        
        env = new HashMap<>();
    }

    // ========== TO_NUMBER Integration Tests ==========

    @Test
    void testToNumberInExpression() {
        Object result = aviator.execute("TO_NUMBER('123')", env);
        assertEquals(123L, result);
    }

    @Test
    void testToNumberWithArithmetic() {
        Object result = aviator.execute("TO_NUMBER('10') + TO_NUMBER('20')", env);
        assertEquals(30L, result);
    }

    @Test
    void testToNumberWithComparison() {
        Object result = aviator.execute("TO_NUMBER('42') > 40", env);
        assertEquals(true, result);
    }

    @Test
    void testToNumberWithBooleanInExpression() {
        Object result = aviator.execute("TO_NUMBER(true) + TO_NUMBER(false)", env);
        assertEquals(1L, result);
    }

    @Test
    void testToNumberWithFloatingPoint() {
        Object result = aviator.execute("TO_NUMBER('3.14') * 2", env);
        assertEquals(6.28, (Double) result, 0.001);
    }

    @Test
    void testToNumberWithVariable() {
        env.put("strValue", "456");
        Object result = aviator.execute("TO_NUMBER(strValue)", env);
        assertEquals(456L, result);
    }

    // ========== TO_STRING Integration Tests ==========

    @Test
    void testToStringInExpression() {
        Object result = aviator.execute("TO_STRING(123)", env);
        assertEquals("123", result);
    }

    @Test
    void testToStringWithConcatenation() {
        Object result = aviator.execute("TO_STRING(42) + ' is the answer'", env);
        assertEquals("42 is the answer", result);
    }

    @Test
    void testToStringWithBoolean() {
        Object result = aviator.execute("TO_STRING(true)", env);
        assertEquals("true", result);
    }

    @Test
    void testToStringWithVariable() {
        env.put("numValue", 789);
        Object result = aviator.execute("TO_STRING(numValue)", env);
        assertEquals("789", result);
    }

    @Test
    void testToStringWithFloatingPoint() {
        Object result = aviator.execute("TO_STRING(3.14)", env);
        assertEquals("3.14", result);
    }

    @Test
    void testToStringWithNegativeNumber() {
        Object result = aviator.execute("TO_STRING(-42)", env);
        assertEquals("-42", result);
    }

    // ========== TO_BOOLEAN Integration Tests ==========

    @Test
    void testToBooleanInExpression() {
        Object result = aviator.execute("TO_BOOLEAN(1)", env);
        assertEquals(true, result);
    }

    @Test
    void testToBooleanWithLogicalOperations() {
        Object result = aviator.execute("TO_BOOLEAN(1) && TO_BOOLEAN(0)", env);
        assertEquals(false, result);
    }

    @Test
    void testToBooleanWithString() {
        Object result = aviator.execute("TO_BOOLEAN('true')", env);
        assertEquals(true, result);
    }

    @Test
    void testToBooleanWithEmptyString() {
        Object result = aviator.execute("TO_BOOLEAN('')", env);
        assertEquals(false, result);
    }

    @Test
    void testToBooleanWithVariable() {
        env.put("value", 42);
        Object result = aviator.execute("TO_BOOLEAN(value)", env);
        assertEquals(true, result);
    }

    @Test
    void testToBooleanInConditional() {
        Object result = aviator.execute("TO_BOOLEAN(1) ? 'yes' : 'no'", env);
        assertEquals("yes", result);
    }

    // ========== Conversion Chain Tests ==========

    @Test
    void testStringToNumberToString() {
        Object result = aviator.execute("TO_STRING(TO_NUMBER('42'))", env);
        assertEquals("42", result);
    }

    @Test
    void testNumberToBooleanToString() {
        Object result = aviator.execute("TO_STRING(TO_BOOLEAN(1))", env);
        assertEquals("true", result);
    }

    @Test
    void testBooleanToNumberToString() {
        Object result = aviator.execute("TO_STRING(TO_NUMBER(true))", env);
        assertEquals("1", result);
    }

    @Test
    void testComplexConversionChain() {
        // Convert string to number, multiply, convert to boolean, convert to string
        Object result = aviator.execute("TO_STRING(TO_BOOLEAN(TO_NUMBER('5') * 2))", env);
        assertEquals("true", result);
    }

    // ========== Real-World Use Cases ==========

    @Test
    void testConvertUserInputToNumber() {
        // Simulate converting user input string to number for calculation
        env.put("userInput", "100");
        Object result = aviator.execute("TO_NUMBER(userInput) * 1.1", env);
        assertEquals(110.0, (Double) result, 0.001);
    }

    @Test
    void testConvertCalculationToString() {
        // Calculate a value and convert to string for display
        Object result = aviator.execute("'Total: ' + TO_STRING(10 + 20 + 30)", env);
        assertEquals("Total: 60", result);
    }

    @Test
    void testConvertFlagToBoolean() {
        // Convert various flag formats to boolean
        env.put("flag1", "yes");
        env.put("flag2", "1");
        env.put("flag3", "true");
        
        Object result1 = aviator.execute("TO_BOOLEAN(flag1)", env);
        Object result2 = aviator.execute("TO_BOOLEAN(flag2)", env);
        Object result3 = aviator.execute("TO_BOOLEAN(flag3)", env);
        
        assertEquals(true, result1);
        assertEquals(true, result2);
        assertEquals(true, result3);
    }

    @Test
    void testConditionalWithConversion() {
        // Use conversion in conditional logic
        env.put("score", "85");
        Object result = aviator.execute("TO_NUMBER(score) >= 80 ? 'Pass' : 'Fail'", env);
        assertEquals("Pass", result);
    }

    @Test
    void testMultipleConversionsInExpression() {
        // Complex expression with multiple conversions
        env.put("a", "10");
        env.put("b", "20");
        Object result = aviator.execute(
            "TO_STRING(TO_NUMBER(a) + TO_NUMBER(b))", 
            env
        );
        assertEquals("30", result);
    }

    @Test
    void testConversionWithNullHandling() {
        // Test that TO_STRING handles null gracefully
        env.put("nullValue", null);
        Object result = aviator.execute("TO_STRING(nullValue)", env);
        assertNull(result);
    }

    @Test
    void testBooleanConversionInFilter() {
        // Simulate filtering based on boolean conversion
        env.put("status", "active");
        Object result = aviator.execute(
            "TO_BOOLEAN(status) ? 'enabled' : 'disabled'", 
            env
        );
        assertEquals("enabled", result);
    }

    @Test
    void testNumericStringComparison() {
        // Convert strings to numbers for proper comparison
        env.put("value1", "100");
        env.put("value2", "20");
        
        // String comparison would give wrong result: "100" < "20" is true
        // But numeric comparison gives correct result: 100 > 20
        Object result = aviator.execute(
            "TO_NUMBER(value1) > TO_NUMBER(value2)", 
            env
        );
        assertEquals(true, result);
    }

    @Test
    void testConversionWithMathOperations() {
        // Test conversions with various math operations
        Object result = aviator.execute(
            "TO_NUMBER('10') + TO_NUMBER('5') * TO_NUMBER('2')", 
            env
        );
        assertEquals(20L, result);
    }

    @Test
    void testBooleanArithmetic() {
        // Test boolean to number conversion for arithmetic
        Object result = aviator.execute(
            "TO_NUMBER(true) + TO_NUMBER(true) + TO_NUMBER(false)", 
            env
        );
        assertEquals(2L, result);
    }

    @Test
    void testStringFormattingWithConversion() {
        // Format output with type conversions
        env.put("count", 42);
        env.put("active", true);
        Object result = aviator.execute(
            "'Count: ' + TO_STRING(count) + ', Active: ' + TO_STRING(active)", 
            env
        );
        assertEquals("Count: 42, Active: true", result);
    }

    @Test
    void testConversionInNestedExpression() {
        // Test conversions in deeply nested expressions
        Object result = aviator.execute(
            "TO_BOOLEAN(TO_NUMBER(TO_STRING(42)))", 
            env
        );
        assertEquals(true, result);
    }

    @Test
    void testZeroConversions() {
        // Test edge case with zero
        Object boolResult = aviator.execute("TO_BOOLEAN(0)", env);
        assertEquals(false, boolResult);
        
        Object strResult = aviator.execute("TO_STRING(0)", env);
        assertEquals("0", strResult);
        
        Object numResult = aviator.execute("TO_NUMBER('0')", env);
        assertEquals(0L, numResult);
    }

    // ========== CONVERT_UNIT Integration Tests ==========

    @Test
    void testConvertUnitInExpression() {
        Object result = aviator.execute("CONVERT_UNIT(60, 'seconds', 'minutes')", env);
        assertEquals(1.0, (Double) result, 0.0001);
    }

    @Test
    void testConvertUnitWithArithmetic() {
        Object result = aviator.execute("CONVERT_UNIT(120, 'seconds', 'minutes') + 5", env);
        assertEquals(7.0, (Double) result, 0.0001);
    }

    @Test
    void testConvertUnitWithComparison() {
        Object result = aviator.execute("CONVERT_UNIT(1000, 'meters', 'kilometers') >= 1", env);
        assertEquals(true, result);
    }

    @Test
    void testConvertUnitWithVariable() {
        env.put("distance", 5000);
        Object result = aviator.execute("CONVERT_UNIT(distance, 'meters', 'kilometers')", env);
        assertEquals(5.0, (Double) result, 0.0001);
    }

    @Test
    void testConvertUnitTimeConversion() {
        // Convert 2 hours to minutes
        Object result = aviator.execute("CONVERT_UNIT(2, 'hours', 'minutes')", env);
        assertEquals(120.0, (Double) result, 0.0001);
    }

    @Test
    void testConvertUnitDistanceConversion() {
        // Convert 5 kilometers to meters
        Object result = aviator.execute("CONVERT_UNIT(5, 'kilometers', 'meters')", env);
        assertEquals(5000.0, (Double) result, 0.0001);
    }

    @Test
    void testConvertUnitWeightConversion() {
        // Convert 2 kilograms to grams
        Object result = aviator.execute("CONVERT_UNIT(2, 'kilograms', 'grams')", env);
        assertEquals(2000.0, (Double) result, 0.0001);
    }

    @Test
    void testConvertUnitInCalculation() {
        // Calculate total distance in kilometers
        env.put("distance1", 500);  // meters
        env.put("distance2", 1500); // meters
        Object result = aviator.execute(
            "CONVERT_UNIT(distance1 + distance2, 'meters', 'kilometers')", 
            env
        );
        assertEquals(2.0, (Double) result, 0.0001);
    }

    @Test
    void testConvertUnitWithTypeConversion() {
        // Convert string to number, then convert units
        env.put("timeStr", "120");
        Object result = aviator.execute(
            "CONVERT_UNIT(TO_NUMBER(timeStr), 'seconds', 'minutes')", 
            env
        );
        assertEquals(2.0, (Double) result, 0.0001);
    }

    @Test
    void testConvertUnitResultToString() {
        // Convert units and format as string
        // Note: TO_STRING converts 1.0 to "1" (integer-valued doubles)
        Object result = aviator.execute(
            "TO_STRING(CONVERT_UNIT(1000, 'meters', 'kilometers')) + ' km'", 
            env
        );
        assertEquals("1 km", result);
    }

    @Test
    void testConvertUnitMultipleConversions() {
        // Chain multiple unit conversions
        Object result = aviator.execute(
            "CONVERT_UNIT(CONVERT_UNIT(1, 'hours', 'minutes'), 'minutes', 'seconds')", 
            env
        );
        assertEquals(3600.0, (Double) result, 0.0001);
    }

    @Test
    void testConvertUnitInConditional() {
        // Use unit conversion in conditional logic
        env.put("distance", 2000);
        Object result = aviator.execute(
            "CONVERT_UNIT(distance, 'meters', 'kilometers') > 1 ? 'far' : 'near'", 
            env
        );
        assertEquals("far", result);
    }

    @Test
    void testConvertUnitRealWorldScenario() {
        // Calculate speed in km/h from meters and seconds
        env.put("distanceMeters", 5000);
        env.put("timeSeconds", 600);
        
        // Convert distance to km and time to hours, then calculate speed
        Object result = aviator.execute(
            "CONVERT_UNIT(distanceMeters, 'meters', 'kilometers') / CONVERT_UNIT(timeSeconds, 'seconds', 'hours')", 
            env
        );
        assertEquals(30.0, (Double) result, 0.1);
    }

    @Test
    void testConvertUnitWeightCalculation() {
        // Calculate total weight in pounds from grams
        env.put("item1", 500);  // grams
        env.put("item2", 750);  // grams
        Object result = aviator.execute(
            "CONVERT_UNIT(item1 + item2, 'grams', 'pounds')", 
            env
        );
        assertEquals(2.755, (Double) result, 0.01);
    }

    @Test
    void testConvertUnitTimeRangeCalculation() {
        // Convert days to different time units
        env.put("days", 7);
        
        Object hours = aviator.execute("CONVERT_UNIT(days, 'days', 'hours')", env);
        assertEquals(168.0, (Double) hours, 0.0001);
        
        Object minutes = aviator.execute("CONVERT_UNIT(days, 'days', 'minutes')", env);
        assertEquals(10080.0, (Double) minutes, 0.0001);
    }

    @Test
    void testConvertUnitWithNegativeValue() {
        // Test with negative values (e.g., temperature difference)
        Object result = aviator.execute("CONVERT_UNIT(-10, 'meters', 'kilometers')", env);
        assertEquals(-0.01, (Double) result, 0.0001);
    }

    @Test
    void testConvertUnitWithZero() {
        // Test with zero value
        Object result = aviator.execute("CONVERT_UNIT(0, 'meters', 'kilometers')", env);
        assertEquals(0.0, (Double) result, 0.0001);
    }

    @Test
    void testConvertUnitCaseInsensitive() {
        // Test that unit names are case-insensitive
        Object result = aviator.execute("CONVERT_UNIT(1000, 'METERS', 'KILOMETERS')", env);
        assertEquals(1.0, (Double) result, 0.0001);
    }

    @Test
    void testConvertUnitComplexExpression() {
        // Complex expression with multiple operations
        env.put("weight1", 1000);  // grams
        env.put("weight2", 500);   // grams
        env.put("count", 3);
        
        Object result = aviator.execute(
            "CONVERT_UNIT((weight1 + weight2) * count, 'grams', 'kilograms')", 
            env
        );
        assertEquals(4.5, (Double) result, 0.0001);
    }
}

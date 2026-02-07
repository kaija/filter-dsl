package com.filter.dsl.unit.functions;

import com.filter.dsl.functions.FunctionArgumentException;
import com.filter.dsl.functions.TypeMismatchException;
import com.filter.dsl.functions.conversion.*;
import com.googlecode.aviator.runtime.type.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for type conversion functions: TO_NUMBER, TO_STRING, TO_BOOLEAN, CONVERT_UNIT
 * 
 * Requirements: 10.1, 10.2, 10.3, 10.4, 10.5, 10.6, 10.7, 10.8
 */
class ConversionFunctionsTest {

    private final Map<String, Object> env = new HashMap<>();

    // Helper method to create AviatorObject from a value
    private AviatorObject createAviatorObject(Object value) {
        return new AviatorJavaType("value") {
            @Override
            public Object getValue(Map<String, Object> environment) {
                return value;
            }
        };
    }

    // ========== TO_NUMBER Function Tests ==========

    @Test
    void testToNumberWithIntegerString() {
        ToNumberFunction toNumber = new ToNumberFunction();
        AviatorObject result = toNumber.call(env, new AviatorString("123"));
        assertEquals(123L, result.getValue(env));
    }

    @Test
    void testToNumberWithFloatingPointString() {
        ToNumberFunction toNumber = new ToNumberFunction();
        AviatorObject result = toNumber.call(env, new AviatorString("45.67"));
        assertEquals(45.67, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testToNumberWithNegativeString() {
        ToNumberFunction toNumber = new ToNumberFunction();
        AviatorObject result = toNumber.call(env, new AviatorString("-42"));
        assertEquals(-42L, result.getValue(env));
    }

    @Test
    void testToNumberWithNegativeFloatString() {
        ToNumberFunction toNumber = new ToNumberFunction();
        AviatorObject result = toNumber.call(env, new AviatorString("-3.14"));
        assertEquals(-3.14, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testToNumberWithScientificNotation() {
        ToNumberFunction toNumber = new ToNumberFunction();
        AviatorObject result = toNumber.call(env, new AviatorString("1.5e3"));
        assertEquals(1500.0, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testToNumberWithWhitespace() {
        ToNumberFunction toNumber = new ToNumberFunction();
        AviatorObject result = toNumber.call(env, new AviatorString("  42  "));
        assertEquals(42L, result.getValue(env));
    }

    @Test
    void testToNumberWithBooleanTrue() {
        ToNumberFunction toNumber = new ToNumberFunction();
        AviatorObject result = toNumber.call(env, AviatorBoolean.TRUE);
        assertEquals(1L, result.getValue(env));
    }

    @Test
    void testToNumberWithBooleanFalse() {
        ToNumberFunction toNumber = new ToNumberFunction();
        AviatorObject result = toNumber.call(env, AviatorBoolean.FALSE);
        assertEquals(0L, result.getValue(env));
    }

    @Test
    void testToNumberWithExistingInteger() {
        ToNumberFunction toNumber = new ToNumberFunction();
        AviatorObject result = toNumber.call(env, AviatorLong.valueOf(42));
        assertEquals(42L, result.getValue(env));
    }

    @Test
    void testToNumberWithExistingDouble() {
        ToNumberFunction toNumber = new ToNumberFunction();
        AviatorObject result = toNumber.call(env, AviatorDouble.valueOf(3.14));
        assertEquals(3.14, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testToNumberWithInvalidString() {
        ToNumberFunction toNumber = new ToNumberFunction();
        assertThrows(TypeMismatchException.class, () -> {
            toNumber.call(env, new AviatorString("not a number"));
        });
    }

    @Test
    void testToNumberWithEmptyString() {
        ToNumberFunction toNumber = new ToNumberFunction();
        assertThrows(TypeMismatchException.class, () -> {
            toNumber.call(env, new AviatorString(""));
        });
    }

    @Test
    void testToNumberWithNull() {
        ToNumberFunction toNumber = new ToNumberFunction();
        assertThrows(TypeMismatchException.class, () -> {
            toNumber.call(env, AviatorNil.NIL);
        });
    }

    @Test
    void testToNumberWithIncompatibleType() {
        ToNumberFunction toNumber = new ToNumberFunction();
        assertThrows(TypeMismatchException.class, () -> {
            toNumber.call(env, createAviatorObject(new ArrayList<String>()));
        });
    }

    @Test
    void testToNumberWithWrongArgumentCount() {
        ToNumberFunction toNumber = new ToNumberFunction();
        assertThrows(FunctionArgumentException.class, () -> {
            toNumber.call(env, new AviatorObject[0]);
        });
    }

    @Test
    void testToNumberMetadata() {
        ToNumberFunction toNumber = new ToNumberFunction();
        assertEquals("TO_NUMBER", toNumber.getName());
        assertEquals(1, toNumber.getFunctionMetadata().getMinArgs());
        assertEquals(1, toNumber.getFunctionMetadata().getMaxArgs());
    }

    // ========== TO_STRING Function Tests ==========

    @Test
    void testToStringWithInteger() {
        ToStringFunction toString = new ToStringFunction();
        AviatorObject result = toString.call(env, AviatorLong.valueOf(123));
        assertEquals("123", result.getValue(env));
    }

    @Test
    void testToStringWithDouble() {
        ToStringFunction toString = new ToStringFunction();
        AviatorObject result = toString.call(env, AviatorDouble.valueOf(45.67));
        assertEquals("45.67", result.getValue(env));
    }

    @Test
    void testToStringWithIntegerValuedDouble() {
        ToStringFunction toString = new ToStringFunction();
        AviatorObject result = toString.call(env, AviatorDouble.valueOf(42.0));
        assertEquals("42", result.getValue(env));
    }

    @Test
    void testToStringWithBooleanTrue() {
        ToStringFunction toString = new ToStringFunction();
        AviatorObject result = toString.call(env, AviatorBoolean.TRUE);
        assertEquals("true", result.getValue(env));
    }

    @Test
    void testToStringWithBooleanFalse() {
        ToStringFunction toString = new ToStringFunction();
        AviatorObject result = toString.call(env, AviatorBoolean.FALSE);
        assertEquals("false", result.getValue(env));
    }

    @Test
    void testToStringWithExistingString() {
        ToStringFunction toString = new ToStringFunction();
        AviatorObject result = toString.call(env, new AviatorString("hello"));
        assertEquals("hello", result.getValue(env));
    }

    @Test
    void testToStringWithEmptyString() {
        ToStringFunction toString = new ToStringFunction();
        AviatorObject result = toString.call(env, new AviatorString(""));
        assertEquals("", result.getValue(env));
    }

    @Test
    void testToStringWithNull() {
        ToStringFunction toString = new ToStringFunction();
        AviatorObject result = toString.call(env, AviatorNil.NIL);
        assertNull(result.getValue(env));
    }

    @Test
    void testToStringWithCollection() {
        ToStringFunction toStr = new ToStringFunction();
        List<Integer> list = Arrays.asList(1, 2, 3);
        AviatorObject result = toStr.call(env, createAviatorObject(list));
        assertEquals("[1, 2, 3]", result.getValue(env));
    }

    @Test
    void testToStringWithArray() {
        ToStringFunction toStr = new ToStringFunction();
        int[] array = {1, 2, 3};
        AviatorObject result = toStr.call(env, createAviatorObject(array));
        assertEquals("[1, 2, 3]", result.getValue(env));
    }

    @Test
    void testToStringWithEmptyArray() {
        ToStringFunction toStr = new ToStringFunction();
        int[] array = {};
        AviatorObject result = toStr.call(env, createAviatorObject(array));
        assertEquals("[]", result.getValue(env));
    }

    @Test
    void testToStringWithNegativeNumber() {
        ToStringFunction toString = new ToStringFunction();
        AviatorObject result = toString.call(env, AviatorLong.valueOf(-42));
        assertEquals("-42", result.getValue(env));
    }

    @Test
    void testToStringWithZero() {
        ToStringFunction toString = new ToStringFunction();
        AviatorObject result = toString.call(env, AviatorLong.valueOf(0));
        assertEquals("0", result.getValue(env));
    }

    @Test
    void testToStringWithWrongArgumentCount() {
        ToStringFunction toString = new ToStringFunction();
        assertThrows(FunctionArgumentException.class, () -> {
            toString.call(env, new AviatorObject[0]);
        });
    }

    @Test
    void testToStringMetadata() {
        ToStringFunction toString = new ToStringFunction();
        assertEquals("TO_STRING", toString.getName());
        assertEquals(1, toString.getFunctionMetadata().getMinArgs());
        assertEquals(1, toString.getFunctionMetadata().getMaxArgs());
    }

    // ========== TO_BOOLEAN Function Tests ==========

    @Test
    void testToBooleanWithBooleanTrue() {
        ToBooleanFunction toBoolean = new ToBooleanFunction();
        AviatorObject result = toBoolean.call(env, AviatorBoolean.TRUE);
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testToBooleanWithBooleanFalse() {
        ToBooleanFunction toBoolean = new ToBooleanFunction();
        AviatorObject result = toBoolean.call(env, AviatorBoolean.FALSE);
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testToBooleanWithZero() {
        ToBooleanFunction toBoolean = new ToBooleanFunction();
        AviatorObject result = toBoolean.call(env, AviatorLong.valueOf(0));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testToBooleanWithNonZeroPositive() {
        ToBooleanFunction toBoolean = new ToBooleanFunction();
        AviatorObject result = toBoolean.call(env, AviatorLong.valueOf(42));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testToBooleanWithNonZeroNegative() {
        ToBooleanFunction toBoolean = new ToBooleanFunction();
        AviatorObject result = toBoolean.call(env, AviatorLong.valueOf(-1));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testToBooleanWithZeroDouble() {
        ToBooleanFunction toBoolean = new ToBooleanFunction();
        AviatorObject result = toBoolean.call(env, AviatorDouble.valueOf(0.0));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testToBooleanWithNonZeroDouble() {
        ToBooleanFunction toBoolean = new ToBooleanFunction();
        AviatorObject result = toBoolean.call(env, AviatorDouble.valueOf(3.14));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testToBooleanWithStringTrue() {
        ToBooleanFunction toBoolean = new ToBooleanFunction();
        AviatorObject result = toBoolean.call(env, new AviatorString("true"));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testToBooleanWithStringFalse() {
        ToBooleanFunction toBoolean = new ToBooleanFunction();
        AviatorObject result = toBoolean.call(env, new AviatorString("false"));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testToBooleanWithStringYes() {
        ToBooleanFunction toBoolean = new ToBooleanFunction();
        AviatorObject result = toBoolean.call(env, new AviatorString("yes"));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testToBooleanWithStringNo() {
        ToBooleanFunction toBoolean = new ToBooleanFunction();
        AviatorObject result = toBoolean.call(env, new AviatorString("no"));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testToBooleanWithStringOne() {
        ToBooleanFunction toBoolean = new ToBooleanFunction();
        AviatorObject result = toBoolean.call(env, new AviatorString("1"));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testToBooleanWithStringZero() {
        ToBooleanFunction toBoolean = new ToBooleanFunction();
        AviatorObject result = toBoolean.call(env, new AviatorString("0"));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testToBooleanWithEmptyString() {
        ToBooleanFunction toBoolean = new ToBooleanFunction();
        AviatorObject result = toBoolean.call(env, new AviatorString(""));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testToBooleanWithNonEmptyString() {
        ToBooleanFunction toBoolean = new ToBooleanFunction();
        AviatorObject result = toBoolean.call(env, new AviatorString("hello"));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testToBooleanWithStringCaseInsensitive() {
        ToBooleanFunction toBoolean = new ToBooleanFunction();
        
        // Test various cases of "true"
        assertEquals(Boolean.TRUE, toBoolean.call(env, new AviatorString("TRUE")).getValue(env));
        assertEquals(Boolean.TRUE, toBoolean.call(env, new AviatorString("True")).getValue(env));
        assertEquals(Boolean.TRUE, toBoolean.call(env, new AviatorString("TrUe")).getValue(env));
        
        // Test various cases of "false"
        assertEquals(Boolean.FALSE, toBoolean.call(env, new AviatorString("FALSE")).getValue(env));
        assertEquals(Boolean.FALSE, toBoolean.call(env, new AviatorString("False")).getValue(env));
        assertEquals(Boolean.FALSE, toBoolean.call(env, new AviatorString("FaLsE")).getValue(env));
    }

    @Test
    void testToBooleanWithWhitespace() {
        ToBooleanFunction toBoolean = new ToBooleanFunction();
        AviatorObject result = toBoolean.call(env, new AviatorString("  true  "));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testToBooleanWithNull() {
        ToBooleanFunction toBoolean = new ToBooleanFunction();
        AviatorObject result = toBoolean.call(env, AviatorNil.NIL);
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testToBooleanWithEmptyCollection() {
        ToBooleanFunction toBool = new ToBooleanFunction();
        AviatorObject result = toBool.call(env, createAviatorObject(new ArrayList<String>()));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testToBooleanWithNonEmptyCollection() {
        ToBooleanFunction toBool = new ToBooleanFunction();
        List<Integer> list = Arrays.asList(1, 2, 3);
        AviatorObject result = toBool.call(env, createAviatorObject(list));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testToBooleanWithEmptyArray() {
        ToBooleanFunction toBool = new ToBooleanFunction();
        int[] array = {};
        AviatorObject result = toBool.call(env, createAviatorObject(array));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testToBooleanWithNonEmptyArray() {
        ToBooleanFunction toBool = new ToBooleanFunction();
        int[] array = {1, 2, 3};
        AviatorObject result = toBool.call(env, createAviatorObject(array));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testToBooleanWithObject() {
        ToBooleanFunction toBool = new ToBooleanFunction();
        AviatorObject result = toBool.call(env, createAviatorObject(new Object()));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testToBooleanWithWrongArgumentCount() {
        ToBooleanFunction toBoolean = new ToBooleanFunction();
        assertThrows(FunctionArgumentException.class, () -> {
            toBoolean.call(env, new AviatorObject[0]);
        });
    }

    @Test
    void testToBooleanMetadata() {
        ToBooleanFunction toBoolean = new ToBooleanFunction();
        assertEquals("TO_BOOLEAN", toBoolean.getName());
        assertEquals(1, toBoolean.getFunctionMetadata().getMinArgs());
        assertEquals(1, toBoolean.getFunctionMetadata().getMaxArgs());
    }

    // ========== Integration Tests ==========

    @Test
    void testConversionChain() {
        // Test converting through multiple types
        ToNumberFunction toNumber = new ToNumberFunction();
        ToStringFunction toString = new ToStringFunction();
        ToBooleanFunction toBoolean = new ToBooleanFunction();
        
        // String -> Number -> String
        AviatorObject num = toNumber.call(env, new AviatorString("42"));
        AviatorObject str = toString.call(env, num);
        assertEquals("42", str.getValue(env));
        
        // Number -> Boolean -> String
        AviatorObject bool = toBoolean.call(env, AviatorLong.valueOf(1));
        AviatorObject boolStr = toString.call(env, bool);
        assertEquals("true", boolStr.getValue(env));
    }

    @Test
    void testConversionRoundTrip() {
        ToNumberFunction toNumber = new ToNumberFunction();
        ToStringFunction toString = new ToStringFunction();
        
        // Number -> String -> Number
        AviatorObject original = AviatorLong.valueOf(123);
        AviatorObject str = toString.call(env, original);
        AviatorObject back = toNumber.call(env, str);
        assertEquals(123L, back.getValue(env));
    }

    // ========== CONVERT_UNIT Function Tests ==========

    @Test
    void testConvertUnitTimeSecondsToMinutes() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        AviatorObject result = convertUnit.call(env, 
            AviatorLong.valueOf(60),
            new AviatorString("seconds"),
            new AviatorString("minutes")
        );
        assertEquals(1.0, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testConvertUnitTimeMinutesToHours() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        AviatorObject result = convertUnit.call(env, 
            AviatorLong.valueOf(60),
            new AviatorString("minutes"),
            new AviatorString("hours")
        );
        assertEquals(1.0, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testConvertUnitTimeHoursToDays() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        AviatorObject result = convertUnit.call(env, 
            AviatorLong.valueOf(24),
            new AviatorString("hours"),
            new AviatorString("days")
        );
        assertEquals(1.0, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testConvertUnitTimeDaysToWeeks() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        AviatorObject result = convertUnit.call(env, 
            AviatorLong.valueOf(14),
            new AviatorString("days"),
            new AviatorString("weeks")
        );
        assertEquals(2.0, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testConvertUnitTimeDaysToMonths() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        AviatorObject result = convertUnit.call(env, 
            AviatorLong.valueOf(30),
            new AviatorString("days"),
            new AviatorString("months")
        );
        assertEquals(1.0, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testConvertUnitTimeDaysToYears() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        AviatorObject result = convertUnit.call(env, 
            AviatorLong.valueOf(365),
            new AviatorString("days"),
            new AviatorString("years")
        );
        assertEquals(1.0, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testConvertUnitDistanceMetersToKilometers() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        AviatorObject result = convertUnit.call(env, 
            AviatorLong.valueOf(1000),
            new AviatorString("meters"),
            new AviatorString("kilometers")
        );
        assertEquals(1.0, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testConvertUnitDistanceKilometersToMiles() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        AviatorObject result = convertUnit.call(env, 
            AviatorDouble.valueOf(1.60934),
            new AviatorString("kilometers"),
            new AviatorString("miles")
        );
        assertEquals(1.0, (Double) result.getValue(env), 0.001);
    }

    @Test
    void testConvertUnitDistanceFeetToMeters() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        AviatorObject result = convertUnit.call(env, 
            AviatorLong.valueOf(1),
            new AviatorString("feet"),
            new AviatorString("meters")
        );
        assertEquals(0.3048, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testConvertUnitDistanceMilesToKilometers() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        AviatorObject result = convertUnit.call(env, 
            AviatorLong.valueOf(1),
            new AviatorString("miles"),
            new AviatorString("kilometers")
        );
        assertEquals(1.60934, (Double) result.getValue(env), 0.001);
    }

    @Test
    void testConvertUnitWeightGramsToKilograms() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        AviatorObject result = convertUnit.call(env, 
            AviatorLong.valueOf(1000),
            new AviatorString("grams"),
            new AviatorString("kilograms")
        );
        assertEquals(1.0, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testConvertUnitWeightKilogramsToPounds() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        AviatorObject result = convertUnit.call(env, 
            AviatorDouble.valueOf(0.453592),
            new AviatorString("kilograms"),
            new AviatorString("pounds")
        );
        assertEquals(1.0, (Double) result.getValue(env), 0.001);
    }

    @Test
    void testConvertUnitWeightPoundsToOunces() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        AviatorObject result = convertUnit.call(env, 
            AviatorLong.valueOf(1),
            new AviatorString("pounds"),
            new AviatorString("ounces")
        );
        assertEquals(16.0, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testConvertUnitWeightOuncesToGrams() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        AviatorObject result = convertUnit.call(env, 
            AviatorLong.valueOf(1),
            new AviatorString("ounces"),
            new AviatorString("grams")
        );
        assertEquals(28.3495, (Double) result.getValue(env), 0.001);
    }

    @Test
    void testConvertUnitCaseInsensitive() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        AviatorObject result = convertUnit.call(env, 
            AviatorLong.valueOf(60),
            new AviatorString("SECONDS"),
            new AviatorString("MINUTES")
        );
        assertEquals(1.0, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testConvertUnitWithWhitespace() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        AviatorObject result = convertUnit.call(env, 
            AviatorLong.valueOf(60),
            new AviatorString("  seconds  "),
            new AviatorString("  minutes  ")
        );
        assertEquals(1.0, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testConvertUnitSameUnit() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        AviatorObject result = convertUnit.call(env, 
            AviatorLong.valueOf(42),
            new AviatorString("meters"),
            new AviatorString("meters")
        );
        assertEquals(42.0, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testConvertUnitWithFloatingPoint() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        AviatorObject result = convertUnit.call(env, 
            AviatorDouble.valueOf(2.5),
            new AviatorString("hours"),
            new AviatorString("minutes")
        );
        assertEquals(150.0, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testConvertUnitWithZero() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        AviatorObject result = convertUnit.call(env, 
            AviatorLong.valueOf(0),
            new AviatorString("meters"),
            new AviatorString("kilometers")
        );
        assertEquals(0.0, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testConvertUnitWithNegativeValue() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        AviatorObject result = convertUnit.call(env, 
            AviatorLong.valueOf(-10),
            new AviatorString("meters"),
            new AviatorString("kilometers")
        );
        assertEquals(-0.01, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testConvertUnitUnknownFromUnit() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        assertThrows(FunctionArgumentException.class, () -> {
            convertUnit.call(env, 
                AviatorLong.valueOf(10),
                new AviatorString("unknown"),
                new AviatorString("meters")
            );
        });
    }

    @Test
    void testConvertUnitUnknownToUnit() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        assertThrows(FunctionArgumentException.class, () -> {
            convertUnit.call(env, 
                AviatorLong.valueOf(10),
                new AviatorString("meters"),
                new AviatorString("unknown")
            );
        });
    }

    @Test
    void testConvertUnitIncompatibleUnits() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        assertThrows(FunctionArgumentException.class, () -> {
            convertUnit.call(env, 
                AviatorLong.valueOf(10),
                new AviatorString("meters"),
                new AviatorString("seconds")
            );
        });
    }

    @Test
    void testConvertUnitTimeToDistance() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        assertThrows(FunctionArgumentException.class, () -> {
            convertUnit.call(env, 
                AviatorLong.valueOf(10),
                new AviatorString("hours"),
                new AviatorString("kilometers")
            );
        });
    }

    @Test
    void testConvertUnitWeightToTime() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        assertThrows(FunctionArgumentException.class, () -> {
            convertUnit.call(env, 
                AviatorLong.valueOf(10),
                new AviatorString("grams"),
                new AviatorString("seconds")
            );
        });
    }

    @Test
    void testConvertUnitWithNullValue() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        assertThrows(TypeMismatchException.class, () -> {
            convertUnit.call(env, 
                AviatorNil.NIL,
                new AviatorString("meters"),
                new AviatorString("kilometers")
            );
        });
    }

    @Test
    void testConvertUnitWithNonNumericValue() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        assertThrows(TypeMismatchException.class, () -> {
            convertUnit.call(env, 
                new AviatorString("not a number"),
                new AviatorString("meters"),
                new AviatorString("kilometers")
            );
        });
    }

    @Test
    void testConvertUnitWithNullFromUnit() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        assertThrows(FunctionArgumentException.class, () -> {
            convertUnit.call(env, 
                AviatorLong.valueOf(10),
                AviatorNil.NIL,
                new AviatorString("kilometers")
            );
        });
    }

    @Test
    void testConvertUnitWithNullToUnit() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        assertThrows(FunctionArgumentException.class, () -> {
            convertUnit.call(env, 
                AviatorLong.valueOf(10),
                new AviatorString("meters"),
                AviatorNil.NIL
            );
        });
    }

    @Test
    void testConvertUnitWrongArgumentCount() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        // AviatorScript's AbstractFunction throws IllegalArgumentException for wrong arity
        assertThrows(IllegalArgumentException.class, () -> {
            convertUnit.call(env, 
                AviatorLong.valueOf(10),
                new AviatorString("meters")
            );
        });
    }

    @Test
    void testConvertUnitMetadata() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        assertEquals("CONVERT_UNIT", convertUnit.getName());
        assertEquals(3, convertUnit.getFunctionMetadata().getMinArgs());
        assertEquals(3, convertUnit.getFunctionMetadata().getMaxArgs());
    }

    @Test
    void testConvertUnitSingularAndPluralUnits() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        
        // Test singular forms
        AviatorObject result1 = convertUnit.call(env, 
            AviatorLong.valueOf(60),
            new AviatorString("second"),
            new AviatorString("minute")
        );
        assertEquals(1.0, (Double) result1.getValue(env), 0.0001);
        
        // Test plural forms
        AviatorObject result2 = convertUnit.call(env, 
            AviatorLong.valueOf(60),
            new AviatorString("seconds"),
            new AviatorString("minutes")
        );
        assertEquals(1.0, (Double) result2.getValue(env), 0.0001);
        
        // Test mixed
        AviatorObject result3 = convertUnit.call(env, 
            AviatorLong.valueOf(60),
            new AviatorString("second"),
            new AviatorString("minutes")
        );
        assertEquals(1.0, (Double) result3.getValue(env), 0.0001);
    }

    @Test
    void testConvertUnitAbbreviations() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        
        // Test distance abbreviations
        AviatorObject result1 = convertUnit.call(env, 
            AviatorLong.valueOf(1000),
            new AviatorString("m"),
            new AviatorString("km")
        );
        assertEquals(1.0, (Double) result1.getValue(env), 0.0001);
        
        // Test weight abbreviations
        AviatorObject result2 = convertUnit.call(env, 
            AviatorLong.valueOf(1000),
            new AviatorString("g"),
            new AviatorString("kg")
        );
        assertEquals(1.0, (Double) result2.getValue(env), 0.0001);
    }
}

package com.filter.dsl.unit.functions;

import com.filter.dsl.functions.FunctionArgumentException;
import com.filter.dsl.functions.TypeMismatchException;
import com.filter.dsl.functions.segmentation.BucketFunction;
import com.filter.dsl.models.BucketDefinition;
import com.filter.dsl.models.BucketRange;
import com.googlecode.aviator.runtime.type.AviatorJavaType;
import com.googlecode.aviator.runtime.type.AviatorLong;
import com.googlecode.aviator.runtime.type.AviatorDouble;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for segmentation functions: BUCKET
 */
class SegmentationFunctionsTest {

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

    // ========== BUCKET Function Tests ==========

    @Test
    void testBucketBasicRanges() {
        BucketFunction bucket = new BucketFunction();
        
        // Create bucket definition: [0, 10) -> "Low", [10, 100) -> "Medium", [100, 1000) -> "High"
        BucketDefinition bucketDef = BucketDefinition.builder()
            .range(0.0, 10.0, "Low")
            .range(10.0, 100.0, "Medium")
            .range(100.0, 1000.0, "High")
            .defaultLabel("Other")
            .build();
        
        // Test value in first range
        AviatorObject result1 = bucket.call(env, AviatorLong.valueOf(5), createAviatorObject(bucketDef));
        assertEquals("Low", result1.getValue(env));
        
        // Test value in second range
        AviatorObject result2 = bucket.call(env, AviatorLong.valueOf(50), createAviatorObject(bucketDef));
        assertEquals("Medium", result2.getValue(env));
        
        // Test value in third range
        AviatorObject result3 = bucket.call(env, AviatorLong.valueOf(500), createAviatorObject(bucketDef));
        assertEquals("High", result3.getValue(env));
    }

    @Test
    void testBucketBoundaryInclusive() {
        BucketFunction bucket = new BucketFunction();
        
        // Create bucket with inclusive boundaries: [0, 10] -> "Low", (10, 100] -> "Medium"
        BucketDefinition bucketDef = BucketDefinition.builder()
            .range(BucketRange.builder()
                .minValue(0.0)
                .maxValue(10.0)
                .minInclusive(true)
                .maxInclusive(true)
                .label("Low")
                .build())
            .range(BucketRange.builder()
                .minValue(10.0)
                .maxValue(100.0)
                .minInclusive(false)
                .maxInclusive(true)
                .label("Medium")
                .build())
            .defaultLabel("Other")
            .build();
        
        // Test min boundary (inclusive)
        AviatorObject result1 = bucket.call(env, AviatorLong.valueOf(0), createAviatorObject(bucketDef));
        assertEquals("Low", result1.getValue(env));
        
        // Test max boundary (inclusive) - should match first range
        AviatorObject result2 = bucket.call(env, AviatorLong.valueOf(10), createAviatorObject(bucketDef));
        assertEquals("Low", result2.getValue(env));
        
        // Test value just above 10 (exclusive in second range)
        AviatorObject result3 = bucket.call(env, AviatorDouble.valueOf(10.1), createAviatorObject(bucketDef));
        assertEquals("Medium", result3.getValue(env));
    }

    @Test
    void testBucketBoundaryExclusive() {
        BucketFunction bucket = new BucketFunction();
        
        // Create bucket with exclusive boundaries: [0, 10) -> "Low", [10, 100) -> "Medium"
        BucketDefinition bucketDef = BucketDefinition.builder()
            .range(BucketRange.builder()
                .minValue(0.0)
                .maxValue(10.0)
                .minInclusive(true)
                .maxInclusive(false)
                .label("Low")
                .build())
            .range(BucketRange.builder()
                .minValue(10.0)
                .maxValue(100.0)
                .minInclusive(true)
                .maxInclusive(false)
                .label("Medium")
                .build())
            .defaultLabel("Other")
            .build();
        
        // Test value at boundary (should be in second range)
        AviatorObject result = bucket.call(env, AviatorLong.valueOf(10), createAviatorObject(bucketDef));
        assertEquals("Medium", result.getValue(env));
    }

    @Test
    void testBucketFirstMatchingRange() {
        BucketFunction bucket = new BucketFunction();
        
        // Create overlapping ranges - first match should win
        BucketDefinition bucketDef = BucketDefinition.builder()
            .range(BucketRange.builder()
                .minValue(0.0)
                .maxValue(50.0)
                .minInclusive(true)
                .maxInclusive(true)
                .label("First")
                .build())
            .range(BucketRange.builder()
                .minValue(25.0)
                .maxValue(75.0)
                .minInclusive(true)
                .maxInclusive(true)
                .label("Second")
                .build())
            .defaultLabel("Other")
            .build();
        
        // Value 30 matches both ranges, should return first
        AviatorObject result = bucket.call(env, AviatorLong.valueOf(30), createAviatorObject(bucketDef));
        assertEquals("First", result.getValue(env));
    }

    @Test
    void testBucketDefaultLabel() {
        BucketFunction bucket = new BucketFunction();
        
        // Create bucket with limited ranges
        BucketDefinition bucketDef = BucketDefinition.builder()
            .range(0.0, 10.0, "Low")
            .range(10.0, 100.0, "Medium")
            .defaultLabel("Other")
            .build();
        
        // Test value outside all ranges
        AviatorObject result = bucket.call(env, AviatorLong.valueOf(500), createAviatorObject(bucketDef));
        assertEquals("Other", result.getValue(env));
    }

    @Test
    void testBucketNoDefaultLabel() {
        BucketFunction bucket = new BucketFunction();
        
        // Create bucket without default label
        BucketDefinition bucketDef = BucketDefinition.builder()
            .range(0.0, 10.0, "Low")
            .range(10.0, 100.0, "Medium")
            .defaultLabel(null)
            .build();
        
        // Test value outside all ranges - should return null
        AviatorObject result = bucket.call(env, AviatorLong.valueOf(500), createAviatorObject(bucketDef));
        assertNull(result.getValue(env));
    }

    @Test
    void testBucketWithDoubleValue() {
        BucketFunction bucket = new BucketFunction();
        
        BucketDefinition bucketDef = BucketDefinition.builder()
            .range(0.0, 10.0, "Low")
            .range(10.0, 100.0, "Medium")
            .range(100.0, 1000.0, "High")
            .defaultLabel("Other")
            .build();
        
        // Test with double value
        AviatorObject result = bucket.call(env, AviatorDouble.valueOf(25.5), createAviatorObject(bucketDef));
        assertEquals("Medium", result.getValue(env));
    }

    @Test
    void testBucketWithNegativeValues() {
        BucketFunction bucket = new BucketFunction();
        
        BucketDefinition bucketDef = BucketDefinition.builder()
            .range(-100.0, -10.0, "Very Negative")
            .range(-10.0, 0.0, "Slightly Negative")
            .range(0.0, 10.0, "Slightly Positive")
            .range(10.0, 100.0, "Very Positive")
            .defaultLabel("Other")
            .build();
        
        // Test negative value
        AviatorObject result = bucket.call(env, AviatorLong.valueOf(-50), createAviatorObject(bucketDef));
        assertEquals("Very Negative", result.getValue(env));
    }

    @Test
    void testBucketWithZero() {
        BucketFunction bucket = new BucketFunction();
        
        BucketDefinition bucketDef = BucketDefinition.builder()
            .range(-10.0, 0.0, "Negative")
            .range(0.0, 10.0, "Positive")
            .defaultLabel("Other")
            .build();
        
        // Test zero (should match first range with inclusive min)
        AviatorObject result = bucket.call(env, AviatorLong.valueOf(0), createAviatorObject(bucketDef));
        assertEquals("Positive", result.getValue(env));
    }

    @Test
    void testBucketWithNullValue() {
        BucketFunction bucket = new BucketFunction();
        
        BucketDefinition bucketDef = BucketDefinition.builder()
            .range(0.0, 10.0, "Low")
            .defaultLabel("Other")
            .build();
        
        // Test null value
        AviatorObject result = bucket.call(env, createAviatorObject(null), createAviatorObject(bucketDef));
        assertNull(result.getValue(env));
    }

    @Test
    void testBucketWithNullBucketDefinition() {
        BucketFunction bucket = new BucketFunction();
        
        // Test null bucket definition
        assertThrows(FunctionArgumentException.class, () -> {
            bucket.call(env, AviatorLong.valueOf(5), createAviatorObject(null));
        });
    }

    @Test
    void testBucketWithWrongArgumentCount() {
        BucketFunction bucket = new BucketFunction();
        
        BucketDefinition bucketDef = BucketDefinition.builder()
            .range(0.0, 10.0, "Low")
            .build();
        
        // Test with only one argument - AviatorScript throws IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            bucket.call(env, AviatorLong.valueOf(5));
        });
        
        // Test with three arguments
        assertThrows(IllegalArgumentException.class, () -> {
            bucket.call(env, AviatorLong.valueOf(5), createAviatorObject(bucketDef), AviatorLong.valueOf(10));
        });
    }

    @Test
    void testBucketWithNoArguments() {
        BucketFunction bucket = new BucketFunction();
        assertThrows(FunctionArgumentException.class, () -> {
            bucket.call(env, new AviatorObject[0]);
        });
    }

    @Test
    void testBucketWithNonNumericValue() {
        BucketFunction bucket = new BucketFunction();
        
        BucketDefinition bucketDef = BucketDefinition.builder()
            .range(0.0, 10.0, "Low")
            .build();
        
        // Test with string value
        assertThrows(TypeMismatchException.class, () -> {
            bucket.call(env, new AviatorString("not a number"), createAviatorObject(bucketDef));
        });
    }

    @Test
    void testBucketWithWrongBucketDefinitionType() {
        BucketFunction bucket = new BucketFunction();
        
        // Test with wrong type for bucket definition
        assertThrows(TypeMismatchException.class, () -> {
            bucket.call(env, AviatorLong.valueOf(5), new AviatorString("not a bucket definition"));
        });
    }

    @Test
    void testBucketMetadata() {
        BucketFunction bucket = new BucketFunction();
        assertEquals("BUCKET", bucket.getName());
        assertEquals(2, bucket.getFunctionMetadata().getMinArgs());
        assertEquals(2, bucket.getFunctionMetadata().getMaxArgs());
        assertEquals("Assigns a value to a bucket based on range definitions", bucket.getFunctionMetadata().getDescription());
    }

    @Test
    void testBucketWithEmptyRanges() {
        BucketFunction bucket = new BucketFunction();
        
        // Create bucket with no ranges
        BucketDefinition bucketDef = BucketDefinition.builder()
            .defaultLabel("Default")
            .build();
        
        // Any value should return default
        AviatorObject result = bucket.call(env, AviatorLong.valueOf(50), createAviatorObject(bucketDef));
        assertEquals("Default", result.getValue(env));
    }

    @Test
    void testBucketWithSingleRange() {
        BucketFunction bucket = new BucketFunction();
        
        BucketDefinition bucketDef = BucketDefinition.builder()
            .range(0.0, 100.0, "InRange")
            .defaultLabel("OutOfRange")
            .build();
        
        // Test value in range
        AviatorObject result1 = bucket.call(env, AviatorLong.valueOf(50), createAviatorObject(bucketDef));
        assertEquals("InRange", result1.getValue(env));
        
        // Test value out of range
        AviatorObject result2 = bucket.call(env, AviatorLong.valueOf(150), createAviatorObject(bucketDef));
        assertEquals("OutOfRange", result2.getValue(env));
    }

    @Test
    void testBucketWithOpenEndedRange() {
        BucketFunction bucket = new BucketFunction();
        
        // Create range with null max (open-ended)
        BucketDefinition bucketDef = BucketDefinition.builder()
            .range(BucketRange.builder()
                .minValue(100.0)
                .maxValue(null)
                .minInclusive(true)
                .maxInclusive(false)
                .label("High")
                .build())
            .defaultLabel("Low")
            .build();
        
        // Test very large value (should match open-ended range)
        AviatorObject result = bucket.call(env, AviatorLong.valueOf(10000), createAviatorObject(bucketDef));
        assertEquals("High", result.getValue(env));
    }

    @Test
    void testBucketWithOpenStartedRange() {
        BucketFunction bucket = new BucketFunction();
        
        // Create range with null min (open-started)
        BucketDefinition bucketDef = BucketDefinition.builder()
            .range(BucketRange.builder()
                .minValue(null)
                .maxValue(0.0)
                .minInclusive(false)
                .maxInclusive(false)
                .label("Negative")
                .build())
            .defaultLabel("Positive")
            .build();
        
        // Test very small value (should match open-started range)
        AviatorObject result = bucket.call(env, AviatorLong.valueOf(-10000), createAviatorObject(bucketDef));
        assertEquals("Negative", result.getValue(env));
    }

    @Test
    void testBucketAgeSegmentation() {
        // Real-world example: Age segmentation
        BucketFunction bucket = new BucketFunction();
        
        BucketDefinition ageBuckets = BucketDefinition.builder()
            .range(0.0, 18.0, "Minor")
            .range(18.0, 30.0, "Young Adult")
            .range(30.0, 50.0, "Adult")
            .range(50.0, 65.0, "Middle Age")
            .range(65.0, 120.0, "Senior")
            .defaultLabel("Unknown")
            .build();
        
        // Test various ages
        assertEquals("Minor", bucket.call(env, AviatorLong.valueOf(15), createAviatorObject(ageBuckets)).getValue(env));
        assertEquals("Young Adult", bucket.call(env, AviatorLong.valueOf(25), createAviatorObject(ageBuckets)).getValue(env));
        assertEquals("Adult", bucket.call(env, AviatorLong.valueOf(40), createAviatorObject(ageBuckets)).getValue(env));
        assertEquals("Middle Age", bucket.call(env, AviatorLong.valueOf(55), createAviatorObject(ageBuckets)).getValue(env));
        assertEquals("Senior", bucket.call(env, AviatorLong.valueOf(70), createAviatorObject(ageBuckets)).getValue(env));
    }

    @Test
    void testBucketPurchaseAmountSegmentation() {
        // Real-world example: Purchase amount segmentation
        BucketFunction bucket = new BucketFunction();
        
        BucketDefinition purchaseBuckets = BucketDefinition.builder()
            .range(0.0, 10.0, "Micro")
            .range(10.0, 100.0, "Small")
            .range(100.0, 500.0, "Medium")
            .range(500.0, 5000.0, "Large")
            .range(5000.0, null, "Enterprise")
            .defaultLabel("Invalid")
            .build();
        
        // Test various purchase amounts
        assertEquals("Micro", bucket.call(env, AviatorDouble.valueOf(5.99), createAviatorObject(purchaseBuckets)).getValue(env));
        assertEquals("Small", bucket.call(env, AviatorDouble.valueOf(49.99), createAviatorObject(purchaseBuckets)).getValue(env));
        assertEquals("Medium", bucket.call(env, AviatorDouble.valueOf(299.99), createAviatorObject(purchaseBuckets)).getValue(env));
        assertEquals("Large", bucket.call(env, AviatorDouble.valueOf(1999.99), createAviatorObject(purchaseBuckets)).getValue(env));
        assertEquals("Enterprise", bucket.call(env, AviatorDouble.valueOf(10000.0), createAviatorObject(purchaseBuckets)).getValue(env));
    }
}

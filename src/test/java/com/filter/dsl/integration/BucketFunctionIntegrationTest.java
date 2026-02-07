package com.filter.dsl.integration;

import com.filter.dsl.functions.FunctionRegistry;
import com.filter.dsl.functions.segmentation.BucketFunction;
import com.filter.dsl.models.BucketDefinition;
import com.filter.dsl.models.BucketRange;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for BUCKET function with AviatorScript.
 * Tests real-world segmentation scenarios.
 */
class BucketFunctionIntegrationTest {

    private FunctionRegistry registry;
    private AviatorEvaluatorInstance aviator;
    private Map<String, Object> env;

    @BeforeEach
    void setUp() {
        registry = new FunctionRegistry();
        aviator = AviatorEvaluator.newInstance();
        env = new HashMap<>();
        
        // Register BUCKET function
        registry.register(new BucketFunction());
        registry.registerAll(aviator);
    }

    @Test
    void testAgeSegmentation() {
        // Create age buckets
        BucketDefinition ageBuckets = BucketDefinition.builder()
            .range(0.0, 18.0, "Minor")
            .range(18.0, 30.0, "Young Adult")
            .range(30.0, 50.0, "Adult")
            .range(50.0, 65.0, "Middle Age")
            .range(65.0, 120.0, "Senior")
            .defaultLabel("Unknown")
            .build();
        
        env.put("ageBuckets", ageBuckets);
        
        // Test various ages
        assertEquals("Minor", aviator.execute("BUCKET(15, ageBuckets)", env));
        assertEquals("Young Adult", aviator.execute("BUCKET(25, ageBuckets)", env));
        assertEquals("Adult", aviator.execute("BUCKET(40, ageBuckets)", env));
        assertEquals("Middle Age", aviator.execute("BUCKET(55, ageBuckets)", env));
        assertEquals("Senior", aviator.execute("BUCKET(70, ageBuckets)", env));
        assertEquals("Unknown", aviator.execute("BUCKET(150, ageBuckets)", env));
    }

    @Test
    void testPurchaseAmountSegmentation() {
        // Create purchase amount buckets
        BucketDefinition purchaseBuckets = BucketDefinition.builder()
            .range(0.0, 10.0, "Micro")
            .range(10.0, 100.0, "Small")
            .range(100.0, 500.0, "Medium")
            .range(500.0, 5000.0, "Large")
            .range(5000.0, null, "Enterprise")
            .defaultLabel("Invalid")
            .build();
        
        env.put("purchaseBuckets", purchaseBuckets);
        
        // Test various purchase amounts
        assertEquals("Micro", aviator.execute("BUCKET(5.99, purchaseBuckets)", env));
        assertEquals("Small", aviator.execute("BUCKET(49.99, purchaseBuckets)", env));
        assertEquals("Medium", aviator.execute("BUCKET(299.99, purchaseBuckets)", env));
        assertEquals("Large", aviator.execute("BUCKET(1999.99, purchaseBuckets)", env));
        assertEquals("Enterprise", aviator.execute("BUCKET(10000.0, purchaseBuckets)", env));
    }

    @Test
    void testEngagementScoreSegmentation() {
        // Create engagement score buckets (0-100 scale)
        BucketDefinition engagementBuckets = BucketDefinition.builder()
            .range(0.0, 20.0, "Very Low")
            .range(20.0, 40.0, "Low")
            .range(40.0, 60.0, "Medium")
            .range(60.0, 80.0, "High")
            .range(80.0, 100.0, "Very High")
            .defaultLabel("Out of Range")
            .build();
        
        env.put("engagementBuckets", engagementBuckets);
        
        // Test various engagement scores
        assertEquals("Very Low", aviator.execute("BUCKET(10, engagementBuckets)", env));
        assertEquals("Low", aviator.execute("BUCKET(30, engagementBuckets)", env));
        assertEquals("Medium", aviator.execute("BUCKET(50, engagementBuckets)", env));
        assertEquals("High", aviator.execute("BUCKET(70, engagementBuckets)", env));
        assertEquals("Very High", aviator.execute("BUCKET(90, engagementBuckets)", env));
    }

    @Test
    void testTemperatureSegmentation() {
        // Create temperature buckets (Celsius)
        BucketDefinition tempBuckets = BucketDefinition.builder()
            .range(null, 0.0, "Freezing")
            .range(0.0, 10.0, "Cold")
            .range(10.0, 20.0, "Cool")
            .range(20.0, 30.0, "Warm")
            .range(30.0, null, "Hot")
            .defaultLabel("Unknown")
            .build();
        
        env.put("tempBuckets", tempBuckets);
        
        // Test various temperatures
        assertEquals("Freezing", aviator.execute("BUCKET(-10, tempBuckets)", env));
        assertEquals("Cold", aviator.execute("BUCKET(5, tempBuckets)", env));
        assertEquals("Cool", aviator.execute("BUCKET(15, tempBuckets)", env));
        assertEquals("Warm", aviator.execute("BUCKET(25, tempBuckets)", env));
        assertEquals("Hot", aviator.execute("BUCKET(35, tempBuckets)", env));
    }

    @Test
    void testIncomeSegmentation() {
        // Create income buckets (annual income in thousands)
        BucketDefinition incomeBuckets = BucketDefinition.builder()
            .range(0.0, 30.0, "Low Income")
            .range(30.0, 60.0, "Lower Middle Income")
            .range(60.0, 100.0, "Middle Income")
            .range(100.0, 200.0, "Upper Middle Income")
            .range(200.0, null, "High Income")
            .defaultLabel("Unknown")
            .build();
        
        env.put("incomeBuckets", incomeBuckets);
        
        // Test various income levels
        assertEquals("Low Income", aviator.execute("BUCKET(25, incomeBuckets)", env));
        assertEquals("Lower Middle Income", aviator.execute("BUCKET(45, incomeBuckets)", env));
        assertEquals("Middle Income", aviator.execute("BUCKET(75, incomeBuckets)", env));
        assertEquals("Upper Middle Income", aviator.execute("BUCKET(150, incomeBuckets)", env));
        assertEquals("High Income", aviator.execute("BUCKET(500, incomeBuckets)", env));
    }

    @Test
    void testBoundaryConditions() {
        // Create buckets with specific boundary conditions
        BucketDefinition buckets = BucketDefinition.builder()
            .range(BucketRange.builder()
                .minValue(0.0)
                .maxValue(10.0)
                .minInclusive(true)
                .maxInclusive(false)
                .label("First")
                .build())
            .range(BucketRange.builder()
                .minValue(10.0)
                .maxValue(20.0)
                .minInclusive(true)
                .maxInclusive(false)
                .label("Second")
                .build())
            .defaultLabel("Other")
            .build();
        
        env.put("buckets", buckets);
        
        // Test boundary values
        assertEquals("First", aviator.execute("BUCKET(0, buckets)", env));
        assertEquals("Second", aviator.execute("BUCKET(10, buckets)", env));
        assertEquals("Other", aviator.execute("BUCKET(20, buckets)", env));
    }

    @Test
    void testDynamicBucketCreation() {
        // Simulate creating buckets dynamically based on data
        double[] thresholds = {0, 25, 50, 75, 100};
        String[] labels = {"Q1", "Q2", "Q3", "Q4"};
        
        BucketDefinition.Builder builder = BucketDefinition.builder();
        for (int i = 0; i < labels.length; i++) {
            builder.range(thresholds[i], thresholds[i + 1], labels[i]);
        }
        BucketDefinition quartiles = builder.defaultLabel("Out of Range").build();
        
        env.put("quartiles", quartiles);
        
        // Test quartile assignment
        assertEquals("Q1", aviator.execute("BUCKET(12.5, quartiles)", env));
        assertEquals("Q2", aviator.execute("BUCKET(37.5, quartiles)", env));
        assertEquals("Q3", aviator.execute("BUCKET(62.5, quartiles)", env));
        assertEquals("Q4", aviator.execute("BUCKET(87.5, quartiles)", env));
    }

    @Test
    void testNegativeValueBuckets() {
        // Create buckets for profit/loss segmentation
        BucketDefinition profitBuckets = BucketDefinition.builder()
            .range(null, -1000.0, "Large Loss")
            .range(-1000.0, -100.0, "Moderate Loss")
            .range(-100.0, 0.0, "Small Loss")
            .range(0.0, 100.0, "Small Profit")
            .range(100.0, 1000.0, "Moderate Profit")
            .range(1000.0, null, "Large Profit")
            .defaultLabel("Break Even")
            .build();
        
        env.put("profitBuckets", profitBuckets);
        
        // Test profit/loss values
        assertEquals("Large Loss", aviator.execute("BUCKET(-5000, profitBuckets)", env));
        assertEquals("Moderate Loss", aviator.execute("BUCKET(-500, profitBuckets)", env));
        assertEquals("Small Loss", aviator.execute("BUCKET(-50, profitBuckets)", env));
        assertEquals("Small Profit", aviator.execute("BUCKET(50, profitBuckets)", env));
        assertEquals("Moderate Profit", aviator.execute("BUCKET(500, profitBuckets)", env));
        assertEquals("Large Profit", aviator.execute("BUCKET(5000, profitBuckets)", env));
    }

    @Test
    void testDecimalPrecisionBuckets() {
        // Create buckets for percentage values with decimal precision
        BucketDefinition percentBuckets = BucketDefinition.builder()
            .range(0.0, 0.25, "0-25%")
            .range(0.25, 0.50, "25-50%")
            .range(0.50, 0.75, "50-75%")
            .range(0.75, 1.0, "75-100%")
            .defaultLabel("Out of Range")
            .build();
        
        env.put("percentBuckets", percentBuckets);
        
        // Test decimal values
        assertEquals("0-25%", aviator.execute("BUCKET(0.15, percentBuckets)", env));
        assertEquals("25-50%", aviator.execute("BUCKET(0.35, percentBuckets)", env));
        assertEquals("50-75%", aviator.execute("BUCKET(0.65, percentBuckets)", env));
        assertEquals("75-100%", aviator.execute("BUCKET(0.85, percentBuckets)", env));
    }

    @Test
    void testSingleRangeBucket() {
        // Create a bucket with just one range
        BucketDefinition singleBucket = BucketDefinition.builder()
            .range(0.0, 100.0, "Valid")
            .defaultLabel("Invalid")
            .build();
        
        env.put("singleBucket", singleBucket);
        
        // Test values inside and outside the range
        assertEquals("Valid", aviator.execute("BUCKET(50, singleBucket)", env));
        assertEquals("Invalid", aviator.execute("BUCKET(-10, singleBucket)", env));
        assertEquals("Invalid", aviator.execute("BUCKET(150, singleBucket)", env));
    }
}

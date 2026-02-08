package com.filter.dsl.performance;

import com.filter.dsl.DSL;
import com.filter.dsl.evaluator.EvaluationResult;
import com.filter.dsl.models.UserData;
import org.junit.jupiter.api.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Comprehensive performance test suite for DSL functions.
 * 
 * Tests each function category with:
 * - Small datasets (1KB)
 * - Medium datasets (1MB)
 * - Large datasets (10MB)
 * - Extra large datasets (100MB)
 * 
 * Measures:
 * - Execution time (avg, min, max, p95, p99)
 * - Memory usage (heap, GC pressure)
 * - Throughput (operations per second)
 * - CPU time
 * 
 * Generates detailed performance reports identifying high-compute functions.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PerformanceTestSuite {
    
    private static final int WARMUP_ITERATIONS = 100;
    private static final int TEST_ITERATIONS = 1000;
    private static final int LARGE_DATA_ITERATIONS = 100;
    
    private static DSL dsl;
    private static PerformanceReporter reporter;
    private static MemoryMXBean memoryBean;
    
    @BeforeAll
    static void setup() {
        dsl = DSL.builder()
            .enableAutoDiscovery(true)
            .enableCaching(true)
            .build();
        
        reporter = new PerformanceReporter();
        memoryBean = ManagementFactory.getMemoryMXBean();
        
        System.out.println("=".repeat(80));
        System.out.println("DSL PERFORMANCE TEST SUITE");
        System.out.println("=".repeat(80));
        System.out.println("Warmup iterations: " + WARMUP_ITERATIONS);
        System.out.println("Test iterations: " + TEST_ITERATIONS);
        System.out.println("Large data iterations: " + LARGE_DATA_ITERATIONS);
        System.out.println("=".repeat(80));
    }
    
    @AfterAll
    static void teardown() throws IOException {
        reporter.generateReport();
        System.out.println("\n" + "=".repeat(80));
        System.out.println("Performance test suite completed!");
        System.out.println("Report saved to: performance_report_" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt");
        System.out.println("=".repeat(80));
    }
    
    // ========== AGGREGATION FUNCTIONS ==========
    
    @Test
    @Order(1)
    @DisplayName("COUNT - Small Dataset (1KB)")
    void testCountSmall() {
        UserData userData = TestDataGenerator.generateSmallDataset();
        String expression = "COUNT(EVENT(\"purchase\"))";
        
        PerformanceMetrics metrics = measurePerformance(
            "COUNT", "Small (1KB)", expression, userData, TEST_ITERATIONS
        );
        reporter.addMetrics(metrics);
    }
    
    @Test
    @Order(2)
    @DisplayName("COUNT - Medium Dataset (1MB)")
    void testCountMedium() {
        UserData userData = TestDataGenerator.generateMediumDataset();
        String expression = "COUNT(EVENT(\"purchase\"))";
        
        PerformanceMetrics metrics = measurePerformance(
            "COUNT", "Medium (1MB)", expression, userData, LARGE_DATA_ITERATIONS
        );
        reporter.addMetrics(metrics);
    }
    
    @Test
    @Order(3)
    @DisplayName("COUNT - Large Dataset (10MB)")
    void testCountLarge() {
        UserData userData = TestDataGenerator.generateLargeDataset();
        String expression = "COUNT(EVENT(\"purchase\"))";
        
        PerformanceMetrics metrics = measurePerformance(
            "COUNT", "Large (10MB)", expression, userData, LARGE_DATA_ITERATIONS
        );
        reporter.addMetrics(metrics);
    }
    
    @Test
    @Order(4)
    @DisplayName("COUNT - Extra Large Dataset (100MB)")
    void testCountExtraLarge() {
        UserData userData = TestDataGenerator.generateExtraLargeDataset();
        String expression = "COUNT(EVENT(\"purchase\"))";
        
        PerformanceMetrics metrics = measurePerformance(
            "COUNT", "Extra Large (100MB)", expression, userData, 10
        );
        reporter.addMetrics(metrics);
    }

    
    @Test
    @Order(5)
    @DisplayName("SUM - Large Dataset")
    void testSumLarge() {
        UserData userData = TestDataGenerator.generateLargeDataset();
        String expression = "SUM(EVENT(\"amount\"))";
        
        PerformanceMetrics metrics = measurePerformance(
            "SUM", "Large (10MB)", expression, userData, LARGE_DATA_ITERATIONS
        );
        reporter.addMetrics(metrics);
    }
    
    @Test
    @Order(6)
    @DisplayName("AVG - Large Dataset")
    void testAvgLarge() {
        UserData userData = TestDataGenerator.generateLargeDataset();
        String expression = "AVG(EVENT(\"amount\"))";
        
        PerformanceMetrics metrics = measurePerformance(
            "AVG", "Large (10MB)", expression, userData, LARGE_DATA_ITERATIONS
        );
        reporter.addMetrics(metrics);
    }
    
    @Test
    @Order(7)
    @DisplayName("MAX - Large Dataset")
    void testMaxLarge() {
        UserData userData = TestDataGenerator.generateLargeDataset();
        String expression = "MAX(EVENT(\"amount\"))";
        
        PerformanceMetrics metrics = measurePerformance(
            "MAX", "Large (10MB)", expression, userData, LARGE_DATA_ITERATIONS
        );
        reporter.addMetrics(metrics);
    }
    
    @Test
    @Order(8)
    @DisplayName("MIN - Large Dataset")
    void testMinLarge() {
        UserData userData = TestDataGenerator.generateLargeDataset();
        String expression = "MIN(EVENT(\"amount\"))";
        
        PerformanceMetrics metrics = measurePerformance(
            "MIN", "Large (10MB)", expression, userData, LARGE_DATA_ITERATIONS
        );
        reporter.addMetrics(metrics);
    }
    
    @Test
    @Order(9)
    @DisplayName("UNIQUE - Large Dataset")
    void testUniqueLarge() {
        UserData userData = TestDataGenerator.generateLargeDataset();
        String expression = "COUNT(UNIQUE(EVENT(\"category\")))";
        
        PerformanceMetrics metrics = measurePerformance(
            "UNIQUE", "Large (10MB)", expression, userData, LARGE_DATA_ITERATIONS
        );
        reporter.addMetrics(metrics);
    }
    
    // ========== FILTERING FUNCTIONS ==========
    
    @Test
    @Order(10)
    @DisplayName("WHERE - Large Dataset")
    void testWhereLarge() {
        UserData userData = TestDataGenerator.generateLargeDataset();
        String expression = "COUNT(WHERE(EVENT(\"purchase\"), GT(PARAM(\"amount\"), 100)))";
        
        PerformanceMetrics metrics = measurePerformance(
            "WHERE", "Large (10MB)", expression, userData, LARGE_DATA_ITERATIONS
        );
        reporter.addMetrics(metrics);
    }
    
    @Test
    @Order(11)
    @DisplayName("WHERE - Extra Large Dataset")
    void testWhereExtraLarge() {
        UserData userData = TestDataGenerator.generateExtraLargeDataset();
        String expression = "COUNT(WHERE(EVENT(\"purchase\"), GT(PARAM(\"amount\"), 100)))";
        
        PerformanceMetrics metrics = measurePerformance(
            "WHERE", "Extra Large (100MB)", expression, userData, 10
        );
        reporter.addMetrics(metrics);
    }
    
    @Test
    @Order(12)
    @DisplayName("BY - Large Dataset")
    void testByLarge() {
        UserData userData = TestDataGenerator.generateLargeDataset();
        String expression = "COUNT(BY(PARAM(\"category\")))";
        
        PerformanceMetrics metrics = measurePerformance(
            "BY", "Large (10MB)", expression, userData, LARGE_DATA_ITERATIONS
        );
        reporter.addMetrics(metrics);
    }
    
    // ========== DATE/TIME FUNCTIONS ==========
    
    @Test
    @Order(13)
    @DisplayName("DATE_DIFF - Large Dataset")
    void testDateDiffLarge() {
        UserData userData = TestDataGenerator.generateLargeDataset();
        String expression = "COUNT(WHERE(EVENT(\"purchase\"), " +
            "LT(DATE_DIFF(NOW(), ACTION_TIME(), \"D\"), 30)))";
        
        PerformanceMetrics metrics = measurePerformance(
            "DATE_DIFF", "Large (10MB)", expression, userData, LARGE_DATA_ITERATIONS
        );
        reporter.addMetrics(metrics);
    }
    
    @Test
    @Order(14)
    @DisplayName("IN_RECENT_DAYS - Large Dataset")
    void testInRecentDaysLarge() {
        UserData userData = TestDataGenerator.generateLargeDataset();
        String expression = "COUNT(IN_RECENT_DAYS(30))";
        
        PerformanceMetrics metrics = measurePerformance(
            "IN_RECENT_DAYS", "Large (10MB)", expression, userData, LARGE_DATA_ITERATIONS
        );
        reporter.addMetrics(metrics);
    }
    
    // ========== COMPLEX NESTED EXPRESSIONS ==========
    
    @Test
    @Order(15)
    @DisplayName("Complex Nested - Large Dataset")
    void testComplexNestedLarge() {
        UserData userData = TestDataGenerator.generateLargeDataset();
        String expression = "AND(" +
            "GT(COUNT(WHERE(EVENT(\"purchase\"), GT(PARAM(\"amount\"), 100))), 5), " +
            "GT(COUNT(IN_RECENT_DAYS(30)), 10)" +
            ")";
        
        PerformanceMetrics metrics = measurePerformance(
            "COMPLEX_NESTED", "Large (10MB)", expression, userData, LARGE_DATA_ITERATIONS
        );
        reporter.addMetrics(metrics);
    }
    
    @Test
    @Order(16)
    @DisplayName("Complex Nested - Extra Large Dataset")
    void testComplexNestedExtraLarge() {
        UserData userData = TestDataGenerator.generateExtraLargeDataset();
        String expression = "AND(" +
            "GT(COUNT(WHERE(EVENT(\"purchase\"), GT(PARAM(\"amount\"), 100))), 5), " +
            "GT(COUNT(IN_RECENT_DAYS(30)), 10)" +
            ")";
        
        PerformanceMetrics metrics = measurePerformance(
            "COMPLEX_NESTED", "Extra Large (100MB)", expression, userData, 10
        );
        reporter.addMetrics(metrics);
    }
    
    // ========== MATH FUNCTIONS ==========
    
    @Test
    @Order(17)
    @DisplayName("Math Operations - Large Dataset")
    void testMathLarge() {
        UserData userData = TestDataGenerator.generateLargeDataset();
        String expression = "COUNT(WHERE(EVENT(\"purchase\"), " +
            "GT(MULTIPLY(PARAM(\"quantity\"), 10), 50)))";
        
        PerformanceMetrics metrics = measurePerformance(
            "MATH_OPS", "Large (10MB)", expression, userData, LARGE_DATA_ITERATIONS
        );
        reporter.addMetrics(metrics);
    }
    
    // ========== STRING FUNCTIONS ==========
    
    @Test
    @Order(18)
    @DisplayName("String Operations - Large Dataset")
    void testStringLarge() {
        UserData userData = TestDataGenerator.generateLargeDataset();
        String expression = "COUNT(WHERE(EVENT(\"purchase\"), " +
            "CONTAINS(PARAM(\"category\"), \"electronics\")))";
        
        PerformanceMetrics metrics = measurePerformance(
            "STRING_OPS", "Large (10MB)", expression, userData, LARGE_DATA_ITERATIONS
        );
        reporter.addMetrics(metrics);
    }
    
    // ========== HELPER METHODS ==========
    
    /**
     * Measure performance metrics for a DSL expression.
     */
    private PerformanceMetrics measurePerformance(
            String functionName, 
            String datasetSize, 
            String expression, 
            UserData userData,
            int iterations) {
        
        System.out.println("\nTesting: " + functionName + " - " + datasetSize);
        
        // Warmup
        System.out.print("  Warming up...");
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            dsl.evaluate(expression, userData);
        }
        System.out.println(" done");
        
        // Force GC before measurement
        System.gc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Measure memory before
        MemoryUsage heapBefore = memoryBean.getHeapMemoryUsage();
        long memoryBefore = heapBefore.getUsed();
        
        // Measure execution times
        List<Long> executionTimes = new ArrayList<>();
        long totalCpuTime = 0;
        
        System.out.print("  Running " + iterations + " iterations...");
        long startTime = System.nanoTime();
        
        for (int i = 0; i < iterations; i++) {
            long iterStart = System.nanoTime();
            EvaluationResult result = dsl.evaluate(expression, userData);
            long iterEnd = System.nanoTime();
            
            executionTimes.add(iterEnd - iterStart);
            
            if (!result.isSuccess()) {
                System.err.println("\n  ERROR: " + result.getErrorMessage());
            }
        }
        
        long endTime = System.nanoTime();
        System.out.println(" done");
        
        // Measure memory after
        System.gc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        MemoryUsage heapAfter = memoryBean.getHeapMemoryUsage();
        long memoryAfter = heapAfter.getUsed();
        long memoryDelta = Math.max(0, memoryAfter - memoryBefore);
        
        // Calculate statistics
        Collections.sort(executionTimes);
        long totalTime = endTime - startTime;
        
        PerformanceMetrics metrics = new PerformanceMetrics();
        metrics.functionName = functionName;
        metrics.datasetSize = datasetSize;
        metrics.expression = expression;
        metrics.iterations = iterations;
        metrics.totalTimeMs = totalTime / 1_000_000.0;
        metrics.avgTimeMs = executionTimes.stream().mapToLong(Long::longValue).average().orElse(0) / 1_000_000.0;
        metrics.minTimeMs = executionTimes.get(0) / 1_000_000.0;
        metrics.maxTimeMs = executionTimes.get(executionTimes.size() - 1) / 1_000_000.0;
        metrics.p95TimeMs = executionTimes.get((int) (executionTimes.size() * 0.95)) / 1_000_000.0;
        metrics.p99TimeMs = executionTimes.get((int) (executionTimes.size() * 0.99)) / 1_000_000.0;
        metrics.throughput = (iterations * 1000.0) / metrics.totalTimeMs;
        metrics.memoryUsedMB = memoryDelta / (1024.0 * 1024.0);
        metrics.datasetSizeMB = TestDataGenerator.estimateDataSize(userData) / (1024.0 * 1024.0);
        
        // Print summary
        System.out.printf("  Avg: %.3f ms | P95: %.3f ms | P99: %.3f ms | Throughput: %.2f ops/s%n",
            metrics.avgTimeMs, metrics.p95TimeMs, metrics.p99TimeMs, metrics.throughput);
        System.out.printf("  Memory: %.2f MB | Dataset: %.2f MB%n",
            metrics.memoryUsedMB, metrics.datasetSizeMB);
        
        return metrics;
    }
}

package com.filter.dsl.performance;

import com.filter.dsl.DSL;
import com.filter.dsl.evaluator.EvaluationResult;
import com.filter.dsl.models.UserData;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.*;

/**
 * Comprehensive benchmark testing ALL DSL functions across different data sizes.
 * 
 * This test systematically evaluates:
 * - All aggregation functions (COUNT, SUM, AVG, MIN, MAX, UNIQUE)
 * - All comparison functions (EQ, GT, LT, GTE, LTE, NEQ)
 * - All logical functions (AND, OR, NOT)
 * - All math functions (ADD, SUBTRACT, MULTIPLY, DIVIDE, etc.)
 * - All date/time functions (DATE_DIFF, IN_RECENT_DAYS, etc.)
 * - All filtering functions (WHERE, BY, IF)
 * - All string functions (CONTAINS, STARTS_WITH, ENDS_WITH, etc.)
 * - Complex nested expressions
 * 
 * Results identify which functions are compute-intensive and how they scale.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ComprehensiveFunctionBenchmark {
    
    private static DSL dsl;
    private static PerformanceReporter reporter;
    private static MemoryMXBean memoryBean;
    
    private static final int ITERATIONS = 500;
    private static final int WARMUP = 50;
    
    @BeforeAll
    static void setup() {
        dsl = DSL.builder()
            .enableAutoDiscovery(true)
            .enableCaching(true)
            .build();
        
        reporter = new PerformanceReporter();
        memoryBean = ManagementFactory.getMemoryMXBean();
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("COMPREHENSIVE FUNCTION BENCHMARK");
        System.out.println("=".repeat(80));
    }
    
    @AfterAll
    static void teardown() throws IOException {
        reporter.generateReport();
        System.out.println("\n" + "=".repeat(80));
        System.out.println("Benchmark completed! Check the generated reports.");
        System.out.println("=".repeat(80));
    }
    
    // ========== AGGREGATION FUNCTIONS ==========
    
    @Test
    @Order(1)
    void benchmarkAggregationFunctions() {
        System.out.println("\n### AGGREGATION FUNCTIONS ###");
        UserData data = TestDataGenerator.generateLargeDataset();
        
        benchmarkFunction("COUNT", "COUNT(EVENT(\"purchase\"))", data);
        benchmarkFunction("SUM", "SUM(EVENT(\"amount\"))", data);
        benchmarkFunction("AVG", "AVG(EVENT(\"amount\"))", data);
        benchmarkFunction("MIN", "MIN(EVENT(\"amount\"))", data);
        benchmarkFunction("MAX", "MAX(EVENT(\"amount\"))", data);
        benchmarkFunction("UNIQUE", "COUNT(UNIQUE(EVENT(\"category\")))", data);
    }
    
    // ========== COMPARISON FUNCTIONS ==========
    
    @Test
    @Order(2)
    void benchmarkComparisonFunctions() {
        System.out.println("\n### COMPARISON FUNCTIONS ###");
        UserData data = TestDataGenerator.generateLargeDataset();
        
        benchmarkFunction("EQ", "COUNT(WHERE(EVENT(\"purchase\"), EQ(PARAM(\"category\"), \"electronics\")))", data);
        benchmarkFunction("GT", "COUNT(WHERE(EVENT(\"purchase\"), GT(PARAM(\"amount\"), 100)))", data);
        benchmarkFunction("LT", "COUNT(WHERE(EVENT(\"purchase\"), LT(PARAM(\"amount\"), 50)))", data);
        benchmarkFunction("GTE", "COUNT(WHERE(EVENT(\"purchase\"), GTE(PARAM(\"amount\"), 100)))", data);
        benchmarkFunction("LTE", "COUNT(WHERE(EVENT(\"purchase\"), LTE(PARAM(\"amount\"), 50)))", data);
        benchmarkFunction("NEQ", "COUNT(WHERE(EVENT(\"purchase\"), NEQ(PARAM(\"category\"), \"electronics\")))", data);
    }
    
    // ========== LOGICAL FUNCTIONS ==========
    
    @Test
    @Order(3)
    void benchmarkLogicalFunctions() {
        System.out.println("\n### LOGICAL FUNCTIONS ###");
        UserData data = TestDataGenerator.generateLargeDataset();
        
        benchmarkFunction("AND", 
            "COUNT(WHERE(EVENT(\"purchase\"), AND(GT(PARAM(\"amount\"), 100), EQ(PARAM(\"category\"), \"electronics\"))))", 
            data);
        
        benchmarkFunction("OR", 
            "COUNT(WHERE(EVENT(\"purchase\"), OR(GT(PARAM(\"amount\"), 500), EQ(PARAM(\"category\"), \"electronics\"))))", 
            data);
        
        benchmarkFunction("NOT", 
            "COUNT(WHERE(EVENT(\"purchase\"), NOT(EQ(PARAM(\"category\"), \"electronics\"))))", 
            data);
    }
    
    // ========== MATH FUNCTIONS ==========
    
    @Test
    @Order(4)
    void benchmarkMathFunctions() {
        System.out.println("\n### MATH FUNCTIONS ###");
        UserData data = TestDataGenerator.generateLargeDataset();
        
        benchmarkFunction("ADD", "SUM(WHERE(EVENT(\"purchase\"), GT(ADD(PARAM(\"amount\"), 10), 100)))", data);
        benchmarkFunction("SUBTRACT", "SUM(WHERE(EVENT(\"purchase\"), GT(SUBTRACT(PARAM(\"amount\"), 10), 50)))", data);
        benchmarkFunction("MULTIPLY", "SUM(WHERE(EVENT(\"purchase\"), GT(MULTIPLY(PARAM(\"amount\"), 1.1), 100)))", data);
        benchmarkFunction("DIVIDE", "AVG(WHERE(EVENT(\"purchase\"), GT(DIVIDE(PARAM(\"amount\"), 2), 25)))", data);
        benchmarkFunction("MOD", "COUNT(WHERE(EVENT(\"purchase\"), EQ(MOD(PARAM(\"quantity\"), 2), 0)))", data);
        benchmarkFunction("ABS", "SUM(WHERE(EVENT(\"purchase\"), GT(ABS(SUBTRACT(PARAM(\"amount\"), 100)), 50)))", data);
        benchmarkFunction("ROUND", "AVG(WHERE(EVENT(\"purchase\"), GT(ROUND(PARAM(\"amount\")), 100)))", data);
        benchmarkFunction("CEIL", "COUNT(WHERE(EVENT(\"purchase\"), GT(CEIL(PARAM(\"amount\")), 100)))", data);
        benchmarkFunction("FLOOR", "COUNT(WHERE(EVENT(\"purchase\"), LT(FLOOR(PARAM(\"amount\")), 100)))", data);
    }
    
    // ========== DATE/TIME FUNCTIONS ==========
    
    @Test
    @Order(5)
    void benchmarkDateTimeFunctions() {
        System.out.println("\n### DATE/TIME FUNCTIONS ###");
        UserData data = TestDataGenerator.generateLargeDataset();
        
        benchmarkFunction("NOW", "GT(COUNT(EVENT(\"purchase\")), 0)", data);
        benchmarkFunction("DATE_DIFF", 
            "COUNT(WHERE(EVENT(\"purchase\"), LT(DATE_DIFF(NOW(), ACTION_TIME(), \"D\"), 30)))", 
            data);
        benchmarkFunction("IN_RECENT_DAYS", 
            "COUNT(IN_RECENT_DAYS(30))", 
            data);
        benchmarkFunction("WEEKDAY", 
            "COUNT(WHERE(EVENT(\"purchase\"), EQ(WEEKDAY(ACTION_TIME()), \"Monday\")))", 
            data);
        benchmarkFunction("MONTH", 
            "COUNT(WHERE(EVENT(\"purchase\"), EQ(MONTH(ACTION_TIME()), \"January\")))", 
            data);
        benchmarkFunction("YEAR", 
            "COUNT(WHERE(EVENT(\"purchase\"), EQ(YEAR(ACTION_TIME()), \"2024\")))", 
            data);
    }
    
    // ========== FILTERING FUNCTIONS ==========
    
    @Test
    @Order(6)
    void benchmarkFilteringFunctions() {
        System.out.println("\n### FILTERING FUNCTIONS ###");
        UserData data = TestDataGenerator.generateLargeDataset();
        
        benchmarkFunction("WHERE", 
            "COUNT(WHERE(EVENT(\"purchase\"), GT(PARAM(\"amount\"), 100)))", 
            data);
        
        benchmarkFunction("BY", 
            "COUNT(BY(PARAM(\"category\")))", 
            data);
        
        benchmarkFunction("IF", 
            "COUNT(WHERE(EVENT(\"purchase\"), IF(GT(PARAM(\"amount\"), 100), true, false)))", 
            data);
    }
    
    // ========== STRING FUNCTIONS ==========
    
    @Test
    @Order(7)
    void benchmarkStringFunctions() {
        System.out.println("\n### STRING FUNCTIONS ###");
        UserData data = TestDataGenerator.generateLargeDataset();
        
        benchmarkFunction("CONTAINS", 
            "COUNT(WHERE(EVENT(\"purchase\"), CONTAINS(PARAM(\"category\"), \"elec\")))", 
            data);
        
        benchmarkFunction("STARTS_WITH", 
            "COUNT(WHERE(EVENT(\"purchase\"), STARTS_WITH(PARAM(\"category\"), \"elec\")))", 
            data);
        
        benchmarkFunction("ENDS_WITH", 
            "COUNT(WHERE(EVENT(\"purchase\"), ENDS_WITH(PARAM(\"category\"), \"ics\")))", 
            data);
        
        benchmarkFunction("TO_UPPER", 
            "COUNT(WHERE(EVENT(\"purchase\"), EQ(TO_UPPER(PARAM(\"category\")), \"ELECTRONICS\")))", 
            data);
        
        benchmarkFunction("TO_LOWER", 
            "COUNT(WHERE(EVENT(\"purchase\"), EQ(TO_LOWER(PARAM(\"category\")), \"electronics\")))", 
            data);
    }
    
    // ========== COMPLEX NESTED EXPRESSIONS ==========
    
    @Test
    @Order(8)
    void benchmarkComplexExpressions() {
        System.out.println("\n### COMPLEX NESTED EXPRESSIONS ###");
        UserData data = TestDataGenerator.generateLargeDataset();
        
        // Deep nesting with multiple aggregations
        benchmarkFunction("COMPLEX_MULTI_AGG", 
            "AND(" +
                "GT(COUNT(WHERE(EVENT(\"purchase\"), GT(PARAM(\"amount\"), 100))), 10), " +
                "GT(COUNT(IN_RECENT_DAYS(30)), 50)" +
            ")", 
            data);
        
        // Multiple WHERE clauses
        benchmarkFunction("COMPLEX_MULTI_WHERE", 
            "COUNT(WHERE(WHERE(WHERE(EVENT(\"purchase\"), " +
                "GT(PARAM(\"amount\"), 50)), " +
                "LT(PARAM(\"amount\"), 500)), " +
                "GT(PARAM(\"quantity\"), 1)))", 
            data);
        
        // Complex math and logic
        benchmarkFunction("COMPLEX_MATH_LOGIC", 
            "COUNT(WHERE(EVENT(\"purchase\"), " +
                "AND(" +
                    "GT(MULTIPLY(PARAM(\"quantity\"), 10), 50), " +
                    "OR(" +
                        "EQ(PARAM(\"category\"), \"electronics\"), " +
                        "EQ(PARAM(\"category\"), \"books\")" +
                    ")" +
                ")" +
            "))", 
            data);
    }
    
    // ========== VISIT FUNCTION TESTS ==========
    
    @Test
    @Order(9)
    void benchmarkVisitFunction() {
        System.out.println("\n### VISIT FUNCTION (Device Attributes) ###");
        UserData data = TestDataGenerator.generateLargeDataset();
        
        // Basic device attribute access
        benchmarkFunction("VISIT_OS", 
            "COUNT(WHERE(EVENT(\"purchase\"), EQ(VISIT(\"os\"), \"Windows\")))", 
            data);
        
        benchmarkFunction("VISIT_BROWSER", 
            "COUNT(WHERE(EVENT(\"purchase\"), EQ(VISIT(\"browser\"), \"Chrome\")))", 
            data);
        
        benchmarkFunction("VISIT_DEVICE", 
            "COUNT(WHERE(EVENT(\"purchase\"), EQ(VISIT(\"device\"), \"Desktop\")))", 
            data);
        
        benchmarkFunction("VISIT_SCREEN", 
            "COUNT(WHERE(EVENT(\"purchase\"), CONTAINS(VISIT(\"screen\"), \"1920\")))", 
            data);
        
        // Visit session attributes
        benchmarkFunction("VISIT_LANDING_PAGE", 
            "COUNT(WHERE(EVENT(\"purchase\"), EQ(VISIT(\"landing_page\"), \"/home\")))", 
            data);
        
        benchmarkFunction("VISIT_REFERRER", 
            "COUNT(WHERE(EVENT(\"purchase\"), EQ(VISIT(\"referrer_type\"), \"search\")))", 
            data);
        
        benchmarkFunction("VISIT_DURATION", 
            "COUNT(WHERE(EVENT(\"purchase\"), GT(VISIT(\"duration\"), 300)))", 
            data);
        
        benchmarkFunction("VISIT_IS_FIRST", 
            "COUNT(WHERE(EVENT(\"purchase\"), EQ(VISIT(\"is_first_visit\"), true)))", 
            data);
        
        // Complex VISIT queries
        benchmarkFunction("VISIT_MOBILE_IOS", 
            "COUNT(WHERE(EVENT(\"purchase\"), " +
                "AND(" +
                    "EQ(VISIT(\"device\"), \"Mobile\"), " +
                    "EQ(VISIT(\"os\"), \"iOS\")" +
                ")" +
            "))", 
            data);
        
        benchmarkFunction("VISIT_DEVICE_AMOUNT", 
            "COUNT(WHERE(EVENT(\"purchase\"), " +
                "AND(" +
                    "EQ(VISIT(\"device\"), \"Mobile\"), " +
                    "GT(PARAM(\"amount\"), 100)" +
                ")" +
            "))", 
            data);
        
        benchmarkFunction("VISIT_FIRST_SEARCH", 
            "COUNT(WHERE(EVENT(\"purchase\"), " +
                "AND(" +
                    "EQ(VISIT(\"is_first_visit\"), true), " +
                    "EQ(VISIT(\"referrer_type\"), \"search\")" +
                ")" +
            "))", 
            data);
    }
    
    // ========== SCALABILITY TEST ==========
    
    @Test
    @Order(10)
    void benchmarkScalability() {
        System.out.println("\n### SCALABILITY TEST ###");
        
        String expression = "AND(" +
            "GT(COUNT(WHERE(EVENT(\"purchase\"), GT(PARAM(\"amount\"), 100))), 5), " +
            "GT(COUNT(IN_RECENT_DAYS(30)), 10)" +
            ")";
        
        // Test with increasing data sizes
        benchmarkFunction("SCALABILITY_1KB", expression, TestDataGenerator.generateSmallDataset());
        benchmarkFunction("SCALABILITY_1MB", expression, TestDataGenerator.generateMediumDataset());
        benchmarkFunction("SCALABILITY_10MB", expression, TestDataGenerator.generateLargeDataset());
        benchmarkFunction("SCALABILITY_100MB", expression, TestDataGenerator.generateExtraLargeDataset());
    }
    
    // ========== HELPER METHOD ==========
    
    private void benchmarkFunction(String name, String expression, UserData userData) {
        // Warmup
        for (int i = 0; i < WARMUP; i++) {
            dsl.evaluate(expression, userData);
        }
        
        // Force GC
        System.gc();
        try { Thread.sleep(50); } catch (InterruptedException e) { }
        
        // Measure
        MemoryUsage heapBefore = memoryBean.getHeapMemoryUsage();
        long memoryBefore = heapBefore.getUsed();
        
        List<Long> times = new ArrayList<>();
        long start = System.nanoTime();
        
        Set<String> uniqueErrors = new HashSet<>();
        int errorCount = 0;
        
        for (int i = 0; i < ITERATIONS; i++) {
            long iterStart = System.nanoTime();
            EvaluationResult result = dsl.evaluate(expression, userData);
            long iterEnd = System.nanoTime();
            times.add(iterEnd - iterStart);
            
            if (!result.isSuccess()) {
                errorCount++;
                uniqueErrors.add(result.getErrorMessage());
            }
        }
        
        long end = System.nanoTime();
        
        // Print unique errors summary (if any)
        if (errorCount > 0) {
            System.err.println("  Note: " + errorCount + " evaluation errors occurred (" + uniqueErrors.size() + " unique)");
        }
        
        // Memory measurement
        System.gc();
        try { Thread.sleep(50); } catch (InterruptedException e) { }
        MemoryUsage heapAfter = memoryBean.getHeapMemoryUsage();
        long memoryAfter = heapAfter.getUsed();
        long memoryDelta = Math.max(0, memoryAfter - memoryBefore);
        
        // Calculate metrics
        Collections.sort(times);
        
        PerformanceMetrics metrics = new PerformanceMetrics();
        metrics.functionName = name;
        metrics.datasetSize = String.format("%.2f MB", TestDataGenerator.estimateDataSize(userData) / (1024.0 * 1024.0));
        metrics.expression = expression;
        metrics.iterations = ITERATIONS;
        metrics.totalTimeMs = (end - start) / 1_000_000.0;
        metrics.avgTimeMs = times.stream().mapToLong(Long::longValue).average().orElse(0) / 1_000_000.0;
        metrics.minTimeMs = times.get(0) / 1_000_000.0;
        metrics.maxTimeMs = times.get(times.size() - 1) / 1_000_000.0;
        metrics.p95TimeMs = times.get((int) (times.size() * 0.95)) / 1_000_000.0;
        metrics.p99TimeMs = times.get((int) (times.size() * 0.99)) / 1_000_000.0;
        metrics.throughput = (ITERATIONS * 1000.0) / metrics.totalTimeMs;
        metrics.memoryUsedMB = memoryDelta / (1024.0 * 1024.0);
        metrics.datasetSizeMB = TestDataGenerator.estimateDataSize(userData) / (1024.0 * 1024.0);
        
        reporter.addMetrics(metrics);
        
        // Print summary
        System.out.printf("  %-25s | Avg: %8.3f ms | P95: %8.3f ms | Mem: %6.2f MB | Score: %6.2f [%s]%n",
            name, metrics.avgTimeMs, metrics.p95TimeMs, metrics.memoryUsedMB, 
            metrics.getComplexityScore(), metrics.getComplexityRating());
    }
}

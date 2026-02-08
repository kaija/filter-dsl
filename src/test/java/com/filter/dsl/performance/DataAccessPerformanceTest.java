package com.filter.dsl.performance;

import com.filter.dsl.DSL;
import com.filter.dsl.evaluator.EvaluationResult;
import com.filter.dsl.models.Event;
import com.filter.dsl.models.Profile;
import com.filter.dsl.models.UserData;
import com.filter.dsl.models.Visit;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Performance tests for complex data access scenarios involving Visit-Event relationships.
 * 
 * This test suite focuses on real-world use cases where filtering combines:
 * - Visit properties (device, OS, browser, screen, referrer, duration)
 * - Event properties (event name, type, timestamp)
 * - Event parameters (amount, category, quantity)
 * 
 * Separate from function-specific performance tests to focus on data access patterns.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Data Access Performance Tests")
public class DataAccessPerformanceTest {
    
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
        System.out.println("DATA ACCESS PERFORMANCE TEST SUITE");
        System.out.println("Complex Visit-Event Relationship Queries");
        System.out.println("=".repeat(80));
    }
    
    @AfterAll
    static void teardown() throws IOException {
        reporter.generateReport();
        System.out.println("\n" + "=".repeat(80));
        System.out.println("Data access performance tests completed!");
        System.out.println("=".repeat(80));
    }
    
    // ========== DEVICE + EVENT COMBINATIONS ==========
    
    @Test
    @Order(1)
    @DisplayName("Mobile device purchases")
    void testMobileDevicePurchases() {
        System.out.println("\n### DEVICE + EVENT COMBINATIONS ###");
        UserData data = generateLargeDataset();
        
        benchmarkQuery("MOBILE_PURCHASES",
            "COUNT(WHERE(EVENT(\"purchase\"), EQ(VISIT(\"device\"), \"Mobile\")))",
            data,
            "Count purchases from mobile devices");
    }
    
    @Test
    @Order(2)
    @DisplayName("Desktop high-value checkouts")
    void testDesktopHighValueCheckouts() {
        UserData data = generateLargeDataset();
        
        benchmarkQuery("DESKTOP_HIGH_VALUE",
            "COUNT(WHERE(EVENT(\"checkout\"), " +
                "AND(EQ(VISIT(\"device\"), \"Desktop\"), GT(PARAM(\"amount\"), 1000))))",
            data,
            "Desktop checkouts with amount > 1000");
    }
    
    // ========== OS + BROWSER COMBINATIONS ==========
    
    @Test
    @Order(3)
    @DisplayName("iOS Safari purchases")
    void testIOSSafariPurchases() {
        System.out.println("\n### OS + BROWSER COMBINATIONS ###");
        UserData data = generateLargeDataset();
        
        benchmarkQuery("IOS_SAFARI",
            "COUNT(WHERE(EVENT(\"purchase\"), " +
                "AND(EQ(VISIT(\"os\"), \"iOS\"), EQ(VISIT(\"browser\"), \"Safari\"))))",
            data,
            "Purchases from iOS Safari users");
    }
    
    @Test
    @Order(4)
    @DisplayName("Windows Chrome high-value")
    void testWindowsChromeHighValue() {
        UserData data = generateLargeDataset();
        
        benchmarkQuery("WINDOWS_CHROME_HIGH",
            "COUNT(WHERE(EVENT(\"purchase\"), " +
                "AND(" +
                    "EQ(VISIT(\"os\"), \"Windows\"), " +
                    "EQ(VISIT(\"browser\"), \"Chrome\"), " +
                    "GT(PARAM(\"amount\"), 500)" +
                ")))",
            data,
            "High-value purchases from Windows Chrome");
    }
    
    // ========== SCREEN RESOLUTION + AMOUNT ==========
    
    @Test
    @Order(5)
    @DisplayName("HD screen high-value checkouts")
    void testHDScreenHighValue() {
        System.out.println("\n### SCREEN RESOLUTION + AMOUNT ###");
        UserData data = generateLargeDataset();
        
        benchmarkQuery("HD_HIGH_VALUE",
            "COUNT(WHERE(EVENT(\"checkout\"), " +
                "AND(EQ(VISIT(\"screen\"), \"1920x1080\"), GT(PARAM(\"amount\"), 1000))))",
            data,
            "Checkouts on 1920x1080 with amount > 1000");
    }
    
    @Test
    @Order(6)
    @DisplayName("Mobile screen purchases")
    void testMobileScreenPurchases() {
        UserData data = generateLargeDataset();
        
        benchmarkQuery("MOBILE_SCREEN",
            "COUNT(WHERE(EVENT(\"purchase\"), EQ(VISIT(\"screen\"), \"375x667\")))",
            data,
            "Purchases on mobile screen resolution");
    }
    
    // ========== REFERRER + CONVERSION ==========
    
    @Test
    @Order(7)
    @DisplayName("Search referrer conversions")
    void testSearchReferrerConversions() {
        System.out.println("\n### REFERRER + CONVERSION ###");
        UserData data = generateLargeDataset();
        
        benchmarkQuery("SEARCH_CONVERSIONS",
            "COUNT(WHERE(EVENT(\"purchase\"), EQ(VISIT(\"referrer_type\"), \"search\")))",
            data,
            "Purchases from search referrers");
    }
    
    @Test
    @Order(8)
    @DisplayName("First visit search conversions")
    void testFirstVisitSearchConversions() {
        UserData data = generateLargeDataset();
        
        benchmarkQuery("FIRST_SEARCH_CONV",
            "COUNT(WHERE(EVENT(\"purchase\"), " +
                "AND(" +
                    "EQ(VISIT(\"is_first_visit\"), true), " +
                    "EQ(VISIT(\"referrer_type\"), \"search\")" +
                ")))",
            data,
            "First-time visitor purchases from search");
    }
    
    // ========== VISIT DURATION + ENGAGEMENT ==========
    
    @Test
    @Order(9)
    @DisplayName("Long visit high-value purchases")
    void testLongVisitHighValue() {
        System.out.println("\n### VISIT DURATION + ENGAGEMENT ###");
        UserData data = generateLargeDataset();
        
        benchmarkQuery("LONG_VISIT_HIGH",
            "COUNT(WHERE(EVENT(\"purchase\"), " +
                "AND(GT(VISIT(\"duration\"), 300), GT(PARAM(\"amount\"), 800))))",
            data,
            "High-value purchases with long visit duration");
    }
    
    @Test
    @Order(10)
    @DisplayName("Multiple actions with checkout")
    void testMultipleActionsCheckout() {
        UserData data = generateLargeDataset();
        
        benchmarkQuery("MULTI_ACTION_CHECKOUT",
            "COUNT(WHERE(EVENT(\"checkout\"), GT(VISIT(\"actions\"), 5)))",
            data,
            "Checkouts with > 5 actions in visit");
    }
    
    // ========== COMPLEX MULTI-CONDITION QUERIES ==========
    
    @Test
    @Order(11)
    @DisplayName("Mobile iOS search high-value")
    void testMobileIOSSearchHighValue() {
        System.out.println("\n### COMPLEX MULTI-CONDITION QUERIES ###");
        UserData data = generateLargeDataset();
        
        benchmarkQuery("MOBILE_IOS_SEARCH_HIGH",
            "COUNT(WHERE(EVENT(\"purchase\"), " +
                "AND(" +
                    "EQ(VISIT(\"device\"), \"Mobile\"), " +
                    "EQ(VISIT(\"os\"), \"iOS\"), " +
                    "EQ(VISIT(\"referrer_type\"), \"search\"), " +
                    "GT(PARAM(\"amount\"), 300)" +
                ")))",
            data,
            "Mobile iOS search users with purchase > 300");
    }
    
    @Test
    @Order(12)
    @DisplayName("Desktop Chrome HD first visit high-value")
    void testDesktopChromeHDFirstHighValue() {
        UserData data = generateLargeDataset();
        
        benchmarkQuery("DESKTOP_CHROME_HD_FIRST",
            "COUNT(WHERE(EVENT(\"checkout\"), " +
                "AND(" +
                    "EQ(VISIT(\"device\"), \"Desktop\"), " +
                    "EQ(VISIT(\"browser\"), \"Chrome\"), " +
                    "EQ(VISIT(\"screen\"), \"1920x1080\"), " +
                    "EQ(VISIT(\"is_first_visit\"), true), " +
                    "GT(PARAM(\"amount\"), 1000)" +
                ")))",
            data,
            "First-time Desktop Chrome HD users with checkout > 1000");
    }
    
    // ========== AGGREGATION QUERIES ==========
    
    @Test
    @Order(13)
    @DisplayName("Average purchase by device")
    void testAveragePurchaseByDevice() {
        System.out.println("\n### AGGREGATION QUERIES ###");
        UserData data = generateLargeDataset();
        
        benchmarkQuery("AVG_DESKTOP",
            "AVG(WHERE(EVENT(\"purchase\"), EQ(VISIT(\"device\"), \"Desktop\")))",
            data,
            "Average purchase amount for desktop");
        
        benchmarkQuery("AVG_MOBILE",
            "AVG(WHERE(EVENT(\"purchase\"), EQ(VISIT(\"device\"), \"Mobile\")))",
            data,
            "Average purchase amount for mobile");
    }
    
    @Test
    @Order(14)
    @DisplayName("Sum purchases by browser")
    void testSumPurchasesByBrowser() {
        UserData data = generateLargeDataset();
        
        benchmarkQuery("SUM_CHROME",
            "SUM(WHERE(EVENT(\"purchase\"), EQ(VISIT(\"browser\"), \"Chrome\")))",
            data,
            "Total revenue from Chrome users");
        
        benchmarkQuery("SUM_SAFARI",
            "SUM(WHERE(EVENT(\"purchase\"), EQ(VISIT(\"browser\"), \"Safari\")))",
            data,
            "Total revenue from Safari users");
    }
    
    @Test
    @Order(15)
    @DisplayName("Max purchase by screen resolution")
    void testMaxPurchaseByScreen() {
        UserData data = generateLargeDataset();
        
        benchmarkQuery("MAX_HD",
            "MAX(WHERE(EVENT(\"purchase\"), EQ(VISIT(\"screen\"), \"1920x1080\")))",
            data,
            "Maximum purchase on HD screens");
        
        benchmarkQuery("MAX_MOBILE",
            "MAX(WHERE(EVENT(\"purchase\"), EQ(VISIT(\"screen\"), \"375x667\")))",
            data,
            "Maximum purchase on mobile screens");
    }
    
    // ========== NESTED AGGREGATIONS ==========
    
    @Test
    @Order(16)
    @DisplayName("Nested aggregations with visit filters")
    void testNestedAggregations() {
        System.out.println("\n### NESTED AGGREGATIONS ###");
        UserData data = generateLargeDataset();
        
        benchmarkQuery("NESTED_MOBILE_HIGH",
            "GT(COUNT(WHERE(EVENT(\"purchase\"), " +
                "AND(EQ(VISIT(\"device\"), \"Mobile\"), GT(PARAM(\"amount\"), 200)))), 0)",
            data,
            "Check if mobile purchases > 200 exist");
        
        benchmarkQuery("NESTED_DESKTOP_AVG",
            "GT(AVG(WHERE(EVENT(\"purchase\"), EQ(VISIT(\"device\"), \"Desktop\"))), 500)",
            data,
            "Check if desktop average > 500");
    }
    
    // ========== CATEGORY + DEVICE COMBINATIONS ==========
    
    @Test
    @Order(17)
    @DisplayName("Electronics purchases by device")
    void testElectronicsByDevice() {
        System.out.println("\n### CATEGORY + DEVICE COMBINATIONS ###");
        UserData data = generateLargeDataset();
        
        benchmarkQuery("ELECTRONICS_DESKTOP",
            "COUNT(WHERE(EVENT(\"purchase\"), " +
                "AND(" +
                    "EQ(PARAM(\"category\"), \"electronics\"), " +
                    "EQ(VISIT(\"device\"), \"Desktop\")" +
                ")))",
            data,
            "Electronics purchases on desktop");
        
        benchmarkQuery("ELECTRONICS_MOBILE",
            "COUNT(WHERE(EVENT(\"purchase\"), " +
                "AND(" +
                    "EQ(PARAM(\"category\"), \"electronics\"), " +
                    "EQ(VISIT(\"device\"), \"Mobile\")" +
                ")))",
            data,
            "Electronics purchases on mobile");
    }
    
    // ========== HELPER METHODS ==========
    
    private void benchmarkQuery(String name, String expression, UserData userData, String description) {
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
        
        // Print error summary if any
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
        metrics.datasetSize = String.format("%.2f MB", estimateDataSize(userData) / (1024.0 * 1024.0));
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
        metrics.datasetSizeMB = estimateDataSize(userData) / (1024.0 * 1024.0);
        
        reporter.addMetrics(metrics);
        
        // Print summary
        System.out.printf("  %-30s | Avg: %8.3f ms | P95: %8.3f ms | Mem: %6.2f MB%n",
            name, metrics.avgTimeMs, metrics.p95TimeMs, metrics.memoryUsedMB);
        System.out.printf("    %s%n", description);
    }
    
    /**
     * Generate large dataset with realistic visit and event data
     */
    private UserData generateLargeDataset() {
        Random random = new Random(42);
        
        Profile profile = Profile.builder()
            .uuid("perf-user")
            .country("US")
            .city("San Francisco")
            .language("en")
            .build();
        
        // Generate 10 visits with different device combinations
        Map<String, Visit> visits = new HashMap<>();
        String[] devices = {"Desktop", "Desktop", "Desktop", "Desktop", "Desktop", "Desktop", "Desktop", "Mobile", "Mobile", "Mobile"};
        String[] oses = {"Windows", "Windows", "Windows", "macOS", "macOS", "iOS", "iOS", "iOS", "Android", "Android"};
        String[] browsers = {"Chrome", "Chrome", "Chrome", "Chrome", "Safari", "Safari", "Safari", "Firefox", "Chrome", "Safari"};
        String[] screens = {"1920x1080", "1920x1080", "1920x1080", "1920x1080", "1920x1080", "1920x1080", "1920x1080", "375x667", "375x667", "375x667"};
        String[] referrers = {"search", "search", "search", "search", "direct", "direct", "search", "search", "direct", "search"};
        
        for (int i = 0; i < 10; i++) {
            Visit visit = Visit.builder()
                .uuid("visit-" + i)
                .timestamp(Instant.now().minus(i * 3, ChronoUnit.DAYS).toString())
                .landingPage(i % 2 == 0 ? "/home" : "/products")
                .referrerType(referrers[i])
                .referrerUrl(referrers[i].equals("search") ? "https://google.com" : null)
                .duration(random.nextInt(500) + 100)
                .actions(random.nextInt(15) + 1)
                .isFirstVisit(i < 3)
                .os(oses[i])
                .browser(browsers[i])
                .device(devices[i])
                .screen(screens[i])
                .build();
            visits.put(visit.getUuid(), visit);
        }
        
        // Generate 10,000 events distributed across visits
        List<Event> events = new ArrayList<>();
        String[] eventNames = {"purchase", "purchase", "purchase", "purchase", "purchase", "checkout", "checkout", "view", "view", "add_to_cart"};
        String[] categories = {"electronics", "electronics", "clothing", "books", "toys", "electronics", "furniture", "electronics", "clothing", "books"};
        
        for (int i = 0; i < 10000; i++) {
            int visitIndex = i % 10;
            Event event = Event.builder()
                .uuid("event-" + i)
                .visitId("visit-" + visitIndex)  // Link event to visit
                .eventName(eventNames[random.nextInt(eventNames.length)])
                .timestamp(Instant.now().minus(visitIndex * 3, ChronoUnit.DAYS)
                    .plus(random.nextInt(300), ChronoUnit.SECONDS).toString())
                .parameter("amount", 50.0 + random.nextDouble() * 1500.0)
                .parameter("category", categories[random.nextInt(categories.length)])
                .parameter("quantity", 1 + random.nextInt(5))
                .build();
            events.add(event);
        }
        
        return UserData.builder()
            .profile(profile)
            .visits(visits)
            .events(events)
            .build();
    }
    
    private long estimateDataSize(UserData userData) {
        long size = 500; // Profile
        
        if (userData.getVisits() != null) {
            size += userData.getVisits().size() * 300L; // Each visit ~300 bytes
        }
        
        if (userData.getEvents() != null) {
            size += userData.getEvents().size() * 150L; // Each event ~150 bytes
        }
        
        return size;
    }
}

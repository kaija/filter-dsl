package com.filter.dsl.integration;

import com.filter.dsl.DSL;
import com.filter.dsl.evaluator.EvaluationResult;
import com.filter.dsl.models.Event;
import com.filter.dsl.models.Profile;
import com.filter.dsl.models.UserData;
import com.filter.dsl.models.Visit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for complex Visit-Event relationships.
 * 
 * Tests real-world scenarios combining visit properties (device, OS, browser, screen)
 * with event properties and parameters.
 * 
 * Note: These tests work with the current architecture where VISIT() accesses
 * the first visit in userData when no currentVisit is set in context.
 */
@DisplayName("Visit-Event Integration Tests")
public class VisitEventIntegrationTest {
    
    private static DSL dsl;
    
    @BeforeAll
    static void setup() {
        dsl = DSL.builder()
            .enableAutoDiscovery(true)
            .enableCaching(true)
            .build();
    }
    
    /**
     * Test: Visit device detection with event counting
     * 
     * Demonstrates that visit properties and event data can both be queried.
     * Events are linked to visits via visit_id field.
     */
    @Test
    @DisplayName("Count events with visit device check")
    void testEventCountWithDeviceCheck() {
        UserData userData = createMobileUserData();
        
        // Test visit device
        String visitExpr = "EQ(VISIT(\"device\"), \"Mobile\")";
        EvaluationResult visitResult = dsl.evaluate(visitExpr, userData);
        assertTrue(visitResult.isSuccess());
        assertTrue((Boolean) visitResult.getValue(), "Visit device should be Mobile");
        
        // Test event count using correct syntax - filter by event name
        String eventExpr = "GT(COUNT(WHERE(userData.events, \"EQ(EVENT(\\\"eventName\\\"), \\\"purchase\\\")\")), 0)";
        EvaluationResult eventResult = dsl.evaluate(eventExpr, userData);
        assertTrue(eventResult.isSuccess());
        assertTrue((Boolean) eventResult.getValue(), "Should have purchase events");
    }
    
    /**
     * Test: Visit OS with high-value event check
     */
    @Test
    @DisplayName("High-value purchases with OS check")
    void testHighValueWithOSCheck() {
        UserData userData = createDesktopUserData();
        
        // Test OS
        String osExpr = "EQ(VISIT(\"os\"), \"Windows\")";
        EvaluationResult osResult = dsl.evaluate(osExpr, userData);
        assertTrue(osResult.isSuccess());
        assertTrue((Boolean) osResult.getValue(), "OS should be Windows");
        
        // Test high-value purchases - sum of purchase event amounts
        String sumExpr = "GT(SUM(WHERE(userData.events, \"EQ(EVENT(\\\"eventName\\\"), \\\"purchase\\\")\")), 1000)";
        EvaluationResult sumResult = dsl.evaluate(sumExpr, userData);
        assertTrue(sumResult.isSuccess());
        assertTrue((Boolean) sumResult.getValue(), "Should have high-value purchases");
    }
    
    /**
     * Test: Visit browser with event type filtering
     */
    @Test
    @DisplayName("Checkout events with browser check")
    void testCheckoutWithBrowserCheck() {
        UserData userData = createChromeUserData();
        
        // Test browser
        String browserExpr = "EQ(VISIT(\"browser\"), \"Chrome\")";
        EvaluationResult browserResult = dsl.evaluate(browserExpr, userData);
        assertTrue(browserResult.isSuccess());
        assertTrue((Boolean) browserResult.getValue(), "Browser should be Chrome");
        
        // Test checkout events using correct syntax
        String checkoutExpr = "GT(COUNT(WHERE(userData.events, \"EQ(EVENT(\\\"eventName\\\"), \\\"checkout\\\")\")), 0)";
        EvaluationResult checkoutResult = dsl.evaluate(checkoutExpr, userData);
        assertTrue(checkoutResult.isSuccess());
        assertTrue((Boolean) checkoutResult.getValue(), "Should have checkout events");
    }
    
    /**
     * Test: Visit screen resolution with purchase amount
     */
    @Test
    @DisplayName("HD screen with high-value purchases")
    void testHDScreenHighValue() {
        UserData userData = createHDScreenUserData();
        
        // Test screen resolution
        String screenExpr = "EQ(VISIT(\"screen\"), \"1920x1080\")";
        EvaluationResult screenResult = dsl.evaluate(screenExpr, userData);
        assertTrue(screenResult.isSuccess());
        assertTrue((Boolean) screenResult.getValue(), "Screen should be 1920x1080");
        
        // Test average purchase amount
        String avgExpr = "GT(AVG(WHERE(userData.events, \"EQ(EVENT(\\\"eventName\\\"), \\\"purchase\\\")\")), 500)";
        EvaluationResult avgResult = dsl.evaluate(avgExpr, userData);
        assertTrue(avgResult.isSuccess());
        assertTrue((Boolean) avgResult.getValue(), "Average purchase should be > 500");
    }
    
    /**
     * Test: Visit referrer with conversion
     */
    @Test
    @DisplayName("Search referrer with purchases")
    void testSearchReferrerConversion() {
        UserData userData = createSearchReferrerData();
        
        // Test referrer type
        String referrerExpr = "EQ(VISIT(\"referrer_type\"), \"search\")";
        EvaluationResult referrerResult = dsl.evaluate(referrerExpr, userData);
        assertTrue(referrerResult.isSuccess());
        assertTrue((Boolean) referrerResult.getValue(), "Referrer should be search");
        
        // Test purchases using correct syntax
        String purchaseExpr = "GT(COUNT(WHERE(userData.events, \"EQ(EVENT(\\\"eventName\\\"), \\\"purchase\\\")\")), 0)";
        EvaluationResult purchaseResult = dsl.evaluate(purchaseExpr, userData);
        assertTrue(purchaseResult.isSuccess());
        assertTrue((Boolean) purchaseResult.getValue(), "Should have purchases");
    }
    
    /**
     * Test: First visit with conversion
     */
    @Test
    @DisplayName("First-time visitor purchases")
    void testFirstVisitConversion() {
        UserData userData = createFirstVisitData();
        
        // Test first visit
        String firstVisitExpr = "EQ(VISIT(\"is_first_visit\"), true)";
        EvaluationResult firstVisitResult = dsl.evaluate(firstVisitExpr, userData);
        assertTrue(firstVisitResult.isSuccess());
        assertTrue((Boolean) firstVisitResult.getValue(), "Should be first visit");
        
        // Test purchases using correct syntax
        String purchaseExpr = "GT(COUNT(WHERE(userData.events, \"EQ(EVENT(\\\"eventName\\\"), \\\"purchase\\\")\")), 0)";
        EvaluationResult purchaseResult = dsl.evaluate(purchaseExpr, userData);
        assertTrue(purchaseResult.isSuccess());
        assertTrue((Boolean) purchaseResult.getValue(), "Should have purchases");
    }
    
    /**
     * Test: Visit duration with engagement
     */
    @Test
    @DisplayName("Long visit duration with multiple events")
    void testLongVisitEngagement() {
        UserData userData = createLongVisitData();
        
        // Test duration
        String durationExpr = "GT(VISIT(\"duration\"), 300)";
        EvaluationResult durationResult = dsl.evaluate(durationExpr, userData);
        assertTrue(durationResult.isSuccess());
        assertTrue((Boolean) durationResult.getValue(), "Duration should be > 300");
        
        // Test purchase count using correct syntax
        String countExpr = "GT(COUNT(WHERE(userData.events, \"EQ(EVENT(\\\"eventName\\\"), \\\"purchase\\\")\")), 2)";
        EvaluationResult countResult = dsl.evaluate(countExpr, userData);
        assertTrue(countResult.isSuccess());
        assertTrue((Boolean) countResult.getValue(), "Should have > 2 purchases");
    }
    
    /**
     * Test: Multiple visit attributes with event aggregation
     */
    @Test
    @DisplayName("Mobile iOS with high-value purchases")
    void testMobileIOSHighValue() {
        UserData userData = createMobileIOSData();
        
        // Test device and OS
        String deviceExpr = "AND(EQ(VISIT(\"device\"), \"Mobile\"), EQ(VISIT(\"os\"), \"iOS\"))";
        EvaluationResult deviceResult = dsl.evaluate(deviceExpr, userData);
        assertTrue(deviceResult.isSuccess());
        assertTrue((Boolean) deviceResult.getValue(), "Should be Mobile iOS");
        
        // Test purchase sum
        String sumExpr = "GT(SUM(WHERE(userData.events, \"EQ(EVENT(\\\"eventName\\\"), \\\"purchase\\\")\")), 500)";
        EvaluationResult sumResult = dsl.evaluate(sumExpr, userData);
        assertTrue(sumResult.isSuccess());
        assertTrue((Boolean) sumResult.getValue(), "Should have high-value purchases");
    }
    
    /**
     * Test: Complex multi-condition query
     */
    @Test
    @DisplayName("Desktop Chrome HD with high-value checkout")
    void testDesktopChromeHDHighValue() {
        UserData userData = createDesktopChromeHDData();
        
        // Test visit attributes
        String visitExpr = "AND(" +
            "EQ(VISIT(\"device\"), \"Desktop\"), " +
            "EQ(VISIT(\"browser\"), \"Chrome\"), " +
            "EQ(VISIT(\"screen\"), \"1920x1080\")" +
            ")";
        EvaluationResult visitResult = dsl.evaluate(visitExpr, userData);
        assertTrue(visitResult.isSuccess());
        assertTrue((Boolean) visitResult.getValue(), "Should be Desktop Chrome HD");
        
        // Test checkout max amount
        String checkoutExpr = "GT(MAX(WHERE(userData.events, \"EQ(EVENT(\\\"eventName\\\"), \\\"checkout\\\")\")), 1000)";
        EvaluationResult checkoutResult = dsl.evaluate(checkoutExpr, userData);
        assertTrue(checkoutResult.isSuccess());
        assertTrue((Boolean) checkoutResult.getValue(), "Should have high-value checkout");
    }
    
    /**
     * Test: Visit actions with event count
     */
    @Test
    @DisplayName("High engagement visits with purchases")
    void testHighEngagementPurchases() {
        UserData userData = createHighEngagementData();
        
        // Test actions
        String actionsExpr = "GT(VISIT(\"actions\"), 5)";
        EvaluationResult actionsResult = dsl.evaluate(actionsExpr, userData);
        assertTrue(actionsResult.isSuccess());
        assertTrue((Boolean) actionsResult.getValue(), "Should have > 5 actions");
        
        // Test purchase count using correct syntax
        String purchaseExpr = "GT(COUNT(WHERE(userData.events, \"EQ(EVENT(\\\"eventName\\\"), \\\"purchase\\\")\")), 1)";
        EvaluationResult purchaseResult = dsl.evaluate(purchaseExpr, userData);
        assertTrue(purchaseResult.isSuccess());
        assertTrue((Boolean) purchaseResult.getValue(), "Should have > 1 purchase");
    }
    
    // ========== TEST DATA CREATION METHODS ==========
    
    private UserData createMobileUserData() {
        return createUserData("Mobile", "iOS", "Safari", "375x667", "search", false, 250, 4,
            new String[]{"purchase", "purchase", "view"},
            new double[]{299.99, 150.00, 0});
    }
    
    private UserData createDesktopUserData() {
        return createUserData("Desktop", "Windows", "Chrome", "1920x1080", "direct", false, 400, 6,
            new String[]{"purchase", "purchase", "checkout"},
            new double[]{599.99, 899.99, 1200.00});
    }
    
    private UserData createChromeUserData() {
        return createUserData("Desktop", "Windows", "Chrome", "1920x1080", "search", false, 350, 5,
            new String[]{"checkout", "checkout", "purchase"},
            new double[]{450.00, 750.00, 320.00});
    }
    
    private UserData createHDScreenUserData() {
        return createUserData("Desktop", "macOS", "Safari", "1920x1080", "direct", false, 380, 7,
            new String[]{"purchase", "purchase", "purchase"},
            new double[]{650.00, 550.00, 720.00});
    }
    
    private UserData createSearchReferrerData() {
        return createUserData("Desktop", "Windows", "Firefox", "1920x1080", "search", false, 290, 5,
            new String[]{"purchase", "view", "purchase"},
            new double[]{399.99, 0, 250.00});
    }
    
    private UserData createFirstVisitData() {
        return createUserData("Mobile", "Android", "Chrome", "375x667", "search", true, 180, 3,
            new String[]{"purchase", "purchase"},
            new double[]{199.99, 299.99});
    }
    
    private UserData createLongVisitData() {
        return createUserData("Desktop", "Windows", "Chrome", "1920x1080", "search", false, 450, 10,
            new String[]{"purchase", "purchase", "purchase", "view"},
            new double[]{350.00, 450.00, 550.00, 0});
    }
    
    private UserData createMobileIOSData() {
        return createUserData("Mobile", "iOS", "Safari", "375x667", "search", false, 320, 6,
            new String[]{"purchase", "purchase", "purchase"},
            new double[]{250.00, 200.00, 250.00});  // Total: 700 > 500
    }
    
    private UserData createDesktopChromeHDData() {
        return createUserData("Desktop", "Windows", "Chrome", "1920x1080", "direct", false, 420, 8,
            new String[]{"checkout", "checkout", "purchase"},
            new double[]{1200.00, 1500.00, 850.00});
    }
    
    private UserData createHighEngagementData() {
        return createUserData("Desktop", "macOS", "Chrome", "1920x1080", "search", false, 500, 12,
            new String[]{"purchase", "purchase", "purchase", "view", "view"},
            new double[]{450.00, 350.00, 280.00, 0, 0});
    }
    
    /**
     * Helper method to create test data with specified visit and event properties
     */
    private UserData createUserData(String device, String os, String browser, String screen,
                                    String referrerType, boolean isFirstVisit, int duration, int actions,
                                    String[] eventNames, double[] amounts) {
        Profile profile = Profile.builder()
            .uuid("test-user")
            .country("US")
            .city("San Francisco")
            .language("en")
            .build();
        
        Visit visit = Visit.builder()
            .uuid("test-visit")
            .timestamp("2024-01-15T10:00:00Z")
            .landingPage("/home")
            .referrerType(referrerType)
            .referrerUrl(referrerType.equals("search") ? "https://google.com" : null)
            .duration(duration)
            .actions(actions)
            .isFirstVisit(isFirstVisit)
            .os(os)
            .browser(browser)
            .device(device)
            .screen(screen)
            .build();
        
        Map<String, Visit> visits = new HashMap<>();
        visits.put(visit.getUuid(), visit);
        
        List<Event> events = new ArrayList<>();
        for (int i = 0; i < eventNames.length; i++) {
            Event.Builder eventBuilder = Event.builder()
                .uuid("event-" + i)
                .visitId(visit.getUuid())  // Link event to visit
                .eventName(eventNames[i])
                .timestamp("2024-01-15T10:" + String.format("%02d", i * 5) + ":00Z");
            
            if (amounts[i] > 0) {
                eventBuilder.parameter("amount", amounts[i]);
                eventBuilder.parameter("category", "electronics");
            }
            
            events.add(eventBuilder.build());
        }
        
        return UserData.builder()
            .profile(profile)
            .visits(visits)
            .events(events)
            .build();
    }
}

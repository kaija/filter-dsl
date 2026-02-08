package com.filter.dsl.performance;

import com.filter.dsl.models.Event;
import com.filter.dsl.models.Profile;
import com.filter.dsl.models.UserData;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Generates test datasets of various sizes for performance testing.
 * 
 * Dataset sizes:
 * - Small: ~1KB (10 events)
 * - Medium: ~1MB (10,000 events)
 * - Large: ~10MB (100,000 events)
 * - Extra Large: ~100MB (1,000,000 events)
 */
public class TestDataGenerator {
    
    private static final Random RANDOM = new Random(42); // Fixed seed for reproducibility
    
    private static final String[] CATEGORIES = {
        "electronics", "clothing", "food", "books", "toys", 
        "sports", "home", "garden", "automotive", "health"
    };
    
    private static final String[] EVENT_NAMES = {
        "purchase", "view", "add_to_cart", "remove_from_cart", 
        "wishlist_add", "search", "filter", "checkout", "payment"
    };
    
    /**
     * Generate a small dataset (~1KB, 10 events).
     */
    public static UserData generateSmallDataset() {
        return generateDataset(10, "small_user");
    }
    
    /**
     * Generate a medium dataset (~1MB, 10,000 events).
     */
    public static UserData generateMediumDataset() {
        return generateDataset(10_000, "medium_user");
    }
    
    /**
     * Generate a large dataset (~10MB, 100,000 events).
     */
    public static UserData generateLargeDataset() {
        return generateDataset(100_000, "large_user");
    }
    
    /**
     * Generate an extra large dataset (~100MB, 1,000,000 events).
     */
    public static UserData generateExtraLargeDataset() {
        return generateDataset(1_000_000, "xlarge_user");
    }
    
    /**
     * Generate a dataset with the specified number of events.
     */
    private static UserData generateDataset(int eventCount, String userId) {
        UserData userData = new UserData();
        
        // Generate profile data
        Profile profile = Profile.builder()
            .uuid(userId)
            .country("US")
            .city("San Francisco")
            .language("en")
            .continent("NA")
            .timezone("America/Los_Angeles")
            .build();
        userData.setProfile(profile);
        
        // Generate visit data with device attributes
        Map<String, com.filter.dsl.models.Visit> visits = new HashMap<>();
        com.filter.dsl.models.Visit visit = com.filter.dsl.models.Visit.builder()
            .uuid("visit-" + userId)
            .timestamp(Instant.now().minus(RANDOM.nextInt(30), ChronoUnit.DAYS).toString())
            .landingPage("/home")
            .referrerType(RANDOM.nextDouble() < 0.5 ? "search" : "direct")
            .referrerUrl(RANDOM.nextDouble() < 0.5 ? "https://google.com" : null)
            .duration(RANDOM.nextInt(600) + 60) // 60-660 seconds
            .actions(RANDOM.nextInt(20) + 1) // 1-20 actions
            .isFirstVisit(RANDOM.nextDouble() < 0.3) // 30% first visits
            // Device attributes (session-specific)
            .os(RANDOM.nextDouble() < 0.5 ? "Windows" : (RANDOM.nextDouble() < 0.5 ? "macOS" : "iOS"))
            .browser(RANDOM.nextDouble() < 0.5 ? "Chrome" : (RANDOM.nextDouble() < 0.5 ? "Safari" : "Firefox"))
            .device(RANDOM.nextDouble() < 0.7 ? "Desktop" : "Mobile")
            .screen(RANDOM.nextDouble() < 0.7 ? "1920x1080" : "375x667")
            .build();
        visits.put(visit.getUuid(), visit);
        userData.setVisits(visits);
        
        // Generate events
        List<Event> events = new ArrayList<>(eventCount);
        Instant now = Instant.now();
        
        for (int i = 0; i < eventCount; i++) {
            Event event = new Event();
            event.setEventName(EVENT_NAMES[RANDOM.nextInt(EVENT_NAMES.length)]);
            
            // Generate timestamp (within last 90 days)
            Instant timestamp = now.minus(RANDOM.nextInt(90), ChronoUnit.DAYS)
                .minus(RANDOM.nextInt(24), ChronoUnit.HOURS);
            event.setTimestamp(timestamp.toString());
            
            // Generate parameters
            Map<String, Object> params = new HashMap<>();
            params.put("amount", 10.0 + RANDOM.nextDouble() * 990.0); // $10-$1000
            params.put("category", CATEGORIES[RANDOM.nextInt(CATEGORIES.length)]);
            params.put("quantity", 1 + RANDOM.nextInt(10));
            params.put("timestamp", timestamp.toString());
            params.put("product_id", "PROD_" + RANDOM.nextInt(1000));
            params.put("session_id", "SESSION_" + (i / 100)); // Group events into sessions
            
            // Add some variation
            if (RANDOM.nextDouble() < 0.3) {
                params.put("discount", RANDOM.nextDouble() * 0.3); // 0-30% discount
            }
            if (RANDOM.nextDouble() < 0.2) {
                params.put("coupon_code", "COUPON_" + RANDOM.nextInt(10));
            }
            
            event.setParameters(params);
            events.add(event);
        }
        
        userData.setEvents(events);
        
        return userData;
    }
    
    /**
     * Estimate the size of a UserData object in bytes.
     * This is a rough estimate for reporting purposes.
     */
    public static long estimateDataSize(UserData userData) {
        long size = 0;
        
        // Profile data (rough estimate: ~500 bytes for all fields)
        if (userData.getProfile() != null) {
            size += 500;
        }
        
        // Events
        if (userData.getEvents() != null) {
            int eventCount = userData.getEvents().size();
            // Each event has: name (~20 bytes) + timestamp (~30 bytes) + params (~100 bytes avg)
            size += eventCount * 150L;
        }
        
        return size;
    }
    
    /**
     * Generate a dataset with specific characteristics for targeted testing.
     */
    public static UserData generateCustomDataset(
            int eventCount,
            String primaryEventName,
            double avgAmount,
            int daysBack) {
        
        UserData userData = new UserData();
        
        Profile profile = Profile.builder()
            .uuid("custom_user")
            .country("US")
            .city("New York")
            .language("en")
            .build();
        userData.setProfile(profile);
        
        List<Event> events = new ArrayList<>(eventCount);
        Instant now = Instant.now();
        
        for (int i = 0; i < eventCount; i++) {
            Event event = new Event();
            event.setEventName(primaryEventName);
            
            Instant timestamp = now.minus(RANDOM.nextInt(daysBack), ChronoUnit.DAYS);
            event.setTimestamp(timestamp.toString());
            
            Map<String, Object> params = new HashMap<>();
            params.put("amount", avgAmount * (0.5 + RANDOM.nextDouble()));
            params.put("category", CATEGORIES[RANDOM.nextInt(CATEGORIES.length)]);
            params.put("timestamp", timestamp.toString());
            
            event.setParameters(params);
            events.add(event);
        }
        
        userData.setEvents(events);
        return userData;
    }
}

package com.filter.dsl;

import com.filter.dsl.functions.FunctionRegistry;
import com.filter.dsl.models.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic test to verify project structure and dependencies are set up correctly.
 */
class ProjectStructureTest {

    @Test
    void testFunctionRegistryCreation() {
        FunctionRegistry registry = new FunctionRegistry();
        assertNotNull(registry);
        assertEquals(0, registry.size());
    }

    @Test
    void testUserDataModelCreation() {
        Profile profile = Profile.builder()
            .country("US")
            .city("New York")
            .uuid("user-123")
            .build();

        Event event = Event.builder()
            .eventName("purchase")
            .timestamp("2024-01-15T10:30:00Z")
            .parameter("amount", 99.99)
            .build();

        Visit visit = Visit.builder()
            .uuid("visit-123")
            .timestamp("2024-01-15T10:00:00Z")
            .landingPage("/home")
            .build();

        UserData userData = UserData.builder()
            .profile(profile)
            .event(event)
            .visit("visit-123", visit)
            .build();

        assertNotNull(userData);
        assertNotNull(userData.getProfile());
        assertEquals("US", userData.getProfile().getCountry());
        assertEquals(1, userData.getEvents().size());
        assertEquals("purchase", userData.getEvents().get(0).getEventName());
        assertEquals(1, userData.getVisits().size());
    }

    @Test
    void testTimeRangeModel() {
        TimeRange timeRange = new TimeRange(
            30, TimeUnit.D,
            0, TimeUnit.D,
            java.time.Instant.now()
        );

        assertNotNull(timeRange);
        assertNotNull(timeRange.getStartTime());
        assertNotNull(timeRange.getEndTime());
        assertTrue(timeRange.getStartTime().isBefore(timeRange.getEndTime()));
    }

    @Test
    void testBucketDefinition() {
        BucketDefinition buckets = BucketDefinition.builder()
            .range(0.0, 10.0, "Low")
            .range(10.0, 100.0, "Medium")
            .range(100.0, 500.0, "High")
            .defaultLabel("Other")
            .build();

        assertNotNull(buckets);
        assertEquals("Low", buckets.getBucketLabel(5.0));
        assertEquals("Medium", buckets.getBucketLabel(50.0));
        assertEquals("High", buckets.getBucketLabel(200.0));
        assertEquals("Other", buckets.getBucketLabel(1000.0));
    }

    @Test
    void testTimeUnitParsing() {
        assertEquals(TimeUnit.D, TimeUnit.parse("D"));
        assertEquals(TimeUnit.H, TimeUnit.parse("h"));
        assertEquals(TimeUnit.M, TimeUnit.parse("m"));
        assertEquals(TimeUnit.W, TimeUnit.parse("W"));
        assertEquals(TimeUnit.MO, TimeUnit.parse("mo"));
        assertEquals(TimeUnit.Y, TimeUnit.parse("Y"));

        assertThrows(IllegalArgumentException.class, () -> TimeUnit.parse("X"));
        assertThrows(IllegalArgumentException.class, () -> TimeUnit.parse(null));
    }
}

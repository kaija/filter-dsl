package com.filter.dsl.unit.models;

import com.filter.dsl.models.BucketDefinition;
import com.filter.dsl.models.BucketRange;
import com.filter.dsl.models.Event;
import com.filter.dsl.models.Profile;
import com.filter.dsl.models.TimeRange;
import com.filter.dsl.models.TimeUnit;
import com.filter.dsl.models.UserData;
import com.filter.dsl.models.Visit;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for data model classes (UserData, Profile, Visit, Event).
 * Tests constructors, getters/setters, builders, and JSON serialization.
 */
class ModelClassesTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testProfileBuilder() {
        Profile profile = Profile.builder()
                .uuid("user-123")
                .country("USA")
                .city("New York")
                .language("en")
                .continent("North America")
                .timezone("America/New_York")
                .build();

        assertEquals("user-123", profile.getUuid());
        assertEquals("USA", profile.getCountry());
        assertEquals("New York", profile.getCity());
        assertEquals("en", profile.getLanguage());
        assertEquals("North America", profile.getContinent());
        assertEquals("America/New_York", profile.getTimezone());
    }

    @Test
    void testVisitBuilder() {
        Visit visit = Visit.builder()
                .uuid("visit-456")
                .timestamp("2024-01-15T10:30:00Z")
                .landingPage("/home")
                .referrerType("search")
                .referrerUrl("https://google.com")
                .referrerQuery("user segmentation")
                .duration(300)
                .actions(5)
                .isFirstVisit(true)
                .build();

        assertEquals("visit-456", visit.getUuid());
        assertEquals("2024-01-15T10:30:00Z", visit.getTimestamp());
        assertEquals("/home", visit.getLandingPage());
        assertEquals("search", visit.getReferrerType());
        assertEquals("https://google.com", visit.getReferrerUrl());
        assertEquals("user segmentation", visit.getReferrerQuery());
        assertEquals(300, visit.getDuration());
        assertEquals(5, visit.getActions());
        assertTrue(visit.getIsFirstVisit());
    }

    @Test
    void testEventBuilder() {
        Map<String, Object> params = new HashMap<>();
        params.put("amount", 99.99);
        params.put("currency", "USD");

        Event event = Event.builder()
                .uuid("event-789")
                .isFirstInVisit(true)
                .isLastInVisit(false)
                .isFirstEvent(false)
                .isCurrent(true)
                .eventName("purchase")
                .integration("web")
                .app("ecommerce")
                .platform("web")
                .isHttps(true)
                .eventType("action")
                .duration(10)
                .timestamp("2024-01-15T10:35:00Z")
                .triggerable(true)
                .parameters(params)
                .build();

        assertEquals("event-789", event.getUuid());
        assertTrue(event.getIsFirstInVisit());
        assertFalse(event.getIsLastInVisit());
        assertFalse(event.getIsFirstEvent());
        assertTrue(event.getIsCurrent());
        assertEquals("purchase", event.getEventName());
        assertEquals("web", event.getIntegration());
        assertEquals("ecommerce", event.getApp());
        assertEquals("web", event.getPlatform());
        assertTrue(event.getIsHttps());
        assertEquals("action", event.getEventType());
        assertEquals(10, event.getDuration());
        assertEquals("2024-01-15T10:35:00Z", event.getTimestamp());
        assertTrue(event.getTriggerable());
        assertEquals(99.99, event.getParameters().get("amount"));
        assertEquals("USD", event.getParameters().get("currency"));
    }

    @Test
    void testEventBuilderWithParameterMethod() {
        Event event = Event.builder()
                .uuid("event-999")
                .eventName("click")
                .parameter("button", "submit")
                .parameter("page", "checkout")
                .build();

        assertEquals("event-999", event.getUuid());
        assertEquals("click", event.getEventName());
        assertEquals("submit", event.getParameters().get("button"));
        assertEquals("checkout", event.getParameters().get("page"));
    }

    @Test
    void testUserDataBuilder() {
        Profile profile = Profile.builder()
                .uuid("user-123")
                .country("USA")
                .build();

        Visit visit = Visit.builder()
                .uuid("visit-456")
                .timestamp("2024-01-15T10:30:00Z")
                .build();

        Event event = Event.builder()
                .uuid("event-789")
                .eventName("purchase")
                .build();

        UserData userData = UserData.builder()
                .profile(profile)
                .visit("visit-456", visit)
                .event(event)
                .build();

        assertNotNull(userData.getProfile());
        assertEquals("user-123", userData.getProfile().getUuid());
        assertEquals(1, userData.getVisits().size());
        assertTrue(userData.getVisits().containsKey("visit-456"));
        assertEquals(1, userData.getEvents().size());
        assertEquals("event-789", userData.getEvents().get(0).getUuid());
    }

    @Test
    void testProfileJsonSerialization() throws Exception {
        Profile profile = Profile.builder()
                .uuid("user-123")
                .country("USA")
                .city("New York")
                .language("en")
                .build();

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(profile);
        assertNotNull(json);
        assertTrue(json.contains("\"uuid\":\"user-123\""));
        assertTrue(json.contains("\"country\":\"USA\""));

        // Deserialize from JSON
        Profile deserialized = objectMapper.readValue(json, Profile.class);
        assertEquals(profile.getUuid(), deserialized.getUuid());
        assertEquals(profile.getCountry(), deserialized.getCountry());
        assertEquals(profile.getCity(), deserialized.getCity());
        assertEquals(profile.getLanguage(), deserialized.getLanguage());
    }

    @Test
    void testVisitJsonSerialization() throws Exception {
        Visit visit = Visit.builder()
                .uuid("visit-456")
                .timestamp("2024-01-15T10:30:00Z")
                .landingPage("/home")
                .duration(300)
                .build();

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(visit);
        assertNotNull(json);
        assertTrue(json.contains("\"uuid\":\"visit-456\""));
        assertTrue(json.contains("\"landing_page\":\"/home\""));

        // Deserialize from JSON
        Visit deserialized = objectMapper.readValue(json, Visit.class);
        assertEquals(visit.getUuid(), deserialized.getUuid());
        assertEquals(visit.getTimestamp(), deserialized.getTimestamp());
        assertEquals(visit.getLandingPage(), deserialized.getLandingPage());
        assertEquals(visit.getDuration(), deserialized.getDuration());
    }

    @Test
    void testEventJsonSerialization() throws Exception {
        Event event = Event.builder()
                .uuid("event-789")
                .eventName("purchase")
                .parameter("amount", 99.99)
                .parameter("currency", "USD")
                .build();

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(event);
        assertNotNull(json);
        assertTrue(json.contains("\"uuid\":\"event-789\""));
        assertTrue(json.contains("\"event_name\":\"purchase\""));

        // Deserialize from JSON
        Event deserialized = objectMapper.readValue(json, Event.class);
        assertEquals(event.getUuid(), deserialized.getUuid());
        assertEquals(event.getEventName(), deserialized.getEventName());
        assertEquals(event.getParameters().get("amount"), deserialized.getParameters().get("amount"));
        assertEquals(event.getParameters().get("currency"), deserialized.getParameters().get("currency"));
    }

    @Test
    void testUserDataJsonSerialization() throws Exception {
        Profile profile = Profile.builder()
                .uuid("user-123")
                .country("USA")
                .build();

        Visit visit = Visit.builder()
                .uuid("visit-456")
                .timestamp("2024-01-15T10:30:00Z")
                .build();

        Event event = Event.builder()
                .uuid("event-789")
                .eventName("purchase")
                .build();

        UserData userData = UserData.builder()
                .profile(profile)
                .visit("visit-456", visit)
                .event(event)
                .build();

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(userData);
        assertNotNull(json);
        assertTrue(json.contains("\"profile\""));
        assertTrue(json.contains("\"visits\""));
        assertTrue(json.contains("\"events\""));

        // Deserialize from JSON
        UserData deserialized = objectMapper.readValue(json, UserData.class);
        assertNotNull(deserialized.getProfile());
        assertEquals(userData.getProfile().getUuid(), deserialized.getProfile().getUuid());
        assertEquals(1, deserialized.getVisits().size());
        assertEquals(1, deserialized.getEvents().size());
    }

    @Test
    void testDefaultConstructors() {
        // Test that default constructors work
        Profile profile = new Profile();
        assertNotNull(profile);

        Visit visit = new Visit();
        assertNotNull(visit);

        Event event = new Event();
        assertNotNull(event);
        assertNotNull(event.getParameters()); // Should initialize empty map

        UserData userData = new UserData();
        assertNotNull(userData);
        assertNotNull(userData.getVisits()); // Should initialize empty map
        assertNotNull(userData.getEvents()); // Should initialize empty list
    }

    @Test
    void testSettersAndGetters() {
        Profile profile = new Profile();
        profile.setUuid("test-uuid");
        profile.setCountry("Canada");
        
        assertEquals("test-uuid", profile.getUuid());
        assertEquals("Canada", profile.getCountry());

        Visit visit = new Visit();
        visit.setUuid("visit-uuid");
        visit.setDuration(500);
        
        assertEquals("visit-uuid", visit.getUuid());
        assertEquals(500, visit.getDuration());

        Event event = new Event();
        event.setUuid("event-uuid");
        event.setEventName("click");
        
        assertEquals("event-uuid", event.getUuid());
        assertEquals("click", event.getEventName());
    }

    // ========== TimeUnit Tests ==========

    @Test
    void testTimeUnitEnumValues() {
        // Test all time unit enum values exist
        assertEquals(6, TimeUnit.values().length);
        assertNotNull(TimeUnit.D);
        assertNotNull(TimeUnit.H);
        assertNotNull(TimeUnit.M);
        assertNotNull(TimeUnit.W);
        assertNotNull(TimeUnit.MO);
        assertNotNull(TimeUnit.Y);
    }

    @Test
    void testTimeUnitChronoUnitMapping() {
        // Test that each TimeUnit maps to the correct ChronoUnit
        assertEquals(ChronoUnit.DAYS, TimeUnit.D.getChronoUnit());
        assertEquals(ChronoUnit.HOURS, TimeUnit.H.getChronoUnit());
        assertEquals(ChronoUnit.MINUTES, TimeUnit.M.getChronoUnit());
        assertEquals(ChronoUnit.WEEKS, TimeUnit.W.getChronoUnit());
        assertEquals(ChronoUnit.MONTHS, TimeUnit.MO.getChronoUnit());
        assertEquals(ChronoUnit.YEARS, TimeUnit.Y.getChronoUnit());
    }

    @Test
    void testTimeUnitParseCaseSensitivity() {
        // Test case-insensitive parsing
        assertEquals(TimeUnit.D, TimeUnit.parse("D"));
        assertEquals(TimeUnit.D, TimeUnit.parse("d"));
        assertEquals(TimeUnit.H, TimeUnit.parse("H"));
        assertEquals(TimeUnit.H, TimeUnit.parse("h"));
        assertEquals(TimeUnit.M, TimeUnit.parse("M"));
        assertEquals(TimeUnit.M, TimeUnit.parse("m"));
        assertEquals(TimeUnit.W, TimeUnit.parse("W"));
        assertEquals(TimeUnit.W, TimeUnit.parse("w"));
        assertEquals(TimeUnit.MO, TimeUnit.parse("MO"));
        assertEquals(TimeUnit.MO, TimeUnit.parse("mo"));
        assertEquals(TimeUnit.MO, TimeUnit.parse("Mo"));
        assertEquals(TimeUnit.Y, TimeUnit.parse("Y"));
        assertEquals(TimeUnit.Y, TimeUnit.parse("y"));
    }

    @Test
    void testTimeUnitParseInvalidUnit() {
        // Test that invalid units throw IllegalArgumentException
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, 
            () -> TimeUnit.parse("X"));
        assertTrue(ex1.getMessage().contains("Unknown time unit"));
        assertTrue(ex1.getMessage().contains("Valid units: D, H, M, W, MO, Y"));

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, 
            () -> TimeUnit.parse("INVALID"));
        assertTrue(ex2.getMessage().contains("Unknown time unit"));
    }

    @Test
    void testTimeUnitParseNull() {
        // Test that null throws IllegalArgumentException
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
            () -> TimeUnit.parse(null));
        assertTrue(ex.getMessage().contains("Time unit cannot be null"));
    }

    // ========== TimeRange Tests ==========

    @Test
    void testTimeRangeConstructor() {
        Instant now = Instant.now();
        TimeRange timeRange = new TimeRange(30, TimeUnit.D, 0, TimeUnit.D, now);

        assertEquals(30, timeRange.getFromValue());
        assertEquals(TimeUnit.D, timeRange.getFromUnit());
        assertEquals(0, timeRange.getToValue());
        assertEquals(TimeUnit.D, timeRange.getToUnit());
        assertEquals(now, timeRange.getReferenceTime());
    }

    @Test
    void testTimeRangeConstructorWithNullReferenceTime() {
        // When reference time is null, it should default to Instant.now()
        TimeRange timeRange = new TimeRange(30, TimeUnit.D, 0, TimeUnit.D, null);

        assertNotNull(timeRange.getReferenceTime());
        // Reference time should be close to now (within 1 second)
        assertTrue(Math.abs(Instant.now().toEpochMilli() - 
                   timeRange.getReferenceTime().toEpochMilli()) < 1000);
    }

    @Test
    void testTimeRangeGetStartTime() {
        Instant now = Instant.parse("2024-01-15T12:00:00Z");
        TimeRange timeRange = new TimeRange(30, TimeUnit.D, 0, TimeUnit.D, now);

        Instant expectedStart = now.minus(30, ChronoUnit.DAYS);
        assertEquals(expectedStart, timeRange.getStartTime());
    }

    @Test
    void testTimeRangeGetEndTime() {
        Instant now = Instant.parse("2024-01-15T12:00:00Z");
        TimeRange timeRange = new TimeRange(30, TimeUnit.D, 0, TimeUnit.D, now);

        Instant expectedEnd = now.minus(0, ChronoUnit.DAYS);
        assertEquals(expectedEnd, timeRange.getEndTime());
    }

    @Test
    void testTimeRangeGetStartTimeWithNullValues() {
        Instant now = Instant.parse("2024-01-15T12:00:00Z");
        TimeRange timeRange = new TimeRange(null, null, 0, TimeUnit.D, now);

        // When fromValue or fromUnit is null, should return Instant.MIN
        assertEquals(Instant.MIN, timeRange.getStartTime());
    }

    @Test
    void testTimeRangeGetEndTimeWithNullValues() {
        Instant now = Instant.parse("2024-01-15T12:00:00Z");
        TimeRange timeRange = new TimeRange(30, TimeUnit.D, null, null, now);

        // When toValue or toUnit is null, should return reference time
        assertEquals(now, timeRange.getEndTime());
    }

    @Test
    void testTimeRangeContainsTimestampInRange() {
        Instant now = Instant.parse("2024-01-15T12:00:00Z");
        TimeRange timeRange = new TimeRange(30, TimeUnit.D, 0, TimeUnit.D, now);

        // Test timestamp within range
        Instant withinRange = now.minus(15, ChronoUnit.DAYS);
        assertTrue(timeRange.contains(withinRange));
    }

    @Test
    void testTimeRangeContainsTimestampAtBoundaries() {
        Instant now = Instant.parse("2024-01-15T12:00:00Z");
        TimeRange timeRange = new TimeRange(30, TimeUnit.D, 0, TimeUnit.D, now);

        // Test timestamp at start boundary (inclusive)
        Instant atStart = now.minus(30, ChronoUnit.DAYS);
        assertTrue(timeRange.contains(atStart));

        // Test timestamp at end boundary (inclusive)
        Instant atEnd = now;
        assertTrue(timeRange.contains(atEnd));
    }

    @Test
    void testTimeRangeContainsTimestampOutsideRange() {
        Instant now = Instant.parse("2024-01-15T12:00:00Z");
        TimeRange timeRange = new TimeRange(30, TimeUnit.D, 0, TimeUnit.D, now);

        // Test timestamp before range
        Instant beforeRange = now.minus(31, ChronoUnit.DAYS);
        assertFalse(timeRange.contains(beforeRange));

        // Test timestamp after range
        Instant afterRange = now.plus(1, ChronoUnit.DAYS);
        assertFalse(timeRange.contains(afterRange));
    }

    @Test
    void testTimeRangeContainsNullTimestamp() {
        Instant now = Instant.parse("2024-01-15T12:00:00Z");
        TimeRange timeRange = new TimeRange(30, TimeUnit.D, 0, TimeUnit.D, now);

        // Null timestamp should return false
        assertFalse(timeRange.contains(null));
    }

    @Test
    void testTimeRangeWithDifferentTimeUnits() {
        Instant now = Instant.parse("2024-01-15T12:00:00Z");

        // Test with hours
        TimeRange hoursRange = new TimeRange(24, TimeUnit.H, 0, TimeUnit.H, now);
        assertEquals(now.minus(24, ChronoUnit.HOURS), hoursRange.getStartTime());
        assertEquals(now, hoursRange.getEndTime());

        // Test with minutes
        TimeRange minutesRange = new TimeRange(60, TimeUnit.M, 0, TimeUnit.M, now);
        assertEquals(now.minus(60, ChronoUnit.MINUTES), minutesRange.getStartTime());
        assertEquals(now, minutesRange.getEndTime());

        // Test with weeks (converted to days internally)
        TimeRange weeksRange = new TimeRange(4, TimeUnit.W, 0, TimeUnit.W, now);
        assertEquals(now.minus(4 * 7, ChronoUnit.DAYS), weeksRange.getStartTime());
        assertEquals(now, weeksRange.getEndTime());

        // Test with months (approximated as 30 days internally)
        TimeRange monthsRange = new TimeRange(3, TimeUnit.MO, 0, TimeUnit.MO, now);
        assertEquals(now.minus(3 * 30, ChronoUnit.DAYS), monthsRange.getStartTime());
        assertEquals(now, monthsRange.getEndTime());

        // Test with years (approximated as 365 days internally)
        TimeRange yearsRange = new TimeRange(1, TimeUnit.Y, 0, TimeUnit.Y, now);
        assertEquals(now.minus(1 * 365, ChronoUnit.DAYS), yearsRange.getStartTime());
        assertEquals(now, yearsRange.getEndTime());
    }

    @Test
    void testTimeRangeWithMixedTimeUnits() {
        Instant now = Instant.parse("2024-01-15T12:00:00Z");
        
        // FROM 30 days ago TO 7 days ago
        TimeRange timeRange = new TimeRange(30, TimeUnit.D, 7, TimeUnit.D, now);

        Instant expectedStart = now.minus(30, ChronoUnit.DAYS);
        Instant expectedEnd = now.minus(7, ChronoUnit.DAYS);

        assertEquals(expectedStart, timeRange.getStartTime());
        assertEquals(expectedEnd, timeRange.getEndTime());

        // Test containment
        Instant within = now.minus(15, ChronoUnit.DAYS);
        assertTrue(timeRange.contains(within));

        Instant before = now.minus(31, ChronoUnit.DAYS);
        assertFalse(timeRange.contains(before));

        Instant after = now.minus(6, ChronoUnit.DAYS);
        assertFalse(timeRange.contains(after));
    }

    @Test
    void testTimeRangeToString() {
        Instant now = Instant.parse("2024-01-15T12:00:00Z");
        TimeRange timeRange = new TimeRange(30, TimeUnit.D, 0, TimeUnit.D, now);

        String str = timeRange.toString();
        assertNotNull(str);
        assertTrue(str.contains("TimeRange"));
        assertTrue(str.contains("from=30"));
        assertTrue(str.contains("to=0"));
        assertTrue(str.contains("D"));
    }

    @Test
    void testTimeRangeStartBeforeEnd() {
        Instant now = Instant.parse("2024-01-15T12:00:00Z");
        TimeRange timeRange = new TimeRange(30, TimeUnit.D, 0, TimeUnit.D, now);

        // Start time should always be before end time for valid ranges
        assertTrue(timeRange.getStartTime().isBefore(timeRange.getEndTime()) ||
                   timeRange.getStartTime().equals(timeRange.getEndTime()));
    }

    @Test
    void testTimeRangeWithZeroValues() {
        Instant now = Instant.parse("2024-01-15T12:00:00Z");
        
        // FROM 0 days ago TO 0 days ago (essentially "now")
        TimeRange timeRange = new TimeRange(0, TimeUnit.D, 0, TimeUnit.D, now);

        assertEquals(now, timeRange.getStartTime());
        assertEquals(now, timeRange.getEndTime());
        assertTrue(timeRange.contains(now));
    }

    // ========== BucketRange Tests ==========

    @Test
    void testBucketRangeBuilder() {
        BucketRange range = BucketRange.builder()
                .minValue(0.0)
                .maxValue(10.0)
                .minInclusive(true)
                .maxInclusive(false)
                .label("Low")
                .build();

        assertEquals(0.0, range.getMinValue());
        assertEquals(10.0, range.getMaxValue());
        assertTrue(range.isMinInclusive());
        assertFalse(range.isMaxInclusive());
        assertEquals("Low", range.getLabel());
    }

    @Test
    void testBucketRangeDefaultInclusivity() {
        // Default should be minInclusive=true, maxInclusive=false
        BucketRange range = BucketRange.builder()
                .minValue(0.0)
                .maxValue(10.0)
                .label("Test")
                .build();

        assertTrue(range.isMinInclusive());
        assertFalse(range.isMaxInclusive());
    }

    @Test
    void testBucketRangeContainsValueInRange() {
        BucketRange range = BucketRange.builder()
                .minValue(0.0)
                .maxValue(10.0)
                .minInclusive(true)
                .maxInclusive(false)
                .label("Low")
                .build();

        // Value in the middle of range
        assertTrue(range.contains(5.0));
        assertTrue(range.contains(0.1));
        assertTrue(range.contains(9.9));
    }

    @Test
    void testBucketRangeContainsMinBoundary() {
        // Test inclusive minimum
        BucketRange inclusiveMin = BucketRange.builder()
                .minValue(0.0)
                .maxValue(10.0)
                .minInclusive(true)
                .maxInclusive(false)
                .label("Test")
                .build();
        assertTrue(inclusiveMin.contains(0.0));

        // Test exclusive minimum
        BucketRange exclusiveMin = BucketRange.builder()
                .minValue(0.0)
                .maxValue(10.0)
                .minInclusive(false)
                .maxInclusive(false)
                .label("Test")
                .build();
        assertFalse(exclusiveMin.contains(0.0));
    }

    @Test
    void testBucketRangeContainsMaxBoundary() {
        // Test inclusive maximum
        BucketRange inclusiveMax = BucketRange.builder()
                .minValue(0.0)
                .maxValue(10.0)
                .minInclusive(true)
                .maxInclusive(true)
                .label("Test")
                .build();
        assertTrue(inclusiveMax.contains(10.0));

        // Test exclusive maximum
        BucketRange exclusiveMax = BucketRange.builder()
                .minValue(0.0)
                .maxValue(10.0)
                .minInclusive(true)
                .maxInclusive(false)
                .label("Test")
                .build();
        assertFalse(exclusiveMax.contains(10.0));
    }

    @Test
    void testBucketRangeContainsValueOutsideRange() {
        BucketRange range = BucketRange.builder()
                .minValue(0.0)
                .maxValue(10.0)
                .minInclusive(true)
                .maxInclusive(false)
                .label("Low")
                .build();

        // Values outside range
        assertFalse(range.contains(-0.1));
        assertFalse(range.contains(10.0));
        assertFalse(range.contains(10.1));
        assertFalse(range.contains(100.0));
    }

    @Test
    void testBucketRangeContainsNullValue() {
        BucketRange range = BucketRange.builder()
                .minValue(0.0)
                .maxValue(10.0)
                .label("Test")
                .build();

        // Null value should return false
        assertFalse(range.contains(null));
    }

    @Test
    void testBucketRangeWithNullMinValue() {
        // Null min value means unbounded on the lower end
        BucketRange range = BucketRange.builder()
                .minValue(null)
                .maxValue(10.0)
                .maxInclusive(false)
                .label("Low")
                .build();

        assertTrue(range.contains(-1000.0));
        assertTrue(range.contains(0.0));
        assertTrue(range.contains(5.0));
        assertFalse(range.contains(10.0)); // maxInclusive=false
        assertFalse(range.contains(100.0));
    }

    @Test
    void testBucketRangeWithNullMaxValue() {
        // Null max value means unbounded on the upper end
        BucketRange range = BucketRange.builder()
                .minValue(100.0)
                .maxValue(null)
                .minInclusive(true)
                .label("High")
                .build();

        assertFalse(range.contains(99.9));
        assertTrue(range.contains(100.0)); // minInclusive=true
        assertTrue(range.contains(500.0));
        assertTrue(range.contains(1000000.0));
    }

    @Test
    void testBucketRangeWithBothNullBoundaries() {
        // Both null means unbounded (matches everything)
        BucketRange range = BucketRange.builder()
                .minValue(null)
                .maxValue(null)
                .label("All")
                .build();

        assertTrue(range.contains(-1000000.0));
        assertTrue(range.contains(0.0));
        assertTrue(range.contains(1000000.0));
    }

    @Test
    void testBucketRangeToString() {
        BucketRange range = BucketRange.builder()
                .minValue(0.0)
                .maxValue(10.0)
                .minInclusive(true)
                .maxInclusive(false)
                .label("Low")
                .build();

        String str = range.toString();
        assertNotNull(str);
        assertTrue(str.contains("[0.0")); // [ indicates inclusive
        assertTrue(str.contains("10.0)")); // ) indicates exclusive
        assertTrue(str.contains("Low"));
    }

    @Test
    void testBucketRangeToStringWithExclusiveMin() {
        BucketRange range = BucketRange.builder()
                .minValue(0.0)
                .maxValue(10.0)
                .minInclusive(false)
                .maxInclusive(true)
                .label("Test")
                .build();

        String str = range.toString();
        assertTrue(str.contains("(0.0")); // ( indicates exclusive
        assertTrue(str.contains("10.0]")); // ] indicates inclusive
    }

    // ========== BucketDefinition Tests ==========

    @Test
    void testBucketDefinitionBuilder() {
        BucketDefinition buckets = BucketDefinition.builder()
                .range(0.0, 10.0, "Low")
                .range(10.0, 100.0, "Medium")
                .range(100.0, 500.0, "High")
                .defaultLabel("Other")
                .build();

        assertNotNull(buckets);
        assertEquals(3, buckets.getRanges().size());
        assertEquals("Other", buckets.getDefaultLabel());
    }

    @Test
    void testBucketDefinitionBuilderWithBucketRangeObjects() {
        BucketRange range1 = BucketRange.builder()
                .minValue(0.0)
                .maxValue(10.0)
                .label("Low")
                .build();

        BucketRange range2 = BucketRange.builder()
                .minValue(10.0)
                .maxValue(100.0)
                .label("Medium")
                .build();

        BucketDefinition buckets = BucketDefinition.builder()
                .range(range1)
                .range(range2)
                .defaultLabel("Other")
                .build();

        assertEquals(2, buckets.getRanges().size());
        assertEquals("Low", buckets.getRanges().get(0).getLabel());
        assertEquals("Medium", buckets.getRanges().get(1).getLabel());
    }

    @Test
    void testBucketDefinitionDefaultLabel() {
        // Default label should be "Other" if not specified
        BucketDefinition buckets = BucketDefinition.builder()
                .range(0.0, 10.0, "Low")
                .build();

        assertEquals("Other", buckets.getDefaultLabel());
    }

    @Test
    void testBucketDefinitionGetBucketLabelFirstMatch() {
        BucketDefinition buckets = BucketDefinition.builder()
                .range(0.0, 10.0, "Low")
                .range(10.0, 100.0, "Medium")
                .range(100.0, 500.0, "High")
                .defaultLabel("Other")
                .build();

        assertEquals("Low", buckets.getBucketLabel(5.0));
        assertEquals("Medium", buckets.getBucketLabel(50.0));
        assertEquals("High", buckets.getBucketLabel(200.0));
    }

    @Test
    void testBucketDefinitionGetBucketLabelAtBoundaries() {
        BucketDefinition buckets = BucketDefinition.builder()
                .range(0.0, 10.0, "Low")
                .range(10.0, 100.0, "Medium")
                .range(100.0, 500.0, "High")
                .defaultLabel("Other")
                .build();

        // Default is minInclusive=true, maxInclusive=false
        assertEquals("Low", buckets.getBucketLabel(0.0));
        assertEquals("Medium", buckets.getBucketLabel(10.0)); // 10.0 is in Medium range
        assertEquals("High", buckets.getBucketLabel(100.0)); // 100.0 is in High range
    }

    @Test
    void testBucketDefinitionGetBucketLabelOutsideAllRanges() {
        BucketDefinition buckets = BucketDefinition.builder()
                .range(0.0, 10.0, "Low")
                .range(10.0, 100.0, "Medium")
                .range(100.0, 500.0, "High")
                .defaultLabel("VeryHigh")
                .build();

        assertEquals("VeryHigh", buckets.getBucketLabel(-5.0));
        assertEquals("VeryHigh", buckets.getBucketLabel(500.0)); // maxInclusive=false by default
        assertEquals("VeryHigh", buckets.getBucketLabel(1000.0));
    }

    @Test
    void testBucketDefinitionGetBucketLabelWithOverlappingRanges() {
        // When ranges overlap, should return first matching range
        BucketRange range1 = BucketRange.builder()
                .minValue(0.0)
                .maxValue(20.0)
                .minInclusive(true)
                .maxInclusive(true)
                .label("First")
                .build();

        BucketRange range2 = BucketRange.builder()
                .minValue(10.0)
                .maxValue(30.0)
                .minInclusive(true)
                .maxInclusive(true)
                .label("Second")
                .build();

        BucketDefinition buckets = BucketDefinition.builder()
                .range(range1)
                .range(range2)
                .build();

        // Value 15.0 matches both ranges, should return first match
        assertEquals("First", buckets.getBucketLabel(15.0));
    }

    @Test
    void testBucketDefinitionGetBucketLabelWithNullValue() {
        BucketDefinition buckets = BucketDefinition.builder()
                .range(0.0, 10.0, "Low")
                .defaultLabel("Other")
                .build();

        // Null value should return default label
        assertEquals("Other", buckets.getBucketLabel(null));
    }

    @Test
    void testBucketDefinitionWithEmptyRanges() {
        BucketDefinition buckets = BucketDefinition.builder()
                .defaultLabel("NoRanges")
                .build();

        assertEquals(0, buckets.getRanges().size());
        assertEquals("NoRanges", buckets.getBucketLabel(5.0));
        assertEquals("NoRanges", buckets.getBucketLabel(100.0));
    }

    @Test
    void testBucketDefinitionWithUnboundedRanges() {
        BucketDefinition buckets = BucketDefinition.builder()
                .range(BucketRange.builder()
                        .minValue(null)
                        .maxValue(10.0)
                        .label("Low")
                        .build())
                .range(BucketRange.builder()
                        .minValue(10.0)
                        .maxValue(100.0)
                        .label("Medium")
                        .build())
                .range(BucketRange.builder()
                        .minValue(100.0)
                        .maxValue(null)
                        .label("High")
                        .build())
                .defaultLabel("Other")
                .build();

        assertEquals("Low", buckets.getBucketLabel(-1000.0));
        assertEquals("Low", buckets.getBucketLabel(5.0));
        assertEquals("Medium", buckets.getBucketLabel(50.0));
        assertEquals("High", buckets.getBucketLabel(500.0));
        assertEquals("High", buckets.getBucketLabel(1000000.0));
    }

    @Test
    void testBucketDefinitionConstructorWithNullRanges() {
        // Constructor should handle null ranges list
        BucketDefinition buckets = new BucketDefinition(null, "Default");

        assertNotNull(buckets.getRanges());
        assertEquals(0, buckets.getRanges().size());
        assertEquals("Default", buckets.getDefaultLabel());
    }

    @Test
    void testBucketDefinitionRealWorldExample() {
        // Real-world example: Purchase amount segmentation
        BucketDefinition purchaseBuckets = BucketDefinition.builder()
                .range(0.0, 10.0, "Micro")
                .range(10.0, 50.0, "Small")
                .range(50.0, 100.0, "Medium")
                .range(100.0, 500.0, "Large")
                .range(500.0, 1000.0, "Premium")
                .range(BucketRange.builder()
                        .minValue(1000.0)
                        .maxValue(null)
                        .label("VIP")
                        .build())
                .defaultLabel("Invalid")
                .build();

        assertEquals("Micro", purchaseBuckets.getBucketLabel(5.99));
        assertEquals("Small", purchaseBuckets.getBucketLabel(25.00));
        assertEquals("Medium", purchaseBuckets.getBucketLabel(75.50));
        assertEquals("Large", purchaseBuckets.getBucketLabel(250.00));
        assertEquals("Premium", purchaseBuckets.getBucketLabel(750.00));
        assertEquals("VIP", purchaseBuckets.getBucketLabel(5000.00));
        assertEquals("Invalid", purchaseBuckets.getBucketLabel(-10.00));
    }
}

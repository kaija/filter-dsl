package com.filter.dsl.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

class ProfileTest {

    @Test
    void testProfileBuilderWithPermanentAttributes() {
        Profile profile = Profile.builder()
            .uuid("user-123")
            .userId("test@example.com")
            .gender("male")
            .birthday("1990-05-15")
            .language("zh-TW")
            .build();

        assertEquals("user-123", profile.getUuid());
        assertEquals("test@example.com", profile.getUserId());
        assertEquals("male", profile.getGender());
        assertEquals("1990-05-15", profile.getBirthday());
        assertEquals("zh-TW", profile.getLanguage());
    }

    @Test
    void testFirstReferral() {
        Profile.FirstReferral referral = Profile.FirstReferral.builder()
            .timestamp("2024-01-15T10:00:00Z")
            .source("google.com")
            .medium("organic")
            .campaign("summer_sale")
            .landingPage("/products")
            .referrerUrl("https://google.com/search")
            .build();

        Profile profile = Profile.builder()
            .uuid("user-456")
            .firstReferral(referral)
            .build();

        assertNotNull(profile.getFirstReferral());
        assertEquals("2024-01-15T10:00:00Z", profile.getFirstReferral().getTimestamp());
        assertEquals("google.com", profile.getFirstReferral().getSource());
        assertEquals("organic", profile.getFirstReferral().getMedium());
        assertEquals("summer_sale", profile.getFirstReferral().getCampaign());
        assertEquals("/products", profile.getFirstReferral().getLandingPage());
    }

    @Test
    void testCustomProperties() {
        Profile profile = Profile.builder()
            .uuid("user-789")
            .customProperty("membership_level", "premium")
            .customProperty("loyalty_points", 1500)
            .customProperty("email_subscribed", true)
            .build();

        assertEquals("premium", profile.getCustomProperty("membership_level"));
        assertEquals(1500, profile.getCustomProperty("loyalty_points"));
        assertEquals(true, profile.getCustomProperty("email_subscribed"));
        
        Map<String, Object> customProps = profile.getCustomProperties();
        assertEquals(3, customProps.size());
        assertTrue(customProps.containsKey("membership_level"));
    }

    @Test
    void testSetCustomPropertyAfterCreation() {
        Profile profile = new Profile();
        profile.setUuid("user-999");
        profile.setCustomProperty("vip_status", true);
        profile.setCustomProperty("last_purchase_amount", 299.99);

        assertEquals(true, profile.getCustomProperty("vip_status"));
        assertEquals(299.99, profile.getCustomProperty("last_purchase_amount"));
    }

    @Test
    void testComputedPropertyExpressions() {
        Profile profile = Profile.builder()
            .uuid("user-computed")
            .computedProperty("total_visits", "COUNT(userData.visits)")
            .computedProperty("total_events", "COUNT(userData.events)")
            .computedProperty("user_type", "IF(GT(COUNT(userData.visits), 1), 'returning', 'new')")
            .build();

        Map<String, String> expressions = profile.getAllComputedPropertyExpressions();
        assertEquals(3, expressions.size());
        assertEquals("COUNT(userData.visits)", expressions.get("total_visits"));
        assertEquals("COUNT(userData.events)", expressions.get("total_events"));
        assertEquals("IF(GT(COUNT(userData.visits), 1), 'returning', 'new')", expressions.get("user_type"));
    }

    @Test
    void testDefineComputedPropertyAfterCreation() {
        Profile profile = new Profile();
        profile.setUuid("user-123");
        profile.defineComputedProperty("total_visits", "COUNT(userData.visits)");
        profile.defineComputedProperty("last_visit", "MAX(userData.visits, 'timestamp')");

        assertEquals("COUNT(userData.visits)", 
                    profile.getComputedPropertyExpression("total_visits"));
        assertEquals("MAX(userData.visits, 'timestamp')", 
                    profile.getComputedPropertyExpression("last_visit"));
    }

    @Test
    void testSetFirstReferralFromVisit() {
        Visit firstVisit = Visit.builder()
            .uuid("visit-1")
            .timestamp("2024-01-15T10:00:00Z")
            .isFirstVisit(true)
            .referrerType("cpc")
            .referrerUrl("https://facebook.com/ads")
            .landingPage("/promo")
            .build();

        Profile profile = Profile.builder()
            .uuid("user-123")
            .build();

        ProfileComputer.setFirstReferralFromVisit(profile, firstVisit);

        assertNotNull(profile.getFirstReferral());
        assertEquals("2024-01-15T10:00:00Z", profile.getFirstReferral().getTimestamp());
        assertEquals("facebook.com", profile.getFirstReferral().getSource());
        assertEquals("cpc", profile.getFirstReferral().getMedium());
        assertEquals("/promo", profile.getFirstReferral().getLandingPage());
    }

    @Test
    void testSetFirstReferralFromNonFirstVisit() {
        Visit regularVisit = Visit.builder()
            .uuid("visit-2")
            .timestamp("2024-01-16T10:00:00Z")
            .isFirstVisit(false)
            .build();

        Profile profile = Profile.builder()
            .uuid("user-123")
            .build();

        ProfileComputer.setFirstReferralFromVisit(profile, regularVisit);

        assertNull(profile.getFirstReferral());
    }

    @Test
    void testStandardComputedPropertiesTemplate() {
        Profile profile = ProfileComputer.createWithStandardComputedProperties();
        
        Map<String, String> expressions = profile.getAllComputedPropertyExpressions();
        
        // Device attributes are now computed via DSL TOP function
        assertTrue(expressions.containsKey("os"));
        assertTrue(expressions.containsKey("browser"));
        assertTrue(expressions.containsKey("device"));
        
        // Behavior metrics
        assertTrue(expressions.containsKey("total_visits"));
        assertTrue(expressions.containsKey("total_events"));
        assertTrue(expressions.containsKey("last_visit_date"));
        assertTrue(expressions.containsKey("user_type"));
        
        // Verify TOP function is used for device attributes
        assertEquals("TOP(userData.visits, 'os')", expressions.get("os"));
        assertEquals("TOP(userData.visits, 'browser')", expressions.get("browser"));
        assertEquals("TOP(userData.visits, 'device')", expressions.get("device"));
    }

    @Test
    void testCompleteProfile() {
        Profile profile = Profile.builder()
            .uuid("user-complete")
            .userId("complete@example.com")
            .gender("female")
            .birthday("1985-08-20")
            .language("en-US")
            .firstReferral(Profile.FirstReferral.builder()
                .timestamp("2023-01-01T00:00:00Z")
                .source("google.com")
                .medium("organic")
                .build())
            .customProperty("membership_level", "gold")
            .customProperty("loyalty_points", 5000)
            .computedProperty("total_visits", "COUNT(userData.visits)")
            .computedProperty("total_events", "COUNT(userData.events)")
            .build();

        // Permanent attributes
        assertEquals("user-complete", profile.getUuid());
        assertEquals("complete@example.com", profile.getUserId());
        assertEquals("female", profile.getGender());
        assertEquals("1985-08-20", profile.getBirthday());
        assertEquals("en-US", profile.getLanguage());

        // First referral
        assertNotNull(profile.getFirstReferral());
        assertEquals("google.com", profile.getFirstReferral().getSource());

        // Custom properties
        assertEquals("gold", profile.getCustomProperty("membership_level"));
        assertEquals(5000, profile.getCustomProperty("loyalty_points"));

        // Computed properties
        assertEquals(2, profile.getAllComputedPropertyExpressions().size());
    }

    @Test
    void testEmptyProfile() {
        Profile profile = new Profile();
        
        assertNull(profile.getUuid());
        assertNull(profile.getUserId());
        assertNull(profile.getGender());
        assertNull(profile.getBirthday());
        assertNull(profile.getLanguage());
        assertNull(profile.getFirstReferral());
        assertTrue(profile.getCustomProperties().isEmpty());
        assertTrue(profile.getAllComputedPropertyExpressions().isEmpty());
    }

    @Test
    void testFirstReferralDirectSource() {
        Visit directVisit = Visit.builder()
            .uuid("visit-1")
            .timestamp("2024-01-15T10:00:00Z")
            .isFirstVisit(true)
            .referrerType("direct")
            .referrerUrl("")
            .build();

        Profile profile = Profile.builder()
            .uuid("user-123")
            .build();

        ProfileComputer.setFirstReferralFromVisit(profile, directVisit);

        assertNotNull(profile.getFirstReferral());
        assertEquals("direct", profile.getFirstReferral().getSource());
    }
}

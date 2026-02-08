package com.filter.dsl.examples;

import com.filter.dsl.models.Profile;
import com.filter.dsl.models.Profile.FirstReferral;
import com.filter.dsl.models.ProfileComputer;
import com.filter.dsl.models.Visit;

/**
 * Example demonstrating the new Profile model with permanent attributes and dynamic computed properties.
 */
public class ProfileUsageExample {

    public static void main(String[] args) {
        System.out.println("=== Profile Model - Permanent Attributes & Dynamic Properties ===\n");

        // ===== Example 1: Create profile with permanent attributes =====
        System.out.println("--- Example 1: Permanent Attributes ---");
        
        Profile profile = Profile.builder()
            .uuid("user-123")
            .userId("john.doe@example.com")
            .gender("male")
            .birthday("1990-05-15")
            .language("zh-TW")
            .country("Taiwan")
            .city("Taipei")
            .region("Taipei City")
            .build();

        System.out.println("UUID: " + profile.getUuid());
        System.out.println("User ID: " + profile.getUserId());
        System.out.println("Gender: " + profile.getGender());
        System.out.println("Birthday: " + profile.getBirthday());
        System.out.println("Language: " + profile.getLanguage());
        System.out.println("Country: " + profile.getCountry());
        System.out.println("City: " + profile.getCity());

        // ===== Example 2: Set first referral (permanent, set once) =====
        System.out.println("\n--- Example 2: First Referral (Permanent) ---");
        
        FirstReferral firstReferral = FirstReferral.builder()
            .timestamp("2023-06-01T10:00:00Z")
            .source("google.com")
            .medium("organic")
            .campaign("summer_sale")
            .landingPage("/products")
            .referrerUrl("https://google.com/search?q=example")
            .build();

        profile.setFirstReferral(firstReferral);

        System.out.println("First Visit: " + profile.getFirstReferral().getTimestamp());
        System.out.println("First Source: " + profile.getFirstReferral().getSource());
        System.out.println("First Medium: " + profile.getFirstReferral().getMedium());
        System.out.println("First Campaign: " + profile.getFirstReferral().getCampaign());

        // ===== Example 3: Add custom properties =====
        System.out.println("\n--- Example 3: Custom Properties ---");
        
        profile.setCustomProperty("membership_level", "premium");
        profile.setCustomProperty("loyalty_points", 1500);
        profile.setCustomProperty("email_subscribed", true);
        profile.setCustomProperty("favorite_category", "electronics");

        System.out.println("Membership Level: " + profile.getCustomProperty("membership_level"));
        System.out.println("Loyalty Points: " + profile.getCustomProperty("loyalty_points"));
        System.out.println("Email Subscribed: " + profile.getCustomProperty("email_subscribed"));

        // ===== Example 4: Define computed properties with DSL expressions =====
        System.out.println("\n--- Example 4: Computed Properties (DSL Expressions) ---");
        
        Profile profileWithComputed = Profile.builder()
            .uuid("user-456")
            .userId("jane@example.com")
            .gender("female")
            .birthday("1985-08-20")
            .language("en-US")
            .country("United States")
            .city("San Francisco")
            // Define DSL expressions for dynamic computation
            .computedProperty("total_visits", "COUNT(userData.visits)")
            .computedProperty("total_events", "COUNT(userData.events)")
            .computedProperty("last_visit_date", "MAX(userData.visits, 'timestamp')")
            .computedProperty("user_type", "IF(GT(COUNT(userData.visits), 1), 'returning', 'new')")
            .build();

        System.out.println("Defined computed properties:");
        profileWithComputed.getAllComputedPropertyExpressions().forEach((key, value) -> 
            System.out.println("  " + key + " = " + value)
        );

        System.out.println("\nNote: Actual computation requires ProfileComputer with DSLEvaluator and userData.");

        // Note: Actual computation requires ProfileComputer with DSLEvaluator
        // profileWithComputed = new ProfileComputer(evaluator, contextManager).computeProperties(profileWithComputed);

        // ===== Example 5: Standard computed properties template =====
        System.out.println("\n--- Example 5: Standard Computed Properties Template ---");
        
        Profile standardProfile = ProfileComputer.createWithStandardComputedProperties();
        standardProfile.setUuid("user-789");
        standardProfile.setUserId("alice@example.com");
        standardProfile.setGender("female");
        standardProfile.setBirthday("1995-03-10");
        standardProfile.setLanguage("zh-TW");
        standardProfile.setCountry("Taiwan");
        standardProfile.setCity("Taipei");

        System.out.println("Standard profile created with computed properties:");
        standardProfile.getAllComputedPropertyExpressions().forEach((key, value) -> 
            System.out.println("  " + key + " = " + value)
        );
        
        System.out.println("\nNote: Device attributes (os, browser, device) are now computed via DSL TOP function.");

        // ===== Example 6: Set first referral from first visit =====
        System.out.println("\n--- Example 6: Set First Referral from Visit ---");
        
        Visit firstVisit = Visit.builder()
            .uuid("visit-1")
            .timestamp("2024-01-15T10:00:00Z")
            .isFirstVisit(true)
            .referrerType("cpc")
            .referrerUrl("https://facebook.com/ads")
            .landingPage("/promo")
            .build();

        Profile newUserProfile = Profile.builder()
            .uuid("user-999")
            .language("zh-TW")
            .build();

        ProfileComputer.setFirstReferralFromVisit(newUserProfile, firstVisit);

        System.out.println("First referral set from visit:");
        System.out.println("  Timestamp: " + newUserProfile.getFirstReferral().getTimestamp());
        System.out.println("  Source: " + newUserProfile.getFirstReferral().getSource());
        System.out.println("  Medium: " + newUserProfile.getFirstReferral().getMedium());

        // ===== Example 7: Complete profile example =====
        System.out.println("\n--- Example 7: Complete Profile ---");
        
        Profile completeProfile = Profile.builder()
            // Permanent attributes
            .uuid("user-complete")
            .userId("complete@example.com")
            .gender("male")
            .birthday("1988-12-25")
            .language("zh-TW")
            .country("Taiwan")
            .city("Taipei")
            .region("Taipei City")
            .firstReferral(FirstReferral.builder()
                .timestamp("2023-01-01T00:00:00Z")
                .source("google.com")
                .medium("organic")
                .campaign("brand")
                .build())
            // Custom properties
            .customProperty("membership_level", "gold")
            .customProperty("loyalty_points", 5000)
            .customProperty("lifetime_value", 25000.0)
            .customProperty("vip_status", true)
            // Computed properties
            .computedProperty("os", "TOP(userData.visits, 'os')")
            .computedProperty("browser", "TOP(userData.visits, 'browser')")
            .computedProperty("total_visits", "COUNT(userData.visits)")
            .computedProperty("total_events", "COUNT(userData.events)")
            .build();

        System.out.println("Complete Profile:");
        System.out.println("  UUID: " + completeProfile.getUuid());
        System.out.println("  User ID: " + completeProfile.getUserId());
        System.out.println("  Gender: " + completeProfile.getGender());
        System.out.println("  Birthday: " + completeProfile.getBirthday());
        System.out.println("  Language: " + completeProfile.getLanguage());
        System.out.println("  Country: " + completeProfile.getCountry());
        System.out.println("  City: " + completeProfile.getCity());
        System.out.println("  First Source: " + completeProfile.getFirstReferral().getSource());
        System.out.println("  Membership: " + completeProfile.getCustomProperty("membership_level"));
        System.out.println("  Computed Properties: " + completeProfile.getAllComputedPropertyExpressions().size());
        
        System.out.println("\nNote: Use ProfileComputer.computeProperties() to evaluate all computed properties.");

        // ===== Summary =====
        System.out.println("\n=== Summary ===");
        System.out.println("Fixed Attributes: uuid, user_id, gender, birthday, language, country, city, region, continent, timezone, first_referral");
        System.out.println("Dynamic Properties: Custom business attributes via customProperties");
        System.out.println("Computed Properties: Defined via DSL expressions, computed on-demand");
        System.out.println("\nKey Benefits:");
        System.out.println("  - Fixed geographic and demographic attributes");
        System.out.println("  - Flexible custom properties for business-specific attributes");
        System.out.println("  - DSL-based computed properties for device/behavior aggregations from visits/events");
        System.out.println("  - Age computed automatically from birthday");
        System.out.println("  - First referral captured once and stored permanently");
        System.out.println("\nKey Changes:");
        System.out.println("  - age → birthday (age auto-computed)");
        System.out.println("  - userType → computed via DSL");
        System.out.println("  - first_source/medium/campaign → consolidated into first_referral");
        System.out.println("  - Device attributes (os, browser, device) → computed via DSL TOP function");
    }
}

package com.filter.dsl.models;

import com.filter.dsl.evaluator.DSLEvaluator;
import com.filter.dsl.context.DataContextManager;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Computes dynamic profile properties using DSL expressions.
 * 
 * Example computed properties:
 * - os = "TOP COUNT UNIQUE VISIT('os')"
 * - total_visits = "COUNT VISIT()"
 * - age = computed from birthday
 * - is_logged_in = userId != null
 * - user_type = "new" if first visit exists, else "returning"
 */
public class ProfileComputer {

    private final DSLEvaluator evaluator;
    private final DataContextManager contextManager;

    public ProfileComputer(DSLEvaluator evaluator, DataContextManager contextManager) {
        this.evaluator = evaluator;
        this.contextManager = contextManager;
    }

    /**
     * Computes all defined dynamic properties for a profile.
     * 
     * @param profile Profile with computed property expressions defined
     * @param userData User data context for evaluation
     * @return Profile with computed values populated in customProperties
     */
    public Profile computeProperties(Profile profile, UserData userData) {
        if (profile == null) {
            return null;
        }

        Map<String, String> expressions = profile.getAllComputedPropertyExpressions();
        
        for (Map.Entry<String, String> entry : expressions.entrySet()) {
            String propertyName = entry.getKey();
            String dslExpression = entry.getValue();
            
            try {
                Object computedValue = evaluator.evaluate(dslExpression, userData);
                profile.setCustomProperty(propertyName, computedValue);
            } catch (Exception e) {
                // Log error but continue with other properties
                System.err.println("Failed to compute property '" + propertyName + 
                                 "' with expression '" + dslExpression + "': " + e.getMessage());
            }
        }

        // Compute built-in derived properties
        computeBuiltInProperties(profile);

        return profile;
    }

    /**
     * Computes built-in derived properties that don't require DSL expressions.
     */
    private void computeBuiltInProperties(Profile profile) {
        // Compute age from birthday
        if (profile.getBirthday() != null && !profile.getBirthday().isEmpty()) {
            try {
                int age = computeAge(profile.getBirthday());
                profile.setCustomProperty("age", age);
                profile.setCustomProperty("age_range", getAgeRange(age));
            } catch (Exception e) {
                System.err.println("Failed to compute age from birthday: " + e.getMessage());
            }
        }

        // Compute first_visit_date from first_referral
        if (profile.getFirstReferral() != null && profile.getFirstReferral().getTimestamp() != null) {
            profile.setCustomProperty("first_visit_date", profile.getFirstReferral().getTimestamp());
        }
    }

    /**
     * Computes age from birthday string (ISO format: "1990-05-15").
     */
    private int computeAge(String birthday) {
        LocalDate birthDate = LocalDate.parse(birthday, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthDate, currentDate).getYears();
    }

    /**
     * Gets age range category from age.
     */
    private String getAgeRange(int age) {
        if (age < 18) return "0-17";
        if (age < 25) return "18-24";
        if (age < 35) return "25-34";
        if (age < 45) return "35-44";
        if (age < 55) return "45-54";
        if (age < 65) return "55-64";
        return "65+";
    }

    /**
     * Creates a profile with common computed property definitions.
     * Device attributes (os, browser, device) are now computed via DSL using TOP function.
     * 
     * @return Profile with standard computed properties defined
     */
    public static Profile createWithStandardComputedProperties() {
        return Profile.builder()
            // Device attributes (computed via DSL TOP function)
            .computedProperty("os", "TOP(userData.visits, 'os')")
            .computedProperty("browser", "TOP(userData.visits, 'browser')")
            .computedProperty("device", "TOP(userData.visits, 'device')")
            
            // Behavior metrics (computed via DSL)
            .computedProperty("total_visits", "COUNT(userData.visits)")
            .computedProperty("total_events", "COUNT(userData.events)")
            .computedProperty("last_visit_date", "MAX(userData.visits, 'timestamp')")
            
            // User type (computed via DSL)
            .computedProperty("user_type", "IF(GT(COUNT(userData.visits), 1), 'returning', 'new')")
            
            .build();
    }

    /**
     * Helper to set first referral from first visit.
     */
    public static void setFirstReferralFromVisit(Profile profile, Visit firstVisit) {
        if (firstVisit == null || !Boolean.TRUE.equals(firstVisit.getIsFirstVisit())) {
            return;
        }

        Profile.FirstReferral referral = Profile.FirstReferral.builder()
            .timestamp(firstVisit.getTimestamp())
            .source(extractSource(firstVisit.getReferrerUrl()))
            .medium(firstVisit.getReferrerType())
            .landingPage(firstVisit.getLandingPage())
            .referrerUrl(firstVisit.getReferrerUrl())
            .build();

        profile.setFirstReferral(referral);
    }

    /**
     * Extracts source domain from referrer URL.
     */
    private static String extractSource(String referrerUrl) {
        if (referrerUrl == null || referrerUrl.isEmpty()) {
            return "direct";
        }
        
        try {
            String domain = referrerUrl.replaceAll("^https?://", "")
                                      .replaceAll("/.*$", "");
            return domain;
        } catch (Exception e) {
            return "unknown";
        }
    }
}

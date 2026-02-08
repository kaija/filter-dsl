package com.filter.dsl.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

/**
 * User profile containing permanent user attributes and dynamic computed properties.
 * 
 * Fixed Fields (Permanent Attributes):
 * - Geographic: country, city, region, continent, timezone
 * - User identification: uuid, user_id
 * - Demographics: gender, birthday, language
 * - First acquisition: first_referral (replaces first_source, first_medium, first_campaign)
 * 
 * Dynamic Properties (Computed via DSL):
 * - Device attributes: os, browser, device (computed from visits)
 * - Behavior metrics: total_visits, total_events (computed)
 * - Custom business properties: membership_level, loyalty_points, etc.
 */
public class Profile {
    // ===== GEOGRAPHIC ATTRIBUTES (Fixed Fields) =====
    
    @JsonProperty("country")
    private String country;

    @JsonProperty("city")
    private String city;

    @JsonProperty("region")
    private String region;

    @JsonProperty("continent")
    private String continent;

    @JsonProperty("timezone")
    private String timezone;

    // ===== USER IDENTIFICATION =====
    
    @JsonProperty("uuid")
    private String uuid;

    @JsonProperty("user_id")
    private String userId;

    // ===== DEMOGRAPHICS (Permanent) =====
    
    @JsonProperty("gender")
    private String gender; // "male" / "female" / "other"

    @JsonProperty("birthday")
    private String birthday; // ISO date format: "1990-05-15" (replaces age)

    @JsonProperty("language")
    private String language;

    // ===== FIRST ACQUISITION (Permanent - set once on first visit) =====
    
    @JsonProperty("first_referral")
    private FirstReferral firstReferral;

    // ===== DYNAMIC PROPERTIES =====
    // Device attributes, behavior metrics, and custom business properties
    // can be computed dynamically via DSL expressions
    private Map<String, Object> customProperties = new HashMap<>();

    // DSL expressions for computing dynamic properties
    private Map<String, String> computedPropertyExpressions = new HashMap<>();

    // Constructors
    public Profile() {}

    // ===== DYNAMIC PROPERTIES SUPPORT =====
    
    @JsonAnyGetter
    public Map<String, Object> getCustomProperties() {
        return customProperties;
    }

    @JsonAnySetter
    public void setCustomProperty(String key, Object value) {
        customProperties.put(key, value);
    }

    public Object getCustomProperty(String key) {
        return customProperties.get(key);
    }

    /**
     * Define a DSL expression to compute a property dynamically.
     * Example: defineComputedProperty("os", "TOP COUNT UNIQUE VISIT('os')")
     */
    public void defineComputedProperty(String propertyName, String dslExpression) {
        computedPropertyExpressions.put(propertyName, dslExpression);
    }

    public String getComputedPropertyExpression(String propertyName) {
        return computedPropertyExpressions.get(propertyName);
    }

    public Map<String, String> getAllComputedPropertyExpressions() {
        return computedPropertyExpressions;
    }

    // ===== GETTERS AND SETTERS FOR FIXED ATTRIBUTES =====

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public FirstReferral getFirstReferral() {
        return firstReferral;
    }

    public void setFirstReferral(FirstReferral firstReferral) {
        this.firstReferral = firstReferral;
    }

    // ===== BUILDER PATTERN =====
    
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Profile profile = new Profile();

        public Builder country(String country) {
            profile.country = country;
            return this;
        }

        public Builder city(String city) {
            profile.city = city;
            return this;
        }

        public Builder region(String region) {
            profile.region = region;
            return this;
        }

        public Builder continent(String continent) {
            profile.continent = continent;
            return this;
        }

        public Builder timezone(String timezone) {
            profile.timezone = timezone;
            return this;
        }

        public Builder uuid(String uuid) {
            profile.uuid = uuid;
            return this;
        }

        public Builder userId(String userId) {
            profile.userId = userId;
            return this;
        }

        public Builder gender(String gender) {
            profile.gender = gender;
            return this;
        }

        public Builder birthday(String birthday) {
            profile.birthday = birthday;
            return this;
        }

        public Builder language(String language) {
            profile.language = language;
            return this;
        }

        public Builder firstReferral(FirstReferral firstReferral) {
            profile.firstReferral = firstReferral;
            return this;
        }

        public Builder customProperty(String key, Object value) {
            profile.customProperties.put(key, value);
            return this;
        }

        public Builder computedProperty(String propertyName, String dslExpression) {
            profile.computedPropertyExpressions.put(propertyName, dslExpression);
            return this;
        }

        public Profile build() {
            return profile;
        }
    }

    // ===== NESTED CLASS: FirstReferral =====
    
    /**
     * First referral information (permanent, set once on first visit).
     */
    public static class FirstReferral {
        @JsonProperty("timestamp")
        private String timestamp;

        @JsonProperty("source")
        private String source;

        @JsonProperty("medium")
        private String medium;

        @JsonProperty("campaign")
        private String campaign;

        @JsonProperty("landing_page")
        private String landingPage;

        @JsonProperty("referrer_url")
        private String referrerUrl;

        public FirstReferral() {}

        // Getters and Setters
        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getMedium() {
            return medium;
        }

        public void setMedium(String medium) {
            this.medium = medium;
        }

        public String getCampaign() {
            return campaign;
        }

        public void setCampaign(String campaign) {
            this.campaign = campaign;
        }

        public String getLandingPage() {
            return landingPage;
        }

        public void setLandingPage(String landingPage) {
            this.landingPage = landingPage;
        }

        public String getReferrerUrl() {
            return referrerUrl;
        }

        public void setReferrerUrl(String referrerUrl) {
            this.referrerUrl = referrerUrl;
        }

        // Builder
        public static FirstReferralBuilder builder() {
            return new FirstReferralBuilder();
        }

        public static class FirstReferralBuilder {
            private final FirstReferral referral = new FirstReferral();

            public FirstReferralBuilder timestamp(String timestamp) {
                referral.timestamp = timestamp;
                return this;
            }

            public FirstReferralBuilder source(String source) {
                referral.source = source;
                return this;
            }

            public FirstReferralBuilder medium(String medium) {
                referral.medium = medium;
                return this;
            }

            public FirstReferralBuilder campaign(String campaign) {
                referral.campaign = campaign;
                return this;
            }

            public FirstReferralBuilder landingPage(String landingPage) {
                referral.landingPage = landingPage;
                return this;
            }

            public FirstReferralBuilder referrerUrl(String referrerUrl) {
                referral.referrerUrl = referrerUrl;
                return this;
            }

            public FirstReferral build() {
                return referral;
            }
        }
    }
}

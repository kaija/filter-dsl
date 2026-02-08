package com.filter.dsl.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Visit (session) record containing timestamp, landing page, referrer, and duration information.
 */
public class Visit {
    @JsonProperty("uuid")
    private String uuid;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("landing_page")
    private String landingPage;

    @JsonProperty("referrer_type")
    private String referrerType;

    @JsonProperty("referrer_url")
    private String referrerUrl;

    @JsonProperty("referrer_query")
    private String referrerQuery;

    @JsonProperty("duration")
    private Integer duration;

    @JsonProperty("actions")
    private Integer actions;

    @JsonProperty("is_first_visit")
    private Boolean isFirstVisit;

    // Device/Session attributes (moved from Profile - these are session-specific)
    @JsonProperty("os")
    private String os;

    @JsonProperty("browser")
    private String browser;

    @JsonProperty("device")
    private String device;

    @JsonProperty("screen")
    private String screen;

    // Constructors
    public Visit() {}

    // Getters and Setters
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getLandingPage() {
        return landingPage;
    }

    public void setLandingPage(String landingPage) {
        this.landingPage = landingPage;
    }

    public String getReferrerType() {
        return referrerType;
    }

    public void setReferrerType(String referrerType) {
        this.referrerType = referrerType;
    }

    public String getReferrerUrl() {
        return referrerUrl;
    }

    public void setReferrerUrl(String referrerUrl) {
        this.referrerUrl = referrerUrl;
    }

    public String getReferrerQuery() {
        return referrerQuery;
    }

    public void setReferrerQuery(String referrerQuery) {
        this.referrerQuery = referrerQuery;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getActions() {
        return actions;
    }

    public void setActions(Integer actions) {
        this.actions = actions;
    }

    public Boolean getIsFirstVisit() {
        return isFirstVisit;
    }

    public void setIsFirstVisit(Boolean isFirstVisit) {
        this.isFirstVisit = isFirstVisit;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getScreen() {
        return screen;
    }

    public void setScreen(String screen) {
        this.screen = screen;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Visit visit = new Visit();

        public Builder uuid(String uuid) {
            visit.uuid = uuid;
            return this;
        }

        public Builder timestamp(String timestamp) {
            visit.timestamp = timestamp;
            return this;
        }

        public Builder landingPage(String landingPage) {
            visit.landingPage = landingPage;
            return this;
        }

        public Builder referrerType(String referrerType) {
            visit.referrerType = referrerType;
            return this;
        }

        public Builder referrerUrl(String referrerUrl) {
            visit.referrerUrl = referrerUrl;
            return this;
        }

        public Builder referrerQuery(String referrerQuery) {
            visit.referrerQuery = referrerQuery;
            return this;
        }

        public Builder duration(Integer duration) {
            visit.duration = duration;
            return this;
        }

        public Builder actions(Integer actions) {
            visit.actions = actions;
            return this;
        }

        public Builder isFirstVisit(Boolean isFirstVisit) {
            visit.isFirstVisit = isFirstVisit;
            return this;
        }

        public Builder os(String os) {
            visit.os = os;
            return this;
        }

        public Builder browser(String browser) {
            visit.browser = browser;
            return this;
        }

        public Builder device(String device) {
            visit.device = device;
            return this;
        }

        public Builder screen(String screen) {
            visit.screen = screen;
            return this;
        }

        public Visit build() {
            return visit;
        }
    }
}

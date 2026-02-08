package com.filter.dsl.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Event (action) record containing event name, timestamp, parameters, and metadata.
 */
public class Event {
    
    // Cached parsed timestamp for performance optimization
    // transient = not serialized to JSON
    private transient Instant cachedTimestamp;
    @JsonProperty("uuid")
    private String uuid;
    
    @JsonProperty("is_first_in_visit")
    private Boolean isFirstInVisit;
    
    @JsonProperty("is_last_in_visit")
    private Boolean isLastInVisit;
    
    @JsonProperty("is_first_event")
    private Boolean isFirstEvent;
    
    @JsonProperty("is_current")
    private Boolean isCurrent;
    
    @JsonProperty("event_name")
    private String eventName;
    
    @JsonProperty("integration")
    private String integration;
    
    @JsonProperty("app")
    private String app;
    
    @JsonProperty("platform")
    private String platform;
    
    @JsonProperty("is_https")
    private Boolean isHttps;
    
    @JsonProperty("event_type")
    private String eventType;
    
    @JsonProperty("duration")
    private Integer duration;
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    @JsonProperty("triggerable")
    private Boolean triggerable;
    
    @JsonProperty("parameters")
    private Map<String, Object> parameters;

    // Constructors
    public Event() {
        this.parameters = new HashMap<>();
    }

    // Getters and Setters
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Boolean getIsFirstInVisit() {
        return isFirstInVisit;
    }

    public void setIsFirstInVisit(Boolean isFirstInVisit) {
        this.isFirstInVisit = isFirstInVisit;
    }

    public Boolean getIsLastInVisit() {
        return isLastInVisit;
    }

    public void setIsLastInVisit(Boolean isLastInVisit) {
        this.isLastInVisit = isLastInVisit;
    }

    public Boolean getIsFirstEvent() {
        return isFirstEvent;
    }

    public void setIsFirstEvent(Boolean isFirstEvent) {
        this.isFirstEvent = isFirstEvent;
    }

    public Boolean getIsCurrent() {
        return isCurrent;
    }

    public void setIsCurrent(Boolean isCurrent) {
        this.isCurrent = isCurrent;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getIntegration() {
        return integration;
    }

    public void setIntegration(String integration) {
        this.integration = integration;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Boolean getIsHttps() {
        return isHttps;
    }

    public void setIsHttps(Boolean isHttps) {
        this.isHttps = isHttps;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        // Clear cached timestamp when timestamp string changes
        this.cachedTimestamp = null;
    }
    
    /**
     * Get the timestamp as an Instant object.
     * This method caches the parsed Instant to avoid repeated parsing,
     * which significantly improves performance when filtering by date.
     * 
     * @return The parsed Instant, or null if timestamp is null or invalid
     */
    @com.fasterxml.jackson.annotation.JsonIgnore
    public Instant getTimestampAsInstant() {
        if (cachedTimestamp == null && timestamp != null && !timestamp.isEmpty()) {
            try {
                cachedTimestamp = Instant.parse(timestamp);
            } catch (DateTimeParseException e) {
                // Return null for invalid timestamps
                // Don't cache the null to allow retry if timestamp is fixed
                return null;
            }
        }
        return cachedTimestamp;
    }

    public Boolean getTriggerable() {
        return triggerable;
    }

    public void setTriggerable(Boolean triggerable) {
        this.triggerable = triggerable;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Event event = new Event();

        public Builder uuid(String uuid) {
            event.uuid = uuid;
            return this;
        }

        public Builder isFirstInVisit(Boolean isFirstInVisit) {
            event.isFirstInVisit = isFirstInVisit;
            return this;
        }

        public Builder isLastInVisit(Boolean isLastInVisit) {
            event.isLastInVisit = isLastInVisit;
            return this;
        }

        public Builder isFirstEvent(Boolean isFirstEvent) {
            event.isFirstEvent = isFirstEvent;
            return this;
        }

        public Builder isCurrent(Boolean isCurrent) {
            event.isCurrent = isCurrent;
            return this;
        }

        public Builder eventName(String eventName) {
            event.eventName = eventName;
            return this;
        }

        public Builder integration(String integration) {
            event.integration = integration;
            return this;
        }

        public Builder app(String app) {
            event.app = app;
            return this;
        }

        public Builder platform(String platform) {
            event.platform = platform;
            return this;
        }

        public Builder isHttps(Boolean isHttps) {
            event.isHttps = isHttps;
            return this;
        }

        public Builder eventType(String eventType) {
            event.eventType = eventType;
            return this;
        }

        public Builder duration(Integer duration) {
            event.duration = duration;
            return this;
        }

        public Builder timestamp(String timestamp) {
            event.timestamp = timestamp;
            return this;
        }

        public Builder triggerable(Boolean triggerable) {
            event.triggerable = triggerable;
            return this;
        }

        public Builder parameters(Map<String, Object> parameters) {
            event.parameters = parameters;
            return this;
        }

        public Builder parameter(String key, Object value) {
            event.parameters.put(key, value);
            return this;
        }

        public Event build() {
            return event;
        }
    }
}

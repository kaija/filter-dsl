package com.filter.dsl.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Complete user data structure containing profile, visits, and events.
 * This is the main data structure passed to DSL expressions for evaluation.
 */
public class UserData {
    @JsonProperty("profile")
    private Profile profile;
    
    @JsonProperty("visits")
    private Map<String, Visit> visits;
    
    @JsonProperty("events")
    private List<Event> events;

    // Constructors
    public UserData() {
        this.visits = new HashMap<>();
        this.events = new ArrayList<>();
    }

    // Getters and Setters
    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Map<String, Visit> getVisits() {
        return visits;
    }

    public void setVisits(Map<String, Visit> visits) {
        this.visits = visits;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final UserData userData = new UserData();

        public Builder profile(Profile profile) {
            userData.profile = profile;
            return this;
        }

        public Builder visits(Map<String, Visit> visits) {
            userData.visits = visits;
            return this;
        }

        public Builder visit(String uuid, Visit visit) {
            userData.visits.put(uuid, visit);
            return this;
        }

        public Builder events(List<Event> events) {
            userData.events = events;
            return this;
        }

        public Builder event(Event event) {
            userData.events.add(event);
            return this;
        }

        public UserData build() {
            return userData;
        }
    }
}

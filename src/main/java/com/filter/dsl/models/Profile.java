package com.filter.dsl.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * User profile containing demographic information.
 * Device attributes (os, browser, device, screen) are now in Visit model.
 */
public class Profile {
    @JsonProperty("country")
    private String country;
    
    @JsonProperty("city")
    private String city;
    
    @JsonProperty("language")
    private String language;
    
    @JsonProperty("continent")
    private String continent;
    
    @JsonProperty("timezone")
    private String timezone;
    
    @JsonProperty("uuid")
    private String uuid;

    // Constructors
    public Profile() {}

    // Getters and Setters
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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
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

    // Builder pattern
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

        public Builder language(String language) {
            profile.language = language;
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

        public Profile build() {
            return profile;
        }
    }
}

package com.filter.dsl.models;

import java.time.Instant;

/**
 * Represents a time range for filtering events.
 * Time ranges are defined relative to a reference time (typically "now").
 *
 * Example: FROM(30, D) TO(0, D) means "from 30 days ago to now"
 */
public class TimeRange {
    private final Integer fromValue;
    private final TimeUnit fromUnit;
    private final Integer toValue;
    private final TimeUnit toUnit;
    private final Instant referenceTime;

    public TimeRange(Integer fromValue, TimeUnit fromUnit,
                     Integer toValue, TimeUnit toUnit,
                     Instant referenceTime) {
        this.fromValue = fromValue;
        this.fromUnit = fromUnit;
        this.toValue = toValue;
        this.toUnit = toUnit;
        this.referenceTime = referenceTime != null ? referenceTime : Instant.now();
    }

    /**
     * Get the start time of the range.
     *
     * @return The start instant
     */
    public Instant getStartTime() {
        if (fromValue == null || fromUnit == null) {
            return Instant.MIN;
        }
        return subtractTimeUnit(referenceTime, fromValue, fromUnit);
    }

    /**
     * Get the end time of the range.
     *
     * @return The end instant
     */
    public Instant getEndTime() {
        if (toValue == null || toUnit == null) {
            return referenceTime;
        }
        return subtractTimeUnit(referenceTime, toValue, toUnit);
    }

    /**
     * Subtract a time unit from an instant, handling units that Instant doesn't support directly.
     *
     * @param instant The instant to subtract from
     * @param value The amount to subtract
     * @param unit The time unit
     * @return The resulting instant
     */
    private Instant subtractTimeUnit(Instant instant, long value, TimeUnit unit) {
        switch (unit) {
            case W:
                // Weeks: convert to days (7 days per week)
                return instant.minus(value * 7, java.time.temporal.ChronoUnit.DAYS);
            case MO:
                // Months: approximate as 30 days
                return instant.minus(value * 30, java.time.temporal.ChronoUnit.DAYS);
            case Y:
                // Years: approximate as 365 days
                return instant.minus(value * 365, java.time.temporal.ChronoUnit.DAYS);
            default:
                // For D, H, M - use the ChronoUnit directly
                return instant.minus(value, unit.getChronoUnit());
        }
    }

    /**
     * Check if a timestamp falls within this time range.
     *
     * @param timestamp The timestamp to check
     * @return true if the timestamp is within the range (inclusive)
     */
    public boolean contains(Instant timestamp) {
        if (timestamp == null) {
            return false;
        }
        Instant start = getStartTime();
        Instant end = getEndTime();
        return !timestamp.isBefore(start) && !timestamp.isAfter(end);
    }

    public Integer getFromValue() {
        return fromValue;
    }

    public TimeUnit getFromUnit() {
        return fromUnit;
    }

    public Integer getToValue() {
        return toValue;
    }

    public TimeUnit getToUnit() {
        return toUnit;
    }

    public Instant getReferenceTime() {
        return referenceTime;
    }

    @Override
    public String toString() {
        return "TimeRange{" +
                "from=" + fromValue + " " + fromUnit +
                ", to=" + toValue + " " + toUnit +
                ", reference=" + referenceTime +
                '}';
    }
}

package com.filter.dsl.models;

import java.time.temporal.ChronoUnit;

/**
 * Time units supported by the DSL for time range operations.
 */
public enum TimeUnit {
    D(ChronoUnit.DAYS),      // Days
    H(ChronoUnit.HOURS),     // Hours
    M(ChronoUnit.MINUTES),   // Minutes
    W(ChronoUnit.WEEKS),     // Weeks
    MO(ChronoUnit.MONTHS),   // Months
    Y(ChronoUnit.YEARS);     // Years

    private final ChronoUnit chronoUnit;

    TimeUnit(ChronoUnit chronoUnit) {
        this.chronoUnit = chronoUnit;
    }

    public ChronoUnit getChronoUnit() {
        return chronoUnit;
    }

    /**
     * Parse a time unit string to TimeUnit enum.
     *
     * @param unit The unit string (case-insensitive)
     * @return The TimeUnit enum value
     * @throws IllegalArgumentException if unit is not recognized
     */
    public static TimeUnit parse(String unit) {
        if (unit == null) {
            throw new IllegalArgumentException("Time unit cannot be null");
        }

        String upperUnit = unit.toUpperCase();
        try {
            return TimeUnit.valueOf(upperUnit);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Unknown time unit: " + unit + ". Valid units: D, H, M, W, MO, Y"
            );
        }
    }
}

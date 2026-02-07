package com.filter.dsl.models;

/**
 * Represents a single bucket range for segmentation.
 * A range defines minimum and maximum values (with inclusive/exclusive boundaries)
 * and a label for values that fall within the range.
 */
public class BucketRange {
    private final Double minValue;
    private final Double maxValue;
    private final boolean minInclusive;
    private final boolean maxInclusive;
    private final String label;

    public BucketRange(Double minValue, Double maxValue, 
                       boolean minInclusive, boolean maxInclusive, 
                       String label) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
        this.label = label;
    }

    /**
     * Check if a value falls within this bucket range.
     * 
     * @param value The value to check
     * @return true if the value is within the range boundaries
     */
    public boolean contains(Double value) {
        if (value == null) {
            return false;
        }

        boolean aboveMin = minValue == null || 
            (minInclusive ? value >= minValue : value > minValue);
        boolean belowMax = maxValue == null || 
            (maxInclusive ? value <= maxValue : value < maxValue);

        return aboveMin && belowMax;
    }

    public Double getMinValue() {
        return minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public boolean isMinInclusive() {
        return minInclusive;
    }

    public boolean isMaxInclusive() {
        return maxInclusive;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        String minBracket = minInclusive ? "[" : "(";
        String maxBracket = maxInclusive ? "]" : ")";
        return minBracket + minValue + ", " + maxValue + maxBracket + " -> " + label;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Double minValue;
        private Double maxValue;
        private boolean minInclusive = true;
        private boolean maxInclusive = false;
        private String label;

        public Builder minValue(Double minValue) {
            this.minValue = minValue;
            return this;
        }

        public Builder maxValue(Double maxValue) {
            this.maxValue = maxValue;
            return this;
        }

        public Builder minInclusive(boolean minInclusive) {
            this.minInclusive = minInclusive;
            return this;
        }

        public Builder maxInclusive(boolean maxInclusive) {
            this.maxInclusive = maxInclusive;
            return this;
        }

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public BucketRange build() {
            return new BucketRange(minValue, maxValue, minInclusive, maxInclusive, label);
        }
    }
}

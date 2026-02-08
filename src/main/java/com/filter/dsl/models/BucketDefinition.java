package com.filter.dsl.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Definition of buckets for segmentation.
 * Contains a list of bucket ranges and an optional default label
 * for values that don't match any range.
 */
public class BucketDefinition {
    private final List<BucketRange> ranges;
    private final String defaultLabel;

    public BucketDefinition(List<BucketRange> ranges, String defaultLabel) {
        this.ranges = ranges != null ? ranges : new ArrayList<>();
        this.defaultLabel = defaultLabel;
    }

    /**
     * Find the bucket label for a given value.
     * Returns the label of the first matching range, or the default label if no match.
     *
     * @param value The value to bucket
     * @return The bucket label, or default label if no match
     */
    public String getBucketLabel(Double value) {
        for (BucketRange range : ranges) {
            if (range.contains(value)) {
                return range.getLabel();
            }
        }
        return defaultLabel;
    }

    public List<BucketRange> getRanges() {
        return ranges;
    }

    public String getDefaultLabel() {
        return defaultLabel;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<BucketRange> ranges = new ArrayList<>();
        private String defaultLabel = "Other";

        public Builder range(BucketRange range) {
            ranges.add(range);
            return this;
        }

        public Builder range(Double minValue, Double maxValue, String label) {
            ranges.add(BucketRange.builder()
                .minValue(minValue)
                .maxValue(maxValue)
                .label(label)
                .build());
            return this;
        }

        public Builder defaultLabel(String defaultLabel) {
            this.defaultLabel = defaultLabel;
            return this;
        }

        public BucketDefinition build() {
            return new BucketDefinition(ranges, defaultLabel);
        }
    }
}

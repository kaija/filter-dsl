package com.filter.dsl.performance;

/**
 * Container for performance metrics of a single test.
 */
public class PerformanceMetrics {
    public String functionName;
    public String datasetSize;
    public String expression;
    public int iterations;
    public double totalTimeMs;
    public double avgTimeMs;
    public double minTimeMs;
    public double maxTimeMs;
    public double p95TimeMs;
    public double p99TimeMs;
    public double throughput; // operations per second
    public double memoryUsedMB;
    public double datasetSizeMB;
    
    /**
     * Calculate a complexity score based on execution time and memory usage.
     * Higher score = more complex/expensive operation.
     */
    public double getComplexityScore() {
        // Normalize by dataset size and combine time + memory factors
        double timeScore = avgTimeMs / Math.max(1.0, datasetSizeMB);
        double memoryScore = memoryUsedMB / Math.max(1.0, datasetSizeMB);
        return (timeScore * 0.7) + (memoryScore * 0.3);
    }
    
    /**
     * Get a human-readable complexity rating.
     */
    public String getComplexityRating() {
        double score = getComplexityScore();
        if (score < 1.0) return "LOW";
        if (score < 5.0) return "MEDIUM";
        if (score < 20.0) return "HIGH";
        return "VERY HIGH";
    }
}

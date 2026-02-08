package com.filter.dsl.performance;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates comprehensive performance reports from test metrics.
 * 
 * Reports include:
 * - Summary statistics for each function
 * - Ranking by execution time
 * - Ranking by memory usage
 * - Complexity analysis
 * - Recommendations for optimization
 */
public class PerformanceReporter {
    
    private final List<PerformanceMetrics> allMetrics = new ArrayList<>();
    private final String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    
    /**
     * Add metrics from a test run.
     */
    public void addMetrics(PerformanceMetrics metrics) {
        allMetrics.add(metrics);
    }
    
    /**
     * Generate a comprehensive performance report.
     */
    public void generateReport() throws IOException {
        String filename = "performance_report_" + timestamp + ".txt";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writeHeader(writer);
            writeExecutiveSummary(writer);
            writeDetailedResults(writer);
            writeComplexityAnalysis(writer);
            writeScalabilityAnalysis(writer);
            writeRecommendations(writer);
            writeFooter(writer);
        }
        
        // Also generate CSV for easy analysis
        generateCSV();
        
        System.out.println("\nPerformance report generated: " + filename);
    }
    
    private void writeHeader(PrintWriter writer) {
        writer.println("=".repeat(100));
        writer.println("DSL FUNCTION PERFORMANCE ANALYSIS REPORT");
        writer.println("=".repeat(100));
        writer.println("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        writer.println("Total Tests: " + allMetrics.size());
        writer.println("=".repeat(100));
        writer.println();
    }
    
    private void writeExecutiveSummary(PrintWriter writer) {
        writer.println("EXECUTIVE SUMMARY");
        writer.println("-".repeat(100));
        writer.println();
        
        // Group by function name
        Map<String, List<PerformanceMetrics>> byFunction = allMetrics.stream()
            .collect(Collectors.groupingBy(m -> m.functionName));
        
        // Find slowest functions
        writer.println("TOP 10 SLOWEST FUNCTIONS (by average execution time):");
        writer.println();
        
        List<Map.Entry<String, Double>> slowest = byFunction.entrySet().stream()
            .map(e -> new AbstractMap.SimpleEntry<>(
                e.getKey(),
                e.getValue().stream().mapToDouble(m -> m.avgTimeMs).average().orElse(0)
            ))
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(10)
            .collect(Collectors.toList());
        
        int rank = 1;
        for (Map.Entry<String, Double> entry : slowest) {
            writer.printf("%2d. %-20s  %10.3f ms%n", rank++, entry.getKey(), entry.getValue());
        }
        
        writer.println();
        
        // Find most memory-intensive functions
        writer.println("TOP 10 MEMORY-INTENSIVE FUNCTIONS:");
        writer.println();
        
        List<Map.Entry<String, Double>> memoryIntensive = byFunction.entrySet().stream()
            .map(e -> new AbstractMap.SimpleEntry<>(
                e.getKey(),
                e.getValue().stream().mapToDouble(m -> m.memoryUsedMB).average().orElse(0)
            ))
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(10)
            .collect(Collectors.toList());
        
        rank = 1;
        for (Map.Entry<String, Double> entry : memoryIntensive) {
            writer.printf("%2d. %-20s  %10.2f MB%n", rank++, entry.getKey(), entry.getValue());
        }
        
        writer.println();
        writer.println();
    }
    
    private void writeDetailedResults(PrintWriter writer) {
        writer.println("DETAILED RESULTS");
        writer.println("-".repeat(100));
        writer.println();
        
        // Group by function
        Map<String, List<PerformanceMetrics>> byFunction = allMetrics.stream()
            .collect(Collectors.groupingBy(m -> m.functionName));
        
        for (Map.Entry<String, List<PerformanceMetrics>> entry : byFunction.entrySet()) {
            String functionName = entry.getKey();
            List<PerformanceMetrics> metrics = entry.getValue();
            
            writer.println("Function: " + functionName);
            writer.println("-".repeat(100));
            writer.println();
            
            writer.printf("%-20s %12s %12s %12s %12s %12s %15s %12s%n",
                "Dataset", "Avg (ms)", "P95 (ms)", "P99 (ms)", "Min (ms)", "Max (ms)", "Throughput(ops/s)", "Memory (MB)");
            writer.println("-".repeat(100));
            
            for (PerformanceMetrics m : metrics) {
                writer.printf("%-20s %12.3f %12.3f %12.3f %12.3f %12.3f %15.2f %12.2f%n",
                    m.datasetSize, m.avgTimeMs, m.p95TimeMs, m.p99TimeMs, 
                    m.minTimeMs, m.maxTimeMs, m.throughput, m.memoryUsedMB);
            }
            
            writer.println();
            
            // Calculate scalability factor
            if (metrics.size() >= 2) {
                PerformanceMetrics smallest = metrics.get(0);
                PerformanceMetrics largest = metrics.get(metrics.size() - 1);
                
                double dataSizeRatio = largest.datasetSizeMB / smallest.datasetSizeMB;
                double timeRatio = largest.avgTimeMs / smallest.avgTimeMs;
                double scalabilityFactor = timeRatio / dataSizeRatio;
                
                writer.printf("Scalability Factor: %.2f ", scalabilityFactor);
                if (scalabilityFactor < 1.2) {
                    writer.println("(EXCELLENT - Near linear scaling)");
                } else if (scalabilityFactor < 2.0) {
                    writer.println("(GOOD - Acceptable scaling)");
                } else if (scalabilityFactor < 5.0) {
                    writer.println("(FAIR - Some performance degradation)");
                } else {
                    writer.println("(POOR - Significant performance degradation)");
                }
            }
            
            writer.println();
            writer.println();
        }
    }
    
    private void writeComplexityAnalysis(PrintWriter writer) {
        writer.println("COMPLEXITY ANALYSIS");
        writer.println("-".repeat(100));
        writer.println();
        
        writer.println("Complexity Score = (Avg Time / Dataset Size) * 0.7 + (Memory / Dataset Size) * 0.3");
        writer.println();
        
        List<PerformanceMetrics> sortedByComplexity = allMetrics.stream()
            .sorted(Comparator.comparingDouble(PerformanceMetrics::getComplexityScore).reversed())
            .collect(Collectors.toList());
        
        writer.printf("%-20s %-20s %15s %15s %15s%n",
            "Function", "Dataset", "Complexity Score", "Rating", "Avg Time (ms)");
        writer.println("-".repeat(100));
        
        for (PerformanceMetrics m : sortedByComplexity) {
            writer.printf("%-20s %-20s %15.2f %15s %15.3f%n",
                m.functionName, m.datasetSize, m.getComplexityScore(), 
                m.getComplexityRating(), m.avgTimeMs);
        }
        
        writer.println();
        writer.println();
    }
    
    private void writeScalabilityAnalysis(PrintWriter writer) {
        writer.println("SCALABILITY ANALYSIS");
        writer.println("-".repeat(100));
        writer.println();
        
        // Group by function and analyze how performance scales with data size
        Map<String, List<PerformanceMetrics>> byFunction = allMetrics.stream()
            .collect(Collectors.groupingBy(m -> m.functionName));
        
        writer.printf("%-20s %15s %15s %20s%n",
            "Function", "10x Data", "100x Data", "Scalability");
        writer.println("-".repeat(100));
        
        for (Map.Entry<String, List<PerformanceMetrics>> entry : byFunction.entrySet()) {
            String functionName = entry.getKey();
            List<PerformanceMetrics> metrics = entry.getValue().stream()
                .sorted(Comparator.comparingDouble(m -> m.datasetSizeMB))
                .collect(Collectors.toList());
            
            if (metrics.size() < 2) continue;
            
            PerformanceMetrics base = metrics.get(0);
            String time10x = "N/A";
            String time100x = "N/A";
            String scalability = "N/A";
            
            // Find 10x data point
            for (PerformanceMetrics m : metrics) {
                if (m.datasetSizeMB >= base.datasetSizeMB * 8) {
                    double ratio = m.avgTimeMs / base.avgTimeMs;
                    time10x = String.format("%.1fx", ratio);
                    break;
                }
            }
            
            // Find 100x data point
            for (PerformanceMetrics m : metrics) {
                if (m.datasetSizeMB >= base.datasetSizeMB * 80) {
                    double ratio = m.avgTimeMs / base.avgTimeMs;
                    time100x = String.format("%.1fx", ratio);
                    break;
                }
            }
            
            // Determine scalability rating
            PerformanceMetrics largest = metrics.get(metrics.size() - 1);
            double dataSizeRatio = largest.datasetSizeMB / base.datasetSizeMB;
            double timeRatio = largest.avgTimeMs / base.avgTimeMs;
            double factor = timeRatio / dataSizeRatio;
            
            if (factor < 1.2) scalability = "O(n) - Linear";
            else if (factor < 2.0) scalability = "O(n log n)";
            else if (factor < 5.0) scalability = "O(n²) - Quadratic";
            else scalability = "O(n³+) - Cubic+";
            
            writer.printf("%-20s %15s %15s %20s%n",
                functionName, time10x, time100x, scalability);
        }
        
        writer.println();
        writer.println();
    }
    
    private void writeRecommendations(PrintWriter writer) {
        writer.println("OPTIMIZATION RECOMMENDATIONS");
        writer.println("-".repeat(100));
        writer.println();
        
        // Find functions with poor scalability
        Map<String, List<PerformanceMetrics>> byFunction = allMetrics.stream()
            .collect(Collectors.groupingBy(m -> m.functionName));
        
        List<String> recommendations = new ArrayList<>();
        
        for (Map.Entry<String, List<PerformanceMetrics>> entry : byFunction.entrySet()) {
            String functionName = entry.getKey();
            List<PerformanceMetrics> metrics = entry.getValue().stream()
                .sorted(Comparator.comparingDouble(m -> m.datasetSizeMB))
                .collect(Collectors.toList());
            
            if (metrics.size() < 2) continue;
            
            PerformanceMetrics smallest = metrics.get(0);
            PerformanceMetrics largest = metrics.get(metrics.size() - 1);
            
            double dataSizeRatio = largest.datasetSizeMB / smallest.datasetSizeMB;
            double timeRatio = largest.avgTimeMs / smallest.avgTimeMs;
            double scalabilityFactor = timeRatio / dataSizeRatio;
            
            // High complexity score
            double avgComplexity = metrics.stream()
                .mapToDouble(PerformanceMetrics::getComplexityScore)
                .average().orElse(0);
            
            if (avgComplexity > 10.0) {
                recommendations.add(String.format(
                    "⚠ %s: HIGH COMPLEXITY (score: %.2f) - Consider algorithm optimization or caching",
                    functionName, avgComplexity));
            }
            
            // Poor scalability
            if (scalabilityFactor > 3.0) {
                recommendations.add(String.format(
                    "⚠ %s: POOR SCALABILITY (factor: %.2f) - Performance degrades significantly with data size",
                    functionName, scalabilityFactor));
            }
            
            // High memory usage
            double avgMemory = metrics.stream()
                .mapToDouble(m -> m.memoryUsedMB)
                .average().orElse(0);
            
            if (avgMemory > 50.0) {
                recommendations.add(String.format(
                    "⚠ %s: HIGH MEMORY USAGE (%.2f MB avg) - Consider streaming or chunking data",
                    functionName, avgMemory));
            }
            
            // Slow absolute performance
            double avgTime = metrics.stream()
                .mapToDouble(m -> m.avgTimeMs)
                .average().orElse(0);
            
            if (avgTime > 100.0) {
                recommendations.add(String.format(
                    "⚠ %s: SLOW EXECUTION (%.2f ms avg) - Consider performance profiling",
                    functionName, avgTime));
            }
        }
        
        if (recommendations.isEmpty()) {
            writer.println("✓ All functions show acceptable performance characteristics.");
        } else {
            writer.println("Priority optimizations:");
            writer.println();
            for (int i = 0; i < recommendations.size(); i++) {
                writer.printf("%2d. %s%n", i + 1, recommendations.get(i));
            }
        }
        
        writer.println();
        writer.println("General Recommendations:");
        writer.println("  • Enable expression caching for repeated evaluations");
        writer.println("  • Use batch evaluation for multiple users with same expression");
        writer.println("  • Consider data preprocessing for frequently accessed fields");
        writer.println("  • Monitor memory usage in production environments");
        writer.println("  • Profile specific slow functions with JProfiler or YourKit");
        
        writer.println();
        writer.println();
    }
    
    private void writeFooter(PrintWriter writer) {
        writer.println("=".repeat(100));
        writer.println("END OF REPORT");
        writer.println("=".repeat(100));
    }
    
    /**
     * Generate a CSV file for easy import into spreadsheet tools.
     */
    private void generateCSV() throws IOException {
        String filename = "performance_data_" + timestamp + ".csv";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Header
            writer.println("Function,Dataset,Iterations,Avg_Time_ms,P95_Time_ms,P99_Time_ms," +
                "Min_Time_ms,Max_Time_ms,Throughput_ops_per_sec,Memory_MB,Dataset_Size_MB," +
                "Complexity_Score,Complexity_Rating");
            
            // Data
            for (PerformanceMetrics m : allMetrics) {
                writer.printf("%s,%s,%d,%.3f,%.3f,%.3f,%.3f,%.3f,%.2f,%.2f,%.2f,%.2f,%s%n",
                    m.functionName, m.datasetSize, m.iterations,
                    m.avgTimeMs, m.p95TimeMs, m.p99TimeMs, m.minTimeMs, m.maxTimeMs,
                    m.throughput, m.memoryUsedMB, m.datasetSizeMB,
                    m.getComplexityScore(), m.getComplexityRating());
            }
        }
        
        System.out.println("Performance data CSV generated: " + filename);
    }
}

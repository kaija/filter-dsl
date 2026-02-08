# DSL Performance Testing Suite

Comprehensive performance testing framework for analyzing the speed and compute usage of all DSL functions with datasets ranging from 1KB to 100MB.

## Overview

This performance testing suite provides:

- **Systematic benchmarking** of all DSL functions across multiple data sizes
- **Detailed metrics** including execution time, memory usage, throughput, and scalability
- **Complexity analysis** to identify compute-intensive functions
- **Comprehensive reports** with optimization recommendations

## Test Suites

### 1. PerformanceTestSuite
Basic performance tests for key functions with different dataset sizes:
- Small (1KB): ~10 events
- Medium (1MB): ~10,000 events  
- Large (10MB): ~100,000 events
- Extra Large (100MB): ~1,000,000 events

### 2. ComprehensiveFunctionBenchmark
Exhaustive testing of ALL DSL functions:
- Aggregation functions (COUNT, SUM, AVG, MIN, MAX, UNIQUE)
- Comparison functions (EQ, GT, LT, GTE, LTE, NEQ)
- Logical functions (AND, OR, NOT)
- Math functions (ADD, SUBTRACT, MULTIPLY, DIVIDE, MOD, ABS, ROUND, CEIL, FLOOR)
- Date/Time functions (NOW, DATE_DIFF, IN_RECENT_DAYS, WEEKDAY, MONTH, YEAR)
- Filtering functions (WHERE, BY, IF)
- String functions (CONTAINS, STARTS_WITH, ENDS_WITH, TO_UPPER, TO_LOWER)
- Complex nested expressions

## Running the Tests

### Run All Performance Tests
```bash
mvn test -Dtest=PerformanceTestSuite,ComprehensiveFunctionBenchmark
```

### Run Specific Test Suite
```bash
# Basic performance tests
mvn test -Dtest=PerformanceTestSuite

# Comprehensive benchmark
mvn test -Dtest=ComprehensiveFunctionBenchmark
```

### Run with Custom JVM Options
```bash
# Increase heap size for large datasets
mvn test -Dtest=ComprehensiveFunctionBenchmark -DargLine="-Xmx4g -Xms2g"

# Enable GC logging
mvn test -Dtest=PerformanceTestSuite -DargLine="-Xlog:gc*:file=gc.log"
```

## Generated Reports

After running tests, the following files are generated:

### 1. Performance Report (TXT)
`performance_report_YYYYMMDD_HHMMSS.txt`

Contains:
- **Executive Summary**: Top 10 slowest and most memory-intensive functions
- **Detailed Results**: Complete metrics for each function and dataset size
- **Complexity Analysis**: Complexity scores and ratings
- **Scalability Analysis**: How functions scale with data size (O(n), O(n²), etc.)
- **Optimization Recommendations**: Prioritized list of functions needing optimization

### 2. Performance Data (CSV)
`performance_data_YYYYMMDD_HHMMSS.csv`

Spreadsheet-friendly format with all metrics for further analysis in Excel, Google Sheets, or data visualization tools.

## Metrics Explained

### Execution Time Metrics
- **Avg Time**: Average execution time across all iterations
- **P95 Time**: 95th percentile (95% of executions are faster)
- **P99 Time**: 99th percentile (99% of executions are faster)
- **Min/Max Time**: Fastest and slowest execution times
- **Throughput**: Operations per second

### Memory Metrics
- **Memory Used**: Heap memory consumed during test execution
- **Dataset Size**: Estimated size of input data

### Complexity Score
Calculated as: `(Avg Time / Dataset Size) × 0.7 + (Memory / Dataset Size) × 0.3`

**Ratings:**
- **LOW** (< 1.0): Efficient, minimal compute usage
- **MEDIUM** (1.0 - 5.0): Acceptable performance
- **HIGH** (5.0 - 20.0): Compute-intensive, consider optimization
- **VERY HIGH** (> 20.0): Very expensive, optimization recommended

### Scalability Factor
Measures how execution time grows relative to data size:
- **< 1.2**: Excellent (near linear O(n))
- **1.2 - 2.0**: Good (O(n log n))
- **2.0 - 5.0**: Fair (O(n²))
- **> 5.0**: Poor (O(n³) or worse)

## Understanding Results

### High Compute Usage Indicators

1. **High Complexity Score**: Function is expensive relative to data size
2. **Poor Scalability Factor**: Performance degrades significantly with larger datasets
3. **High P99 Time**: Inconsistent performance with occasional slow executions
4. **High Memory Usage**: May cause GC pressure in production

### Example Analysis

```
Function: WHERE
Dataset: Large (10MB)
Avg Time: 45.3 ms
P95 Time: 52.1 ms
Complexity Score: 4.2 [MEDIUM]
Scalability: O(n) - Linear
```

**Interpretation**: WHERE function has medium complexity but scales linearly. Acceptable for production use with large datasets.

```
Function: COMPLEX_NESTED
Dataset: Extra Large (100MB)
Avg Time: 523.7 ms
P95 Time: 612.4 ms
Complexity Score: 18.9 [HIGH]
Scalability: O(n²) - Quadratic
```

**Interpretation**: Complex nested expressions show high complexity and quadratic scaling. Consider:
- Breaking into simpler expressions
- Caching intermediate results
- Preprocessing data

## Customizing Tests

### Create Custom Dataset
```java
UserData customData = TestDataGenerator.generateCustomDataset(
    50000,              // event count
    "purchase",         // primary event name
    150.0,              // average amount
    60                  // days back
);
```

### Add Custom Benchmark
```java
@Test
void benchmarkMyFunction() {
    UserData data = TestDataGenerator.generateLargeDataset();
    String expression = "MY_CUSTOM_EXPRESSION";
    
    PerformanceMetrics metrics = measurePerformance(
        "MY_FUNCTION", 
        "Large (10MB)", 
        expression, 
        data, 
        ITERATIONS
    );
    reporter.addMetrics(metrics);
}
```

## Performance Optimization Tips

Based on test results, consider these optimizations:

### 1. For High Complexity Functions
- Profile with JProfiler or YourKit to identify bottlenecks
- Consider algorithm improvements (e.g., use HashSet for lookups)
- Add caching for expensive computations

### 2. For Poor Scalability
- Review algorithm complexity (aim for O(n) or O(n log n))
- Avoid nested loops over large collections
- Use streaming/lazy evaluation where possible

### 3. For High Memory Usage
- Process data in chunks instead of loading all at once
- Use primitive collections (e.g., TIntArrayList) instead of boxed types
- Clear intermediate collections when no longer needed

### 4. General Best Practices
- Enable expression caching: `DSL.builder().enableCaching(true)`
- Use batch evaluation for multiple users: `DSL.evaluateBatch()`
- Preprocess frequently accessed data fields
- Monitor GC activity in production

## Continuous Performance Monitoring

### Integrate into CI/CD
```bash
# Run performance tests and fail if regression detected
mvn test -Dtest=ComprehensiveFunctionBenchmark
./check_performance_regression.sh
```

### Track Metrics Over Time
- Store CSV reports in version control
- Use visualization tools (Grafana, Tableau) to track trends
- Set performance budgets (e.g., "P95 < 100ms for 10MB dataset")

## Troubleshooting

### OutOfMemoryError
Increase heap size:
```bash
mvn test -DargLine="-Xmx8g -Xms4g"
```

### Tests Taking Too Long
Reduce iterations in test classes:
```java
private static final int TEST_ITERATIONS = 100; // Reduce from 1000
```

### Inconsistent Results
- Ensure no other processes are consuming CPU
- Run multiple times and average results
- Increase warmup iterations

## Contributing

When adding new DSL functions:
1. Add performance tests to `ComprehensiveFunctionBenchmark`
2. Run full benchmark suite
3. Document any performance characteristics in function documentation
4. Update this README if new patterns emerge

## Questions?

For questions about performance testing or optimization strategies, please open an issue or contact the development team.

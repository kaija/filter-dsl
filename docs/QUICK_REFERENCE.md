# Quick Reference Guide

Fast reference for common DSL patterns and functions.

## Installation

```xml
<!-- Maven -->
<dependency>
    <groupId>com.example</groupId>
    <artifactId>user-segmentation-dsl</artifactId>
    <version>1.0.0</version>
</dependency>
```

```gradle
// Gradle
implementation 'com.example:user-segmentation-dsl:1.0.0'
```

## Basic Usage

```java
// Evaluate expression
EvaluationResult result = DSL.evaluate(expression, userData);

// Check result
if (result.isSuccess()) {
    Object value = result.getValue();
} else {
    String error = result.getErrorMessage();
}

// Batch evaluation
List<EvaluationResult> results = DSL.evaluateBatch(expression, users);
```

## Common Patterns

### User Filtering

```java
// Users from specific country
"EQ(PROFILE(\"country\"), \"US\")"

// Users with > N events
"GT(COUNT(userData.events), 10)"

// Users with recent activity
"GT(COUNT(WHERE(userData.events, IN_RECENT_DAYS(30))), 0)"

// High-value users
"GT(SUM(PARAM(\"amount\")), 1000)"
```

### Event Filtering

```java
// Purchase events only
"WHERE(userData.events, EQ(EVENT(\"eventName\"), \"purchase\"))"

// Events in time range
"WHERE(userData.events, AND(FROM(30, \"D\"), TO(0, \"D\")))"

// High-value purchases
"WHERE(userData.events, AND(EQ(EVENT(\"eventName\"), \"purchase\"), GT(PARAM(\"amount\"), 100)))"
```

### Aggregations

```java
// Count events
"COUNT(userData.events)"

// Sum amounts
"SUM(PARAM(\"amount\"))"

// Average value
"AVG(PARAM(\"amount\"))"

// Unique event types
"COUNT(UNIQUE(BY(EVENT(\"eventName\"))))"
```

### Date/Time

```java
// Format date
"DATE_FORMAT(ACTION_TIME(), \"yyyy-MM-dd\")"

// Day of week
"WEEKDAY(ACTION_TIME())"

// Recent events
"IN_RECENT_DAYS(30)"

// Recurring events
"IS_RECURRING(\"login\", 3, 90)"
```

### String Operations

```java
// Contains
"CONTAINS(PROFILE(\"city\"), \"New\")"

// Starts with
"STARTS_WITH(PROFILE(\"email\"), \"admin\")"

// Case conversion
"UPPER(PROFILE(\"country\"))"

// Concatenation
"CONCAT(PROFILE(\"firstName\"), \" \", PROFILE(\"lastName\"))"
```

### Math Operations

```java
// Basic arithmetic
"ADD(5, 3)"
"SUBTRACT(10, 3)"
"MULTIPLY(5, 2)"
"DIVIDE(10, 2)"

// Advanced math
"ROUND(3.14159, 2)"
"POW(2, 3)"
"SQRT(16)"
```

### Segmentation

```java
// Bucket by value
"BUCKET(SUM(PARAM(\"amount\")), [[0, 100, \"low\"], [100, 500, \"medium\"], [500, 5000, \"high\"]])"

// Convert units
"CONVERT_UNIT(SUM(PARAM(\"amount\")), \"cents\", \"dollars\")"
```

## Function Categories

### Logical
`AND`, `OR`, `NOT`

### Comparison
`GT`, `LT`, `GTE`, `LTE`, `EQ`, `NEQ`

### Aggregation
`COUNT`, `SUM`, `AVG`, `MIN`, `MAX`, `UNIQUE`

### Math
`ADD`, `SUBTRACT`, `MULTIPLY`, `DIVIDE`, `MOD`, `ABS`, `ROUND`, `CEIL`, `FLOOR`, `POW`, `SQRT`, `LOG`, `EXP`

### Date/Time
`ACTION_TIME`, `DATE_FORMAT`, `DATE_DIFF`, `FROM`, `TO`, `NOW`, `WEEKDAY`, `IN_RECENT_DAYS`, `IS_RECURRING`, `DAY_OF_MONTH`, `MONTH`, `YEAR`

### Data Access
`PROFILE`, `EVENT`, `PARAM`

### Filtering
`IF`, `WHERE`, `BY`

### String
`CONTAINS`, `STARTS_WITH`, `ENDS_WITH`, `REGEX_MATCH`, `UPPER`, `LOWER`, `TRIM`, `SUBSTRING`, `REPLACE`, `LENGTH`, `CONCAT`, `SPLIT`

### Conversion
`TO_NUMBER`, `TO_STRING`, `TO_BOOLEAN`, `CONVERT_UNIT`

### Segmentation
`BUCKET`

## Performance Tips

1. **Enable caching** (default)
2. **Use batch evaluation** for multiple users
3. **Filter early** in expressions
4. **Pre-warm cache** on startup
5. **Monitor cache size**

```java
// Check cache
int size = DSL.getCacheSize();

// Clear cache
DSL.clearCache();
```

## Error Handling

```java
EvaluationResult result = DSL.evaluate(expression, userData);

if (result.isSuccess()) {
    // Success
    Object value = result.getValue();
    long time = result.getEvaluationTimeMs();
} else {
    // Error
    String error = result.getErrorMessage();
    logger.error("Evaluation failed: {}", error);
}
```

## Thread Safety

✅ All components are thread-safe
✅ Default instance is a singleton
✅ Expression cache is concurrent
✅ Safe for parallel processing

```java
// Parallel batch processing
List<Boolean> results = users.parallelStream()
    .map(user -> DSL.evaluate(expression, user))
    .map(result -> result.isSuccess() && (Boolean) result.getValue())
    .collect(Collectors.toList());
```

## Custom Functions

```java
// 1. Create function
public class MyFunction extends DSLFunction {
    @Override
    public String getName() { return "MY_FUNCTION"; }
    
    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("MY_FUNCTION")
            .minArgs(1)
            .maxArgs(1)
            .returnType(ReturnType.STRING)
            .build();
    }
    
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 1);
        String input = toString(args[0], env);
        return new AviatorString("Result: " + input);
    }
}

// 2. Register function
DSL dsl = DSL.builder().enableAutoDiscovery(false).build();
dsl.getRegistry().register(new MyFunction());

// 3. Use function
EvaluationResult result = dsl.evaluateInstance("MY_FUNCTION(\"test\")", userData);
```

## See Also

- [API Documentation](API.md) - Complete reference
- [Function Reference](FUNCTION_REFERENCE.md) - All functions
- [Extension Guide](EXTENSION_GUIDE.md) - Custom functions
- [Performance Guide](PERFORMANCE_GUIDE.md) - Optimization
- [Use Case Examples](USE_CASE_EXAMPLES.md) - Common patterns

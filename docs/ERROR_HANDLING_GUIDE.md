# Error Handling Guide

Comprehensive guide to handling errors in the User Segmentation DSL.

## Table of Contents

1. [Error Types](#error-types)
2. [Error Detection](#error-detection)
3. [Common Errors](#common-errors)
4. [Error Recovery](#error-recovery)
5. [Best Practices](#best-practices)
6. [Production Patterns](#production-patterns)

## Error Types

The DSL categorizes errors into four types:

### 1. Syntax Errors

Invalid expression structure or grammar.

**Causes:**
- Missing parentheses or brackets
- Invalid function names
- Malformed literals
- Unbalanced quotes

**Examples:**
```java
// Missing closing parenthesis
"GT(COUNT(userData.events), 5"
// Error: Syntax error at position 28: Expected closing parenthesis

// Invalid function name (lowercase)
"gt(COUNT(userData.events), 5)"
// Error: Function 'gt' is not defined. Did you mean 'GT'?

// Unbalanced quotes
"EQ(PROFILE("country), "US")"
// Error: Syntax error: Unbalanced quotes
```

### 2. Validation Errors

Semantically incorrect expressions.

**Causes:**
- Undefined function references
- Wrong argument count
- Type mismatches in function signatures

**Examples:**
```java
// Undefined function (typo)
"SUMM(userData.events)"
// Error: Function 'SUMM' is not defined. Did you mean 'SUM'?

// Wrong argument count
"ADD(5)"
// Error: ADD expects 2 arguments, got 1

// Type mismatch
"GT(\"hello\", 5)"
// Error: GT expects numeric arguments, got String and Number
```

### 3. Runtime Errors

Errors during expression evaluation.

**Causes:**
- Division by zero
- Null pointer access
- Invalid date formats
- Collection operations on non-collections
- Negative square roots

**Examples:**
```java
// Division by zero
"DIVIDE(10, 0)"
// Error: Runtime error: Division by zero

// Invalid date format
"DATE_FORMAT(\"invalid-date\", \"yyyy-MM-dd\")"
// Error: Invalid timestamp format: invalid-date

// Negative square root
"SQRT(-1)"
// Error: SQRT requires non-negative value, got: -1
```

### 4. Data Errors

Issues with input user data.

**Causes:**
- Missing required fields
- Invalid data types
- Malformed timestamps
- Null values in critical fields

**Examples:**
```java
// Missing field
"PROFILE(\"nonexistent_field\")"
// Returns: null (not an error, but may cause issues downstream)

// Null event
// If userData.events is null
"COUNT(userData.events)"
// Error: Cannot count null collection
```

## Error Detection

### Checking for Errors

Always check `isSuccess()` before accessing values:

```java
EvaluationResult result = DSL.evaluate(expression, userData);

if (result.isSuccess()) {
    // Safe to access value
    Object value = result.getValue();
    System.out.println("Result: " + value);
} else {
    // Handle error
    String errorMessage = result.getErrorMessage();
    System.err.println("Error: " + errorMessage);
}
```

### Error Information

`EvaluationResult` provides:

```java
boolean isSuccess()           // true if evaluation succeeded
Object getValue()             // result value (only if successful)
String getErrorMessage()      // error description (only if failed)
long getEvaluationTimeMs()    // evaluation time in milliseconds
```

### Pre-Validation

Validate expressions before evaluation:

```java
DSL dsl = DSL.builder().build();
DSLParser parser = dsl.getParser();

ParseResult parseResult = parser.parse(expression);

if (parseResult.isValid()) {
    // Expression is syntactically valid
    EvaluationResult result = dsl.evaluateInstance(expression, userData);
} else {
    // Expression has syntax errors
    System.err.println("Invalid expression: " + parseResult.getErrorMessage());
    System.err.println("Error at position: " + parseResult.getErrorPosition());
}
```

## Common Errors

### Syntax Errors

#### Missing Parentheses

```java
// ❌ Wrong
"GT(COUNT(userData.events), 5"

// ✅ Correct
"GT(COUNT(userData.events), 5)"
```

#### Case Sensitivity

```java
// ❌ Wrong (lowercase)
"gt(count(userData.events), 5)"

// ✅ Correct (UPPERCASE)
"GT(COUNT(userData.events), 5)"
```

#### Quote Escaping

```java
// ❌ Wrong (unescaped quotes)
"EQ(PROFILE("country"), "US")"

// ✅ Correct (escaped quotes)
"EQ(PROFILE(\"country\"), \"US\")"
```

### Validation Errors

#### Function Typos

```java
// ❌ Wrong
"SUMM(userData.events)"  // Typo: SUMM instead of SUM

// ✅ Correct
"SUM(userData.events)"
```

#### Wrong Argument Count

```java
// ❌ Wrong
"ADD(5)"  // ADD requires 2 arguments

// ✅ Correct
"ADD(5, 3)"
```

#### Type Mismatches

```java
// ❌ Wrong
"ADD(\"hello\", 5)"  // Can't add string and number

// ✅ Correct
"ADD(5, 3)"
```

### Runtime Errors

#### Division by Zero

```java
// ❌ Wrong
"DIVIDE(10, 0)"

// ✅ Correct (check denominator)
"DIVIDE(10, MAX(COUNT(userData.events), 1))"  // Ensures denominator >= 1
```

#### Invalid Dates

```java
// ❌ Wrong
"DATE_FORMAT(\"2024-13-45\", \"yyyy-MM-dd\")"  // Invalid date

// ✅ Correct
"DATE_FORMAT(\"2024-01-15T10:30:00Z\", \"yyyy-MM-dd\")"  // ISO 8601 format
```

#### Null Collections

```java
// ❌ Risky (if events is null)
"COUNT(userData.events)"

// ✅ Safe (handle null in application code)
UserData userData = UserData.builder()
    .profile(profile)
    .events(events != null ? events : Collections.emptyList())
    .build();
```

## Error Recovery

### Graceful Degradation

Provide fallback values when errors occur:

```java
public Object evaluateWithFallback(String expression, UserData userData, Object fallback) {
    EvaluationResult result = DSL.evaluate(expression, userData);
    
    if (result.isSuccess()) {
        return result.getValue();
    } else {
        logger.warn("Evaluation failed, using fallback: {}", result.getErrorMessage());
        return fallback;
    }
}

// Usage
Boolean matches = (Boolean) evaluateWithFallback(expression, userData, false);
```

### Retry Logic

Retry transient errors:

```java
public EvaluationResult evaluateWithRetry(String expression, UserData userData, int maxRetries) {
    int attempts = 0;
    
    while (attempts < maxRetries) {
        try {
            EvaluationResult result = DSL.evaluate(expression, userData);
            
            if (result.isSuccess()) {
                return result;
            }
            
            // Don't retry validation errors (they won't succeed)
            if (result.getErrorMessage().contains("Function") || 
                result.getErrorMessage().contains("Syntax")) {
                return result;
            }
            
            attempts++;
            Thread.sleep(100 * attempts);  // Exponential backoff
            
        } catch (Exception e) {
            logger.error("Evaluation attempt {} failed", attempts + 1, e);
            attempts++;
        }
    }
    
    return EvaluationResult.failure("Max retries exceeded");
}
```

### Circuit Breaker

Protect against cascading failures:

```java
@Service
public class ResilientDSLService {
    
    @CircuitBreaker(name = "dsl", fallbackMethod = "fallbackEvaluate")
    @Retry(name = "dsl", maxAttempts = 3)
    public EvaluationResult evaluate(String expression, UserData userData) {
        return DSL.evaluate(expression, userData);
    }
    
    public EvaluationResult fallbackEvaluate(String expression, UserData userData, Exception e) {
        logger.error("DSL evaluation failed, circuit breaker activated", e);
        return EvaluationResult.failure("Service temporarily unavailable");
    }
}
```

## Best Practices

### 1. Always Check Success

```java
// ❌ Wrong (assumes success)
Boolean result = (Boolean) DSL.evaluate(expression, userData).getValue();

// ✅ Correct (checks success)
EvaluationResult result = DSL.evaluate(expression, userData);
if (result.isSuccess()) {
    Boolean value = (Boolean) result.getValue();
} else {
    // Handle error
}
```

### 2. Log Errors

```java
EvaluationResult result = DSL.evaluate(expression, userData);

if (!result.isSuccess()) {
    logger.error("DSL evaluation failed: expression={}, user={}, error={}", 
                expression, userData.getProfile().getUuid(), result.getErrorMessage());
}
```

### 3. Validate Early

```java
// Validate expressions at startup or configuration time
@PostConstruct
public void validateExpressions() {
    List<String> expressions = loadExpressions();
    DSLParser parser = DSL.builder().build().getParser();
    
    for (String expr : expressions) {
        ParseResult result = parser.parse(expr);
        if (!result.isValid()) {
            logger.error("Invalid expression: {} - {}", expr, result.getErrorMessage());
        }
    }
}
```

### 4. Handle Null Data

```java
// Ensure UserData is never null
public UserData loadUserData(String userId) {
    User user = userRepository.findById(userId).orElse(null);
    
    if (user == null) {
        // Return empty UserData instead of null
        return UserData.builder()
            .profile(Profile.builder().uuid(userId).build())
            .events(Collections.emptyList())
            .build();
    }
    
    return convertToUserData(user);
}
```

### 5. Use Try-Catch for Unexpected Errors

```java
try {
    EvaluationResult result = DSL.evaluate(expression, userData);
    
    if (result.isSuccess()) {
        processResult(result.getValue());
    } else {
        handleError(result.getErrorMessage());
    }
} catch (Exception e) {
    logger.error("Unexpected error during DSL evaluation", e);
    // Handle unexpected errors
}
```

## Production Patterns

### Pattern 1: Comprehensive Error Handling

```java
@Service
public class ProductionDSLService {
    private static final Logger logger = LoggerFactory.getLogger(ProductionDSLService.class);
    
    public Optional<Object> evaluateSafely(String expression, UserData userData) {
        try {
            // Validate expression first
            DSLParser parser = DSL.builder().build().getParser();
            ParseResult parseResult = parser.parse(expression);
            
            if (!parseResult.isValid()) {
                logger.error("Invalid expression: {} - {}", 
                           expression, parseResult.getErrorMessage());
                return Optional.empty();
            }
            
            // Evaluate
            EvaluationResult result = DSL.evaluate(expression, userData);
            
            if (result.isSuccess()) {
                return Optional.of(result.getValue());
            } else {
                logger.warn("Evaluation failed: expression={}, user={}, error={}", 
                          expression, userData.getProfile().getUuid(), 
                          result.getErrorMessage());
                return Optional.empty();
            }
            
        } catch (Exception e) {
            logger.error("Unexpected error: expression={}, user={}", 
                       expression, userData.getProfile().getUuid(), e);
            return Optional.empty();
        }
    }
}
```

### Pattern 2: Error Metrics

```java
@Service
public class MonitoredDSLService {
    private final MeterRegistry meterRegistry;
    
    public EvaluationResult evaluate(String expression, UserData userData) {
        try {
            EvaluationResult result = DSL.evaluate(expression, userData);
            
            // Record metrics
            meterRegistry.counter("dsl.evaluations",
                "status", result.isSuccess() ? "success" : "failure",
                "expression_hash", hashExpression(expression)
            ).increment();
            
            if (!result.isSuccess()) {
                // Record error type
                String errorType = categorizeError(result.getErrorMessage());
                meterRegistry.counter("dsl.errors", "type", errorType).increment();
            }
            
            return result;
            
        } catch (Exception e) {
            meterRegistry.counter("dsl.errors", "type", "unexpected").increment();
            throw e;
        }
    }
    
    private String categorizeError(String errorMessage) {
        if (errorMessage.contains("Syntax")) return "syntax";
        if (errorMessage.contains("Function")) return "validation";
        if (errorMessage.contains("Division by zero")) return "division_by_zero";
        if (errorMessage.contains("Type")) return "type_mismatch";
        return "runtime";
    }
}
```

### Pattern 3: Error Aggregation

```java
@Service
public class ErrorAggregationService {
    private final Map<String, AtomicInteger> errorCounts = new ConcurrentHashMap<>();
    
    public EvaluationResult evaluateWithTracking(String expression, UserData userData) {
        EvaluationResult result = DSL.evaluate(expression, userData);
        
        if (!result.isSuccess()) {
            String errorKey = expression + ":" + result.getErrorMessage();
            errorCounts.computeIfAbsent(errorKey, k -> new AtomicInteger()).incrementAndGet();
        }
        
        return result;
    }
    
    @Scheduled(fixedRate = 60000)  // Every minute
    public void reportErrors() {
        if (!errorCounts.isEmpty()) {
            logger.warn("DSL errors in past minute:");
            errorCounts.forEach((key, count) -> {
                logger.warn("  {} occurrences: {}", count.get(), key);
            });
            errorCounts.clear();
        }
    }
}
```

### Pattern 4: User-Friendly Error Messages

```java
public class ErrorMessageFormatter {
    
    public static String formatUserFriendlyError(String technicalError) {
        if (technicalError.contains("Syntax error")) {
            return "The expression has a syntax error. Please check parentheses and quotes.";
        }
        
        if (technicalError.contains("Function") && technicalError.contains("not defined")) {
            return "Unknown function name. Please check the function reference.";
        }
        
        if (technicalError.contains("Division by zero")) {
            return "Cannot divide by zero. Please check your calculation.";
        }
        
        if (technicalError.contains("Type")) {
            return "Type mismatch. Please check that function arguments have the correct types.";
        }
        
        return "An error occurred while evaluating the expression. Please contact support.";
    }
}
```

## Summary

**Key Takeaways:**

1. **Always check `isSuccess()`** before accessing values
2. **Log all errors** for debugging and monitoring
3. **Validate expressions early** (at configuration time)
4. **Handle null data** gracefully
5. **Use try-catch** for unexpected errors
6. **Provide fallback values** for graceful degradation
7. **Monitor error rates** in production
8. **Categorize errors** for better diagnostics

**Error Handling Checklist:**

- [ ] Check `isSuccess()` before accessing values
- [ ] Log errors with context (expression, user, error message)
- [ ] Validate expressions at startup
- [ ] Handle null UserData
- [ ] Provide fallback values
- [ ] Use circuit breaker for resilience
- [ ] Monitor error metrics
- [ ] Test error scenarios

## See Also

- [API Documentation](API.md) - Core API reference
- [Function Reference](FUNCTION_REFERENCE.md) - All DSL functions
- [Performance Guide](PERFORMANCE_GUIDE.md) - Optimization techniques

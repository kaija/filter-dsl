# User Segmentation DSL - API Documentation

## Table of Contents

1. [Overview](#overview)
2. [Installation](#installation)
3. [Quick Start](#quick-start)
4. [Core API](#core-api)
5. [Data Models](#data-models)
6. [Error Handling](#error-handling)
7. [Thread Safety & Concurrency](#thread-safety--concurrency)
8. [Performance Considerations](#performance-considerations)
9. [Advanced Usage](#advanced-usage)

## Overview

The User Segmentation DSL is a Java library that provides a domain-specific language for querying and filtering user data. Built on AviatorScript, it offers:

- **Simple API**: Single method call to evaluate expressions
- **Type Safety**: Built-in validation and type checking
- **Extensibility**: Easy to add custom functions
- **Performance**: Expression caching and batch evaluation
- **Thread Safety**: Safe for concurrent use

**Minimum Requirements:**
- Java 11 or higher
- Maven 3.6+ or Gradle 7+

## Installation

### Maven

Add this dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.filter.dsl</groupId>
    <artifactId>filter-dsl</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

Add this dependency to your `build.gradle`:

```gradle
implementation 'com.filter.dsl:filter-dsl:1.0.0'
```

### Gradle (Kotlin DSL)

Add this dependency to your `build.gradle.kts`:

```kotlin
implementation("com.filter.dsl:filter-dsl:1.0.0")
```

## Quick Start

### Basic Usage

```java
import com.filter.dsl.DSL;
import com.filter.dsl.models.*;
import com.filter.dsl.evaluator.EvaluationResult;

// Create user data
UserData userData = UserData.builder()
    .profile(Profile.builder()
        .country("US")
        .city("New York")
        .build())
    .addEvent(Event.builder()
        .eventName("purchase")
        .timestamp("2024-01-15T10:30:00Z")
        .addParameter("amount", 99.99)
        .build())
    .build();

// Evaluate expression (simplified syntax)
String expression = "GT(COUNT(\"EQ(EVENT(\\\"eventName\\\"), \\\"purchase\\\")\"), 5)";
EvaluationResult result = DSL.evaluate(expression, userData);

// Check result
if (result.isSuccess()) {
    Boolean matches = (Boolean) result.getValue();
    System.out.println("User matches criteria: " + matches);
} else {
    System.err.println("Error: " + result.getErrorMessage());
}
```

### Batch Evaluation

Evaluate the same expression for multiple users efficiently:

```java
List<UserData> users = loadUsers(); // Your method to load users
String expression = "GT(SUM(\"EQ(EVENT(\\\"eventName\\\"), \\\"purchase\\\")\"), 1000)";

List<EvaluationResult> results = DSL.evaluateBatch(expression, users);

for (int i = 0; i < users.size(); i++) {
    UserData user = users.get(i);
    EvaluationResult result = results.get(i);
    
    if (result.isSuccess()) {
        System.out.println("User " + user.getProfile().getUuid() + 
                         ": " + result.getValue());
    }
}
```

## Core API

### DSL Class

The `DSL` class is the main entry point for the library. It provides both static convenience methods and instance methods for custom configuration.

#### Static Methods

**`evaluate(String expression, UserData userData)`**

Evaluates a DSL expression for a single user using the default configuration.

- **Parameters:**
  - `expression` - The DSL expression string
  - `userData` - The user data to evaluate against
- **Returns:** `EvaluationResult` containing the value or error
- **Thread Safety:** Thread-safe (uses singleton instance)

**Example:**
```java
EvaluationResult result = DSL.evaluate("EQ(PROFILE(\"country\"), \"US\")", userData);
```

**`evaluateBatch(String expression, List<UserData> users)`**

Evaluates a DSL expression for multiple users. The expression is compiled once and reused.

- **Parameters:**
  - `expression` - The DSL expression string
  - `users` - List of user data to evaluate
- **Returns:** `List<EvaluationResult>` corresponding to each user
- **Thread Safety:** Thread-safe

**Example:**
```java
List<EvaluationResult> results = DSL.evaluateBatch(expression, users);
```

**`clearCache()`**

Clears the expression cache in the default instance. Useful for freeing memory or forcing recompilation.

```java
DSL.clearCache();
```

**`getCacheSize()`**

Returns the number of cached expressions in the default instance.

```java
int size = DSL.getCacheSize();
System.out.println("Cached expressions: " + size);
```

#### Custom Configuration with Builder

For advanced use cases, create a custom DSL instance with specific configuration:

```java
DSL dsl = DSL.builder()
    .enableAutoDiscovery(true)   // Auto-discover functions (default: true)
    .enableCaching(true)          // Cache compiled expressions (default: true)
    .build();

// Use instance methods
EvaluationResult result = dsl.evaluateInstance(expression, userData);
```

**Builder Methods:**

- **`enableAutoDiscovery(boolean enabled)`** - Enable/disable automatic function discovery
  - Default: `true`
  - When enabled, scans classpath for DSL functions
  - When disabled, functions must be registered manually

- **`enableCaching(boolean enabled)`** - Enable/disable expression caching
  - Default: `true`
  - When enabled, compiled expressions are cached for reuse
  - When disabled, expressions are recompiled on every evaluation

- **`build()`** - Creates the DSL instance

#### Instance Methods

When using a custom DSL instance, use these methods:

- **`evaluateInstance(String expression, UserData userData)`** - Evaluate for single user
- **`evaluateBatchInstance(String expression, List<UserData> users)`** - Evaluate for multiple users
- **`clearCacheInstance()`** - Clear this instance's cache
- **`getCacheSizeInstance()`** - Get this instance's cache size
- **`getRegistry()`** - Get the function registry (for custom functions)
- **`getParser()`** - Get the parser (for validation/pretty-printing)

### EvaluationResult Class

Represents the result of evaluating a DSL expression.

**Methods:**

- **`boolean isSuccess()`** - Returns true if evaluation succeeded
- **`Object getValue()`** - Returns the result value (Boolean, Number, String, or Collection)
- **`String getErrorMessage()`** - Returns error message if evaluation failed
- **`long getEvaluationTimeMs()`** - Returns evaluation time in milliseconds

**Example:**
```java
EvaluationResult result = DSL.evaluate(expression, userData);

if (result.isSuccess()) {
    Object value = result.getValue();
    long time = result.getEvaluationTimeMs();
    System.out.println("Result: " + value + " (took " + time + "ms)");
} else {
    System.err.println("Error: " + result.getErrorMessage());
}
```

## Data Models

### UserData

The main data structure containing all user information.

**Builder Pattern:**
```java
UserData userData = UserData.builder()
    .profile(profile)
    .addVisit(visit)
    .addEvent(event)
    .build();
```

**Methods:**
- `Profile getProfile()` - Get user profile
- `Map<String, Visit> getVisits()` - Get all visits (UUID -> Visit)
- `List<Event> getEvents()` - Get all events

### Profile

Contains user demographic and device information.

**Fields:**
- `String uuid` - Unique user identifier
- `String country` - Country code (e.g., "US")
- `String city` - City name
- `String language` - Language code (e.g., "en")
- `String continent` - Continent name
- `String timezone` - Timezone (e.g., "America/New_York")
- `String os` - Operating system
- `String browser` - Browser name
- `String device` - Device type
- `String screen` - Screen resolution

**Builder Example:**
```java
Profile profile = Profile.builder()
    .uuid("user-123")
    .country("US")
    .city("New York")
    .language("en")
    .os("Windows")
    .browser("Chrome")
    .device("Desktop")
    .build();
```

### Event

Represents a user action or event.

**Fields:**
- `String uuid` - Event unique identifier
- `String eventName` - Name of the event (e.g., "purchase", "login")
- `String eventType` - Type of event (e.g., "action", "pageview")
- `String timestamp` - ISO 8601 timestamp
- `Integer duration` - Duration in seconds
- `String integration` - Integration source
- `String app` - Application name
- `String platform` - Platform name
- `Boolean isHttps` - Whether connection was HTTPS
- `Boolean isFirstInVisit` - First event in visit
- `Boolean isLastInVisit` - Last event in visit
- `Boolean isFirstEvent` - First event ever for user
- `Boolean isCurrent` - Currently active event
- `Boolean triggerable` - Can trigger actions
- `Map<String, Object> parameters` - Event parameters (custom data)

**Builder Example:**
```java
Event event = Event.builder()
    .uuid("event-456")
    .eventName("purchase")
    .eventType("action")
    .timestamp("2024-01-15T10:30:00Z")
    .addParameter("amount", 99.99)
    .addParameter("product_id", "prod-123")
    .addParameter("currency", "USD")
    .build();
```

### Visit

Represents a user session/visit.

**Fields:**
- `String uuid` - Visit unique identifier
- `String timestamp` - Visit start time (ISO 8601)
- `String landingPage` - Landing page URL
- `String referrerType` - Type of referrer (e.g., "search", "direct")
- `String referrerUrl` - Referrer URL
- `String referrerQuery` - Search query from referrer
- `Integer duration` - Visit duration in seconds
- `Integer actions` - Number of actions in visit
- `Boolean isFirstVisit` - Whether this is user's first visit

**Builder Example:**
```java
Visit visit = Visit.builder()
    .uuid("visit-789")
    .timestamp("2024-01-15T10:00:00Z")
    .landingPage("/home")
    .referrerType("search")
    .duration(300)
    .actions(5)
    .isFirstVisit(false)
    .build();
```

## Error Handling

The DSL provides comprehensive error handling with descriptive messages.

### Error Types

1. **Syntax Errors** - Invalid expression structure
2. **Validation Errors** - Semantic errors (undefined functions, wrong argument count)
3. **Runtime Errors** - Errors during evaluation (division by zero, type mismatches)
4. **Data Errors** - Issues with input data

### Checking for Errors

Always check `isSuccess()` before accessing the value:

```java
EvaluationResult result = DSL.evaluate(expression, userData);

if (result.isSuccess()) {
    // Safe to access value
    Object value = result.getValue();
} else {
    // Handle error
    String error = result.getErrorMessage();
    System.err.println("Evaluation failed: " + error);
}
```

### Common Error Scenarios

**Syntax Error:**
```java
String expr = "GT(COUNT(WHERE(userData.events, EQ(EVENT(\"eventName\"), \"purchase\"))"; // Missing )
EvaluationResult result = DSL.evaluate(expr, userData);
// result.getErrorMessage() -> "Syntax error at position 67: Expected closing parenthesis"
```

**Undefined Function:**
```java
String expr = "SUMM(userData.events)"; // Typo: SUMM instead of SUM
EvaluationResult result = DSL.evaluate(expr, userData);
// result.getErrorMessage() -> "Function 'SUMM' is not defined. Did you mean 'SUM'?"
```

**Type Mismatch:**
```java
String expr = "ADD(\"hello\", 5)"; // Can't add string and number
EvaluationResult result = DSL.evaluate(expr, userData);
// result.getErrorMessage() -> "Type error: ADD expects numeric arguments, got String and Number"
```

**Division by Zero:**
```java
String expr = "DIVIDE(10, 0)";
EvaluationResult result = DSL.evaluate(expr, userData);
// result.getErrorMessage() -> "Runtime error: Division by zero"
```

### Error Handling Best Practices

1. **Always check `isSuccess()`** before accessing values
2. **Log error messages** for debugging
3. **Validate expressions** before production use
4. **Handle null data** gracefully in your UserData objects
5. **Use try-catch** for unexpected exceptions (though the DSL catches most errors)

```java
try {
    EvaluationResult result = DSL.evaluate(expression, userData);
    
    if (result.isSuccess()) {
        processResult(result.getValue());
    } else {
        logger.error("DSL evaluation failed: {}", result.getErrorMessage());
        // Fallback logic
    }
} catch (Exception e) {
    logger.error("Unexpected error during DSL evaluation", e);
    // Handle unexpected errors
}
```

## Thread Safety & Concurrency

The DSL library is designed for safe concurrent use in multi-threaded applications.

### Thread-Safe Components

✅ **DSL Class** - Both static methods and instances are thread-safe
✅ **FunctionRegistry** - Safe for concurrent reads after initialization
✅ **DSLEvaluator** - Thread-safe with internal synchronization
✅ **Expression Cache** - Uses concurrent data structures
✅ **All DSL Functions** - Stateless and thread-safe

### Concurrency Patterns

#### Pattern 1: Shared Default Instance (Recommended)

The simplest and most efficient approach for most applications:

```java
// Multiple threads can safely call this
public class UserSegmentationService {
    public boolean matchesCriteria(String expression, UserData userData) {
        EvaluationResult result = DSL.evaluate(expression, userData);
        return result.isSuccess() && (Boolean) result.getValue();
    }
}

// Thread 1
service.matchesCriteria(expr1, user1);

// Thread 2 (concurrent)
service.matchesCriteria(expr2, user2);
```

**Benefits:**
- Expression cache is shared across threads
- No synchronization overhead for reads
- Optimal memory usage

#### Pattern 2: Thread-Local Instances

For scenarios where you want isolated caches per thread:

```java
public class UserSegmentationService {
    private static final ThreadLocal<DSL> dslInstance = 
        ThreadLocal.withInitial(() -> DSL.builder()
            .enableAutoDiscovery(true)
            .enableCaching(true)
            .build());
    
    public boolean matchesCriteria(String expression, UserData userData) {
        DSL dsl = dslInstance.get();
        EvaluationResult result = dsl.evaluateInstance(expression, userData);
        return result.isSuccess() && (Boolean) result.getValue();
    }
}
```

**Use Cases:**
- When you need isolated caches per thread
- When different threads evaluate different expression sets
- When you want to avoid cache contention

#### Pattern 3: Parallel Batch Processing

Process multiple users in parallel using Java Streams:

```java
List<UserData> users = loadUsers();
String expression = "GT(SUM(PARAM(\"amount\")), 1000)";

List<Boolean> results = users.parallelStream()
    .map(user -> DSL.evaluate(expression, user))
    .map(result -> result.isSuccess() && (Boolean) result.getValue())
    .collect(Collectors.toList());
```

### Concurrency Best Practices

1. **Use the default instance** for most cases - it's optimized for concurrent access
2. **Avoid clearing cache** during active evaluation in production
3. **Pre-warm the cache** by evaluating expressions once before high-traffic periods
4. **Monitor cache size** to prevent unbounded growth
5. **Use batch evaluation** when processing multiple users with the same expression

### Cache Management in Concurrent Environments

```java
// Pre-warm cache with common expressions
List<String> commonExpressions = loadCommonExpressions();
UserData sampleUser = createSampleUser();

for (String expr : commonExpressions) {
    DSL.evaluate(expr, sampleUser); // Warms up the cache
}

// Monitor cache size
int cacheSize = DSL.getCacheSize();
if (cacheSize > 1000) {
    logger.warn("Expression cache is large: {} entries", cacheSize);
    // Consider clearing old entries or increasing memory
}

// Clear cache during maintenance window (not during active traffic)
if (isMaintenanceWindow()) {
    DSL.clearCache();
    logger.info("Expression cache cleared");
}
```

## Performance Considerations

### Expression Caching

The DSL automatically caches compiled expressions for optimal performance.

**First Evaluation (Cold):**
```
Parse → Validate → Compile → Execute → Cache
~10-50ms depending on expression complexity
```

**Subsequent Evaluations (Warm):**
```
Cache Lookup → Execute
~1-5ms
```

**Performance Tips:**
1. **Reuse expressions** - The same expression string benefits from caching
2. **Use batch evaluation** - Compiles once, executes many times
3. **Pre-compile** common expressions during startup
4. **Monitor cache size** - Large caches use memory

### Batch Evaluation Performance

Batch evaluation is significantly faster than individual evaluations:

```java
// ❌ Slow: Individual evaluations
for (UserData user : users) {
    DSL.evaluate(expression, user); // Parses/compiles each time (if not cached)
}

// ✅ Fast: Batch evaluation
DSL.evaluateBatch(expression, users); // Parses/compiles once
```

**Performance Comparison:**
- Individual: ~10ms per user (cold) or ~2ms per user (warm)
- Batch: ~10ms + ~1ms per user

For 1000 users:
- Individual (cold): ~10,000ms
- Individual (warm): ~2,000ms
- Batch: ~1,010ms

### Optimization Strategies

#### 1. Expression Simplification

Simpler expressions evaluate faster:

```java
// ❌ Complex nested expression
String complex = "AND(GT(COUNT(WHERE(userData.events, EQ(EVENT(\"eventName\"), \"purchase\"))), 5), " +
                 "GT(SUM(WHERE(userData.events, EQ(EVENT(\"eventName\"), \"purchase\"))), 1000))";

// ✅ Simplified (if possible)
String simple = "AND(GT(COUNT(purchases), 5), GT(SUM(purchases), 1000))";
```

#### 2. Early Filtering

Filter data early to reduce processing:

```java
// ✅ Good: Filter first, then aggregate
"COUNT(WHERE(userData.events, EQ(EVENT(\"eventName\"), \"purchase\")))"

// ❌ Less efficient: Aggregate all, then filter
"COUNT(userData.events)" // Then filter in application code
```

#### 3. Data Preparation

Prepare UserData efficiently:

```java
// ✅ Good: Only include necessary events
UserData userData = UserData.builder()
    .profile(profile)
    .events(relevantEvents) // Pre-filtered
    .build();

// ❌ Less efficient: Include all events
UserData userData = UserData.builder()
    .profile(profile)
    .events(allEvents) // DSL filters at runtime
    .build();
```

#### 4. Expression Reuse

Store and reuse expression strings:

```java
public class ExpressionConstants {
    public static final String HIGH_VALUE_USER = 
        "GT(SUM(PARAM(\"amount\")), 1000)";
    
    public static final String ACTIVE_USER = 
        "GT(COUNT(WHERE(userData.events, EQ(EVENT(\"eventType\"), \"action\"))), 10)";
}

// Reuse across application
EvaluationResult result = DSL.evaluate(ExpressionConstants.HIGH_VALUE_USER, userData);
```

### Performance Monitoring

Monitor DSL performance in production:

```java
public class MonitoredDSLService {
    private static final Logger logger = LoggerFactory.getLogger(MonitoredDSLService.class);
    
    public EvaluationResult evaluate(String expression, UserData userData) {
        long start = System.currentTimeMillis();
        
        EvaluationResult result = DSL.evaluate(expression, userData);
        
        long duration = System.currentTimeMillis() - start;
        
        // Log slow evaluations
        if (duration > 100) {
            logger.warn("Slow DSL evaluation: {}ms for expression: {}", 
                       duration, expression);
        }
        
        // Metrics
        metrics.recordEvaluationTime(duration);
        metrics.recordCacheSize(DSL.getCacheSize());
        
        return result;
    }
}
```

### Memory Considerations

**Expression Cache Memory:**
- Each cached expression: ~1-10KB depending on complexity
- 1000 cached expressions: ~1-10MB
- Monitor with `DSL.getCacheSize()`

**UserData Memory:**
- Profile: ~1KB
- Event: ~0.5-2KB depending on parameters
- 1000 events: ~0.5-2MB

**Best Practices:**
1. **Limit cache size** - Clear cache periodically if it grows too large
2. **Filter events** - Only include relevant events in UserData
3. **Use batch processing** - Process users in batches to control memory
4. **Monitor heap usage** - Track JVM memory in production

## Advanced Usage

### Custom Function Registration

Add custom functions to extend the DSL:

```java
// Create custom DSL instance
DSL dsl = DSL.builder()
    .enableAutoDiscovery(false) // Disable auto-discovery
    .build();

// Register custom function
dsl.getRegistry().register(new MyCustomFunction());

// Use custom function
EvaluationResult result = dsl.evaluateInstance("MY_CUSTOM(\"value\")", userData);
```

See [Extension Guide](EXTENSION_GUIDE.md) for detailed instructions.

### Expression Validation

Validate expressions before evaluation:

```java
DSL dsl = DSL.builder().build();
DSLParser parser = dsl.getParser();

ParseResult parseResult = parser.parse(expression);

if (parseResult.isValid()) {
    System.out.println("Expression is valid");
} else {
    System.err.println("Invalid expression: " + parseResult.getErrorMessage());
    System.err.println("Error at position: " + parseResult.getErrorPosition());
}
```

### Pretty Printing

Format expressions for readability:

```java
DSL dsl = DSL.builder().build();
DSLParser parser = dsl.getParser();

String expression = "GT(COUNT(WHERE(userData.events,EQ(EVENT(\"eventName\"),\"purchase\"))),5)";
String formatted = parser.prettyPrint(expression);

System.out.println(formatted);
// Output:
// GT(
//   COUNT(
//     WHERE(
//       userData.events,
//       EQ(EVENT("eventName"), "purchase")
//     )
//   ),
//   5
// )
```

### Working with Different Data Sources

The DSL can work with data from various sources:

```java
// From database
UserData userData = userRepository.findById(userId)
    .map(this::convertToUserData)
    .orElseThrow();

// From API
UserData userData = apiClient.getUserData(userId);

// From JSON
String json = readJsonFile("user-data.json");
UserData userData = objectMapper.readValue(json, UserData.class);

// Evaluate
EvaluationResult result = DSL.evaluate(expression, userData);
```

### Integration Patterns

#### Pattern 1: Service Layer

```java
@Service
public class UserSegmentationService {
    
    public List<User> findUsersMatchingCriteria(String expression) {
        List<User> allUsers = userRepository.findAll();
        
        return allUsers.stream()
            .filter(user -> {
                UserData userData = convertToUserData(user);
                EvaluationResult result = DSL.evaluate(expression, userData);
                return result.isSuccess() && (Boolean) result.getValue();
            })
            .collect(Collectors.toList());
    }
    
    private UserData convertToUserData(User user) {
        // Convert your domain model to UserData
        return UserData.builder()
            .profile(convertProfile(user))
            .events(convertEvents(user.getEvents()))
            .build();
    }
}
```

#### Pattern 2: REST API

```java
@RestController
@RequestMapping("/api/segmentation")
public class SegmentationController {
    
    @PostMapping("/evaluate")
    public ResponseEntity<SegmentationResponse> evaluate(
            @RequestBody SegmentationRequest request) {
        
        try {
            UserData userData = loadUserData(request.getUserId());
            EvaluationResult result = DSL.evaluate(request.getExpression(), userData);
            
            if (result.isSuccess()) {
                return ResponseEntity.ok(new SegmentationResponse(
                    true,
                    result.getValue(),
                    result.getEvaluationTimeMs()
                ));
            } else {
                return ResponseEntity.badRequest()
                    .body(new SegmentationResponse(false, null, 0, 
                          result.getErrorMessage()));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new SegmentationResponse(false, null, 0, e.getMessage()));
        }
    }
}
```

#### Pattern 3: Batch Processing

```java
@Component
public class BatchSegmentationProcessor {
    
    public void processUserSegments(String expression, int batchSize) {
        long totalUsers = userRepository.count();
        int batches = (int) Math.ceil((double) totalUsers / batchSize);
        
        for (int i = 0; i < batches; i++) {
            List<User> batch = userRepository.findBatch(i * batchSize, batchSize);
            List<UserData> userDataList = batch.stream()
                .map(this::convertToUserData)
                .collect(Collectors.toList());
            
            List<EvaluationResult> results = DSL.evaluateBatch(expression, userDataList);
            
            // Process results
            for (int j = 0; j < batch.size(); j++) {
                User user = batch.get(j);
                EvaluationResult result = results.get(j);
                
                if (result.isSuccess() && (Boolean) result.getValue()) {
                    addUserToSegment(user, expression);
                }
            }
        }
    }
}
```

## See Also

- [Function Reference](FUNCTION_REFERENCE.md) - Complete list of all DSL functions
- [Extension Guide](EXTENSION_GUIDE.md) - How to add custom functions
- [Use Case Examples](USE_CASE_EXAMPLES.md) - Common segmentation patterns
- [Performance Guide](PERFORMANCE_GUIDE.md) - Optimization techniques

## Support

For issues, questions, or feature requests:
- GitHub Issues: [https://github.com/example/user-segmentation-dsl/issues](https://github.com/example/user-segmentation-dsl/issues)
- Documentation: [https://github.com/example/user-segmentation-dsl/docs](https://github.com/example/user-segmentation-dsl/docs)

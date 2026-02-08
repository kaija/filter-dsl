# User Segmentation DSL

A domain-specific language (DSL) built on AviatorScript for user segmentation and filtering. This library enables marketers and analysts to create complex queries on user profile data, visits, and events to perform segmentation, filtering, and computed property creation.

## Key Features

**Simplified Aggregation Syntax** - Aggregation functions support implicit event filtering for cleaner expressions:

```java
// Count all events
"COUNT()"

// Count with filter condition
"COUNT(\"EQ(EVENT(\\\"eventName\\\"), \\\"purchase\\\")\")"

// Sum with filter
"SUM(\"EQ(EVENT(\\\"eventName\\\"), \\\"purchase\\\")\")"
```

**Benefits:**
- Shorter, more readable expressions
- Fewer nested parentheses
- Intuitive syntax

## Features

- **UPPERCASE Function Syntax**: Clear, readable DSL with consistent naming
- **Simplified Aggregation Syntax**: Implicit event filtering for cleaner expressions
- **String-Based Filtering**: IF and WHERE functions accept string expressions for proper lazy evaluation
- **Extensible Architecture**: Easy to add custom functions without modifying core code
- **Type-Safe**: Built-in type checking and validation
- **Comprehensive Function Library**: 66+ built-in functions including TOP for frequency analysis
- **Property-Based Testing**: Thoroughly tested with both unit and property-based tests
- **Thread-Safe**: Safe for concurrent use in multi-threaded applications

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

### Manual Installation

If you're building from source or using the JAR directly:

1. Download the latest release JAR from the releases page
2. Add the JAR to your project's classpath
3. Ensure AviatorScript 5.4.3+ is also on your classpath

**Note**: When deploying to your own Maven repository, update the `distributionManagement` section in `pom.xml` with your repository URLs.

## Quick Start

```java
import com.filter.dsl.DSL;
import com.filter.dsl.models.UserData;
import com.filter.dsl.evaluator.EvaluationResult;

// Create user data
UserData userData = UserData.builder()
    .profile(Profile.builder()
        .country("US")
        .city("New York")
        .build())
    .event(Event.builder()
        .eventName("purchase")
        .timestamp("2024-01-15T10:30:00Z")
        .parameter("amount", 99.99)
        .build())
    .build();

// Evaluate a DSL expression
String expression = "GT(COUNT(\"EQ(EVENT(\\\"eventName\\\"), \\\"purchase\\\")\"), 5)";
EvaluationResult result = DSL.evaluate(expression, userData);

if (result.isSuccess()) {
    Boolean matches = (Boolean) result.getValue();
    System.out.println("User matches criteria: " + matches);
}
```

## Function Categories

### Logical Functions
- `AND(expr1, expr2, ...)` - Logical AND
- `OR(expr1, expr2, ...)` - Logical OR
- `NOT(expr)` - Logical NOT

### Comparison Functions
- `GT(a, b)` - Greater than
- `LT(a, b)` - Less than
- `GTE(a, b)` - Greater than or equal
- `LTE(a, b)` - Less than or equal
- `EQ(a, b)` - Equal
- `NEQ(a, b)` - Not equal

### Aggregation Functions

- `COUNT()` - Count all events from userData.events
- `COUNT("condition")` - Count events matching condition
- `COUNT(collection)` - Count items in collection
- `SUM()` - Sum all event parameters
- `SUM("condition")` - Sum events matching condition
- `AVG()` - Average of all event parameters
- `AVG("condition")` - Average of events matching condition
- `MIN(collection)` - Minimum value from collection
- `MAX(collection)` - Maximum value from collection
- `UNIQUE(collection)` - Distinct values from collection
- `TOP(collection)` - Most frequent value
- `TOP(collection, n)` - Top n most frequent values
- `TOP(collection, propertyName)` - Most frequent property value
- `TOP(collection, propertyName, n)` - Top n most frequent property values

### Mathematical Functions
- Basic: `ADD`, `SUBTRACT`, `MULTIPLY`, `DIVIDE`, `MOD`
- Advanced: `ABS`, `ROUND`, `CEIL`, `FLOOR`, `POW`, `SQRT`, `LOG`, `EXP`

### Date/Time Functions
- `ACTION_TIME()` - Get current event timestamp
- `DATE_FORMAT(timestamp, format)` - Format date
- `DATE_DIFF(date1, date2, unit)` - Calculate difference
- `FROM(n, unit)` / `TO(n, unit)` - Time range
- `NOW()`, `WEEKDAY()`, `IN_RECENT_DAYS()`, `IS_RECURRING()`

### Data Access Functions
- `PROFILE(field)` - Access profile field
- `EVENT(field)` - Access event field
- `PARAM(field)` - Access event parameter

### String Functions
- Matching: `CONTAINS`, `STARTS_WITH`, `ENDS_WITH`, `REGEX_MATCH`
- Manipulation: `UPPER`, `LOWER`, `TRIM`, `SUBSTRING`, `REPLACE`, `LENGTH`, `CONCAT`, `SPLIT`

### Filtering Functions
- `IF(condition)` - Filter by condition
- `WHERE(condition)` - Filter collection
- `BY(field)` - Group by field

### Conversion Functions
- `TO_NUMBER(value)` - Convert to number
- `TO_STRING(value)` - Convert to string
- `TO_BOOLEAN(value)` - Convert to boolean
- `CONVERT_UNIT(value, from, to)` - Convert between units

### Segmentation Functions
- `BUCKET(value, ranges)` - Assign to bucket

## Example Use Cases

### Users with more than 5 purchases

```java
String expression = "GT(COUNT(\"EQ(EVENT(\\\"eventName\\\"), \\\"purchase\\\")\"), 5)";
```

### Calculate active days ratio in recent 30 days

```java
String expression = """
    DIVIDE(
        COUNT(
            UNIQUE(
                "AND(
                    EQ(EVENT(\\"eventType\\"), \\"action\\"),
                    IN_RECENT_DAYS(30)
                )"
            )
        ),
        30
    )
""";
```

### Segment users by purchase amount

```java
String expression = """
    BUCKET(
        SUM("EQ(EVENT(\\"eventName\\"), \\"purchase\\")"),
        [[0, 10, "Low"], [10, 100, "Medium"], [100, 500, "High"], [500, 5000, "VIP"]]
    )
""";
```

### Compute most common device attributes

```java
// Most frequently used OS
profile.defineComputedProperty("os", "TOP(userData.visits, 'os')");

// Most frequently used browser
profile.defineComputedProperty("browser", "TOP(userData.visits, 'browser')");

// Top 3 most visited pages
profile.defineComputedProperty("top_pages", "TOP(userData.visits, 'landingPage', 3)");
```

## Extending the DSL

To add a custom function:

1. Create a class extending `DSLFunction`
2. Implement required methods
3. Register with `FunctionRegistry`

```java
public class MyCustomFunction extends DSLFunction {
    @Override
    public String getName() {
        return "MY_CUSTOM";
    }
    
    @Override
    public FunctionMetadata getMetadata() {
        return FunctionMetadata.builder()
            .name("MY_CUSTOM")
            .minArgs(1)
            .maxArgs(1)
            .returnType(ReturnType.STRING)
            .build();
    }
    
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 1);
        String value = toString(args[0], env);
        return new AviatorString("Custom: " + value);
    }
}

// Register the function
FunctionRegistry registry = new FunctionRegistry();
registry.register(new MyCustomFunction());
```

## Building from Source

```bash
# Clone the repository
git clone https://github.com/example/user-segmentation-dsl.git
cd user-segmentation-dsl

# Build with Maven
mvn clean install

# Run tests
mvn test

# Generate Javadoc
mvn javadoc:javadoc

# Package for distribution (creates JAR with sources and javadoc)
mvn clean package
```

## Publishing to Maven Repository

To publish this library to your Maven repository:

1. Configure your repository credentials in `~/.m2/settings.xml`:
```xml
<servers>
    <server>
        <id>releases</id>
        <username>your-username</username>
        <password>your-password</password>
    </server>
    <server>
        <id>snapshots</id>
        <username>your-username</username>
        <password>your-password</password>
    </server>
</servers>
```

2. Update `distributionManagement` in `pom.xml` with your repository URLs

3. Deploy to repository:
```bash
# Deploy release version
mvn clean deploy

# Deploy snapshot version (change version to X.Y.Z-SNAPSHOT in pom.xml)
mvn clean deploy
```

## Requirements

- Java 11 or higher
- Maven 3.6+ or Gradle 7+
- AviatorScript 5.4.3 (automatically included as dependency)

## Performance

The DSL is optimized for high-performance evaluation with expression caching and efficient execution.

### Performance Characteristics

| Metric | Cold (First Run) | Warm (Cached) | Notes |
|--------|------------------|---------------|-------|
| **Simple Expression** | ~15ms | ~2ms | Single condition check |
| **Complex Expression** | ~45ms | ~8ms | Multiple aggregations |
| **Batch (1000 users)** | ~1.5s | ~1.0s | Compile once, execute many |
| **Throughput (warm)** | - | 500-1000 ops/sec | Depends on complexity |

### Function Performance (Typical Execution Time)

| Category | Functions | Avg Time | Notes |
|----------|-----------|----------|-------|
| **Data Access** | PROFILE, EVENT, PARAM | <0.001ms | Direct field access |
| **Comparison** | EQ, GT, LT, GTE, LTE, NEQ | <0.001ms | Simple comparisons |
| **Logical** | AND, OR, NOT | <0.001ms | Short-circuit evaluation |
| **Math (Basic)** | ADD, SUBTRACT, MULTIPLY, DIVIDE | <0.001ms | Native operations |
| **Math (Advanced)** | POW, SQRT, LOG, EXP | 0.001-0.002ms | Math library calls |
| **String** | CONTAINS, UPPER, LOWER, TRIM | 0.001-0.003ms | String operations |
| **String (Regex)** | REGEX_MATCH | 0.005-0.020ms | Pattern matching |
| **Aggregation** | COUNT, SUM, AVG, MIN, MAX | 0.003-0.010ms | Depends on collection size |
| **Aggregation (Filtered)** | COUNT("filter"), SUM("filter") | 0.005-0.015ms | Includes filtering overhead |
| **Date/Time** | DATE_FORMAT, DATE_DIFF, WEEKDAY | 0.002-0.005ms | Date parsing/formatting |
| **Conversion** | TO_NUMBER, TO_STRING, TO_BOOLEAN | <0.001ms | Type conversion |
| **Segmentation** | BUCKET | 0.001-0.003ms | Range lookup |

### Performance Tips

1. **Enable Caching** (default) - 10-50x speedup for repeated expressions
2. **Use Batch Evaluation** - Process multiple users with same expression
3. **Filter Early** - Use simplified aggregation syntax to filter at source
4. **Pre-warm Cache** - Compile common expressions at startup
5. **Monitor Cache Size** - Clear periodically if needed

**Example - Batch Processing:**
```java
// ❌ Slow: Individual evaluations (2000ms for 1000 users)
for (UserData user : users) {
    DSL.evaluate(expression, user);
}

// ✅ Fast: Batch evaluation (1000ms for 1000 users)
DSL.evaluateBatch(expression, users);
```

See [Performance Guide](docs/PERFORMANCE_GUIDE.md) for detailed optimization techniques.

## Versioning

This project follows [Semantic Versioning](https://semver.org/):
- **MAJOR** version for incompatible API changes
- **MINOR** version for backwards-compatible functionality additions
- **PATCH** version for backwards-compatible bug fixes

Current version: **1.0.0**

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

We welcome contributions! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines on:
- Setting up your development environment
- Adding new DSL functions
- Writing tests
- Submitting pull requests
- Code style and documentation standards

## Documentation

For detailed documentation, see:
- [API Documentation](docs/API.md) - Complete API reference, data models, error handling, thread safety, and performance
- [Function Reference](docs/FUNCTION_REFERENCE.md) - All 66+ built-in DSL functions with examples
- [Profile Guide](docs/PROFILE_GUIDE.md) - User profile model with computed properties
- [Extension Guide](docs/EXTENSION_GUIDE.md) - How to add custom functions
- [Performance Guide](docs/PERFORMANCE_GUIDE.md) - Optimization techniques and best practices
- [Use Case Examples](docs/USE_CASE_EXAMPLES.md) - Common segmentation patterns

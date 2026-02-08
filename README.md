# User Segmentation DSL

A domain-specific language (DSL) built on AviatorScript for user segmentation and filtering. This library enables marketers and analysts to create complex queries on user profile data, visits, and events to perform segmentation, filtering, and computed property creation.

## ✨ What's New in v1.1.0

**Simplified Aggregation Syntax** - Aggregation functions now support implicit event filtering!

```java
// Old syntax (still supported)
"COUNT(WHERE(userData.events, \"EQ(EVENT(\\\"eventName\\\"), \\\"purchase\\\")\"))"

// New simplified syntax (recommended)
"COUNT(\"EQ(EVENT(\\\"eventName\\\"), \\\"purchase\\\")\")"

// Even simpler - count all events
"COUNT()"
```

**Benefits:**
- 25-70% shorter expressions
- More intuitive and readable
- Fewer nested parentheses
- Fully backward compatible

See [Migration Guide](#migration-to-simplified-syntax) below for details.

## Features

- **UPPERCASE Function Syntax**: Clear, readable DSL with consistent naming
- **Simplified Aggregation Syntax**: Implicit event filtering for cleaner expressions (NEW!)
- **String-Based Filtering**: IF and WHERE functions accept string expressions for proper lazy evaluation
- **Extensible Architecture**: Easy to add custom functions without modifying core code
- **Type-Safe**: Built-in type checking and validation
- **Comprehensive Function Library**: 65+ built-in functions for logical operations, aggregations, math, dates, strings, and more
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

// Evaluate a DSL expression (NEW simplified syntax!)
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

**NEW: Simplified syntax with implicit event filtering!**

- `COUNT()` - Count all events from userData.events
- `COUNT("condition")` - Count events matching condition
- `COUNT(collection)` - Count items in collection (legacy)
- `SUM()` - Sum all event parameters
- `SUM("condition")` - Sum events matching condition
- `AVG()` - Average of all event parameters
- `AVG("condition")` - Average of events matching condition
- `MIN()` - Minimum from all events
- `MIN("condition")` - Minimum from events matching condition
- `MAX()` - Maximum from all events
- `MAX("condition")` - Maximum from events matching condition
- `UNIQUE()` - Distinct events
- `UNIQUE("condition")` - Distinct events matching condition

**Note:** All aggregation functions now default to operating on `userData.events` when no collection is provided. This eliminates the need for explicit `WHERE(userData.events, ...)` wrappers in most cases.

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

### Users with more than 5 purchases in the past year

**New simplified syntax (recommended):**
```java
String expression = "GT(COUNT(\"EQ(EVENT(\\\"eventName\\\"), \\\"purchase\\\")\"), 5)";
```

**Old syntax (still supported):**
```java
String expression = """
    GT(
        COUNT(
            WHERE(
                userData.events,
                "EQ(EVENT(\\"eventName\\"), \\"purchase\\")"
            )
        ),
        5
    )
""";
```

### Calculate active days ratio in recent 30 days

**New simplified syntax (recommended):**
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

**Old syntax (still supported):**
```java
String expression = """
    DIVIDE(
        COUNT(
            UNIQUE(
                WHERE(
                    userData.events,
                    "AND(
                        EQ(EVENT(\\"eventType\\"), \\"action\\"),
                        IN_RECENT_DAYS(30)
                    )"
                )
            )
        ),
        30
    )
""";
```

### Segment users by purchase amount

**New simplified syntax (recommended):**
```java
String expression = """
    BUCKET(
        SUM("EQ(EVENT(\\"eventName\\"), \\"purchase\\")"),
        [[0, 10, "Low"], [10, 100, "Medium"], [100, 500, "High"], [500, 5000, "VIP"]]
    )
""";
```

**Old syntax (still supported):**
```java
String expression = """
    BUCKET(
        SUM(
            WHERE(
                userData.events,
                "EQ(EVENT(\\"eventName\\"), \\"purchase\\")"
            )
        ),
        [[0, 10, "Low"], [10, 100, "Medium"], [100, 500, "High"], [500, 5000, "VIP"]]
    )
""";
```

## Migration to Simplified Syntax

The new simplified syntax is **fully backward compatible**. Your existing code will continue to work without any changes.

### Key Changes

1. **Aggregation functions now accept 0-2 arguments** (previously 1 argument)
   - 0 arguments: operates on all `userData.events`
   - 1 string argument: filters `userData.events` with condition
   - 1 collection argument: operates on provided collection (legacy)
   - 2 arguments: filters provided collection with condition (legacy)

2. **No need for explicit `WHERE(userData.events, ...)` wrapper**
   - Old: `COUNT(WHERE(userData.events, "condition"))`
   - New: `COUNT("condition")`

3. **Expression length reduced by 25-70%**
   - Fewer nested parentheses
   - Less quote escaping
   - More readable

### Migration Examples

```java
// Count all events
Old: COUNT(userData.events)
New: COUNT()

// Count with filter
Old: COUNT(WHERE(userData.events, "EQ(EVENT(\"eventName\"), \"purchase\")"))
New: COUNT("EQ(EVENT(\"eventName\"), \"purchase\")")

// Sum with filter
Old: SUM(WHERE(userData.events, "EQ(EVENT(\"eventName\"), \"purchase\")"))
New: SUM("EQ(EVENT(\"eventName\"), \"purchase\")")

// Complex nested expression
Old: DIVIDE(COUNT(UNIQUE(WHERE(userData.events, "AND(...)"))), 30)
New: DIVIDE(COUNT(UNIQUE("AND(...)")), 30)
```

### When to Use Old Syntax

The old syntax is still useful when:
- Filtering a custom collection (not `userData.events`)
- Working with existing code that you don't want to update
- Team preference for explicit collection specification

Both syntaxes are equally performant and fully supported.

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

## Versioning

This project follows [Semantic Versioning](https://semver.org/):
- **MAJOR** version for incompatible API changes
- **MINOR** version for backwards-compatible functionality additions
- **PATCH** version for backwards-compatible bug fixes

Current version: **1.1.0**

### Version History
- **1.1.0** (2024-02) - Added simplified aggregation syntax with implicit event filtering
- **1.0.0** (2024-01) - Initial release

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
- [Function Reference](docs/FUNCTION_REFERENCE.md) - All 50+ built-in DSL functions with examples
- [Extension Guide](docs/EXTENSION_GUIDE.md) - How to add custom functions
- [Performance Guide](docs/PERFORMANCE_GUIDE.md) - Optimization techniques and best practices
- [Use Case Examples](docs/USE_CASE_EXAMPLES.md) - Common segmentation patterns

# User Segmentation DSL - Documentation

Complete documentation for the User Segmentation DSL library.

## Getting Started

New to the DSL? Start here:

1. **[Quick Reference](QUICK_REFERENCE.md)** - Fast reference for common patterns
2. **[API Documentation](API.md)** - Core API and basic usage
3. **[Use Case Examples](USE_CASE_EXAMPLES.md)** - Real-world examples

## Core Documentation

### [API Documentation](API.md)
Complete API reference covering:
- Installation and setup
- Quick start guide
- Core API methods
- Data models (UserData, Profile, Event, Visit)
- Error handling
- Thread safety and concurrency
- Performance considerations
- Advanced usage patterns

**Read this for:** Understanding how to integrate and use the DSL in your application.

### [Function Reference](FUNCTION_REFERENCE.md)
Comprehensive reference for all 50+ built-in DSL functions:
- Logical functions (AND, OR, NOT)
- Comparison functions (GT, LT, EQ, etc.)
- Aggregation functions (COUNT, SUM, AVG, etc.)
- Mathematical functions (ADD, MULTIPLY, SQRT, etc.)
- Date/time functions (DATE_FORMAT, WEEKDAY, etc.)
- Data access functions (PROFILE, EVENT, PARAM)
- Filtering functions (WHERE, IF, BY)
- String functions (CONTAINS, UPPER, SPLIT, etc.)
- Conversion functions (TO_NUMBER, CONVERT_UNIT, etc.)
- Segmentation functions (BUCKET)

**Read this for:** Detailed information about each DSL function with examples.

### [Profile Guide](PROFILE_GUIDE.md)
Complete guide to the Profile model:
- Fixed attributes (geographic, demographic, identification)
- Auto-computed properties (age, age_range)
- Dynamic properties (custom and computed)
- First referral tracking
- Usage examples and best practices
- DSL filtering patterns

**Read this for:** Understanding and using user profile attributes.

### [Extension Guide](EXTENSION_GUIDE.md)
Learn how to add custom functions:
- Quick start tutorial
- Function anatomy
- Step-by-step implementation
- Helper methods reference
- Testing custom functions
- Best practices
- Advanced topics

**Read this for:** Extending the DSL with domain-specific functions.

### [Performance Guide](PERFORMANCE_GUIDE.md)
Optimization techniques and best practices:
- Performance characteristics
- Expression caching strategies
- Batch processing
- Data optimization
- Expression optimization
- Memory management
- Monitoring and profiling
- Production best practices

**Read this for:** Optimizing DSL performance for production deployments.

### [Error Handling Guide](ERROR_HANDLING_GUIDE.md)
Comprehensive error handling:
- Error types (syntax, validation, runtime, data)
- Error detection and recovery
- Common errors and solutions
- Best practices
- Production patterns

**Read this for:** Robust error handling in production applications.

### [Use Case Examples](USE_CASE_EXAMPLES.md)
Common segmentation patterns:
- Users with > N purchases
- Purchase amount filtering
- Segmentation by ranges
- Active days calculation
- UTM parameter filtering
- Recurring event detection
- Weekday filtering
- Unit conversion
- Complex multi-condition queries

**Read this for:** Real-world examples and common patterns.

### [Extensibility Example](EXTENSIBILITY_EXAMPLE.md)
Practical example of adding a custom function:
- Complete REVERSE function implementation
- Registration and usage
- Testing approach

**Read this for:** Hands-on example of DSL extensibility.

## Quick References

### [Quick Reference](QUICK_REFERENCE.md)
Fast reference guide with:
- Installation snippets
- Basic usage patterns
- Common expression patterns
- Function categories
- Performance tips
- Error handling
- Thread safety
- Custom functions

**Read this for:** Quick lookup of common patterns and syntax.

## Documentation by Use Case

### For Application Developers

**Getting Started:**
1. [API Documentation](API.md) - Installation and basic usage
2. [Quick Reference](QUICK_REFERENCE.md) - Common patterns
3. [Use Case Examples](USE_CASE_EXAMPLES.md) - Real-world examples

**Going Deeper:**
4. [Profile Guide](PROFILE_GUIDE.md) - User profile attributes
5. [Function Reference](FUNCTION_REFERENCE.md) - All available functions
6. [Error Handling Guide](ERROR_HANDLING_GUIDE.md) - Robust error handling
7. [Performance Guide](PERFORMANCE_GUIDE.md) - Optimization techniques

### For Library Extenders

**Getting Started:**
1. [Extension Guide](EXTENSION_GUIDE.md) - Adding custom functions
2. [Extensibility Example](EXTENSIBILITY_EXAMPLE.md) - Practical example

**Going Deeper:**
3. [API Documentation](API.md) - Understanding the architecture
4. [Function Reference](FUNCTION_REFERENCE.md) - Learning from built-in functions

### For DevOps/SRE

**Getting Started:**
1. [Performance Guide](PERFORMANCE_GUIDE.md) - Production optimization
2. [Error Handling Guide](ERROR_HANDLING_GUIDE.md) - Error monitoring

**Going Deeper:**
3. [API Documentation](API.md) - Thread safety and concurrency
4. [Quick Reference](QUICK_REFERENCE.md) - Performance tips

## Documentation Structure

```
docs/
├── README.md                      # This file - documentation index
├── API.md                         # Core API documentation
├── FUNCTION_REFERENCE.md          # All DSL functions
├── PROFILE_GUIDE.md               # User profile attributes guide
├── EXTENSION_GUIDE.md             # Adding custom functions
├── PERFORMANCE_GUIDE.md           # Optimization techniques
├── ERROR_HANDLING_GUIDE.md        # Error handling patterns
├── QUICK_REFERENCE.md             # Quick reference guide
├── USE_CASE_EXAMPLES.md           # Real-world examples
└── EXTENSIBILITY_EXAMPLE.md       # Practical extension example
```

## Key Concepts

### DSL Expression
A string containing DSL functions that evaluates to a boolean or computed value.

Example: `"GT(COUNT(userData.events), 10)"`

### UserData
The data structure containing user profile, visits, and events.

### Evaluation
The process of executing a DSL expression against UserData to produce a result.

### Function
A built-in or custom operation that can be used in DSL expressions.

### Caching
Storing compiled expressions for reuse to improve performance.

### Batch Evaluation
Evaluating the same expression for multiple users efficiently.

## Common Tasks

### Evaluate an Expression
```java
EvaluationResult result = DSL.evaluate(expression, userData);
```
See: [API Documentation](API.md#core-api)

### Check for Errors
```java
if (result.isSuccess()) {
    Object value = result.getValue();
} else {
    String error = result.getErrorMessage();
}
```
See: [Error Handling Guide](ERROR_HANDLING_GUIDE.md)

### Batch Process Users
```java
List<EvaluationResult> results = DSL.evaluateBatch(expression, users);
```
See: [Performance Guide](PERFORMANCE_GUIDE.md#batch-processing)

### Add Custom Function
```java
public class MyFunction extends DSLFunction {
    // Implementation
}
dsl.getRegistry().register(new MyFunction());
```
See: [Extension Guide](EXTENSION_GUIDE.md)

### Optimize Performance
```java
// Enable caching (default)
DSL dsl = DSL.builder().enableCaching(true).build();

// Pre-warm cache
DSL.evaluate(expression, sampleUser);
```
See: [Performance Guide](PERFORMANCE_GUIDE.md)

## Support

### Issues and Questions
- GitHub Issues: [https://github.com/example/user-segmentation-dsl/issues](https://github.com/example/user-segmentation-dsl/issues)
- Documentation: [https://github.com/example/user-segmentation-dsl/docs](https://github.com/example/user-segmentation-dsl/docs)

### Contributing
See the main [README](../README.md) for contribution guidelines.

## Version Information

This documentation is for version 1.0.0 of the User Segmentation DSL library.

**Requirements:**
- Java 11 or higher
- Maven 3.6+ or Gradle 7+

**Dependencies:**
- AviatorScript 5.4.3
- Jackson 2.16.1

## License

See the main [README](../README.md) for license information.

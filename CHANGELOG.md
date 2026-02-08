# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.1.0] - 2024-02-XX

### Added
- **Simplified Aggregation Syntax**: All aggregation functions (COUNT, SUM, AVG, MIN, MAX, UNIQUE) now support implicit event filtering
  - 0-argument form: Operates on all events (e.g., `COUNT()`)
  - 1-argument string form: Filters events with condition (e.g., `COUNT("EQ(EVENT(\"eventName\"), \"purchase\")")`)
  - Backward compatible with existing 1-2 argument forms
  - Reduces expression length by 25-70%
  - More intuitive and readable syntax
- Helper methods in `DSLFunction` base class:
  - `getUserDataEvents()` - Get events from userData context
  - `filterCollection()` - Filter collection with string expression
  - `toCollection()` - Convert various types to collections
  - `parseTimestamp()` - Parse ISO 8601 timestamps

### Changed
- Updated all aggregation functions to support 0-2 arguments with flexible parameter types
- Enhanced `DSLFunction` base class with reusable helper methods for common operations

### Documentation
- Updated all documentation to showcase new simplified syntax
- Added migration examples showing old vs new syntax
- Updated `README.md` with v1.1.0 feature highlights
- Updated `FUNCTION_REFERENCE.md` with new aggregation function signatures
- Updated `QUICK_REFERENCE.md` with simplified examples
- Updated `USE_CASE_EXAMPLES.md` with new syntax patterns
- Updated `API.md` and `PERFORMANCE_GUIDE.md` with new examples

### Backward Compatibility
- **100% backward compatible** - all existing expressions continue to work
- Old syntax with explicit WHERE and userData.events still fully supported
- No breaking changes to API or function signatures

## [1.0.0] - 2024-01-XX

### Added
- Initial release of User Segmentation DSL library
- Complete DSL function framework with extensibility support
  - `DSLFunction` abstract base class with helper methods
  - `FunctionRegistry` for function management and auto-discovery
  - `FunctionMetadata` for function validation
- Comprehensive data models for user segmentation
  - `UserData`, `Profile`, `Visit`, `Event` models with builder patterns
  - `TimeRange` and `TimeUnit` for time-based filtering
  - `BucketDefinition` and `BucketRange` for segmentation
  - JSON serialization support via Jackson
- **50+ Built-in DSL Functions** organized by category:
  - Logical functions: AND, OR, NOT
  - Comparison functions: GT, LT, GTE, LTE, EQ, NEQ
  - Aggregation functions: COUNT, SUM, AVG, MIN, MAX, UNIQUE
  - Mathematical functions: ADD, SUBTRACT, MULTIPLY, DIVIDE, MOD, ABS, ROUND, CEIL, FLOOR, POW, SQRT, LOG, EXP
  - Date/time functions: ACTION_TIME, DATE_FORMAT, DATE_DIFF, FROM, TO, NOW, WEEKDAY, IN_RECENT_DAYS, IS_RECURRING, DAY_OF_MONTH, MONTH, YEAR
  - Data access functions: PROFILE, EVENT, PARAM
  - Filtering functions: IF, WHERE, BY
  - String functions: CONTAINS, STARTS_WITH, ENDS_WITH, REGEX_MATCH, UPPER, LOWER, TRIM, SUBSTRING, REPLACE, LENGTH, CONCAT, SPLIT
  - Conversion functions: TO_NUMBER, TO_STRING, TO_BOOLEAN, CONVERT_UNIT
  - Segmentation functions: BUCKET
- DSL Parser with validation and error reporting
  - Syntax validation with detailed error messages
  - Function name validation (UPPERCASE enforcement)
  - Argument count and type validation
  - Pretty printer for consistent expression formatting
- DSL Evaluator with comprehensive error handling
  - Expression compilation and caching
  - Batch evaluation support
  - Thread-safe execution
  - Graceful error handling (syntax, validation, runtime, data errors)
- Data Context Manager for evaluation context preparation
- Comprehensive test suite
  - Unit tests for all functions and components
  - Property-based tests for correctness properties
  - Integration tests for complex use cases
  - 39 correctness properties validated
- Complete documentation
  - API documentation with examples
  - Function reference guide
  - Extension guide for custom functions
  - Performance optimization guide
  - Error handling guide
  - Use case examples
  - Quick reference guide
- Library distribution support
  - Maven/Gradle dependency configuration
  - Source and Javadoc JAR generation
  - Publishing configuration for Maven repositories
  - Semantic versioning strategy
  - MIT License

### Infrastructure
- Java 11 compatibility
- Maven build system with JAR packaging
- JUnit 5 for unit testing
- jqwik for property-based testing
- AviatorScript 5.4.3 integration
- Jackson 2.16.1 for JSON support
- Maven plugins for source, javadoc, and deployment


## [Unreleased]

### Planned for Future Releases
- Additional aggregation functions (MEDIAN, PERCENTILE, STDDEV)
- Enhanced time zone support
- Query optimization layer
- Expression caching strategies
- Additional unit conversion categories (temperature, volume, area)
- Performance profiling tools
- Expression debugging utilities
- GraphQL-style query composition
- Support for custom data source plugins

---

## Version History

- **1.1.0** - Simplified aggregation syntax with implicit event filtering
- **1.0.0** - Initial release with complete DSL implementation

## Upgrade Guide

### Migrating to 1.1.0 from 1.0.0

No migration required! Version 1.1.0 is 100% backward compatible. However, you can optionally update your expressions to use the new simplified syntax:

**Old Syntax (Still Works)**:
```java
"COUNT(WHERE(userData.events, \"EQ(EVENT(\\\"eventName\\\"), \\\"purchase\\\")\"))"
```

**New Simplified Syntax (Recommended)**:
```java
"COUNT(\"EQ(EVENT(\\\"eventName\\\"), \\\"purchase\\\")\")"
```

**Benefits of New Syntax**:
- 25-70% shorter expressions
- Fewer nested parentheses
- More intuitive and readable
- Same performance characteristics

### Migrating to 1.0.0
This is the initial release. No migration needed.

## Breaking Changes

### 1.1.0
None - fully backward compatible.

### 1.0.0
None - initial release.

## Deprecation Notices

None at this time.

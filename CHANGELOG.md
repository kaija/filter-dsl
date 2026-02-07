# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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

- **1.0.0** - Initial release with complete DSL implementation

## Upgrade Guide

### Migrating to 1.0.0
This is the initial release. No migration needed.

## Breaking Changes

### 1.0.0
None - initial release.

## Deprecation Notices

None at this time.

# Implementation Plan: User Segmentation DSL

## Overview

This implementation plan breaks down the User Segmentation DSL into incremental coding tasks. The DSL is built on AviatorScript and provides UPPERCASE function syntax for querying user profile data, visits, and events. The implementation follows a bottom-up approach: data models → core functions → parser → evaluator → integration.

**This is a shared library project** that will be packaged as a JAR and distributed via Maven/Gradle for use in multiple Java applications. The library provides a clean API that applications can use to evaluate user segmentation expressions.

**Key Design Principle**: The DSL is highly extensible - each function is implemented in its own Java file, making it easy to add new functions without modifying existing code.

## Tasks

- [x] 1. Set up project structure and dependencies
  - Create Maven/Gradle project with AviatorScript dependency
  - Configure project as a library (JAR packaging)
  - Set up directory structure: models/, functions/{logical,comparison,aggregation,math,datetime,string,conversion,data,filtering,segmentation}/, parser/, evaluator/, tests/
  - Configure property-based testing library (jqwik)
  - Create base interfaces and abstract classes
  - Add Maven/Gradle publishing configuration for library distribution
  - _Requirements: 1.1, 14.1, 16.5_

- [ ] 2. Implement data models
  - [x] 2.1 Create UserData, Profile, Visit, and Event classes
    - Implement POJOs with all fields from requirements
    - Add constructors, getters, and builder patterns
    - Include JSON serialization annotations
    - _Requirements: 6.1, 6.2, 6.3_
  
  - [x] 2.2 Create TimeRange and TimeUnit models
    - Implement TimeRange class with from/to calculations
    - Create TimeUnit enum (D, H, M, W, MO, Y)
    - Add methods for time range containment checks
    - _Requirements: 5.4, 5.5, 5.14_
  
  - [x] 2.3 Create BucketDefinition and BucketRange models
    - Implement range boundary logic (inclusive/exclusive)
    - Add value containment checking
    - Support custom labels for ranges
    - _Requirements: 9.1, 9.4_
  
  - [ ]* 2.4 Write property test for data model serialization
    - **Property: Serialization round trip**
    - **Validates: Requirements 6.1, 6.2, 6.3**
    - Generate random UserData instances, serialize to JSON, deserialize, verify equality

- [ ] 3. Implement base DSL function framework
  - [x] 3.1 Create DSLFunction abstract base class with extensibility features
    - Extend AviatorScript's AbstractFunction
    - Add argument validation helpers (validateArgCount, validateArgCountRange)
    - Add type conversion helpers (toNumber, toString, toBoolean, toCollection)
    - Add context access methods (getUserData, getCurrentEvent, getCurrentVisit, getNow, getTimeRange)
    - Create FunctionMetadata class for validation
    - Add comprehensive JavaDoc for extension developers
    - _Requirements: 1.1, 11.5, 16.1, 16.6_
  
  - [x] 3.2 Create FunctionRegistry with auto-discovery
    - Implement function registration with AviatorScript
    - Store function metadata for validation
    - Provide lookup methods for parser
    - Support classpath scanning for automatic function discovery
    - Validate function metadata on registration
    - _Requirements: 15.2, 16.2, 16.3, 16.4_
  
  - [ ]* 3.3 Write unit tests for function framework
    - Test argument validation helpers
    - Test type conversion helpers
    - Test context access methods
    - Test metadata retrieval
    - Test auto-discovery mechanism
    - _Requirements: 11.5, 16.1, 16.6_

- [ ] 4. Implement logical and comparison functions
  - [x] 4.1 Implement AND, OR, NOT functions
    - Create LogicalAndFunction, LogicalOrFunction, LogicalNotFunction in functions/logical/
    - Each function in its own Java file
    - Handle variable argument counts for AND/OR
    - Register with FunctionRegistry
    - _Requirements: 2.1, 2.2, 2.3, 16.5_
  
  - [x] 4.2 Implement GT, LT, GTE, LTE, EQ, NEQ functions
    - Create comparison function classes in functions/comparison/
    - Each function in its own Java file
    - Handle numeric comparisons with proper type coercion
    - Register with FunctionRegistry
    - _Requirements: 2.4, 2.5, 2.6, 2.7, 2.8, 2.9, 16.5_
  
  - [ ]* 4.3 Write property tests for boolean algebra laws
    - **Property 2: Boolean Algebra Laws**
    - **Validates: Requirements 2.1, 2.2, 2.3**
    - Test commutativity, double negation, identity laws
  
  - [ ]* 4.4 Write property tests for comparison operations
    - **Property 3: Comparison Transitivity**
    - **Validates: Requirements 2.4, 2.5, 2.6, 2.7**
    - Test transitivity for GT, LT, GTE, LTE
    - **Property 4: Equality Symmetry**
    - **Validates: Requirements 2.8, 2.9**
    - Test EQ symmetry and NEQ equivalence to NOT(EQ)

- [ ] 5. Implement mathematical functions
  - [x] 5.1 Implement basic arithmetic functions (ADD, SUBTRACT, MULTIPLY, DIVIDE, MOD)
    - Create individual function classes in functions/math/ package
    - Each function in its own Java file for extensibility
    - Handle division by zero with proper error
    - Support type checking for numeric inputs
    - Register with FunctionRegistry
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5, 4.6, 4.15, 16.5_
  
  - [x] 5.2 Implement advanced math functions (ABS, ROUND, CEIL, FLOOR, POW, SQRT, LOG, EXP)
    - Create individual function classes in functions/math/ package
    - Each function in its own Java file
    - Handle edge cases (negative sqrt, log of zero, etc.)
    - Support optional parameters (ROUND decimals, LOG base)
    - Register with FunctionRegistry
    - _Requirements: 4.7, 4.8, 4.9, 4.10, 4.11, 4.12, 4.13, 4.14, 16.5_
  
  - [ ]* 5.3 Write property tests for arithmetic laws
    - **Property 10: Arithmetic Commutativity**
    - **Validates: Requirements 4.1, 4.3**
    - **Property 11: Arithmetic Associativity**
    - **Validates: Requirements 4.1, 4.3**
    - **Property 12: Subtraction and Addition Inverse**
    - **Validates: Requirements 4.1, 4.2**
    - **Property 13: Multiplication and Division Inverse**
    - **Validates: Requirements 4.3, 4.4**
  
  - [ ]* 5.4 Write property tests for error handling
    - **Property 14: Division by Zero Handling**
    - **Validates: Requirements 4.5**
    - **Property 15: Type Safety for Arithmetic**
    - **Validates: Requirements 4.15**
  
  - [ ]* 5.5 Write unit tests for advanced math functions
    - Test ABS, ROUND, CEIL, FLOOR, POW, SQRT, LOG, EXP
    - Test edge cases and error conditions
    - _Requirements: 4.7, 4.8, 4.9, 4.10, 4.11, 4.12, 4.13, 4.14_

- [ ] 6. Implement aggregation functions
  - [x] 6.1 Implement COUNT function
    - Create CountFunction in functions/aggregation/
    - Handle collections and return size
    - Return 0 for empty collections
    - Register with FunctionRegistry
    - _Requirements: 3.1, 3.7, 16.5_
  
  - [x] 6.2 Implement SUM and AVG functions
    - Create SumFunction and AvgFunction in functions/aggregation/
    - Each function in its own Java file
    - Handle empty collections appropriately
    - Register with FunctionRegistry
    - _Requirements: 3.2, 3.3, 3.7, 16.5_
  
  - [x] 6.3 Implement MIN and MAX functions
    - Create MinFunction and MaxFunction in functions/aggregation/
    - Handle comparable values
    - Return null for empty collections
    - Register with FunctionRegistry
    - _Requirements: 3.4, 3.5, 3.7, 16.5_
  
  - [x] 6.4 Implement UNIQUE function
    - Create UniqueFunction in functions/aggregation/
    - Remove duplicates from collections
    - Preserve order of first occurrence
    - Register with FunctionRegistry
    - _Requirements: 3.6, 16.5_
  
  - [ ]* 6.5 Write property tests for aggregation functions
    - **Property 5: COUNT Correctness**
    - **Validates: Requirements 3.1**
    - **Property 6: SUM Correctness**
    - **Validates: Requirements 3.2, 3.7**
    - **Property 7: AVG Equals SUM Divided by COUNT**
    - **Validates: Requirements 3.3**
    - **Property 8: MIN and MAX Bounds**
    - **Validates: Requirements 3.4, 3.5**
    - **Property 9: UNIQUE Eliminates Duplicates**
    - **Validates: Requirements 3.6**

- [x] 7. Checkpoint - Ensure basic functions work
  - Run all tests for logical, comparison, mathematical, and aggregation functions
  - Verify functions integrate correctly with AviatorScript
  - Test extensibility by adding a simple custom function
  - Ask user if questions arise

- [ ] 8. Implement date/time functions
  - [x] 8.1 Implement basic date/time functions (ACTION_TIME, DATE_FORMAT, DATE_DIFF, FROM, TO)
    - Create individual function classes in functions/datetime/ package
    - Each function in its own Java file
    - Use Java 8+ time API (Instant, DateTimeFormatter)
    - Handle invalid formats with errors
    - Support all time units (D, H, M, W, MO, Y)
    - Register with FunctionRegistry
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 5.13, 5.14, 16.5_
  
  - [x] 8.2 Implement extended date/time functions (NOW, WEEKDAY, IN_RECENT_DAYS, IS_RECURRING, DAY_OF_MONTH, MONTH, YEAR)
    - Create individual function classes in functions/datetime/ package
    - Each function in its own Java file
    - NOW returns current timestamp
    - WEEKDAY returns 1-7 (Monday-Sunday)
    - IN_RECENT_DAYS filters events from past N days
    - IS_RECURRING checks event frequency in time window
    - DAY_OF_MONTH, MONTH, YEAR extract date components
    - Register with FunctionRegistry
    - _Requirements: 5.6, 5.7, 5.8, 5.9, 5.10, 5.11, 5.12, 16.5_
  
  - [ ]* 8.3 Write property tests for date/time functions
    - **Property 16: ACTION_TIME Returns Event Timestamp**
    - **Validates: Requirements 5.1**
    - **Property 17: DATE_FORMAT Produces Valid Format**
    - **Validates: Requirements 5.2**
    - **Property 18: DATE_DIFF Unit Consistency**
    - **Validates: Requirements 5.3**
    - **Property 19: Time Range Containment**
    - **Validates: Requirements 5.4, 5.5, 7.4**
    - **Property 20: Invalid Date Error Handling**
    - **Validates: Requirements 5.13**
  
  - [ ]* 8.4 Write unit tests for extended date/time functions
    - Test NOW, WEEKDAY, IN_RECENT_DAYS, IS_RECURRING
    - Test date component extraction
    - _Requirements: 5.6, 5.7, 5.8, 5.9, 5.10, 5.11, 5.12_

- [ ] 9. Implement data access functions
  - [x] 9.1 Implement PROFILE, EVENT, PARAM functions
    - Create ProfileFunction, EventFunction, ParamFunction in functions/data/
    - Each function in its own Java file
    - Return null for non-existent fields
    - Support dot notation for nested access
    - Register with FunctionRegistry
    - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5, 16.5_
  
  - [ ]* 9.2 Write property tests for data access
    - **Property 21: Field Access Correctness**
    - **Validates: Requirements 6.1, 6.2, 6.3**
    - **Property 22: Non-Existent Field Returns Null**
    - **Validates: Requirements 6.4**

- [ ] 10. Implement filtering and collection functions
  - [x] 10.1 Implement IF and WHERE functions
    - Create IfFunction and WhereFunction in functions/filtering/
    - Filter collections based on boolean conditions
    - Integrate with time range context (FROM/TO)
    - Return filtered collections
    - Register with FunctionRegistry
    - _Requirements: 7.1, 7.3, 7.4, 7.5, 16.5_
  
  - [x] 10.2 Implement BY function
    - Create ByFunction in functions/filtering/
    - Group collection items by field value
    - Return grouped structure or flattened unique values
    - Register with FunctionRegistry
    - _Requirements: 7.2, 16.5_
  
  - [ ]* 10.3 Write property tests for filtering
    - **Property 23: Filter Condition Correctness**
    - **Validates: Requirements 7.1, 7.3**
    - **Property 24: Grouping Consistency**
    - **Validates: Requirements 7.2**
    - **Property 25: Filter Chaining Composition**
    - **Validates: Requirements 7.6**

- [ ] 11. Implement string functions
  - [x] 11.1 Implement string matching functions (CONTAINS, STARTS_WITH, ENDS_WITH, REGEX_MATCH)
    - Create individual function classes in functions/string/ package
    - Each function in its own Java file
    - Handle null inputs gracefully
    - Support case-sensitive matching
    - Handle invalid regex patterns with errors
    - Register with FunctionRegistry
    - _Requirements: 8.1, 8.2, 8.3, 8.4, 8.13, 16.5_
  
  - [x] 11.2 Implement string manipulation functions (UPPER, LOWER, TRIM, SUBSTRING, REPLACE, LENGTH, CONCAT, SPLIT)
    - Create individual function classes in functions/string/ package
    - Each function in its own Java file
    - Implement all string manipulation operations
    - Register with FunctionRegistry
    - _Requirements: 8.5, 8.6, 8.7, 8.8, 8.9, 8.10, 8.11, 8.12, 16.5_
  
  - [ ]* 11.3 Write property tests for string operations
    - **Property 26: String Contains Correctness**
    - **Validates: Requirements 8.1**
    - **Property 27: String Prefix and Suffix**
    - **Validates: Requirements 8.2, 8.3**
    - **Property 28: Regex Match Correctness**
    - **Validates: Requirements 8.4**
    - **Property 29: String Operation Null Safety**
    - **Validates: Requirements 8.13**
  
  - [ ]* 11.4 Write unit tests for string manipulation
    - Test all string manipulation functions
    - _Requirements: 8.5, 8.6, 8.7, 8.8, 8.9, 8.10, 8.11, 8.12_

- [ ] 12. Implement segmentation functions
  - [x] 12.1 Implement BUCKET function
    - Create BucketFunction in functions/segmentation/
    - Accept value and bucket definition
    - Find first matching range
    - Return bucket label or default
    - Handle boundary inclusivity correctly
    - Register with FunctionRegistry
    - _Requirements: 9.1, 9.2, 9.3, 9.4, 16.5_
  
  - [ ]* 12.2 Write property tests for bucketing
    - **Property 30: Bucket Assignment Correctness**
    - **Validates: Requirements 9.1, 9.3**
    - **Property 31: Bucket Boundary Semantics**
    - **Validates: Requirements 9.4**

- [ ] 13. Implement type conversion and unit conversion functions
  - [x] 13.1 Implement type conversion functions (TO_NUMBER, TO_STRING, TO_BOOLEAN)
    - Create individual function classes in functions/conversion/ package
    - Each function in its own Java file
    - TO_NUMBER converts strings and booleans to numbers
    - TO_STRING converts any value to string representation
    - TO_BOOLEAN uses standard truthiness rules
    - Handle incompatible types with errors
    - Register with FunctionRegistry
    - _Requirements: 10.1, 10.2, 10.3, 10.7, 16.5_
  
  - [x] 13.2 Implement CONVERT_UNIT function
    - Create ConvertUnitFunction in functions/conversion/
    - Support time units (seconds, minutes, hours, days, weeks, months, years)
    - Support distance units (meters, kilometers, miles, feet)
    - Support weight units (grams, kilograms, pounds, ounces)
    - Handle unknown units with errors
    - Use conversion factor tables
    - Register with FunctionRegistry
    - _Requirements: 10.4, 10.5, 10.6, 10.8, 16.5_
  
  - [ ]* 13.3 Write unit tests for conversion functions
    - Test TO_NUMBER, TO_STRING, TO_BOOLEAN
    - Test CONVERT_UNIT with all unit categories
    - Test error handling
    - _Requirements: 10.1, 10.2, 10.3, 10.4, 10.5, 10.6, 10.7, 10.8_

- [x] 14. Checkpoint - Ensure all functions implemented
  - Run all function tests
  - Verify all 39 correctness properties pass
  - Verify extensibility: add a custom function and test it works
  - Ask user if questions arise

- [ ] 15. Implement DSL parser
  - [x] 15.1 Create DSLParser interface and implementation
    - Validate DSL expression syntax
    - Check function names are UPPERCASE
    - Verify parentheses and bracket matching
    - Validate function argument counts using metadata
    - Detect undefined function references
    - Return ParseResult with errors and position
    - _Requirements: 1.1, 1.2, 1.5, 11.1, 11.2, 11.5_
  
  - [x] 15.2 Implement pretty printer
    - Format DSL expressions with consistent indentation
    - Preserve semantic meaning
    - Support configurable formatting options
    - _Requirements: 13.5_
  
  - [ ]* 15.3 Write property test for parser round-trip
    - **Property 1: Parser Round-Trip Consistency**
    - **Validates: Requirements 13.6**
  
  - [ ]* 15.4 Write unit tests for parser error handling
    - Test syntax error detection and reporting
    - _Requirements: 11.1, 11.2, 11.5_

- [ ] 16. Implement data context manager
  - [x] 16.1 Create DataContextManager
    - Build evaluation context from UserData
    - Create event-specific contexts
    - Include current event, visit, and timestamp
    - Provide context for all data access functions
    - _Requirements: 6.1, 6.2, 6.3_
  
  - [ ]* 16.2 Write unit tests for context creation
    - Test context contains all required fields
    - _Requirements: 6.1, 6.2, 6.3, 6.5_

- [ ] 17. Implement DSL evaluator
  - [x] 17.1 Create DSLEvaluator interface and implementation
    - Integrate parser, AviatorScript compiler, and context manager
    - Compile expressions and cache compiled forms
    - Execute expressions against user data
    - Return EvaluationResult with value or error
    - Handle all error types gracefully
    - _Requirements: 1.1, 1.3, 11.3, 11.4_
  
  - [x] 17.2 Implement batch evaluation
    - Evaluate expression for multiple users
    - Reuse compiled expression
    - Return list of results
    - _Requirements: 1.1_
  
  - [ ]* 17.3 Write property tests for evaluator
    - **Property 37: Expression Type Consistency**
    - **Validates: Requirements 1.3**
    - **Property 38: Nested Evaluation Order**
    - **Validates: Requirements 1.4**
    - **Property 39: Case Sensitivity for Function Names**
    - **Validates: Requirements 1.5**
  
  - [ ]* 17.4 Write property tests for error safety
    - **Property 32: Parse Error Reporting**
    - **Validates: Requirements 11.1**
    - **Property 33: Undefined Function Detection**
    - **Validates: Requirements 11.2**
    - **Property 34: Type Mismatch Detection**
    - **Validates: Requirements 11.3**
    - **Property 35: Runtime Error Safety**
    - **Validates: Requirements 11.4**
    - **Property 36: Argument Validation**
    - **Validates: Requirements 11.5**

- [ ] 18. Implement error handling system
  - [x] 18.1 Create DSLError class and error types
    - Define ErrorType enum (SYNTAX, VALIDATION, RUNTIME, DATA)
    - Include error message, position, and context
    - Create specific error subclasses
    - _Requirements: 11.1, 11.2, 11.3, 11.4_
  
  - [ ]* 18.2 Write unit tests for error handling
    - Test each error type
    - _Requirements: 11.1, 11.2, 11.3, 11.4_

- [ ] 19. Implement use case examples
  - [x] 19.1 Create example expressions for common use cases
    - Users with > N purchases in past year
    - Users with purchase amount > N
    - Segment users by purchase amount ranges
    - Calculate active days in time period
    - Filter by UTM parameters
    - Check recurring events
    - Filter by weekday
    - Convert units in expressions
    - _Requirements: 12.1, 12.2, 12.3, 12.4, 12.5_
  
  - [ ]* 19.2 Write integration tests for use cases
    - Test each example expression with realistic data
    - Test new extended functions in real scenarios
    - _Requirements: 12.1, 12.2, 12.3, 12.4, 12.5_

- [ ] 20. Create public API and documentation
  - [x] 20.1 Create main DSL facade class
    - Provide simple API: `DSL.evaluate(expression, userData)`
    - Hide internal complexity
    - Include builder for configuration
    - Ensure thread-safety for library usage
    - _Requirements: 1.1_
  
  - [x] 20.2 Write API documentation
    - Document all DSL functions with examples
    - Create usage guide with common patterns
    - Include error handling guide
    - Document performance considerations
    - Create extension guide for adding custom functions
    - Add library integration guide (Maven/Gradle dependency setup)
    - Document thread-safety and concurrency considerations
    - _Requirements: 13.1, 13.2, 13.3, 16.1, 16.2, 16.3, 16.4, 16.5, 16.6_
  
  - [ ]* 20.3 Write API usage examples
    - Create example programs demonstrating API
    - Show how to add custom functions
    - Show integration in a sample application
    - _Requirements: 1.1, 16.1, 16.2_
  
  - [x] 20.4 Prepare library for distribution
    - Configure Maven POM or Gradle build for publishing
    - Add README with quick start guide
    - Add LICENSE file
    - Create CHANGELOG
    - Set up versioning strategy
    - _Requirements: 1.1_

- [x] 21. Final checkpoint and integration testing
  - Run complete test suite (unit + property tests)
  - Verify all 39 correctness properties pass with 100+ iterations
  - Test with realistic user data volumes
  - Verify error handling for all error types
  - Test extensibility by adding and using a custom function
  - Ask user if questions arise

## Notes

- Tasks marked with `*` are optional and can be skipped for faster MVP
- Each task references specific requirements for traceability
- Property tests validate universal correctness properties from design
- Unit tests validate specific examples and edge cases
- Checkpoints ensure incremental validation
- **Extensibility is key**: Each function is in its own Java file in categorized packages
- All DSL functions must be registered with FunctionRegistry
- AviatorScript integration is critical - test thoroughly
- Focus on error handling - DSL should never crash on invalid input

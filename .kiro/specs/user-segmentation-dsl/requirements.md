# Requirements Document

## Introduction

This document specifies the requirements for a Domain Specific Language (DSL) built on AviatorScript for user segmentation and filtering. The DSL enables marketers and analysts to create complex queries on user profile data, visits, and events to perform segmentation, filtering, and computed property creation. The DSL uses UPPERCASE function names for clarity and supports both Boolean filtering expressions and value computation expressions.

## Glossary

- **DSL_Engine**: The AviatorScript-based parser and evaluator that processes DSL expressions
- **User_Profile**: A data structure containing user demographic and device information
- **Visit**: A session record containing timestamp, landing page, referrer, and duration information
- **Event**: An action record containing event name, timestamp, parameters, and metadata
- **Expression**: A DSL statement that evaluates to either a Boolean or computed value
- **Segmentation**: The process of grouping users based on shared characteristics or behaviors
- **AviatorScript**: The underlying expression language used to implement the DSL

## Requirements

### Requirement 1: Parse and Evaluate DSL Expressions

**User Story:** As a developer, I want to parse and evaluate DSL expressions, so that I can execute user segmentation queries against user data.

#### Acceptance Criteria

1. WHEN a valid DSL expression is provided, THE DSL_Engine SHALL parse it into an executable form
2. WHEN an invalid DSL expression is provided, THE DSL_Engine SHALL return a descriptive syntax error
3. WHEN a parsed expression is evaluated with user data, THE DSL_Engine SHALL return either a Boolean or computed value
4. WHEN a DSL expression contains nested function calls, THE DSL_Engine SHALL evaluate them in the correct order
5. THE DSL_Engine SHALL support UPPERCASE function names for all DSL operations

### Requirement 2: Logical and Comparison Operations

**User Story:** As a marketer, I want to use logical and comparison operations, so that I can create complex filtering conditions.

#### Acceptance Criteria

1. WHEN using AND operation, THE DSL_Engine SHALL return true only if all operands are true
2. WHEN using OR operation, THE DSL_Engine SHALL return true if any operand is true
3. WHEN using NOT operation, THE DSL_Engine SHALL return the logical negation of the operand
4. WHEN using GT operation, THE DSL_Engine SHALL return true if the first operand is greater than the second
5. WHEN using LT operation, THE DSL_Engine SHALL return true if the first operand is less than the second
6. WHEN using GTE operation, THE DSL_Engine SHALL return true if the first operand is greater than or equal to the second
7. WHEN using LTE operation, THE DSL_Engine SHALL return true if the first operand is less than or equal to the second
8. WHEN using EQ operation, THE DSL_Engine SHALL return true if operands are equal
9. WHEN using NEQ operation, THE DSL_Engine SHALL return true if operands are not equal

### Requirement 3: Aggregation Functions

**User Story:** As an analyst, I want to use aggregation functions on event collections, so that I can compute metrics like counts, sums, and averages.

#### Acceptance Criteria

1. WHEN using COUNT function on a collection, THE DSL_Engine SHALL return the number of items
2. WHEN using SUM function on numeric values, THE DSL_Engine SHALL return the total sum
3. WHEN using AVG function on numeric values, THE DSL_Engine SHALL return the arithmetic mean
4. WHEN using MIN function on comparable values, THE DSL_Engine SHALL return the minimum value
5. WHEN using MAX function on comparable values, THE DSL_Engine SHALL return the maximum value
6. WHEN using UNIQUE function on a collection, THE DSL_Engine SHALL return only distinct values
7. WHEN aggregation functions receive empty collections, THE DSL_Engine SHALL return appropriate default values (0 for COUNT, null for others)

### Requirement 4: Mathematical Operations

**User Story:** As an analyst, I want to perform mathematical calculations, so that I can create computed metrics and ratios.

#### Acceptance Criteria

1. WHEN using ADD operation, THE DSL_Engine SHALL return the sum of operands
2. WHEN using SUBTRACT operation, THE DSL_Engine SHALL return the difference of operands
3. WHEN using MULTIPLY operation, THE DSL_Engine SHALL return the product of operands
4. WHEN using DIVIDE operation with non-zero divisor, THE DSL_Engine SHALL return the quotient
5. WHEN using DIVIDE operation with zero divisor, THE DSL_Engine SHALL return an error or null
6. WHEN using MOD operation, THE DSL_Engine SHALL return the remainder of division
7. WHEN using ABS operation, THE DSL_Engine SHALL return the absolute value
8. WHEN using ROUND operation, THE DSL_Engine SHALL round to the nearest integer or specified decimal places
9. WHEN using CEIL operation, THE DSL_Engine SHALL round up to the nearest integer
10. WHEN using FLOOR operation, THE DSL_Engine SHALL round down to the nearest integer
11. WHEN using POW operation, THE DSL_Engine SHALL return base raised to exponent
12. WHEN using SQRT operation, THE DSL_Engine SHALL return the square root
13. WHEN using LOG operation, THE DSL_Engine SHALL return the logarithm with specified or default base
14. WHEN using EXP operation, THE DSL_Engine SHALL return e raised to the power
15. WHEN mathematical operations receive non-numeric inputs, THE DSL_Engine SHALL return an error

### Requirement 5: Date and Time Functions

**User Story:** As a marketer, I want to work with dates and times, so that I can create time-based segmentation rules.

#### Acceptance Criteria

1. WHEN using ACTION_TIME function, THE DSL_Engine SHALL return the timestamp of the current event being evaluated
2. WHEN using DATE_FORMAT function with a timestamp and format string, THE DSL_Engine SHALL return the formatted date string
3. WHEN using DATE_DIFF function with two dates and a unit, THE DSL_Engine SHALL return the difference in the specified unit
4. WHEN using FROM function with a number and time unit, THE DSL_Engine SHALL define the start of a relative time range
5. WHEN using TO function with a number and time unit, THE DSL_Engine SHALL define the end of a relative time range
6. WHEN using NOW function, THE DSL_Engine SHALL return the current timestamp
7. WHEN using WEEKDAY function, THE DSL_Engine SHALL return the day of week (1=Monday, 7=Sunday)
8. WHEN using IN_RECENT_DAYS function with N days, THE DSL_Engine SHALL filter events from the past N days
9. WHEN using IS_RECURRING function, THE DSL_Engine SHALL check if an event occurs at least min_count times within time_window
10. WHEN using DAY_OF_MONTH function, THE DSL_Engine SHALL return the day of month (1-31)
11. WHEN using MONTH function, THE DSL_Engine SHALL return the month (1-12)
12. WHEN using YEAR function, THE DSL_Engine SHALL return the year
13. WHEN date functions receive invalid date formats, THE DSL_Engine SHALL return an error
14. THE DSL_Engine SHALL support time units: D (days), H (hours), M (minutes), W (weeks), MO (months), Y (years)

### Requirement 6: Event and Profile Data Access

**User Story:** As a developer, I want to access user profile, visit, and event data, so that I can reference specific fields in my expressions.

#### Acceptance Criteria

1. WHEN using PROFILE function with a field name, THE DSL_Engine SHALL return the value from the user profile
2. WHEN using EVENT function with a field name, THE DSL_Engine SHALL return the value from the current event
3. WHEN using PARAM function with a parameter name, THE DSL_Engine SHALL return the value from the event parameters
4. WHEN accessing a non-existent field, THE DSL_Engine SHALL return null or an appropriate default value
5. WHEN accessing nested fields, THE DSL_Engine SHALL support dot notation or nested function calls
6. THE DSL_Engine SHALL provide access to all fields in the profile, visits, and events data structures

### Requirement 7: Filtering and Collection Operations

**User Story:** As an analyst, I want to filter and transform event collections, so that I can focus on specific subsets of data.

#### Acceptance Criteria

1. WHEN using IF function with a condition, THE DSL_Engine SHALL filter events to only those matching the condition
2. WHEN using BY function with a field, THE DSL_Engine SHALL group events by the specified field value
3. WHEN using WHERE function with a condition, THE DSL_Engine SHALL filter collections based on the condition
4. WHEN combining FROM and TO with filtering functions, THE DSL_Engine SHALL apply time range constraints
5. WHEN filtering operations receive empty collections, THE DSL_Engine SHALL return empty results
6. THE DSL_Engine SHALL support chaining multiple filtering operations

### Requirement 8: String Operations

**User Story:** As a marketer, I want to perform string matching and manipulation operations, so that I can filter and transform text data.

#### Acceptance Criteria

1. WHEN using CONTAINS function, THE DSL_Engine SHALL return true if the string contains the substring
2. WHEN using STARTS_WITH function, THE DSL_Engine SHALL return true if the string starts with the prefix
3. WHEN using ENDS_WITH function, THE DSL_Engine SHALL return true if the string ends with the suffix
4. WHEN using REGEX_MATCH function with a pattern, THE DSL_Engine SHALL return true if the string matches the regex
5. WHEN using UPPER function, THE DSL_Engine SHALL convert the string to uppercase
6. WHEN using LOWER function, THE DSL_Engine SHALL convert the string to lowercase
7. WHEN using TRIM function, THE DSL_Engine SHALL remove leading and trailing whitespace
8. WHEN using SUBSTRING function, THE DSL_Engine SHALL extract a substring from start position with optional length
9. WHEN using REPLACE function, THE DSL_Engine SHALL replace all occurrences of search string with replacement
10. WHEN using LENGTH function, THE DSL_Engine SHALL return the string length
11. WHEN using CONCAT function, THE DSL_Engine SHALL concatenate multiple strings
12. WHEN using SPLIT function, THE DSL_Engine SHALL split string into array by delimiter
13. WHEN string operations receive null or non-string inputs, THE DSL_Engine SHALL handle them gracefully
14. THE DSL_Engine SHALL support case-sensitive and case-insensitive string matching

### Requirement 9: Segmentation and Bucketing

**User Story:** As a marketer, I want to group users into segments based on value ranges, so that I can create cohorts for analysis.

#### Acceptance Criteria

1. WHEN using BUCKET function with a value and range definitions, THE DSL_Engine SHALL return the appropriate bucket label
2. WHEN a value falls outside all defined ranges, THE DSL_Engine SHALL return a default bucket or null
3. WHEN bucket ranges overlap, THE DSL_Engine SHALL use the first matching range
4. THE DSL_Engine SHALL support numeric range definitions with inclusive and exclusive boundaries
5. THE DSL_Engine SHALL support custom bucket labels for each range

### Requirement 10: Type Conversion and Unit Conversion

**User Story:** As an analyst, I want to convert between data types and units, so that I can normalize and compare values.

#### Acceptance Criteria

1. WHEN using TO_NUMBER function, THE DSL_Engine SHALL convert string or boolean values to numbers
2. WHEN using TO_STRING function, THE DSL_Engine SHALL convert any value to its string representation
3. WHEN using TO_BOOLEAN function, THE DSL_Engine SHALL convert values to boolean using standard truthiness rules
4. WHEN using CONVERT_UNIT function with time units, THE DSL_Engine SHALL convert between seconds, minutes, hours, days, weeks, months, years
5. WHEN using CONVERT_UNIT function with distance units, THE DSL_Engine SHALL convert between meters, kilometers, miles, feet
6. WHEN using CONVERT_UNIT function with weight units, THE DSL_Engine SHALL convert between grams, kilograms, pounds, ounces
7. WHEN conversion functions receive incompatible types, THE DSL_Engine SHALL return an error
8. WHEN CONVERT_UNIT receives unknown units, THE DSL_Engine SHALL return an error

### Requirement 11: Expression Validation and Error Handling

**User Story:** As a developer, I want clear error messages for invalid expressions, so that I can quickly identify and fix issues.

#### Acceptance Criteria

1. WHEN a DSL expression has syntax errors, THE DSL_Engine SHALL return an error message indicating the location and type of error
2. WHEN a DSL expression references undefined functions, THE DSL_Engine SHALL return a descriptive error
3. WHEN a DSL expression has type mismatches, THE DSL_Engine SHALL return a type error with details
4. WHEN a DSL expression has runtime errors during evaluation, THE DSL_Engine SHALL return an error without crashing
5. THE DSL_Engine SHALL validate function argument counts and types before evaluation

### Requirement 11: Expression Validation and Error Handling

**User Story:** As a developer, I want clear error messages for invalid expressions, so that I can quickly identify and fix issues.

#### Acceptance Criteria

1. WHEN a DSL expression has syntax errors, THE DSL_Engine SHALL return an error message indicating the location and type of error
2. WHEN a DSL expression references undefined functions, THE DSL_Engine SHALL return a descriptive error
3. WHEN a DSL expression has type mismatches, THE DSL_Engine SHALL return a type error with details
4. WHEN a DSL expression has runtime errors during evaluation, THE DSL_Engine SHALL return an error without crashing
5. THE DSL_Engine SHALL validate function argument counts and types before evaluation

### Requirement 12: Complex Query Support

**User Story:** As an analyst, I want to create complex multi-condition queries, so that I can implement sophisticated segmentation logic.

#### Acceptance Criteria

1. WHEN combining multiple aggregation functions, THE DSL_Engine SHALL evaluate them correctly
2. WHEN nesting filtering operations with time ranges, THE DSL_Engine SHALL apply all constraints properly
3. WHEN using mathematical operations on aggregated values, THE DSL_Engine SHALL compute the correct result
4. WHEN creating expressions with multiple levels of nesting, THE DSL_Engine SHALL maintain correct evaluation order
5. THE DSL_Engine SHALL support expressions that reference multiple data sources (profile, visits, events)

### Requirement 13: DSL Grammar and Syntax

**User Story:** As a developer, I want a well-defined grammar for the DSL, so that I can write valid expressions consistently.

#### Acceptance Criteria

1. THE DSL_Engine SHALL define a complete grammar specification for all supported operations
2. THE DSL_Engine SHALL support function call syntax with parentheses and comma-separated arguments
3. THE DSL_Engine SHALL support numeric literals, string literals, and boolean literals
4. THE DSL_Engine SHALL support comments or documentation within expressions
5. THE DSL_Engine SHALL provide a pretty printer that formats DSL expressions consistently
6. FOR ALL valid DSL expressions, parsing then pretty-printing then parsing SHALL produce an equivalent result

### Requirement 14: Performance and Scalability

**User Story:** As a system administrator, I want the DSL engine to perform efficiently, so that it can handle large volumes of user data.

#### Acceptance Criteria

1. WHEN evaluating expressions on large event collections, THE DSL_Engine SHALL complete within reasonable time bounds
2. WHEN processing multiple expressions concurrently, THE DSL_Engine SHALL handle them without resource exhaustion
3. THE DSL_Engine SHALL optimize repeated subexpressions to avoid redundant computation
4. THE DSL_Engine SHALL support lazy evaluation for collection operations when possible
5. WHEN memory usage exceeds thresholds, THE DSL_Engine SHALL handle it gracefully

### Requirement 15: Integration with AviatorScript

**User Story:** As a developer, I want the DSL to integrate seamlessly with AviatorScript, so that I can leverage its existing capabilities.

#### Acceptance Criteria

1. THE DSL_Engine SHALL compile DSL expressions into valid AviatorScript code
2. THE DSL_Engine SHALL register all DSL functions as AviatorScript custom functions
3. WHEN AviatorScript evaluation fails, THE DSL_Engine SHALL translate errors into DSL-specific error messages
4. THE DSL_Engine SHALL support mixing DSL functions with native AviatorScript operations
5. THE DSL_Engine SHALL maintain compatibility with AviatorScript's type system

### Requirement 16: Extensibility and Plugin Architecture

**User Story:** As a developer, I want to easily add new DSL functions, so that I can extend the DSL without modifying core code.

#### Acceptance Criteria

1. THE DSL_Engine SHALL provide an abstract base class for implementing custom functions
2. WHEN a new function class is created, THE DSL_Engine SHALL allow registration via FunctionRegistry
3. THE DSL_Engine SHALL support automatic function discovery via classpath scanning
4. WHEN registering a function, THE DSL_Engine SHALL validate function metadata
5. THE DSL_Engine SHALL organize functions by category in separate packages
6. THE DSL_Engine SHALL provide helper methods in base class for common operations (type conversion, validation, context access)

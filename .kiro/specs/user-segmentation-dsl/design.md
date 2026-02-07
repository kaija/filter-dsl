# Design Document: User Segmentation DSL

## Overview

The User Segmentation DSL is an AviatorScript-based domain-specific language for querying and filtering user profile data, visits, and events. The DSL provides a concise, UPPERCASE function syntax that enables marketers and analysts to create complex segmentation rules without writing code.

**This is a shared library designed to be used across multiple Java applications.** Applications can include this library as a Maven/Gradle dependency and use it to evaluate user segmentation expressions against their user data.

The system consists of three main components:
1. **DSL Parser**: Validates and parses DSL expressions into AviatorScript AST
2. **Function Registry**: Registers custom DSL functions with AviatorScript runtime
3. **Evaluator**: Executes parsed expressions against user data and returns results

The DSL supports two types of expressions:
- **Boolean expressions**: Return true/false for filtering users (e.g., "users who purchased > 5 times")
- **Computed expressions**: Return numeric or string values for creating user properties (e.g., "active days in past 30 days")

**Library Usage Example**:
```java
// Add dependency to your application
// Maven: <dependency>
//   <groupId>com.example</groupId>
//   <artifactId>user-segmentation-dsl</artifactId>
//   <version>1.0.0</version>
// </dependency>

// Use in your application
import com.example.dsl.DSL;
import com.example.dsl.model.UserData;

UserData userData = loadUserData();
String expression = "GT(COUNT(WHERE(EQ(EVENT(\"event_name\"), \"purchase\"))), 5)";

EvaluationResult result = DSL.evaluate(expression, userData);
if (result.isSuccess() && (Boolean) result.getValue()) {
    // User matches segmentation criteria
}
```

## Architecture

### High-Level Architecture

```
┌─────────────────┐
│  DSL Expression │
│   (String)      │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   DSL Parser    │
│  (Validation)   │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  AviatorScript  │
│    Compiler     │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   Expression    │
│   (Compiled)    │
└────────┬────────┘
         │
         ▼
┌─────────────────┐      ┌──────────────┐
│    Evaluator    │◄─────┤  User Data   │
│                 │      │  (Context)   │
└────────┬────────┘      └──────────────┘
         │
         ▼
┌─────────────────┐
│     Result      │
│ (Boolean/Value) │
└─────────────────┘
```

### Component Interaction

1. **Input Phase**: DSL expression string is received
2. **Parsing Phase**: Parser validates syntax and structure
3. **Compilation Phase**: AviatorScript compiles expression with registered custom functions
4. **Evaluation Phase**: Compiled expression executes against user data context
5. **Output Phase**: Result (Boolean or computed value) is returned

## Components and Interfaces

### 1. DSL Parser

**Responsibility**: Validate DSL expression syntax before compilation

**Interface**:
```java
public interface DSLParser {
    /**
     * Parse and validate a DSL expression
     * @param expression The DSL expression string
     * @return ParseResult containing validation status and errors
     */
    ParseResult parse(String expression);
    
    /**
     * Pretty-print a DSL expression with consistent formatting
     * @param expression The DSL expression string
     * @return Formatted expression string
     */
    String prettyPrint(String expression);
}

public class ParseResult {
    private boolean valid;
    private String errorMessage;
    private int errorPosition;
    private String formattedExpression;
}
```

**Key Behaviors**:
- Validates function names are UPPERCASE
- Checks parentheses and bracket matching
- Verifies function argument counts
- Identifies undefined function references
- Provides detailed error messages with position information

### 2. Function Registry

**Responsibility**: Register and manage all DSL custom functions with AviatorScript

**Interface**:
```java
public interface FunctionRegistry {
    /**
     * Register all DSL functions with AviatorScript instance
     * @param aviator The AviatorScript evaluator instance
     */
    void registerAll(AviatorEvaluatorInstance aviator);
    
    /**
     * Get function metadata for validation
     * @param functionName The UPPERCASE function name
     * @return Function metadata including argument types and count
     */
    FunctionMetadata getMetadata(String functionName);
}

public class FunctionMetadata {
    private String name;
    private int minArgs;
    private int maxArgs;
    private List<ArgumentType> argumentTypes;
    private ReturnType returnType;
}
```

**Function Categories**:

1. **Logical Functions**: AND, OR, NOT
2. **Comparison Functions**: GT, LT, GTE, LTE, EQ, NEQ
3. **Aggregation Functions**: COUNT, SUM, AVG, MIN, MAX, UNIQUE
4. **Mathematical Functions**: 
   - Basic: ADD, SUBTRACT, MULTIPLY, DIVIDE, MOD
   - Advanced: ABS, ROUND, CEIL, FLOOR, POW, SQRT, LOG, EXP
5. **Date/Time Functions**: 
   - Basic: ACTION_TIME, DATE_FORMAT, DATE_DIFF, FROM, TO
   - Extended: NOW, WEEKDAY, IN_RECENT_DAYS, IS_RECURRING, DAY_OF_MONTH, MONTH, YEAR
6. **Data Access Functions**: PROFILE, EVENT, PARAM
7. **Filtering Functions**: IF, BY, WHERE
8. **String Functions**: 
   - Matching: CONTAINS, STARTS_WITH, ENDS_WITH, REGEX_MATCH
   - Manipulation: UPPER, LOWER, TRIM, SUBSTRING, REPLACE, LENGTH, CONCAT, SPLIT
9. **Conversion Functions**: TO_NUMBER, TO_STRING, TO_BOOLEAN, CONVERT_UNIT
10. **Segmentation Functions**: BUCKET

### 3. Custom Function Implementations

The DSL provides an extensible architecture where each function is implemented in its own Java file, making it easy to add new functions without modifying existing code.

**Base Abstraction Layer**:

```java
public abstract class DSLFunction extends AbstractFunction {
    @Override
    public abstract AviatorObject call(Map<String, Object> env, 
                                       AviatorObject... args);
    
    /**
     * Get the UPPERCASE function name
     */
    @Override
    public abstract String getName();
    
    /**
     * Get function metadata for validation
     */
    public abstract FunctionMetadata getMetadata();
    
    // Helper methods for all DSL functions
    
    protected void validateArgCount(AviatorObject[] args, int expected) {
        if (args.length != expected) {
            throw new FunctionArgumentException(
                getName() + " expects " + expected + " arguments, got " + args.length);
        }
    }
    
    protected void validateArgCountRange(AviatorObject[] args, int min, int max) {
        if (args.length < min || args.length > max) {
            throw new FunctionArgumentException(
                getName() + " expects " + min + "-" + max + " arguments, got " + args.length);
        }
    }
    
    protected Object getUserData(Map<String, Object> env) {
        return env.get("userData");
    }
    
    protected Event getCurrentEvent(Map<String, Object> env) {
        return (Event) env.get("currentEvent");
    }
    
    protected Visit getCurrentVisit(Map<String, Object> env) {
        return (Visit) env.get("currentVisit");
    }
    
    protected Instant getNow(Map<String, Object> env) {
        return (Instant) env.get("now");
    }
    
    protected TimeRange getTimeRange(Map<String, Object> env) {
        return (TimeRange) env.get("timeRange");
    }
    
    protected Number toNumber(AviatorObject obj, Map<String, Object> env) {
        Object value = obj.getValue(env);
        if (value instanceof Number) {
            return (Number) value;
        }
        throw new TypeMismatchException("Expected number, got " + value.getClass().getSimpleName());
    }
    
    protected String toString(AviatorObject obj, Map<String, Object> env) {
        Object value = obj.getValue(env);
        return value == null ? null : value.toString();
    }
    
    protected Boolean toBoolean(AviatorObject obj, Map<String, Object> env) {
        Object value = obj.getValue(env);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        throw new TypeMismatchException("Expected boolean, got " + value.getClass().getSimpleName());
    }
    
    protected Collection<?> toCollection(AviatorObject obj, Map<String, Object> env) {
        Object value = obj.getValue(env);
        if (value instanceof Collection) {
            return (Collection<?>) value;
        }
        throw new TypeMismatchException("Expected collection, got " + value.getClass().getSimpleName());
    }
}
```

**Example Implementation - COUNT Function** (in `functions/aggregation/CountFunction.java`):
```java
package com.example.dsl.functions.aggregation;

public class CountFunction extends DSLFunction {
    @Override
    public String getName() {
        return "COUNT";
    }
    
    @Override
    public FunctionMetadata getMetadata() {
        return FunctionMetadata.builder()
            .name("COUNT")
            .minArgs(1)
            .maxArgs(1)
            .argumentType(0, ArgumentType.COLLECTION)
            .returnType(ReturnType.NUMBER)
            .description("Returns the number of items in a collection")
            .build();
    }
    
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 1);
        
        Object value = args[0].getValue(env);
        if (value == null) {
            return AviatorLong.valueOf(0);
        }
        
        if (value instanceof Collection) {
            return AviatorLong.valueOf(((Collection<?>) value).size());
        }
        
        throw new TypeMismatchException("COUNT expects a collection, got " + value.getClass().getSimpleName());
    }
}
```

**Function Organization by Category**:

```
functions/
├── logical/
│   ├── AndFunction.java
│   ├── OrFunction.java
│   └── NotFunction.java
├── comparison/
│   ├── GreaterThanFunction.java
│   ├── LessThanFunction.java
│   ├── EqualsFunction.java
│   └── ...
├── aggregation/
│   ├── CountFunction.java
│   ├── SumFunction.java
│   ├── AvgFunction.java
│   └── ...
├── math/
│   ├── AddFunction.java
│   ├── SubtractFunction.java
│   ├── AbsFunction.java
│   ├── RoundFunction.java
│   ├── CeilFunction.java
│   ├── FloorFunction.java
│   ├── PowFunction.java
│   ├── SqrtFunction.java
│   └── ...
├── datetime/
│   ├── ActionTimeFunction.java
│   ├── DateFormatFunction.java
│   ├── DateDiffFunction.java
│   ├── NowFunction.java
│   ├── WeekdayFunction.java
│   ├── InRecentDaysFunction.java
│   ├── IsRecurringFunction.java
│   └── ...
├── string/
│   ├── ContainsFunction.java
│   ├── UpperFunction.java
│   ├── LowerFunction.java
│   ├── TrimFunction.java
│   ├── LengthFunction.java
│   ├── SubstringFunction.java
│   ├── ReplaceFunction.java
│   └── ...
├── conversion/
│   ├── ToNumberFunction.java
│   ├── ToStringFunction.java
│   ├── ToBooleanFunction.java
│   ├── ConvertUnitFunction.java
│   └── ...
├── data/
│   ├── ProfileFunction.java
│   ├── EventFunction.java
│   └── ParamFunction.java
├── filtering/
│   ├── IfFunction.java
│   ├── WhereFunction.java
│   └── ByFunction.java
└── segmentation/
    └── BucketFunction.java
```

This architecture allows developers to:
1. Add new functions by creating a single Java file
2. Extend DSLFunction with minimal boilerplate
3. Leverage helper methods for common operations
4. Automatically register functions via classpath scanning or explicit registration

### 4. Data Context Manager

**Responsibility**: Prepare user data for expression evaluation

**Interface**:
```java
public interface DataContextManager {
    /**
     * Create evaluation context from user data
     * @param userData The complete user data structure
     * @return Map containing all accessible data for expression
     */
    Map<String, Object> createContext(UserData userData);
    
    /**
     * Create context for single event evaluation
     * @param userData The complete user data structure
     * @param event The specific event being evaluated
     * @return Map with event-specific context
     */
    Map<String, Object> createEventContext(UserData userData, 
                                           Event event);
}
```

**Context Structure**:
```java
{
    "userData": {
        "profile": { ... },
        "visits": { ... },
        "events": [ ... ]
    },
    "currentEvent": { ... },  // Set when evaluating per-event
    "currentVisit": { ... },  // Set when evaluating per-visit
    "now": <timestamp>        // Current evaluation time
}
```

### 5. DSL Evaluator

**Responsibility**: Execute compiled expressions against user data

**Interface**:
```java
public interface DSLEvaluator {
    /**
     * Evaluate a DSL expression for a user
     * @param expression The DSL expression string
     * @param userData The user data to evaluate against
     * @return Evaluation result (Boolean or computed value)
     */
    EvaluationResult evaluate(String expression, UserData userData);
    
    /**
     * Evaluate expression for multiple users (batch)
     * @param expression The DSL expression string
     * @param users List of user data
     * @return List of results corresponding to each user
     */
    List<EvaluationResult> evaluateBatch(String expression, 
                                         List<UserData> users);
}

public class EvaluationResult {
    private boolean success;
    private Object value;  // Boolean or computed value
    private String errorMessage;
    private long evaluationTimeMs;
}
```

## Data Models

### UserData Structure

```java
public class UserData {
    private Profile profile;
    private Map<String, Visit> visits;  // UUID -> Visit
    private List<Event> events;
}

public class Profile {
    private String country;
    private String city;
    private String language;
    private String continent;
    private String timezone;
    private String uuid;
    private String os;
    private String browser;
    private String device;
    private String screen;
}

public class Visit {
    private String uuid;
    private String timestamp;
    private String landingPage;
    private String referrerType;
    private String referrerUrl;
    private String referrerQuery;
    private Integer duration;
    private Integer actions;
    private Boolean isFirstVisit;
}

public class Event {
    private String uuid;
    private Boolean isFirstInVisit;
    private Boolean isLastInVisit;
    private Boolean isFirstEvent;
    private Boolean isCurrent;
    private String eventName;
    private String integration;
    private String app;
    private String platform;
    private Boolean isHttps;
    private String eventType;
    private Integer duration;
    private String timestamp;
    private Boolean triggerable;
    private Map<String, Object> parameters;
}
```

### Time Range Model

```java
public class TimeRange {
    private Integer fromValue;
    private TimeUnit fromUnit;
    private Integer toValue;
    private TimeUnit toUnit;
    private Instant referenceTime;
    
    public Instant getStartTime() {
        return referenceTime.minus(fromValue, fromUnit);
    }
    
    public Instant getEndTime() {
        return referenceTime.minus(toValue, toUnit);
    }
    
    public boolean contains(Instant timestamp) {
        return !timestamp.isBefore(getStartTime()) && 
               !timestamp.isAfter(getEndTime());
    }
}

public enum TimeUnit {
    D,   // Days
    H,   // Hours
    M,   // Minutes
    W,   // Weeks
    MO,  // Months
    Y    // Years
}
```

### Bucket Definition Model

```java
public class BucketDefinition {
    private List<BucketRange> ranges;
    private String defaultLabel;
}

public class BucketRange {
    private Double minValue;
    private Double maxValue;
    private boolean minInclusive;
    private boolean maxInclusive;
    private String label;
    
    public boolean contains(Double value) {
        boolean aboveMin = minInclusive ? 
            value >= minValue : value > minValue;
        boolean belowMax = maxInclusive ? 
            value <= maxValue : value < maxValue;
        return aboveMin && belowMax;
    }
}
```

## 
Correctness Properties

### What are Correctness Properties?

A property is a characteristic or behavior that should hold true across all valid executions of a system—essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.

Before defining the correctness properties, let me analyze each acceptance criterion for testability:


### Property Reflection

After analyzing all acceptance criteria, I've identified several areas where properties can be consolidated:

**Redundancy Elimination:**
1. Comparison operations (GT, LT, GTE, LTE, EQ, NEQ) can be combined into a single property about comparison correctness
2. Mathematical operations (ADD, SUBTRACT, MULTIPLY, DIVIDE, MOD) can be grouped into properties about arithmetic laws
3. String operations (CONTAINS, STARTS_WITH, ENDS_WITH) share similar patterns and can be tested together
4. Data access functions (PROFILE, EVENT, PARAM) all follow the same pattern of field access
5. Filtering operations (IF, WHERE) are essentially the same operation and can be combined
6. Aggregation functions each test unique behaviors and should remain separate

**Properties to Write:**
- Parser round-trip property (subsumes pretty-printing correctness)
- Logical operation properties (AND, OR, NOT follow boolean algebra)
- Comparison operation property (follows mathematical ordering)
- Aggregation properties (one per function: COUNT, SUM, AVG, MIN, MAX, UNIQUE)
- Arithmetic properties (commutative, associative laws)
- Date/time properties (format/parse, time range containment)
- Data access property (field retrieval correctness)
- Filtering property (filtered results match condition)
- String matching properties (one per operation type)
- Bucketing property (value-to-bucket mapping)
- Error handling properties (invalid inputs produce errors, not crashes)

### Correctness Properties

**Property 1: Parser Round-Trip Consistency**

*For any* valid DSL expression, parsing it, pretty-printing it, and parsing again should produce an equivalent executable form.

**Validates: Requirements 12.6**

**Property 2: Boolean Algebra Laws**

*For any* boolean values A and B:
- AND(A, B) should equal AND(B, A) (commutative)
- OR(A, B) should equal OR(B, A) (commutative)
- NOT(NOT(A)) should equal A (double negation)
- AND(A, true) should equal A (identity)
- OR(A, false) should equal A (identity)

**Validates: Requirements 2.1, 2.2, 2.3**

**Property 3: Comparison Transitivity**

*For any* numeric values A, B, and C, if GT(A, B) is true and GT(B, C) is true, then GT(A, C) should be true. Similar transitivity should hold for LT, GTE, and LTE.

**Validates: Requirements 2.4, 2.5, 2.6, 2.7**

**Property 4: Equality Symmetry**

*For any* values A and B, EQ(A, B) should equal EQ(B, A), and NEQ(A, B) should equal NOT(EQ(A, B)).

**Validates: Requirements 2.8, 2.9**

**Property 5: COUNT Correctness**

*For any* collection of events, COUNT should return a non-negative integer equal to the number of items in the collection.

**Validates: Requirements 3.1**

**Property 6: SUM Correctness**

*For any* collection of numeric values, SUM should return the arithmetic sum of all values, and SUM of an empty collection should return 0.

**Validates: Requirements 3.2, 3.7**

**Property 7: AVG Equals SUM Divided by COUNT**

*For any* non-empty collection of numeric values, AVG should equal DIVIDE(SUM(collection), COUNT(collection)).

**Validates: Requirements 3.3**

**Property 8: MIN and MAX Bounds**

*For any* non-empty collection of comparable values:
- MIN should return a value that is less than or equal to all values in the collection
- MAX should return a value that is greater than or equal to all values in the collection
- MIN should be less than or equal to MAX

**Validates: Requirements 3.4, 3.5**

**Property 9: UNIQUE Eliminates Duplicates**

*For any* collection, UNIQUE should return a collection where:
- No value appears more than once
- Every distinct value from the input appears exactly once in the output
- COUNT(UNIQUE(collection)) should be less than or equal to COUNT(collection)

**Validates: Requirements 3.6**

**Property 10: Arithmetic Commutativity**

*For any* numeric values A and B:
- ADD(A, B) should equal ADD(B, A)
- MULTIPLY(A, B) should equal MULTIPLY(B, A)

**Validates: Requirements 4.1, 4.3**

**Property 11: Arithmetic Associativity**

*For any* numeric values A, B, and C:
- ADD(ADD(A, B), C) should equal ADD(A, ADD(B, C))
- MULTIPLY(MULTIPLY(A, B), C) should equal MULTIPLY(A, MULTIPLY(B, C))

**Validates: Requirements 4.1, 4.3**

**Property 12: Subtraction and Addition Inverse**

*For any* numeric values A and B, ADD(SUBTRACT(A, B), B) should equal A.

**Validates: Requirements 4.1, 4.2**

**Property 13: Multiplication and Division Inverse**

*For any* numeric values A and non-zero B, MULTIPLY(DIVIDE(A, B), B) should equal A (within floating-point precision).

**Validates: Requirements 4.3, 4.4**

**Property 14: Division by Zero Handling**

*For any* numeric value A, DIVIDE(A, 0) should return an error or null, not crash the system.

**Validates: Requirements 4.5**

**Property 15: Type Safety for Arithmetic**

*For any* non-numeric inputs to arithmetic operations (ADD, SUBTRACT, MULTIPLY, DIVIDE, MOD), the DSL_Engine should return an error.

**Validates: Requirements 4.7**

**Property 16: ACTION_TIME Returns Event Timestamp**

*For any* event being evaluated, ACTION_TIME() should return the timestamp field of that specific event.

**Validates: Requirements 5.1**

**Property 17: DATE_FORMAT Produces Valid Format**

*For any* valid timestamp and format string, DATE_FORMAT should return a string that matches the specified format pattern.

**Validates: Requirements 5.2**

**Property 18: DATE_DIFF Unit Consistency**

*For any* two dates A and B, DATE_DIFF(A, B, D) should equal DATE_DIFF(A, B, H) divided by 24 (within rounding).

**Validates: Requirements 5.3**

**Property 19: Time Range Containment**

*For any* event with timestamp T, and time range defined by FROM(N1, U1) and TO(N2, U2), the event should be included in filtered results if and only if T falls within the calculated time range.

**Validates: Requirements 5.4, 5.5, 7.4**

**Property 20: Invalid Date Error Handling**

*For any* invalid date format or unparseable date string, date functions should return an error without crashing.

**Validates: Requirements 5.6**

**Property 21: Field Access Correctness**

*For any* user data and valid field name:
- PROFILE(field) should return the value at userData.profile[field]
- EVENT(field) should return the value at currentEvent[field]
- PARAM(field) should return the value at currentEvent.parameters[field]

**Validates: Requirements 6.1, 6.2, 6.3**

**Property 22: Non-Existent Field Returns Null**

*For any* field name that doesn't exist in the data structure, data access functions should return null or a default value, not throw an error.

**Validates: Requirements 6.4**

**Property 23: Filter Condition Correctness**

*For any* collection and boolean condition, IF(condition) and WHERE(condition) should return a collection where every item satisfies the condition.

**Validates: Requirements 7.1, 7.3**

**Property 24: Grouping Consistency**

*For any* collection and field name, BY(field) should group items such that all items in each group have the same value for the specified field.

**Validates: Requirements 7.2**

**Property 25: Filter Chaining Composition**

*For any* collection and two conditions C1 and C2, applying WHERE(C1) then WHERE(C2) should produce the same result as WHERE(AND(C1, C2)).

**Validates: Requirements 7.6**

**Property 26: String Contains Correctness**

*For any* strings S and substring T, CONTAINS(S, T) should return true if and only if T appears anywhere within S.

**Validates: Requirements 8.1**

**Property 27: String Prefix and Suffix**

*For any* strings S, prefix P, and suffix X:
- STARTS_WITH(S, P) should return true if and only if S begins with P
- ENDS_WITH(S, X) should return true if and only if S ends with X

**Validates: Requirements 8.2, 8.3**

**Property 28: Regex Match Correctness**

*For any* valid regex pattern R and string S, REGEX_MATCH(S, R) should return true if and only if S matches the pattern according to standard regex semantics.

**Validates: Requirements 8.4**

**Property 29: String Operation Null Safety**

*For any* string operation receiving null or non-string inputs, the operation should handle it gracefully (return false or error) without crashing.

**Validates: Requirements 8.5**

**Property 30: Bucket Assignment Correctness**

*For any* numeric value V and bucket definition with ranges, BUCKET(V, ranges) should return the label of the first range that contains V.

**Validates: Requirements 9.1, 9.3**

**Property 31: Bucket Boundary Semantics**

*For any* bucket range with boundaries, a value exactly equal to a boundary should be included or excluded based on the inclusive/exclusive flag.

**Validates: Requirements 9.4**

**Property 32: Parse Error Reporting**

*For any* syntactically invalid DSL expression, the parser should return an error message that includes the error location and type, not crash.

**Validates: Requirements 10.1**

**Property 33: Undefined Function Detection**

*For any* DSL expression referencing a function name not in the registry, the parser should return an error identifying the undefined function.

**Validates: Requirements 10.2**

**Property 34: Type Mismatch Detection**

*For any* DSL expression with type mismatches (e.g., ADD("string", 5)), the evaluator should return a type error with details about the mismatch.

**Validates: Requirements 10.3**

**Property 35: Runtime Error Safety**

*For any* runtime error during evaluation (division by zero, null reference, etc.), the evaluator should return an error result without crashing the process.

**Validates: Requirements 10.4**

**Property 36: Argument Validation**

*For any* function call with incorrect argument count or types, the validator should detect and report the error before evaluation.

**Validates: Requirements 10.5**

**Property 37: Expression Type Consistency**

*For any* valid DSL expression evaluated with user data, the result should be either a Boolean value or a computed value (number/string), never undefined or an unexpected type.

**Validates: Requirements 1.3**

**Property 38: Nested Evaluation Order**

*For any* DSL expression with nested function calls, inner functions should be evaluated before outer functions, following standard function composition rules.

**Validates: Requirements 1.4**

**Property 39: Case Sensitivity for Function Names**

*For any* DSL function, the UPPERCASE name should be recognized, and lowercase or mixed-case versions should be rejected with an error.

**Validates: Requirements 1.5**

## Error Handling

### Error Categories

1. **Syntax Errors**: Invalid DSL expression structure
   - Missing parentheses or brackets
   - Invalid function names
   - Malformed literals
   - **Handling**: Return ParseError with position and description

2. **Validation Errors**: Structurally valid but semantically incorrect
   - Undefined function references
   - Wrong argument count
   - Type mismatches
   - **Handling**: Return ValidationError before evaluation

3. **Runtime Errors**: Errors during expression evaluation
   - Division by zero
   - Null pointer access
   - Invalid date formats
   - Collection operation on non-collection
   - **Handling**: Return EvaluationError with context

4. **Data Errors**: Issues with input user data
   - Missing required fields
   - Invalid data types
   - Malformed timestamps
   - **Handling**: Return DataError with field information

### Error Response Format

```java
public class DSLError {
    private ErrorType type;
    private String message;
    private Integer position;  // For syntax errors
    private String expression;
    private Map<String, Object> context;
    
    public enum ErrorType {
        SYNTAX_ERROR,
        VALIDATION_ERROR,
        RUNTIME_ERROR,
        DATA_ERROR
    }
}
```

### Error Handling Principles

1. **Fail Fast**: Detect errors as early as possible (parse > validate > evaluate)
2. **Never Crash**: All errors should be caught and returned as error objects
3. **Descriptive Messages**: Include enough context to diagnose the issue
4. **Position Information**: For syntax errors, indicate where the error occurred
5. **Graceful Degradation**: Return null/default values for missing data when appropriate

### Example Error Messages

```
Syntax Error at position 15: Expected closing parenthesis, found ','
Validation Error: Function 'SUMM' is not defined. Did you mean 'SUM'?
Runtime Error: Division by zero in DIVIDE(COUNT(...), 0)
Type Error: ADD expects numeric arguments, got String and Number
Data Error: Field 'profile.age' does not exist in user data
```

## Testing Strategy

### Dual Testing Approach

This DSL requires both unit testing and property-based testing for comprehensive coverage:

**Unit Tests** focus on:
- Specific example expressions that demonstrate correct behavior
- Edge cases (empty collections, boundary values, null inputs)
- Error conditions (invalid syntax, type mismatches, runtime errors)
- Integration between parser, validator, and evaluator
- Specific complex queries from use cases

**Property-Based Tests** focus on:
- Universal properties that hold for all inputs (see Correctness Properties section)
- Algebraic laws (commutativity, associativity, identity)
- Round-trip properties (parse/print, serialize/deserialize)
- Invariants (type safety, error safety)
- Comprehensive input coverage through randomization

### Property-Based Testing Configuration

**Library Selection**: Use a property-based testing library appropriate for the implementation language:
- Java: jqwik or QuickTheories
- JavaScript/TypeScript: fast-check
- Python: Hypothesis

**Test Configuration**:
- Minimum 100 iterations per property test (due to randomization)
- Each property test must reference its design document property
- Tag format: `@Tag("Feature: user-segmentation-dsl, Property {number}: {property_text}")`

**Example Property Test Structure**:
```java
@Property
@Tag("Feature: user-segmentation-dsl, Property 2: Boolean Algebra Laws")
void testBooleanAlgebraCommutativity(@ForAll boolean a, @ForAll boolean b) {
    // Test AND(A, B) = AND(B, A)
    String expr1 = String.format("AND(%b, %b)", a, b);
    String expr2 = String.format("AND(%b, %b)", b, a);
    
    Object result1 = evaluator.evaluate(expr1, userData).getValue();
    Object result2 = evaluator.evaluate(expr2, userData).getValue();
    
    assertEquals(result1, result2);
}
```

### Test Data Generation

**For Property Tests**, generate:
- Random valid DSL expressions (using grammar-based generation)
- Random user data structures with varying profiles, visits, and events
- Random numeric values, strings, dates, and collections
- Random boolean combinations
- Edge cases: empty collections, null values, boundary values

**For Unit Tests**, use:
- Specific example expressions from requirements
- Known edge cases (empty strings, zero values, null)
- Real-world use case examples
- Complex nested expressions
- Error-inducing inputs

### Coverage Goals

1. **Function Coverage**: Every DSL function tested in isolation
2. **Integration Coverage**: Common function combinations tested
3. **Error Path Coverage**: All error types triggered and verified
4. **Property Coverage**: Each correctness property has a corresponding property test
5. **Use Case Coverage**: All example use cases from requirements implemented as tests

### Test Organization

```
tests/
├── unit/
│   ├── parser/
│   │   ├── ParserSyntaxTest.java
│   │   ├── ParserErrorTest.java
│   │   └── PrettyPrinterTest.java
│   ├── functions/
│   │   ├── LogicalFunctionsTest.java
│   │   ├── AggregationFunctionsTest.java
│   │   ├── MathFunctionsTest.java
│   │   ├── DateFunctionsTest.java
│   │   ├── StringFunctionsTest.java
│   │   └── DataAccessFunctionsTest.java
│   ├── evaluator/
│   │   ├── EvaluatorTest.java
│   │   └── ErrorHandlingTest.java
│   └── integration/
│       ├── ComplexQueryTest.java
│       └── UseCaseTest.java
└── property/
    ├── ParserPropertiesTest.java
    ├── LogicalPropertiesTest.java
    ├── ArithmeticPropertiesTest.java
    ├── AggregationPropertiesTest.java
    ├── DateTimePropertiesTest.java
    ├── FilteringPropertiesTest.java
    ├── StringPropertiesTest.java
    └── ErrorHandlingPropertiesTest.java
```

### Example Use Case Tests

```java
@Test
void testUsersWithMoreThanNPurchases() {
    String expression = """
        GT(
            COUNT(
                WHERE(EQ(EVENT("event_name"), "purchase")),
                FROM(365, D),
                TO(0, D)
            ),
            5
        )
    """;
    
    // Test with user who has 7 purchases
    UserData user1 = createUserWithPurchases(7);
    assertTrue(evaluator.evaluate(expression, user1).getValue());
    
    // Test with user who has 3 purchases
    UserData user2 = createUserWithPurchases(3);
    assertFalse(evaluator.evaluate(expression, user2).getValue());
}

@Test
void testActiveDaysCalculation() {
    String expression = """
        DIVIDE(
            COUNT(
                UNIQUE(
                    BY(DATE_FORMAT(ACTION_TIME(), "yyyy-MM-dd")),
                    IF(EQ(EVENT("event_type"), "action")),
                    FROM(29, D),
                    TO(0, D)
                )
            ),
            30
        )
    """;
    
    UserData user = createUserWithActivityPattern();
    Double result = (Double) evaluator.evaluate(expression, user).getValue();
    
    // Verify result is between 0 and 1
    assertTrue(result >= 0.0 && result <= 1.0);
}
```

## Implementation Notes

### AviatorScript Integration Details

1. **Custom Function Registration**: Each DSL function extends `AbstractFunction` and is registered with `AviatorEvaluatorInstance.addFunction()`

2. **Type Handling**: AviatorScript uses `AviatorObject` wrapper types:
   - `AviatorLong` for integers
   - `AviatorDouble` for decimals
   - `AviatorString` for strings
   - `AviatorBoolean` for booleans
   - `AviatorJavaType` for Java objects

3. **Context Passing**: User data is passed through the evaluation environment map

4. **Error Propagation**: Throw `ExpressionRuntimeException` for runtime errors, which AviatorScript will catch and wrap

### Performance Considerations

1. **Expression Caching**: Compile expressions once and reuse for multiple evaluations
2. **Lazy Evaluation**: Implement short-circuit evaluation for AND/OR operations
3. **Collection Streaming**: Use Java Streams for collection operations to enable lazy evaluation
4. **Time Range Optimization**: Pre-calculate time range boundaries once per evaluation
5. **Index Usage**: For large event collections, consider indexing by timestamp or event type

### Extension Points

The design supports future extensions:

1. **New Functions**: Add new DSL functions by implementing `DSLFunction` and registering
2. **Custom Aggregations**: Extend aggregation framework with new operations
3. **Data Source Plugins**: Support additional data sources beyond profile/visits/events
4. **Output Formats**: Add serialization formats for expression results
5. **Query Optimization**: Add query plan optimization layer before evaluation

## Appendix: Complete Function Reference

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
- `COUNT(collection)` - Count items
- `SUM(collection)` - Sum numeric values
- `AVG(collection)` - Average of numeric values
- `MIN(collection)` - Minimum value
- `MAX(collection)` - Maximum value
- `UNIQUE(collection)` - Distinct values only

### Mathematical Functions

**Basic Arithmetic**:
- `ADD(a, b)` - Addition
- `SUBTRACT(a, b)` - Subtraction
- `MULTIPLY(a, b)` - Multiplication
- `DIVIDE(a, b)` - Division
- `MOD(a, b)` - Modulo

**Advanced Math**:
- `ABS(n)` - Absolute value
- `ROUND(n, [decimals])` - Round to nearest integer or decimal places
- `CEIL(n)` - Round up to nearest integer
- `FLOOR(n)` - Round down to nearest integer
- `POW(base, exponent)` - Power/exponentiation
- `SQRT(n)` - Square root
- `LOG(n, [base])` - Logarithm (default base e)
- `EXP(n)` - e raised to power n

### Date/Time Functions

**Basic Date/Time**:
- `ACTION_TIME()` - Get current event timestamp
- `DATE_FORMAT(timestamp, format)` - Format date
- `DATE_DIFF(date1, date2, unit)` - Calculate difference
- `FROM(n, unit)` - Time range start
- `TO(n, unit)` - Time range end

**Extended Date/Time**:
- `NOW()` - Get current timestamp
- `WEEKDAY(timestamp)` - Get day of week (1=Monday, 7=Sunday)
- `IN_RECENT_DAYS(n)` - Check if event occurred in recent N days
- `IS_RECURRING(event_name, min_count, time_window)` - Check if event recurs
- `DAY_OF_MONTH(timestamp)` - Get day of month (1-31)
- `MONTH(timestamp)` - Get month (1-12)
- `YEAR(timestamp)` - Get year

### Data Access Functions
- `PROFILE(field)` - Access profile field
- `EVENT(field)` - Access event field
- `PARAM(field)` - Access event parameter

### Filtering Functions
- `IF(condition)` - Filter by condition
- `BY(field)` - Group by field
- `WHERE(condition)` - Filter collection

### String Functions

**String Matching**:
- `CONTAINS(string, substring)` - Check substring
- `STARTS_WITH(string, prefix)` - Check prefix
- `ENDS_WITH(string, suffix)` - Check suffix
- `REGEX_MATCH(string, pattern)` - Match regex

**String Manipulation**:
- `UPPER(string)` - Convert to uppercase
- `LOWER(string)` - Convert to lowercase
- `TRIM(string)` - Remove leading/trailing whitespace
- `SUBSTRING(string, start, [length])` - Extract substring
- `REPLACE(string, search, replacement)` - Replace occurrences
- `LENGTH(string)` - Get string length
- `CONCAT(str1, str2, ...)` - Concatenate strings
- `SPLIT(string, delimiter)` - Split into array

### Conversion Functions
- `TO_NUMBER(value)` - Convert to number
- `TO_STRING(value)` - Convert to string
- `TO_BOOLEAN(value)` - Convert to boolean
- `CONVERT_UNIT(value, from_unit, to_unit)` - Convert between units
  - Supported units: seconds, minutes, hours, days, weeks, months, years
  - Distance: meters, kilometers, miles, feet
  - Weight: grams, kilograms, pounds, ounces

### Segmentation Functions
- `BUCKET(value, ranges)` - Assign to bucket

### Example Usage

**Calculate active days in recent 30 days**:
```
DIVIDE(
  COUNT(
    UNIQUE(
      BY(DATE_FORMAT(ACTION_TIME(), "yyyy-MM-dd")),
      IF(EQ(EVENT("event_type"), "action")),
      IN_RECENT_DAYS(30)
    )
  ),
  30
)
```

**Check if user is a recurring purchaser**:
```
IS_RECURRING("purchase", 3, FROM(90, D))
```

**Segment by purchase amount with unit conversion**:
```
BUCKET(
  CONVERT_UNIT(SUM(PARAM("amount")), "cents", "dollars"),
  [[0, 10], [10, 100], [100, 500], [500, 5000]]
)
```

**Filter weekend events**:
```
WHERE(
  OR(
    EQ(WEEKDAY(ACTION_TIME()), 6),
    EQ(WEEKDAY(ACTION_TIME()), 7)
  )
)
```

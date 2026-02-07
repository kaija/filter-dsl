# Date/Time Functions Implementation Summary

## Task 8.1: Basic Date/Time Functions

This document summarizes the implementation of the five basic date/time functions for the User Segmentation DSL.

## Implemented Functions

### 1. ACTION_TIME()
**Location**: `src/main/java/com/example/dsl/functions/datetime/ActionTimeFunction.java`

**Purpose**: Returns the timestamp of the current event being evaluated.

**Signature**: `ACTION_TIME()` (no arguments)

**Returns**: String (ISO-8601 timestamp) or null if no current event

**Example**:
```java
// With event context
Event event = Event.builder()
    .timestamp("2023-01-15T10:30:00Z")
    .build();
env.put("currentEvent", event);

ACTION_TIME() → "2023-01-15T10:30:00Z"
```

**Key Features**:
- Retrieves timestamp from the current event in the evaluation context
- Returns null if no current event is set
- Returns null if event has no timestamp

---

### 2. DATE_FORMAT(timestamp, format)
**Location**: `src/main/java/com/example/dsl/functions/datetime/DateFormatFunction.java`

**Purpose**: Formats a timestamp according to a specified format pattern.

**Signature**: `DATE_FORMAT(timestamp: String, format: String)`

**Returns**: String (formatted date) or null if inputs are null

**Examples**:
```java
DATE_FORMAT("2023-01-15T10:30:00Z", "yyyy-MM-dd") → "2023-01-15"
DATE_FORMAT("2023-01-15T10:30:00Z", "HH:mm:ss") → "10:30:00"
DATE_FORMAT("2023-01-15T10:30:00Z", "MM/dd/yyyy") → "01/15/2023"
```

**Key Features**:
- Uses Java DateTimeFormatter patterns
- Parses ISO-8601 timestamps
- Formats in UTC timezone
- Throws TypeMismatchException for invalid timestamps or patterns

**Common Format Patterns**:
- `yyyy`: 4-digit year
- `MM`: 2-digit month
- `dd`: 2-digit day
- `HH`: 2-digit hour (24-hour)
- `mm`: 2-digit minute
- `ss`: 2-digit second
- `EEEE`: full day name
- `MMMM`: full month name

---

### 3. DATE_DIFF(date1, date2, unit)
**Location**: `src/main/java/com/example/dsl/functions/datetime/DateDiffFunction.java`

**Purpose**: Calculates the difference between two dates in the specified unit.

**Signature**: `DATE_DIFF(date1: String, date2: String, unit: String)`

**Returns**: Long (difference in specified unit)

**Examples**:
```java
DATE_DIFF("2023-01-20T00:00:00Z", "2023-01-15T00:00:00Z", "D") → 5
DATE_DIFF("2023-01-15T12:00:00Z", "2023-01-15T10:00:00Z", "H") → 2
DATE_DIFF("2023-01-15T10:30:00Z", "2023-01-15T10:00:00Z", "M") → 30
```

**Supported Units**:
- `D`: Days
- `H`: Hours
- `M`: Minutes
- `W`: Weeks
- `MO`: Months (approximate, 30 days)
- `Y`: Years (approximate, 365 days)

**Key Features**:
- Result is date1 - date2 (positive if date1 is after date2)
- Parses ISO-8601 timestamps
- Uses Java Duration for precise calculations
- Throws TypeMismatchException for invalid timestamps or units

---

### 4. FROM(n, unit)
**Location**: `src/main/java/com/example/dsl/functions/datetime/FromFunction.java`

**Purpose**: Defines the start of a relative time range.

**Signature**: `FROM(n: Number, unit: String)`

**Returns**: TimeRange object (stored in evaluation context)

**Examples**:
```java
FROM(30, "D")  → 30 days ago from now
FROM(7, "W")   → 7 weeks ago from now
FROM(12, "MO") → 12 months ago from now
```

**Supported Units**:
- `D`: Days
- `H`: Hours
- `M`: Minutes
- `W`: Weeks
- `MO`: Months
- `Y`: Years

**Key Features**:
- Creates or updates TimeRange in evaluation context
- Can be used with TO() to define complete time range
- The FROM value represents how far back in time to start the range
- Typically used with filtering functions like WHERE() or IF()

---

### 5. TO(n, unit)
**Location**: `src/main/java/com/example/dsl/functions/datetime/ToFunction.java`

**Purpose**: Defines the end of a relative time range.

**Signature**: `TO(n: Number, unit: String)`

**Returns**: TimeRange object (stored in evaluation context)

**Examples**:
```java
TO(0, "D")  → now (0 days ago)
TO(7, "D")  → 7 days ago from now
TO(1, "H")  → 1 hour ago from now
```

**Supported Units**:
- `D`: Days
- `H`: Hours
- `M`: Minutes
- `W`: Weeks
- `MO`: Months
- `Y`: Years

**Key Features**:
- Creates or updates TimeRange in evaluation context
- Can be used with FROM() to define complete time range
- The TO value represents how far back in time to end the range
- Common usage: `FROM(30, "D"), TO(0, "D")` means "from 30 days ago to now"

---

## Test Coverage

### Unit Tests
**Location**: `src/test/java/com/example/dsl/unit/functions/DateTimeFunctionsTest.java`

**Total Tests**: 45 tests covering:
- Basic functionality for each function
- Edge cases (null inputs, invalid formats, etc.)
- Error handling (invalid timestamps, invalid units, wrong argument counts)
- Integration between functions
- Metadata validation

**Test Categories**:
- ACTION_TIME: 5 tests
- DATE_FORMAT: 10 tests
- DATE_DIFF: 13 tests
- FROM: 7 tests
- TO: 7 tests
- Integration: 3 tests

### Registration Tests
**Location**: `src/test/java/com/example/dsl/unit/functions/DateTimeFunctionsRegistrationTest.java`

**Total Tests**: 10 tests covering:
- Function registration with FunctionRegistry
- Integration with AviatorScript
- Combined expressions
- Variable usage
- Metadata accessibility

---

## Implementation Details

### Design Patterns Used

1. **Template Method Pattern**: All functions extend `DSLFunction` base class
2. **Builder Pattern**: FunctionMetadata uses builder pattern
3. **Context Pattern**: Functions access shared evaluation context for user data and time ranges

### Key Technologies

- **Java 8+ Time API**: Uses `Instant`, `Duration`, `DateTimeFormatter`
- **AviatorScript**: Custom function integration via `AbstractFunction`
- **JUnit 5**: Unit testing framework

### Error Handling

All functions implement robust error handling:
- **TypeMismatchException**: For invalid input types or formats
- **IllegalArgumentException**: For wrong argument counts (via AviatorScript)
- **Null Safety**: Functions handle null inputs gracefully

### Code Organization

```
src/main/java/com/example/dsl/functions/datetime/
├── ActionTimeFunction.java
├── DateFormatFunction.java
├── DateDiffFunction.java
├── FromFunction.java
└── ToFunction.java

src/test/java/com/example/dsl/unit/functions/
├── DateTimeFunctionsTest.java
└── DateTimeFunctionsRegistrationTest.java
```

---

## Usage Examples

### Example 1: Format Event Timestamp
```java
// Get event timestamp and format it
DATE_FORMAT(ACTION_TIME(), "yyyy-MM-dd")
```

### Example 2: Calculate Days Since Event
```java
// Calculate days between two dates
DATE_DIFF(NOW(), ACTION_TIME(), "D")
```

### Example 3: Filter Events in Time Range
```java
// Filter events from the past 30 days
WHERE(
    FROM(30, "D"),
    TO(0, "D"),
    EQ(EVENT("event_name"), "purchase")
)
```

### Example 4: Complex Date Formatting
```java
// Format event timestamp with custom pattern
DATE_FORMAT(ACTION_TIME(), "EEEE, MMMM d, yyyy 'at' HH:mm")
// → "Sunday, January 15, 2023 at 10:30"
```

---

## Requirements Validation

This implementation satisfies the following requirements from the design document:

- **Requirement 5.1**: ACTION_TIME returns event timestamp ✓
- **Requirement 5.2**: DATE_FORMAT formats timestamps with patterns ✓
- **Requirement 5.3**: DATE_DIFF calculates differences in specified units ✓
- **Requirement 5.4**: FROM defines start of relative time range ✓
- **Requirement 5.5**: TO defines end of relative time range ✓
- **Requirement 5.13**: Invalid date formats return errors ✓
- **Requirement 5.14**: All time units (D, H, M, W, MO, Y) are supported ✓
- **Requirement 16.5**: Each function in its own Java file ✓

---

## Test Results

All tests passing:
- **Unit Tests**: 45/45 ✓
- **Registration Tests**: 10/10 ✓
- **Total Project Tests**: 657/657 ✓

---

## Next Steps

The following date/time functions remain to be implemented in task 8.2:
- NOW() - Get current timestamp
- WEEKDAY() - Get day of week (1-7)
- IN_RECENT_DAYS() - Filter events from past N days
- IS_RECURRING() - Check event frequency
- DAY_OF_MONTH() - Extract day of month
- MONTH() - Extract month
- YEAR() - Extract year

---

## Notes

1. **Time Zone Handling**: All date formatting uses UTC timezone
2. **Approximate Units**: Months (30 days) and Years (365 days) are approximations
3. **ISO-8601 Format**: All timestamp inputs must be in ISO-8601 format
4. **Context Dependency**: ACTION_TIME requires a current event in the evaluation context
5. **Time Range Context**: FROM and TO functions update a shared TimeRange in the evaluation context

---

## Extensibility

The implementation follows the DSL's extensibility principles:
- Each function is in its own file
- Functions extend the DSLFunction base class
- Functions are registered with FunctionRegistry
- Metadata is provided for validation
- Helper methods from base class are utilized

Adding new date/time functions follows the same pattern established by these five functions.

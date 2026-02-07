# DSL Function Reference

Complete reference for all built-in DSL functions.

## Table of Contents

1. [Logical Functions](#logical-functions)
2. [Comparison Functions](#comparison-functions)
3. [Aggregation Functions](#aggregation-functions)
4. [Mathematical Functions](#mathematical-functions)
5. [Date/Time Functions](#datetime-functions)
6. [Data Access Functions](#data-access-functions)
7. [Filtering Functions](#filtering-functions)
8. [String Functions](#string-functions)
9. [Conversion Functions](#conversion-functions)
10. [Segmentation Functions](#segmentation-functions)

## Function Notation

Each function is documented with:
- **Syntax**: Function signature with parameter types
- **Description**: What the function does
- **Parameters**: Detailed parameter descriptions
- **Returns**: Return type and value
- **Examples**: Usage examples
- **Notes**: Important considerations

**Parameter Types:**
- `Boolean` - true/false value
- `Number` - Integer or decimal number
- `String` - Text value
- `Collection` - List of items
- `Any` - Any type

## Logical Functions

### AND

Logical AND operation - returns true only if all arguments are true.

**Syntax:** `AND(expr1, expr2, ...)`

**Parameters:**
- `expr1, expr2, ...` (Boolean) - Two or more boolean expressions

**Returns:** Boolean - true if all arguments are true, false otherwise

**Examples:**
```
AND(true, true)                                    → true
AND(true, false)                                   → false
AND(EQ(PROFILE("country"), "US"), GT(COUNT(userData.events), 10))  → true/false
AND(true, true, true, false)                       → false
```

**Notes:**
- Short-circuits: stops evaluating once a false value is found
- Requires at least 2 arguments

### OR

Logical OR operation - returns true if any argument is true.

**Syntax:** `OR(expr1, expr2, ...)`

**Parameters:**
- `expr1, expr2, ...` (Boolean) - Two or more boolean expressions

**Returns:** Boolean - true if any argument is true, false otherwise

**Examples:**
```
OR(true, false)                                    → true
OR(false, false)                                   → false
OR(EQ(PROFILE("country"), "US"), EQ(PROFILE("country"), "UK"))  → true/false
OR(false, false, false, true)                      → true
```

**Notes:**
- Short-circuits: stops evaluating once a true value is found
- Requires at least 2 arguments

### NOT

Logical NOT operation - returns the opposite boolean value.

**Syntax:** `NOT(expr)`

**Parameters:**
- `expr` (Boolean) - Boolean expression to negate

**Returns:** Boolean - opposite of the input value

**Examples:**
```
NOT(true)                                          → false
NOT(false)                                         → true
NOT(EQ(PROFILE("country"), "US"))                  → true/false
NOT(AND(true, false))                              → true
```

**Notes:**
- Requires exactly 1 argument
- Double negation: `NOT(NOT(x))` equals `x`

## Comparison Functions

### GT (Greater Than)

Checks if first value is greater than second value.

**Syntax:** `GT(a, b)`

**Parameters:**
- `a` (Number) - First value
- `b` (Number) - Second value

**Returns:** Boolean - true if a > b

**Examples:**
```
GT(10, 5)                                          → true
GT(5, 10)                                          → false
GT(5, 5)                                           → false
GT(COUNT(userData.events), 100)                    → true/false
```

### LT (Less Than)

Checks if first value is less than second value.

**Syntax:** `LT(a, b)`

**Parameters:**
- `a` (Number) - First value
- `b` (Number) - Second value

**Returns:** Boolean - true if a < b

**Examples:**
```
LT(5, 10)                                          → true
LT(10, 5)                                          → false
LT(5, 5)                                           → false
```

### GTE (Greater Than or Equal)

Checks if first value is greater than or equal to second value.

**Syntax:** `GTE(a, b)`

**Parameters:**
- `a` (Number) - First value
- `b` (Number) - Second value

**Returns:** Boolean - true if a >= b

**Examples:**
```
GTE(10, 5)                                         → true
GTE(5, 5)                                          → true
GTE(5, 10)                                         → false
```

### LTE (Less Than or Equal)

Checks if first value is less than or equal to second value.

**Syntax:** `LTE(a, b)`

**Parameters:**
- `a` (Number) - First value
- `b` (Number) - Second value

**Returns:** Boolean - true if a <= b

**Examples:**
```
LTE(5, 10)                                         → true
LTE(5, 5)                                          → true
LTE(10, 5)                                         → false
```

### EQ (Equals)

Checks if two values are equal.

**Syntax:** `EQ(a, b)`

**Parameters:**
- `a` (Any) - First value
- `b` (Any) - Second value

**Returns:** Boolean - true if values are equal

**Examples:**
```
EQ(5, 5)                                           → true
EQ(5, 10)                                          → false
EQ("hello", "hello")                               → true
EQ(PROFILE("country"), "US")                       → true/false
```

**Notes:**
- Works with numbers, strings, and booleans
- Case-sensitive for strings

### NEQ (Not Equals)

Checks if two values are not equal.

**Syntax:** `NEQ(a, b)`

**Parameters:**
- `a` (Any) - First value
- `b` (Any) - Second value

**Returns:** Boolean - true if values are not equal

**Examples:**
```
NEQ(5, 10)                                         → true
NEQ(5, 5)                                          → false
NEQ("hello", "world")                              → true
```

**Notes:**
- Equivalent to `NOT(EQ(a, b))`

## Aggregation Functions

### COUNT

Returns the number of items in a collection.

**Syntax:** `COUNT(collection)`

**Parameters:**
- `collection` (Collection) - Collection to count

**Returns:** Number - count of items (0 for empty collections)

**Examples:**
```
COUNT(userData.events)                             → 42
COUNT(WHERE(userData.events, EQ(EVENT("eventName"), "purchase")))  → 5
COUNT([])                                          → 0
```

### SUM

Returns the sum of numeric values in a collection.

**Syntax:** `SUM(collection)`

**Parameters:**
- `collection` (Collection<Number>) - Collection of numbers

**Returns:** Number - sum of all values (0 for empty collections)

**Examples:**
```
SUM([1, 2, 3, 4, 5])                              → 15
SUM(PARAM("amount"))                               → 1234.56
SUM([])                                            → 0
```

### AVG

Returns the average of numeric values in a collection.

**Syntax:** `AVG(collection)`

**Parameters:**
- `collection` (Collection<Number>) - Collection of numbers

**Returns:** Number - average value (null for empty collections)

**Examples:**
```
AVG([1, 2, 3, 4, 5])                              → 3.0
AVG([10, 20, 30])                                  → 20.0
AVG([])                                            → null
```

### MIN

Returns the minimum value from a collection.

**Syntax:** `MIN(collection)`

**Parameters:**
- `collection` (Collection<Comparable>) - Collection of comparable values

**Returns:** Any - minimum value (null for empty collections)

**Examples:**
```
MIN([5, 2, 8, 1, 9])                              → 1
MIN([10.5, 3.2, 7.8])                             → 3.2
MIN([])                                            → null
```

### MAX

Returns the maximum value from a collection.

**Syntax:** `MAX(collection)`

**Parameters:**
- `collection` (Collection<Comparable>) - Collection of comparable values

**Returns:** Any - maximum value (null for empty collections)

**Examples:**
```
MAX([5, 2, 8, 1, 9])                              → 9
MAX([10.5, 3.2, 7.8])                             → 10.5
MAX([])                                            → null
```

### UNIQUE

Returns only distinct values from a collection, removing duplicates.

**Syntax:** `UNIQUE(collection)`

**Parameters:**
- `collection` (Collection) - Collection with potential duplicates

**Returns:** Collection - collection with only unique values

**Examples:**
```
UNIQUE([1, 2, 2, 3, 3, 3])                        → [1, 2, 3]
UNIQUE(["a", "b", "a", "c"])                      → ["a", "b", "c"]
COUNT(UNIQUE(BY(DATE_FORMAT(ACTION_TIME(), "yyyy-MM-dd"))))  → unique days
```

**Notes:**
- Preserves order of first occurrence
- Useful for counting distinct values

## Mathematical Functions

### Basic Arithmetic

#### ADD

Returns the sum of two numbers.

**Syntax:** `ADD(a, b)`

**Parameters:**
- `a` (Number) - First number
- `b` (Number) - Second number

**Returns:** Number - sum of a and b

**Examples:**
```
ADD(5, 3)                                          → 8
ADD(10.5, 2.3)                                     → 12.8
ADD(-5, 10)                                        → 5
```

#### SUBTRACT

Returns the difference of two numbers.

**Syntax:** `SUBTRACT(a, b)`

**Parameters:**
- `a` (Number) - First number
- `b` (Number) - Second number

**Returns:** Number - a minus b

**Examples:**
```
SUBTRACT(10, 3)                                    → 7
SUBTRACT(5.5, 2.2)                                 → 3.3
SUBTRACT(0, 5)                                     → -5
```

#### MULTIPLY

Returns the product of two numbers.

**Syntax:** `MULTIPLY(a, b)`

**Parameters:**
- `a` (Number) - First number
- `b` (Number) - Second number

**Returns:** Number - product of a and b

**Examples:**
```
MULTIPLY(5, 3)                                     → 15
MULTIPLY(2.5, 4)                                   → 10.0
MULTIPLY(-2, 3)                                    → -6
```

#### DIVIDE

Returns the quotient of two numbers.

**Syntax:** `DIVIDE(a, b)`

**Parameters:**
- `a` (Number) - Numerator
- `b` (Number) - Denominator (must not be zero)

**Returns:** Number - a divided by b

**Examples:**
```
DIVIDE(10, 2)                                      → 5.0
DIVIDE(7, 2)                                       → 3.5
DIVIDE(1, 3)                                       → 0.333...
```

**Notes:**
- Division by zero returns an error
- Always returns a decimal (double) value

#### MOD

Returns the remainder of division.

**Syntax:** `MOD(a, b)`

**Parameters:**
- `a` (Number) - Dividend
- `b` (Number) - Divisor

**Returns:** Number - remainder of a divided by b

**Examples:**
```
MOD(10, 3)                                         → 1
MOD(15, 4)                                         → 3
MOD(10, 5)                                         → 0
```

### Advanced Math

#### ABS

Returns the absolute value of a number.

**Syntax:** `ABS(n)`

**Parameters:**
- `n` (Number) - Input number

**Returns:** Number - absolute value (always non-negative)

**Examples:**
```
ABS(-5)                                            → 5
ABS(5)                                             → 5
ABS(0)                                             → 0
ABS(-3.14)                                         → 3.14
```

#### ROUND

Rounds a number to the nearest integer or specified decimal places.

**Syntax:** `ROUND(n, [decimals])`

**Parameters:**
- `n` (Number) - Number to round
- `decimals` (Number, optional) - Number of decimal places (default: 0)

**Returns:** Number - rounded value

**Examples:**
```
ROUND(3.7)                                         → 4
ROUND(3.2)                                         → 3
ROUND(3.14159, 2)                                  → 3.14
ROUND(3.14159, 0)                                  → 3
```

#### CEIL

Rounds a number up to the nearest integer.

**Syntax:** `CEIL(n)`

**Parameters:**
- `n` (Number) - Number to round up

**Returns:** Number - smallest integer >= n

**Examples:**
```
CEIL(3.1)                                          → 4
CEIL(3.9)                                          → 4
CEIL(-3.1)                                         → -3
CEIL(5)                                            → 5
```

#### FLOOR

Rounds a number down to the nearest integer.

**Syntax:** `FLOOR(n)`

**Parameters:**
- `n` (Number) - Number to round down

**Returns:** Number - largest integer <= n

**Examples:**
```
FLOOR(3.9)                                         → 3
FLOOR(3.1)                                         → 3
FLOOR(-3.1)                                        → -4
FLOOR(5)                                           → 5
```

#### POW

Raises a number to a power.

**Syntax:** `POW(base, exponent)`

**Parameters:**
- `base` (Number) - Base number
- `exponent` (Number) - Exponent

**Returns:** Number - base raised to exponent

**Examples:**
```
POW(2, 3)                                          → 8
POW(10, 2)                                         → 100
POW(5, 0)                                          → 1
POW(2, -1)                                         → 0.5
```

#### SQRT

Returns the square root of a number.

**Syntax:** `SQRT(n)`

**Parameters:**
- `n` (Number) - Non-negative number

**Returns:** Number - square root of n

**Examples:**
```
SQRT(9)                                            → 3.0
SQRT(16)                                           → 4.0
SQRT(2)                                            → 1.414...
SQRT(0)                                            → 0
```

**Notes:**
- Negative inputs return an error

#### LOG

Returns the logarithm of a number.

**Syntax:** `LOG(n, [base])`

**Parameters:**
- `n` (Number) - Positive number
- `base` (Number, optional) - Logarithm base (default: e)

**Returns:** Number - logarithm of n

**Examples:**
```
LOG(10)                                            → 2.302... (natural log)
LOG(100, 10)                                       → 2.0 (log base 10)
LOG(8, 2)                                          → 3.0 (log base 2)
```

#### EXP

Returns e raised to a power.

**Syntax:** `EXP(n)`

**Parameters:**
- `n` (Number) - Exponent

**Returns:** Number - e^n

**Examples:**
```
EXP(0)                                             → 1.0
EXP(1)                                             → 2.718... (e)
EXP(2)                                             → 7.389...
```

## Date/Time Functions

### Basic Date/Time

#### ACTION_TIME

Returns the timestamp of the current event being evaluated.

**Syntax:** `ACTION_TIME()`

**Parameters:** None

**Returns:** String - ISO 8601 timestamp

**Examples:**
```
ACTION_TIME()                                      → "2024-01-15T10:30:00Z"
DATE_FORMAT(ACTION_TIME(), "yyyy-MM-dd")          → "2024-01-15"
```

**Notes:**
- Only works within event context
- Returns the timestamp of the event being processed

#### DATE_FORMAT

Formats a timestamp according to a specified pattern.

**Syntax:** `DATE_FORMAT(timestamp, format)`

**Parameters:**
- `timestamp` (String) - ISO 8601 timestamp
- `format` (String) - Format pattern (Java DateTimeFormatter)

**Returns:** String - formatted date string

**Examples:**
```
DATE_FORMAT("2024-01-15T10:30:00Z", "yyyy-MM-dd")           → "2024-01-15"
DATE_FORMAT("2024-01-15T10:30:00Z", "HH:mm:ss")             → "10:30:00"
DATE_FORMAT("2024-01-15T10:30:00Z", "EEEE, MMMM d, yyyy")  → "Monday, January 15, 2024"
```

**Common Format Patterns:**
- `yyyy` - 4-digit year
- `MM` - 2-digit month
- `dd` - 2-digit day
- `HH` - 2-digit hour (24-hour)
- `mm` - 2-digit minute
- `ss` - 2-digit second
- `EEEE` - full day name
- `MMMM` - full month name

#### DATE_DIFF

Calculates the difference between two dates in specified units.

**Syntax:** `DATE_DIFF(date1, date2, unit)`

**Parameters:**
- `date1` (String) - First timestamp (ISO 8601)
- `date2` (String) - Second timestamp (ISO 8601)
- `unit` (String) - Time unit: "D" (days), "H" (hours), "M" (minutes), "W" (weeks), "MO" (months), "Y" (years)

**Returns:** Number - difference in specified units

**Examples:**
```
DATE_DIFF("2024-01-15T00:00:00Z", "2024-01-10T00:00:00Z", "D")  → 5
DATE_DIFF("2024-01-15T12:00:00Z", "2024-01-15T10:00:00Z", "H")  → 2
DATE_DIFF("2024-06-01T00:00:00Z", "2024-01-01T00:00:00Z", "MO") → 5
```

#### FROM

Defines the start of a relative time range (N units ago).

**Syntax:** `FROM(n, unit)`

**Parameters:**
- `n` (Number) - Number of units
- `unit` (String) - Time unit: "D", "H", "M", "W", "MO", "Y"

**Returns:** TimeRange start marker

**Examples:**
```
FROM(30, "D")                                      → 30 days ago
FROM(7, "W")                                       → 7 weeks ago
FROM(1, "Y")                                       → 1 year ago
```

**Notes:**
- Used with WHERE/IF to filter events by time range
- Typically paired with TO()

#### TO

Defines the end of a relative time range (N units ago).

**Syntax:** `TO(n, unit)`

**Parameters:**
- `n` (Number) - Number of units
- `unit` (String) - Time unit: "D", "H", "M", "W", "MO", "Y"

**Returns:** TimeRange end marker

**Examples:**
```
TO(0, "D")                                         → now
TO(7, "D")                                         → 7 days ago
```

**Notes:**
- Used with WHERE/IF to filter events by time range
- Typically paired with FROM()

#### NOW

Returns the current timestamp.

**Syntax:** `NOW()`

**Parameters:** None

**Returns:** String - current ISO 8601 timestamp

**Examples:**
```
NOW()                                              → "2024-01-15T14:30:00Z"
DATE_DIFF(NOW(), ACTION_TIME(), "D")              → days since event
```

### Extended Date/Time

#### WEEKDAY

Returns the day of the week (1=Monday, 7=Sunday).

**Syntax:** `WEEKDAY(timestamp)`

**Parameters:**
- `timestamp` (String) - ISO 8601 timestamp

**Returns:** Number - day of week (1-7)

**Examples:**
```
WEEKDAY("2024-01-15T10:00:00Z")                   → 1 (Monday)
WEEKDAY("2024-01-20T10:00:00Z")                   → 6 (Saturday)
WEEKDAY("2024-01-21T10:00:00Z")                   → 7 (Sunday)
```

**Day Values:**
- 1 = Monday
- 2 = Tuesday
- 3 = Wednesday
- 4 = Thursday
- 5 = Friday
- 6 = Saturday
- 7 = Sunday

#### IN_RECENT_DAYS

Checks if an event occurred in the recent N days.

**Syntax:** `IN_RECENT_DAYS(n)`

**Parameters:**
- `n` (Number) - Number of days

**Returns:** Boolean - true if event is within N days

**Examples:**
```
IN_RECENT_DAYS(30)                                 → true/false
WHERE(userData.events, IN_RECENT_DAYS(7))         → events in past week
```

#### IS_RECURRING

Checks if an event occurs at least N times within a time window.

**Syntax:** `IS_RECURRING(eventName, minCount, timeWindow)`

**Parameters:**
- `eventName` (String) - Name of event to check
- `minCount` (Number) - Minimum occurrences required
- `timeWindow` (Number) - Time window in days

**Returns:** Boolean - true if event recurs enough times

**Examples:**
```
IS_RECURRING("login", 3, 90)                       → true if 3+ logins in 90 days
IS_RECURRING("purchase", 5, 365)                   → true if 5+ purchases in year
```

#### DAY_OF_MONTH

Returns the day of the month (1-31).

**Syntax:** `DAY_OF_MONTH(timestamp)`

**Parameters:**
- `timestamp` (String) - ISO 8601 timestamp

**Returns:** Number - day of month (1-31)

**Examples:**
```
DAY_OF_MONTH("2024-01-15T10:00:00Z")              → 15
DAY_OF_MONTH("2024-12-31T10:00:00Z")              → 31
```

#### MONTH

Returns the month (1-12).

**Syntax:** `MONTH(timestamp)`

**Parameters:**
- `timestamp` (String) - ISO 8601 timestamp

**Returns:** Number - month (1-12)

**Examples:**
```
MONTH("2024-01-15T10:00:00Z")                      → 1
MONTH("2024-12-15T10:00:00Z")                      → 12
```

#### YEAR

Returns the year.

**Syntax:** `YEAR(timestamp)`

**Parameters:**
- `timestamp` (String) - ISO 8601 timestamp

**Returns:** Number - year

**Examples:**
```
YEAR("2024-01-15T10:00:00Z")                       → 2024
YEAR("2023-12-31T10:00:00Z")                       → 2023
```

## Data Access Functions

### PROFILE

Accesses a field from the user profile.

**Syntax:** `PROFILE(field)`

**Parameters:**
- `field` (String) - Profile field name

**Returns:** Any - field value (or null if not found)

**Examples:**
```
PROFILE("country")                                 → "US"
PROFILE("city")                                    → "New York"
PROFILE("os")                                      → "Windows"
EQ(PROFILE("country"), "US")                       → true/false
```

**Available Fields:**
- `uuid`, `country`, `city`, `language`, `continent`, `timezone`
- `os`, `browser`, `device`, `screen`

### EVENT

Accesses a field from the current event.

**Syntax:** `EVENT(field)`

**Parameters:**
- `field` (String) - Event field name

**Returns:** Any - field value (or null if not found)

**Examples:**
```
EVENT("eventName")                                 → "purchase"
EVENT("eventType")                                 → "action"
EVENT("timestamp")                                 → "2024-01-15T10:30:00Z"
EQ(EVENT("eventName"), "purchase")                 → true/false
```

**Available Fields:**
- `uuid`, `eventName`, `eventType`, `timestamp`, `duration`
- `integration`, `app`, `platform`, `isHttps`
- `isFirstInVisit`, `isLastInVisit`, `isFirstEvent`, `isCurrent`, `triggerable`

### PARAM

Accesses a parameter from the current event's parameters map.

**Syntax:** `PARAM(name)`

**Parameters:**
- `name` (String) - Parameter name

**Returns:** Any - parameter value (or null if not found)

**Examples:**
```
PARAM("amount")                                    → 99.99
PARAM("product_id")                                → "prod-123"
PARAM("utm_campaign")                              → "summer_sale"
GT(PARAM("amount"), 100)                           → true/false
```

**Notes:**
- Parameters are custom data attached to events
- Returns null if parameter doesn't exist

## Filtering Functions

### IF

Filters events based on a boolean condition.

**Syntax:** `IF(condition)`

**Parameters:**
- `condition` (Boolean) - Filtering condition

**Returns:** Collection - filtered events

**Examples:**
```
IF(EQ(EVENT("eventName"), "purchase"))             → only purchase events
IF(GT(PARAM("amount"), 100))                       → events with amount > 100
COUNT(IF(EQ(EVENT("eventType"), "action")))        → count of action events
```

**Notes:**
- Filters the events collection
- Can be combined with time ranges (FROM/TO)

### WHERE

Filters a collection based on a boolean condition.

**Syntax:** `WHERE(collection, condition)`

**Parameters:**
- `collection` (Collection) - Collection to filter
- `condition` (Boolean) - Filtering condition

**Returns:** Collection - filtered collection

**Examples:**
```
WHERE(userData.events, EQ(EVENT("eventName"), "purchase"))  → purchase events
WHERE(userData.events, GT(PARAM("amount"), 100))            → high-value events
COUNT(WHERE(userData.events, IN_RECENT_DAYS(30)))           → recent event count
```

**Notes:**
- More explicit than IF
- Can filter any collection, not just events

### BY

Groups collection items by a field value.

**Syntax:** `BY(field)`

**Parameters:**
- `field` (String) - Field name to group by

**Returns:** Collection - grouped values

**Examples:**
```
BY(EVENT("eventName"))                             → ["purchase", "login", "view"]
BY(DATE_FORMAT(ACTION_TIME(), "yyyy-MM-dd"))      → ["2024-01-15", "2024-01-16"]
COUNT(UNIQUE(BY(EVENT("eventName"))))              → count of unique event types
```

**Notes:**
- Often used with UNIQUE to get distinct values
- Useful for grouping and counting

## String Functions

### String Matching

#### CONTAINS

Checks if a string contains a substring.

**Syntax:** `CONTAINS(string, substring)`

**Parameters:**
- `string` (String) - String to search in
- `substring` (String) - Substring to find

**Returns:** Boolean - true if substring is found

**Examples:**
```
CONTAINS("hello world", "world")                   → true
CONTAINS("hello world", "foo")                     → false
CONTAINS(PROFILE("city"), "New")                   → true for "New York"
```

**Notes:**
- Case-sensitive
- Empty substring always returns true

#### STARTS_WITH

Checks if a string starts with a prefix.

**Syntax:** `STARTS_WITH(string, prefix)`

**Parameters:**
- `string` (String) - String to check
- `prefix` (String) - Prefix to match

**Returns:** Boolean - true if string starts with prefix

**Examples:**
```
STARTS_WITH("hello world", "hello")                → true
STARTS_WITH("hello world", "world")                → false
STARTS_WITH(PROFILE("email"), "admin")             → true for "admin@..."
```

#### ENDS_WITH

Checks if a string ends with a suffix.

**Syntax:** `ENDS_WITH(string, suffix)`

**Parameters:**
- `string` (String) - String to check
- `suffix` (String) - Suffix to match

**Returns:** Boolean - true if string ends with suffix

**Examples:**
```
ENDS_WITH("hello world", "world")                  → true
ENDS_WITH("hello world", "hello")                  → false
ENDS_WITH(PROFILE("email"), ".com")                → true for "...@example.com"
```

#### REGEX_MATCH

Checks if a string matches a regular expression pattern.

**Syntax:** `REGEX_MATCH(string, pattern)`

**Parameters:**
- `string` (String) - String to match
- `pattern` (String) - Regular expression pattern

**Returns:** Boolean - true if string matches pattern

**Examples:**
```
REGEX_MATCH("test@example.com", ".*@.*\\.com")     → true
REGEX_MATCH("12345", "\\d+")                       → true
REGEX_MATCH("abc", "[0-9]+")                       → false
```

**Notes:**
- Uses Java regex syntax
- Backslashes must be escaped: `\\d` not `\d`

### String Manipulation

#### UPPER

Converts a string to uppercase.

**Syntax:** `UPPER(string)`

**Parameters:**
- `string` (String) - String to convert

**Returns:** String - uppercase string

**Examples:**
```
UPPER("hello")                                     → "HELLO"
UPPER("Hello World")                               → "HELLO WORLD"
UPPER(PROFILE("country"))                          → "US"
```

#### LOWER

Converts a string to lowercase.

**Syntax:** `LOWER(string)`

**Parameters:**
- `string` (String) - String to convert

**Returns:** String - lowercase string

**Examples:**
```
LOWER("HELLO")                                     → "hello"
LOWER("Hello World")                               → "hello world"
LOWER(PROFILE("country"))                          → "us"
```

#### TRIM

Removes leading and trailing whitespace.

**Syntax:** `TRIM(string)`

**Parameters:**
- `string` (String) - String to trim

**Returns:** String - trimmed string

**Examples:**
```
TRIM("  hello  ")                                  → "hello"
TRIM("hello")                                      → "hello"
TRIM("  hello world  ")                            → "hello world"
```

#### SUBSTRING

Extracts a substring from a string.

**Syntax:** `SUBSTRING(string, start, [length])`

**Parameters:**
- `string` (String) - Source string
- `start` (Number) - Start index (0-based)
- `length` (Number, optional) - Length to extract (default: to end)

**Returns:** String - extracted substring

**Examples:**
```
SUBSTRING("hello world", 0, 5)                     → "hello"
SUBSTRING("hello world", 6)                        → "world"
SUBSTRING("hello world", 6, 3)                     → "wor"
```

#### REPLACE

Replaces all occurrences of a substring.

**Syntax:** `REPLACE(string, search, replacement)`

**Parameters:**
- `string` (String) - Source string
- `search` (String) - Substring to replace
- `replacement` (String) - Replacement string

**Returns:** String - string with replacements

**Examples:**
```
REPLACE("hello world", "world", "there")           → "hello there"
REPLACE("foo bar foo", "foo", "baz")               → "baz bar baz"
REPLACE("hello", "x", "y")                         → "hello" (no change)
```

#### LENGTH

Returns the length of a string.

**Syntax:** `LENGTH(string)`

**Parameters:**
- `string` (String) - String to measure

**Returns:** Number - string length

**Examples:**
```
LENGTH("hello")                                    → 5
LENGTH("")                                         → 0
LENGTH("hello world")                              → 11
```

#### CONCAT

Concatenates multiple strings.

**Syntax:** `CONCAT(str1, str2, ...)`

**Parameters:**
- `str1, str2, ...` (String) - Strings to concatenate

**Returns:** String - concatenated string

**Examples:**
```
CONCAT("hello", " ", "world")                      → "hello world"
CONCAT(PROFILE("firstName"), " ", PROFILE("lastName"))  → "John Doe"
CONCAT("User: ", PROFILE("uuid"))                  → "User: user-123"
```

#### SPLIT

Splits a string into an array by delimiter.

**Syntax:** `SPLIT(string, delimiter)`

**Parameters:**
- `string` (String) - String to split
- `delimiter` (String) - Delimiter to split on

**Returns:** Collection - array of substrings

**Examples:**
```
SPLIT("a,b,c", ",")                                → ["a", "b", "c"]
SPLIT("hello world", " ")                          → ["hello", "world"]
COUNT(SPLIT("a,b,c,d", ","))                       → 4
```

## Conversion Functions

### TO_NUMBER

Converts a value to a number.

**Syntax:** `TO_NUMBER(value)`

**Parameters:**
- `value` (Any) - Value to convert

**Returns:** Number - numeric value

**Examples:**
```
TO_NUMBER("123")                                   → 123
TO_NUMBER("45.67")                                 → 45.67
TO_NUMBER(true)                                    → 1
TO_NUMBER(false)                                   → 0
```

**Notes:**
- Strings must be valid numbers
- Booleans: true=1, false=0
- Invalid inputs throw an error

### TO_STRING

Converts a value to a string.

**Syntax:** `TO_STRING(value)`

**Parameters:**
- `value` (Any) - Value to convert

**Returns:** String - string representation

**Examples:**
```
TO_STRING(123)                                     → "123"
TO_STRING(45.67)                                   → "45.67"
TO_STRING(true)                                    → "true"
```

### TO_BOOLEAN

Converts a value to a boolean.

**Syntax:** `TO_BOOLEAN(value)`

**Parameters:**
- `value` (Any) - Value to convert

**Returns:** Boolean - boolean value

**Examples:**
```
TO_BOOLEAN(1)                                      → true
TO_BOOLEAN(0)                                      → false
TO_BOOLEAN("true")                                 → true
TO_BOOLEAN("false")                                → false
```

**Truthiness Rules:**
- Numbers: 0 = false, non-zero = true
- Strings: "false", "0", "" = false, others = true
- null = false

### CONVERT_UNIT

Converts a value between units.

**Syntax:** `CONVERT_UNIT(value, fromUnit, toUnit)`

**Parameters:**
- `value` (Number) - Value to convert
- `fromUnit` (String) - Source unit
- `toUnit` (String) - Target unit

**Returns:** Number - converted value

**Examples:**
```
CONVERT_UNIT(60, "seconds", "minutes")             → 1.0
CONVERT_UNIT(1000, "meters", "kilometers")         → 1.0
CONVERT_UNIT(1000, "grams", "kilograms")           → 1.0
CONVERT_UNIT(100, "cents", "dollars")              → 1.0
```

**Supported Units:**

**Time:**
- seconds, minutes, hours, days, weeks, months, years

**Distance:**
- meters, kilometers, miles, feet

**Weight:**
- grams, kilograms, pounds, ounces

## Segmentation Functions

### BUCKET

Assigns a value to a bucket based on ranges.

**Syntax:** `BUCKET(value, ranges)`

**Parameters:**
- `value` (Number) - Value to bucket
- `ranges` (Array) - Array of [min, max, label] ranges

**Returns:** String - bucket label

**Examples:**
```
BUCKET(50, [[0, 100, "low"], [100, 500, "medium"], [500, 5000, "high"]])  → "low"
BUCKET(250, [[0, 100, "low"], [100, 500, "medium"], [500, 5000, "high"]]) → "medium"
BUCKET(1000, [[0, 100, "low"], [100, 500, "medium"], [500, 5000, "high"]]) → "high"
```

**Range Format:**
```
[minValue, maxValue, "label"]
```

**Notes:**
- Returns the label of the first matching range
- If no range matches, returns null or default label
- Ranges can be inclusive or exclusive (configurable)

## Function Combinations

DSL functions can be combined to create powerful expressions:

### Example 1: High-Value Recent Purchasers
```
AND(
  GT(COUNT(WHERE(userData.events, EQ(EVENT("eventName"), "purchase"))), 5),
  GT(SUM(PARAM("amount")), 1000),
  IN_RECENT_DAYS(30)
)
```

### Example 2: Active Days Ratio
```
DIVIDE(
  COUNT(UNIQUE(BY(DATE_FORMAT(ACTION_TIME(), "yyyy-MM-dd")))),
  30
)
```

### Example 3: Weekend Shoppers
```
GT(
  COUNT(WHERE(
    userData.events,
    AND(
      EQ(EVENT("eventName"), "purchase"),
      OR(EQ(WEEKDAY(EVENT("timestamp")), 6), EQ(WEEKDAY(EVENT("timestamp")), 7))
    )
  )),
  0
)
```

### Example 4: Segmentation by Purchase Amount
```
BUCKET(
  CONVERT_UNIT(SUM(PARAM("amount")), "cents", "dollars"),
  [[0, 100, "low"], [100, 500, "medium"], [500, 2000, "high"], [2000, 10000, "vip"]]
)
```

## See Also

- [API Documentation](API.md) - Core API and usage
- [Extension Guide](EXTENSION_GUIDE.md) - Adding custom functions
- [Use Case Examples](USE_CASE_EXAMPLES.md) - Common patterns
- [Performance Guide](PERFORMANCE_GUIDE.md) - Optimization techniques

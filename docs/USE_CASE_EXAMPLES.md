# User Segmentation DSL - Use Case Examples

This document provides comprehensive examples of DSL expressions for common user segmentation use cases. Each example includes the DSL expression, a description of what it does, and sample scenarios.

## Table of Contents

1. [Users with > N Purchases in Past Year](#1-users-with--n-purchases-in-past-year)
2. [Users with Purchase Amount > N](#2-users-with-purchase-amount--n)
3. [Segment Users by Purchase Amount Ranges](#3-segment-users-by-purchase-amount-ranges)
4. [Calculate Active Days in Time Period](#4-calculate-active-days-in-time-period)
5. [Filter by UTM Parameters](#5-filter-by-utm-parameters)
6. [Check Recurring Events](#6-check-recurring-events)
7. [Filter by Weekday](#7-filter-by-weekday)
8. [Convert Units in Expressions](#8-convert-units-in-expressions)
9. [Complex Multi-Condition Segmentation](#9-complex-multi-condition-segmentation)

---

## 1. Users with > N Purchases in Past Year

**Use Case**: Identify users who have made more than a specific number of purchases (e.g., 5) in the past 365 days.

**DSL Expression**:
```
GT(
    COUNT(
        WHERE(
            userData.events, 
            "EQ(EVENT(\"eventName\"), \"purchase\")"
        )
    ), 
    5
)
```

**How it works**:
1. `WHERE(userData.events, "EQ(EVENT(\"eventName\"), \"purchase\")")` - Filters all events to only purchase events (note: WHERE accepts a string expression)
2. `COUNT(...)` - Counts the number of purchase events
3. `GT(..., 5)` - Checks if the count is greater than 5

**Example Scenarios**:
- User with 7 purchases → Returns `true`
- User with 3 purchases → Returns `false`
- User with exactly 5 purchases → Returns `false` (not greater than)

**Variations**:
- Change `5` to any threshold value
- Use `GTE` instead of `GT` for "greater than or equal to"
- Add time range filters using `FROM` and `TO` functions

---

## 2. Users with Purchase Amount > N

**Use Case**: Identify users whose total purchase amount exceeds a threshold (e.g., $1000).

**DSL Expression**:
```
GT(
    SUM(
        WHERE(
            userData.events, 
            "EQ(EVENT(\"eventName\"), \"purchase\")"
        )
    ), 
    1000
)
```

**How it works**:
1. `WHERE(userData.events, "EQ(EVENT(\"eventName\"), \"purchase\")")` - Filters to purchase events (note: WHERE accepts a string expression)
2. `SUM(...)` - Sums the amounts from all purchase events (assumes events have an "amount" field)
3. `GT(..., 1000)` - Checks if the total exceeds $1000

**Example Scenarios**:
- User with purchases totaling $1500 → Returns `true`
- User with purchases totaling $800 → Returns `false`

**Variations**:
- Access specific parameter: `SUM(PARAM("amount"))` if amount is in event parameters
- Use different thresholds
- Combine with time ranges for "purchases in last 90 days"

---

## 3. Segment Users by Purchase Amount Ranges

**Use Case**: Categorize users into segments (e.g., "low", "medium", "high", "vip") based on their total purchase amount.

**DSL Expression**:
```
BUCKET(
    SUM(
        WHERE(
            userData.events, 
            "EQ(EVENT(\"eventName\"), \"purchase\")"
        )
    ), 
    [
        [0, 100, "low"], 
        [100, 500, "medium"], 
        [500, 2000, "high"], 
        [2000, 10000, "vip"]
    ]
)
```

**How it works**:
1. `SUM(WHERE(...))` - Calculates total purchase amount
2. `BUCKET(..., ranges)` - Assigns the user to a segment based on which range their total falls into

**Bucket Ranges**:
- `[0, 100, "low"]` - $0 to $100 → "low" segment
- `[100, 500, "medium"]` - $100 to $500 → "medium" segment
- `[500, 2000, "high"]` - $500 to $2000 → "high" segment
- `[2000, 10000, "vip"]` - $2000+ → "vip" segment

**Example Scenarios**:
- User with $50 total → Returns `"low"`
- User with $300 total → Returns `"medium"`
- User with $1200 total → Returns `"high"`
- User with $3000 total → Returns `"vip"`

**Variations**:
- Adjust ranges to match your business needs
- Use different segment labels
- Combine with other filters (e.g., only purchases in past year)

---

## 4. Calculate Active Days in Time Period

**Use Case**: Calculate the ratio of active days to total days in a period (e.g., active days / 30 days).

**DSL Expression**:
```
DIVIDE(
    COUNT(
        UNIQUE(
            WHERE(
                userData.events, 
                "EQ(EVENT(\"eventType\"), \"action\")"
            )
        )
    ), 
    30
)
```

**How it works**:
1. `WHERE(userData.events, "EQ(EVENT(\"eventType\"), \"action\")")` - Filters to action events (note: WHERE accepts a string expression)
2. `UNIQUE(...)` - Gets unique events (removes duplicates from same day)
3. `COUNT(...)` - Counts the number of unique active days
4. `DIVIDE(..., 30)` - Calculates the ratio (active days / 30)

**Example Scenarios**:
- User active on 15 unique days → Returns `0.5` (50% active)
- User active on 30 unique days → Returns `1.0` (100% active)
- User active on 5 unique days → Returns `0.167` (~17% active)

**Variations**:
- Use `DATE_FORMAT(ACTION_TIME(), "yyyy-MM-dd")` to group by date
- Change the denominator for different time periods
- Add time range filters with `FROM` and `TO`

---

## 5. Filter by UTM Parameters

**Use Case**: Identify users who came from a specific marketing campaign (e.g., "summer_sale").

**DSL Expression**:
```
GT(
    COUNT(
        WHERE(
            userData.events, 
            "EQ(PARAM(\"utm_campaign\"), \"summer_sale\")"
        )
    ), 
    0
)
```

**How it works**:
1. `WHERE(userData.events, "EQ(PARAM(\"utm_campaign\"), \"summer_sale\")")` - Filters events with the specified UTM campaign (note: WHERE accepts a string expression)
2. `COUNT(...)` - Counts matching events
3. `GT(..., 0)` - Returns true if any events match

**Example Scenarios**:
- User with events from "summer_sale" campaign → Returns `true`
- User with events from "winter_promo" campaign → Returns `false`
- User with no UTM parameters → Returns `false`

**Variations**:
- Filter by other UTM parameters: `utm_source`, `utm_medium`, `utm_content`
- Combine multiple UTM conditions with `AND` or `OR`
- Check for multiple campaigns: `OR(EQ(PARAM("utm_campaign"), "summer_sale"), EQ(PARAM("utm_campaign"), "fall_promo"))`

---

## 6. Check Recurring Events

**Use Case**: Identify users who have performed a specific event (e.g., "login") at least N times (e.g., 3) within a time window (e.g., past 90 days).

**DSL Expression**:
```
IS_RECURRING("login", 3, 90)
```

**How it works**:
1. `IS_RECURRING(eventName, minCount, timeWindow)` - Checks if the specified event occurred at least `minCount` times in the past `timeWindow` days

**Example Scenarios**:
- User with 5 login events in past 90 days → Returns `true`
- User with 2 login events in past 90 days → Returns `false`
- User with exactly 3 login events → Returns `true`

**Variations**:
- Change event name: `IS_RECURRING("purchase", 5, 365)` for recurring purchasers
- Adjust minimum count threshold
- Adjust time window (in days)

---

## 7. Filter by Weekday

**Use Case**: Identify users who have events on specific days of the week (e.g., weekends).

**DSL Expression**:
```
GT(
    COUNT(
        WHERE(
            userData.events, 
            "OR(
                EQ(WEEKDAY(EVENT(\"timestamp\")), 6), 
                EQ(WEEKDAY(EVENT(\"timestamp\")), 7)
            )"
        )
    ), 
    0
)
```

**How it works**:
1. `WEEKDAY(EVENT("timestamp"))` - Gets the day of week (1=Monday, 7=Sunday)
2. `OR(EQ(..., 6), EQ(..., 7))` - Checks if it's Saturday (6) or Sunday (7)
3. `WHERE(...)` - Filters events to only weekend events (note: WHERE accepts a string expression)
4. `COUNT(...)` - Counts weekend events
5. `GT(..., 0)` - Returns true if any weekend events exist

**Weekday Values**:
- 1 = Monday
- 2 = Tuesday
- 3 = Wednesday
- 4 = Thursday
- 5 = Friday
- 6 = Saturday
- 7 = Sunday

**Example Scenarios**:
- User with weekend events → Returns `true`
- User with only weekday events → Returns `false`

**Variations**:
- Filter for specific weekdays: `EQ(WEEKDAY(EVENT("timestamp")), 1)` for Mondays
- Filter for weekdays: `AND(GTE(WEEKDAY(...), 1), LTE(WEEKDAY(...), 5))`
- Combine with event type filters

---

## 8. Convert Units in Expressions

**Use Case**: Convert values between units (e.g., cents to dollars) before performing calculations or bucketing.

**DSL Expression**:
```
BUCKET(
    CONVERT_UNIT(
        SUM(
            WHERE(
                userData.events, 
                "EQ(EVENT(\"eventName\"), \"purchase\")"
            )
        ), 
        "cents", 
        "dollars"
    ), 
    [
        [0, 100, "low"], 
        [100, 500, "medium"], 
        [500, 10000, "high"]
    ]
)
```

**How it works**:
1. `SUM(WHERE(...))` - Calculates total purchase amount in cents (note: WHERE accepts a string expression)
2. `CONVERT_UNIT(..., "cents", "dollars")` - Converts from cents to dollars
3. `BUCKET(...)` - Assigns to segment based on dollar amount

**Supported Unit Categories**:

**Time Units**:
- seconds, minutes, hours, days, weeks, months, years

**Distance Units**:
- meters, kilometers, miles, feet

**Weight Units**:
- grams, kilograms, pounds, ounces

**Currency Units** (custom):
- cents, dollars (100 cents = 1 dollar)

**Example Scenarios**:
- User with 5000 cents → Converts to $50 → Returns `"low"`
- User with 30000 cents → Converts to $300 → Returns `"medium"`
- User with 100000 cents → Converts to $1000 → Returns `"high"`

**Variations**:
- Convert time: `CONVERT_UNIT(EVENT("duration"), "seconds", "minutes")`
- Convert distance: `CONVERT_UNIT(PARAM("distance"), "meters", "kilometers")`
- Convert weight: `CONVERT_UNIT(PARAM("weight"), "grams", "kilograms")`

---

## 9. Complex Multi-Condition Segmentation

**Use Case**: Identify high-value users who meet multiple criteria:
- More than 10 purchases in past year
- Total purchase amount > $2000
- Active in past 30 days
- From US or UK

**DSL Expression**:
```
AND(
    GT(
        COUNT(
            WHERE(
                userData.events, 
                "EQ(EVENT(\"eventName\"), \"purchase\")"
            )
        ), 
        10
    ), 
    GT(
        SUM(
            WHERE(
                userData.events, 
                "EQ(EVENT(\"eventName\"), \"purchase\")"
            )
        ), 
        2000
    ), 
    GT(
        COUNT(
            WHERE(
                userData.events, 
                "EQ(EVENT(\"eventType\"), \"action\")"
            )
        ), 
        0
    ), 
    OR(
        EQ(PROFILE("country"), "US"), 
        EQ(PROFILE("country"), "UK")
    )
)
```

**How it works**:
1. First condition: Checks purchase count > 10 (note: WHERE accepts a string expression)
2. Second condition: Checks total purchase amount > $2000 (note: WHERE accepts a string expression)
3. Third condition: Checks for recent activity (note: WHERE accepts a string expression)
4. Fourth condition: Checks if user is from US or UK
5. `AND(...)` - All conditions must be true

**Example Scenarios**:
- User meeting all criteria → Returns `true`
- User with only 8 purchases → Returns `false`
- User from France → Returns `false`
- User with no recent activity → Returns `false`

**Variations**:
- Add more conditions (e.g., specific device type, browser)
- Adjust thresholds
- Use different logical combinations (`OR` for any condition)
- Add time range filters

---

## Best Practices

### 1. **Use Descriptive Variable Names**
When building complex expressions, break them down into logical parts for readability.

### 2. **Optimize Performance**
- Use `WHERE` to filter early in the expression
- Avoid redundant calculations
- Leverage expression caching for repeated evaluations

### 3. **Handle Edge Cases**
- Check for null values with appropriate defaults
- Consider empty collections (COUNT returns 0)
- Handle division by zero scenarios

### 4. **Test with Real Data**
- Validate expressions with actual user data
- Test boundary conditions
- Verify performance with large datasets

### 5. **Document Your Expressions**
- Add comments explaining the business logic
- Document expected input/output
- Include example scenarios

---

## Additional Resources

- **Function Reference**: See the complete list of available DSL functions in the main documentation
- **API Documentation**: Learn how to integrate the DSL into your application
- **Extension Guide**: Add custom functions for your specific use cases
- **Performance Guide**: Optimize DSL expressions for large-scale data processing

---

## Support

For questions, issues, or feature requests, please refer to the project repository or contact the development team.

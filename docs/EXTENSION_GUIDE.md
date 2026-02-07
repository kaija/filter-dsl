# Extension Guide - Adding Custom Functions

This guide explains how to extend the User Segmentation DSL by adding custom functions.

## Table of Contents

1. [Overview](#overview)
2. [Quick Start](#quick-start)
3. [Function Anatomy](#function-anatomy)
4. [Step-by-Step Tutorial](#step-by-step-tutorial)
5. [Helper Methods](#helper-methods)
6. [Testing Custom Functions](#testing-custom-functions)
7. [Best Practices](#best-practices)
8. [Advanced Topics](#advanced-topics)

## Overview

The DSL is designed for extensibility. Each function is implemented in its own Java file, making it easy to add new functionality without modifying core code.

**Key Benefits:**
- ✅ No core code modifications required
- ✅ Type-safe with metadata validation
- ✅ Access to helper methods for common operations
- ✅ Automatic registration via classpath scanning
- ✅ Consistent API across all functions

## Quick Start

Here's a minimal example of a custom function:

```java
package com.example.dsl.functions.custom;

import com.example.dsl.functions.DSLFunction;
import com.example.dsl.functions.FunctionMetadata;
import com.example.dsl.functions.FunctionMetadata.*;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import java.util.Map;

/**
 * GREET function - returns a greeting message.
 * Usage: GREET("Alice") returns "Hello, Alice!"
 */
public class GreetFunction extends DSLFunction {

    @Override
    public String getName() {
        return "GREET";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("GREET")
            .minArgs(1)
            .maxArgs(1)
            .argumentType(0, ArgumentType.STRING)
            .returnType(ReturnType.STRING)
            .description("Returns a greeting message")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 1);
        String name = toString(args[0], env);
        return new AviatorString("Hello, " + name + "!");
    }
}
```

**Usage:**
```java
// Register and use
DSL dsl = DSL.builder()
    .enableAutoDiscovery(false)
    .build();

dsl.getRegistry().register(new GreetFunction());

EvaluationResult result = dsl.evaluateInstance("GREET(\"Alice\")", userData);
// result.getValue() → "Hello, Alice!"
```

## Function Anatomy

Every DSL function must:

1. **Extend `DSLFunction`** - Base class providing helper methods
2. **Implement `getName()`** - Return UPPERCASE function name
3. **Implement `getFunctionMetadata()`** - Define function signature
4. **Implement `call()`** - Execute the function logic

### Required Methods

#### 1. getName()

Returns the UPPERCASE function name used in expressions.

```java
@Override
public String getName() {
    return "MY_FUNCTION";  // Must be UPPERCASE
}
```

#### 2. getFunctionMetadata()

Defines the function signature for validation.

```java
@Override
public FunctionMetadata getFunctionMetadata() {
    return FunctionMetadata.builder()
        .name("MY_FUNCTION")
        .minArgs(1)              // Minimum arguments
        .maxArgs(2)              // Maximum arguments
        .argumentType(0, ArgumentType.STRING)  // First arg type
        .argumentType(1, ArgumentType.NUMBER)  // Second arg type (optional)
        .returnType(ReturnType.BOOLEAN)
        .description("Function description")
        .build();
}
```

**Argument Types:**
- `ArgumentType.STRING` - Text value
- `ArgumentType.NUMBER` - Numeric value
- `ArgumentType.BOOLEAN` - True/false value
- `ArgumentType.COLLECTION` - List of items
- `ArgumentType.ANY` - Any type

**Return Types:**
- `ReturnType.STRING`
- `ReturnType.NUMBER`
- `ReturnType.BOOLEAN`
- `ReturnType.COLLECTION`
- `ReturnType.ANY`

#### 3. call()

Executes the function logic.

```java
@Override
public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
    // 1. Validate arguments
    validateArgCount(args, 1);
    
    // 2. Extract and convert arguments
    String input = toString(args[0], env);
    
    // 3. Execute logic
    String result = processInput(input);
    
    // 4. Return AviatorObject
    return new AviatorString(result);
}
```

**AviatorObject Types:**
- `AviatorString` - For string results
- `AviatorLong` - For integer results
- `AviatorDouble` - For decimal results
- `AviatorBoolean` - For boolean results
- `AviatorJavaType` - For Java objects (collections, etc.)

## Step-by-Step Tutorial

Let's create a `REVERSE` function that reverses a string.

### Step 1: Create the Function Class

Create `src/main/java/com/example/dsl/functions/string/ReverseFunction.java`:

```java
package com.example.dsl.functions.string;

import com.example.dsl.functions.DSLFunction;
import com.example.dsl.functions.FunctionMetadata;
import com.example.dsl.functions.FunctionMetadata.*;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import java.util.Map;

/**
 * REVERSE function - reverses a string.
 * 
 * <p>Examples:
 * <ul>
 *   <li>REVERSE("hello") → "olleh"</li>
 *   <li>REVERSE("DSL") → "LSD"</li>
 *   <li>REVERSE(PROFILE("city")) → reversed city name</li>
 * </ul>
 * 
 * @see DSLFunction
 */
public class ReverseFunction extends DSLFunction {

    @Override
    public String getName() {
        return "REVERSE";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("REVERSE")
            .minArgs(1)
            .maxArgs(1)
            .argumentType(0, ArgumentType.STRING)
            .returnType(ReturnType.STRING)
            .description("Reverses a string")
            .example("REVERSE(\"hello\")", "\"olleh\"")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        // Validate argument count
        validateArgCount(args, 1);
        
        // Convert argument to string
        String input = toString(args[0], env);
        
        // Handle null input
        if (input == null) {
            return new AviatorString("");
        }
        
        // Reverse the string
        String reversed = new StringBuilder(input).reverse().toString();
        
        // Return as AviatorString
        return new AviatorString(reversed);
    }
}
```

### Step 2: Register the Function

**Option A: Automatic Discovery (Recommended)**

If your function is in the `com.example.dsl.functions` package, it will be auto-discovered:

```java
// Default instance automatically discovers functions
EvaluationResult result = DSL.evaluate("REVERSE(\"hello\")", userData);
```

**Option B: Manual Registration**

```java
DSL dsl = DSL.builder()
    .enableAutoDiscovery(false)
    .build();

// Register manually
dsl.getRegistry().register(new ReverseFunction());

// Use the function
EvaluationResult result = dsl.evaluateInstance("REVERSE(\"hello\")", userData);
```

### Step 3: Test the Function

Create `src/test/java/com/example/dsl/functions/string/ReverseFunctionTest.java`:

```java
package com.example.dsl.functions.string;

import com.example.dsl.functions.FunctionMetadata;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ReverseFunctionTest {

    @Test
    void testReverse() {
        ReverseFunction function = new ReverseFunction();
        Map<String, Object> env = new HashMap<>();
        
        AviatorObject[] args = {new AviatorString("hello")};
        AviatorObject result = function.call(env, args);
        
        assertEquals("olleh", result.getValue(env));
    }

    @Test
    void testReverseEmpty() {
        ReverseFunction function = new ReverseFunction();
        Map<String, Object> env = new HashMap<>();
        
        AviatorObject[] args = {new AviatorString("")};
        AviatorObject result = function.call(env, args);
        
        assertEquals("", result.getValue(env));
    }

    @Test
    void testReverseNull() {
        ReverseFunction function = new ReverseFunction();
        Map<String, Object> env = new HashMap<>();
        
        AviatorObject[] args = {new AviatorString(null)};
        AviatorObject result = function.call(env, args);
        
        assertEquals("", result.getValue(env));
    }

    @Test
    void testMetadata() {
        ReverseFunction function = new ReverseFunction();
        FunctionMetadata metadata = function.getFunctionMetadata();
        
        assertEquals("REVERSE", metadata.getName());
        assertEquals(1, metadata.getMinArgs());
        assertEquals(1, metadata.getMaxArgs());
    }
}
```

### Step 4: Use the Function

```java
// In expressions
String expr1 = "REVERSE(\"DSL\")";  // Returns "LSD"
String expr2 = "UPPER(REVERSE(\"hello\"))";  // Returns "OLLEH"
String expr3 = "REVERSE(PROFILE(\"city\"))";  // Reverses user's city name

// Evaluate
EvaluationResult result = DSL.evaluate(expr1, userData);
System.out.println(result.getValue());  // "LSD"
```

## Helper Methods

The `DSLFunction` base class provides helper methods for common operations.

### Argument Validation

**`validateArgCount(AviatorObject[] args, int expected)`**

Validates exact argument count:

```java
validateArgCount(args, 2);  // Requires exactly 2 arguments
```

**`validateArgCountRange(AviatorObject[] args, int min, int max)`**

Validates argument count range:

```java
validateArgCountRange(args, 1, 3);  // Requires 1-3 arguments
```

### Type Conversion

**`toNumber(AviatorObject obj, Map<String, Object> env)`**

Converts to Number, throws TypeMismatchException if not numeric:

```java
Number value = toNumber(args[0], env);
double d = value.doubleValue();
```

**`toString(AviatorObject obj, Map<String, Object> env)`**

Converts to String:

```java
String value = toString(args[0], env);
```

**`toBoolean(AviatorObject obj, Map<String, Object> env)`**

Converts to Boolean:

```java
Boolean value = toBoolean(args[0], env);
```

**`toCollection(AviatorObject obj, Map<String, Object> env)`**

Converts to Collection:

```java
Collection<?> collection = toCollection(args[0], env);
```

### Context Access

**`getUserData(Map<String, Object> env)`**

Gets the complete UserData object:

```java
Object userData = getUserData(env);
```

**`getCurrentEvent(Map<String, Object> env)`**

Gets the current event being evaluated:

```java
Event event = getCurrentEvent(env);
String eventName = event.getEventName();
```

**`getCurrentVisit(Map<String, Object> env)`**

Gets the current visit:

```java
Visit visit = getCurrentVisit(env);
```

**`getNow(Map<String, Object> env)`**

Gets the current evaluation timestamp:

```java
Instant now = getNow(env);
```

**`getTimeRange(Map<String, Object> env)`**

Gets the active time range (if set by FROM/TO):

```java
TimeRange timeRange = getTimeRange(env);
if (timeRange != null) {
    Instant start = timeRange.getStartTime();
    Instant end = timeRange.getEndTime();
}
```

## Testing Custom Functions

### Unit Tests

Test your function in isolation:

```java
@Test
void testMyFunction() {
    MyFunction function = new MyFunction();
    Map<String, Object> env = new HashMap<>();
    
    AviatorObject[] args = {new AviatorString("input")};
    AviatorObject result = function.call(env, args);
    
    assertEquals("expected", result.getValue(env));
}
```

### Integration Tests

Test your function in DSL expressions:

```java
@Test
void testMyFunctionInExpression() {
    DSL dsl = DSL.builder()
        .enableAutoDiscovery(false)
        .build();
    
    dsl.getRegistry().register(new MyFunction());
    
    UserData userData = createTestUserData();
    String expression = "MY_FUNCTION(\"test\")";
    
    EvaluationResult result = dsl.evaluateInstance(expression, userData);
    
    assertTrue(result.isSuccess());
    assertEquals("expected", result.getValue());
}
```

### Property-Based Tests

Test universal properties with jqwik:

```java
import net.jqwik.api.*;

class MyFunctionPropertyTest {
    
    @Property
    void reverseTwiceGivesOriginal(@ForAll String input) {
        // Property: REVERSE(REVERSE(x)) = x
        ReverseFunction function = new ReverseFunction();
        Map<String, Object> env = new HashMap<>();
        
        AviatorObject[] args1 = {new AviatorString(input)};
        AviatorObject result1 = function.call(env, args1);
        
        AviatorObject[] args2 = {result1};
        AviatorObject result2 = function.call(env, args2);
        
        assertEquals(input, result2.getValue(env));
    }
}
```

## Best Practices

### 1. Function Naming

✅ **DO:**
- Use UPPERCASE names: `MY_FUNCTION`
- Use descriptive names: `CALCULATE_DISCOUNT` not `CALC_DISC`
- Follow existing naming patterns

❌ **DON'T:**
- Use lowercase or mixed case: `myFunction`
- Use abbreviations: `CALC` instead of `CALCULATE`
- Use special characters: `MY-FUNCTION`

### 2. Error Handling

✅ **DO:**
- Validate arguments early
- Throw descriptive exceptions
- Handle null inputs gracefully
- Document error conditions

```java
@Override
public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
    validateArgCount(args, 1);
    
    Number value = toNumber(args[0], env);
    
    if (value.doubleValue() < 0) {
        throw new FunctionArgumentException(
            getName() + " requires non-negative value, got: " + value);
    }
    
    // Process...
}
```

❌ **DON'T:**
- Silently ignore errors
- Return null without documentation
- Throw generic exceptions

### 3. Documentation

✅ **DO:**
- Add JavaDoc to your function class
- Document parameters and return values
- Provide usage examples
- Explain edge cases

```java
/**
 * CALCULATE_DISCOUNT function - calculates discount percentage.
 * 
 * <p>Calculates the discount percentage based on purchase amount.
 * Higher amounts receive larger discounts.
 * 
 * <p>Examples:
 * <ul>
 *   <li>CALCULATE_DISCOUNT(100) → 5.0 (5% discount)</li>
 *   <li>CALCULATE_DISCOUNT(500) → 10.0 (10% discount)</li>
 *   <li>CALCULATE_DISCOUNT(1000) → 15.0 (15% discount)</li>
 * </ul>
 * 
 * @see DSLFunction
 */
```

### 4. Performance

✅ **DO:**
- Keep functions stateless
- Avoid expensive operations in loops
- Cache computed values when appropriate
- Use efficient algorithms

❌ **DON'T:**
- Store state in instance variables
- Perform I/O operations
- Create unnecessary objects
- Use inefficient algorithms

### 5. Type Safety

✅ **DO:**
- Use helper methods for type conversion
- Validate types early
- Provide clear type error messages

```java
try {
    Number value = toNumber(args[0], env);
} catch (TypeMismatchException e) {
    throw new FunctionArgumentException(
        getName() + " expects numeric argument, got: " + 
        args[0].getValue(env).getClass().getSimpleName());
}
```

## Advanced Topics

### Variable Arguments

Support variable number of arguments:

```java
@Override
public FunctionMetadata getFunctionMetadata() {
    return FunctionMetadata.builder()
        .name("CONCAT_ALL")
        .minArgs(1)
        .maxArgs(Integer.MAX_VALUE)  // Unlimited
        .returnType(ReturnType.STRING)
        .build();
}

@Override
public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
    validateArgCountRange(args, 1, Integer.MAX_VALUE);
    
    StringBuilder result = new StringBuilder();
    for (AviatorObject arg : args) {
        result.append(toString(arg, env));
    }
    
    return new AviatorString(result.toString());
}
```

### Optional Arguments

Support optional arguments with defaults:

```java
@Override
public FunctionMetadata getFunctionMetadata() {
    return FunctionMetadata.builder()
        .name("ROUND")
        .minArgs(1)
        .maxArgs(2)  // Second arg is optional
        .argumentType(0, ArgumentType.NUMBER)
        .argumentType(1, ArgumentType.NUMBER)  // Optional: decimal places
        .returnType(ReturnType.NUMBER)
        .build();
}

@Override
public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
    validateArgCountRange(args, 1, 2);
    
    Number value = toNumber(args[0], env);
    int decimals = 0;  // Default
    
    if (args.length > 1) {
        decimals = toNumber(args[1], env).intValue();
    }
    
    // Round to specified decimals...
}
```

### Working with Collections

Process collections efficiently:

```java
@Override
public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
    validateArgCount(args, 1);
    
    Collection<?> collection = toCollection(args[0], env);
    
    List<Object> result = new ArrayList<>();
    for (Object item : collection) {
        // Process each item
        result.add(processItem(item));
    }
    
    return AviatorRuntimeJavaType.valueOf(result);
}
```

### Accessing Event Data

Access event fields in your function:

```java
@Override
public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
    Event currentEvent = getCurrentEvent(env);
    
    if (currentEvent == null) {
        throw new FunctionArgumentException(
            getName() + " requires an event context");
    }
    
    String eventName = currentEvent.getEventName();
    Map<String, Object> params = currentEvent.getParameters();
    
    // Process event data...
}
```

### Custom Exceptions

Create custom exceptions for specific errors:

```java
public class InvalidDiscountException extends RuntimeException {
    public InvalidDiscountException(String message) {
        super(message);
    }
}

// In your function
if (discount < 0 || discount > 100) {
    throw new InvalidDiscountException(
        "Discount must be between 0 and 100, got: " + discount);
}
```

## Complete Example: CALCULATE_DISCOUNT Function

Here's a complete example with all best practices:

```java
package com.example.dsl.functions.custom;

import com.example.dsl.functions.DSLFunction;
import com.example.dsl.functions.FunctionArgumentException;
import com.example.dsl.functions.FunctionMetadata;
import com.example.dsl.functions.FunctionMetadata.*;
import com.googlecode.aviator.runtime.type.AviatorDouble;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Map;

/**
 * CALCULATE_DISCOUNT function - calculates discount percentage based on amount.
 * 
 * <p>Discount tiers:
 * <ul>
 *   <li>$0-$99: 0% discount</li>
 *   <li>$100-$499: 5% discount</li>
 *   <li>$500-$999: 10% discount</li>
 *   <li>$1000+: 15% discount</li>
 * </ul>
 * 
 * <p>Examples:
 * <ul>
 *   <li>CALCULATE_DISCOUNT(50) → 0.0</li>
 *   <li>CALCULATE_DISCOUNT(250) → 5.0</li>
 *   <li>CALCULATE_DISCOUNT(750) → 10.0</li>
 *   <li>CALCULATE_DISCOUNT(1500) → 15.0</li>
 * </ul>
 * 
 * @see DSLFunction
 */
public class CalculateDiscountFunction extends DSLFunction {

    private static final double TIER1_THRESHOLD = 100.0;
    private static final double TIER2_THRESHOLD = 500.0;
    private static final double TIER3_THRESHOLD = 1000.0;
    
    private static final double TIER1_DISCOUNT = 5.0;
    private static final double TIER2_DISCOUNT = 10.0;
    private static final double TIER3_DISCOUNT = 15.0;

    @Override
    public String getName() {
        return "CALCULATE_DISCOUNT";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("CALCULATE_DISCOUNT")
            .minArgs(1)
            .maxArgs(1)
            .argumentType(0, ArgumentType.NUMBER)
            .returnType(ReturnType.NUMBER)
            .description("Calculates discount percentage based on purchase amount")
            .example("CALCULATE_DISCOUNT(250)", "5.0")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        // Validate arguments
        validateArgCount(args, 1);
        
        // Extract amount
        Number amount = toNumber(args[0], env);
        double amountValue = amount.doubleValue();
        
        // Validate amount is non-negative
        if (amountValue < 0) {
            throw new FunctionArgumentException(
                getName() + " requires non-negative amount, got: " + amountValue);
        }
        
        // Calculate discount
        double discount = calculateDiscount(amountValue);
        
        // Return result
        return new AviatorDouble(discount);
    }
    
    /**
     * Calculates discount percentage based on amount.
     * 
     * @param amount Purchase amount
     * @return Discount percentage (0-15)
     */
    private double calculateDiscount(double amount) {
        if (amount >= TIER3_THRESHOLD) {
            return TIER3_DISCOUNT;
        } else if (amount >= TIER2_THRESHOLD) {
            return TIER2_DISCOUNT;
        } else if (amount >= TIER1_THRESHOLD) {
            return TIER1_DISCOUNT;
        } else {
            return 0.0;
        }
    }
}
```

## Summary

Adding custom functions to the DSL is straightforward:

1. **Create** a class extending `DSLFunction`
2. **Implement** three required methods
3. **Register** the function (auto or manual)
4. **Test** thoroughly
5. **Document** with JavaDoc and examples

The extensible architecture makes it easy to add domain-specific functionality while maintaining type safety and consistency with built-in functions.

## See Also

- [API Documentation](API.md) - Core API reference
- [Function Reference](FUNCTION_REFERENCE.md) - Built-in functions
- [Use Case Examples](USE_CASE_EXAMPLES.md) - Common patterns

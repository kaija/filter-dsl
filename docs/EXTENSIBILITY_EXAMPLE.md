# Extensibility Example

This document demonstrates how easy it is to add new functions to the User Segmentation DSL.

## Adding a Custom Function

The DSL is designed to be highly extensible. Each function is implemented in its own Java file, making it easy to add new functionality without modifying existing code.

### Example: Creating a REVERSE Function

Let's create a simple function that reverses a string.

#### Step 1: Create the Function Class

Create a new file `src/main/java/com/example/dsl/functions/string/ReverseFunction.java`:

```java
package com.example.dsl.functions.string;

import com.example.dsl.functions.DSLFunction;
import com.example.dsl.functions.FunctionMetadata;
import com.example.dsl.functions.FunctionMetadata.ArgumentType;
import com.example.dsl.functions.FunctionMetadata.ReturnType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;

import java.util.Map;

/**
 * REVERSE function - reverses a string.
 * 
 * Usage: REVERSE("hello") returns "olleh"
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

#### Step 2: Register the Function

In your application code, register the function with the FunctionRegistry:

```java
import com.example.dsl.functions.FunctionRegistry;
import com.example.dsl.functions.string.ReverseFunction;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;

// Create registry and register the function
FunctionRegistry registry = new FunctionRegistry();
registry.register(new ReverseFunction());

// Register with AviatorScript
AviatorEvaluatorInstance aviator = AviatorEvaluator.newInstance();
registry.registerAll(aviator);

// Now you can use REVERSE in expressions
String expression = "REVERSE(\"hello\")";
Object result = aviator.execute(expression);
System.out.println(result); // Outputs: olleh
```

#### Step 3: Use the Function

The function is now available in DSL expressions:

```java
// Simple usage
String expr1 = "REVERSE(\"DSL\")";  // Returns "LSD"

// Combined with other functions
String expr2 = "UPPER(REVERSE(\"hello\"))";  // Returns "OLLEH"

// With profile data
String expr3 = "REVERSE(PROFILE(\"city\"))";  // Reverses the user's city name
```

## Key Benefits of This Architecture

1. **Isolation**: Each function is in its own file, making the codebase easy to navigate
2. **No Core Modifications**: Adding functions doesn't require changing any core DSL code
3. **Type Safety**: The metadata system provides compile-time validation
4. **Helper Methods**: The DSLFunction base class provides utilities for common operations
5. **Consistent API**: All functions follow the same pattern

## Function Categories

Functions are organized by category in separate packages:

- `functions/logical/` - Boolean logic (AND, OR, NOT)
- `functions/comparison/` - Comparisons (GT, LT, EQ, etc.)
- `functions/aggregation/` - Aggregations (COUNT, SUM, AVG, etc.)
- `functions/math/` - Mathematical operations (ADD, MULTIPLY, SQRT, etc.)
- `functions/datetime/` - Date/time operations (DATE_FORMAT, DATE_DIFF, etc.)
- `functions/string/` - String operations (CONTAINS, UPPER, TRIM, etc.)
- `functions/conversion/` - Type conversions (TO_NUMBER, TO_STRING, etc.)
- `functions/data/` - Data access (PROFILE, EVENT, PARAM)
- `functions/filtering/` - Collection filtering (WHERE, IF, BY)
- `functions/segmentation/` - Segmentation (BUCKET)

## Testing Your Custom Function

Create a test for your function:

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReverseFunctionTest {
    
    @Test
    void testReverse() {
        ReverseFunction function = new ReverseFunction();
        
        // Test basic reversal
        Map<String, Object> env = new HashMap<>();
        AviatorObject[] args = {new AviatorString("hello")};
        AviatorObject result = function.call(env, args);
        
        assertEquals("olleh", result.getValue(env));
    }
    
    @Test
    void testReverseNull() {
        ReverseFunction function = new ReverseFunction();
        
        Map<String, Object> env = new HashMap<>();
        AviatorObject[] args = {AviatorNil.NIL};
        AviatorObject result = function.call(env, args);
        
        assertEquals("", result.getValue(env));
    }
}
```

## Advanced: Property-Based Testing

For comprehensive testing, use property-based tests:

```java
import net.jqwik.api.*;

class ReverseFunctionPropertyTest {
    
    @Property
    void reversingTwiceGivesOriginal(@ForAll String input) {
        // Property: REVERSE(REVERSE(x)) = x
        String reversed = new StringBuilder(input).reverse().toString();
        String doubleReversed = new StringBuilder(reversed).reverse().toString();
        
        assertEquals(input, doubleReversed);
    }
    
    @Property
    void reverseLengthIsUnchanged(@ForAll String input) {
        // Property: LENGTH(REVERSE(x)) = LENGTH(x)
        String reversed = new StringBuilder(input).reverse().toString();
        
        assertEquals(input.length(), reversed.length());
    }
}
```

## Conclusion

The User Segmentation DSL is designed for extensibility. Adding new functions is straightforward:

1. Create a class extending `DSLFunction`
2. Implement three methods: `getName()`, `getFunctionMetadata()`, and `call()`
3. Register with `FunctionRegistry`
4. Write tests

This architecture makes it easy to grow the DSL's capabilities without increasing complexity.

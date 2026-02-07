package com.filter.dsl.unit.functions;

import com.filter.dsl.functions.FunctionRegistry;
import com.filter.dsl.functions.math.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for registering math functions with the FunctionRegistry.
 */
class MathFunctionsRegistrationTest {

    private FunctionRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new FunctionRegistry();
    }

    @Test
    void testRegisterAddFunction() {
        AddFunction add = new AddFunction();
        registry.register(add);
        
        assertTrue(registry.hasFunction("ADD"));
        assertEquals(add, registry.getFunction("ADD"));
        assertNotNull(registry.getMetadata("ADD"));
    }

    @Test
    void testRegisterSubtractFunction() {
        SubtractFunction subtract = new SubtractFunction();
        registry.register(subtract);
        
        assertTrue(registry.hasFunction("SUBTRACT"));
        assertEquals(subtract, registry.getFunction("SUBTRACT"));
        assertNotNull(registry.getMetadata("SUBTRACT"));
    }

    @Test
    void testRegisterMultiplyFunction() {
        MultiplyFunction multiply = new MultiplyFunction();
        registry.register(multiply);
        
        assertTrue(registry.hasFunction("MULTIPLY"));
        assertEquals(multiply, registry.getFunction("MULTIPLY"));
        assertNotNull(registry.getMetadata("MULTIPLY"));
    }

    @Test
    void testRegisterDivideFunction() {
        DivideFunction divide = new DivideFunction();
        registry.register(divide);
        
        assertTrue(registry.hasFunction("DIVIDE"));
        assertEquals(divide, registry.getFunction("DIVIDE"));
        assertNotNull(registry.getMetadata("DIVIDE"));
    }

    @Test
    void testRegisterModFunction() {
        ModFunction mod = new ModFunction();
        registry.register(mod);
        
        assertTrue(registry.hasFunction("MOD"));
        assertEquals(mod, registry.getFunction("MOD"));
        assertNotNull(registry.getMetadata("MOD"));
    }

    @Test
    void testRegisterAllMathFunctions() {
        registry.register(new AddFunction());
        registry.register(new SubtractFunction());
        registry.register(new MultiplyFunction());
        registry.register(new DivideFunction());
        registry.register(new ModFunction());
        
        assertEquals(5, registry.size());
        assertTrue(registry.hasFunction("ADD"));
        assertTrue(registry.hasFunction("SUBTRACT"));
        assertTrue(registry.hasFunction("MULTIPLY"));
        assertTrue(registry.hasFunction("DIVIDE"));
        assertTrue(registry.hasFunction("MOD"));
    }

    @Test
    void testAutoDiscoveryOfMathFunctions() {
        int count = registry.discoverAndRegister("com.filter.dsl.functions.math");
        
        // Should discover all 5 math functions
        assertTrue(count >= 5, "Expected at least 5 math functions, found: " + count);
        assertTrue(registry.hasFunction("ADD"));
        assertTrue(registry.hasFunction("SUBTRACT"));
        assertTrue(registry.hasFunction("MULTIPLY"));
        assertTrue(registry.hasFunction("DIVIDE"));
        assertTrue(registry.hasFunction("MOD"));
    }

    @Test
    void testFunctionMetadata() {
        registry.register(new AddFunction());
        registry.register(new SubtractFunction());
        registry.register(new MultiplyFunction());
        registry.register(new DivideFunction());
        registry.register(new ModFunction());
        
        // Verify all functions have proper metadata
        assertNotNull(registry.getMetadata("ADD"));
        assertNotNull(registry.getMetadata("SUBTRACT"));
        assertNotNull(registry.getMetadata("MULTIPLY"));
        assertNotNull(registry.getMetadata("DIVIDE"));
        assertNotNull(registry.getMetadata("MOD"));
        
        // Verify argument counts
        assertEquals(2, registry.getMetadata("ADD").getMinArgs());
        assertEquals(2, registry.getMetadata("ADD").getMaxArgs());
        assertEquals(2, registry.getMetadata("SUBTRACT").getMinArgs());
        assertEquals(2, registry.getMetadata("SUBTRACT").getMaxArgs());
        assertEquals(2, registry.getMetadata("MULTIPLY").getMinArgs());
        assertEquals(2, registry.getMetadata("MULTIPLY").getMaxArgs());
        assertEquals(2, registry.getMetadata("DIVIDE").getMinArgs());
        assertEquals(2, registry.getMetadata("DIVIDE").getMaxArgs());
        assertEquals(2, registry.getMetadata("MOD").getMinArgs());
        assertEquals(2, registry.getMetadata("MOD").getMaxArgs());
    }

    @Test
    void testDuplicateRegistrationThrowsException() {
        AddFunction add = new AddFunction();
        registry.register(add);
        
        assertThrows(IllegalArgumentException.class, () -> {
            registry.register(new AddFunction());
        });
    }

    // ========== Advanced Math Functions Registration Tests ==========

    @Test
    void testRegisterAbsFunction() {
        AbsFunction abs = new AbsFunction();
        registry.register(abs);
        
        assertTrue(registry.hasFunction("ABS"));
        assertEquals(abs, registry.getFunction("ABS"));
        assertNotNull(registry.getMetadata("ABS"));
    }

    @Test
    void testRegisterRoundFunction() {
        RoundFunction round = new RoundFunction();
        registry.register(round);
        
        assertTrue(registry.hasFunction("ROUND"));
        assertEquals(round, registry.getFunction("ROUND"));
        assertNotNull(registry.getMetadata("ROUND"));
    }

    @Test
    void testRegisterCeilFunction() {
        CeilFunction ceil = new CeilFunction();
        registry.register(ceil);
        
        assertTrue(registry.hasFunction("CEIL"));
        assertEquals(ceil, registry.getFunction("CEIL"));
        assertNotNull(registry.getMetadata("CEIL"));
    }

    @Test
    void testRegisterFloorFunction() {
        FloorFunction floor = new FloorFunction();
        registry.register(floor);
        
        assertTrue(registry.hasFunction("FLOOR"));
        assertEquals(floor, registry.getFunction("FLOOR"));
        assertNotNull(registry.getMetadata("FLOOR"));
    }

    @Test
    void testRegisterPowFunction() {
        PowFunction pow = new PowFunction();
        registry.register(pow);
        
        assertTrue(registry.hasFunction("POW"));
        assertEquals(pow, registry.getFunction("POW"));
        assertNotNull(registry.getMetadata("POW"));
    }

    @Test
    void testRegisterSqrtFunction() {
        SqrtFunction sqrt = new SqrtFunction();
        registry.register(sqrt);
        
        assertTrue(registry.hasFunction("SQRT"));
        assertEquals(sqrt, registry.getFunction("SQRT"));
        assertNotNull(registry.getMetadata("SQRT"));
    }

    @Test
    void testRegisterLogFunction() {
        LogFunction log = new LogFunction();
        registry.register(log);
        
        assertTrue(registry.hasFunction("LOG"));
        assertEquals(log, registry.getFunction("LOG"));
        assertNotNull(registry.getMetadata("LOG"));
    }

    @Test
    void testRegisterExpFunction() {
        ExpFunction exp = new ExpFunction();
        registry.register(exp);
        
        assertTrue(registry.hasFunction("EXP"));
        assertEquals(exp, registry.getFunction("EXP"));
        assertNotNull(registry.getMetadata("EXP"));
    }

    @Test
    void testRegisterAllAdvancedMathFunctions() {
        registry.register(new AbsFunction());
        registry.register(new RoundFunction());
        registry.register(new CeilFunction());
        registry.register(new FloorFunction());
        registry.register(new PowFunction());
        registry.register(new SqrtFunction());
        registry.register(new LogFunction());
        registry.register(new ExpFunction());
        
        assertEquals(8, registry.size());
        assertTrue(registry.hasFunction("ABS"));
        assertTrue(registry.hasFunction("ROUND"));
        assertTrue(registry.hasFunction("CEIL"));
        assertTrue(registry.hasFunction("FLOOR"));
        assertTrue(registry.hasFunction("POW"));
        assertTrue(registry.hasFunction("SQRT"));
        assertTrue(registry.hasFunction("LOG"));
        assertTrue(registry.hasFunction("EXP"));
    }

    @Test
    void testRegisterAllMathFunctionsIncludingAdvanced() {
        // Register basic arithmetic functions
        registry.register(new AddFunction());
        registry.register(new SubtractFunction());
        registry.register(new MultiplyFunction());
        registry.register(new DivideFunction());
        registry.register(new ModFunction());
        
        // Register advanced math functions
        registry.register(new AbsFunction());
        registry.register(new RoundFunction());
        registry.register(new CeilFunction());
        registry.register(new FloorFunction());
        registry.register(new PowFunction());
        registry.register(new SqrtFunction());
        registry.register(new LogFunction());
        registry.register(new ExpFunction());
        
        assertEquals(13, registry.size());
    }

    @Test
    void testAutoDiscoveryIncludesAdvancedMathFunctions() {
        int count = registry.discoverAndRegister("com.filter.dsl.functions.math");
        
        // Should discover all 13 math functions (5 basic + 8 advanced)
        assertTrue(count >= 13, "Expected at least 13 math functions, found: " + count);
        
        // Verify basic functions
        assertTrue(registry.hasFunction("ADD"));
        assertTrue(registry.hasFunction("SUBTRACT"));
        assertTrue(registry.hasFunction("MULTIPLY"));
        assertTrue(registry.hasFunction("DIVIDE"));
        assertTrue(registry.hasFunction("MOD"));
        
        // Verify advanced functions
        assertTrue(registry.hasFunction("ABS"));
        assertTrue(registry.hasFunction("ROUND"));
        assertTrue(registry.hasFunction("CEIL"));
        assertTrue(registry.hasFunction("FLOOR"));
        assertTrue(registry.hasFunction("POW"));
        assertTrue(registry.hasFunction("SQRT"));
        assertTrue(registry.hasFunction("LOG"));
        assertTrue(registry.hasFunction("EXP"));
    }

    @Test
    void testAdvancedMathFunctionMetadata() {
        registry.register(new AbsFunction());
        registry.register(new RoundFunction());
        registry.register(new CeilFunction());
        registry.register(new FloorFunction());
        registry.register(new PowFunction());
        registry.register(new SqrtFunction());
        registry.register(new LogFunction());
        registry.register(new ExpFunction());
        
        // Verify all functions have proper metadata
        assertNotNull(registry.getMetadata("ABS"));
        assertNotNull(registry.getMetadata("ROUND"));
        assertNotNull(registry.getMetadata("CEIL"));
        assertNotNull(registry.getMetadata("FLOOR"));
        assertNotNull(registry.getMetadata("POW"));
        assertNotNull(registry.getMetadata("SQRT"));
        assertNotNull(registry.getMetadata("LOG"));
        assertNotNull(registry.getMetadata("EXP"));
        
        // Verify argument counts for single-argument functions
        assertEquals(1, registry.getMetadata("ABS").getMinArgs());
        assertEquals(1, registry.getMetadata("ABS").getMaxArgs());
        assertEquals(1, registry.getMetadata("CEIL").getMinArgs());
        assertEquals(1, registry.getMetadata("CEIL").getMaxArgs());
        assertEquals(1, registry.getMetadata("FLOOR").getMinArgs());
        assertEquals(1, registry.getMetadata("FLOOR").getMaxArgs());
        assertEquals(1, registry.getMetadata("SQRT").getMinArgs());
        assertEquals(1, registry.getMetadata("SQRT").getMaxArgs());
        assertEquals(1, registry.getMetadata("EXP").getMinArgs());
        assertEquals(1, registry.getMetadata("EXP").getMaxArgs());
        
        // Verify argument counts for two-argument functions
        assertEquals(2, registry.getMetadata("POW").getMinArgs());
        assertEquals(2, registry.getMetadata("POW").getMaxArgs());
        
        // Verify argument counts for variable-argument functions
        assertEquals(1, registry.getMetadata("ROUND").getMinArgs());
        assertEquals(2, registry.getMetadata("ROUND").getMaxArgs());
        assertEquals(1, registry.getMetadata("LOG").getMinArgs());
        assertEquals(2, registry.getMetadata("LOG").getMaxArgs());
    }
}

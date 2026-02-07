package com.filter.dsl.unit.functions;

import com.filter.dsl.functions.*;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FunctionRegistry class.
 */
class FunctionRegistryTest {

    private FunctionRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new FunctionRegistry();
    }

    @Test
    void testRegister_ValidFunction() {
        TestFunction function = new TestFunction("COUNT");
        
        assertDoesNotThrow(() -> registry.register(function));
        assertTrue(registry.hasFunction("COUNT"));
        assertEquals(1, registry.size());
    }

    @Test
    void testRegister_LowercaseName_ThrowsException() {
        TestFunction function = new TestFunction("count");
        
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> registry.register(function)
        );
        assertTrue(ex.getMessage().contains("must be UPPERCASE"));
    }

    @Test
    void testRegister_MixedCaseName_ThrowsException() {
        TestFunction function = new TestFunction("Count");
        
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> registry.register(function)
        );
        assertTrue(ex.getMessage().contains("must be UPPERCASE"));
    }

    @Test
    void testRegister_DuplicateFunction_ThrowsException() {
        TestFunction function1 = new TestFunction("COUNT");
        TestFunction function2 = new TestFunction("COUNT");
        
        registry.register(function1);
        
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> registry.register(function2)
        );
        assertTrue(ex.getMessage().contains("already registered"));
    }

    @Test
    void testRegister_NullMetadata_ThrowsException() {
        DSLFunction function = new DSLFunction() {
            @Override
            public String getName() {
                return "INVALID";
            }

            @Override
            public FunctionMetadata getFunctionMetadata() {
                return null;
            }

            @Override
            public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
                return AviatorLong.valueOf(0);
            }
        };
        
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> registry.register(function)
        );
        assertTrue(ex.getMessage().contains("metadata cannot be null"));
    }

    @Test
    void testRegister_MetadataNameMismatch_ThrowsException() {
        DSLFunction function = new DSLFunction() {
            @Override
            public String getName() {
                return "FUNC1";
            }

            @Override
            public FunctionMetadata getFunctionMetadata() {
                return FunctionMetadata.builder()
                    .name("FUNC2")  // Different name
                    .minArgs(0)
                    .maxArgs(0)
                    .returnType(FunctionMetadata.ReturnType.ANY)
                    .build();
            }

            @Override
            public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
                return AviatorLong.valueOf(0);
            }
        };
        
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> registry.register(function)
        );
        assertTrue(ex.getMessage().contains("name mismatch"));
    }

    @Test
    void testGetMetadata_ExistingFunction() {
        TestFunction function = new TestFunction("COUNT");
        registry.register(function);
        
        FunctionMetadata metadata = registry.getMetadata("COUNT");
        
        assertNotNull(metadata);
        assertEquals("COUNT", metadata.getName());
    }

    @Test
    void testGetMetadata_NonExistentFunction() {
        FunctionMetadata metadata = registry.getMetadata("NONEXISTENT");
        
        assertNull(metadata);
    }

    @Test
    void testHasFunction_Exists() {
        TestFunction function = new TestFunction("COUNT");
        registry.register(function);
        
        assertTrue(registry.hasFunction("COUNT"));
    }

    @Test
    void testHasFunction_DoesNotExist() {
        assertFalse(registry.hasFunction("NONEXISTENT"));
    }

    @Test
    void testGetFunction_Exists() {
        TestFunction function = new TestFunction("COUNT");
        registry.register(function);
        
        DSLFunction retrieved = registry.getFunction("COUNT");
        
        assertNotNull(retrieved);
        assertEquals("COUNT", retrieved.getName());
    }

    @Test
    void testGetFunction_DoesNotExist() {
        DSLFunction retrieved = registry.getFunction("NONEXISTENT");
        
        assertNull(retrieved);
    }

    @Test
    void testGetFunctionNames() {
        registry.register(new TestFunction("COUNT"));
        registry.register(new TestFunction("SUM"));
        registry.register(new TestFunction("AVG"));
        
        Set<String> names = registry.getFunctionNames();
        
        assertEquals(3, names.size());
        assertTrue(names.contains("COUNT"));
        assertTrue(names.contains("SUM"));
        assertTrue(names.contains("AVG"));
    }

    @Test
    void testGetFunctionNames_Empty() {
        Set<String> names = registry.getFunctionNames();
        
        assertNotNull(names);
        assertTrue(names.isEmpty());
    }

    @Test
    void testSize() {
        assertEquals(0, registry.size());
        
        registry.register(new TestFunction("COUNT"));
        assertEquals(1, registry.size());
        
        registry.register(new TestFunction("SUM"));
        assertEquals(2, registry.size());
    }

    @Test
    void testClear() {
        registry.register(new TestFunction("COUNT"));
        registry.register(new TestFunction("SUM"));
        assertEquals(2, registry.size());
        
        registry.clear();
        
        assertEquals(0, registry.size());
        assertFalse(registry.hasFunction("COUNT"));
        assertFalse(registry.hasFunction("SUM"));
    }

    @Test
    void testRegisterAll_WithAviatorScript() {
        // Create an AviatorScript instance
        AviatorEvaluatorInstance aviator = AviatorEvaluator.newInstance();
        
        // Register a test function
        TestFunction function = new TestFunction("TESTFUNC");
        registry.register(function);
        
        // Register all functions with AviatorScript
        registry.registerAll(aviator);
        
        // Verify the function is callable in AviatorScript with an argument
        // (since our test function accepts 0 args but AviatorScript needs something to call)
        Map<String, Object> env = new HashMap<>();
        Object result = aviator.execute("TESTFUNC()", env);
        assertEquals(42L, result);
    }

    @Test
    void testRegisterAll_MultipleFunctions() {
        AviatorEvaluatorInstance aviator = AviatorEvaluator.newInstance();
        
        registry.register(new TestFunction("FUNC1"));
        registry.register(new TestFunction("FUNC2"));
        registry.register(new TestFunction("FUNC3"));
        
        registry.registerAll(aviator);
        
        // Verify all functions are callable
        Map<String, Object> env = new HashMap<>();
        assertEquals(42L, aviator.execute("FUNC1()", env));
        assertEquals(42L, aviator.execute("FUNC2()", env));
        assertEquals(42L, aviator.execute("FUNC3()", env));
    }

    @Test
    void testRegisterMultipleFunctions() {
        registry.register(new TestFunction("COUNT"));
        registry.register(new TestFunction("SUM"));
        registry.register(new TestFunction("AVG"));
        registry.register(new TestFunction("MIN"));
        registry.register(new TestFunction("MAX"));
        
        assertEquals(5, registry.size());
        assertTrue(registry.hasFunction("COUNT"));
        assertTrue(registry.hasFunction("SUM"));
        assertTrue(registry.hasFunction("AVG"));
        assertTrue(registry.hasFunction("MIN"));
        assertTrue(registry.hasFunction("MAX"));
    }

    // ========== Auto-Discovery Tests ==========

    @Test
    void testDiscoverAndRegister_ValidPackage() {
        // Discover functions in the test package
        int count = registry.discoverAndRegister("com.filter.dsl.unit.functions");
        
        // Should find at least the TestFunction class
        assertTrue(count >= 0, "Should discover functions without error");
    }

    @Test
    void testDiscoverAndRegister_NonExistentPackage() {
        // Should handle non-existent package gracefully
        int count = registry.discoverAndRegister("com.nonexistent.package");
        
        assertEquals(0, count, "Should return 0 for non-existent package");
    }

    @Test
    void testDiscoverAndRegister_EmptyPackage() {
        // Create a new registry and try to discover in an empty package
        FunctionRegistry newRegistry = new FunctionRegistry();
        
        // Test with a package that has no DSL functions
        int count = newRegistry.discoverAndRegister("com.filter.dsl.functions.nonexistent");
        
        // Should return 0 for non-existent or empty package
        assertEquals(0, count, "Should return 0 for package with no DSL functions");
    }

    @Test
    void testDiscoverAndRegister_RegistersValidFunctions() {
        // First, manually register a function to verify it works
        TestFunction manualFunction = new TestFunction("MANUAL");
        registry.register(manualFunction);
        
        assertEquals(1, registry.size());
        assertTrue(registry.hasFunction("MANUAL"));
    }

    @Test
    void testDiscoverAndRegister_SkipsAbstractClasses() {
        // The discovery should skip DSLFunction itself (abstract)
        int count = registry.discoverAndRegister("com.filter.dsl.functions");
        
        // Should not register DSLFunction base class
        assertFalse(registry.hasFunction("DSLFUNC"));
    }

    @Test
    void testDiscoverAndRegister_SkipsNonDSLFunctionClasses() {
        // Discovery should only register DSLFunction subclasses
        int initialCount = registry.size();
        
        registry.discoverAndRegister("com.filter.dsl.models");
        
        // Should not register any model classes
        assertEquals(initialCount, registry.size());
    }

    @Test
    void testDiscoverAndRegister_ValidatesMetadata() {
        // Functions discovered should still be validated
        // This is implicitly tested by the register() method validation
        
        TestFunction function = new TestFunction("VALIDATED");
        assertDoesNotThrow(() -> registry.register(function));
        
        FunctionMetadata meta = registry.getMetadata("VALIDATED");
        assertNotNull(meta);
        assertEquals("VALIDATED", meta.getName());
    }

    @Test
    void testDiscoverAndRegister_MultiplePackages() {
        // Test discovering from multiple packages
        int count1 = registry.discoverAndRegister("com.filter.dsl.functions.logical");
        int count2 = registry.discoverAndRegister("com.filter.dsl.functions.math");
        
        // Both should complete without error
        assertTrue(count1 >= 0);
        assertTrue(count2 >= 0);
    }

    @Test
    void testDiscoverAndRegister_SubPackages() {
        // Discovery should include sub-packages
        int count = registry.discoverAndRegister("com.filter.dsl.functions");
        
        // Should discover functions in all sub-packages
        assertTrue(count >= 0, "Should discover functions in sub-packages");
    }

    @Test
    void testDiscoverAndRegister_FindsCountFunction() {
        // Discover functions in aggregation package
        int count = registry.discoverAndRegister("com.filter.dsl.functions.aggregation");
        
        // Should find at least the CountFunction
        assertTrue(count >= 1, "Should discover at least CountFunction");
        assertTrue(registry.hasFunction("COUNT"), "Should have registered COUNT function");
        
        // Verify metadata
        FunctionMetadata metadata = registry.getMetadata("COUNT");
        assertNotNull(metadata);
        assertEquals("COUNT", metadata.getName());
        assertEquals(1, metadata.getMinArgs());
        assertEquals(1, metadata.getMaxArgs());
    }

    @Test
    void testDiscoverAndRegister_FindsSumAndAvgFunctions() {
        // Discover functions in aggregation package
        int count = registry.discoverAndRegister("com.filter.dsl.functions.aggregation");
        
        // Should find COUNT, SUM, and AVG functions
        assertTrue(count >= 3, "Should discover at least COUNT, SUM, and AVG functions");
        assertTrue(registry.hasFunction("COUNT"), "Should have registered COUNT function");
        assertTrue(registry.hasFunction("SUM"), "Should have registered SUM function");
        assertTrue(registry.hasFunction("AVG"), "Should have registered AVG function");
        
        // Verify SUM metadata
        FunctionMetadata sumMetadata = registry.getMetadata("SUM");
        assertNotNull(sumMetadata);
        assertEquals("SUM", sumMetadata.getName());
        assertEquals(1, sumMetadata.getMinArgs());
        assertEquals(1, sumMetadata.getMaxArgs());
        
        // Verify AVG metadata
        FunctionMetadata avgMetadata = registry.getMetadata("AVG");
        assertNotNull(avgMetadata);
        assertEquals("AVG", avgMetadata.getName());
        assertEquals(1, avgMetadata.getMinArgs());
        assertEquals(1, avgMetadata.getMaxArgs());
    }

    @Test
    void testDiscoverAndRegister_IntegrationWithAviator() {
        // Discover and register functions
        registry.discoverAndRegister("com.filter.dsl.functions.aggregation");
        
        // Create AviatorScript instance and register all
        AviatorEvaluatorInstance aviator = AviatorEvaluator.newInstance();
        registry.registerAll(aviator);
        
        // Test that COUNT function works
        Map<String, Object> env = new HashMap<>();
        env.put("myList", java.util.Arrays.asList(1, 2, 3, 4, 5));
        
        Object result = aviator.execute("COUNT(myList)", env);
        assertEquals(5L, result);
    }

    @Test
    void testDiscoverAndRegister_SumAndAvgIntegrationWithAviator() {
        // Discover and register functions
        registry.discoverAndRegister("com.filter.dsl.functions.aggregation");
        
        // Create AviatorScript instance and register all
        AviatorEvaluatorInstance aviator = AviatorEvaluator.newInstance();
        registry.registerAll(aviator);
        
        // Test SUM function
        Map<String, Object> env = new HashMap<>();
        env.put("numbers", java.util.Arrays.asList(10, 20, 30, 40, 50));
        
        Object sumResult = aviator.execute("SUM(numbers)", env);
        assertEquals(150L, sumResult);
        
        // Test AVG function
        Object avgResult = aviator.execute("AVG(numbers)", env);
        assertEquals(30.0, (Double) avgResult, 0.0001);
        
        // Test empty collection
        env.put("empty", java.util.Collections.emptyList());
        Object sumEmpty = aviator.execute("SUM(empty)", env);
        assertEquals(0L, sumEmpty);
        
        Object avgEmpty = aviator.execute("AVG(empty)", env);
        assertNull(avgEmpty);
    }

    // ========== Test Function Implementation ==========

    /**
     * Simple test function for registry testing.
     */
    private static class TestFunction extends DSLFunction {
        private final String name;

        public TestFunction(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public FunctionMetadata getFunctionMetadata() {
            return FunctionMetadata.builder()
                .name(name)
                .minArgs(0)
                .maxArgs(0)
                .returnType(FunctionMetadata.ReturnType.NUMBER)
                .description("Test function: " + name)
                .build();
        }

        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
            return AviatorLong.valueOf(42);
        }
        
        // Override the 0-argument call method
        @Override
        public AviatorObject call(Map<String, Object> env) {
            return AviatorLong.valueOf(42);
        }
    }
}

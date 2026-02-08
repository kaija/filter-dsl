package com.filter.dsl.unit.functions;

import com.filter.dsl.functions.FunctionRegistry;
import com.filter.dsl.functions.aggregation.CountFunction;
import com.filter.dsl.functions.aggregation.SumFunction;
import com.filter.dsl.functions.aggregation.AvgFunction;
import com.filter.dsl.functions.aggregation.MinFunction;
import com.filter.dsl.functions.aggregation.MaxFunction;
import com.filter.dsl.functions.aggregation.UniqueFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for registering aggregation functions with the FunctionRegistry.
 */
class AggregationFunctionsRegistrationTest {

    private FunctionRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new FunctionRegistry();
    }

    @Test
    void testRegisterCountFunction() {
        CountFunction count = new CountFunction();
        registry.register(count);
        
        assertTrue(registry.hasFunction("COUNT"));
        assertEquals(count, registry.getFunction("COUNT"));
        assertNotNull(registry.getMetadata("COUNT"));
    }

    @Test
    void testCountFunctionMetadata() {
        CountFunction count = new CountFunction();
        registry.register(count);
        
        assertNotNull(registry.getMetadata("COUNT"));
        assertEquals("COUNT", registry.getMetadata("COUNT").getName());
        assertEquals(0, registry.getMetadata("COUNT").getMinArgs());
        assertEquals(2, registry.getMetadata("COUNT").getMaxArgs());
        assertTrue(registry.getMetadata("COUNT").getDescription().contains("userData.events"));
    }

    @Test
    void testDuplicateCountRegistrationThrowsException() {
        CountFunction count = new CountFunction();
        registry.register(count);
        
        assertThrows(IllegalArgumentException.class, () -> {
            registry.register(new CountFunction());
        });
    }

    @Test
    void testAutoDiscoveryOfAggregationFunctions() {
        int count = registry.discoverAndRegister("com.filter.dsl.functions.aggregation");
        
        // Should discover COUNT, SUM, AVG, MIN, MAX, and UNIQUE functions
        assertTrue(count >= 6, "Expected at least 6 aggregation functions, found: " + count);
        assertTrue(registry.hasFunction("COUNT"));
        assertTrue(registry.hasFunction("SUM"));
        assertTrue(registry.hasFunction("AVG"));
        assertTrue(registry.hasFunction("MIN"));
        assertTrue(registry.hasFunction("MAX"));
        assertTrue(registry.hasFunction("UNIQUE"));
    }

    @Test
    void testRegisterSumFunction() {
        SumFunction sum = new SumFunction();
        registry.register(sum);
        
        assertTrue(registry.hasFunction("SUM"));
        assertEquals(sum, registry.getFunction("SUM"));
        assertNotNull(registry.getMetadata("SUM"));
    }

    @Test
    void testSumFunctionMetadata() {
        SumFunction sum = new SumFunction();
        registry.register(sum);
        
        assertNotNull(registry.getMetadata("SUM"));
        assertEquals("SUM", registry.getMetadata("SUM").getName());
        assertEquals(0, registry.getMetadata("SUM").getMinArgs());
        assertEquals(2, registry.getMetadata("SUM").getMaxArgs());
        assertTrue(registry.getMetadata("SUM").getDescription().contains("userData.events"));
    }

    @Test
    void testRegisterAvgFunction() {
        AvgFunction avg = new AvgFunction();
        registry.register(avg);
        
        assertTrue(registry.hasFunction("AVG"));
        assertEquals(avg, registry.getFunction("AVG"));
        assertNotNull(registry.getMetadata("AVG"));
    }

    @Test
    void testAvgFunctionMetadata() {
        AvgFunction avg = new AvgFunction();
        registry.register(avg);
        
        assertNotNull(registry.getMetadata("AVG"));
        assertEquals("AVG", registry.getMetadata("AVG").getName());
        assertEquals(0, registry.getMetadata("AVG").getMinArgs());
        assertEquals(2, registry.getMetadata("AVG").getMaxArgs());
        assertTrue(registry.getMetadata("AVG").getDescription().contains("userData.events"));
    }

    @Test
    void testRegisterAllAggregationFunctions() {
        registry.register(new CountFunction());
        registry.register(new SumFunction());
        registry.register(new AvgFunction());
        registry.register(new MinFunction());
        registry.register(new MaxFunction());
        registry.register(new UniqueFunction());
        
        assertEquals(6, registry.size());
        assertTrue(registry.hasFunction("COUNT"));
        assertTrue(registry.hasFunction("SUM"));
        assertTrue(registry.hasFunction("AVG"));
        assertTrue(registry.hasFunction("MIN"));
        assertTrue(registry.hasFunction("MAX"));
        assertTrue(registry.hasFunction("UNIQUE"));
    }

    @Test
    void testCountFunctionIsRegisteredCorrectly() {
        registry.register(new CountFunction());
        
        // Verify the function is accessible
        assertTrue(registry.hasFunction("COUNT"));
        assertNotNull(registry.getFunction("COUNT"));
        
        // Verify it's the correct type
        assertTrue(registry.getFunction("COUNT") instanceof CountFunction);
    }

    @Test
    void testRegistrySize() {
        assertEquals(0, registry.size());
        
        registry.register(new CountFunction());
        assertEquals(1, registry.size());
    }

    @Test
    void testClearRegistry() {
        registry.register(new CountFunction());
        assertEquals(1, registry.size());
        
        registry.clear();
        assertEquals(0, registry.size());
        assertFalse(registry.hasFunction("COUNT"));
    }

    @Test
    void testGetFunctionNames() {
        registry.register(new CountFunction());
        
        assertTrue(registry.getFunctionNames().contains("COUNT"));
        assertEquals(1, registry.getFunctionNames().size());
    }

    @Test
    void testGetNonExistentFunction() {
        assertNull(registry.getFunction("NONEXISTENT"));
        assertNull(registry.getMetadata("NONEXISTENT"));
        assertFalse(registry.hasFunction("NONEXISTENT"));
    }

    @Test
    void testRegisterMinFunction() {
        MinFunction min = new MinFunction();
        registry.register(min);
        
        assertTrue(registry.hasFunction("MIN"));
        assertEquals(min, registry.getFunction("MIN"));
        assertNotNull(registry.getMetadata("MIN"));
    }

    @Test
    void testMinFunctionMetadata() {
        MinFunction min = new MinFunction();
        registry.register(min);
        
        assertNotNull(registry.getMetadata("MIN"));
        assertEquals("MIN", registry.getMetadata("MIN").getName());
        assertEquals(0, registry.getMetadata("MIN").getMinArgs());
        assertEquals(2, registry.getMetadata("MIN").getMaxArgs());
        assertTrue(registry.getMetadata("MIN").getDescription().contains("userData.events"));
    }

    @Test
    void testRegisterMaxFunction() {
        MaxFunction max = new MaxFunction();
        registry.register(max);
        
        assertTrue(registry.hasFunction("MAX"));
        assertEquals(max, registry.getFunction("MAX"));
        assertNotNull(registry.getMetadata("MAX"));
    }

    @Test
    void testMaxFunctionMetadata() {
        MaxFunction max = new MaxFunction();
        registry.register(max);
        
        assertNotNull(registry.getMetadata("MAX"));
        assertEquals("MAX", registry.getMetadata("MAX").getName());
        assertEquals(0, registry.getMetadata("MAX").getMinArgs());
        assertEquals(2, registry.getMetadata("MAX").getMaxArgs());
        assertTrue(registry.getMetadata("MAX").getDescription().contains("userData.events"));
    }

    @Test
    void testMinFunctionIsRegisteredCorrectly() {
        registry.register(new MinFunction());
        
        // Verify the function is accessible
        assertTrue(registry.hasFunction("MIN"));
        assertNotNull(registry.getFunction("MIN"));
        
        // Verify it's the correct type
        assertTrue(registry.getFunction("MIN") instanceof MinFunction);
    }

    @Test
    void testMaxFunctionIsRegisteredCorrectly() {
        registry.register(new MaxFunction());
        
        // Verify the function is accessible
        assertTrue(registry.hasFunction("MAX"));
        assertNotNull(registry.getFunction("MAX"));
        
        // Verify it's the correct type
        assertTrue(registry.getFunction("MAX") instanceof MaxFunction);
    }

    @Test
    void testRegisterUniqueFunction() {
        UniqueFunction unique = new UniqueFunction();
        registry.register(unique);
        
        assertTrue(registry.hasFunction("UNIQUE"));
        assertEquals(unique, registry.getFunction("UNIQUE"));
        assertNotNull(registry.getMetadata("UNIQUE"));
    }

    @Test
    void testUniqueFunctionMetadata() {
        UniqueFunction unique = new UniqueFunction();
        registry.register(unique);
        
        assertNotNull(registry.getMetadata("UNIQUE"));
        assertEquals("UNIQUE", registry.getMetadata("UNIQUE").getName());
        assertEquals(0, registry.getMetadata("UNIQUE").getMinArgs());
        assertEquals(2, registry.getMetadata("UNIQUE").getMaxArgs());
        assertTrue(registry.getMetadata("UNIQUE").getDescription().contains("userData.events"));
    }

    @Test
    void testUniqueFunctionIsRegisteredCorrectly() {
        registry.register(new UniqueFunction());
        
        // Verify the function is accessible
        assertTrue(registry.hasFunction("UNIQUE"));
        assertNotNull(registry.getFunction("UNIQUE"));
        
        // Verify it's the correct type
        assertTrue(registry.getFunction("UNIQUE") instanceof UniqueFunction);
    }

    @Test
    void testDuplicateUniqueRegistrationThrowsException() {
        UniqueFunction unique = new UniqueFunction();
        registry.register(unique);
        
        assertThrows(IllegalArgumentException.class, () -> {
            registry.register(new UniqueFunction());
        });
    }
}

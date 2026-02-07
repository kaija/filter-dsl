package com.filter.dsl.unit.functions;

import com.filter.dsl.functions.FunctionRegistry;
import com.filter.dsl.functions.filtering.ByFunction;
import com.filter.dsl.functions.filtering.IfFunction;
import com.filter.dsl.functions.filtering.WhereFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for filtering function registration with FunctionRegistry.
 */
class FilteringFunctionsRegistrationTest {

    private FunctionRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new FunctionRegistry();
    }

    @Test
    void testRegisterIfFunction() {
        IfFunction ifFunc = new IfFunction();
        registry.register(ifFunc);
        
        assertTrue(registry.hasFunction("IF"));
        assertEquals(ifFunc, registry.getFunction("IF"));
        assertNotNull(registry.getMetadata("IF"));
    }

    @Test
    void testRegisterWhereFunction() {
        WhereFunction whereFunc = new WhereFunction();
        registry.register(whereFunc);
        
        assertTrue(registry.hasFunction("WHERE"));
        assertEquals(whereFunc, registry.getFunction("WHERE"));
        assertNotNull(registry.getMetadata("WHERE"));
    }

    @Test
    void testRegisterAllFilteringFunctions() {
        registry.register(new IfFunction());
        registry.register(new WhereFunction());
        registry.register(new ByFunction());
        
        assertEquals(3, registry.size());
        assertTrue(registry.hasFunction("IF"));
        assertTrue(registry.hasFunction("WHERE"));
        assertTrue(registry.hasFunction("BY"));
    }

    @Test
    void testIfFunctionMetadata() {
        IfFunction ifFunc = new IfFunction();
        registry.register(ifFunc);
        
        var metadata = registry.getMetadata("IF");
        assertNotNull(metadata);
        assertEquals("IF", metadata.getName());
        assertEquals(1, metadata.getMinArgs());
        assertEquals(1, metadata.getMaxArgs());
        assertNotNull(metadata.getDescription());
    }

    @Test
    void testWhereFunctionMetadata() {
        WhereFunction whereFunc = new WhereFunction();
        registry.register(whereFunc);
        
        var metadata = registry.getMetadata("WHERE");
        assertNotNull(metadata);
        assertEquals("WHERE", metadata.getName());
        assertEquals(2, metadata.getMinArgs());
        assertEquals(2, metadata.getMaxArgs());
        assertNotNull(metadata.getDescription());
    }

    @Test
    void testDiscoverFilteringFunctions() {
        int count = registry.discoverAndRegister("com.filter.dsl.functions.filtering");
        
        // Should discover IF, WHERE, and BY functions
        assertTrue(count >= 3, "Should discover at least IF, WHERE, and BY functions");
        assertTrue(registry.hasFunction("IF"));
        assertTrue(registry.hasFunction("WHERE"));
        assertTrue(registry.hasFunction("BY"));
    }

    @Test
    void testDuplicateRegistrationThrowsException() {
        IfFunction ifFunc = new IfFunction();
        registry.register(ifFunc);
        
        assertThrows(IllegalArgumentException.class, () -> {
            registry.register(new IfFunction());
        });
    }

    @Test
    void testGetNonExistentFunction() {
        assertNull(registry.getFunction("NONEXISTENT"));
        assertNull(registry.getMetadata("NONEXISTENT"));
        assertFalse(registry.hasFunction("NONEXISTENT"));
    }

    @Test
    void testClearRegistry() {
        registry.register(new IfFunction());
        registry.register(new WhereFunction());
        
        assertEquals(2, registry.size());
        
        registry.clear();
        
        assertEquals(0, registry.size());
        assertFalse(registry.hasFunction("IF"));
        assertFalse(registry.hasFunction("WHERE"));
    }

    @Test
    void testGetFunctionNames() {
        registry.register(new IfFunction());
        registry.register(new WhereFunction());
        registry.register(new ByFunction());
        
        var names = registry.getFunctionNames();
        assertEquals(3, names.size());
        assertTrue(names.contains("IF"));
        assertTrue(names.contains("WHERE"));
        assertTrue(names.contains("BY"));
    }
    
    @Test
    void testRegisterByFunction() {
        ByFunction byFunc = new ByFunction();
        registry.register(byFunc);
        
        assertTrue(registry.hasFunction("BY"));
        assertEquals(byFunc, registry.getFunction("BY"));
        assertNotNull(registry.getMetadata("BY"));
    }
    
    @Test
    void testByFunctionMetadata() {
        ByFunction byFunc = new ByFunction();
        registry.register(byFunc);
        
        var metadata = registry.getMetadata("BY");
        assertNotNull(metadata);
        assertEquals("BY", metadata.getName());
        assertEquals(1, metadata.getMinArgs());
        assertEquals(1, metadata.getMaxArgs());
        assertNotNull(metadata.getDescription());
    }
}

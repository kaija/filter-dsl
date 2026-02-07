package com.filter.dsl.unit.functions;

import com.filter.dsl.functions.FunctionRegistry;
import com.filter.dsl.functions.data.ProfileFunction;
import com.filter.dsl.functions.data.EventFunction;
import com.filter.dsl.functions.data.ParamFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for registering data access functions with the FunctionRegistry.
 */
class DataAccessFunctionsRegistrationTest {

    private FunctionRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new FunctionRegistry();
    }

    @Test
    void testRegisterProfileFunction() {
        ProfileFunction profile = new ProfileFunction();
        registry.register(profile);
        
        assertTrue(registry.hasFunction("PROFILE"));
        assertEquals(1, registry.size());
    }

    @Test
    void testRegisterEventFunction() {
        EventFunction event = new EventFunction();
        registry.register(event);
        
        assertTrue(registry.hasFunction("EVENT"));
        assertEquals(1, registry.size());
    }

    @Test
    void testRegisterParamFunction() {
        ParamFunction param = new ParamFunction();
        registry.register(param);
        
        assertTrue(registry.hasFunction("PARAM"));
        assertEquals(1, registry.size());
    }

    @Test
    void testRegisterAllDataAccessFunctions() {
        registry.register(new ProfileFunction());
        registry.register(new EventFunction());
        registry.register(new ParamFunction());
        
        assertEquals(3, registry.size());
        assertTrue(registry.hasFunction("PROFILE"));
        assertTrue(registry.hasFunction("EVENT"));
        assertTrue(registry.hasFunction("PARAM"));
    }

    @Test
    void testAutoDiscoveryOfDataAccessFunctions() {
        int count = registry.discoverAndRegister("com.filter.dsl.functions.data");
        
        // Should discover all 3 data access functions
        assertEquals(3, count);
        assertTrue(registry.hasFunction("PROFILE"));
        assertTrue(registry.hasFunction("EVENT"));
        assertTrue(registry.hasFunction("PARAM"));
    }

    @Test
    void testFunctionMetadata() {
        registry.register(new ProfileFunction());
        registry.register(new EventFunction());
        registry.register(new ParamFunction());
        
        // Verify all functions have proper metadata
        assertNotNull(registry.getMetadata("PROFILE"));
        assertNotNull(registry.getMetadata("EVENT"));
        assertNotNull(registry.getMetadata("PARAM"));
        
        // Verify metadata details
        assertEquals("PROFILE", registry.getMetadata("PROFILE").getName());
        assertEquals("EVENT", registry.getMetadata("EVENT").getName());
        assertEquals("PARAM", registry.getMetadata("PARAM").getName());
        
        // All should accept exactly 1 argument
        assertEquals(1, registry.getMetadata("PROFILE").getMinArgs());
        assertEquals(1, registry.getMetadata("PROFILE").getMaxArgs());
        assertEquals(1, registry.getMetadata("EVENT").getMinArgs());
        assertEquals(1, registry.getMetadata("EVENT").getMaxArgs());
        assertEquals(1, registry.getMetadata("PARAM").getMinArgs());
        assertEquals(1, registry.getMetadata("PARAM").getMaxArgs());
    }

    @Test
    void testDuplicateRegistrationThrowsException() {
        registry.register(new ProfileFunction());
        
        assertThrows(IllegalArgumentException.class, () -> {
            registry.register(new ProfileFunction());
        });
    }

    @Test
    void testGetRegisteredFunction() {
        ProfileFunction profile = new ProfileFunction();
        registry.register(profile);
        
        assertNotNull(registry.getFunction("PROFILE"));
        assertEquals("PROFILE", registry.getFunction("PROFILE").getName());
    }

    @Test
    void testGetNonExistentFunction() {
        assertNull(registry.getFunction("NONEXISTENT"));
        assertNull(registry.getMetadata("NONEXISTENT"));
    }

    @Test
    void testFunctionNames() {
        registry.register(new ProfileFunction());
        registry.register(new EventFunction());
        registry.register(new ParamFunction());
        
        var names = registry.getFunctionNames();
        assertEquals(3, names.size());
        assertTrue(names.contains("PROFILE"));
        assertTrue(names.contains("EVENT"));
        assertTrue(names.contains("PARAM"));
    }

    @Test
    void testClearRegistry() {
        registry.register(new ProfileFunction());
        registry.register(new EventFunction());
        registry.register(new ParamFunction());
        
        assertEquals(3, registry.size());
        
        registry.clear();
        
        assertEquals(0, registry.size());
        assertFalse(registry.hasFunction("PROFILE"));
        assertFalse(registry.hasFunction("EVENT"));
        assertFalse(registry.hasFunction("PARAM"));
    }

    @Test
    void testFunctionDescriptions() {
        registry.register(new ProfileFunction());
        registry.register(new EventFunction());
        registry.register(new ParamFunction());
        
        assertEquals("Returns the value of a field from the user profile", 
            registry.getMetadata("PROFILE").getDescription());
        assertEquals("Returns the value of a field from the current event", 
            registry.getMetadata("EVENT").getDescription());
        assertEquals("Returns the value of a parameter from the current event", 
            registry.getMetadata("PARAM").getDescription());
    }
}

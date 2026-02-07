package com.filter.dsl.unit.functions;

import com.filter.dsl.functions.FunctionRegistry;
import com.filter.dsl.functions.segmentation.BucketFunction;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for registering segmentation functions with the FunctionRegistry.
 */
class SegmentationFunctionsRegistrationTest {

    private FunctionRegistry registry;
    private AviatorEvaluatorInstance aviator;

    @BeforeEach
    void setUp() {
        registry = new FunctionRegistry();
        aviator = AviatorEvaluator.newInstance();
    }

    @Test
    void testRegisterBucketFunction() {
        BucketFunction bucket = new BucketFunction();
        
        registry.register(bucket);
        
        assertTrue(registry.hasFunction("BUCKET"));
        assertEquals(bucket, registry.getFunction("BUCKET"));
    }

    @Test
    void testBucketFunctionMetadata() {
        BucketFunction bucket = new BucketFunction();
        
        registry.register(bucket);
        
        var metadata = registry.getMetadata("BUCKET");
        assertNotNull(metadata);
        assertEquals("BUCKET", metadata.getName());
        assertEquals(2, metadata.getMinArgs());
        assertEquals(2, metadata.getMaxArgs());
        assertEquals("Assigns a value to a bucket based on range definitions", metadata.getDescription());
    }

    @Test
    void testDuplicateBucketRegistrationThrowsException() {
        BucketFunction bucket = new BucketFunction();
        
        registry.register(bucket);
        
        assertThrows(IllegalArgumentException.class, () -> {
            registry.register(new BucketFunction());
        });
    }

    @Test
    void testBucketFunctionWithAviatorScript() {
        // Register function
        registry.register(new BucketFunction());
        registry.registerAll(aviator);
        
        // Verify function is available in AviatorScript
        assertNotNull(aviator.getFunction("BUCKET"));
    }

    @Test
    void testClearRegistry() {
        registry.register(new BucketFunction());
        
        assertEquals(1, registry.size());
        
        registry.clear();
        
        assertEquals(0, registry.size());
        assertFalse(registry.hasFunction("BUCKET"));
    }

    @Test
    void testGetFunctionNames() {
        registry.register(new BucketFunction());
        
        var names = registry.getFunctionNames();
        assertTrue(names.contains("BUCKET"));
        assertEquals(1, names.size());
    }

    @Test
    void testBucketFunctionNameIsUppercase() {
        BucketFunction bucket = new BucketFunction();
        assertEquals("BUCKET", bucket.getName());
        assertEquals(bucket.getName(), bucket.getName().toUpperCase());
    }
}

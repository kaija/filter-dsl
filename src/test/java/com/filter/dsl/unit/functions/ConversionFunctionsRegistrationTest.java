package com.filter.dsl.unit.functions;

import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionRegistry;
import com.filter.dsl.functions.conversion.*;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for registering conversion functions with the FunctionRegistry.
 * 
 * Requirements: 16.2, 16.4
 */
class ConversionFunctionsRegistrationTest {

    private FunctionRegistry registry;
    private AviatorEvaluatorInstance aviator;

    @BeforeEach
    void setUp() {
        registry = new FunctionRegistry();
        aviator = AviatorEvaluator.newInstance();
    }

    @Test
    void testRegisterToNumberFunction() {
        ToNumberFunction toNumber = new ToNumberFunction();
        
        registry.register(toNumber);
        
        assertTrue(registry.hasFunction("TO_NUMBER"));
        assertEquals(toNumber, registry.getFunction("TO_NUMBER"));
    }

    @Test
    void testRegisterToStringFunction() {
        ToStringFunction toString = new ToStringFunction();
        
        registry.register(toString);
        
        assertTrue(registry.hasFunction("TO_STRING"));
        assertEquals(toString, registry.getFunction("TO_STRING"));
    }

    @Test
    void testRegisterToBooleanFunction() {
        ToBooleanFunction toBoolean = new ToBooleanFunction();
        
        registry.register(toBoolean);
        
        assertTrue(registry.hasFunction("TO_BOOLEAN"));
        assertEquals(toBoolean, registry.getFunction("TO_BOOLEAN"));
    }

    @Test
    void testRegisterConvertUnitFunction() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        
        registry.register(convertUnit);
        
        assertTrue(registry.hasFunction("CONVERT_UNIT"));
        assertEquals(convertUnit, registry.getFunction("CONVERT_UNIT"));
    }

    @Test
    void testRegisterAllConversionFunctions() {
        registry.register(new ToNumberFunction());
        registry.register(new ToStringFunction());
        registry.register(new ToBooleanFunction());
        registry.register(new ConvertUnitFunction());
        
        assertEquals(4, registry.size());
        assertTrue(registry.hasFunction("TO_NUMBER"));
        assertTrue(registry.hasFunction("TO_STRING"));
        assertTrue(registry.hasFunction("TO_BOOLEAN"));
        assertTrue(registry.hasFunction("CONVERT_UNIT"));
    }

    @Test
    void testToNumberFunctionMetadata() {
        ToNumberFunction toNumber = new ToNumberFunction();
        
        registry.register(toNumber);
        
        FunctionMetadata metadata = registry.getMetadata("TO_NUMBER");
        assertNotNull(metadata);
        assertEquals("TO_NUMBER", metadata.getName());
        assertEquals(1, metadata.getMinArgs());
        assertEquals(1, metadata.getMaxArgs());
        assertEquals(FunctionMetadata.ReturnType.NUMBER, metadata.getReturnType());
    }

    @Test
    void testToStringFunctionMetadata() {
        ToStringFunction toString = new ToStringFunction();
        
        registry.register(toString);
        
        FunctionMetadata metadata = registry.getMetadata("TO_STRING");
        assertNotNull(metadata);
        assertEquals("TO_STRING", metadata.getName());
        assertEquals(1, metadata.getMinArgs());
        assertEquals(1, metadata.getMaxArgs());
        assertEquals(FunctionMetadata.ReturnType.STRING, metadata.getReturnType());
    }

    @Test
    void testToBooleanFunctionMetadata() {
        ToBooleanFunction toBoolean = new ToBooleanFunction();
        
        registry.register(toBoolean);
        
        FunctionMetadata metadata = registry.getMetadata("TO_BOOLEAN");
        assertNotNull(metadata);
        assertEquals("TO_BOOLEAN", metadata.getName());
        assertEquals(1, metadata.getMinArgs());
        assertEquals(1, metadata.getMaxArgs());
        assertEquals(FunctionMetadata.ReturnType.BOOLEAN, metadata.getReturnType());
    }

    @Test
    void testConvertUnitFunctionMetadata() {
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        
        registry.register(convertUnit);
        
        FunctionMetadata metadata = registry.getMetadata("CONVERT_UNIT");
        assertNotNull(metadata);
        assertEquals("CONVERT_UNIT", metadata.getName());
        assertEquals(3, metadata.getMinArgs());
        assertEquals(3, metadata.getMaxArgs());
        assertEquals(FunctionMetadata.ReturnType.NUMBER, metadata.getReturnType());
    }

    @Test
    void testDuplicateRegistrationThrowsException() {
        ToNumberFunction toNumber = new ToNumberFunction();
        
        registry.register(toNumber);
        
        assertThrows(IllegalArgumentException.class, () -> {
            registry.register(new ToNumberFunction());
        });
    }

    @Test
    void testConversionFunctionsWithAviatorScript() {
        // Register functions
        registry.register(new ToNumberFunction());
        registry.register(new ToStringFunction());
        registry.register(new ToBooleanFunction());
        registry.register(new ConvertUnitFunction());
        
        registry.registerAll(aviator);
        
        // Verify functions are available in AviatorScript
        assertNotNull(aviator.getFunction("TO_NUMBER"));
        assertNotNull(aviator.getFunction("TO_STRING"));
        assertNotNull(aviator.getFunction("TO_BOOLEAN"));
        assertNotNull(aviator.getFunction("CONVERT_UNIT"));
    }

    @Test
    void testClearRegistry() {
        registry.register(new ToNumberFunction());
        registry.register(new ToStringFunction());
        registry.register(new ToBooleanFunction());
        registry.register(new ConvertUnitFunction());
        
        assertEquals(4, registry.size());
        
        registry.clear();
        
        assertEquals(0, registry.size());
        assertFalse(registry.hasFunction("TO_NUMBER"));
        assertFalse(registry.hasFunction("TO_STRING"));
        assertFalse(registry.hasFunction("TO_BOOLEAN"));
        assertFalse(registry.hasFunction("CONVERT_UNIT"));
    }

    @Test
    void testGetFunctionNames() {
        registry.register(new ToNumberFunction());
        registry.register(new ToStringFunction());
        registry.register(new ToBooleanFunction());
        registry.register(new ConvertUnitFunction());
        
        var names = registry.getFunctionNames();
        assertEquals(4, names.size());
        assertTrue(names.contains("TO_NUMBER"));
        assertTrue(names.contains("TO_STRING"));
        assertTrue(names.contains("TO_BOOLEAN"));
        assertTrue(names.contains("CONVERT_UNIT"));
    }

    @Test
    void testFunctionNamesAreUppercase() {
        ToNumberFunction toNumber = new ToNumberFunction();
        ToStringFunction toString = new ToStringFunction();
        ToBooleanFunction toBoolean = new ToBooleanFunction();
        ConvertUnitFunction convertUnit = new ConvertUnitFunction();
        
        assertEquals("TO_NUMBER", toNumber.getName());
        assertEquals(toNumber.getName(), toNumber.getName().toUpperCase());
        
        assertEquals("TO_STRING", toString.getName());
        assertEquals(toString.getName(), toString.getName().toUpperCase());
        
        assertEquals("TO_BOOLEAN", toBoolean.getName());
        assertEquals(toBoolean.getName(), toBoolean.getName().toUpperCase());
        
        assertEquals("CONVERT_UNIT", convertUnit.getName());
        assertEquals(convertUnit.getName(), convertUnit.getName().toUpperCase());
    }
}

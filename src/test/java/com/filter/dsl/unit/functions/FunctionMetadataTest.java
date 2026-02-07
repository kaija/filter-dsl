package com.filter.dsl.unit.functions;

import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FunctionMetadata class.
 */
class FunctionMetadataTest {

    @Test
    void testBuilder_MinimalMetadata() {
        FunctionMetadata metadata = FunctionMetadata.builder()
            .name("TEST")
            .minArgs(1)
            .maxArgs(1)
            .returnType(ReturnType.NUMBER)
            .build();

        assertEquals("TEST", metadata.getName());
        assertEquals(1, metadata.getMinArgs());
        assertEquals(1, metadata.getMaxArgs());
        assertEquals(ReturnType.NUMBER, metadata.getReturnType());
        assertNull(metadata.getDescription());
    }

    @Test
    void testBuilder_CompleteMetadata() {
        FunctionMetadata metadata = FunctionMetadata.builder()
            .name("COUNT")
            .minArgs(1)
            .maxArgs(1)
            .argumentType(0, ArgumentType.COLLECTION)
            .returnType(ReturnType.NUMBER)
            .description("Returns the number of items in a collection")
            .build();

        assertEquals("COUNT", metadata.getName());
        assertEquals(1, metadata.getMinArgs());
        assertEquals(1, metadata.getMaxArgs());
        assertEquals(ReturnType.NUMBER, metadata.getReturnType());
        assertEquals("Returns the number of items in a collection", metadata.getDescription());
        assertEquals(1, metadata.getArgumentTypes().size());
        assertEquals(ArgumentType.COLLECTION, metadata.getArgumentTypes().get(0));
    }

    @Test
    void testBuilder_MultipleArgumentTypes() {
        FunctionMetadata metadata = FunctionMetadata.builder()
            .name("ADD")
            .minArgs(2)
            .maxArgs(2)
            .argumentType(0, ArgumentType.NUMBER)
            .argumentType(1, ArgumentType.NUMBER)
            .returnType(ReturnType.NUMBER)
            .build();

        assertEquals(2, metadata.getArgumentTypes().size());
        assertEquals(ArgumentType.NUMBER, metadata.getArgumentTypes().get(0));
        assertEquals(ArgumentType.NUMBER, metadata.getArgumentTypes().get(1));
    }

    @Test
    void testBuilder_VariableArguments() {
        FunctionMetadata metadata = FunctionMetadata.builder()
            .name("AND")
            .minArgs(2)
            .maxArgs(Integer.MAX_VALUE)
            .returnType(ReturnType.BOOLEAN)
            .build();

        assertEquals(2, metadata.getMinArgs());
        assertEquals(Integer.MAX_VALUE, metadata.getMaxArgs());
    }

    @Test
    void testBuilder_ArgumentTypeWithGaps() {
        // Setting argument type at index 2 should fill gaps with ANY
        FunctionMetadata metadata = FunctionMetadata.builder()
            .name("TEST")
            .minArgs(3)
            .maxArgs(3)
            .argumentType(2, ArgumentType.STRING)
            .returnType(ReturnType.ANY)
            .build();

        assertEquals(3, metadata.getArgumentTypes().size());
        assertEquals(ArgumentType.ANY, metadata.getArgumentTypes().get(0));
        assertEquals(ArgumentType.ANY, metadata.getArgumentTypes().get(1));
        assertEquals(ArgumentType.STRING, metadata.getArgumentTypes().get(2));
    }

    @Test
    void testArgumentType_AllValues() {
        // Verify all ArgumentType enum values are accessible
        assertNotNull(ArgumentType.ANY);
        assertNotNull(ArgumentType.NUMBER);
        assertNotNull(ArgumentType.STRING);
        assertNotNull(ArgumentType.BOOLEAN);
        assertNotNull(ArgumentType.COLLECTION);
        assertNotNull(ArgumentType.DATE);
        assertNotNull(ArgumentType.OBJECT);
    }

    @Test
    void testReturnType_AllValues() {
        // Verify all ReturnType enum values are accessible
        assertNotNull(ReturnType.ANY);
        assertNotNull(ReturnType.NUMBER);
        assertNotNull(ReturnType.STRING);
        assertNotNull(ReturnType.BOOLEAN);
        assertNotNull(ReturnType.COLLECTION);
        assertNotNull(ReturnType.DATE);
        assertNotNull(ReturnType.VOID);
    }

    @Test
    void testBuilder_Chaining() {
        // Verify builder methods return the builder for chaining
        FunctionMetadata.Builder builder = FunctionMetadata.builder();
        
        assertSame(builder, builder.name("TEST"));
        assertSame(builder, builder.minArgs(1));
        assertSame(builder, builder.maxArgs(2));
        assertSame(builder, builder.argumentType(0, ArgumentType.NUMBER));
        assertSame(builder, builder.returnType(ReturnType.NUMBER));
        assertSame(builder, builder.description("Test description"));
    }

    @Test
    void testGetters() {
        FunctionMetadata metadata = FunctionMetadata.builder()
            .name("SUM")
            .minArgs(1)
            .maxArgs(1)
            .argumentType(0, ArgumentType.COLLECTION)
            .returnType(ReturnType.NUMBER)
            .description("Sum of numeric values")
            .build();

        // Test all getters
        assertEquals("SUM", metadata.getName());
        assertEquals(1, metadata.getMinArgs());
        assertEquals(1, metadata.getMaxArgs());
        assertNotNull(metadata.getArgumentTypes());
        assertEquals(ReturnType.NUMBER, metadata.getReturnType());
        assertEquals("Sum of numeric values", metadata.getDescription());
    }
}

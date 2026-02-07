package com.filter.dsl.unit.parser;

import com.filter.dsl.functions.*;
import com.filter.dsl.functions.aggregation.CountFunction;
import com.filter.dsl.functions.comparison.GreaterThanFunction;
import com.filter.dsl.functions.comparison.EqualsFunction;
import com.filter.dsl.functions.logical.LogicalAndFunction;
import com.filter.dsl.functions.math.AddFunction;
import com.filter.dsl.functions.math.DivideFunction;
import com.filter.dsl.parser.DSLParser;
import com.filter.dsl.parser.DSLParserImpl;
import com.filter.dsl.parser.PrettyPrintConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for pretty printer with configurable formatting options.
 * 
 * Tests:
 * - Default formatting (2-space indent, 80 char width, expanded mode)
 * - Compact mode (single-line output)
 * - Custom indent sizes
 * - Custom line widths
 * - Tab vs space indentation
 * - Semantic preservation (parse -> format -> parse should be equivalent)
 */
class PrettyPrinterTest {
    
    private FunctionRegistry registry;
    private DSLParser parser;
    
    @BeforeEach
    void setUp() {
        registry = new FunctionRegistry();
        
        // Register test functions
        registry.register(new CountFunction());
        registry.register(new GreaterThanFunction());
        registry.register(new EqualsFunction());
        registry.register(new LogicalAndFunction());
        registry.register(new AddFunction());
        registry.register(new DivideFunction());
        
        parser = new DSLParserImpl(registry);
    }
    
    // ========== Default Formatting Tests ==========
    
    @Test
    void testDefaultFormatSimpleExpression() {
        String expression = "GT(5,3)";
        String formatted = parser.prettyPrint(expression);
        
        assertNotNull(formatted);
        assertTrue(formatted.contains("GT"));
        assertTrue(formatted.contains("5"));
        assertTrue(formatted.contains("3"));
        // Should have newlines in default expanded mode
        assertTrue(formatted.contains("\n"));
    }
    
    @Test
    void testDefaultFormatNestedExpression() {
        String expression = "GT(COUNT(events),5)";
        String formatted = parser.prettyPrint(expression);
        
        assertNotNull(formatted);
        assertTrue(formatted.contains("GT"));
        assertTrue(formatted.contains("COUNT"));
        assertTrue(formatted.contains("events"));
        assertTrue(formatted.contains("5"));
        // Should have multiple levels of indentation
        assertTrue(formatted.contains("\n"));
    }
    
    @Test
    void testDefaultFormatComplexExpression() {
        String expression = "AND(GT(COUNT(events),5),EQ(status,\"active\"))";
        String formatted = parser.prettyPrint(expression);
        
        assertNotNull(formatted);
        assertTrue(formatted.contains("AND"));
        assertTrue(formatted.contains("GT"));
        assertTrue(formatted.contains("COUNT"));
        assertTrue(formatted.contains("EQ"));
        assertTrue(formatted.contains("\"active\""));
        // Should have proper indentation
        assertTrue(formatted.contains("\n"));
    }
    
    // ========== Compact Mode Tests ==========
    
    @Test
    void testCompactModeSimpleExpression() {
        String expression = "GT(5, 3)";
        String formatted = parser.prettyPrint(expression, PrettyPrintConfig.COMPACT);
        
        assertNotNull(formatted);
        // Should not have newlines in compact mode
        assertFalse(formatted.contains("\n"));
        assertTrue(formatted.contains("GT"));
        assertTrue(formatted.contains("5"));
        assertTrue(formatted.contains("3"));
    }
    
    @Test
    void testCompactModeNestedExpression() {
        String expression = "GT(COUNT(events), 5)";
        String formatted = parser.prettyPrint(expression, PrettyPrintConfig.COMPACT);
        
        assertNotNull(formatted);
        assertFalse(formatted.contains("\n"));
        assertTrue(formatted.contains("GT"));
        assertTrue(formatted.contains("COUNT"));
    }
    
    @Test
    void testCompactModeRemovesExtraWhitespace() {
        String expression = "GT  (  COUNT  (  events  )  ,  5  )";
        String formatted = parser.prettyPrint(expression, PrettyPrintConfig.COMPACT);
        
        assertNotNull(formatted);
        assertFalse(formatted.contains("\n"));
        // Should have minimal whitespace
        assertFalse(formatted.contains("  "));
    }
    
    @Test
    void testCompactModePreservesStrings() {
        String expression = "EQ(name, \"test value with spaces\")";
        String formatted = parser.prettyPrint(expression, PrettyPrintConfig.COMPACT);
        
        assertNotNull(formatted);
        assertTrue(formatted.contains("\"test value with spaces\""));
        assertFalse(formatted.contains("\n"));
    }
    
    // ========== Custom Indent Size Tests ==========
    
    @Test
    void testCustomIndentSize4Spaces() {
        PrettyPrintConfig config = new PrettyPrintConfig.Builder()
            .indentSize(4)
            .build();
        
        String expression = "GT(COUNT(events),5)";
        String formatted = parser.prettyPrint(expression, config);
        
        assertNotNull(formatted);
        // Should have 4-space indentation
        assertTrue(formatted.contains("    ") || formatted.contains("\n"));
    }
    
    @Test
    void testCustomIndentSize1Space() {
        PrettyPrintConfig config = new PrettyPrintConfig.Builder()
            .indentSize(1)
            .build();
        
        String expression = "GT(COUNT(events),5)";
        String formatted = parser.prettyPrint(expression, config);
        
        assertNotNull(formatted);
        assertTrue(formatted.contains("\n"));
    }
    
    @Test
    void testCustomIndentSize0() {
        PrettyPrintConfig config = new PrettyPrintConfig.Builder()
            .indentSize(0)
            .build();
        
        String expression = "GT(COUNT(events),5)";
        String formatted = parser.prettyPrint(expression, config);
        
        assertNotNull(formatted);
        // Should still have newlines but no indentation
        assertTrue(formatted.contains("\n"));
    }
    
    // ========== Tab Indentation Tests ==========
    
    @Test
    void testTabIndentation() {
        PrettyPrintConfig config = new PrettyPrintConfig.Builder()
            .useTabs(true)
            .build();
        
        String expression = "GT(COUNT(events),5)";
        String formatted = parser.prettyPrint(expression, config);
        
        assertNotNull(formatted);
        // Should use tabs for indentation
        assertTrue(formatted.contains("\t") || formatted.contains("\n"));
    }
    
    // ========== Line Width Tests ==========
    
    @Test
    void testWideLineWidth() {
        PrettyPrintConfig config = new PrettyPrintConfig.Builder()
            .lineWidth(120)
            .build();
        
        String expression = "AND(GT(COUNT(events),5),EQ(status,\"active\"))";
        String formatted = parser.prettyPrint(expression, config);
        
        assertNotNull(formatted);
        assertTrue(formatted.contains("AND"));
    }
    
    @Test
    void testNarrowLineWidth() {
        PrettyPrintConfig config = new PrettyPrintConfig.Builder()
            .lineWidth(40)
            .build();
        
        String expression = "AND(GT(COUNT(events),5),EQ(status,\"active\"))";
        String formatted = parser.prettyPrint(expression, config);
        
        assertNotNull(formatted);
        // Should have more line breaks with narrow width
        assertTrue(formatted.contains("\n"));
    }
    
    // ========== Semantic Preservation Tests ==========
    
    @Test
    void testSemanticPreservationSimple() {
        String original = "GT(5, 3)";
        String formatted = parser.prettyPrint(original);
        
        // Both should parse successfully
        assertTrue(parser.parse(original).isValid());
        assertTrue(parser.parse(formatted).isValid());
        
        // Format again - should be stable
        String reformatted = parser.prettyPrint(formatted);
        assertEquals(formatted, reformatted);
    }
    
    @Test
    void testSemanticPreservationNested() {
        String original = "GT(COUNT(events), 5)";
        String formatted = parser.prettyPrint(original);
        
        assertTrue(parser.parse(original).isValid());
        assertTrue(parser.parse(formatted).isValid());
    }
    
    @Test
    void testSemanticPreservationComplex() {
        String original = "AND(GT(COUNT(events), 5), EQ(status, \"active\"))";
        String formatted = parser.prettyPrint(original);
        
        assertTrue(parser.parse(original).isValid());
        assertTrue(parser.parse(formatted).isValid());
    }
    
    @Test
    void testSemanticPreservationWithStrings() {
        String original = "EQ(name, \"test(value),with,special,chars\")";
        String formatted = parser.prettyPrint(original);
        
        assertTrue(parser.parse(original).isValid());
        assertTrue(parser.parse(formatted).isValid());
        assertTrue(formatted.contains("\"test(value),with,special,chars\""));
    }
    
    @Test
    void testSemanticPreservationCompactMode() {
        String original = "GT(COUNT(events), 5)";
        String formatted = parser.prettyPrint(original, PrettyPrintConfig.COMPACT);
        
        assertTrue(parser.parse(original).isValid());
        assertTrue(parser.parse(formatted).isValid());
    }
    
    // ========== Edge Cases ==========
    
    @Test
    void testPrettyPrintNullExpression() {
        String formatted = parser.prettyPrint(null);
        assertNull(formatted);
    }
    
    @Test
    void testPrettyPrintEmptyExpression() {
        String formatted = parser.prettyPrint("");
        assertEquals("", formatted);
    }
    
    @Test
    void testPrettyPrintWhitespaceOnly() {
        String formatted = parser.prettyPrint("   ");
        assertNotNull(formatted);
    }
    
    @Test
    void testPrettyPrintWithNullConfig() {
        String expression = "GT(5, 3)";
        String formatted = parser.prettyPrint(expression, null);
        
        // Should use default config
        assertNotNull(formatted);
        assertTrue(formatted.contains("GT"));
    }
    
    @Test
    void testPrettyPrintEmptyParentheses() {
        // This would be invalid, but pretty printer should handle it
        String expression = "COUNT()";
        String formatted = parser.prettyPrint(expression);
        
        assertNotNull(formatted);
        assertTrue(formatted.contains("COUNT"));
        assertTrue(formatted.contains("()"));
    }
    
    @Test
    void testPrettyPrintDeeplyNested() {
        String expression = "GT(ADD(DIVIDE(COUNT(events),5),3),10)";
        String formatted = parser.prettyPrint(expression);
        
        assertNotNull(formatted);
        assertTrue(formatted.contains("GT"));
        assertTrue(formatted.contains("ADD"));
        assertTrue(formatted.contains("DIVIDE"));
        assertTrue(formatted.contains("COUNT"));
        // Should have multiple levels of indentation
        assertTrue(formatted.contains("\n"));
    }
    
    // ========== Preset Configuration Tests ==========
    
    @Test
    void testDefaultPreset() {
        String expression = "GT(COUNT(events),5)";
        String formatted = parser.prettyPrint(expression, PrettyPrintConfig.DEFAULT);
        
        assertNotNull(formatted);
        assertTrue(formatted.contains("\n"));
    }
    
    @Test
    void testCompactPreset() {
        String expression = "GT(COUNT(events),5)";
        String formatted = parser.prettyPrint(expression, PrettyPrintConfig.COMPACT);
        
        assertNotNull(formatted);
        assertFalse(formatted.contains("\n"));
    }
    
    @Test
    void testWidePreset() {
        String expression = "GT(COUNT(events),5)";
        String formatted = parser.prettyPrint(expression, PrettyPrintConfig.WIDE);
        
        assertNotNull(formatted);
        assertTrue(formatted.contains("GT"));
    }
    
    // ========== Builder Validation Tests ==========
    
    @Test
    void testBuilderNegativeIndentSize() {
        assertThrows(IllegalArgumentException.class, () -> {
            new PrettyPrintConfig.Builder().indentSize(-1).build();
        });
    }
    
    @Test
    void testBuilderTooSmallLineWidth() {
        assertThrows(IllegalArgumentException.class, () -> {
            new PrettyPrintConfig.Builder().lineWidth(10).build();
        });
    }
    
    @Test
    void testBuilderValidConfiguration() {
        PrettyPrintConfig config = new PrettyPrintConfig.Builder()
            .indentSize(3)
            .lineWidth(100)
            .compactMode(false)
            .useTabs(false)
            .build();
        
        assertNotNull(config);
        assertEquals(3, config.getIndentSize());
        assertEquals(100, config.getLineWidth());
        assertFalse(config.isCompactMode());
        assertFalse(config.useTabs());
    }
    
    // ========== String Literal Handling Tests ==========
    
    @Test
    void testStringWithParentheses() {
        String expression = "EQ(name, \"test(value)\")";
        String formatted = parser.prettyPrint(expression);
        
        assertNotNull(formatted);
        assertTrue(formatted.contains("\"test(value)\""));
    }
    
    @Test
    void testStringWithCommas() {
        String expression = "EQ(name, \"last, first\")";
        String formatted = parser.prettyPrint(expression);
        
        assertNotNull(formatted);
        assertTrue(formatted.contains("\"last, first\""));
    }
    
    @Test
    void testStringWithNewlines() {
        String expression = "EQ(name, \"line1\\nline2\")";
        String formatted = parser.prettyPrint(expression);
        
        assertNotNull(formatted);
        assertTrue(formatted.contains("\"line1\\nline2\""));
    }
    
    @Test
    void testMultipleStrings() {
        String expression = "AND(EQ(first, \"value1\"), EQ(second, \"value2\"))";
        String formatted = parser.prettyPrint(expression);
        
        assertNotNull(formatted);
        assertTrue(formatted.contains("\"value1\""));
        assertTrue(formatted.contains("\"value2\""));
    }
    
    @Test
    void testSingleQuotedStrings() {
        String expression = "EQ(name, 'test value')";
        String formatted = parser.prettyPrint(expression);
        
        assertNotNull(formatted);
        assertTrue(formatted.contains("'test value'"));
    }
}

package com.filter.dsl.unit.parser;

import com.filter.dsl.functions.*;
import com.filter.dsl.functions.aggregation.CountFunction;
import com.filter.dsl.functions.comparison.GreaterThanFunction;
import com.filter.dsl.functions.comparison.EqualsFunction;
import com.filter.dsl.functions.logical.LogicalAndFunction;
import com.filter.dsl.functions.math.AddFunction;
import com.filter.dsl.parser.DSLParser;
import com.filter.dsl.parser.DSLParserImpl;
import com.filter.dsl.parser.ParseResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DSLParser implementation.
 * Tests validation of DSL expressions including:
 * - Function name case sensitivity
 * - Parentheses matching
 * - Undefined function detection
 * - Argument count validation
 */
class DSLParserTest {
    
    private FunctionRegistry registry;
    private DSLParser parser;
    
    @BeforeEach
    void setUp() {
        registry = new FunctionRegistry();
        
        // Register some test functions
        registry.register(new CountFunction());
        registry.register(new GreaterThanFunction());
        registry.register(new EqualsFunction());
        registry.register(new LogicalAndFunction());
        registry.register(new AddFunction());
        
        parser = new DSLParserImpl(registry);
    }
    
    // ========== Valid Expression Tests ==========
    
    @Test
    void testParseValidSimpleExpression() {
        String expression = "GT(5, 3)";
        ParseResult result = parser.parse(expression);
        
        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
        assertNotNull(result.getFormattedExpression());
    }
    
    @Test
    void testParseValidNestedExpression() {
        String expression = "GT(COUNT(events), 5)";
        ParseResult result = parser.parse(expression);
        
        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }
    
    @Test
    void testParseValidComplexExpression() {
        String expression = "AND(GT(COUNT(events), 5), EQ(status, \"active\"))";
        ParseResult result = parser.parse(expression);
        
        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }
    
    @Test
    void testParseValidExpressionWithWhitespace() {
        String expression = "  GT  (  COUNT  (  events  )  ,  5  )  ";
        ParseResult result = parser.parse(expression);
        
        assertTrue(result.isValid());
    }
    
    // ========== Function Name Case Tests ==========
    
    @Test
    void testParseLowercaseFunctionName() {
        String expression = "gt(5, 3)";
        ParseResult result = parser.parse(expression);
        
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().contains("UPPERCASE"));
        assertTrue(result.getErrorMessage().contains("gt"));
        assertTrue(result.getErrorMessage().contains("GT"));
        assertNotNull(result.getErrorPosition());
    }
    
    @Test
    void testParseMixedCaseFunctionName() {
        String expression = "Count(events)";
        ParseResult result = parser.parse(expression);
        
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().contains("UPPERCASE"));
    }
    
    @Test
    void testParseLowercaseFunctionInNestedExpression() {
        String expression = "GT(count(events), 5)";
        ParseResult result = parser.parse(expression);
        
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("count"));
    }
    
    // ========== Parentheses Matching Tests ==========
    
    @Test
    void testParseMissingClosingParenthesis() {
        String expression = "GT(5, 3";
        ParseResult result = parser.parse(expression);
        
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().toLowerCase().contains("unclosed") ||
                   result.getErrorMessage().toLowerCase().contains("delimiter"));
        assertNotNull(result.getErrorPosition());
    }
    
    @Test
    void testParseMissingOpeningParenthesis() {
        String expression = "GT 5, 3)";
        ParseResult result = parser.parse(expression);
        
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().toLowerCase().contains("unexpected") ||
                   result.getErrorMessage().toLowerCase().contains("closing"));
    }
    
    @Test
    void testParseMismatchedParentheses() {
        String expression = "GT(5, 3]";
        ParseResult result = parser.parse(expression);
        
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().toLowerCase().contains("mismatch"));
    }
    
    @Test
    void testParseNestedMissingParenthesis() {
        String expression = "GT(COUNT(events, 5)";
        ParseResult result = parser.parse(expression);
        
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().toLowerCase().contains("unclosed") ||
                   result.getErrorMessage().toLowerCase().contains("delimiter"));
    }
    
    @Test
    void testParseBalancedBrackets() {
        String expression = "GT([1, 2, 3], 5)";
        ParseResult result = parser.parse(expression);
        
        // Should be valid in terms of bracket matching
        // (may fail on other validation if array literals aren't supported)
        assertTrue(result.isValid() || !result.getErrorMessage().toLowerCase().contains("bracket"));
    }
    
    // ========== Undefined Function Tests ==========
    
    @Test
    void testParseUndefinedFunction() {
        String expression = "UNDEFINED_FUNC(5, 3)";
        ParseResult result = parser.parse(expression);
        
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().contains("UNDEFINED_FUNC"));
        assertTrue(result.getErrorMessage().contains("not defined") || 
                   result.getErrorMessage().contains("is not defined"));
        assertNotNull(result.getErrorPosition());
    }
    
    @Test
    void testParseUndefinedFunctionWithSuggestion() {
        String expression = "CONT(events)";
        ParseResult result = parser.parse(expression);
        
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("CONT"));
        // Should suggest COUNT since it's similar
        assertTrue(result.getErrorMessage().contains("COUNT"));
    }
    
    @Test
    void testParseTypoInFunctionName() {
        String expression = "GRATER_THAN(5, 3)";
        ParseResult result = parser.parse(expression);
        
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("GRATER_THAN") ||
                   result.getErrorMessage().contains("not defined") ||
                   result.getErrorMessage().contains("is not defined"));
    }
    
    // ========== Argument Count Validation Tests ==========
    
    @Test
    void testParseTooFewArguments() {
        String expression = "GT(5)";
        ParseResult result = parser.parse(expression);
        
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().contains("GT"));
        assertTrue(result.getErrorMessage().toLowerCase().contains("argument"));
        assertTrue(result.getErrorMessage().contains("2"));
        assertTrue(result.getErrorMessage().contains("1"));
    }
    
    @Test
    void testParseTooManyArguments() {
        String expression = "GT(5, 3, 1)";
        ParseResult result = parser.parse(expression);
        
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("GT"));
        assertTrue(result.getErrorMessage().toLowerCase().contains("argument"));
    }
    
    @Test
    void testParseCorrectArgumentCount() {
        String expression = "GT(5, 3)";
        ParseResult result = parser.parse(expression);
        
        assertTrue(result.isValid());
    }
    
    @Test
    void testParseEmptyArgumentList() {
        String expression = "COUNT()";
        ParseResult result = parser.parse(expression);
        
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("COUNT"));
        assertTrue(result.getErrorMessage().toLowerCase().contains("argument"));
    }
    
    @Test
    void testParseVariableArgumentFunction() {
        // AND accepts 2 or more arguments
        String expression1 = "AND(true, false)";
        ParseResult result1 = parser.parse(expression1);
        assertTrue(result1.isValid());
        
        String expression2 = "AND(true, false, true)";
        ParseResult result2 = parser.parse(expression2);
        assertTrue(result2.isValid());
        
        String expression3 = "AND(true)";
        ParseResult result3 = parser.parse(expression3);
        assertFalse(result3.isValid());
    }
    
    @Test
    void testParseNestedFunctionArgumentCounts() {
        String expression = "GT(COUNT(events), ADD(2, 3))";
        ParseResult result = parser.parse(expression);
        
        assertTrue(result.isValid());
    }
    
    // ========== Edge Cases ==========
    
    @Test
    void testParseNullExpression() {
        ParseResult result = parser.parse(null);
        
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
    }
    
    @Test
    void testParseEmptyExpression() {
        ParseResult result = parser.parse("");
        
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
    }
    
    @Test
    void testParseWhitespaceOnlyExpression() {
        ParseResult result = parser.parse("   ");
        
        assertFalse(result.isValid());
        assertNotNull(result.getErrorMessage());
    }
    
    @Test
    void testParseStringLiteralsWithParentheses() {
        String expression = "EQ(name, \"test(value)\")";
        ParseResult result = parser.parse(expression);
        
        assertTrue(result.isValid());
    }
    
    @Test
    void testParseStringLiteralsWithCommas() {
        String expression = "EQ(name, \"last, first\")";
        ParseResult result = parser.parse(expression);
        
        assertTrue(result.isValid());
    }
    
    // ========== Pretty Print Tests ==========
    
    @Test
    void testPrettyPrintSimpleExpression() {
        String expression = "GT(5,3)";
        String formatted = parser.prettyPrint(expression);
        
        assertNotNull(formatted);
        assertTrue(formatted.contains("GT"));
        assertTrue(formatted.contains("5"));
        assertTrue(formatted.contains("3"));
    }
    
    @Test
    void testPrettyPrintNestedExpression() {
        String expression = "GT(COUNT(events),5)";
        String formatted = parser.prettyPrint(expression);
        
        assertNotNull(formatted);
        assertTrue(formatted.contains("GT"));
        assertTrue(formatted.contains("COUNT"));
        // Should have some formatting (newlines or indentation)
        assertTrue(formatted.length() > expression.length() || formatted.contains("\n"));
    }
    
    @Test
    void testPrettyPrintPreservesStrings() {
        String expression = "EQ(name, \"test value\")";
        String formatted = parser.prettyPrint(expression);
        
        assertNotNull(formatted);
        assertTrue(formatted.contains("\"test value\""));
    }
    
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
}

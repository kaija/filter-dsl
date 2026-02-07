package com.filter.dsl.parser;

/**
 * Parser for DSL expressions.
 * 
 * The DSLParser validates DSL expression syntax before compilation by AviatorScript.
 * It performs the following checks:
 * - Function names are UPPERCASE
 * - Parentheses and brackets are properly matched
 * - Function argument counts match metadata specifications
 * - Function names are defined in the registry
 * - Basic syntax structure is valid
 * 
 * Usage:
 * <pre>
 * DSLParser parser = new DSLParserImpl(functionRegistry);
 * ParseResult result = parser.parse("GT(COUNT(events), 5)");
 * 
 * if (result.isValid()) {
 *     // Expression is valid, proceed with compilation
 *     String formatted = result.getFormattedExpression();
 * } else {
 *     // Handle error
 *     System.err.println(result.getErrorMessage());
 * }
 * </pre>
 */
public interface DSLParser {
    
    /**
     * Parse and validate a DSL expression.
     * 
     * This method performs comprehensive validation including:
     * - Syntax validation (parentheses matching, valid structure)
     * - Function name validation (UPPERCASE, defined in registry)
     * - Argument count validation (matches function metadata)
     * 
     * @param expression The DSL expression string to parse
     * @return ParseResult containing validation status and errors
     */
    ParseResult parse(String expression);
    
    /**
     * Pretty-print a DSL expression with default formatting.
     * 
     * This method formats the expression with proper indentation and spacing
     * while preserving its semantic meaning. If the expression is invalid,
     * returns the original expression.
     * 
     * @param expression The DSL expression string to format
     * @return Formatted expression string
     */
    String prettyPrint(String expression);
    
    /**
     * Pretty-print a DSL expression with configurable formatting options.
     * 
     * This method formats the expression according to the provided configuration,
     * supporting options like:
     * - Indent size (spaces per level)
     * - Line width (maximum characters per line)
     * - Compact vs expanded mode
     * - Tabs vs spaces
     * 
     * The formatter preserves semantic meaning while applying consistent style.
     * 
     * @param expression The DSL expression string to format
     * @param config The formatting configuration options
     * @return Formatted expression string
     */
    String prettyPrint(String expression, PrettyPrintConfig config);
}

package com.filter.dsl.parser;

import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of DSLParser that validates DSL expressions.
 * 
 * This parser performs syntax validation without full compilation:
 * - Validates function names are UPPERCASE
 * - Checks parentheses and bracket matching
 * - Validates function argument counts using metadata
 * - Detects undefined function references
 * - Provides detailed error messages with position information
 */
public class DSLParserImpl implements DSLParser {
    
    private final FunctionRegistry functionRegistry;
    
    // Pattern to match function calls: FUNCTION_NAME(
    private static final Pattern FUNCTION_PATTERN = Pattern.compile("([A-Z_][A-Z0-9_]*)\\s*\\(");
    
    // Pattern to match lowercase function names (for error detection)
    private static final Pattern LOWERCASE_FUNCTION_PATTERN = Pattern.compile("([a-z_][a-zA-Z0-9_]*)\\s*\\(");
    
    public DSLParserImpl(FunctionRegistry functionRegistry) {
        if (functionRegistry == null) {
            throw new IllegalArgumentException("FunctionRegistry cannot be null");
        }
        this.functionRegistry = functionRegistry;
    }
    
    @Override
    public ParseResult parse(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            return ParseResult.error(expression, "Expression cannot be null or empty");
        }
        
        String trimmed = expression.trim();
        
        // Check for lowercase function names first
        ParseResult lowercaseCheck = checkLowercaseFunctions(trimmed);
        if (!lowercaseCheck.isValid()) {
            return lowercaseCheck;
        }
        
        // Check parentheses and bracket matching
        ParseResult balanceCheck = checkBalancedDelimiters(trimmed);
        if (!balanceCheck.isValid()) {
            return balanceCheck;
        }
        
        // Check for undefined functions
        ParseResult undefinedCheck = checkUndefinedFunctions(trimmed);
        if (!undefinedCheck.isValid()) {
            return undefinedCheck;
        }
        
        // Validate function argument counts
        ParseResult argCountCheck = validateArgumentCounts(trimmed);
        if (!argCountCheck.isValid()) {
            return argCountCheck;
        }
        
        // If all checks pass, format the expression
        String formatted = prettyPrint(trimmed);
        return ParseResult.success(expression, formatted);
    }
    
    @Override
    public String prettyPrint(String expression) {
        return prettyPrint(expression, PrettyPrintConfig.DEFAULT);
    }
    
    @Override
    public String prettyPrint(String expression, PrettyPrintConfig config) {
        if (expression == null || expression.trim().isEmpty()) {
            return expression;
        }
        
        if (config == null) {
            config = PrettyPrintConfig.DEFAULT;
        }
        
        // Use compact mode for single-line output
        if (config.isCompactMode()) {
            return formatCompact(expression);
        }
        
        // Use expanded mode with configurable indentation
        return formatExpanded(expression, config);
    }
    
    /**
     * Format expression in compact mode (single line, minimal whitespace).
     */
    private String formatCompact(String expression) {
        StringBuilder result = new StringBuilder();
        boolean inString = false;
        char stringDelimiter = 0;
        boolean lastWasSpace = false;
        
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            
            // Handle string literals
            if ((c == '"' || c == '\'') && (i == 0 || expression.charAt(i - 1) != '\\')) {
                if (!inString) {
                    inString = true;
                    stringDelimiter = c;
                } else if (c == stringDelimiter) {
                    inString = false;
                }
                result.append(c);
                lastWasSpace = false;
                continue;
            }
            
            if (inString) {
                result.append(c);
                lastWasSpace = false;
                continue;
            }
            
            // Handle formatting outside strings
            switch (c) {
                case ' ':
                case '\t':
                case '\n':
                case '\r':
                    // Add single space if not already added
                    if (!lastWasSpace && result.length() > 0) {
                        // Only add space if needed (not after '(' or before ')', ',')
                        if (i + 1 < expression.length()) {
                            char next = expression.charAt(i + 1);
                            if (next != ')' && next != ',' && result.charAt(result.length() - 1) != '(') {
                                result.append(' ');
                                lastWasSpace = true;
                            }
                        }
                    }
                    break;
                    
                case '(':
                case ')':
                case ',':
                    result.append(c);
                    lastWasSpace = false;
                    break;
                    
                default:
                    result.append(c);
                    lastWasSpace = false;
                    break;
            }
        }
        
        return result.toString();
    }
    
    /**
     * Format expression in expanded mode with configurable indentation.
     */
    private String formatExpanded(String expression, PrettyPrintConfig config) {
        StringBuilder result = new StringBuilder();
        int indentLevel = 0;
        boolean inString = false;
        char stringDelimiter = 0;
        
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            
            // Handle string literals
            if ((c == '"' || c == '\'') && (i == 0 || expression.charAt(i - 1) != '\\')) {
                if (!inString) {
                    inString = true;
                    stringDelimiter = c;
                } else if (c == stringDelimiter) {
                    inString = false;
                }
                result.append(c);
                continue;
            }
            
            if (inString) {
                result.append(c);
                continue;
            }
            
            // Handle formatting outside strings
            switch (c) {
                case '(':
                    result.append(c);
                    indentLevel++;
                    
                    // Check if we should add newline after opening paren
                    if (i + 1 < expression.length() && !isClosingParen(expression, i + 1)) {
                        String indent = config.getIndent(indentLevel);
                        result.append('\n').append(indent);
                    }
                    break;
                    
                case ')':
                    indentLevel--;
                    
                    // Add newline before closing paren if previous char is not opening paren
                    if (result.length() > 0 && !isAfterOpenParen(result)) {
                        String indent = config.getIndent(indentLevel);
                        result.append('\n').append(indent);
                    }
                    result.append(c);
                    break;
                    
                case ',':
                    result.append(c);
                    
                    // Add newline and indent after comma
                    if (i + 1 < expression.length()) {
                        String indent = config.getIndent(indentLevel);
                        result.append('\n').append(indent);
                    }
                    break;
                    
                case ' ':
                case '\t':
                case '\n':
                case '\r':
                    // Skip whitespace - we'll add our own
                    break;
                    
                default:
                    result.append(c);
                    break;
            }
        }
        
        return result.toString();
    }
    
    /**
     * Check if the next non-whitespace character is a closing parenthesis.
     */
    private boolean isClosingParen(String expression, int startPos) {
        for (int i = startPos; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (!Character.isWhitespace(c)) {
                return c == ')';
            }
        }
        return false;
    }
    
    /**
     * Check if the last character in the result is an opening parenthesis.
     */
    private boolean isAfterOpenParen(StringBuilder result) {
        for (int i = result.length() - 1; i >= 0; i--) {
            char c = result.charAt(i);
            if (!Character.isWhitespace(c) && c != '\n') {
                return c == '(';
            }
        }
        return false;
    }
    
    /**
     * Check for lowercase function names and report them as errors.
     */
    private ParseResult checkLowercaseFunctions(String expression) {
        boolean inString = false;
        char stringDelimiter = 0;
        
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            
            // Handle string literals
            if ((c == '"' || c == '\'') && (i == 0 || expression.charAt(i - 1) != '\\')) {
                if (!inString) {
                    inString = true;
                    stringDelimiter = c;
                } else if (c == stringDelimiter) {
                    inString = false;
                }
                continue;
            }
            
            if (inString) {
                continue;
            }
            
            // Check for function name pattern outside strings
            if (Character.isLetter(c) || c == '_') {
                // Start of potential function name
                int start = i;
                StringBuilder name = new StringBuilder();
                
                // Collect the identifier
                while (i < expression.length() && 
                       (Character.isLetterOrDigit(expression.charAt(i)) || expression.charAt(i) == '_')) {
                    name.append(expression.charAt(i));
                    i++;
                }
                
                // Check if followed by '(' (making it a function call)
                while (i < expression.length() && Character.isWhitespace(expression.charAt(i))) {
                    i++;
                }
                
                if (i < expression.length() && expression.charAt(i) == '(') {
                    // This is a function call
                    String functionName = name.toString();
                    
                    // Check if it has any lowercase letters
                    if (!functionName.equals(functionName.toUpperCase())) {
                        String uppercase = functionName.toUpperCase();
                        
                        // Check if the uppercase version exists in registry
                        if (functionRegistry.hasFunction(uppercase)) {
                            return ParseResult.error(
                                expression,
                                "Function name must be UPPERCASE: '" + functionName + "' should be '" + uppercase + "'",
                                start
                            );
                        } else {
                            return ParseResult.error(
                                expression,
                                "Function name must be UPPERCASE: '" + functionName + "'",
                                start
                            );
                        }
                    }
                }
                
                i--; // Back up one since the outer loop will increment
            }
        }
        
        return ParseResult.success(expression, expression);
    }
    
    /**
     * Check that all parentheses and brackets are properly balanced.
     */
    private ParseResult checkBalancedDelimiters(String expression) {
        Stack<Character> stack = new Stack<>();
        Stack<Integer> positions = new Stack<>();
        boolean inString = false;
        char stringDelimiter = 0;
        
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            
            // Handle string literals
            if ((c == '"' || c == '\'') && (i == 0 || expression.charAt(i - 1) != '\\')) {
                if (!inString) {
                    inString = true;
                    stringDelimiter = c;
                } else if (c == stringDelimiter) {
                    inString = false;
                }
                continue;
            }
            
            if (inString) {
                continue;
            }
            
            // Check delimiters
            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);
                positions.push(i);
            } else if (c == ')' || c == ']' || c == '}') {
                if (stack.isEmpty()) {
                    return ParseResult.error(
                        expression,
                        "Unexpected closing delimiter '" + c + "' with no matching opening delimiter",
                        i
                    );
                }
                
                char opening = stack.pop();
                int openPos = positions.pop();
                
                // Check if delimiters match
                if ((c == ')' && opening != '(') ||
                    (c == ']' && opening != '[') ||
                    (c == '}' && opening != '{')) {
                    return ParseResult.error(
                        expression,
                        "Mismatched delimiters: '" + opening + "' at position " + openPos + 
                        " closed by '" + c + "' at position " + i,
                        i
                    );
                }
            }
        }
        
        if (!stack.isEmpty()) {
            int openPos = positions.pop();
            char opening = stack.pop();
            return ParseResult.error(
                expression,
                "Unclosed delimiter '" + opening + "' at position " + openPos,
                openPos
            );
        }
        
        return ParseResult.success(expression, expression);
    }
    
    /**
     * Check for undefined function references.
     */
    private ParseResult checkUndefinedFunctions(String expression) {
        Matcher matcher = FUNCTION_PATTERN.matcher(expression);
        
        while (matcher.find()) {
            String functionName = matcher.group(1);
            int position = matcher.start();
            
            if (!functionRegistry.hasFunction(functionName)) {
                // Try to suggest a similar function
                String suggestion = findSimilarFunction(functionName);
                String errorMsg = "Function '" + functionName + "' is not defined";
                if (suggestion != null) {
                    errorMsg += ". Did you mean '" + suggestion + "'?";
                }
                
                return ParseResult.error(expression, errorMsg, position);
            }
        }
        
        return ParseResult.success(expression, expression);
    }
    
    /**
     * Validate that function calls have the correct number of arguments.
     */
    private ParseResult validateArgumentCounts(String expression) {
        try {
            List<FunctionCall> calls = extractFunctionCalls(expression);
            
            for (FunctionCall call : calls) {
                FunctionMetadata metadata = functionRegistry.getMetadata(call.name);
                if (metadata == null) {
                    continue; // Already checked in checkUndefinedFunctions
                }
                
                int argCount = call.argumentCount;
                int minArgs = metadata.getMinArgs();
                int maxArgs = metadata.getMaxArgs();
                
                if (argCount < minArgs || argCount > maxArgs) {
                    String errorMsg;
                    if (minArgs == maxArgs) {
                        errorMsg = "Function '" + call.name + "' expects " + minArgs + 
                                   " argument(s), got " + argCount;
                    } else {
                        errorMsg = "Function '" + call.name + "' expects " + minArgs + "-" + 
                                   maxArgs + " argument(s), got " + argCount;
                    }
                    
                    return ParseResult.error(expression, errorMsg, call.position);
                }
            }
            
            return ParseResult.success(expression, expression);
        } catch (Exception e) {
            return ParseResult.error(expression, "Error parsing function arguments: " + e.getMessage());
        }
    }
    
    /**
     * Extract all function calls from the expression with their argument counts.
     */
    private List<FunctionCall> extractFunctionCalls(String expression) {
        List<FunctionCall> calls = new ArrayList<>();
        Matcher matcher = FUNCTION_PATTERN.matcher(expression);
        
        while (matcher.find()) {
            String functionName = matcher.group(1);
            int position = matcher.start();
            int openParenPos = matcher.end() - 1;
            
            // Count arguments by finding the matching closing paren and counting commas
            int argCount = countArguments(expression, openParenPos);
            
            calls.add(new FunctionCall(functionName, position, argCount));
        }
        
        return calls;
    }
    
    /**
     * Count the number of arguments in a function call.
     * Returns 0 for empty argument list, otherwise counts comma-separated arguments.
     */
    private int countArguments(String expression, int openParenPos) {
        int depth = 1;
        int argCount = 0;
        boolean hasContent = false;
        boolean inString = false;
        char stringDelimiter = 0;
        
        for (int i = openParenPos + 1; i < expression.length(); i++) {
            char c = expression.charAt(i);
            
            // Handle string literals
            if ((c == '"' || c == '\'') && (i == 0 || expression.charAt(i - 1) != '\\')) {
                if (!inString) {
                    inString = true;
                    stringDelimiter = c;
                } else if (c == stringDelimiter) {
                    inString = false;
                }
                hasContent = true;
                continue;
            }
            
            if (inString) {
                continue;
            }
            
            // Track nesting depth
            if (c == '(') {
                depth++;
                hasContent = true;
            } else if (c == ')') {
                depth--;
                if (depth == 0) {
                    // Found matching closing paren
                    if (hasContent) {
                        argCount++; // Count the last argument
                    }
                    return argCount;
                }
                hasContent = true;
            } else if (c == ',' && depth == 1) {
                // Comma at the same level as function arguments
                argCount++;
                hasContent = false; // Reset for next argument
            } else if (!Character.isWhitespace(c)) {
                hasContent = true;
            }
        }
        
        // If we get here, parentheses weren't balanced (should be caught earlier)
        return argCount;
    }
    
    /**
     * Find a similar function name for suggestions (simple Levenshtein distance).
     */
    private String findSimilarFunction(String name) {
        String bestMatch = null;
        int bestDistance = Integer.MAX_VALUE;
        
        for (String functionName : functionRegistry.getFunctionNames()) {
            int distance = levenshteinDistance(name, functionName);
            if (distance < bestDistance && distance <= 3) {
                bestDistance = distance;
                bestMatch = functionName;
            }
        }
        
        return bestMatch;
    }
    
    /**
     * Calculate Levenshtein distance between two strings.
     */
    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        
        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }
        
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                    Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                );
            }
        }
        
        return dp[s1.length()][s2.length()];
    }
    
    /**
     * Helper class to store function call information.
     */
    private static class FunctionCall {
        final String name;
        final int position;
        final int argumentCount;
        
        FunctionCall(String name, int position, int argumentCount) {
            this.name = name;
            this.position = position;
            this.argumentCount = argumentCount;
        }
    }
}

package com.filter.dsl.evaluator;

import com.filter.dsl.context.DataContextManager;
import com.filter.dsl.functions.FunctionRegistry;
import com.filter.dsl.models.UserData;
import com.filter.dsl.parser.DSLParser;
import com.filter.dsl.parser.ParseResult;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.exception.CompileExpressionErrorException;
import com.googlecode.aviator.exception.ExpressionRuntimeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of DSLEvaluator that integrates parser, compiler, and context manager.
 * 
 * This implementation:
 * - Uses DSLParser to validate expressions before compilation
 * - Compiles expressions using AviatorScript and caches compiled forms
 * - Uses DataContextManager to create evaluation contexts
 * - Executes compiled expressions and returns results
 * - Handles all error types gracefully (parse, compile, runtime)
 * - Thread-safe with concurrent expression cache
 */
public class DSLEvaluatorImpl implements DSLEvaluator {
    
    private static final Logger LOGGER = Logger.getLogger(DSLEvaluatorImpl.class.getName());
    
    private final DSLParser parser;
    private final FunctionRegistry registry;
    private final DataContextManager contextManager;
    private final AviatorEvaluatorInstance aviator;
    
    // Thread-safe cache for compiled expressions
    private final Map<String, Expression> expressionCache;
    
    /**
     * Create a new DSLEvaluator with the specified components.
     * 
     * @param parser The DSL parser for validation
     * @param registry The function registry
     * @param contextManager The data context manager
     */
    public DSLEvaluatorImpl(DSLParser parser, FunctionRegistry registry, 
                           DataContextManager contextManager) {
        this.parser = parser;
        this.registry = registry;
        this.contextManager = contextManager;
        this.expressionCache = new ConcurrentHashMap<>();
        
        // Create and configure AviatorScript instance
        this.aviator = com.googlecode.aviator.AviatorEvaluator.newInstance();
        
        // Register all DSL functions with AviatorScript
        registry.registerAll(aviator);
        
        LOGGER.log(Level.INFO, "DSLEvaluator initialized with {0} functions", 
                  registry.size());
    }
    
    @Override
    public EvaluationResult evaluate(String expression, UserData userData) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Step 1: Parse and validate the expression
            ParseResult parseResult = parser.parse(expression);
            if (!parseResult.isValid()) {
                long elapsed = System.currentTimeMillis() - startTime;
                return EvaluationResult.error(
                    expression,
                    EvaluationResult.ErrorType.SYNTAX_ERROR,
                    parseResult.getErrorMessage(),
                    elapsed
                );
            }
            
            // Step 2: Compile the expression (or get from cache)
            Expression compiledExpr;
            try {
                compiledExpr = getOrCompileExpression(expression);
            } catch (CompileExpressionErrorException e) {
                long elapsed = System.currentTimeMillis() - startTime;
                return EvaluationResult.error(
                    expression,
                    EvaluationResult.ErrorType.COMPILATION_ERROR,
                    "Failed to compile expression: " + e.getMessage(),
                    elapsed
                );
            }
            
            // Step 3: Create evaluation context
            Map<String, Object> context = contextManager.createContext(userData);
            
            // Add the aviator instance to the context so functions can compile sub-expressions
            context.put("__aviator__", aviator);
            
            // Step 4: Execute the expression
            Object result;
            try {
                result = compiledExpr.execute(context);
            } catch (ExpressionRuntimeException e) {
                long elapsed = System.currentTimeMillis() - startTime;
                return EvaluationResult.error(
                    expression,
                    EvaluationResult.ErrorType.RUNTIME_ERROR,
                    "Runtime error: " + e.getMessage(),
                    elapsed
                );
            } catch (Exception e) {
                long elapsed = System.currentTimeMillis() - startTime;
                return EvaluationResult.error(
                    expression,
                    EvaluationResult.ErrorType.RUNTIME_ERROR,
                    "Unexpected error: " + e.getMessage(),
                    elapsed
                );
            }
            
            // Step 5: Return success result
            long elapsed = System.currentTimeMillis() - startTime;
            return EvaluationResult.success(expression, result, elapsed);
            
        } catch (Exception e) {
            // Catch any unexpected errors
            long elapsed = System.currentTimeMillis() - startTime;
            LOGGER.log(Level.SEVERE, "Unexpected error evaluating expression: " + expression, e);
            return EvaluationResult.error(
                expression,
                EvaluationResult.ErrorType.RUNTIME_ERROR,
                "Unexpected error: " + e.getMessage(),
                elapsed
            );
        }
    }
    
    @Override
    public List<EvaluationResult> evaluateBatch(String expression, List<UserData> users) {
        List<EvaluationResult> results = new ArrayList<>(users.size());
        
        // Parse and compile once
        ParseResult parseResult = parser.parse(expression);
        if (!parseResult.isValid()) {
            // If parse fails, return error for all users
            EvaluationResult error = EvaluationResult.error(
                expression,
                EvaluationResult.ErrorType.SYNTAX_ERROR,
                parseResult.getErrorMessage(),
                0
            );
            for (int i = 0; i < users.size(); i++) {
                results.add(error);
            }
            return results;
        }
        
        Expression compiledExpr;
        try {
            compiledExpr = getOrCompileExpression(expression);
        } catch (CompileExpressionErrorException e) {
            // If compilation fails, return error for all users
            EvaluationResult error = EvaluationResult.error(
                expression,
                EvaluationResult.ErrorType.COMPILATION_ERROR,
                "Failed to compile expression: " + e.getMessage(),
                0
            );
            for (int i = 0; i < users.size(); i++) {
                results.add(error);
            }
            return results;
        }
        
        // Evaluate for each user
        for (UserData userData : users) {
            long startTime = System.currentTimeMillis();
            
            try {
                Map<String, Object> context = contextManager.createContext(userData);
                // Add the aviator instance to the context so functions can compile sub-expressions
                context.put("__aviator__", aviator);
                Object result = compiledExpr.execute(context);
                long elapsed = System.currentTimeMillis() - startTime;
                results.add(EvaluationResult.success(expression, result, elapsed));
            } catch (ExpressionRuntimeException e) {
                long elapsed = System.currentTimeMillis() - startTime;
                results.add(EvaluationResult.error(
                    expression,
                    EvaluationResult.ErrorType.RUNTIME_ERROR,
                    "Runtime error: " + e.getMessage(),
                    elapsed
                ));
            } catch (Exception e) {
                long elapsed = System.currentTimeMillis() - startTime;
                results.add(EvaluationResult.error(
                    expression,
                    EvaluationResult.ErrorType.RUNTIME_ERROR,
                    "Unexpected error: " + e.getMessage(),
                    elapsed
                ));
            }
        }
        
        return results;
    }
    
    @Override
    public void clearCache() {
        expressionCache.clear();
        LOGGER.log(Level.INFO, "Expression cache cleared");
    }
    
    @Override
    public int getCacheSize() {
        return expressionCache.size();
    }
    
    /**
     * Get a compiled expression from cache, or compile and cache it.
     * 
     * @param expression The DSL expression string
     * @return The compiled expression
     * @throws CompileExpressionErrorException if compilation fails
     */
    private Expression getOrCompileExpression(String expression) {
        // Check cache first
        Expression cached = expressionCache.get(expression);
        if (cached != null) {
            return cached;
        }
        
        // Compile the expression
        Expression compiled = aviator.compile(expression, true);
        
        // Cache it
        expressionCache.put(expression, compiled);
        
        LOGGER.log(Level.FINE, "Compiled and cached expression: {0}", expression);
        
        return compiled;
    }
}

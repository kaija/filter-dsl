package com.filter.dsl.evaluator;

import com.filter.dsl.models.UserData;

import java.util.List;

/**
 * Main entry point for evaluating DSL expressions against user data.
 * 
 * The DSLEvaluator integrates the parser, AviatorScript compiler, and context manager
 * to provide a complete evaluation pipeline:
 * 
 * 1. Parse and validate the DSL expression
 * 2. Compile the expression using AviatorScript
 * 3. Create evaluation context from user data
 * 4. Execute the compiled expression
 * 5. Return the result or error
 * 
 * The evaluator handles all error types gracefully and provides detailed error information.
 * It also caches compiled expressions for improved performance when evaluating the same
 * expression multiple times.
 * 
 * Usage:
 * <pre>
 * DSLEvaluator evaluator = new DSLEvaluatorImpl(parser, registry, contextManager);
 * 
 * String expression = "GT(COUNT(WHERE(EQ(EVENT(\"event_name\"), \"purchase\"))), 5)";
 * UserData userData = loadUserData();
 * 
 * EvaluationResult result = evaluator.evaluate(expression, userData);
 * 
 * if (result.isSuccess()) {
 *     Boolean matches = (Boolean) result.getValue();
 *     System.out.println("User matches criteria: " + matches);
 * } else {
 *     System.err.println("Evaluation failed: " + result.getErrorMessage());
 * }
 * </pre>
 * 
 * Thread Safety:
 * Implementations should be thread-safe for use in multi-threaded environments.
 * The expression cache should be properly synchronized.
 */
public interface DSLEvaluator {
    
    /**
     * Evaluate a DSL expression for a single user.
     * 
     * This method performs the complete evaluation pipeline:
     * 1. Validates the expression syntax
     * 2. Compiles the expression (or retrieves from cache)
     * 3. Creates evaluation context from user data
     * 4. Executes the expression
     * 5. Returns the result
     * 
     * All errors are caught and returned as error results - this method never throws exceptions.
     * 
     * @param expression The DSL expression string
     * @param userData The user data to evaluate against
     * @return EvaluationResult containing the value or error
     */
    EvaluationResult evaluate(String expression, UserData userData);
    
    /**
     * Evaluate a DSL expression for multiple users (batch evaluation).
     * 
     * This method is optimized for evaluating the same expression against multiple users.
     * The expression is parsed and compiled once, then executed for each user.
     * 
     * This is more efficient than calling evaluate() multiple times with the same expression.
     * 
     * @param expression The DSL expression string
     * @param users List of user data to evaluate against
     * @return List of results corresponding to each user
     */
    List<EvaluationResult> evaluateBatch(String expression, List<UserData> users);
    
    /**
     * Clear the compiled expression cache.
     * 
     * This can be useful to free memory or force recompilation of expressions
     * (e.g., after updating function implementations).
     */
    void clearCache();
    
    /**
     * Get the number of expressions currently cached.
     * 
     * @return The cache size
     */
    int getCacheSize();
}

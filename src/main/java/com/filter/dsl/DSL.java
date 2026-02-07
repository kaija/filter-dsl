package com.filter.dsl;

import com.filter.dsl.context.DataContextManager;
import com.filter.dsl.context.DataContextManagerImpl;
import com.filter.dsl.evaluator.DSLEvaluator;
import com.filter.dsl.evaluator.DSLEvaluatorImpl;
import com.filter.dsl.evaluator.EvaluationResult;
import com.filter.dsl.functions.FunctionRegistry;
import com.filter.dsl.models.UserData;
import com.filter.dsl.parser.DSLParser;
import com.filter.dsl.parser.DSLParserImpl;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main facade for the User Segmentation DSL library.
 * 
 * This is the primary entry point for applications using the DSL library.
 * It provides a simple, clean API that hides the complexity of parser,
 * registry, context manager, and evaluator initialization.
 * 
 * <h2>Quick Start</h2>
 * <pre>
 * // Simple usage with default configuration
 * String expression = "GT(COUNT(WHERE(EQ(EVENT(\"event_name\"), \"purchase\"))), 5)";
 * UserData userData = loadUserData();
 * 
 * EvaluationResult result = DSL.evaluate(expression, userData);
 * 
 * if (result.isSuccess()) {
 *     Boolean matches = (Boolean) result.getValue();
 *     System.out.println("User matches criteria: " + matches);
 * } else {
 *     System.err.println("Evaluation failed: " + result.getErrorMessage());
 * }
 * </pre>
 * 
 * <h2>Custom Configuration</h2>
 * <pre>
 * // Create a custom DSL instance with configuration
 * DSL dsl = DSL.builder()
 *     .enableAutoDiscovery(true)
 *     .enableCaching(true)
 *     .build();
 * 
 * EvaluationResult result = dsl.evaluate(expression, userData);
 * </pre>
 * 
 * <h2>Batch Evaluation</h2>
 * <pre>
 * // Evaluate the same expression for multiple users
 * List&lt;UserData&gt; users = loadUsers();
 * List&lt;EvaluationResult&gt; results = DSL.evaluateBatch(expression, users);
 * </pre>
 * 
 * <h2>Thread Safety</h2>
 * The DSL facade and all its components are thread-safe and can be safely used
 * in multi-threaded environments. The default instance is a singleton that can
 * be shared across threads. Custom instances created via the builder are also
 * thread-safe.
 * 
 * <h2>Library Integration</h2>
 * Add this library to your project:
 * <pre>
 * &lt;!-- Maven --&gt;
 * &lt;dependency&gt;
 *   &lt;groupId&gt;com.example&lt;/groupId&gt;
 *   &lt;artifactId&gt;user-segmentation-dsl&lt;/artifactId&gt;
 *   &lt;version&gt;1.0.0&lt;/version&gt;
 * &lt;/dependency&gt;
 * 
 * // Gradle
 * implementation 'com.example:user-segmentation-dsl:1.0.0'
 * </pre>
 * 
 * @see EvaluationResult
 * @see UserData
 */
public class DSL {
    
    private static final Logger LOGGER = Logger.getLogger(DSL.class.getName());
    
    // Singleton instance for simple static usage
    private static volatile DSL defaultInstance;
    
    // Core components
    private final DSLEvaluator evaluator;
    private final FunctionRegistry registry;
    private final DSLParser parser;
    private final DataContextManager contextManager;
    
    /**
     * Private constructor - use builder() or static methods.
     * 
     * @param builder The builder containing configuration
     */
    private DSL(Builder builder) {
        // Initialize function registry
        this.registry = new FunctionRegistry();
        
        // Auto-discover functions if enabled
        if (builder.autoDiscovery) {
            int count = registry.discoverAndRegister("com.filter.dsl.functions");
            LOGGER.log(Level.INFO, "Auto-discovered {0} DSL functions", count);
        }
        
        // Initialize parser
        this.parser = new DSLParserImpl(registry);
        
        // Initialize context manager
        this.contextManager = new DataContextManagerImpl();
        
        // Initialize evaluator
        this.evaluator = new DSLEvaluatorImpl(parser, registry, contextManager);
        
        // Configure caching
        if (!builder.cachingEnabled) {
            evaluator.clearCache();
        }
        
        LOGGER.log(Level.INFO, "DSL instance initialized with {0} functions", registry.size());
    }
    
    /**
     * Get the default DSL instance (singleton).
     * 
     * This method uses double-checked locking to ensure thread-safe lazy initialization
     * of the default instance. The default instance has auto-discovery enabled and
     * caching enabled.
     * 
     * @return The default DSL instance
     */
    private static DSL getDefaultInstance() {
        if (defaultInstance == null) {
            synchronized (DSL.class) {
                if (defaultInstance == null) {
                    defaultInstance = builder()
                        .enableAutoDiscovery(true)
                        .enableCaching(true)
                        .build();
                }
            }
        }
        return defaultInstance;
    }
    
    /**
     * Evaluate a DSL expression for a single user (static convenience method).
     * 
     * This is the simplest way to use the DSL library. It uses the default
     * singleton instance with auto-discovery and caching enabled.
     * 
     * @param expression The DSL expression string
     * @param userData The user data to evaluate against
     * @return EvaluationResult containing the value or error
     */
    public static EvaluationResult evaluate(String expression, UserData userData) {
        return getDefaultInstance().evaluateInstance(expression, userData);
    }
    
    /**
     * Evaluate a DSL expression for multiple users (static convenience method).
     * 
     * This method is optimized for evaluating the same expression against multiple users.
     * The expression is parsed and compiled once, then executed for each user.
     * 
     * @param expression The DSL expression string
     * @param users List of user data to evaluate against
     * @return List of results corresponding to each user
     */
    public static List<EvaluationResult> evaluateBatch(String expression, List<UserData> users) {
        return getDefaultInstance().evaluateBatchInstance(expression, users);
    }
    
    /**
     * Clear the expression cache in the default instance.
     * 
     * This can be useful to free memory or force recompilation of expressions.
     */
    public static void clearCache() {
        getDefaultInstance().clearCacheInstance();
    }
    
    /**
     * Get the number of cached expressions in the default instance.
     * 
     * @return The cache size
     */
    public static int getCacheSize() {
        return getDefaultInstance().getCacheSizeInstance();
    }
    
    /**
     * Evaluate a DSL expression for a single user (instance method).
     * 
     * Use this method when working with a custom DSL instance created via the builder.
     * 
     * @param expression The DSL expression string
     * @param userData The user data to evaluate against
     * @return EvaluationResult containing the value or error
     */
    public EvaluationResult evaluateInstance(String expression, UserData userData) {
        return evaluator.evaluate(expression, userData);
    }
    
    /**
     * Evaluate a DSL expression for multiple users (instance method).
     * 
     * Use this method when working with a custom DSL instance created via the builder.
     * 
     * @param expression The DSL expression string
     * @param users List of user data to evaluate against
     * @return List of results corresponding to each user
     */
    public List<EvaluationResult> evaluateBatchInstance(String expression, List<UserData> users) {
        return evaluator.evaluateBatch(expression, users);
    }
    
    /**
     * Clear the expression cache in this instance.
     */
    public void clearCacheInstance() {
        evaluator.clearCache();
    }
    
    /**
     * Get the number of cached expressions in this instance.
     * 
     * @return The cache size
     */
    public int getCacheSizeInstance() {
        return evaluator.getCacheSize();
    }
    
    /**
     * Get the function registry for this instance.
     * 
     * This can be used to inspect registered functions or register custom functions.
     * 
     * @return The function registry
     */
    public FunctionRegistry getRegistry() {
        return registry;
    }
    
    /**
     * Get the parser for this instance.
     * 
     * This can be used to validate expressions or pretty-print them.
     * 
     * @return The DSL parser
     */
    public DSLParser getParser() {
        return parser;
    }
    
    /**
     * Create a new DSL builder for custom configuration.
     * 
     * @return A new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Builder for creating custom DSL instances with specific configuration.
     * 
     * <h2>Example Usage</h2>
     * <pre>
     * DSL dsl = DSL.builder()
     *     .enableAutoDiscovery(true)
     *     .enableCaching(true)
     *     .build();
     * </pre>
     */
    public static class Builder {
        private boolean autoDiscovery = true;
        private boolean cachingEnabled = true;
        
        /**
         * Enable or disable automatic function discovery.
         * 
         * When enabled, the DSL will automatically scan the classpath for
         * DSL function implementations and register them. This is the recommended
         * approach for most applications.
         * 
         * When disabled, you must manually register functions using the registry.
         * 
         * Default: true
         * 
         * @param enabled true to enable auto-discovery
         * @return This builder for chaining
         */
        public Builder enableAutoDiscovery(boolean enabled) {
            this.autoDiscovery = enabled;
            return this;
        }
        
        /**
         * Enable or disable expression caching.
         * 
         * When enabled, compiled expressions are cached for improved performance
         * when evaluating the same expression multiple times. This is recommended
         * for most applications.
         * 
         * When disabled, expressions are recompiled on every evaluation. This can
         * be useful for development or testing scenarios where functions are being
         * modified frequently.
         * 
         * Default: true
         * 
         * @param enabled true to enable caching
         * @return This builder for chaining
         */
        public Builder enableCaching(boolean enabled) {
            this.cachingEnabled = enabled;
            return this;
        }
        
        /**
         * Build the DSL instance with the configured options.
         * 
         * @return A new DSL instance
         */
        public DSL build() {
            return new DSL(this);
        }
    }
}

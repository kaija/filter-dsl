package com.filter.dsl.functions;

import com.googlecode.aviator.AviatorEvaluatorInstance;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Registry for managing DSL functions.
 *
 * This class maintains a registry of all available DSL functions and provides:
 * - Registration of functions with AviatorScript
 * - Function metadata lookup for validation
 * - Function discovery and listing
 * - Automatic classpath scanning for function discovery
 *
 * Usage:
 * <pre>
 * // Manual registration
 * FunctionRegistry registry = new FunctionRegistry();
 * registry.register(new CountFunction());
 * registry.register(new SumFunction());
 * registry.registerAll(aviatorInstance);
 *
 * // Auto-discovery
 * FunctionRegistry registry = new FunctionRegistry();
 * registry.discoverAndRegister("com.filter.dsl.functions");
 * registry.registerAll(aviatorInstance);
 * </pre>
 */
public class FunctionRegistry {

    private static final Logger LOGGER = Logger.getLogger(FunctionRegistry.class.getName());

    private final Map<String, DSLFunction> functions = new HashMap<>();
    private final Map<String, FunctionMetadata> metadata = new HashMap<>();

    /**
     * Register a single DSL function.
     *
     * @param function The function to register
     * @throws IllegalArgumentException if function name is not UPPERCASE or already registered
     */
    public void register(DSLFunction function) {
        String name = function.getName();

        // Validate function name is UPPERCASE
        if (!name.equals(name.toUpperCase())) {
            throw new IllegalArgumentException(
                "Function name must be UPPERCASE: " + name
            );
        }

        // Check for duplicate registration
        if (functions.containsKey(name)) {
            throw new IllegalArgumentException(
                "Function already registered: " + name
            );
        }

        // Validate metadata
        FunctionMetadata meta = function.getFunctionMetadata();
        if (meta == null) {
            throw new IllegalArgumentException(
                "Function metadata cannot be null: " + name
            );
        }
        if (!name.equals(meta.getName())) {
            throw new IllegalArgumentException(
                "Function name mismatch: getName()=" + name + ", metadata.name=" + meta.getName()
            );
        }

        functions.put(name, function);
        metadata.put(name, meta);
    }

    /**
     * Register all functions with an AviatorScript instance.
     *
     * @param aviator The AviatorScript evaluator instance
     */
    public void registerAll(AviatorEvaluatorInstance aviator) {
        for (DSLFunction function : functions.values()) {
            aviator.addFunction(function);
        }
    }

    /**
     * Get function metadata for validation.
     *
     * @param functionName The UPPERCASE function name
     * @return Function metadata, or null if function not found
     */
    public FunctionMetadata getMetadata(String functionName) {
        return metadata.get(functionName);
    }

    /**
     * Check if a function is registered.
     *
     * @param functionName The UPPERCASE function name
     * @return true if the function is registered
     */
    public boolean hasFunction(String functionName) {
        return functions.containsKey(functionName);
    }

    /**
     * Get a registered function.
     *
     * @param functionName The UPPERCASE function name
     * @return The function, or null if not found
     */
    public DSLFunction getFunction(String functionName) {
        return functions.get(functionName);
    }

    /**
     * Get all registered function names.
     *
     * @return Set of function names
     */
    public Set<String> getFunctionNames() {
        return functions.keySet();
    }

    /**
     * Get the number of registered functions.
     *
     * @return The function count
     */
    public int size() {
        return functions.size();
    }

    /**
     * Clear all registered functions.
     */
    public void clear() {
        functions.clear();
        metadata.clear();
    }

    /**
     * Discover and register all DSL functions in the specified package.
     *
     * This method scans the classpath for classes that extend DSLFunction
     * in the given package and its sub-packages, instantiates them, and
     * registers them automatically.
     *
     * @param packageName The base package to scan (e.g., "com.filter.dsl.functions")
     * @return The number of functions discovered and registered
     */
    public int discoverAndRegister(String packageName) {
        int count = 0;
        try {
            List<Class<?>> classes = findClassesInPackage(packageName);

            for (Class<?> clazz : classes) {
                if (isDSLFunctionClass(clazz)) {
                    try {
                        DSLFunction function = (DSLFunction) clazz.getDeclaredConstructor().newInstance();
                        register(function);
                        count++;
                        LOGGER.log(Level.FINE, "Discovered and registered function: {0}", function.getName());
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Failed to instantiate function class: " + clazz.getName(), e);
                    }
                }
            }

            LOGGER.log(Level.INFO, "Discovered {0} DSL functions in package: {1}", new Object[]{count, packageName});
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to discover functions in package: " + packageName, e);
        }

        return count;
    }

    /**
     * Check if a class is a concrete DSLFunction implementation.
     *
     * @param clazz The class to check
     * @return true if the class is a concrete DSLFunction subclass
     */
    private boolean isDSLFunctionClass(Class<?> clazz) {
        return DSLFunction.class.isAssignableFrom(clazz)
            && !clazz.equals(DSLFunction.class)
            && !Modifier.isAbstract(clazz.getModifiers())
            && !clazz.isInterface();
    }

    /**
     * Find all classes in a package and its sub-packages.
     *
     * @param packageName The package name to scan
     * @return List of classes found
     * @throws IOException if there's an error reading the classpath
     * @throws ClassNotFoundException if a class cannot be loaded
     */
    private List<Class<?>> findClassesInPackage(String packageName) throws IOException, ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }

        List<Class<?>> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }

        return classes;
    }

    /**
     * Recursively find classes in a directory.
     *
     * @param directory The directory to search
     * @param packageName The package name for the directory
     * @return List of classes found
     * @throws ClassNotFoundException if a class cannot be loaded
     */
    private List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();

        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return classes;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                // Recursively search sub-packages
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                // Load the class
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                try {
                    classes.add(Class.forName(className));
                } catch (ClassNotFoundException | NoClassDefFoundError e) {
                    LOGGER.log(Level.FINE, "Could not load class: " + className, e);
                }
            }
        }

        return classes;
    }
}

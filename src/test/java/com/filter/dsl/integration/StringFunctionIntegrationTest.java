package com.filter.dsl.integration;

import com.filter.dsl.functions.FunctionRegistry;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for string matching functions in real-world scenarios
 */
class StringFunctionIntegrationTest {

    private FunctionRegistry registry;
    private AviatorEvaluatorInstance aviator;

    @BeforeEach
    void setUp() {
        registry = new FunctionRegistry();
        aviator = AviatorEvaluator.newInstance();
        
        // Auto-discover and register all string functions
        registry.discoverAndRegister("com.filter.dsl.functions.string");
        // Also register comparison functions for some tests
        registry.discoverAndRegister("com.filter.dsl.functions.comparison");
        registry.registerAll(aviator);
    }

    @Test
    void testEmailValidationWithRegex() {
        Map<String, Object> env = new HashMap<>();
        env.put("email", "user@example.com");
        
        // Validate email format
        Object result = aviator.execute(
            "REGEX_MATCH(email, '^[\\\\w.]+@[\\\\w.]+\\\\.[a-z]+$')", 
            env);
        assertEquals(Boolean.TRUE, result);
        
        // Invalid email
        env.put("email", "invalid-email");
        result = aviator.execute(
            "REGEX_MATCH(email, '^[\\\\w.]+@[\\\\w.]+\\\\.[a-z]+$')", 
            env);
        assertEquals(Boolean.FALSE, result);
    }

    @Test
    void testUrlFiltering() {
        Map<String, Object> env = new HashMap<>();
        
        // Check if URL starts with https
        env.put("url", "https://example.com/page");
        Object result = aviator.execute("STARTS_WITH(url, 'https://')", env);
        assertEquals(Boolean.TRUE, result);
        
        // Check if URL ends with specific path
        result = aviator.execute("ENDS_WITH(url, '/page')", env);
        assertEquals(Boolean.TRUE, result);
        
        // Check if URL contains domain
        result = aviator.execute("CONTAINS(url, 'example.com')", env);
        assertEquals(Boolean.TRUE, result);
    }

    @Test
    void testEventNameFiltering() {
        Map<String, Object> env = new HashMap<>();
        env.put("eventName", "purchase_completed");
        
        // Check if event is a purchase event
        Object result = aviator.execute("STARTS_WITH(eventName, 'purchase')", env);
        assertEquals(Boolean.TRUE, result);
        
        // Check if event is completed
        result = aviator.execute("ENDS_WITH(eventName, 'completed')", env);
        assertEquals(Boolean.TRUE, result);
        
        // Check if event contains underscore
        result = aviator.execute("CONTAINS(eventName, '_')", env);
        assertEquals(Boolean.TRUE, result);
    }

    @Test
    void testUtmParameterMatching() {
        Map<String, Object> env = new HashMap<>();
        env.put("utmSource", "google");
        env.put("utmMedium", "cpc");
        env.put("utmCampaign", "summer_sale_2024");
        
        // Check UTM source
        Object result = aviator.execute("EQ(utmSource, 'google')", env);
        assertEquals(Boolean.TRUE, result);
        
        // Check if campaign contains year
        result = aviator.execute("CONTAINS(utmCampaign, '2024')", env);
        assertEquals(Boolean.TRUE, result);
        
        // Check if campaign matches pattern
        result = aviator.execute(
            "REGEX_MATCH(utmCampaign, '^\\\\w+_\\\\w+_\\\\d{4}$')", 
            env);
        assertEquals(Boolean.TRUE, result);
    }

    @Test
    void testComplexStringConditions() {
        Map<String, Object> env = new HashMap<>();
        env.put("referrer", "https://google.com/search?q=test");
        
        // Check if referrer is from search engine AND contains query
        Object result = aviator.execute(
            "CONTAINS(referrer, 'google.com') && CONTAINS(referrer, '?q=')", 
            env);
        assertEquals(Boolean.TRUE, result);
        
        // Check if referrer is from google OR bing
        result = aviator.execute(
            "CONTAINS(referrer, 'google.com') || CONTAINS(referrer, 'bing.com')", 
            env);
        assertEquals(Boolean.TRUE, result);
    }

    @Test
    void testPhoneNumberValidation() {
        Map<String, Object> env = new HashMap<>();
        
        // US phone number format
        env.put("phone", "123-456-7890");
        Object result = aviator.execute(
            "REGEX_MATCH(phone, '^\\\\d{3}-\\\\d{3}-\\\\d{4}$')", 
            env);
        assertEquals(Boolean.TRUE, result);
        
        // Invalid format
        env.put("phone", "123456789");
        result = aviator.execute(
            "REGEX_MATCH(phone, '^\\\\d{3}-\\\\d{3}-\\\\d{4}$')", 
            env);
        assertEquals(Boolean.FALSE, result);
    }

    @Test
    void testUserAgentParsing() {
        Map<String, Object> env = new HashMap<>();
        env.put("userAgent", "Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X)");
        
        // Check if mobile device
        Object result = aviator.execute("CONTAINS(userAgent, 'iPhone')", env);
        assertEquals(Boolean.TRUE, result);
        
        // Check iOS version
        result = aviator.execute("CONTAINS(userAgent, 'OS 14_0')", env);
        assertEquals(Boolean.TRUE, result);
        
        // Check if it's a Mozilla browser
        result = aviator.execute("STARTS_WITH(userAgent, 'Mozilla')", env);
        assertEquals(Boolean.TRUE, result);
    }

    @Test
    void testProductSkuMatching() {
        Map<String, Object> env = new HashMap<>();
        env.put("sku", "PROD-2024-ABC-001");
        
        // Check if SKU starts with product prefix
        Object result = aviator.execute("STARTS_WITH(sku, 'PROD-')", env);
        assertEquals(Boolean.TRUE, result);
        
        // Check if SKU contains year
        result = aviator.execute("CONTAINS(sku, '2024')", env);
        assertEquals(Boolean.TRUE, result);
        
        // Validate SKU format
        result = aviator.execute(
            "REGEX_MATCH(sku, '^PROD-\\\\d{4}-[A-Z]{3}-\\\\d{3}$')", 
            env);
        assertEquals(Boolean.TRUE, result);
    }

    @Test
    void testCaseSensitiveMatching() {
        Map<String, Object> env = new HashMap<>();
        env.put("text", "Hello World");
        
        // Case-sensitive CONTAINS
        Object result = aviator.execute("CONTAINS(text, 'Hello')", env);
        assertEquals(Boolean.TRUE, result);
        
        result = aviator.execute("CONTAINS(text, 'hello')", env);
        assertEquals(Boolean.FALSE, result);
        
        // Case-sensitive STARTS_WITH
        result = aviator.execute("STARTS_WITH(text, 'Hello')", env);
        assertEquals(Boolean.TRUE, result);
        
        result = aviator.execute("STARTS_WITH(text, 'hello')", env);
        assertEquals(Boolean.FALSE, result);
        
        // Case-sensitive ENDS_WITH
        result = aviator.execute("ENDS_WITH(text, 'World')", env);
        assertEquals(Boolean.TRUE, result);
        
        result = aviator.execute("ENDS_WITH(text, 'world')", env);
        assertEquals(Boolean.FALSE, result);
    }

    @Test
    void testStringMatchingWithNullValues() {
        Map<String, Object> env = new HashMap<>();
        env.put("text", null);
        
        // All string functions should handle null gracefully
        Object result = aviator.execute("CONTAINS(text, 'test')", env);
        assertEquals(Boolean.FALSE, result);
        
        result = aviator.execute("STARTS_WITH(text, 'test')", env);
        assertEquals(Boolean.FALSE, result);
        
        result = aviator.execute("ENDS_WITH(text, 'test')", env);
        assertEquals(Boolean.FALSE, result);
        
        result = aviator.execute("REGEX_MATCH(text, '.*')", env);
        assertEquals(Boolean.FALSE, result);
    }

    @Test
    void testAutoDiscoveryRegistersAllStringFunctions() {
        // Verify that auto-discovery found and registered all string matching functions
        assertTrue(registry.hasFunction("CONTAINS"));
        assertTrue(registry.hasFunction("STARTS_WITH"));
        assertTrue(registry.hasFunction("ENDS_WITH"));
        assertTrue(registry.hasFunction("REGEX_MATCH"));
        
        // Should also have REVERSE function that was already there
        assertTrue(registry.hasFunction("REVERSE"));
        
        // Verify we have at least 5 string functions
        assertTrue(registry.size() >= 5);
    }
}

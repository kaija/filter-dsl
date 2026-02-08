package com.filter.dsl.unit.functions;

import com.filter.dsl.functions.FunctionArgumentException;
import com.filter.dsl.functions.data.ProfileFunction;
import com.filter.dsl.functions.data.EventFunction;
import com.filter.dsl.functions.data.ParamFunction;
import com.filter.dsl.models.Event;
import com.filter.dsl.models.Profile;
import com.filter.dsl.models.UserData;
import com.googlecode.aviator.runtime.type.AviatorJavaType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for data access functions: PROFILE, EVENT, PARAM
 */
class DataAccessFunctionsTest {

    private Map<String, Object> env;
    private UserData userData;
    private Profile profile;
    private Event event;

    @BeforeEach
    void setUp() {
        env = new HashMap<>();
        
        // Create test profile
        profile = Profile.builder()
            .country("US")
            .city("New York")
            .language("en")
            .continent("North America")
            .timezone("America/New_York")
            .uuid("user-123")
            .build();
        
        // Create test event
        event = Event.builder()
            .uuid("event-456")
            .eventName("purchase")
            .eventType("action")
            .timestamp("2024-01-15T10:30:00Z")
            .duration(120)
            .isFirstInVisit(false)
            .isLastInVisit(false)
            .isFirstEvent(false)
            .isCurrent(true)
            .integration("web")
            .app("myapp")
            .platform("web")
            .isHttps(true)
            .triggerable(true)
            .parameter("amount", 99.99)
            .parameter("product_id", "SKU-12345")
            .parameter("utm_source", "google")
            .parameter("utm_campaign", "summer_sale")
            .build();
        
        // Create test user data
        userData = UserData.builder()
            .profile(profile)
            .event(event)
            .build();
        
        // Set up environment
        env.put("userData", userData);
        env.put("currentEvent", event);
    }

    // Helper method to create AviatorObject from a value
    private AviatorObject createAviatorObject(Object value) {
        return new AviatorJavaType("value") {
            @Override
            public Object getValue(Map<String, Object> environment) {
                return value;
            }
        };
    }

    // ========== PROFILE Function Tests ==========

    @Test
    void testProfileGetCountry() {
        ProfileFunction profileFunc = new ProfileFunction();
        AviatorObject result = profileFunc.call(env, new AviatorString("country"));
        assertEquals("US", result.getValue(env));
    }

    @Test
    void testProfileGetCity() {
        ProfileFunction profileFunc = new ProfileFunction();
        AviatorObject result = profileFunc.call(env, new AviatorString("city"));
        assertEquals("New York", result.getValue(env));
    }

    @Test
    void testProfileGetLanguage() {
        ProfileFunction profileFunc = new ProfileFunction();
        AviatorObject result = profileFunc.call(env, new AviatorString("language"));
        assertEquals("en", result.getValue(env));
    }

    @Test
    void testProfileGetContinent() {
        ProfileFunction profileFunc = new ProfileFunction();
        AviatorObject result = profileFunc.call(env, new AviatorString("continent"));
        assertEquals("North America", result.getValue(env));
    }

    @Test
    void testProfileGetTimezone() {
        ProfileFunction profileFunc = new ProfileFunction();
        AviatorObject result = profileFunc.call(env, new AviatorString("timezone"));
        assertEquals("America/New_York", result.getValue(env));
    }

    @Test
    void testProfileGetUuid() {
        ProfileFunction profileFunc = new ProfileFunction();
        AviatorObject result = profileFunc.call(env, new AviatorString("uuid"));
        assertEquals("user-123", result.getValue(env));
    }

    @Test
    void testProfileNonExistentField() {
        ProfileFunction profileFunc = new ProfileFunction();
        AviatorObject result = profileFunc.call(env, new AviatorString("nonexistent"));
        assertNull(result.getValue(env));
    }

    @Test
    void testProfileNullFieldName() {
        ProfileFunction profileFunc = new ProfileFunction();
        AviatorObject result = profileFunc.call(env, createAviatorObject(null));
        assertNull(result.getValue(env));
    }

    @Test
    void testProfileEmptyFieldName() {
        ProfileFunction profileFunc = new ProfileFunction();
        AviatorObject result = profileFunc.call(env, new AviatorString(""));
        assertNull(result.getValue(env));
    }

    @Test
    void testProfileNoUserData() {
        ProfileFunction profileFunc = new ProfileFunction();
        Map<String, Object> emptyEnv = new HashMap<>();
        AviatorObject result = profileFunc.call(emptyEnv, new AviatorString("country"));
        assertNull(result.getValue(emptyEnv));
    }

    @Test
    void testProfileNullProfile() {
        ProfileFunction profileFunc = new ProfileFunction();
        UserData userDataWithoutProfile = UserData.builder().build();
        Map<String, Object> envWithoutProfile = new HashMap<>();
        envWithoutProfile.put("userData", userDataWithoutProfile);
        
        AviatorObject result = profileFunc.call(envWithoutProfile, new AviatorString("country"));
        assertNull(result.getValue(envWithoutProfile));
    }

    @Test
    void testProfileWithWrongArgumentCount() {
        ProfileFunction profileFunc = new ProfileFunction();
        assertThrows(IllegalArgumentException.class, () -> {
            profileFunc.call(env, new AviatorString("country"), new AviatorString("city"));
        });
    }

    @Test
    void testProfileWithNoArguments() {
        ProfileFunction profileFunc = new ProfileFunction();
        assertThrows(FunctionArgumentException.class, () -> {
            profileFunc.call(env, new AviatorObject[0]);
        });
    }

    @Test
    void testProfileMetadata() {
        ProfileFunction profileFunc = new ProfileFunction();
        assertEquals("PROFILE", profileFunc.getName());
        assertEquals(1, profileFunc.getFunctionMetadata().getMinArgs());
        assertEquals(1, profileFunc.getFunctionMetadata().getMaxArgs());
        assertEquals("Returns the value of a field from the user profile", 
            profileFunc.getFunctionMetadata().getDescription());
    }

    @Test
    void testProfileWithNullValue() {
        // Test with a profile field that is null
        Profile profileWithNull = Profile.builder()
            .country("US")
            .city(null)  // Null city
            .build();
        
        UserData userDataWithNull = UserData.builder()
            .profile(profileWithNull)
            .build();
        
        Map<String, Object> envWithNull = new HashMap<>();
        envWithNull.put("userData", userDataWithNull);
        
        ProfileFunction profileFunc = new ProfileFunction();
        AviatorObject result = profileFunc.call(envWithNull, new AviatorString("city"));
        assertNull(result.getValue(envWithNull));
    }

    // ========== EVENT Function Tests ==========

    @Test
    void testEventGetEventName() {
        EventFunction eventFunc = new EventFunction();
        AviatorObject result = eventFunc.call(env, new AviatorString("event_name"));
        assertEquals("purchase", result.getValue(env));
    }

    @Test
    void testEventGetEventType() {
        EventFunction eventFunc = new EventFunction();
        AviatorObject result = eventFunc.call(env, new AviatorString("event_type"));
        assertEquals("action", result.getValue(env));
    }

    @Test
    void testEventGetTimestamp() {
        EventFunction eventFunc = new EventFunction();
        AviatorObject result = eventFunc.call(env, new AviatorString("timestamp"));
        assertEquals("2024-01-15T10:30:00Z", result.getValue(env));
    }

    @Test
    void testEventGetDuration() {
        EventFunction eventFunc = new EventFunction();
        AviatorObject result = eventFunc.call(env, new AviatorString("duration"));
        assertEquals(120, result.getValue(env));
    }

    @Test
    void testEventGetUuid() {
        EventFunction eventFunc = new EventFunction();
        AviatorObject result = eventFunc.call(env, new AviatorString("uuid"));
        assertEquals("event-456", result.getValue(env));
    }

    @Test
    void testEventGetIsFirstInVisit() {
        EventFunction eventFunc = new EventFunction();
        AviatorObject result = eventFunc.call(env, new AviatorString("is_first_in_visit"));
        assertEquals(false, result.getValue(env));
    }

    @Test
    void testEventGetIsLastInVisit() {
        EventFunction eventFunc = new EventFunction();
        AviatorObject result = eventFunc.call(env, new AviatorString("is_last_in_visit"));
        assertEquals(false, result.getValue(env));
    }

    @Test
    void testEventGetIsFirstEvent() {
        EventFunction eventFunc = new EventFunction();
        AviatorObject result = eventFunc.call(env, new AviatorString("is_first_event"));
        assertEquals(false, result.getValue(env));
    }

    @Test
    void testEventGetIsCurrent() {
        EventFunction eventFunc = new EventFunction();
        AviatorObject result = eventFunc.call(env, new AviatorString("is_current"));
        assertEquals(true, result.getValue(env));
    }

    @Test
    void testEventGetIntegration() {
        EventFunction eventFunc = new EventFunction();
        AviatorObject result = eventFunc.call(env, new AviatorString("integration"));
        assertEquals("web", result.getValue(env));
    }

    @Test
    void testEventGetApp() {
        EventFunction eventFunc = new EventFunction();
        AviatorObject result = eventFunc.call(env, new AviatorString("app"));
        assertEquals("myapp", result.getValue(env));
    }

    @Test
    void testEventGetPlatform() {
        EventFunction eventFunc = new EventFunction();
        AviatorObject result = eventFunc.call(env, new AviatorString("platform"));
        assertEquals("web", result.getValue(env));
    }

    @Test
    void testEventGetIsHttps() {
        EventFunction eventFunc = new EventFunction();
        AviatorObject result = eventFunc.call(env, new AviatorString("is_https"));
        assertEquals(true, result.getValue(env));
    }

    @Test
    void testEventGetTriggerable() {
        EventFunction eventFunc = new EventFunction();
        AviatorObject result = eventFunc.call(env, new AviatorString("triggerable"));
        assertEquals(true, result.getValue(env));
    }

    @Test
    void testEventNonExistentField() {
        EventFunction eventFunc = new EventFunction();
        AviatorObject result = eventFunc.call(env, new AviatorString("nonexistent"));
        assertNull(result.getValue(env));
    }

    @Test
    void testEventNullFieldName() {
        EventFunction eventFunc = new EventFunction();
        AviatorObject result = eventFunc.call(env, createAviatorObject(null));
        assertNull(result.getValue(env));
    }

    @Test
    void testEventEmptyFieldName() {
        EventFunction eventFunc = new EventFunction();
        AviatorObject result = eventFunc.call(env, new AviatorString(""));
        assertNull(result.getValue(env));
    }

    @Test
    void testEventNoCurrentEvent() {
        EventFunction eventFunc = new EventFunction();
        Map<String, Object> emptyEnv = new HashMap<>();
        AviatorObject result = eventFunc.call(emptyEnv, new AviatorString("event_name"));
        assertNull(result.getValue(emptyEnv));
    }

    @Test
    void testEventWithWrongArgumentCount() {
        EventFunction eventFunc = new EventFunction();
        assertThrows(IllegalArgumentException.class, () -> {
            eventFunc.call(env, new AviatorString("event_name"), new AviatorString("event_type"));
        });
    }

    @Test
    void testEventWithNoArguments() {
        EventFunction eventFunc = new EventFunction();
        assertThrows(FunctionArgumentException.class, () -> {
            eventFunc.call(env, new AviatorObject[0]);
        });
    }

    @Test
    void testEventMetadata() {
        EventFunction eventFunc = new EventFunction();
        assertEquals("EVENT", eventFunc.getName());
        assertEquals(1, eventFunc.getFunctionMetadata().getMinArgs());
        assertEquals(1, eventFunc.getFunctionMetadata().getMaxArgs());
        assertEquals("Returns the value of a field from the current event", 
            eventFunc.getFunctionMetadata().getDescription());
    }

    @Test
    void testEventWithNullValue() {
        // Test with an event field that is null
        Event eventWithNull = Event.builder()
            .eventName("test")
            .duration(null)  // Null duration
            .build();
        
        Map<String, Object> envWithNull = new HashMap<>();
        envWithNull.put("currentEvent", eventWithNull);
        
        EventFunction eventFunc = new EventFunction();
        AviatorObject result = eventFunc.call(envWithNull, new AviatorString("duration"));
        assertNull(result.getValue(envWithNull));
    }

    // ========== PARAM Function Tests ==========

    @Test
    void testParamGetAmount() {
        ParamFunction paramFunc = new ParamFunction();
        AviatorObject result = paramFunc.call(env, new AviatorString("amount"));
        assertEquals(99.99, result.getValue(env));
    }

    @Test
    void testParamGetProductId() {
        ParamFunction paramFunc = new ParamFunction();
        AviatorObject result = paramFunc.call(env, new AviatorString("product_id"));
        assertEquals("SKU-12345", result.getValue(env));
    }

    @Test
    void testParamGetUtmSource() {
        ParamFunction paramFunc = new ParamFunction();
        AviatorObject result = paramFunc.call(env, new AviatorString("utm_source"));
        assertEquals("google", result.getValue(env));
    }

    @Test
    void testParamGetUtmCampaign() {
        ParamFunction paramFunc = new ParamFunction();
        AviatorObject result = paramFunc.call(env, new AviatorString("utm_campaign"));
        assertEquals("summer_sale", result.getValue(env));
    }

    @Test
    void testParamNonExistentParameter() {
        ParamFunction paramFunc = new ParamFunction();
        AviatorObject result = paramFunc.call(env, new AviatorString("nonexistent"));
        assertNull(result.getValue(env));
    }

    @Test
    void testParamNullParameterName() {
        ParamFunction paramFunc = new ParamFunction();
        AviatorObject result = paramFunc.call(env, createAviatorObject(null));
        assertNull(result.getValue(env));
    }

    @Test
    void testParamEmptyParameterName() {
        ParamFunction paramFunc = new ParamFunction();
        AviatorObject result = paramFunc.call(env, new AviatorString(""));
        assertNull(result.getValue(env));
    }

    @Test
    void testParamNoCurrentEvent() {
        ParamFunction paramFunc = new ParamFunction();
        Map<String, Object> emptyEnv = new HashMap<>();
        AviatorObject result = paramFunc.call(emptyEnv, new AviatorString("amount"));
        assertNull(result.getValue(emptyEnv));
    }

    @Test
    void testParamNullParameters() {
        ParamFunction paramFunc = new ParamFunction();
        Event eventWithoutParams = Event.builder()
            .eventName("test")
            .build();
        eventWithoutParams.setParameters(null);
        
        Map<String, Object> envWithoutParams = new HashMap<>();
        envWithoutParams.put("currentEvent", eventWithoutParams);
        
        AviatorObject result = paramFunc.call(envWithoutParams, new AviatorString("amount"));
        assertNull(result.getValue(envWithoutParams));
    }

    @Test
    void testParamWithNestedMap() {
        // Test dot notation for nested parameters
        Map<String, Object> nestedParams = new HashMap<>();
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", "user-789");
        userInfo.put("name", "John Doe");
        nestedParams.put("user", userInfo);
        
        Event eventWithNested = Event.builder()
            .eventName("test")
            .parameters(nestedParams)
            .build();
        
        Map<String, Object> envWithNested = new HashMap<>();
        envWithNested.put("currentEvent", eventWithNested);
        
        ParamFunction paramFunc = new ParamFunction();
        
        // Test nested access with dot notation
        AviatorObject result = paramFunc.call(envWithNested, new AviatorString("user.id"));
        assertEquals("user-789", result.getValue(envWithNested));
        
        AviatorObject result2 = paramFunc.call(envWithNested, new AviatorString("user.name"));
        assertEquals("John Doe", result2.getValue(envWithNested));
    }

    @Test
    void testParamWithNestedNonMap() {
        // Test dot notation when nested value is not a map
        Map<String, Object> params = new HashMap<>();
        params.put("simple", "value");
        
        Event eventWithSimple = Event.builder()
            .eventName("test")
            .parameters(params)
            .build();
        
        Map<String, Object> envWithSimple = new HashMap<>();
        envWithSimple.put("currentEvent", eventWithSimple);
        
        ParamFunction paramFunc = new ParamFunction();
        
        // Try to access nested field on non-map value - should return the value itself since it's not a map
        AviatorObject result = paramFunc.call(envWithSimple, new AviatorString("simple.nested"));
        // When the value is not a map, we can't access nested properties, so it returns null
        assertNull(result.getValue(envWithSimple));
    }

    @Test
    void testParamWithWrongArgumentCount() {
        ParamFunction paramFunc = new ParamFunction();
        assertThrows(IllegalArgumentException.class, () -> {
            paramFunc.call(env, new AviatorString("amount"), new AviatorString("product_id"));
        });
    }

    @Test
    void testParamWithNoArguments() {
        ParamFunction paramFunc = new ParamFunction();
        assertThrows(FunctionArgumentException.class, () -> {
            paramFunc.call(env, new AviatorObject[0]);
        });
    }

    @Test
    void testParamMetadata() {
        ParamFunction paramFunc = new ParamFunction();
        assertEquals("PARAM", paramFunc.getName());
        assertEquals(1, paramFunc.getFunctionMetadata().getMinArgs());
        assertEquals(1, paramFunc.getFunctionMetadata().getMaxArgs());
        assertEquals("Returns the value of a parameter from the current event", 
            paramFunc.getFunctionMetadata().getDescription());
    }

    @Test
    void testParamWithNullValue() {
        // Test with a parameter that has null value
        Map<String, Object> paramsWithNull = new HashMap<>();
        paramsWithNull.put("key", null);
        
        Event eventWithNullParam = Event.builder()
            .eventName("test")
            .parameters(paramsWithNull)
            .build();
        
        Map<String, Object> envWithNullParam = new HashMap<>();
        envWithNullParam.put("currentEvent", eventWithNullParam);
        
        ParamFunction paramFunc = new ParamFunction();
        AviatorObject result = paramFunc.call(envWithNullParam, new AviatorString("key"));
        assertNull(result.getValue(envWithNullParam));
    }

    @Test
    void testParamWithNumericValue() {
        ParamFunction paramFunc = new ParamFunction();
        AviatorObject result = paramFunc.call(env, new AviatorString("amount"));
        assertTrue(result.getValue(env) instanceof Number);
        assertEquals(99.99, ((Number) result.getValue(env)).doubleValue(), 0.001);
    }

    @Test
    void testParamWithStringValue() {
        ParamFunction paramFunc = new ParamFunction();
        AviatorObject result = paramFunc.call(env, new AviatorString("product_id"));
        assertTrue(result.getValue(env) instanceof String);
        assertEquals("SKU-12345", result.getValue(env));
    }

    // ========== Integration Tests ==========

    @Test
    void testAllThreeFunctionsTogether() {
        // Test that all three functions can work together in the same environment
        ProfileFunction profileFunc = new ProfileFunction();
        EventFunction eventFunc = new EventFunction();
        ParamFunction paramFunc = new ParamFunction();
        
        AviatorObject profileResult = profileFunc.call(env, new AviatorString("country"));
        AviatorObject eventResult = eventFunc.call(env, new AviatorString("event_name"));
        AviatorObject paramResult = paramFunc.call(env, new AviatorString("amount"));
        
        assertEquals("US", profileResult.getValue(env));
        assertEquals("purchase", eventResult.getValue(env));
        assertEquals(99.99, paramResult.getValue(env));
    }

    @Test
    void testDataAccessWithCompleteUserData() {
        // Test with a complete, realistic user data structure
        ProfileFunction profileFunc = new ProfileFunction();
        EventFunction eventFunc = new EventFunction();
        ParamFunction paramFunc = new ParamFunction();
        
        // Verify profile access
        assertNotNull(profileFunc.call(env, new AviatorString("country")).getValue(env));
        assertNotNull(profileFunc.call(env, new AviatorString("city")).getValue(env));
        
        // Verify event access
        assertNotNull(eventFunc.call(env, new AviatorString("event_name")).getValue(env));
        assertNotNull(eventFunc.call(env, new AviatorString("timestamp")).getValue(env));
        
        // Verify param access
        assertNotNull(paramFunc.call(env, new AviatorString("amount")).getValue(env));
        assertNotNull(paramFunc.call(env, new AviatorString("utm_source")).getValue(env));
    }

    // ========== VISIT Function Tests ==========

    @Test
    void testVisitGetUuid() {
        com.filter.dsl.functions.data.VisitFunction visitFunc = new com.filter.dsl.functions.data.VisitFunction();
        com.filter.dsl.models.Visit visit = com.filter.dsl.models.Visit.builder()
            .uuid("visit-123")
            .timestamp("2024-01-15T10:00:00Z")
            .landingPage("/home")
            .build();
        env.put("currentVisit", visit);
        
        AviatorObject result = visitFunc.call(env, new AviatorString("uuid"));
        assertEquals("visit-123", result.getValue(env));
    }

    @Test
    void testVisitGetTimestamp() {
        com.filter.dsl.functions.data.VisitFunction visitFunc = new com.filter.dsl.functions.data.VisitFunction();
        com.filter.dsl.models.Visit visit = com.filter.dsl.models.Visit.builder()
            .uuid("visit-123")
            .timestamp("2024-01-15T10:00:00Z")
            .build();
        env.put("currentVisit", visit);
        
        AviatorObject result = visitFunc.call(env, new AviatorString("timestamp"));
        assertEquals("2024-01-15T10:00:00Z", result.getValue(env));
    }

    @Test
    void testVisitGetLandingPage() {
        com.filter.dsl.functions.data.VisitFunction visitFunc = new com.filter.dsl.functions.data.VisitFunction();
        com.filter.dsl.models.Visit visit = com.filter.dsl.models.Visit.builder()
            .uuid("visit-123")
            .landingPage("/home")
            .build();
        env.put("currentVisit", visit);
        
        AviatorObject result = visitFunc.call(env, new AviatorString("landing_page"));
        assertEquals("/home", result.getValue(env));
    }

    @Test
    void testVisitGetReferrerType() {
        com.filter.dsl.functions.data.VisitFunction visitFunc = new com.filter.dsl.functions.data.VisitFunction();
        com.filter.dsl.models.Visit visit = com.filter.dsl.models.Visit.builder()
            .uuid("visit-123")
            .referrerType("search")
            .build();
        env.put("currentVisit", visit);
        
        AviatorObject result = visitFunc.call(env, new AviatorString("referrer_type"));
        assertEquals("search", result.getValue(env));
    }

    @Test
    void testVisitGetReferrerUrl() {
        com.filter.dsl.functions.data.VisitFunction visitFunc = new com.filter.dsl.functions.data.VisitFunction();
        com.filter.dsl.models.Visit visit = com.filter.dsl.models.Visit.builder()
            .uuid("visit-123")
            .referrerUrl("https://google.com")
            .build();
        env.put("currentVisit", visit);
        
        AviatorObject result = visitFunc.call(env, new AviatorString("referrer_url"));
        assertEquals("https://google.com", result.getValue(env));
    }

    @Test
    void testVisitGetDuration() {
        com.filter.dsl.functions.data.VisitFunction visitFunc = new com.filter.dsl.functions.data.VisitFunction();
        com.filter.dsl.models.Visit visit = com.filter.dsl.models.Visit.builder()
            .uuid("visit-123")
            .duration(300)
            .build();
        env.put("currentVisit", visit);
        
        AviatorObject result = visitFunc.call(env, new AviatorString("duration"));
        assertEquals(300, result.getValue(env));
    }

    @Test
    void testVisitGetActions() {
        com.filter.dsl.functions.data.VisitFunction visitFunc = new com.filter.dsl.functions.data.VisitFunction();
        com.filter.dsl.models.Visit visit = com.filter.dsl.models.Visit.builder()
            .uuid("visit-123")
            .actions(5)
            .build();
        env.put("currentVisit", visit);
        
        AviatorObject result = visitFunc.call(env, new AviatorString("actions"));
        assertEquals(5, result.getValue(env));
    }

    @Test
    void testVisitGetIsFirstVisit() {
        com.filter.dsl.functions.data.VisitFunction visitFunc = new com.filter.dsl.functions.data.VisitFunction();
        com.filter.dsl.models.Visit visit = com.filter.dsl.models.Visit.builder()
            .uuid("visit-123")
            .isFirstVisit(true)
            .build();
        env.put("currentVisit", visit);
        
        AviatorObject result = visitFunc.call(env, new AviatorString("is_first_visit"));
        assertEquals(true, result.getValue(env));
    }

    @Test
    void testVisitGetOs() {
        com.filter.dsl.functions.data.VisitFunction visitFunc = new com.filter.dsl.functions.data.VisitFunction();
        com.filter.dsl.models.Visit visit = com.filter.dsl.models.Visit.builder()
            .uuid("visit-123")
            .os("Windows")
            .build();
        env.put("currentVisit", visit);
        
        AviatorObject result = visitFunc.call(env, new AviatorString("os"));
        assertEquals("Windows", result.getValue(env));
    }

    @Test
    void testVisitGetBrowser() {
        com.filter.dsl.functions.data.VisitFunction visitFunc = new com.filter.dsl.functions.data.VisitFunction();
        com.filter.dsl.models.Visit visit = com.filter.dsl.models.Visit.builder()
            .uuid("visit-123")
            .browser("Chrome")
            .build();
        env.put("currentVisit", visit);
        
        AviatorObject result = visitFunc.call(env, new AviatorString("browser"));
        assertEquals("Chrome", result.getValue(env));
    }

    @Test
    void testVisitGetDevice() {
        com.filter.dsl.functions.data.VisitFunction visitFunc = new com.filter.dsl.functions.data.VisitFunction();
        com.filter.dsl.models.Visit visit = com.filter.dsl.models.Visit.builder()
            .uuid("visit-123")
            .device("Desktop")
            .build();
        env.put("currentVisit", visit);
        
        AviatorObject result = visitFunc.call(env, new AviatorString("device"));
        assertEquals("Desktop", result.getValue(env));
    }

    @Test
    void testVisitGetScreen() {
        com.filter.dsl.functions.data.VisitFunction visitFunc = new com.filter.dsl.functions.data.VisitFunction();
        com.filter.dsl.models.Visit visit = com.filter.dsl.models.Visit.builder()
            .uuid("visit-123")
            .screen("1920x1080")
            .build();
        env.put("currentVisit", visit);
        
        AviatorObject result = visitFunc.call(env, new AviatorString("screen"));
        assertEquals("1920x1080", result.getValue(env));
    }

    @Test
    void testVisitNonExistentField() {
        com.filter.dsl.functions.data.VisitFunction visitFunc = new com.filter.dsl.functions.data.VisitFunction();
        com.filter.dsl.models.Visit visit = com.filter.dsl.models.Visit.builder()
            .uuid("visit-123")
            .build();
        env.put("currentVisit", visit);
        
        AviatorObject result = visitFunc.call(env, new AviatorString("nonexistent"));
        assertNull(result.getValue(env));
    }

    @Test
    void testVisitNullFieldName() {
        com.filter.dsl.functions.data.VisitFunction visitFunc = new com.filter.dsl.functions.data.VisitFunction();
        com.filter.dsl.models.Visit visit = com.filter.dsl.models.Visit.builder()
            .uuid("visit-123")
            .build();
        env.put("currentVisit", visit);
        
        AviatorObject result = visitFunc.call(env, createAviatorObject(null));
        assertNull(result.getValue(env));
    }

    @Test
    void testVisitNoCurrentVisit() {
        com.filter.dsl.functions.data.VisitFunction visitFunc = new com.filter.dsl.functions.data.VisitFunction();
        Map<String, Object> emptyEnv = new HashMap<>();
        
        AviatorObject result = visitFunc.call(emptyEnv, new AviatorString("os"));
        assertNull(result.getValue(emptyEnv));
    }

    @Test
    void testVisitFromUserData() {
        com.filter.dsl.functions.data.VisitFunction visitFunc = new com.filter.dsl.functions.data.VisitFunction();
        
        com.filter.dsl.models.Visit visit = com.filter.dsl.models.Visit.builder()
            .uuid("visit-123")
            .os("macOS")
            .browser("Safari")
            .device("Mobile")
            .build();
        
        Map<String, com.filter.dsl.models.Visit> visits = new HashMap<>();
        visits.put("visit-123", visit);
        
        UserData userData = UserData.builder()
            .profile(profile)
            .visits(visits)
            .build();
        
        Map<String, Object> testEnv = new HashMap<>();
        testEnv.put("userData", userData);
        
        AviatorObject result = visitFunc.call(testEnv, new AviatorString("os"));
        assertEquals("macOS", result.getValue(testEnv));
    }

    @Test
    void testVisitWithWrongArgumentCount() {
        com.filter.dsl.functions.data.VisitFunction visitFunc = new com.filter.dsl.functions.data.VisitFunction();
        assertThrows(IllegalArgumentException.class, () -> {
            visitFunc.call(env, new AviatorString("os"), new AviatorString("browser"));
        });
    }

    @Test
    void testVisitWithNoArguments() {
        com.filter.dsl.functions.data.VisitFunction visitFunc = new com.filter.dsl.functions.data.VisitFunction();
        assertThrows(FunctionArgumentException.class, () -> {
            visitFunc.call(env, new AviatorObject[0]);
        });
    }

    @Test
    void testVisitMetadata() {
        com.filter.dsl.functions.data.VisitFunction visitFunc = new com.filter.dsl.functions.data.VisitFunction();
        assertEquals("VISIT", visitFunc.getName());
        assertEquals(1, visitFunc.getFunctionMetadata().getMinArgs());
        assertEquals(1, visitFunc.getFunctionMetadata().getMaxArgs());
        assertTrue(visitFunc.getFunctionMetadata().getDescription().contains("visit"));
    }

    @Test
    void testVisitDeviceAttributesInSession() {
        // Test that device attributes are correctly accessed from visit (session-level)
        com.filter.dsl.functions.data.VisitFunction visitFunc = new com.filter.dsl.functions.data.VisitFunction();
        
        com.filter.dsl.models.Visit visit = com.filter.dsl.models.Visit.builder()
            .uuid("visit-123")
            .os("iOS")
            .browser("Safari")
            .device("Mobile")
            .screen("375x667")
            .build();
        
        env.put("currentVisit", visit);
        
        assertEquals("iOS", visitFunc.call(env, new AviatorString("os")).getValue(env));
        assertEquals("Safari", visitFunc.call(env, new AviatorString("browser")).getValue(env));
        assertEquals("Mobile", visitFunc.call(env, new AviatorString("device")).getValue(env));
        assertEquals("375x667", visitFunc.call(env, new AviatorString("screen")).getValue(env));
    }
}


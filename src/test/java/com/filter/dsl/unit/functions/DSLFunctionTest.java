package com.filter.dsl.unit.functions;

import com.filter.dsl.functions.*;
import com.filter.dsl.models.Event;
import com.filter.dsl.models.Visit;
import com.filter.dsl.models.TimeRange;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorLong;
import com.googlecode.aviator.runtime.type.AviatorString;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorDouble;
import com.googlecode.aviator.runtime.type.AviatorJavaType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DSLFunction base class and its helper methods.
 */
class DSLFunctionTest {

    private Map<String, Object> env;
    private TestFunction testFunction;

    @BeforeEach
    void setUp() {
        env = new HashMap<>();
        testFunction = new TestFunction();
    }

    // ========== Argument Validation Tests ==========

    @Test
    void testValidateArgCount_Exact() {
        AviatorObject[] args = new AviatorObject[]{AviatorLong.valueOf(1), AviatorLong.valueOf(2)};
        
        // Should not throw
        assertDoesNotThrow(() -> testFunction.validateArgCount(args, 2));
    }

    @Test
    void testValidateArgCount_TooFew() {
        AviatorObject[] args = new AviatorObject[]{AviatorLong.valueOf(1)};
        
        FunctionArgumentException ex = assertThrows(
            FunctionArgumentException.class,
            () -> testFunction.validateArgCount(args, 2)
        );
        assertTrue(ex.getMessage().contains("expects 2 argument(s), got 1"));
    }

    @Test
    void testValidateArgCount_TooMany() {
        AviatorObject[] args = new AviatorObject[]{
            AviatorLong.valueOf(1), 
            AviatorLong.valueOf(2), 
            AviatorLong.valueOf(3)
        };
        
        FunctionArgumentException ex = assertThrows(
            FunctionArgumentException.class,
            () -> testFunction.validateArgCount(args, 2)
        );
        assertTrue(ex.getMessage().contains("expects 2 argument(s), got 3"));
    }

    @Test
    void testValidateArgCountRange_WithinRange() {
        AviatorObject[] args = new AviatorObject[]{AviatorLong.valueOf(1), AviatorLong.valueOf(2)};
        
        // Should not throw
        assertDoesNotThrow(() -> testFunction.validateArgCountRange(args, 1, 3));
    }

    @Test
    void testValidateArgCountRange_AtMinimum() {
        AviatorObject[] args = new AviatorObject[]{AviatorLong.valueOf(1)};
        
        // Should not throw
        assertDoesNotThrow(() -> testFunction.validateArgCountRange(args, 1, 3));
    }

    @Test
    void testValidateArgCountRange_AtMaximum() {
        AviatorObject[] args = new AviatorObject[]{
            AviatorLong.valueOf(1), 
            AviatorLong.valueOf(2), 
            AviatorLong.valueOf(3)
        };
        
        // Should not throw
        assertDoesNotThrow(() -> testFunction.validateArgCountRange(args, 1, 3));
    }

    @Test
    void testValidateArgCountRange_BelowMinimum() {
        AviatorObject[] args = new AviatorObject[]{};
        
        FunctionArgumentException ex = assertThrows(
            FunctionArgumentException.class,
            () -> testFunction.validateArgCountRange(args, 1, 3)
        );
        assertTrue(ex.getMessage().contains("expects 1-3 argument(s), got 0"));
    }

    @Test
    void testValidateArgCountRange_AboveMaximum() {
        AviatorObject[] args = new AviatorObject[]{
            AviatorLong.valueOf(1), 
            AviatorLong.valueOf(2), 
            AviatorLong.valueOf(3),
            AviatorLong.valueOf(4)
        };
        
        FunctionArgumentException ex = assertThrows(
            FunctionArgumentException.class,
            () -> testFunction.validateArgCountRange(args, 1, 3)
        );
        assertTrue(ex.getMessage().contains("expects 1-3 argument(s), got 4"));
    }

    @Test
    void testValidateMinArgCount_Sufficient() {
        AviatorObject[] args = new AviatorObject[]{
            AviatorLong.valueOf(1), 
            AviatorLong.valueOf(2)
        };
        
        // Should not throw
        assertDoesNotThrow(() -> testFunction.validateMinArgCount(args, 2));
    }

    @Test
    void testValidateMinArgCount_MoreThanMinimum() {
        AviatorObject[] args = new AviatorObject[]{
            AviatorLong.valueOf(1), 
            AviatorLong.valueOf(2),
            AviatorLong.valueOf(3)
        };
        
        // Should not throw
        assertDoesNotThrow(() -> testFunction.validateMinArgCount(args, 2));
    }

    @Test
    void testValidateMinArgCount_Insufficient() {
        AviatorObject[] args = new AviatorObject[]{AviatorLong.valueOf(1)};
        
        FunctionArgumentException ex = assertThrows(
            FunctionArgumentException.class,
            () -> testFunction.validateMinArgCount(args, 2)
        );
        assertTrue(ex.getMessage().contains("expects at least 2 argument(s), got 1"));
    }

    // ========== Context Access Tests ==========

    @Test
    void testGetUserData() {
        Object userData = new Object();
        env.put("userData", userData);
        
        assertEquals(userData, testFunction.getUserData(env));
    }

    @Test
    void testGetUserData_NotPresent() {
        assertNull(testFunction.getUserData(env));
    }

    @Test
    void testGetCurrentEvent() {
        Event event = new Event();
        event.setEventName("test_event");
        env.put("currentEvent", event);
        
        Event result = testFunction.getCurrentEvent(env);
        assertNotNull(result);
        assertEquals("test_event", result.getEventName());
    }

    @Test
    void testGetCurrentEvent_NotPresent() {
        assertNull(testFunction.getCurrentEvent(env));
    }

    @Test
    void testGetCurrentVisit() {
        Visit visit = new Visit();
        visit.setUuid("visit-123");
        env.put("currentVisit", visit);
        
        Visit result = testFunction.getCurrentVisit(env);
        assertNotNull(result);
        assertEquals("visit-123", result.getUuid());
    }

    @Test
    void testGetCurrentVisit_NotPresent() {
        assertNull(testFunction.getCurrentVisit(env));
    }

    @Test
    void testGetNow_Present() {
        Instant now = Instant.parse("2024-01-15T10:30:00Z");
        env.put("now", now);
        
        assertEquals(now, testFunction.getNow(env));
    }

    @Test
    void testGetNow_NotPresent() {
        Instant before = Instant.now();
        Instant result = testFunction.getNow(env);
        Instant after = Instant.now();
        
        // Result should be between before and after
        assertNotNull(result);
        assertFalse(result.isBefore(before));
        assertFalse(result.isAfter(after));
    }

    @Test
    void testGetTimeRange() {
        TimeRange timeRange = new TimeRange(30, com.filter.dsl.models.TimeUnit.D, 
                                            0, com.filter.dsl.models.TimeUnit.D, 
                                            Instant.now());
        env.put("timeRange", timeRange);
        
        assertEquals(timeRange, testFunction.getTimeRange(env));
    }

    @Test
    void testGetTimeRange_NotPresent() {
        assertNull(testFunction.getTimeRange(env));
    }

    // ========== Type Conversion Tests ==========

    @Test
    void testToNumber_Integer() {
        AviatorObject obj = AviatorLong.valueOf(42);
        Number result = testFunction.toNumber(obj, env);
        
        assertEquals(42L, result.longValue());
    }

    @Test
    void testToNumber_Double() {
        AviatorObject obj = AviatorDouble.valueOf(3.14);
        Number result = testFunction.toNumber(obj, env);
        
        assertEquals(3.14, result.doubleValue(), 0.001);
    }

    @Test
    void testToNumber_NotANumber() {
        AviatorObject obj = new AviatorString("not a number");
        
        TypeMismatchException ex = assertThrows(
            TypeMismatchException.class,
            () -> testFunction.toNumber(obj, env)
        );
        assertTrue(ex.getMessage().contains("Expected number"));
    }

    @Test
    void testToNumber_Null() {
        AviatorObject obj = new AviatorJavaType("nullValue") {
            @Override
            public Object getValue(Map<String, Object> environment) {
                return null;
            }
        };
        
        TypeMismatchException ex = assertThrows(
            TypeMismatchException.class,
            () -> testFunction.toNumber(obj, env)
        );
        assertTrue(ex.getMessage().contains("Expected number, got null"));
    }

    @Test
    void testToString_String() {
        AviatorObject obj = new AviatorString("hello");
        String result = testFunction.toString(obj, env);
        
        assertEquals("hello", result);
    }

    @Test
    void testToString_Number() {
        AviatorObject obj = AviatorLong.valueOf(42);
        String result = testFunction.toString(obj, env);
        
        assertEquals("42", result);
    }

    @Test
    void testToString_Null() {
        AviatorObject obj = new AviatorJavaType("nullValue") {
            @Override
            public Object getValue(Map<String, Object> environment) {
                return null;
            }
        };
        String result = testFunction.toString(obj, env);
        
        assertNull(result);
    }

    @Test
    void testToBoolean_True() {
        AviatorObject obj = AviatorBoolean.TRUE;
        Boolean result = testFunction.toBoolean(obj, env);
        
        assertTrue(result);
    }

    @Test
    void testToBoolean_False() {
        AviatorObject obj = AviatorBoolean.FALSE;
        Boolean result = testFunction.toBoolean(obj, env);
        
        assertFalse(result);
    }

    @Test
    void testToBoolean_NotABoolean() {
        AviatorObject obj = AviatorLong.valueOf(1);
        
        TypeMismatchException ex = assertThrows(
            TypeMismatchException.class,
            () -> testFunction.toBoolean(obj, env)
        );
        assertTrue(ex.getMessage().contains("Expected boolean"));
    }

    @Test
    void testToCollection_List() {
        List<String> list = Arrays.asList("a", "b", "c");
        AviatorObject obj = new AviatorJavaType("dummy");  // Wrapper
        env.put("dummy", list);  // Put actual list in env
        
        // Override getValue to return the list
        AviatorObject listObj = new AviatorJavaType("testList") {
            @Override
            public Object getValue(Map<String, Object> environment) {
                return list;
            }
        };
        
        Collection<?> result = testFunction.toCollection(listObj, env);
        
        assertEquals(3, result.size());
        assertTrue(result.contains("a"));
    }

    @Test
    void testToCollection_Set() {
        Set<Integer> set = new HashSet<>(Arrays.asList(1, 2, 3));
        
        AviatorObject setObj = new AviatorJavaType("testSet") {
            @Override
            public Object getValue(Map<String, Object> environment) {
                return set;
            }
        };
        
        Collection<?> result = testFunction.toCollection(setObj, env);
        
        assertEquals(3, result.size());
        assertTrue(result.contains(1));
    }

    @Test
    void testToCollection_NotACollection() {
        AviatorObject obj = new AviatorString("not a collection");
        
        TypeMismatchException ex = assertThrows(
            TypeMismatchException.class,
            () -> testFunction.toCollection(obj, env)
        );
        assertTrue(ex.getMessage().contains("Expected collection"));
    }

    @Test
    void testGetValue_NonNull() {
        AviatorObject obj = AviatorLong.valueOf(42);
        Object result = testFunction.getValue(obj, env);
        
        assertEquals(42L, result);
    }

    @Test
    void testGetValue_Null() {
        Object result = testFunction.getValue(null, env);
        
        assertNull(result);
    }

    // ========== Test Function Implementation ==========

    /**
     * Concrete implementation of DSLFunction for testing purposes.
     */
    private static class TestFunction extends DSLFunction {
        
        @Override
        public String getName() {
            return "TEST";
        }

        @Override
        public FunctionMetadata getFunctionMetadata() {
            return FunctionMetadata.builder()
                .name("TEST")
                .minArgs(0)
                .maxArgs(10)
                .returnType(FunctionMetadata.ReturnType.ANY)
                .description("Test function for unit testing")
                .build();
        }

        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
            return AviatorLong.valueOf(42);
        }

        // Expose protected methods for testing
        @Override
        public void validateArgCount(AviatorObject[] args, int expected) {
            super.validateArgCount(args, expected);
        }

        @Override
        public void validateArgCountRange(AviatorObject[] args, int min, int max) {
            super.validateArgCountRange(args, min, max);
        }

        @Override
        public void validateMinArgCount(AviatorObject[] args, int min) {
            super.validateMinArgCount(args, min);
        }

        @Override
        public Object getUserData(Map<String, Object> env) {
            return super.getUserData(env);
        }

        @Override
        public Event getCurrentEvent(Map<String, Object> env) {
            return super.getCurrentEvent(env);
        }

        @Override
        public Visit getCurrentVisit(Map<String, Object> env) {
            return super.getCurrentVisit(env);
        }

        @Override
        public Instant getNow(Map<String, Object> env) {
            return super.getNow(env);
        }

        @Override
        public TimeRange getTimeRange(Map<String, Object> env) {
            return super.getTimeRange(env);
        }

        @Override
        public Number toNumber(AviatorObject obj, Map<String, Object> env) {
            return super.toNumber(obj, env);
        }

        @Override
        public String toString(AviatorObject obj, Map<String, Object> env) {
            return super.toString(obj, env);
        }

        @Override
        public Boolean toBoolean(AviatorObject obj, Map<String, Object> env) {
            return super.toBoolean(obj, env);
        }

        @Override
        public Collection<?> toCollection(AviatorObject obj, Map<String, Object> env) {
            return super.toCollection(obj, env);
        }

        @Override
        public Object getValue(AviatorObject obj, Map<String, Object> env) {
            return super.getValue(obj, env);
        }
    }
}

package com.filter.dsl.unit.functions;

import com.filter.dsl.functions.FunctionArgumentException;
import com.filter.dsl.functions.TypeMismatchException;
import com.filter.dsl.functions.aggregation.CountFunction;
import com.filter.dsl.functions.aggregation.SumFunction;
import com.filter.dsl.functions.aggregation.AvgFunction;
import com.filter.dsl.functions.aggregation.MinFunction;
import com.filter.dsl.functions.aggregation.MaxFunction;
import com.filter.dsl.functions.aggregation.UniqueFunction;
import com.googlecode.aviator.runtime.type.AviatorJavaType;
import com.googlecode.aviator.runtime.type.AviatorLong;
import com.googlecode.aviator.runtime.type.AviatorDouble;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for aggregation functions: COUNT, SUM, AVG, MIN, MAX, UNIQUE
 */
class AggregationFunctionsTest {

    private final Map<String, Object> env = new HashMap<>();

    // Helper method to create AviatorObject from a value
    private AviatorObject createAviatorObject(Object value) {
        return new AviatorJavaType("value") {
            @Override
            public Object getValue(Map<String, Object> environment) {
                return value;
            }
        };
    }

    // ========== COUNT Function Tests ==========

    @Test
    void testCountEmptyCollection() {
        CountFunction count = new CountFunction();
        List<Integer> emptyList = new ArrayList<>();
        AviatorObject result = count.call(env, createAviatorObject(emptyList));
        assertEquals(0L, result.getValue(env));
    }

    @Test
    void testCountNonEmptyCollection() {
        CountFunction count = new CountFunction();
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        AviatorObject result = count.call(env, createAviatorObject(list));
        assertEquals(5L, result.getValue(env));
    }

    @Test
    void testCountSingleElement() {
        CountFunction count = new CountFunction();
        List<String> list = Collections.singletonList("item");
        AviatorObject result = count.call(env, createAviatorObject(list));
        assertEquals(1L, result.getValue(env));
    }

    @Test
    void testCountWithDuplicates() {
        CountFunction count = new CountFunction();
        List<Integer> list = Arrays.asList(1, 2, 2, 3, 3, 3);
        AviatorObject result = count.call(env, createAviatorObject(list));
        assertEquals(6L, result.getValue(env));
    }

    @Test
    void testCountWithNullElements() {
        CountFunction count = new CountFunction();
        List<String> list = Arrays.asList("a", null, "b", null, "c");
        AviatorObject result = count.call(env, createAviatorObject(list));
        assertEquals(5L, result.getValue(env));
    }

    @Test
    void testCountWithSet() {
        CountFunction count = new CountFunction();
        Set<String> set = new HashSet<>(Arrays.asList("a", "b", "c"));
        AviatorObject result = count.call(env, createAviatorObject(set));
        assertEquals(3L, result.getValue(env));
    }

    @Test
    void testCountWithQueue() {
        CountFunction count = new CountFunction();
        Queue<Integer> queue = new LinkedList<>(Arrays.asList(1, 2, 3, 4));
        AviatorObject result = count.call(env, createAviatorObject(queue));
        assertEquals(4L, result.getValue(env));
    }

    @Test
    void testCountNullCollection() {
        CountFunction count = new CountFunction();
        AviatorObject result = count.call(env, createAviatorObject(null));
        assertEquals(0L, result.getValue(env));
    }

    @Test
    void testCountWithArray() {
        CountFunction count = new CountFunction();
        Integer[] array = {1, 2, 3, 4, 5};
        AviatorObject result = count.call(env, createAviatorObject(array));
        assertEquals(5L, result.getValue(env));
    }

    @Test
    void testCountWithEmptyArray() {
        CountFunction count = new CountFunction();
        String[] array = {};
        AviatorObject result = count.call(env, createAviatorObject(array));
        assertEquals(0L, result.getValue(env));
    }

    @Test
    void testCountWithPrimitiveArray() {
        CountFunction count = new CountFunction();
        int[] array = {1, 2, 3};
        AviatorObject result = count.call(env, createAviatorObject(array));
        assertEquals(3L, result.getValue(env));
    }

    @Test
    void testCountWithLargeCollection() {
        CountFunction count = new CountFunction();
        List<Integer> largeList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            largeList.add(i);
        }
        AviatorObject result = count.call(env, createAviatorObject(largeList));
        assertEquals(1000L, result.getValue(env));
    }

    @Test
    void testCountWithWrongArgumentCount() {
        CountFunction count = new CountFunction();
        List<Integer> list = Arrays.asList(1, 2, 3);
        assertThrows(TypeMismatchException.class, () -> {
            count.call(env, createAviatorObject(list), createAviatorObject(list));
        });
    }

    @Test
    void testCountWithNoArguments() {
        // New behavior: COUNT() should work and return count of userData.events
        CountFunction count = new CountFunction();
        
        // Create test environment with userData
        Map<String, Object> testEnv = createTestEnvWithEvents(5);
        
        AviatorObject result = count.call(testEnv, new AviatorObject[0]);
        assertNotNull(result);
        assertEquals(5L, result.getValue(testEnv));
    }

    @Test
    void testCountWithNonCollectionNonStringArgument() {
        // String arguments are now valid (filter conditions)
        // Only non-collection, non-string arguments should throw
        CountFunction count = new CountFunction();
        assertThrows(TypeMismatchException.class, () -> {
            count.call(env, AviatorLong.valueOf(123));
        });
    }

    @Test
    void testCountWithNumberArgument() {
        CountFunction count = new CountFunction();
        assertThrows(TypeMismatchException.class, () -> {
            count.call(env, AviatorLong.valueOf(42));
        });
    }

    @Test
    void testCountMetadata() {
        CountFunction count = new CountFunction();
        assertEquals("COUNT", count.getName());
        assertEquals(0, count.getFunctionMetadata().getMinArgs());
        assertEquals(2, count.getFunctionMetadata().getMaxArgs());
        assertTrue(count.getFunctionMetadata().getDescription().contains("userData.events"));
    }

    @Test
    void testCountReturnsNonNegative() {
        // Property: COUNT should always return a non-negative integer
        CountFunction count = new CountFunction();
        
        // Test with various collections
        List<?>[] testCases = {
            new ArrayList<>(),
            Arrays.asList(1),
            Arrays.asList(1, 2, 3),
            Collections.emptyList()
        };
        
        for (List<?> testCase : testCases) {
            AviatorObject result = count.call(env, createAviatorObject(testCase));
            long value = (Long) result.getValue(env);
            assertTrue(value >= 0, "COUNT should return non-negative value, got: " + value);
        }
    }

    @Test
    void testCountConsistency() {
        // Property: COUNT should return the same value for the same collection
        CountFunction count = new CountFunction();
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        
        AviatorObject result1 = count.call(env, createAviatorObject(list));
        AviatorObject result2 = count.call(env, createAviatorObject(list));
        
        assertEquals(result1.getValue(env), result2.getValue(env));
    }

    @Test
    void testCountWithMixedTypes() {
        CountFunction count = new CountFunction();
        List<Object> mixedList = Arrays.asList(1, "string", 3.14, true, null);
        AviatorObject result = count.call(env, createAviatorObject(mixedList));
        assertEquals(5L, result.getValue(env));
    }

    @Test
    void testCountWithNestedCollections() {
        CountFunction count = new CountFunction();
        List<List<Integer>> nestedList = Arrays.asList(
            Arrays.asList(1, 2),
            Arrays.asList(3, 4, 5),
            Arrays.asList(6)
        );
        // COUNT should count the outer collection elements, not flatten
        AviatorObject result = count.call(env, createAviatorObject(nestedList));
        assertEquals(3L, result.getValue(env));
    }

    @Test
    void testCountEmptyVsNull() {
        // Property: COUNT of empty collection should be 0, COUNT of null should be 0
        CountFunction count = new CountFunction();
        
        AviatorObject emptyResult = count.call(env, createAviatorObject(new ArrayList<>()));
        AviatorObject nullResult = count.call(env, createAviatorObject(null));
        
        assertEquals(0L, emptyResult.getValue(env));
        assertEquals(0L, nullResult.getValue(env));
    }

    @Test
    void testCountWithUnmodifiableCollection() {
        CountFunction count = new CountFunction();
        List<Integer> unmodifiableList = Collections.unmodifiableList(Arrays.asList(1, 2, 3));
        AviatorObject result = count.call(env, createAviatorObject(unmodifiableList));
        assertEquals(3L, result.getValue(env));
    }

    @Test
    void testCountWithSynchronizedCollection() {
        CountFunction count = new CountFunction();
        List<Integer> syncList = Collections.synchronizedList(Arrays.asList(1, 2, 3, 4));
        AviatorObject result = count.call(env, createAviatorObject(syncList));
        assertEquals(4L, result.getValue(env));
    }

    @Test
    void testCountReturnType() {
        // Verify that COUNT returns a Long (AviatorLong)
        CountFunction count = new CountFunction();
        List<Integer> list = Arrays.asList(1, 2, 3);
        AviatorObject result = count.call(env, createAviatorObject(list));
        
        Object value = result.getValue(env);
        assertTrue(value instanceof Long, "COUNT should return Long, got: " + value.getClass().getSimpleName());
    }

    @Test
    void testCountWithCharArray() {
        CountFunction count = new CountFunction();
        char[] charArray = {'a', 'b', 'c', 'd'};
        AviatorObject result = count.call(env, createAviatorObject(charArray));
        assertEquals(4L, result.getValue(env));
    }

    @Test
    void testCountWithBooleanArray() {
        CountFunction count = new CountFunction();
        boolean[] boolArray = {true, false, true};
        AviatorObject result = count.call(env, createAviatorObject(boolArray));
        assertEquals(3L, result.getValue(env));
    }

    @Test
    void testCountWithDoubleArray() {
        CountFunction count = new CountFunction();
        double[] doubleArray = {1.1, 2.2, 3.3, 4.4, 5.5};
        AviatorObject result = count.call(env, createAviatorObject(doubleArray));
        assertEquals(5L, result.getValue(env));
    }

    // ========== SUM Function Tests ==========

    @Test
    void testSumEmptyCollection() {
        SumFunction sum = new SumFunction();
        List<Integer> emptyList = new ArrayList<>();
        AviatorObject result = sum.call(env, createAviatorObject(emptyList));
        assertEquals(0L, result.getValue(env));
    }

    @Test
    void testSumIntegers() {
        SumFunction sum = new SumFunction();
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        AviatorObject result = sum.call(env, createAviatorObject(list));
        assertEquals(15L, result.getValue(env));
    }

    @Test
    void testSumDoubles() {
        SumFunction sum = new SumFunction();
        List<Double> list = Arrays.asList(1.5, 2.5, 3.0);
        AviatorObject result = sum.call(env, createAviatorObject(list));
        assertEquals(7.0, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testSumMixedNumbers() {
        SumFunction sum = new SumFunction();
        List<Number> list = Arrays.asList(1, 2.5, 3, 4.5);
        AviatorObject result = sum.call(env, createAviatorObject(list));
        assertEquals(11.0, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testSumSingleElement() {
        SumFunction sum = new SumFunction();
        List<Integer> list = Collections.singletonList(42);
        AviatorObject result = sum.call(env, createAviatorObject(list));
        assertEquals(42L, result.getValue(env));
    }

    @Test
    void testSumWithNullElements() {
        SumFunction sum = new SumFunction();
        List<Integer> list = Arrays.asList(1, null, 2, null, 3);
        AviatorObject result = sum.call(env, createAviatorObject(list));
        assertEquals(6L, result.getValue(env));
    }

    @Test
    void testSumNullCollection() {
        SumFunction sum = new SumFunction();
        AviatorObject result = sum.call(env, createAviatorObject(null));
        assertEquals(0L, result.getValue(env));
    }

    @Test
    void testSumWithArray() {
        SumFunction sum = new SumFunction();
        Integer[] array = {1, 2, 3, 4, 5};
        AviatorObject result = sum.call(env, createAviatorObject(array));
        assertEquals(15L, result.getValue(env));
    }

    @Test
    void testSumWithEmptyArray() {
        SumFunction sum = new SumFunction();
        Integer[] array = {};
        AviatorObject result = sum.call(env, createAviatorObject(array));
        assertEquals(0L, result.getValue(env));
    }

    @Test
    void testSumWithPrimitiveArray() {
        SumFunction sum = new SumFunction();
        int[] array = {1, 2, 3, 4, 5};
        AviatorObject result = sum.call(env, createAviatorObject(array));
        assertEquals(15L, result.getValue(env));
    }

    @Test
    void testSumWithDoubleArray() {
        SumFunction sum = new SumFunction();
        double[] array = {1.1, 2.2, 3.3};
        AviatorObject result = sum.call(env, createAviatorObject(array));
        assertEquals(6.6, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testSumNegativeNumbers() {
        SumFunction sum = new SumFunction();
        List<Integer> list = Arrays.asList(-1, -2, -3, -4, -5);
        AviatorObject result = sum.call(env, createAviatorObject(list));
        assertEquals(-15L, result.getValue(env));
    }

    @Test
    void testSumMixedPositiveNegative() {
        SumFunction sum = new SumFunction();
        List<Integer> list = Arrays.asList(10, -5, 3, -2, 4);
        AviatorObject result = sum.call(env, createAviatorObject(list));
        assertEquals(10L, result.getValue(env));
    }

    @Test
    void testSumZeros() {
        SumFunction sum = new SumFunction();
        List<Integer> list = Arrays.asList(0, 0, 0, 0);
        AviatorObject result = sum.call(env, createAviatorObject(list));
        assertEquals(0L, result.getValue(env));
    }

    @Test
    void testSumLargeNumbers() {
        SumFunction sum = new SumFunction();
        List<Long> list = Arrays.asList(1000000L, 2000000L, 3000000L);
        AviatorObject result = sum.call(env, createAviatorObject(list));
        assertEquals(6000000L, result.getValue(env));
    }

    @Test
    void testSumWithWrongArgumentCount() {
        SumFunction sum = new SumFunction();
        List<Integer> list = Arrays.asList(1, 2, 3);
        assertThrows(TypeMismatchException.class, () -> {
            sum.call(env, createAviatorObject(list), createAviatorObject(list));
        });
    }

    @Test
    void testSumWithNoArguments() {
        // New behavior: SUM() should work and return sum of userData.events
        SumFunction sum = new SumFunction();
        
        // Create test environment with userData
        Map<String, Object> testEnv = createTestEnvWithEvents(5);
        
        AviatorObject result = sum.call(testEnv, new AviatorObject[0]);
        assertNotNull(result);
        // Sum of empty event parameters should be 0
        assertEquals(0L, result.getValue(testEnv));
    }

    @Test
    void testSumWithNonNumericElements() {
        SumFunction sum = new SumFunction();
        List<Object> list = Arrays.asList(1, "string", 3);
        assertThrows(TypeMismatchException.class, () -> {
            sum.call(env, createAviatorObject(list));
        });
    }

    @Test
    void testSumWithNonCollectionNonStringArgument() {
        // String arguments are now valid (filter conditions)
        // Only non-collection, non-string arguments should throw
        SumFunction sum = new SumFunction();
        assertThrows(TypeMismatchException.class, () -> {
            sum.call(env, AviatorLong.valueOf(123));
        });
    }

    @Test
    void testSumMetadata() {
        SumFunction sum = new SumFunction();
        assertEquals("SUM", sum.getName());
        assertEquals(0, sum.getFunctionMetadata().getMinArgs());
        assertEquals(2, sum.getFunctionMetadata().getMaxArgs());
        assertTrue(sum.getFunctionMetadata().getDescription().contains("userData.events"));
    }

    @Test
    void testSumReturnsCorrectType() {
        SumFunction sum = new SumFunction();
        
        // Integer sum should return Long
        List<Integer> intList = Arrays.asList(1, 2, 3);
        AviatorObject intResult = sum.call(env, createAviatorObject(intList));
        assertTrue(intResult.getValue(env) instanceof Long);
        
        // Double sum should return Double
        List<Double> doubleList = Arrays.asList(1.5, 2.5);
        AviatorObject doubleResult = sum.call(env, createAviatorObject(doubleList));
        assertTrue(doubleResult.getValue(env) instanceof Double);
    }

    @Test
    void testSumWithFloats() {
        SumFunction sum = new SumFunction();
        List<Float> list = Arrays.asList(1.5f, 2.5f, 3.0f);
        AviatorObject result = sum.call(env, createAviatorObject(list));
        assertEquals(7.0, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testSumWithLongs() {
        SumFunction sum = new SumFunction();
        List<Long> list = Arrays.asList(100L, 200L, 300L);
        AviatorObject result = sum.call(env, createAviatorObject(list));
        assertEquals(600L, result.getValue(env));
    }

    @Test
    void testSumWithBytes() {
        SumFunction sum = new SumFunction();
        List<Byte> list = Arrays.asList((byte) 1, (byte) 2, (byte) 3);
        AviatorObject result = sum.call(env, createAviatorObject(list));
        assertEquals(6L, result.getValue(env));
    }

    @Test
    void testSumWithShorts() {
        SumFunction sum = new SumFunction();
        List<Short> list = Arrays.asList((short) 10, (short) 20, (short) 30);
        AviatorObject result = sum.call(env, createAviatorObject(list));
        assertEquals(60L, result.getValue(env));
    }

    // ========== AVG Function Tests ==========

    @Test
    void testAvgEmptyCollection() {
        AvgFunction avg = new AvgFunction();
        List<Integer> emptyList = new ArrayList<>();
        AviatorObject result = avg.call(env, createAviatorObject(emptyList));
        assertNull(result.getValue(env));
    }

    @Test
    void testAvgIntegers() {
        AvgFunction avg = new AvgFunction();
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        AviatorObject result = avg.call(env, createAviatorObject(list));
        assertEquals(3.0, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testAvgDoubles() {
        AvgFunction avg = new AvgFunction();
        List<Double> list = Arrays.asList(1.5, 2.5, 3.0);
        AviatorObject result = avg.call(env, createAviatorObject(list));
        assertEquals(2.333333, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testAvgMixedNumbers() {
        AvgFunction avg = new AvgFunction();
        List<Number> list = Arrays.asList(1, 2, 3, 4);
        AviatorObject result = avg.call(env, createAviatorObject(list));
        assertEquals(2.5, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testAvgSingleElement() {
        AvgFunction avg = new AvgFunction();
        List<Integer> list = Collections.singletonList(42);
        AviatorObject result = avg.call(env, createAviatorObject(list));
        assertEquals(42.0, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testAvgWithNullElements() {
        AvgFunction avg = new AvgFunction();
        List<Integer> list = Arrays.asList(1, null, 2, null, 3);
        AviatorObject result = avg.call(env, createAviatorObject(list));
        // Average of 1, 2, 3 (nulls are skipped) = 6/3 = 2.0
        assertEquals(2.0, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testAvgAllNullElements() {
        AvgFunction avg = new AvgFunction();
        List<Integer> list = Arrays.asList(null, null, null);
        AviatorObject result = avg.call(env, createAviatorObject(list));
        assertNull(result.getValue(env));
    }

    @Test
    void testAvgNullCollection() {
        AvgFunction avg = new AvgFunction();
        AviatorObject result = avg.call(env, createAviatorObject(null));
        assertNull(result.getValue(env));
    }

    @Test
    void testAvgWithArray() {
        AvgFunction avg = new AvgFunction();
        Integer[] array = {10, 20, 30};
        AviatorObject result = avg.call(env, createAviatorObject(array));
        assertEquals(20.0, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testAvgWithEmptyArray() {
        AvgFunction avg = new AvgFunction();
        Integer[] array = {};
        AviatorObject result = avg.call(env, createAviatorObject(array));
        assertNull(result.getValue(env));
    }

    @Test
    void testAvgWithPrimitiveArray() {
        AvgFunction avg = new AvgFunction();
        int[] array = {2, 4, 6, 8, 10};
        AviatorObject result = avg.call(env, createAviatorObject(array));
        assertEquals(6.0, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testAvgWithDoubleArray() {
        AvgFunction avg = new AvgFunction();
        double[] array = {1.0, 2.0, 3.0, 4.0};
        AviatorObject result = avg.call(env, createAviatorObject(array));
        assertEquals(2.5, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testAvgNegativeNumbers() {
        AvgFunction avg = new AvgFunction();
        List<Integer> list = Arrays.asList(-10, -20, -30);
        AviatorObject result = avg.call(env, createAviatorObject(list));
        assertEquals(-20.0, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testAvgMixedPositiveNegative() {
        AvgFunction avg = new AvgFunction();
        List<Integer> list = Arrays.asList(10, -10, 5, -5);
        AviatorObject result = avg.call(env, createAviatorObject(list));
        assertEquals(0.0, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testAvgZeros() {
        AvgFunction avg = new AvgFunction();
        List<Integer> list = Arrays.asList(0, 0, 0, 0);
        AviatorObject result = avg.call(env, createAviatorObject(list));
        assertEquals(0.0, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testAvgLargeNumbers() {
        AvgFunction avg = new AvgFunction();
        List<Long> list = Arrays.asList(1000000L, 2000000L, 3000000L);
        AviatorObject result = avg.call(env, createAviatorObject(list));
        assertEquals(2000000.0, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testAvgWithWrongArgumentCount() {
        AvgFunction avg = new AvgFunction();
        List<Integer> list = Arrays.asList(1, 2, 3);
        assertThrows(TypeMismatchException.class, () -> {
            avg.call(env, createAviatorObject(list), createAviatorObject(list));
        });
    }

    @Test
    void testAvgWithNoArguments() {
        // New behavior: AVG() should work and return average of userData.events
        AvgFunction avg = new AvgFunction();
        
        // Create test environment with userData
        Map<String, Object> testEnv = createTestEnvWithEvents(5);
        
        AviatorObject result = avg.call(testEnv, new AviatorObject[0]);
        assertNotNull(result);
        // Average of empty event parameters should be null
        assertNull(result.getValue(testEnv));
    }

    @Test
    void testAvgWithNonNumericElements() {
        AvgFunction avg = new AvgFunction();
        List<Object> list = Arrays.asList(1, "string", 3);
        assertThrows(TypeMismatchException.class, () -> {
            avg.call(env, createAviatorObject(list));
        });
    }

    @Test
    void testAvgWithNonCollectionNonStringArgument() {
        // String arguments are now valid (filter conditions)
        // Only non-collection, non-string arguments should throw
        AvgFunction avg = new AvgFunction();
        assertThrows(TypeMismatchException.class, () -> {
            avg.call(env, AviatorLong.valueOf(123));
        });
    }

    @Test
    void testAvgMetadata() {
        AvgFunction avg = new AvgFunction();
        assertEquals("AVG", avg.getName());
        assertEquals(0, avg.getFunctionMetadata().getMinArgs());
        assertEquals(2, avg.getFunctionMetadata().getMaxArgs());
        assertTrue(avg.getFunctionMetadata().getDescription().contains("userData.events"));
    }

    @Test
    void testAvgReturnsDouble() {
        AvgFunction avg = new AvgFunction();
        List<Integer> list = Arrays.asList(1, 2, 3);
        AviatorObject result = avg.call(env, createAviatorObject(list));
        assertTrue(result.getValue(env) instanceof Double);
    }

    @Test
    void testAvgWithFloats() {
        AvgFunction avg = new AvgFunction();
        List<Float> list = Arrays.asList(1.5f, 2.5f, 3.5f);
        AviatorObject result = avg.call(env, createAviatorObject(list));
        assertEquals(2.5, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testAvgWithLongs() {
        AvgFunction avg = new AvgFunction();
        List<Long> list = Arrays.asList(100L, 200L, 300L);
        AviatorObject result = avg.call(env, createAviatorObject(list));
        assertEquals(200.0, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testAvgWithBytes() {
        AvgFunction avg = new AvgFunction();
        List<Byte> list = Arrays.asList((byte) 2, (byte) 4, (byte) 6);
        AviatorObject result = avg.call(env, createAviatorObject(list));
        assertEquals(4.0, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testAvgWithShorts() {
        AvgFunction avg = new AvgFunction();
        List<Short> list = Arrays.asList((short) 10, (short) 20, (short) 30);
        AviatorObject result = avg.call(env, createAviatorObject(list));
        assertEquals(20.0, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testAvgPrecision() {
        AvgFunction avg = new AvgFunction();
        List<Double> list = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0);
        AviatorObject result = avg.call(env, createAviatorObject(list));
        assertEquals(4.0, (Double) result.getValue(env), 0.0001);
    }

    // ========== Property Tests for SUM and AVG ==========

    @Test
    void testSumAndAvgRelationship() {
        // Property: AVG should equal SUM / COUNT for non-empty collections
        SumFunction sum = new SumFunction();
        AvgFunction avg = new AvgFunction();
        CountFunction count = new CountFunction();
        
        List<Integer> list = Arrays.asList(10, 20, 30, 40, 50);
        
        AviatorObject sumResult = sum.call(env, createAviatorObject(list));
        AviatorObject avgResult = avg.call(env, createAviatorObject(list));
        AviatorObject countResult = count.call(env, createAviatorObject(list));
        
        double sumValue = ((Number) sumResult.getValue(env)).doubleValue();
        double avgValue = (Double) avgResult.getValue(env);
        long countValue = (Long) countResult.getValue(env);
        
        assertEquals(sumValue / countValue, avgValue, 0.0001);
    }

    @Test
    void testSumEmptyVsNull() {
        // Property: SUM of empty collection should be 0, SUM of null should be 0
        SumFunction sum = new SumFunction();
        
        AviatorObject emptyResult = sum.call(env, createAviatorObject(new ArrayList<>()));
        AviatorObject nullResult = sum.call(env, createAviatorObject(null));
        
        assertEquals(0L, emptyResult.getValue(env));
        assertEquals(0L, nullResult.getValue(env));
    }

    @Test
    void testAvgEmptyVsNull() {
        // Property: AVG of empty collection should be null, AVG of null should be null
        AvgFunction avg = new AvgFunction();
        
        AviatorObject emptyResult = avg.call(env, createAviatorObject(new ArrayList<>()));
        AviatorObject nullResult = avg.call(env, createAviatorObject(null));
        
        assertNull(emptyResult.getValue(env));
        assertNull(nullResult.getValue(env));
    }

    // ========== MIN Function Tests ==========

    @Test
    void testMinEmptyCollection() {
        MinFunction min = new MinFunction();
        List<Integer> emptyList = new ArrayList<>();
        AviatorObject result = min.call(env, createAviatorObject(emptyList));
        assertNull(result.getValue(env));
    }

    @Test
    void testMinIntegers() {
        MinFunction min = new MinFunction();
        List<Integer> list = Arrays.asList(5, 2, 8, 1, 9);
        AviatorObject result = min.call(env, createAviatorObject(list));
        assertEquals(1L, result.getValue(env));
    }

    @Test
    void testMinDoubles() {
        MinFunction min = new MinFunction();
        List<Double> list = Arrays.asList(5.5, 2.2, 8.8, 1.1);
        AviatorObject result = min.call(env, createAviatorObject(list));
        assertEquals(1.1, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testMinMixedNumbers() {
        MinFunction min = new MinFunction();
        List<Number> list = Arrays.asList(5, 2.5, 8, 1.1);
        AviatorObject result = min.call(env, createAviatorObject(list));
        assertEquals(1.1, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testMinSingleElement() {
        MinFunction min = new MinFunction();
        List<Integer> list = Collections.singletonList(42);
        AviatorObject result = min.call(env, createAviatorObject(list));
        assertEquals(42L, result.getValue(env));
    }

    @Test
    void testMinWithNullElements() {
        MinFunction min = new MinFunction();
        List<Integer> list = Arrays.asList(5, null, 2, null, 8);
        AviatorObject result = min.call(env, createAviatorObject(list));
        assertEquals(2L, result.getValue(env));
    }

    @Test
    void testMinAllNullElements() {
        MinFunction min = new MinFunction();
        List<Integer> list = Arrays.asList(null, null, null);
        AviatorObject result = min.call(env, createAviatorObject(list));
        assertNull(result.getValue(env));
    }

    @Test
    void testMinNullCollection() {
        MinFunction min = new MinFunction();
        AviatorObject result = min.call(env, createAviatorObject(null));
        assertNull(result.getValue(env));
    }

    @Test
    void testMinWithArray() {
        MinFunction min = new MinFunction();
        Integer[] array = {10, 5, 20, 3, 15};
        AviatorObject result = min.call(env, createAviatorObject(array));
        assertEquals(3L, result.getValue(env));
    }

    @Test
    void testMinWithEmptyArray() {
        MinFunction min = new MinFunction();
        Integer[] array = {};
        AviatorObject result = min.call(env, createAviatorObject(array));
        assertNull(result.getValue(env));
    }

    @Test
    void testMinWithPrimitiveArray() {
        MinFunction min = new MinFunction();
        int[] array = {5, 2, 8, 1, 9};
        AviatorObject result = min.call(env, createAviatorObject(array));
        assertEquals(1L, result.getValue(env));
    }

    @Test
    void testMinWithDoubleArray() {
        MinFunction min = new MinFunction();
        double[] array = {5.5, 2.2, 8.8, 1.1};
        AviatorObject result = min.call(env, createAviatorObject(array));
        assertEquals(1.1, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testMinNegativeNumbers() {
        MinFunction min = new MinFunction();
        List<Integer> list = Arrays.asList(-1, -5, -3, -10, -2);
        AviatorObject result = min.call(env, createAviatorObject(list));
        assertEquals(-10L, result.getValue(env));
    }

    @Test
    void testMinMixedPositiveNegative() {
        MinFunction min = new MinFunction();
        List<Integer> list = Arrays.asList(10, -5, 3, -2, 4);
        AviatorObject result = min.call(env, createAviatorObject(list));
        assertEquals(-5L, result.getValue(env));
    }

    @Test
    void testMinWithZero() {
        MinFunction min = new MinFunction();
        List<Integer> list = Arrays.asList(5, 0, 3, 1);
        AviatorObject result = min.call(env, createAviatorObject(list));
        assertEquals(0L, result.getValue(env));
    }

    @Test
    void testMinStrings() {
        MinFunction min = new MinFunction();
        List<String> list = Arrays.asList("cherry", "apple", "banana");
        AviatorObject result = min.call(env, createAviatorObject(list));
        assertEquals("apple", result.getValue(env));
    }

    @Test
    void testMinWithWrongArgumentCount() {
        MinFunction min = new MinFunction();
        List<Integer> list = Arrays.asList(1, 2, 3);
        assertThrows(TypeMismatchException.class, () -> {
            min.call(env, createAviatorObject(list), createAviatorObject(list));
        });
    }

    @Test
    void testMinWithNoArguments() {
        // New behavior: MIN() should work and return min from userData.events
        MinFunction min = new MinFunction();
        
        // Create test environment with userData
        Map<String, Object> testEnv = createTestEnvWithEvents(5);
        
        AviatorObject result = min.call(testEnv, new AviatorObject[0]);
        assertNotNull(result);
        // Empty event parameters return null
        assertNull(result.getValue(testEnv));
    }

    @Test
    void testMinWithNonComparableElements() {
        MinFunction min = new MinFunction();
        List<Object> list = Arrays.asList(new Object(), new Object());
        assertThrows(TypeMismatchException.class, () -> {
            min.call(env, createAviatorObject(list));
        });
    }

    @Test
    void testMinWithMixedTypes() {
        MinFunction min = new MinFunction();
        List<Object> list = Arrays.asList(1, "string", 3);
        assertThrows(TypeMismatchException.class, () -> {
            min.call(env, createAviatorObject(list));
        });
    }

    @Test
    void testMinWithNonCollectionNonStringArgument() {
        // String arguments are now valid (filter conditions)
        // Only non-collection, non-string arguments should throw
        MinFunction min = new MinFunction();
        assertThrows(TypeMismatchException.class, () -> {
            min.call(env, AviatorLong.valueOf(123));
        });
    }

    @Test
    void testMinMetadata() {
        MinFunction min = new MinFunction();
        assertEquals("MIN", min.getName());
        assertEquals(0, min.getFunctionMetadata().getMinArgs());
        assertEquals(2, min.getFunctionMetadata().getMaxArgs());
        assertTrue(min.getFunctionMetadata().getDescription().contains("userData.events"));
    }

    @Test
    void testMinReturnsCorrectType() {
        MinFunction min = new MinFunction();
        
        // Integer min should return Long
        List<Integer> intList = Arrays.asList(1, 2, 3);
        AviatorObject intResult = min.call(env, createAviatorObject(intList));
        assertTrue(intResult.getValue(env) instanceof Long);
        
        // Double min should return Double
        List<Double> doubleList = Arrays.asList(1.5, 2.5);
        AviatorObject doubleResult = min.call(env, createAviatorObject(doubleList));
        assertTrue(doubleResult.getValue(env) instanceof Double);
        
        // String min should return String
        List<String> stringList = Arrays.asList("b", "a", "c");
        AviatorObject stringResult = min.call(env, createAviatorObject(stringList));
        assertTrue(stringResult.getValue(env) instanceof String);
    }

    @Test
    void testMinWithFloats() {
        MinFunction min = new MinFunction();
        List<Float> list = Arrays.asList(5.5f, 2.2f, 8.8f, 1.1f);
        AviatorObject result = min.call(env, createAviatorObject(list));
        assertEquals(1.1, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testMinWithLongs() {
        MinFunction min = new MinFunction();
        List<Long> list = Arrays.asList(1000L, 500L, 2000L);
        AviatorObject result = min.call(env, createAviatorObject(list));
        assertEquals(500L, result.getValue(env));
    }

    @Test
    void testMinWithBytes() {
        MinFunction min = new MinFunction();
        List<Byte> list = Arrays.asList((byte) 5, (byte) 2, (byte) 8);
        AviatorObject result = min.call(env, createAviatorObject(list));
        assertEquals(2L, result.getValue(env));
    }

    @Test
    void testMinWithShorts() {
        MinFunction min = new MinFunction();
        List<Short> list = Arrays.asList((short) 50, (short) 20, (short) 80);
        AviatorObject result = min.call(env, createAviatorObject(list));
        assertEquals(20L, result.getValue(env));
    }

    @Test
    void testMinEmptyVsNull() {
        // Property: MIN of empty collection should be null, MIN of null should be null
        MinFunction min = new MinFunction();
        
        AviatorObject emptyResult = min.call(env, createAviatorObject(new ArrayList<>()));
        AviatorObject nullResult = min.call(env, createAviatorObject(null));
        
        assertNull(emptyResult.getValue(env));
        assertNull(nullResult.getValue(env));
    }

    // ========== MAX Function Tests ==========

    @Test
    void testMaxEmptyCollection() {
        MaxFunction max = new MaxFunction();
        List<Integer> emptyList = new ArrayList<>();
        AviatorObject result = max.call(env, createAviatorObject(emptyList));
        assertNull(result.getValue(env));
    }

    @Test
    void testMaxIntegers() {
        MaxFunction max = new MaxFunction();
        List<Integer> list = Arrays.asList(5, 2, 8, 1, 9);
        AviatorObject result = max.call(env, createAviatorObject(list));
        assertEquals(9L, result.getValue(env));
    }

    @Test
    void testMaxDoubles() {
        MaxFunction max = new MaxFunction();
        List<Double> list = Arrays.asList(5.5, 2.2, 8.8, 1.1);
        AviatorObject result = max.call(env, createAviatorObject(list));
        assertEquals(8.8, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testMaxMixedNumbers() {
        MaxFunction max = new MaxFunction();
        List<Number> list = Arrays.asList(5, 2.5, 8.9, 1.1);
        AviatorObject result = max.call(env, createAviatorObject(list));
        assertEquals(8.9, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testMaxSingleElement() {
        MaxFunction max = new MaxFunction();
        List<Integer> list = Collections.singletonList(42);
        AviatorObject result = max.call(env, createAviatorObject(list));
        assertEquals(42L, result.getValue(env));
    }

    @Test
    void testMaxWithNullElements() {
        MaxFunction max = new MaxFunction();
        List<Integer> list = Arrays.asList(5, null, 2, null, 8);
        AviatorObject result = max.call(env, createAviatorObject(list));
        assertEquals(8L, result.getValue(env));
    }

    @Test
    void testMaxAllNullElements() {
        MaxFunction max = new MaxFunction();
        List<Integer> list = Arrays.asList(null, null, null);
        AviatorObject result = max.call(env, createAviatorObject(list));
        assertNull(result.getValue(env));
    }

    @Test
    void testMaxNullCollection() {
        MaxFunction max = new MaxFunction();
        AviatorObject result = max.call(env, createAviatorObject(null));
        assertNull(result.getValue(env));
    }

    @Test
    void testMaxWithArray() {
        MaxFunction max = new MaxFunction();
        Integer[] array = {10, 5, 20, 3, 15};
        AviatorObject result = max.call(env, createAviatorObject(array));
        assertEquals(20L, result.getValue(env));
    }

    @Test
    void testMaxWithEmptyArray() {
        MaxFunction max = new MaxFunction();
        Integer[] array = {};
        AviatorObject result = max.call(env, createAviatorObject(array));
        assertNull(result.getValue(env));
    }

    @Test
    void testMaxWithPrimitiveArray() {
        MaxFunction max = new MaxFunction();
        int[] array = {5, 2, 8, 1, 9};
        AviatorObject result = max.call(env, createAviatorObject(array));
        assertEquals(9L, result.getValue(env));
    }

    @Test
    void testMaxWithDoubleArray() {
        MaxFunction max = new MaxFunction();
        double[] array = {5.5, 2.2, 8.8, 1.1};
        AviatorObject result = max.call(env, createAviatorObject(array));
        assertEquals(8.8, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testMaxNegativeNumbers() {
        MaxFunction max = new MaxFunction();
        List<Integer> list = Arrays.asList(-1, -5, -3, -10, -2);
        AviatorObject result = max.call(env, createAviatorObject(list));
        assertEquals(-1L, result.getValue(env));
    }

    @Test
    void testMaxMixedPositiveNegative() {
        MaxFunction max = new MaxFunction();
        List<Integer> list = Arrays.asList(10, -5, 3, -2, 4);
        AviatorObject result = max.call(env, createAviatorObject(list));
        assertEquals(10L, result.getValue(env));
    }

    @Test
    void testMaxWithZero() {
        MaxFunction max = new MaxFunction();
        List<Integer> list = Arrays.asList(-5, 0, -3, -1);
        AviatorObject result = max.call(env, createAviatorObject(list));
        assertEquals(0L, result.getValue(env));
    }

    @Test
    void testMaxStrings() {
        MaxFunction max = new MaxFunction();
        List<String> list = Arrays.asList("cherry", "apple", "banana");
        AviatorObject result = max.call(env, createAviatorObject(list));
        assertEquals("cherry", result.getValue(env));
    }

    @Test
    void testMaxWithWrongArgumentCount() {
        MaxFunction max = new MaxFunction();
        List<Integer> list = Arrays.asList(1, 2, 3);
        assertThrows(TypeMismatchException.class, () -> {
            max.call(env, createAviatorObject(list), createAviatorObject(list));
        });
    }

    @Test
    void testMaxWithNoArguments() {
        // New behavior: MAX() should work and return max from userData.events
        MaxFunction max = new MaxFunction();
        
        // Create test environment with userData
        Map<String, Object> testEnv = createTestEnvWithEvents(5);
        
        AviatorObject result = max.call(testEnv, new AviatorObject[0]);
        assertNotNull(result);
        // Empty event parameters return null
        assertNull(result.getValue(testEnv));
    }

    @Test
    void testMaxWithNonComparableElements() {
        MaxFunction max = new MaxFunction();
        List<Object> list = Arrays.asList(new Object(), new Object());
        assertThrows(TypeMismatchException.class, () -> {
            max.call(env, createAviatorObject(list));
        });
    }

    @Test
    void testMaxWithMixedTypes() {
        MaxFunction max = new MaxFunction();
        List<Object> list = Arrays.asList(1, "string", 3);
        assertThrows(TypeMismatchException.class, () -> {
            max.call(env, createAviatorObject(list));
        });
    }

    @Test
    void testMaxWithNonCollectionNonStringArgument() {
        // String arguments are now valid (filter conditions)
        // Only non-collection, non-string arguments should throw
        MaxFunction max = new MaxFunction();
        assertThrows(TypeMismatchException.class, () -> {
            max.call(env, AviatorLong.valueOf(123));
        });
    }

    @Test
    void testMaxMetadata() {
        MaxFunction max = new MaxFunction();
        assertEquals("MAX", max.getName());
        assertEquals(0, max.getFunctionMetadata().getMinArgs());
        assertEquals(2, max.getFunctionMetadata().getMaxArgs());
        assertTrue(max.getFunctionMetadata().getDescription().contains("userData.events"));
    }

    @Test
    void testMaxReturnsCorrectType() {
        MaxFunction max = new MaxFunction();
        
        // Integer max should return Long
        List<Integer> intList = Arrays.asList(1, 2, 3);
        AviatorObject intResult = max.call(env, createAviatorObject(intList));
        assertTrue(intResult.getValue(env) instanceof Long);
        
        // Double max should return Double
        List<Double> doubleList = Arrays.asList(1.5, 2.5);
        AviatorObject doubleResult = max.call(env, createAviatorObject(doubleList));
        assertTrue(doubleResult.getValue(env) instanceof Double);
        
        // String max should return String
        List<String> stringList = Arrays.asList("b", "a", "c");
        AviatorObject stringResult = max.call(env, createAviatorObject(stringList));
        assertTrue(stringResult.getValue(env) instanceof String);
    }

    @Test
    void testMaxWithFloats() {
        MaxFunction max = new MaxFunction();
        List<Float> list = Arrays.asList(5.5f, 2.2f, 8.8f, 1.1f);
        AviatorObject result = max.call(env, createAviatorObject(list));
        assertEquals(8.8, (Double) result.getValue(env), 0.0001);
    }

    @Test
    void testMaxWithLongs() {
        MaxFunction max = new MaxFunction();
        List<Long> list = Arrays.asList(1000L, 500L, 2000L);
        AviatorObject result = max.call(env, createAviatorObject(list));
        assertEquals(2000L, result.getValue(env));
    }

    @Test
    void testMaxWithBytes() {
        MaxFunction max = new MaxFunction();
        List<Byte> list = Arrays.asList((byte) 5, (byte) 2, (byte) 8);
        AviatorObject result = max.call(env, createAviatorObject(list));
        assertEquals(8L, result.getValue(env));
    }

    @Test
    void testMaxWithShorts() {
        MaxFunction max = new MaxFunction();
        List<Short> list = Arrays.asList((short) 50, (short) 20, (short) 80);
        AviatorObject result = max.call(env, createAviatorObject(list));
        assertEquals(80L, result.getValue(env));
    }

    @Test
    void testMaxEmptyVsNull() {
        // Property: MAX of empty collection should be null, MAX of null should be null
        MaxFunction max = new MaxFunction();
        
        AviatorObject emptyResult = max.call(env, createAviatorObject(new ArrayList<>()));
        AviatorObject nullResult = max.call(env, createAviatorObject(null));
        
        assertNull(emptyResult.getValue(env));
        assertNull(nullResult.getValue(env));
    }

    // ========== Property Tests for MIN and MAX ==========

    @Test
    void testMinAndMaxBounds() {
        // Property 8: MIN and MAX Bounds
        // For any non-empty collection:
        // - MIN should return a value <= all values in the collection
        // - MAX should return a value >= all values in the collection
        // - MIN should be <= MAX
        
        MinFunction min = new MinFunction();
        MaxFunction max = new MaxFunction();
        
        List<Integer> list = Arrays.asList(10, 5, 20, 3, 15, 8);
        
        AviatorObject minResult = min.call(env, createAviatorObject(list));
        AviatorObject maxResult = max.call(env, createAviatorObject(list));
        
        long minValue = (Long) minResult.getValue(env);
        long maxValue = (Long) maxResult.getValue(env);
        
        // MIN should be <= all values
        for (Integer value : list) {
            assertTrue(minValue <= value, "MIN should be <= all values");
        }
        
        // MAX should be >= all values
        for (Integer value : list) {
            assertTrue(maxValue >= value, "MAX should be >= all values");
        }
        
        // MIN should be <= MAX
        assertTrue(minValue <= maxValue, "MIN should be <= MAX");
    }

    @Test
    void testMinMaxWithSingleElement() {
        // Property: For a single-element collection, MIN should equal MAX
        MinFunction min = new MinFunction();
        MaxFunction max = new MaxFunction();
        
        List<Integer> list = Collections.singletonList(42);
        
        AviatorObject minResult = min.call(env, createAviatorObject(list));
        AviatorObject maxResult = max.call(env, createAviatorObject(list));
        
        assertEquals(minResult.getValue(env), maxResult.getValue(env));
    }

    @Test
    void testMinMaxWithIdenticalElements() {
        // Property: For a collection with identical elements, MIN should equal MAX
        MinFunction min = new MinFunction();
        MaxFunction max = new MaxFunction();
        
        List<Integer> list = Arrays.asList(5, 5, 5, 5);
        
        AviatorObject minResult = min.call(env, createAviatorObject(list));
        AviatorObject maxResult = max.call(env, createAviatorObject(list));
        
        assertEquals(minResult.getValue(env), maxResult.getValue(env));
    }

    @Test
    void testMinMaxConsistency() {
        // Property: MIN and MAX should return consistent results for the same collection
        MinFunction min = new MinFunction();
        MaxFunction max = new MaxFunction();
        
        List<Integer> list = Arrays.asList(10, 5, 20, 3, 15);
        
        AviatorObject minResult1 = min.call(env, createAviatorObject(list));
        AviatorObject minResult2 = min.call(env, createAviatorObject(list));
        AviatorObject maxResult1 = max.call(env, createAviatorObject(list));
        AviatorObject maxResult2 = max.call(env, createAviatorObject(list));
        
        assertEquals(minResult1.getValue(env), minResult2.getValue(env));
        assertEquals(maxResult1.getValue(env), maxResult2.getValue(env));
    }

    @Test
    void testMinMaxWithDuplicates() {
        // MIN and MAX should work correctly even with duplicate values
        MinFunction min = new MinFunction();
        MaxFunction max = new MaxFunction();
        
        List<Integer> list = Arrays.asList(5, 2, 8, 2, 8, 5);
        
        AviatorObject minResult = min.call(env, createAviatorObject(list));
        AviatorObject maxResult = max.call(env, createAviatorObject(list));
        
        assertEquals(2L, minResult.getValue(env));
        assertEquals(8L, maxResult.getValue(env));
    }

    @Test
    void testMinMaxStringComparison() {
        // MIN and MAX should work with string lexicographic comparison
        MinFunction min = new MinFunction();
        MaxFunction max = new MaxFunction();
        
        List<String> list = Arrays.asList("zebra", "apple", "mango", "banana");
        
        AviatorObject minResult = min.call(env, createAviatorObject(list));
        AviatorObject maxResult = max.call(env, createAviatorObject(list));
        
        assertEquals("apple", minResult.getValue(env));
        assertEquals("zebra", maxResult.getValue(env));
    }

    // ========== UNIQUE Function Tests ==========

    @Test
    void testUniqueEmptyCollection() {
        UniqueFunction unique = new UniqueFunction();
        List<Integer> emptyList = new ArrayList<>();
        AviatorObject result = unique.call(env, createAviatorObject(emptyList));
        
        @SuppressWarnings("unchecked")
        List<Integer> resultList = (List<Integer>) result.getValue(env);
        assertTrue(resultList.isEmpty());
    }

    @Test
    void testUniqueNoDuplicates() {
        UniqueFunction unique = new UniqueFunction();
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        AviatorObject result = unique.call(env, createAviatorObject(list));
        
        @SuppressWarnings("unchecked")
        List<Integer> resultList = (List<Integer>) result.getValue(env);
        assertEquals(5, resultList.size());
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), resultList);
    }

    @Test
    void testUniqueWithDuplicates() {
        UniqueFunction unique = new UniqueFunction();
        List<Integer> list = Arrays.asList(1, 2, 2, 3, 1, 4, 3, 5);
        AviatorObject result = unique.call(env, createAviatorObject(list));
        
        @SuppressWarnings("unchecked")
        List<Integer> resultList = (List<Integer>) result.getValue(env);
        assertEquals(5, resultList.size());
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), resultList);
    }

    @Test
    void testUniquePreservesOrder() {
        // Property: UNIQUE should preserve the order of first occurrence
        UniqueFunction unique = new UniqueFunction();
        List<Integer> list = Arrays.asList(5, 3, 5, 1, 3, 2, 1);
        AviatorObject result = unique.call(env, createAviatorObject(list));
        
        @SuppressWarnings("unchecked")
        List<Integer> resultList = (List<Integer>) result.getValue(env);
        assertEquals(Arrays.asList(5, 3, 1, 2), resultList);
    }

    @Test
    void testUniqueStrings() {
        UniqueFunction unique = new UniqueFunction();
        List<String> list = Arrays.asList("apple", "banana", "apple", "cherry", "banana");
        AviatorObject result = unique.call(env, createAviatorObject(list));
        
        @SuppressWarnings("unchecked")
        List<String> resultList = (List<String>) result.getValue(env);
        assertEquals(3, resultList.size());
        assertEquals(Arrays.asList("apple", "banana", "cherry"), resultList);
    }

    @Test
    void testUniqueSingleElement() {
        UniqueFunction unique = new UniqueFunction();
        List<Integer> list = Collections.singletonList(42);
        AviatorObject result = unique.call(env, createAviatorObject(list));
        
        @SuppressWarnings("unchecked")
        List<Integer> resultList = (List<Integer>) result.getValue(env);
        assertEquals(1, resultList.size());
        assertEquals(42, resultList.get(0));
    }

    @Test
    void testUniqueAllDuplicates() {
        UniqueFunction unique = new UniqueFunction();
        List<Integer> list = Arrays.asList(7, 7, 7, 7, 7);
        AviatorObject result = unique.call(env, createAviatorObject(list));
        
        @SuppressWarnings("unchecked")
        List<Integer> resultList = (List<Integer>) result.getValue(env);
        assertEquals(1, resultList.size());
        assertEquals(7, resultList.get(0));
    }

    @Test
    void testUniqueWithNullElements() {
        UniqueFunction unique = new UniqueFunction();
        List<Integer> list = Arrays.asList(1, null, 2, null, 3, null);
        AviatorObject result = unique.call(env, createAviatorObject(list));
        
        @SuppressWarnings("unchecked")
        List<Integer> resultList = (List<Integer>) result.getValue(env);
        assertEquals(4, resultList.size());
        assertEquals(Arrays.asList(1, null, 2, 3), resultList);
    }

    @Test
    void testUniqueNullCollection() {
        UniqueFunction unique = new UniqueFunction();
        AviatorObject result = unique.call(env, createAviatorObject(null));
        
        @SuppressWarnings("unchecked")
        List<?> resultList = (List<?>) result.getValue(env);
        assertTrue(resultList.isEmpty());
    }

    @Test
    void testUniqueWithArray() {
        UniqueFunction unique = new UniqueFunction();
        Integer[] array = {1, 2, 2, 3, 1, 4};
        AviatorObject result = unique.call(env, createAviatorObject(array));
        
        @SuppressWarnings("unchecked")
        List<Integer> resultList = (List<Integer>) result.getValue(env);
        assertEquals(4, resultList.size());
        assertEquals(Arrays.asList(1, 2, 3, 4), resultList);
    }

    @Test
    void testUniqueWithEmptyArray() {
        UniqueFunction unique = new UniqueFunction();
        Integer[] array = {};
        AviatorObject result = unique.call(env, createAviatorObject(array));
        
        @SuppressWarnings("unchecked")
        List<?> resultList = (List<?>) result.getValue(env);
        assertTrue(resultList.isEmpty());
    }

    @Test
    void testUniqueWithPrimitiveArray() {
        UniqueFunction unique = new UniqueFunction();
        int[] array = {1, 2, 2, 3, 1, 4, 3};
        AviatorObject result = unique.call(env, createAviatorObject(array));
        
        @SuppressWarnings("unchecked")
        List<Integer> resultList = (List<Integer>) result.getValue(env);
        assertEquals(4, resultList.size());
        assertEquals(Arrays.asList(1, 2, 3, 4), resultList);
    }

    @Test
    void testUniqueWithMixedTypes() {
        UniqueFunction unique = new UniqueFunction();
        List<Object> list = Arrays.asList(1, "string", 1, 3.14, "string", true);
        AviatorObject result = unique.call(env, createAviatorObject(list));
        
        @SuppressWarnings("unchecked")
        List<Object> resultList = (List<Object>) result.getValue(env);
        assertEquals(4, resultList.size());
        assertEquals(Arrays.asList(1, "string", 3.14, true), resultList);
    }

    @Test
    void testUniqueWithDoubles() {
        UniqueFunction unique = new UniqueFunction();
        List<Double> list = Arrays.asList(1.1, 2.2, 1.1, 3.3, 2.2);
        AviatorObject result = unique.call(env, createAviatorObject(list));
        
        @SuppressWarnings("unchecked")
        List<Double> resultList = (List<Double>) result.getValue(env);
        assertEquals(3, resultList.size());
        assertEquals(Arrays.asList(1.1, 2.2, 3.3), resultList);
    }

    @Test
    void testUniqueWithSet() {
        // Even if input is already unique (Set), UNIQUE should work
        UniqueFunction unique = new UniqueFunction();
        Set<Integer> set = new LinkedHashSet<>(Arrays.asList(1, 2, 3, 4));
        AviatorObject result = unique.call(env, createAviatorObject(set));
        
        @SuppressWarnings("unchecked")
        List<Integer> resultList = (List<Integer>) result.getValue(env);
        assertEquals(4, resultList.size());
        assertTrue(resultList.containsAll(Arrays.asList(1, 2, 3, 4)));
    }

    @Test
    void testUniqueWithLargeCollection() {
        UniqueFunction unique = new UniqueFunction();
        List<Integer> largeList = new ArrayList<>();
        // Add numbers 0-99, each repeated 10 times
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 100; j++) {
                largeList.add(j);
            }
        }
        
        AviatorObject result = unique.call(env, createAviatorObject(largeList));
        
        @SuppressWarnings("unchecked")
        List<Integer> resultList = (List<Integer>) result.getValue(env);
        assertEquals(100, resultList.size());
        // Verify order is preserved (first occurrence)
        for (int i = 0; i < 100; i++) {
            assertEquals(i, resultList.get(i));
        }
    }

    @Test
    void testUniqueWithWrongArgumentCount() {
        UniqueFunction unique = new UniqueFunction();
        List<Integer> list = Arrays.asList(1, 2, 3);
        assertThrows(TypeMismatchException.class, () -> {
            unique.call(env, createAviatorObject(list), createAviatorObject(list));
        });
    }

    @Test
    void testUniqueWithNoArguments() {
        // New behavior: UNIQUE() should work and return unique events from userData.events
        UniqueFunction unique = new UniqueFunction();
        
        // Create test environment with userData
        Map<String, Object> testEnv = createTestEnvWithEvents(5);
        
        AviatorObject result = unique.call(testEnv, new AviatorObject[0]);
        assertNotNull(result);
        @SuppressWarnings("unchecked")
        List<?> resultList = (List<?>) result.getValue(testEnv);
        assertEquals(5, resultList.size()); // 5 unique events
    }

    @Test
    void testUniqueWithNonCollectionNonStringArgument() {
        // String arguments are now valid (filter conditions)
        // Only non-collection, non-string arguments should throw
        UniqueFunction unique = new UniqueFunction();
        assertThrows(TypeMismatchException.class, () -> {
            unique.call(env, AviatorLong.valueOf(123));
        });
    }

    @Test
    void testUniqueMetadata() {
        UniqueFunction unique = new UniqueFunction();
        assertEquals("UNIQUE", unique.getName());
        assertEquals(0, unique.getFunctionMetadata().getMinArgs());
        assertEquals(2, unique.getFunctionMetadata().getMaxArgs());
        assertTrue(unique.getFunctionMetadata().getDescription().contains("userData.events"));
    }

    @Test
    void testUniqueReturnsCollection() {
        // Verify that UNIQUE returns a Collection
        UniqueFunction unique = new UniqueFunction();
        List<Integer> list = Arrays.asList(1, 2, 3);
        AviatorObject result = unique.call(env, createAviatorObject(list));
        
        Object value = result.getValue(env);
        assertTrue(value instanceof Collection, "UNIQUE should return Collection, got: " + value.getClass().getSimpleName());
    }

    @Test
    void testUniqueCountRelationship() {
        // Property: COUNT(UNIQUE(collection)) <= COUNT(collection)
        UniqueFunction unique = new UniqueFunction();
        CountFunction count = new CountFunction();
        
        List<Integer> list = Arrays.asList(1, 2, 2, 3, 1, 4, 3, 5);
        
        AviatorObject uniqueResult = unique.call(env, createAviatorObject(list));
        AviatorObject countOriginal = count.call(env, createAviatorObject(list));
        AviatorObject countUnique = count.call(env, uniqueResult);
        
        long originalCount = (Long) countOriginal.getValue(env);
        long uniqueCount = (Long) countUnique.getValue(env);
        
        assertTrue(uniqueCount <= originalCount, 
                   "COUNT(UNIQUE(collection)) should be <= COUNT(collection)");
        assertEquals(8L, originalCount);
        assertEquals(5L, uniqueCount);
    }

    @Test
    void testUniqueIdempotent() {
        // Property: UNIQUE(UNIQUE(collection)) should equal UNIQUE(collection)
        UniqueFunction unique = new UniqueFunction();
        
        List<Integer> list = Arrays.asList(1, 2, 2, 3, 1, 4);
        
        AviatorObject uniqueOnce = unique.call(env, createAviatorObject(list));
        AviatorObject uniqueTwice = unique.call(env, uniqueOnce);
        
        @SuppressWarnings("unchecked")
        List<Integer> resultOnce = (List<Integer>) uniqueOnce.getValue(env);
        @SuppressWarnings("unchecked")
        List<Integer> resultTwice = (List<Integer>) uniqueTwice.getValue(env);
        
        assertEquals(resultOnce, resultTwice, "UNIQUE should be idempotent");
    }

    @Test
    void testUniqueConsistency() {
        // Property: UNIQUE should return the same result for the same collection
        UniqueFunction unique = new UniqueFunction();
        List<Integer> list = Arrays.asList(5, 3, 5, 1, 3, 2);
        
        AviatorObject result1 = unique.call(env, createAviatorObject(list));
        AviatorObject result2 = unique.call(env, createAviatorObject(list));
        
        assertEquals(result1.getValue(env), result2.getValue(env));
    }

    @Test
    void testUniqueWithNestedCollections() {
        UniqueFunction unique = new UniqueFunction();
        List<List<Integer>> nestedList = Arrays.asList(
            Arrays.asList(1, 2),
            Arrays.asList(3, 4),
            Arrays.asList(1, 2),  // Duplicate
            Arrays.asList(5, 6)
        );
        
        AviatorObject result = unique.call(env, createAviatorObject(nestedList));
        
        @SuppressWarnings("unchecked")
        List<List<Integer>> resultList = (List<List<Integer>>) result.getValue(env);
        assertEquals(3, resultList.size());
        // First occurrence of [1,2] should be preserved
        assertEquals(Arrays.asList(1, 2), resultList.get(0));
        assertEquals(Arrays.asList(3, 4), resultList.get(1));
        assertEquals(Arrays.asList(5, 6), resultList.get(2));
    }

    @Test
    void testUniquePreservesFirstOccurrence() {
        // Verify that when there are duplicates, the first occurrence is kept
        UniqueFunction unique = new UniqueFunction();
        
        // Create a list where we can track which instance is kept
        List<String> list = new ArrayList<>();
        list.add("first-1");
        list.add("second");
        list.add("first-2");  // Duplicate value but different instance
        
        // Make them equal for the test
        list.set(0, "value");
        list.set(2, "value");
        
        AviatorObject result = unique.call(env, createAviatorObject(list));
        
        @SuppressWarnings("unchecked")
        List<String> resultList = (List<String>) result.getValue(env);
        assertEquals(2, resultList.size());
        assertEquals("value", resultList.get(0));
        assertEquals("second", resultList.get(1));
    }

    // ========== Helper Methods for New Syntax Tests ==========

    /**
     * Create a test environment with userData containing specified number of events
     */
    private Map<String, Object> createTestEnvWithEvents(int eventCount) {
        Map<String, Object> testEnv = new HashMap<>();
        
        // Create real Event objects
        List<com.filter.dsl.models.Event> events = new ArrayList<>();
        for (int i = 0; i < eventCount; i++) {
            com.filter.dsl.models.Event event = com.filter.dsl.models.Event.builder()
                .eventName("test_event_" + i)
                .eventType("action")
                .build();
            events.add(event);
        }
        
        // Create real UserData object
        com.filter.dsl.models.UserData userData = com.filter.dsl.models.UserData.builder()
            .events(events)
            .build();
        
        testEnv.put("userData", userData);
        return testEnv;
    }
}
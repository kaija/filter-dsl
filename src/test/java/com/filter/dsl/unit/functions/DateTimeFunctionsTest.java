package com.filter.dsl.unit.functions;

import com.filter.dsl.functions.FunctionArgumentException;
import com.filter.dsl.functions.TypeMismatchException;
import com.filter.dsl.functions.datetime.*;
import com.filter.dsl.models.Event;
import com.filter.dsl.models.TimeRange;
import com.filter.dsl.models.TimeUnit;
import com.filter.dsl.models.UserData;
import com.googlecode.aviator.runtime.type.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for date/time functions: ACTION_TIME, DATE_FORMAT, DATE_DIFF, FROM, TO
 */
class DateTimeFunctionsTest {

    private final Map<String, Object> env = new HashMap<>();

    // ========== ACTION_TIME Function Tests ==========

    @Test
    void testActionTimeReturnsEventTimestamp() {
        ActionTimeFunction actionTime = new ActionTimeFunction();
        
        // Create an event with a timestamp
        Event event = Event.builder()
            .timestamp("2023-01-15T10:30:00Z")
            .eventName("purchase")
            .build();
        
        env.put("currentEvent", event);
        
        AviatorObject result = actionTime.call(env);
        assertEquals("2023-01-15T10:30:00Z", result.getValue(env));
    }

    @Test
    void testActionTimeWithNoCurrentEvent() {
        ActionTimeFunction actionTime = new ActionTimeFunction();
        
        // No current event in context
        AviatorObject result = actionTime.call(env);
        assertNull(result.getValue(env));
    }

    @Test
    void testActionTimeWithNullTimestamp() {
        ActionTimeFunction actionTime = new ActionTimeFunction();
        
        Event event = Event.builder()
            .eventName("purchase")
            .build();
        
        env.put("currentEvent", event);
        
        AviatorObject result = actionTime.call(env);
        assertNull(result.getValue(env));
    }

    @Test
    void testActionTimeWithWrongArgumentCount() {
        ActionTimeFunction actionTime = new ActionTimeFunction();
        assertThrows(IllegalArgumentException.class, () -> {
            actionTime.call(env, AviatorLong.valueOf(5));
        });
    }

    @Test
    void testActionTimeMetadata() {
        ActionTimeFunction actionTime = new ActionTimeFunction();
        assertEquals("ACTION_TIME", actionTime.getName());
        assertEquals(0, actionTime.getFunctionMetadata().getMinArgs());
        assertEquals(0, actionTime.getFunctionMetadata().getMaxArgs());
    }

    // ========== DATE_FORMAT Function Tests ==========

    @Test
    void testDateFormatBasicYearMonthDay() {
        DateFormatFunction dateFormat = new DateFormatFunction();
        
        AviatorObject result = dateFormat.call(env,
            new AviatorString("2023-01-15T10:30:00Z"),
            new AviatorString("yyyy-MM-dd")
        );
        
        assertEquals("2023-01-15", result.getValue(env));
    }

    @Test
    void testDateFormatTimeOnly() {
        DateFormatFunction dateFormat = new DateFormatFunction();
        
        AviatorObject result = dateFormat.call(env,
            new AviatorString("2023-01-15T10:30:45Z"),
            new AviatorString("HH:mm:ss")
        );
        
        assertEquals("10:30:45", result.getValue(env));
    }

    @Test
    void testDateFormatFullDateTime() {
        DateFormatFunction dateFormat = new DateFormatFunction();
        
        AviatorObject result = dateFormat.call(env,
            new AviatorString("2023-01-15T10:30:00Z"),
            new AviatorString("yyyy-MM-dd HH:mm:ss")
        );
        
        assertEquals("2023-01-15 10:30:00", result.getValue(env));
    }

    @Test
    void testDateFormatCustomPattern() {
        DateFormatFunction dateFormat = new DateFormatFunction();
        
        AviatorObject result = dateFormat.call(env,
            new AviatorString("2023-01-15T10:30:00Z"),
            new AviatorString("MM/dd/yyyy")
        );
        
        assertEquals("01/15/2023", result.getValue(env));
    }

    @Test
    void testDateFormatWithNullTimestamp() {
        DateFormatFunction dateFormat = new DateFormatFunction();
        
        AviatorObject result = dateFormat.call(env,
            AviatorNil.NIL,
            new AviatorString("yyyy-MM-dd")
        );
        
        assertNull(result.getValue(env));
    }

    @Test
    void testDateFormatWithNullFormat() {
        DateFormatFunction dateFormat = new DateFormatFunction();
        
        AviatorObject result = dateFormat.call(env,
            new AviatorString("2023-01-15T10:30:00Z"),
            AviatorNil.NIL
        );
        
        assertNull(result.getValue(env));
    }

    @Test
    void testDateFormatWithInvalidTimestamp() {
        DateFormatFunction dateFormat = new DateFormatFunction();
        
        assertThrows(TypeMismatchException.class, () -> {
            dateFormat.call(env,
                new AviatorString("not a valid timestamp"),
                new AviatorString("yyyy-MM-dd")
            );
        });
    }

    @Test
    void testDateFormatWithInvalidPattern() {
        DateFormatFunction dateFormat = new DateFormatFunction();
        
        assertThrows(TypeMismatchException.class, () -> {
            dateFormat.call(env,
                new AviatorString("2023-01-15T10:30:00Z"),
                new AviatorString("invalid pattern %%%")
            );
        });
    }

    @Test
    void testDateFormatWithWrongArgumentCount() {
        DateFormatFunction dateFormat = new DateFormatFunction();
        assertThrows(IllegalArgumentException.class, () -> {
            dateFormat.call(env, new AviatorString("2023-01-15T10:30:00Z"));
        });
    }

    @Test
    void testDateFormatMetadata() {
        DateFormatFunction dateFormat = new DateFormatFunction();
        assertEquals("DATE_FORMAT", dateFormat.getName());
        assertEquals(2, dateFormat.getFunctionMetadata().getMinArgs());
        assertEquals(2, dateFormat.getFunctionMetadata().getMaxArgs());
    }

    // ========== DATE_DIFF Function Tests ==========

    @Test
    void testDateDiffInDays() {
        DateDiffFunction dateDiff = new DateDiffFunction();
        
        AviatorObject result = dateDiff.call(env,
            new AviatorString("2023-01-20T00:00:00Z"),
            new AviatorString("2023-01-15T00:00:00Z"),
            new AviatorString("D")
        );
        
        assertEquals(5L, result.getValue(env));
    }

    @Test
    void testDateDiffInHours() {
        DateDiffFunction dateDiff = new DateDiffFunction();
        
        AviatorObject result = dateDiff.call(env,
            new AviatorString("2023-01-15T12:00:00Z"),
            new AviatorString("2023-01-15T10:00:00Z"),
            new AviatorString("H")
        );
        
        assertEquals(2L, result.getValue(env));
    }

    @Test
    void testDateDiffInMinutes() {
        DateDiffFunction dateDiff = new DateDiffFunction();
        
        AviatorObject result = dateDiff.call(env,
            new AviatorString("2023-01-15T10:30:00Z"),
            new AviatorString("2023-01-15T10:00:00Z"),
            new AviatorString("M")
        );
        
        assertEquals(30L, result.getValue(env));
    }

    @Test
    void testDateDiffInWeeks() {
        DateDiffFunction dateDiff = new DateDiffFunction();
        
        AviatorObject result = dateDiff.call(env,
            new AviatorString("2023-01-29T00:00:00Z"),
            new AviatorString("2023-01-15T00:00:00Z"),
            new AviatorString("W")
        );
        
        assertEquals(2L, result.getValue(env));
    }

    @Test
    void testDateDiffInMonths() {
        DateDiffFunction dateDiff = new DateDiffFunction();
        
        AviatorObject result = dateDiff.call(env,
            new AviatorString("2023-04-15T00:00:00Z"),
            new AviatorString("2023-01-15T00:00:00Z"),
            new AviatorString("MO")
        );
        
        // Approximate: 90 days / 30 = 3 months
        assertEquals(3L, result.getValue(env));
    }

    @Test
    void testDateDiffInYears() {
        DateDiffFunction dateDiff = new DateDiffFunction();
        
        AviatorObject result = dateDiff.call(env,
            new AviatorString("2025-01-15T00:00:00Z"),
            new AviatorString("2023-01-15T00:00:00Z"),
            new AviatorString("Y")
        );
        
        // Approximate: 730 days / 365 = 2 years
        assertEquals(2L, result.getValue(env));
    }

    @Test
    void testDateDiffNegativeResult() {
        DateDiffFunction dateDiff = new DateDiffFunction();
        
        // date1 is before date2, so result should be negative
        AviatorObject result = dateDiff.call(env,
            new AviatorString("2023-01-10T00:00:00Z"),
            new AviatorString("2023-01-15T00:00:00Z"),
            new AviatorString("D")
        );
        
        assertEquals(-5L, result.getValue(env));
    }

    @Test
    void testDateDiffSameDates() {
        DateDiffFunction dateDiff = new DateDiffFunction();
        
        AviatorObject result = dateDiff.call(env,
            new AviatorString("2023-01-15T10:30:00Z"),
            new AviatorString("2023-01-15T10:30:00Z"),
            new AviatorString("D")
        );
        
        assertEquals(0L, result.getValue(env));
    }

    @Test
    void testDateDiffWithInvalidTimestamp() {
        DateDiffFunction dateDiff = new DateDiffFunction();
        
        assertThrows(TypeMismatchException.class, () -> {
            dateDiff.call(env,
                new AviatorString("invalid timestamp"),
                new AviatorString("2023-01-15T00:00:00Z"),
                new AviatorString("D")
            );
        });
    }

    @Test
    void testDateDiffWithInvalidUnit() {
        DateDiffFunction dateDiff = new DateDiffFunction();
        
        assertThrows(TypeMismatchException.class, () -> {
            dateDiff.call(env,
                new AviatorString("2023-01-20T00:00:00Z"),
                new AviatorString("2023-01-15T00:00:00Z"),
                new AviatorString("INVALID")
            );
        });
    }

    @Test
    void testDateDiffWithNullArguments() {
        DateDiffFunction dateDiff = new DateDiffFunction();
        
        assertThrows(TypeMismatchException.class, () -> {
            dateDiff.call(env,
                AviatorNil.NIL,
                new AviatorString("2023-01-15T00:00:00Z"),
                new AviatorString("D")
            );
        });
    }

    @Test
    void testDateDiffWithWrongArgumentCount() {
        DateDiffFunction dateDiff = new DateDiffFunction();
        assertThrows(IllegalArgumentException.class, () -> {
            dateDiff.call(env,
                new AviatorString("2023-01-20T00:00:00Z"),
                new AviatorString("2023-01-15T00:00:00Z")
            );
        });
    }

    @Test
    void testDateDiffMetadata() {
        DateDiffFunction dateDiff = new DateDiffFunction();
        assertEquals("DATE_DIFF", dateDiff.getName());
        assertEquals(3, dateDiff.getFunctionMetadata().getMinArgs());
        assertEquals(3, dateDiff.getFunctionMetadata().getMaxArgs());
    }

    // ========== FROM Function Tests ==========

    @Test
    void testFromCreatesTimeRange() {
        FromFunction from = new FromFunction();
        
        Instant now = Instant.parse("2023-01-15T10:00:00Z");
        env.put("now", now);
        
        AviatorObject result = from.call(env,
            AviatorLong.valueOf(30),
            new AviatorString("D")
        );
        
        // Verify time range was created in context
        TimeRange timeRange = (TimeRange) env.get("timeRange");
        assertNotNull(timeRange);
        assertEquals(30, timeRange.getFromValue());
        assertEquals(TimeUnit.D, timeRange.getFromUnit());
    }

    @Test
    void testFromWithDifferentUnits() {
        FromFunction from = new FromFunction();
        
        Instant now = Instant.now();
        env.put("now", now);
        
        // Test with weeks
        from.call(env, AviatorLong.valueOf(7), new AviatorString("W"));
        TimeRange timeRange = (TimeRange) env.get("timeRange");
        assertEquals(TimeUnit.W, timeRange.getFromUnit());
        
        // Test with months
        from.call(env, AviatorLong.valueOf(12), new AviatorString("MO"));
        timeRange = (TimeRange) env.get("timeRange");
        assertEquals(TimeUnit.MO, timeRange.getFromUnit());
        
        // Test with years
        from.call(env, AviatorLong.valueOf(2), new AviatorString("Y"));
        timeRange = (TimeRange) env.get("timeRange");
        assertEquals(TimeUnit.Y, timeRange.getFromUnit());
    }

    @Test
    void testFromUpdatesExistingTimeRange() {
        FromFunction from = new FromFunction();
        
        Instant now = Instant.now();
        env.put("now", now);
        
        // Create initial time range with TO
        TimeRange initialRange = new TimeRange(null, null, 0, TimeUnit.D, now);
        env.put("timeRange", initialRange);
        
        // Update with FROM
        from.call(env, AviatorLong.valueOf(30), new AviatorString("D"));
        
        TimeRange updatedRange = (TimeRange) env.get("timeRange");
        assertEquals(30, updatedRange.getFromValue());
        assertEquals(TimeUnit.D, updatedRange.getFromUnit());
        assertEquals(0, updatedRange.getToValue());
        assertEquals(TimeUnit.D, updatedRange.getToUnit());
    }

    @Test
    void testFromWithInvalidUnit() {
        FromFunction from = new FromFunction();
        
        assertThrows(TypeMismatchException.class, () -> {
            from.call(env,
                AviatorLong.valueOf(30),
                new AviatorString("INVALID")
            );
        });
    }

    @Test
    void testFromWithNullArguments() {
        FromFunction from = new FromFunction();
        
        assertThrows(TypeMismatchException.class, () -> {
            from.call(env, AviatorNil.NIL, new AviatorString("D"));
        });
    }

    @Test
    void testFromWithWrongArgumentCount() {
        FromFunction from = new FromFunction();
        assertThrows(IllegalArgumentException.class, () -> {
            from.call(env, AviatorLong.valueOf(30));
        });
    }

    @Test
    void testFromMetadata() {
        FromFunction from = new FromFunction();
        assertEquals("FROM", from.getName());
        assertEquals(2, from.getFunctionMetadata().getMinArgs());
        assertEquals(2, from.getFunctionMetadata().getMaxArgs());
    }

    // ========== TO Function Tests ==========

    @Test
    void testToCreatesTimeRange() {
        ToFunction to = new ToFunction();
        
        Instant now = Instant.parse("2023-01-15T10:00:00Z");
        env.put("now", now);
        
        AviatorObject result = to.call(env,
            AviatorLong.valueOf(0),
            new AviatorString("D")
        );
        
        // Verify time range was created in context
        TimeRange timeRange = (TimeRange) env.get("timeRange");
        assertNotNull(timeRange);
        assertEquals(0, timeRange.getToValue());
        assertEquals(TimeUnit.D, timeRange.getToUnit());
    }

    @Test
    void testToWithDifferentUnits() {
        ToFunction to = new ToFunction();
        
        Instant now = Instant.now();
        env.put("now", now);
        
        // Test with hours
        to.call(env, AviatorLong.valueOf(1), new AviatorString("H"));
        TimeRange timeRange = (TimeRange) env.get("timeRange");
        assertEquals(TimeUnit.H, timeRange.getToUnit());
        
        // Test with minutes
        to.call(env, AviatorLong.valueOf(30), new AviatorString("M"));
        timeRange = (TimeRange) env.get("timeRange");
        assertEquals(TimeUnit.M, timeRange.getToUnit());
    }

    @Test
    void testToUpdatesExistingTimeRange() {
        ToFunction to = new ToFunction();
        
        Instant now = Instant.now();
        env.put("now", now);
        
        // Create initial time range with FROM
        TimeRange initialRange = new TimeRange(30, TimeUnit.D, null, null, now);
        env.put("timeRange", initialRange);
        
        // Update with TO
        to.call(env, AviatorLong.valueOf(0), new AviatorString("D"));
        
        TimeRange updatedRange = (TimeRange) env.get("timeRange");
        assertEquals(30, updatedRange.getFromValue());
        assertEquals(TimeUnit.D, updatedRange.getFromUnit());
        assertEquals(0, updatedRange.getToValue());
        assertEquals(TimeUnit.D, updatedRange.getToUnit());
    }

    @Test
    void testToWithInvalidUnit() {
        ToFunction to = new ToFunction();
        
        assertThrows(TypeMismatchException.class, () -> {
            to.call(env,
                AviatorLong.valueOf(0),
                new AviatorString("INVALID")
            );
        });
    }

    @Test
    void testToWithNullArguments() {
        ToFunction to = new ToFunction();
        
        assertThrows(TypeMismatchException.class, () -> {
            to.call(env, AviatorNil.NIL, new AviatorString("D"));
        });
    }

    @Test
    void testToWithWrongArgumentCount() {
        ToFunction to = new ToFunction();
        assertThrows(IllegalArgumentException.class, () -> {
            to.call(env, AviatorLong.valueOf(0));
        });
    }

    @Test
    void testToMetadata() {
        ToFunction to = new ToFunction();
        assertEquals("TO", to.getName());
        assertEquals(2, to.getFunctionMetadata().getMinArgs());
        assertEquals(2, to.getFunctionMetadata().getMaxArgs());
    }

    // ========== Integration Tests ==========

    @Test
    void testFromAndToTogether() {
        FromFunction from = new FromFunction();
        ToFunction to = new ToFunction();
        
        Instant now = Instant.parse("2023-01-15T10:00:00Z");
        env.put("now", now);
        
        // Set FROM(30, D)
        from.call(env, AviatorLong.valueOf(30), new AviatorString("D"));
        
        // Set TO(0, D)
        to.call(env, AviatorLong.valueOf(0), new AviatorString("D"));
        
        // Verify complete time range
        TimeRange timeRange = (TimeRange) env.get("timeRange");
        assertNotNull(timeRange);
        assertEquals(30, timeRange.getFromValue());
        assertEquals(TimeUnit.D, timeRange.getFromUnit());
        assertEquals(0, timeRange.getToValue());
        assertEquals(TimeUnit.D, timeRange.getToUnit());
        
        // Verify time range calculations
        Instant startTime = timeRange.getStartTime();
        Instant endTime = timeRange.getEndTime();
        
        // Start should be 30 days before now
        assertEquals(now.minus(30, java.time.temporal.ChronoUnit.DAYS), startTime);
        
        // End should be now (0 days before now)
        assertEquals(now, endTime);
    }

    @Test
    void testActionTimeWithDateFormat() {
        ActionTimeFunction actionTime = new ActionTimeFunction();
        DateFormatFunction dateFormat = new DateFormatFunction();
        
        Event event = Event.builder()
            .timestamp("2023-01-15T10:30:00Z")
            .eventName("purchase")
            .build();
        
        env.put("currentEvent", event);
        
        // Get timestamp from ACTION_TIME
        AviatorObject timestamp = actionTime.call(env);
        
        // Format it with DATE_FORMAT
        AviatorObject formatted = dateFormat.call(env,
            timestamp,
            new AviatorString("yyyy-MM-dd")
        );
        
        assertEquals("2023-01-15", formatted.getValue(env));
    }

    @Test
    void testDateDiffConsistency() {
        DateDiffFunction dateDiff = new DateDiffFunction();
        
        // Test that DATE_DIFF(A, B, H) / 24 ≈ DATE_DIFF(A, B, D)
        String date1 = "2023-01-20T00:00:00Z";
        String date2 = "2023-01-15T00:00:00Z";
        
        AviatorObject diffInHours = dateDiff.call(env,
            new AviatorString(date1),
            new AviatorString(date2),
            new AviatorString("H")
        );
        
        AviatorObject diffInDays = dateDiff.call(env,
            new AviatorString(date1),
            new AviatorString(date2),
            new AviatorString("D")
        );
        
        long hours = (Long) diffInHours.getValue(env);
        long days = (Long) diffInDays.getValue(env);
        
        assertEquals(days * 24, hours);
    }

    // ========== NOW Function Tests ==========

    @Test
    void testNowReturnsCurrentTimestamp() {
        NowFunction now = new NowFunction();
        
        Instant currentTime = Instant.parse("2023-01-15T10:30:00Z");
        env.put("now", currentTime);
        
        AviatorObject result = now.call(env);
        
        assertNotNull(result);
        assertEquals("2023-01-15T10:30:00Z", result.getValue(env));
    }

    @Test
    void testNowWithNoContextUsesActualTime() {
        NowFunction now = new NowFunction();
        
        // Don't set "now" in context - should use actual current time
        AviatorObject result = now.call(env);
        
        assertNotNull(result);
        String timestamp = (String) result.getValue(env);
        assertNotNull(timestamp);
        // Verify it's a valid ISO-8601 timestamp
        assertDoesNotThrow(() -> Instant.parse(timestamp));
    }

    @Test
    void testNowWithWrongArgumentCount() {
        NowFunction now = new NowFunction();
        assertThrows(IllegalArgumentException.class, () -> {
            now.call(env, AviatorLong.valueOf(5));
        });
    }

    @Test
    void testNowMetadata() {
        NowFunction now = new NowFunction();
        assertEquals("NOW", now.getName());
        assertEquals(0, now.getFunctionMetadata().getMinArgs());
        assertEquals(0, now.getFunctionMetadata().getMaxArgs());
    }

    // ========== WEEKDAY Function Tests ==========

    @Test
    void testWeekdayMonday() {
        WeekdayFunction weekday = new WeekdayFunction();
        
        // 2023-01-16 is a Monday
        AviatorObject result = weekday.call(env, new AviatorString("2023-01-16T10:30:00Z"));
        
        assertEquals(1L, result.getValue(env));
    }

    @Test
    void testWeekdaySunday() {
        WeekdayFunction weekday = new WeekdayFunction();
        
        // 2023-01-15 is a Sunday
        AviatorObject result = weekday.call(env, new AviatorString("2023-01-15T10:30:00Z"));
        
        assertEquals(7L, result.getValue(env));
    }

    @Test
    void testWeekdayFriday() {
        WeekdayFunction weekday = new WeekdayFunction();
        
        // 2023-01-20 is a Friday
        AviatorObject result = weekday.call(env, new AviatorString("2023-01-20T10:30:00Z"));
        
        assertEquals(5L, result.getValue(env));
    }

    @Test
    void testWeekdayWithNullTimestamp() {
        WeekdayFunction weekday = new WeekdayFunction();
        
        AviatorObject result = weekday.call(env, AviatorNil.NIL);
        
        assertTrue(result instanceof AviatorNil);
    }

    @Test
    void testWeekdayWithInvalidTimestamp() {
        WeekdayFunction weekday = new WeekdayFunction();
        
        assertThrows(TypeMismatchException.class, () -> {
            weekday.call(env, new AviatorString("invalid-date"));
        });
    }

    @Test
    void testWeekdayMetadata() {
        WeekdayFunction weekday = new WeekdayFunction();
        assertEquals("WEEKDAY", weekday.getName());
        assertEquals(1, weekday.getFunctionMetadata().getMinArgs());
        assertEquals(1, weekday.getFunctionMetadata().getMaxArgs());
    }

    // ========== DAY_OF_MONTH Function Tests ==========

    @Test
    void testDayOfMonthFirstDay() {
        DayOfMonthFunction dayOfMonth = new DayOfMonthFunction();
        
        AviatorObject result = dayOfMonth.call(env, new AviatorString("2023-01-01T10:30:00Z"));
        
        assertEquals(1L, result.getValue(env));
    }

    @Test
    void testDayOfMonthMidMonth() {
        DayOfMonthFunction dayOfMonth = new DayOfMonthFunction();
        
        AviatorObject result = dayOfMonth.call(env, new AviatorString("2023-01-15T10:30:00Z"));
        
        assertEquals(15L, result.getValue(env));
    }

    @Test
    void testDayOfMonthLastDay() {
        DayOfMonthFunction dayOfMonth = new DayOfMonthFunction();
        
        AviatorObject result = dayOfMonth.call(env, new AviatorString("2023-01-31T10:30:00Z"));
        
        assertEquals(31L, result.getValue(env));
    }

    @Test
    void testDayOfMonthWithNullTimestamp() {
        DayOfMonthFunction dayOfMonth = new DayOfMonthFunction();
        
        AviatorObject result = dayOfMonth.call(env, AviatorNil.NIL);
        
        assertTrue(result instanceof AviatorNil);
    }

    @Test
    void testDayOfMonthWithInvalidTimestamp() {
        DayOfMonthFunction dayOfMonth = new DayOfMonthFunction();
        
        assertThrows(TypeMismatchException.class, () -> {
            dayOfMonth.call(env, new AviatorString("invalid-date"));
        });
    }

    @Test
    void testDayOfMonthMetadata() {
        DayOfMonthFunction dayOfMonth = new DayOfMonthFunction();
        assertEquals("DAY_OF_MONTH", dayOfMonth.getName());
        assertEquals(1, dayOfMonth.getFunctionMetadata().getMinArgs());
        assertEquals(1, dayOfMonth.getFunctionMetadata().getMaxArgs());
    }

    // ========== MONTH Function Tests ==========

    @Test
    void testMonthJanuary() {
        MonthFunction month = new MonthFunction();
        
        AviatorObject result = month.call(env, new AviatorString("2023-01-15T10:30:00Z"));
        
        assertEquals(1L, result.getValue(env));
    }

    @Test
    void testMonthJune() {
        MonthFunction month = new MonthFunction();
        
        AviatorObject result = month.call(env, new AviatorString("2023-06-15T10:30:00Z"));
        
        assertEquals(6L, result.getValue(env));
    }

    @Test
    void testMonthDecember() {
        MonthFunction month = new MonthFunction();
        
        AviatorObject result = month.call(env, new AviatorString("2023-12-31T10:30:00Z"));
        
        assertEquals(12L, result.getValue(env));
    }

    @Test
    void testMonthWithNullTimestamp() {
        MonthFunction month = new MonthFunction();
        
        AviatorObject result = month.call(env, AviatorNil.NIL);
        
        assertTrue(result instanceof AviatorNil);
    }

    @Test
    void testMonthWithInvalidTimestamp() {
        MonthFunction month = new MonthFunction();
        
        assertThrows(TypeMismatchException.class, () -> {
            month.call(env, new AviatorString("invalid-date"));
        });
    }

    @Test
    void testMonthMetadata() {
        MonthFunction month = new MonthFunction();
        assertEquals("MONTH", month.getName());
        assertEquals(1, month.getFunctionMetadata().getMinArgs());
        assertEquals(1, month.getFunctionMetadata().getMaxArgs());
    }

    // ========== YEAR Function Tests ==========

    @Test
    void testYear2023() {
        YearFunction year = new YearFunction();
        
        AviatorObject result = year.call(env, new AviatorString("2023-01-15T10:30:00Z"));
        
        assertEquals(2023L, result.getValue(env));
    }

    @Test
    void testYear2024() {
        YearFunction year = new YearFunction();
        
        AviatorObject result = year.call(env, new AviatorString("2024-06-15T10:30:00Z"));
        
        assertEquals(2024L, result.getValue(env));
    }

    @Test
    void testYear2022() {
        YearFunction year = new YearFunction();
        
        AviatorObject result = year.call(env, new AviatorString("2022-12-31T10:30:00Z"));
        
        assertEquals(2022L, result.getValue(env));
    }

    @Test
    void testYearWithNullTimestamp() {
        YearFunction year = new YearFunction();
        
        AviatorObject result = year.call(env, AviatorNil.NIL);
        
        assertTrue(result instanceof AviatorNil);
    }

    @Test
    void testYearWithInvalidTimestamp() {
        YearFunction year = new YearFunction();
        
        assertThrows(TypeMismatchException.class, () -> {
            year.call(env, new AviatorString("invalid-date"));
        });
    }

    @Test
    void testYearMetadata() {
        YearFunction year = new YearFunction();
        assertEquals("YEAR", year.getName());
        assertEquals(1, year.getFunctionMetadata().getMinArgs());
        assertEquals(1, year.getFunctionMetadata().getMaxArgs());
    }

    // ========== IN_RECENT_DAYS Function Tests ==========

    @Test
    void testInRecentDaysFiltersCorrectly() {
        InRecentDaysFunction inRecentDays = new InRecentDaysFunction();
        
        Instant now = Instant.parse("2023-01-20T10:00:00Z");
        env.put("now", now);
        
        // Create user data with events
        UserData userData = new UserData();
        List<Event> events = new ArrayList<>();
        
        // Event from 5 days ago (should be included)
        events.add(Event.builder()
            .eventName("purchase")
            .timestamp("2023-01-15T10:00:00Z")
            .build());
        
        // Event from 10 days ago (should not be included)
        events.add(Event.builder()
            .eventName("view")
            .timestamp("2023-01-10T10:00:00Z")
            .build());
        
        // Event from 2 days ago (should be included)
        events.add(Event.builder()
            .eventName("click")
            .timestamp("2023-01-18T10:00:00Z")
            .build());
        
        userData.setEvents(events);
        env.put("userData", userData);
        
        // Filter events from past 7 days
        AviatorObject result = inRecentDays.call(env, AviatorLong.valueOf(7));
        
        @SuppressWarnings("unchecked")
        List<Event> filteredEvents = (List<Event>) result.getValue(env);
        
        assertEquals(2, filteredEvents.size());
        assertTrue(filteredEvents.stream().anyMatch(e -> "purchase".equals(e.getEventName())));
        assertTrue(filteredEvents.stream().anyMatch(e -> "click".equals(e.getEventName())));
        assertFalse(filteredEvents.stream().anyMatch(e -> "view".equals(e.getEventName())));
    }

    @Test
    void testInRecentDaysWithZeroDays() {
        InRecentDaysFunction inRecentDays = new InRecentDaysFunction();
        
        Instant now = Instant.parse("2023-01-20T10:00:00Z");
        env.put("now", now);
        
        UserData userData = new UserData();
        List<Event> events = new ArrayList<>();
        
        // Event at exact "now" time
        events.add(Event.builder()
            .eventName("purchase")
            .timestamp("2023-01-20T10:00:00Z")
            .build());
        
        // Event from 1 hour ago (should still be included in "past 0 days" since it's the same day)
        events.add(Event.builder()
            .eventName("view")
            .timestamp("2023-01-20T09:00:00Z")
            .build());
        
        // Event from yesterday (should not be included)
        events.add(Event.builder()
            .eventName("click")
            .timestamp("2023-01-19T10:00:00Z")
            .build());
        
        userData.setEvents(events);
        env.put("userData", userData);
        
        // Filter events from past 0 days (should include events from today only)
        AviatorObject result = inRecentDays.call(env, AviatorLong.valueOf(0));
        
        @SuppressWarnings("unchecked")
        List<Event> filteredEvents = (List<Event>) result.getValue(env);
        
        // With 0 days, we expect events within the same instant or very recent
        // The event at exact "now" should be included, but the one from 1 hour ago might not
        assertEquals(1, filteredEvents.size());
        assertTrue(filteredEvents.stream().anyMatch(e -> "purchase".equals(e.getEventName())));
    }

    @Test
    void testInRecentDaysWithNoEvents() {
        InRecentDaysFunction inRecentDays = new InRecentDaysFunction();
        
        Instant now = Instant.parse("2023-01-20T10:00:00Z");
        env.put("now", now);
        
        UserData userData = new UserData();
        userData.setEvents(new ArrayList<>());
        env.put("userData", userData);
        
        AviatorObject result = inRecentDays.call(env, AviatorLong.valueOf(7));
        
        @SuppressWarnings("unchecked")
        List<Event> filteredEvents = (List<Event>) result.getValue(env);
        
        assertEquals(0, filteredEvents.size());
    }

    @Test
    void testInRecentDaysWithNegativeDays() {
        InRecentDaysFunction inRecentDays = new InRecentDaysFunction();
        
        assertThrows(TypeMismatchException.class, () -> {
            inRecentDays.call(env, AviatorLong.valueOf(-5));
        });
    }

    @Test
    void testInRecentDaysMetadata() {
        InRecentDaysFunction inRecentDays = new InRecentDaysFunction();
        assertEquals("IN_RECENT_DAYS", inRecentDays.getName());
        assertEquals(1, inRecentDays.getFunctionMetadata().getMinArgs());
        assertEquals(1, inRecentDays.getFunctionMetadata().getMaxArgs());
    }

    // ========== IS_RECURRING Function Tests ==========

    @Test
    void testIsRecurringReturnsTrueWhenThresholdMet() {
        IsRecurringFunction isRecurring = new IsRecurringFunction();
        
        Instant now = Instant.parse("2023-01-20T10:00:00Z");
        env.put("now", now);
        
        // Create user data with recurring purchases
        UserData userData = new UserData();
        List<Event> events = new ArrayList<>();
        
        // 4 purchase events in the past 30 days
        events.add(Event.builder().eventName("purchase").timestamp("2023-01-18T10:00:00Z").build());
        events.add(Event.builder().eventName("purchase").timestamp("2023-01-15T10:00:00Z").build());
        events.add(Event.builder().eventName("purchase").timestamp("2023-01-10T10:00:00Z").build());
        events.add(Event.builder().eventName("purchase").timestamp("2023-01-05T10:00:00Z").build());
        
        // Some other events
        events.add(Event.builder().eventName("view").timestamp("2023-01-19T10:00:00Z").build());
        
        userData.setEvents(events);
        env.put("userData", userData);
        
        // Check if user made 3+ purchases in past 30 days
        AviatorObject result = isRecurring.call(env,
            new AviatorString("purchase"),
            AviatorLong.valueOf(3),
            AviatorLong.valueOf(30));
        
        assertEquals(true, result.getValue(env));
    }

    @Test
    void testIsRecurringReturnsFalseWhenThresholdNotMet() {
        IsRecurringFunction isRecurring = new IsRecurringFunction();
        
        Instant now = Instant.parse("2023-01-20T10:00:00Z");
        env.put("now", now);
        
        UserData userData = new UserData();
        List<Event> events = new ArrayList<>();
        
        // Only 2 purchase events in the past 30 days
        events.add(Event.builder().eventName("purchase").timestamp("2023-01-18T10:00:00Z").build());
        events.add(Event.builder().eventName("purchase").timestamp("2023-01-15T10:00:00Z").build());
        
        userData.setEvents(events);
        env.put("userData", userData);
        
        // Check if user made 3+ purchases in past 30 days (should be false)
        AviatorObject result = isRecurring.call(env,
            new AviatorString("purchase"),
            AviatorLong.valueOf(3),
            AviatorLong.valueOf(30));
        
        assertEquals(false, result.getValue(env));
    }

    @Test
    void testIsRecurringIgnoresEventsOutsideTimeWindow() {
        IsRecurringFunction isRecurring = new IsRecurringFunction();
        
        Instant now = Instant.parse("2023-01-20T10:00:00Z");
        env.put("now", now);
        
        UserData userData = new UserData();
        List<Event> events = new ArrayList<>();
        
        // 2 purchases in past 7 days
        events.add(Event.builder().eventName("purchase").timestamp("2023-01-18T10:00:00Z").build());
        events.add(Event.builder().eventName("purchase").timestamp("2023-01-15T10:00:00Z").build());
        
        // 2 purchases outside 7-day window
        events.add(Event.builder().eventName("purchase").timestamp("2023-01-10T10:00:00Z").build());
        events.add(Event.builder().eventName("purchase").timestamp("2023-01-05T10:00:00Z").build());
        
        userData.setEvents(events);
        env.put("userData", userData);
        
        // Check if user made 3+ purchases in past 7 days (should be false)
        AviatorObject result = isRecurring.call(env,
            new AviatorString("purchase"),
            AviatorLong.valueOf(3),
            AviatorLong.valueOf(7));
        
        assertEquals(false, result.getValue(env));
    }

    @Test
    void testIsRecurringWithDifferentEventNames() {
        IsRecurringFunction isRecurring = new IsRecurringFunction();
        
        Instant now = Instant.parse("2023-01-20T10:00:00Z");
        env.put("now", now);
        
        UserData userData = new UserData();
        List<Event> events = new ArrayList<>();
        
        // Mix of different events
        events.add(Event.builder().eventName("purchase").timestamp("2023-01-18T10:00:00Z").build());
        events.add(Event.builder().eventName("view").timestamp("2023-01-17T10:00:00Z").build());
        events.add(Event.builder().eventName("purchase").timestamp("2023-01-15T10:00:00Z").build());
        events.add(Event.builder().eventName("click").timestamp("2023-01-14T10:00:00Z").build());
        
        userData.setEvents(events);
        env.put("userData", userData);
        
        // Check if user made 3+ purchases (should be false - only 2 purchases)
        AviatorObject result = isRecurring.call(env,
            new AviatorString("purchase"),
            AviatorLong.valueOf(3),
            AviatorLong.valueOf(30));
        
        assertEquals(false, result.getValue(env));
    }

    @Test
    void testIsRecurringWithNoEvents() {
        IsRecurringFunction isRecurring = new IsRecurringFunction();
        
        Instant now = Instant.parse("2023-01-20T10:00:00Z");
        env.put("now", now);
        
        UserData userData = new UserData();
        userData.setEvents(new ArrayList<>());
        env.put("userData", userData);
        
        AviatorObject result = isRecurring.call(env,
            new AviatorString("purchase"),
            AviatorLong.valueOf(3),
            AviatorLong.valueOf(30));
        
        assertEquals(false, result.getValue(env));
    }

    @Test
    void testIsRecurringWithNegativeMinCount() {
        IsRecurringFunction isRecurring = new IsRecurringFunction();
        
        assertThrows(TypeMismatchException.class, () -> {
            isRecurring.call(env,
                new AviatorString("purchase"),
                AviatorLong.valueOf(-1),
                AviatorLong.valueOf(30));
        });
    }

    @Test
    void testIsRecurringWithNegativeTimeWindow() {
        IsRecurringFunction isRecurring = new IsRecurringFunction();
        
        assertThrows(TypeMismatchException.class, () -> {
            isRecurring.call(env,
                new AviatorString("purchase"),
                AviatorLong.valueOf(3),
                AviatorLong.valueOf(-30));
        });
    }

    @Test
    void testIsRecurringMetadata() {
        IsRecurringFunction isRecurring = new IsRecurringFunction();
        assertEquals("IS_RECURRING", isRecurring.getName());
        assertEquals(3, isRecurring.getFunctionMetadata().getMinArgs());
        assertEquals(3, isRecurring.getFunctionMetadata().getMaxArgs());
    }
}

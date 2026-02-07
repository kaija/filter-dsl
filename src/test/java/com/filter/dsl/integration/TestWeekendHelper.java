package com.filter.dsl.integration;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class TestWeekendHelper {
    public static void main(String[] args) {
        Instant now = Instant.now();
        System.out.println("Now: " + now);
        
        DayOfWeek currentDay = now.atZone(ZoneId.systemDefault()).getDayOfWeek();
        System.out.println("Current day: " + currentDay + " (value=" + currentDay.getValue() + ")");
        
        int currentDayValue = currentDay.getValue();
        int daysUntilSaturday = (6 - currentDayValue + 7) % 7;
        if (daysUntilSaturday == 0) daysUntilSaturday = 7;
        
        System.out.println("Days until Saturday: " + daysUntilSaturday);
        
        Instant saturday = now.plus(daysUntilSaturday, ChronoUnit.DAYS);
        System.out.println("Saturday: " + saturday);
        
        DayOfWeek saturdayDay = saturday.atZone(ZoneId.systemDefault()).getDayOfWeek();
        System.out.println("Saturday day: " + saturdayDay + " (value=" + saturdayDay.getValue() + ")");
    }
}

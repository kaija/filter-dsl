package com.filter.dsl.functions.conversion;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.filter.dsl.functions.TypeMismatchException;
import com.filter.dsl.functions.FunctionArgumentException;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorDouble;

import java.util.Map;
import java.util.HashMap;

/**
 * CONVERT_UNIT function - Converts values between different units of measurement.
 * 
 * Usage: CONVERT_UNIT(value, from_unit, to_unit)
 * 
 * Supported unit categories:
 * 
 * **Time Units:**
 * - seconds, minutes, hours, days, weeks, months, years
 * 
 * **Distance Units:**
 * - meters, kilometers, miles, feet
 * 
 * **Weight Units:**
 * - grams, kilograms, pounds, ounces
 * 
 * Examples:
 * - CONVERT_UNIT(60, "seconds", "minutes") -> 1.0
 * - CONVERT_UNIT(1, "hours", "minutes") -> 60.0
 * - CONVERT_UNIT(1000, "meters", "kilometers") -> 1.0
 * - CONVERT_UNIT(1, "miles", "kilometers") -> 1.60934
 * - CONVERT_UNIT(1000, "grams", "kilograms") -> 1.0
 * - CONVERT_UNIT(1, "pounds", "ounces") -> 16.0
 * - CONVERT_UNIT(365, "days", "years") -> 1.0
 * - CONVERT_UNIT(2, "weeks", "days") -> 14.0
 * 
 * Error cases:
 * - Unknown units -> Error
 * - Incompatible unit categories (e.g., converting time to distance) -> Error
 * - Non-numeric value -> Error
 * - Null value -> Error
 * 
 * Requirements: 10.4, 10.5, 10.6, 10.8
 */
public class ConvertUnitFunction extends DSLFunction {

    // Conversion factors to base units
    // Time: base unit is seconds
    private static final Map<String, Double> TIME_UNITS = new HashMap<>();
    // Distance: base unit is meters
    private static final Map<String, Double> DISTANCE_UNITS = new HashMap<>();
    // Weight: base unit is grams
    private static final Map<String, Double> WEIGHT_UNITS = new HashMap<>();
    
    static {
        // Time conversions (to seconds)
        TIME_UNITS.put("seconds", 1.0);
        TIME_UNITS.put("second", 1.0);
        TIME_UNITS.put("minutes", 60.0);
        TIME_UNITS.put("minute", 60.0);
        TIME_UNITS.put("hours", 3600.0);
        TIME_UNITS.put("hour", 3600.0);
        TIME_UNITS.put("days", 86400.0);
        TIME_UNITS.put("day", 86400.0);
        TIME_UNITS.put("weeks", 604800.0);
        TIME_UNITS.put("week", 604800.0);
        TIME_UNITS.put("months", 2592000.0); // 30 days
        TIME_UNITS.put("month", 2592000.0);
        TIME_UNITS.put("years", 31536000.0); // 365 days
        TIME_UNITS.put("year", 31536000.0);
        
        // Distance conversions (to meters)
        DISTANCE_UNITS.put("meters", 1.0);
        DISTANCE_UNITS.put("meter", 1.0);
        DISTANCE_UNITS.put("m", 1.0);
        DISTANCE_UNITS.put("kilometers", 1000.0);
        DISTANCE_UNITS.put("kilometer", 1000.0);
        DISTANCE_UNITS.put("km", 1000.0);
        DISTANCE_UNITS.put("miles", 1609.34);
        DISTANCE_UNITS.put("mile", 1609.34);
        DISTANCE_UNITS.put("feet", 0.3048);
        DISTANCE_UNITS.put("foot", 0.3048);
        DISTANCE_UNITS.put("ft", 0.3048);
        
        // Weight conversions (to grams)
        WEIGHT_UNITS.put("grams", 1.0);
        WEIGHT_UNITS.put("gram", 1.0);
        WEIGHT_UNITS.put("g", 1.0);
        WEIGHT_UNITS.put("kilograms", 1000.0);
        WEIGHT_UNITS.put("kilogram", 1000.0);
        WEIGHT_UNITS.put("kg", 1000.0);
        WEIGHT_UNITS.put("pounds", 453.592);
        WEIGHT_UNITS.put("pound", 453.592);
        WEIGHT_UNITS.put("lb", 453.592);
        WEIGHT_UNITS.put("lbs", 453.592);
        WEIGHT_UNITS.put("ounces", 28.3495);
        WEIGHT_UNITS.put("ounce", 28.3495);
        WEIGHT_UNITS.put("oz", 28.3495);
    }

    @Override
    public String getName() {
        return "CONVERT_UNIT";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("CONVERT_UNIT")
            .minArgs(3)
            .maxArgs(3)
            .argumentType(0, ArgumentType.NUMBER)
            .argumentType(1, ArgumentType.STRING)
            .argumentType(2, ArgumentType.STRING)
            .returnType(ReturnType.NUMBER)
            .description("Converts values between different units of measurement")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 3);
        
        // Get the value to convert
        Object valueObj = getValue(args[0], env);
        if (valueObj == null) {
            throw new TypeMismatchException(
                "CONVERT_UNIT cannot convert null value"
            );
        }
        
        if (!(valueObj instanceof Number)) {
            throw new TypeMismatchException(
                "CONVERT_UNIT expects a numeric value, got " + valueObj.getClass().getSimpleName()
            );
        }
        
        double value = ((Number) valueObj).doubleValue();
        
        // Get the from and to units
        Object fromUnitObj = getValue(args[1], env);
        Object toUnitObj = getValue(args[2], env);
        
        if (fromUnitObj == null || toUnitObj == null) {
            throw new FunctionArgumentException(
                "CONVERT_UNIT requires non-null unit names"
            );
        }
        
        String fromUnit = fromUnitObj.toString().toLowerCase().trim();
        String toUnit = toUnitObj.toString().toLowerCase().trim();
        
        // Determine which unit category and perform conversion
        UnitCategory category = determineUnitCategory(fromUnit, toUnit);
        
        double result = convertValue(value, fromUnit, toUnit, category);
        
        return AviatorDouble.valueOf(result);
    }

    /**
     * Determine which unit category the units belong to.
     * 
     * @param fromUnit The source unit
     * @param toUnit The target unit
     * @return The unit category
     * @throws FunctionArgumentException if units are unknown or incompatible
     */
    private UnitCategory determineUnitCategory(String fromUnit, String toUnit) {
        boolean fromIsTime = TIME_UNITS.containsKey(fromUnit);
        boolean toIsTime = TIME_UNITS.containsKey(toUnit);
        boolean fromIsDistance = DISTANCE_UNITS.containsKey(fromUnit);
        boolean toIsDistance = DISTANCE_UNITS.containsKey(toUnit);
        boolean fromIsWeight = WEIGHT_UNITS.containsKey(fromUnit);
        boolean toIsWeight = WEIGHT_UNITS.containsKey(toUnit);
        
        // Check if both units are in the same category
        if (fromIsTime && toIsTime) {
            return UnitCategory.TIME;
        } else if (fromIsDistance && toIsDistance) {
            return UnitCategory.DISTANCE;
        } else if (fromIsWeight && toIsWeight) {
            return UnitCategory.WEIGHT;
        }
        
        // Check if units are unknown
        if (!fromIsTime && !fromIsDistance && !fromIsWeight) {
            throw new FunctionArgumentException(
                "CONVERT_UNIT: Unknown unit '" + fromUnit + "'. " +
                "Supported units: time (seconds, minutes, hours, days, weeks, months, years), " +
                "distance (meters, kilometers, miles, feet), " +
                "weight (grams, kilograms, pounds, ounces)"
            );
        }
        
        if (!toIsTime && !toIsDistance && !toIsWeight) {
            throw new FunctionArgumentException(
                "CONVERT_UNIT: Unknown unit '" + toUnit + "'. " +
                "Supported units: time (seconds, minutes, hours, days, weeks, months, years), " +
                "distance (meters, kilometers, miles, feet), " +
                "weight (grams, kilograms, pounds, ounces)"
            );
        }
        
        // Units are from different categories
        throw new FunctionArgumentException(
            "CONVERT_UNIT: Cannot convert between incompatible unit types. " +
            "'" + fromUnit + "' and '" + toUnit + "' are from different categories."
        );
    }

    /**
     * Convert a value from one unit to another within the same category.
     * 
     * @param value The value to convert
     * @param fromUnit The source unit
     * @param toUnit The target unit
     * @param category The unit category
     * @return The converted value
     */
    private double convertValue(double value, String fromUnit, String toUnit, UnitCategory category) {
        Map<String, Double> conversionTable;
        
        switch (category) {
            case TIME:
                conversionTable = TIME_UNITS;
                break;
            case DISTANCE:
                conversionTable = DISTANCE_UNITS;
                break;
            case WEIGHT:
                conversionTable = WEIGHT_UNITS;
                break;
            default:
                throw new IllegalStateException("Unknown unit category: " + category);
        }
        
        // Convert to base unit, then to target unit
        double fromFactor = conversionTable.get(fromUnit);
        double toFactor = conversionTable.get(toUnit);
        
        double baseValue = value * fromFactor;
        double result = baseValue / toFactor;
        
        return result;
    }

    /**
     * Enum representing unit categories.
     */
    private enum UnitCategory {
        TIME,
        DISTANCE,
        WEIGHT
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2, AviatorObject arg3) {
        return call(env, new AviatorObject[]{arg1, arg2, arg3});
    }
}

package com.filter.dsl.functions.segmentation;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.filter.dsl.models.BucketDefinition;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import com.googlecode.aviator.runtime.type.AviatorNil;

import java.util.Map;

/**
 * BUCKET function - Assigns a value to a bucket based on range definitions.
 * 
 * Usage: BUCKET(value, bucketDefinition)
 * 
 * The function accepts a numeric value and a BucketDefinition object,
 * then returns the label of the first matching bucket range.
 * If no range matches, returns the default label (or null if no default is set).
 * 
 * Boundary inclusivity is handled according to each range's configuration:
 * - minInclusive: true means value >= minValue, false means value > minValue
 * - maxInclusive: true means value <= maxValue, false means value < maxValue
 * 
 * Examples:
 * - BUCKET(25, bucketDef) -> "Young" (if 25 falls in [18, 30) range)
 * - BUCKET(150, bucketDef) -> "High" (if 150 falls in [100, 500) range)
 * - BUCKET(9999, bucketDef) -> "Other" (if no range matches and default is "Other")
 * 
 * Requirements: 9.1, 9.2, 9.3, 9.4, 16.5
 */
public class BucketFunction extends DSLFunction {

    @Override
    public String getName() {
        return "BUCKET";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("BUCKET")
            .minArgs(2)
            .maxArgs(2)
            .argumentType(0, ArgumentType.NUMBER)
            .argumentType(1, ArgumentType.ANY)
            .returnType(ReturnType.STRING)
            .description("Assigns a value to a bucket based on range definitions")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 2);
        
        // Get the value to bucket
        Object valueObj = getValue(args[0], env);
        if (valueObj == null) {
            return AviatorNil.NIL;
        }
        
        // Convert to Double
        Double value;
        if (valueObj instanceof Number) {
            value = ((Number) valueObj).doubleValue();
        } else {
            throw new com.filter.dsl.functions.TypeMismatchException(
                "BUCKET expects a numeric value as first argument, got " + valueObj.getClass().getSimpleName()
            );
        }
        
        // Get the bucket definition
        Object bucketDefObj = getValue(args[1], env);
        if (bucketDefObj == null) {
            throw new com.filter.dsl.functions.FunctionArgumentException(
                "BUCKET requires a BucketDefinition as second argument, got null"
            );
        }
        
        if (!(bucketDefObj instanceof BucketDefinition)) {
            throw new com.filter.dsl.functions.TypeMismatchException(
                "BUCKET expects a BucketDefinition as second argument, got " + bucketDefObj.getClass().getSimpleName()
            );
        }
        
        BucketDefinition bucketDef = (BucketDefinition) bucketDefObj;
        
        // Find the matching bucket label
        String label = bucketDef.getBucketLabel(value);
        
        // Return the label or null if no match
        if (label == null) {
            return AviatorNil.NIL;
        }
        
        return new AviatorString(label);
    }
    
    // Override the two-argument call method for AviatorScript compatibility
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        return call(env, new AviatorObject[]{arg1, arg2});
    }
}

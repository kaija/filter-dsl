package com.filter.dsl.functions.aggregation;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorLong;

import java.util.Collection;
import java.util.Map;

/**
 * COUNT function - Returns the number of items in a collection.
 * 
 * Usage: COUNT(collection)
 * 
 * Examples:
 * - COUNT(events) -> 10
 * - COUNT([]) -> 0
 * - COUNT(null) -> 0
 */
public class CountFunction extends DSLFunction {

    @Override
    public String getName() {
        return "COUNT";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("COUNT")
            .minArgs(1)
            .maxArgs(1)
            .argumentType(0, ArgumentType.COLLECTION)
            .returnType(ReturnType.NUMBER)
            .description("Returns the number of items in a collection")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 1);
        
        Object value = getValue(args[0], env);
        
        // Handle null
        if (value == null) {
            return AviatorLong.valueOf(0);
        }
        
        // Handle collection
        if (value instanceof Collection) {
            return AviatorLong.valueOf(((Collection<?>) value).size());
        }
        
        // Handle array
        if (value.getClass().isArray()) {
            return AviatorLong.valueOf(java.lang.reflect.Array.getLength(value));
        }
        
        throw new com.filter.dsl.functions.TypeMismatchException(
            "COUNT expects a collection or array, got " + value.getClass().getSimpleName()
        );
    }
    
    // Override the single-argument call method for AviatorScript compatibility
    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        return call(env, new AviatorObject[]{arg1});
    }
}

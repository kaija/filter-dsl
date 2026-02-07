package com.filter.dsl.functions.datetime;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.filter.dsl.models.Event;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import com.googlecode.aviator.runtime.type.AviatorNil;

import java.util.Map;

/**
 * ACTION_TIME function - Returns the timestamp of the current event being evaluated.
 * 
 * Usage: ACTION_TIME()
 * 
 * Examples:
 * - ACTION_TIME() -> "2023-01-15T10:30:00Z"
 * 
 * This function is typically used within event filtering contexts to access
 * the timestamp of the event being processed.
 */
public class ActionTimeFunction extends DSLFunction {

    @Override
    public String getName() {
        return "ACTION_TIME";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("ACTION_TIME")
            .minArgs(0)
            .maxArgs(0)
            .returnType(ReturnType.STRING)
            .description("Returns the timestamp of the current event being evaluated")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 0);
        
        // Get the current event from context
        Event currentEvent = getCurrentEvent(env);
        
        if (currentEvent == null) {
            // No current event in context
            return AviatorNil.NIL;
        }
        
        String timestamp = currentEvent.getTimestamp();
        
        if (timestamp == null) {
            return AviatorNil.NIL;
        }
        
        return new AviatorString(timestamp);
    }
    
    // Override the no-argument call method for AviatorScript compatibility
    @Override
    public AviatorObject call(Map<String, Object> env) {
        return call(env, new AviatorObject[0]);
    }
}

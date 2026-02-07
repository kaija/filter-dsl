package com.filter.dsl.functions.string;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorBoolean;

import java.util.Map;

/**
 * CONTAINS function - Returns true if the string contains the substring.
 * 
 * Usage: CONTAINS(string, substring)
 * 
 * Examples:
 * - CONTAINS("hello world", "world") -> true
 * - CONTAINS("hello world", "foo") -> false
 * - CONTAINS("hello", "hello") -> true
 * - CONTAINS("", "test") -> false
 * - CONTAINS("test", "") -> true (empty string is contained in any string)
 * 
 * The function performs case-sensitive substring matching.
 * Null inputs are handled gracefully - returns false if either argument is null.
 */
public class ContainsFunction extends DSLFunction {

    @Override
    public String getName() {
        return "CONTAINS";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("CONTAINS")
            .minArgs(2)
            .maxArgs(2)
            .argumentType(0, ArgumentType.STRING)
            .argumentType(1, ArgumentType.STRING)
            .returnType(ReturnType.BOOLEAN)
            .description("Returns true if the string contains the substring")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        validateArgCount(new AviatorObject[]{arg1, arg2}, 2);
        
        // Convert arguments to strings
        String str = toString(arg1, env);
        String substring = toString(arg2, env);
        
        // Handle null inputs gracefully
        if (str == null || substring == null) {
            return AviatorBoolean.FALSE;
        }
        
        // Check if string contains substring (case-sensitive)
        boolean result = str.contains(substring);
        
        return result ? AviatorBoolean.TRUE : AviatorBoolean.FALSE;
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 2);
        return call(env, args[0], args[1]);
    }
}

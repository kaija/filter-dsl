package com.filter.dsl.functions.string;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.exception.ExpressionRuntimeException;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * REGEX_MATCH function - Returns true if the string matches the regex pattern.
 * 
 * Usage: REGEX_MATCH(string, pattern)
 * 
 * Examples:
 * - REGEX_MATCH("hello123", "\\w+") -> true
 * - REGEX_MATCH("test@example.com", "^[\\w.]+@[\\w.]+$") -> true
 * - REGEX_MATCH("hello", "^[0-9]+$") -> false
 * - REGEX_MATCH("abc123", ".*\\d+.*") -> true
 * 
 * The function performs case-sensitive regex matching using Java's Pattern class.
 * Invalid regex patterns will throw an error with a descriptive message.
 * Null inputs are handled gracefully - returns false if either argument is null.
 */
public class RegexMatchFunction extends DSLFunction {

    @Override
    public String getName() {
        return "REGEX_MATCH";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("REGEX_MATCH")
            .minArgs(2)
            .maxArgs(2)
            .argumentType(0, ArgumentType.STRING)
            .argumentType(1, ArgumentType.STRING)
            .returnType(ReturnType.BOOLEAN)
            .description("Returns true if the string matches the regex pattern")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        validateArgCount(new AviatorObject[]{arg1, arg2}, 2);
        
        // Convert arguments to strings
        String str = toString(arg1, env);
        String patternStr = toString(arg2, env);
        
        // Handle null inputs gracefully
        if (str == null || patternStr == null) {
            return AviatorBoolean.FALSE;
        }
        
        try {
            // Compile the regex pattern and check if string matches
            Pattern pattern = Pattern.compile(patternStr);
            boolean result = pattern.matcher(str).matches();
            
            return result ? AviatorBoolean.TRUE : AviatorBoolean.FALSE;
        } catch (PatternSyntaxException e) {
            // Handle invalid regex patterns with a descriptive error
            throw new ExpressionRuntimeException(
                "Invalid regex pattern in REGEX_MATCH: " + e.getMessage()
            );
        }
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 2);
        return call(env, args[0], args[1]);
    }
}

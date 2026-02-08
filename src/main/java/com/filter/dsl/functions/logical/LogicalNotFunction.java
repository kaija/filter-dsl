package com.filter.dsl.functions.logical;

import com.filter.dsl.functions.DSLFunction;
import com.filter.dsl.functions.FunctionArgumentException;
import com.filter.dsl.functions.FunctionMetadata;
import com.filter.dsl.functions.FunctionMetadata.ArgumentType;
import com.filter.dsl.functions.FunctionMetadata.ReturnType;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorBoolean;

import java.util.Map;

/**
 * NOT function - Returns the logical negation of the operand.
 *
 * Usage: NOT(expr)
 *
 * Examples:
 * - NOT(true) -> false
 * - NOT(false) -> true
 * - NOT(NOT(true)) -> true
 */
public class LogicalNotFunction extends DSLFunction {

    @Override
    public String getName() {
        return "NOT";
    }

    @Override
    public FunctionMetadata getFunctionMetadata() {
        return FunctionMetadata.builder()
            .name("NOT")
            .minArgs(1)
            .maxArgs(1)
            .argumentType(0, ArgumentType.BOOLEAN)
            .returnType(ReturnType.BOOLEAN)
            .description("Returns the logical negation of the operand")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        Boolean value = toBoolean(arg1, env);
        return value ? AviatorBoolean.FALSE : AviatorBoolean.TRUE;
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        throw new FunctionArgumentException(getName() + " expects 1 argument, got 2");
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        validateArgCount(args, 1);

        Boolean value = toBoolean(args[0], env);
        return value ? AviatorBoolean.FALSE : AviatorBoolean.TRUE;
    }
}

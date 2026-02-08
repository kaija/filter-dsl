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
 * Usage: 
 * - COUNT() -> Count all events from userData.events
 * - COUNT("condition") -> Count events matching the condition
 * - COUNT(collection) -> Count items in the provided collection (legacy)
 * - COUNT(collection, "condition") -> Count items in collection matching condition (legacy)
 *
 * Examples:
 * - COUNT() -> 10 (all events)
 * - COUNT("EQ(EVENT(\"eventType\"), \"action\")") -> 5 (filtered events)
 * - COUNT("IN_RECENT_DAYS(30)") -> 8 (recent events)
 * - COUNT([1,2,3]) -> 3 (legacy syntax)
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
            .minArgs(0)
            .maxArgs(2)
            .argumentType(0, ArgumentType.ANY)
            .argumentType(1, ArgumentType.STRING)
            .returnType(ReturnType.NUMBER)
            .description("Returns the number of items. Defaults to userData.events with optional filter condition.")
            .build();
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject... args) {
        // No arguments: COUNT() -> count all events
        if (args.length == 0) {
            Collection<?> events = getUserDataEvents(env);
            return AviatorLong.valueOf(events.size());
        }

        Object firstArg = getValue(args[0], env);

        // Single string argument: COUNT("condition") -> filter and count events
        if (args.length == 1 && firstArg instanceof String) {
            Collection<?> events = getUserDataEvents(env);
            Collection<?> filtered = filterCollection(events, (String) firstArg, env);
            return AviatorLong.valueOf(filtered.size());
        }

        // Single collection argument: COUNT(collection) -> legacy syntax
        if (args.length == 1) {
            if (firstArg == null) {
                return AviatorLong.valueOf(0);
            }

            if (firstArg instanceof Collection) {
                return AviatorLong.valueOf(((Collection<?>) firstArg).size());
            }

            if (firstArg.getClass().isArray()) {
                return AviatorLong.valueOf(java.lang.reflect.Array.getLength(firstArg));
            }

            throw new com.filter.dsl.functions.TypeMismatchException(
                "COUNT expects a collection, array, or filter condition, got " + firstArg.getClass().getSimpleName()
            );
        }

        // Two arguments: COUNT(collection, "condition") -> legacy syntax with filter
        if (args.length == 2) {
            Object conditionObj = getValue(args[1], env);
            if (!(conditionObj instanceof String)) {
                throw new com.filter.dsl.functions.TypeMismatchException(
                    "COUNT condition must be a string expression"
                );
            }

            Collection<?> collection = toCollection(firstArg);
            Collection<?> filtered = filterCollection(collection, (String) conditionObj, env);
            return AviatorLong.valueOf(filtered.size());
        }

        throw new com.filter.dsl.functions.FunctionArgumentException(
            "COUNT expects 0-2 arguments, got " + args.length
        );
    }

    // Override for AviatorScript compatibility
    @Override
    public AviatorObject call(Map<String, Object> env) {
        return call(env, new AviatorObject[]{});
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1) {
        return call(env, new AviatorObject[]{arg1});
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        return call(env, new AviatorObject[]{arg1, arg2});
    }
}

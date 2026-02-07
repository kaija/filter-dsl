package com.filter.dsl.unit.parser;

import com.filter.dsl.functions.*;
import com.filter.dsl.functions.aggregation.CountFunction;
import com.filter.dsl.functions.comparison.EqualsFunction;
import com.filter.dsl.functions.comparison.GreaterThanFunction;
import com.filter.dsl.functions.data.EventFunction;
import com.filter.dsl.functions.data.ProfileFunction;
import com.filter.dsl.functions.filtering.WhereFunction;
import com.filter.dsl.functions.logical.LogicalAndFunction;
import com.filter.dsl.parser.DSLParser;
import com.filter.dsl.parser.DSLParserImpl;
import com.filter.dsl.parser.ParseResult;
import com.filter.dsl.parser.PrettyPrintConfig;

public class DebugPrettyPrinter {
    public static void main(String[] args) {
        FunctionRegistry registry = new FunctionRegistry();
        
        // Register all necessary functions
        registry.register(new LogicalAndFunction());
        registry.register(new GreaterThanFunction());
        registry.register(new EqualsFunction());
        registry.register(new CountFunction());
        registry.register(new WhereFunction());
        registry.register(new EventFunction());
        registry.register(new ProfileFunction());
        
        DSLParser parser = new DSLParserImpl(registry);
        
        String expression = "AND(GT(COUNT(WHERE(EQ(EVENT(\"event_name\"),\"purchase\"))),5),EQ(PROFILE(\"country\"),\"US\"))";
        
        System.out.println("Original:");
        System.out.println(expression);
        System.out.println();
        
        String formatted = parser.prettyPrint(expression, PrettyPrintConfig.DEFAULT);
        System.out.println("Formatted:");
        System.out.println(formatted);
        System.out.println();
        
        ParseResult result = parser.parse(formatted);
        System.out.println("Parse result: " + result.isValid());
        if (!result.isValid()) {
            System.out.println("Error: " + result.getErrorMessage());
            System.out.println("Position: " + result.getErrorPosition());
        }
    }
}

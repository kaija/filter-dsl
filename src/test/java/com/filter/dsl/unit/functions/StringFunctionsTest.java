package com.filter.dsl.unit.functions;

import com.filter.dsl.functions.FunctionArgumentException;
import com.filter.dsl.functions.string.*;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import com.googlecode.aviator.runtime.type.AviatorNil;
import com.googlecode.aviator.exception.ExpressionRuntimeException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for string matching functions: CONTAINS, STARTS_WITH, ENDS_WITH, REGEX_MATCH
 */
class StringFunctionsTest {

    private final Map<String, Object> env = new HashMap<>();

    // ========== CONTAINS Function Tests ==========

    @Test
    void testContainsWithSubstringPresent() {
        ContainsFunction contains = new ContainsFunction();
        AviatorObject result = contains.call(env, 
            new AviatorString("hello world"), 
            new AviatorString("world"));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testContainsWithSubstringAbsent() {
        ContainsFunction contains = new ContainsFunction();
        AviatorObject result = contains.call(env, 
            new AviatorString("hello world"), 
            new AviatorString("foo"));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testContainsWithExactMatch() {
        ContainsFunction contains = new ContainsFunction();
        AviatorObject result = contains.call(env, 
            new AviatorString("hello"), 
            new AviatorString("hello"));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testContainsWithEmptySubstring() {
        ContainsFunction contains = new ContainsFunction();
        AviatorObject result = contains.call(env, 
            new AviatorString("test"), 
            new AviatorString(""));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testContainsWithEmptyString() {
        ContainsFunction contains = new ContainsFunction();
        AviatorObject result = contains.call(env, 
            new AviatorString(""), 
            new AviatorString("test"));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testContainsIsCaseSensitive() {
        ContainsFunction contains = new ContainsFunction();
        AviatorObject result = contains.call(env, 
            new AviatorString("Hello World"), 
            new AviatorString("world"));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testContainsWithNullString() {
        ContainsFunction contains = new ContainsFunction();
        AviatorObject result = contains.call(env, 
            AviatorNil.NIL, 
            new AviatorString("test"));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testContainsWithNullSubstring() {
        ContainsFunction contains = new ContainsFunction();
        AviatorObject result = contains.call(env, 
            new AviatorString("test"), 
            AviatorNil.NIL);
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testContainsWithWrongArgumentCount() {
        ContainsFunction contains = new ContainsFunction();
        assertThrows(IllegalArgumentException.class, () -> {
            contains.call(env, new AviatorString("test"));
        });
    }

    @Test
    void testContainsMetadata() {
        ContainsFunction contains = new ContainsFunction();
        assertEquals("CONTAINS", contains.getName());
        assertEquals(2, contains.getFunctionMetadata().getMinArgs());
        assertEquals(2, contains.getFunctionMetadata().getMaxArgs());
    }

    // ========== STARTS_WITH Function Tests ==========

    @Test
    void testStartsWithValidPrefix() {
        StartsWithFunction startsWith = new StartsWithFunction();
        AviatorObject result = startsWith.call(env, 
            new AviatorString("hello world"), 
            new AviatorString("hello"));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testStartsWithInvalidPrefix() {
        StartsWithFunction startsWith = new StartsWithFunction();
        AviatorObject result = startsWith.call(env, 
            new AviatorString("hello world"), 
            new AviatorString("world"));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testStartsWithExactMatch() {
        StartsWithFunction startsWith = new StartsWithFunction();
        AviatorObject result = startsWith.call(env, 
            new AviatorString("hello"), 
            new AviatorString("hello"));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testStartsWithEmptyPrefix() {
        StartsWithFunction startsWith = new StartsWithFunction();
        AviatorObject result = startsWith.call(env, 
            new AviatorString("test"), 
            new AviatorString(""));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testStartsWithEmptyString() {
        StartsWithFunction startsWith = new StartsWithFunction();
        AviatorObject result = startsWith.call(env, 
            new AviatorString(""), 
            new AviatorString("test"));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testStartsWithIsCaseSensitive() {
        StartsWithFunction startsWith = new StartsWithFunction();
        AviatorObject result = startsWith.call(env, 
            new AviatorString("Hello World"), 
            new AviatorString("hello"));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testStartsWithNullString() {
        StartsWithFunction startsWith = new StartsWithFunction();
        AviatorObject result = startsWith.call(env, 
            AviatorNil.NIL, 
            new AviatorString("test"));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testStartsWithNullPrefix() {
        StartsWithFunction startsWith = new StartsWithFunction();
        AviatorObject result = startsWith.call(env, 
            new AviatorString("test"), 
            AviatorNil.NIL);
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testStartsWithWrongArgumentCount() {
        StartsWithFunction startsWith = new StartsWithFunction();
        assertThrows(IllegalArgumentException.class, () -> {
            startsWith.call(env, new AviatorString("test"));
        });
    }

    @Test
    void testStartsWithMetadata() {
        StartsWithFunction startsWith = new StartsWithFunction();
        assertEquals("STARTS_WITH", startsWith.getName());
        assertEquals(2, startsWith.getFunctionMetadata().getMinArgs());
        assertEquals(2, startsWith.getFunctionMetadata().getMaxArgs());
    }

    // ========== ENDS_WITH Function Tests ==========

    @Test
    void testEndsWithValidSuffix() {
        EndsWithFunction endsWith = new EndsWithFunction();
        AviatorObject result = endsWith.call(env, 
            new AviatorString("hello world"), 
            new AviatorString("world"));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testEndsWithInvalidSuffix() {
        EndsWithFunction endsWith = new EndsWithFunction();
        AviatorObject result = endsWith.call(env, 
            new AviatorString("hello world"), 
            new AviatorString("hello"));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testEndsWithExactMatch() {
        EndsWithFunction endsWith = new EndsWithFunction();
        AviatorObject result = endsWith.call(env, 
            new AviatorString("hello"), 
            new AviatorString("hello"));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testEndsWithEmptySuffix() {
        EndsWithFunction endsWith = new EndsWithFunction();
        AviatorObject result = endsWith.call(env, 
            new AviatorString("test"), 
            new AviatorString(""));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testEndsWithEmptyString() {
        EndsWithFunction endsWith = new EndsWithFunction();
        AviatorObject result = endsWith.call(env, 
            new AviatorString(""), 
            new AviatorString("test"));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testEndsWithIsCaseSensitive() {
        EndsWithFunction endsWith = new EndsWithFunction();
        AviatorObject result = endsWith.call(env, 
            new AviatorString("Hello World"), 
            new AviatorString("WORLD"));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testEndsWithNullString() {
        EndsWithFunction endsWith = new EndsWithFunction();
        AviatorObject result = endsWith.call(env, 
            AviatorNil.NIL, 
            new AviatorString("test"));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testEndsWithNullSuffix() {
        EndsWithFunction endsWith = new EndsWithFunction();
        AviatorObject result = endsWith.call(env, 
            new AviatorString("test"), 
            AviatorNil.NIL);
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testEndsWithWrongArgumentCount() {
        EndsWithFunction endsWith = new EndsWithFunction();
        assertThrows(IllegalArgumentException.class, () -> {
            endsWith.call(env, new AviatorString("test"));
        });
    }

    @Test
    void testEndsWithMetadata() {
        EndsWithFunction endsWith = new EndsWithFunction();
        assertEquals("ENDS_WITH", endsWith.getName());
        assertEquals(2, endsWith.getFunctionMetadata().getMinArgs());
        assertEquals(2, endsWith.getFunctionMetadata().getMaxArgs());
    }

    // ========== REGEX_MATCH Function Tests ==========

    @Test
    void testRegexMatchWithSimplePattern() {
        RegexMatchFunction regexMatch = new RegexMatchFunction();
        AviatorObject result = regexMatch.call(env, 
            new AviatorString("hello123"), 
            new AviatorString("\\w+"));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testRegexMatchWithEmailPattern() {
        RegexMatchFunction regexMatch = new RegexMatchFunction();
        AviatorObject result = regexMatch.call(env, 
            new AviatorString("test@example.com"), 
            new AviatorString("^[\\w.]+@[\\w.]+$"));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testRegexMatchWithDigitsOnly() {
        RegexMatchFunction regexMatch = new RegexMatchFunction();
        AviatorObject result = regexMatch.call(env, 
            new AviatorString("12345"), 
            new AviatorString("^[0-9]+$"));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testRegexMatchWithDigitsOnlyFails() {
        RegexMatchFunction regexMatch = new RegexMatchFunction();
        AviatorObject result = regexMatch.call(env, 
            new AviatorString("hello"), 
            new AviatorString("^[0-9]+$"));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testRegexMatchWithPartialMatch() {
        RegexMatchFunction regexMatch = new RegexMatchFunction();
        AviatorObject result = regexMatch.call(env, 
            new AviatorString("abc123def"), 
            new AviatorString(".*\\d+.*"));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testRegexMatchIsCaseSensitive() {
        RegexMatchFunction regexMatch = new RegexMatchFunction();
        AviatorObject result = regexMatch.call(env, 
            new AviatorString("Hello"), 
            new AviatorString("^hello$"));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testRegexMatchWithEmptyString() {
        RegexMatchFunction regexMatch = new RegexMatchFunction();
        AviatorObject result = regexMatch.call(env, 
            new AviatorString(""), 
            new AviatorString("^$"));
        assertEquals(Boolean.TRUE, result.getValue(env));
    }

    @Test
    void testRegexMatchWithInvalidPattern() {
        RegexMatchFunction regexMatch = new RegexMatchFunction();
        assertThrows(ExpressionRuntimeException.class, () -> {
            regexMatch.call(env, 
                new AviatorString("test"), 
                new AviatorString("[invalid("));
        });
    }

    @Test
    void testRegexMatchWithNullString() {
        RegexMatchFunction regexMatch = new RegexMatchFunction();
        AviatorObject result = regexMatch.call(env, 
            AviatorNil.NIL, 
            new AviatorString(".*"));
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testRegexMatchWithNullPattern() {
        RegexMatchFunction regexMatch = new RegexMatchFunction();
        AviatorObject result = regexMatch.call(env, 
            new AviatorString("test"), 
            AviatorNil.NIL);
        assertEquals(Boolean.FALSE, result.getValue(env));
    }

    @Test
    void testRegexMatchWithWrongArgumentCount() {
        RegexMatchFunction regexMatch = new RegexMatchFunction();
        assertThrows(IllegalArgumentException.class, () -> {
            regexMatch.call(env, new AviatorString("test"));
        });
    }

    @Test
    void testRegexMatchMetadata() {
        RegexMatchFunction regexMatch = new RegexMatchFunction();
        assertEquals("REGEX_MATCH", regexMatch.getName());
        assertEquals(2, regexMatch.getFunctionMetadata().getMinArgs());
        assertEquals(2, regexMatch.getFunctionMetadata().getMaxArgs());
    }

    // ========== Integration Tests ==========

    @Test
    void testStringMatchingConsistency() {
        // If CONTAINS is true, at least one of STARTS_WITH or ENDS_WITH should be true
        // for a substring that appears at the beginning or end
        ContainsFunction contains = new ContainsFunction();
        StartsWithFunction startsWith = new StartsWithFunction();
        EndsWithFunction endsWith = new EndsWithFunction();
        
        String text = "hello world";
        String prefix = "hello";
        String suffix = "world";
        
        AviatorObject containsPrefix = contains.call(env, 
            new AviatorString(text), new AviatorString(prefix));
        AviatorObject startsWithPrefix = startsWith.call(env, 
            new AviatorString(text), new AviatorString(prefix));
        
        AviatorObject containsSuffix = contains.call(env, 
            new AviatorString(text), new AviatorString(suffix));
        AviatorObject endsWithSuffix = endsWith.call(env, 
            new AviatorString(text), new AviatorString(suffix));
        
        assertEquals(Boolean.TRUE, containsPrefix.getValue(env));
        assertEquals(Boolean.TRUE, startsWithPrefix.getValue(env));
        assertEquals(Boolean.TRUE, containsSuffix.getValue(env));
        assertEquals(Boolean.TRUE, endsWithSuffix.getValue(env));
    }

    @Test
    void testRegexMatchEquivalentToContains() {
        // REGEX_MATCH with ".*substring.*" should be equivalent to CONTAINS
        ContainsFunction contains = new ContainsFunction();
        RegexMatchFunction regexMatch = new RegexMatchFunction();
        
        String text = "hello world";
        String substring = "world";
        
        AviatorObject containsResult = contains.call(env, 
            new AviatorString(text), new AviatorString(substring));
        AviatorObject regexResult = regexMatch.call(env, 
            new AviatorString(text), new AviatorString(".*" + substring + ".*"));
        
        assertEquals(containsResult.getValue(env), regexResult.getValue(env));
    }

    @Test
    void testRegexMatchEquivalentToStartsWith() {
        // REGEX_MATCH with "^prefix.*" should be equivalent to STARTS_WITH
        StartsWithFunction startsWith = new StartsWithFunction();
        RegexMatchFunction regexMatch = new RegexMatchFunction();
        
        String text = "hello world";
        String prefix = "hello";
        
        AviatorObject startsWithResult = startsWith.call(env, 
            new AviatorString(text), new AviatorString(prefix));
        AviatorObject regexResult = regexMatch.call(env, 
            new AviatorString(text), new AviatorString("^" + prefix + ".*"));
        
        assertEquals(startsWithResult.getValue(env), regexResult.getValue(env));
    }

    @Test
    void testRegexMatchEquivalentToEndsWith() {
        // REGEX_MATCH with ".*suffix$" should be equivalent to ENDS_WITH
        EndsWithFunction endsWith = new EndsWithFunction();
        RegexMatchFunction regexMatch = new RegexMatchFunction();
        
        String text = "hello world";
        String suffix = "world";
        
        AviatorObject endsWithResult = endsWith.call(env, 
            new AviatorString(text), new AviatorString(suffix));
        AviatorObject regexResult = regexMatch.call(env, 
            new AviatorString(text), new AviatorString(".*" + suffix + "$"));
        
        assertEquals(endsWithResult.getValue(env), regexResult.getValue(env));
    }

    @Test
    void testAllStringFunctionsHandleNullGracefully() {
        // All string functions should return false for null inputs, not throw exceptions
        ContainsFunction contains = new ContainsFunction();
        StartsWithFunction startsWith = new StartsWithFunction();
        EndsWithFunction endsWith = new EndsWithFunction();
        RegexMatchFunction regexMatch = new RegexMatchFunction();
        
        assertDoesNotThrow(() -> {
            contains.call(env, AviatorNil.NIL, new AviatorString("test"));
            startsWith.call(env, AviatorNil.NIL, new AviatorString("test"));
            endsWith.call(env, AviatorNil.NIL, new AviatorString("test"));
            regexMatch.call(env, AviatorNil.NIL, new AviatorString(".*"));
        });
    }

    // ========== UPPER Function Tests ==========

    @Test
    void testUpperWithLowercase() {
        UpperFunction upper = new UpperFunction();
        AviatorObject result = upper.call(env, new AviatorString("hello"));
        assertEquals("HELLO", result.getValue(env));
    }

    @Test
    void testUpperWithMixedCase() {
        UpperFunction upper = new UpperFunction();
        AviatorObject result = upper.call(env, new AviatorString("Hello World"));
        assertEquals("HELLO WORLD", result.getValue(env));
    }

    @Test
    void testUpperWithAlreadyUppercase() {
        UpperFunction upper = new UpperFunction();
        AviatorObject result = upper.call(env, new AviatorString("ALREADY UPPER"));
        assertEquals("ALREADY UPPER", result.getValue(env));
    }

    @Test
    void testUpperWithEmptyString() {
        UpperFunction upper = new UpperFunction();
        AviatorObject result = upper.call(env, new AviatorString(""));
        assertEquals("", result.getValue(env));
    }

    @Test
    void testUpperWithNull() {
        UpperFunction upper = new UpperFunction();
        AviatorObject result = upper.call(env, AviatorNil.NIL);
        assertNull(result.getValue(env));
    }

    // ========== LOWER Function Tests ==========

    @Test
    void testLowerWithUppercase() {
        LowerFunction lower = new LowerFunction();
        AviatorObject result = lower.call(env, new AviatorString("HELLO"));
        assertEquals("hello", result.getValue(env));
    }

    @Test
    void testLowerWithMixedCase() {
        LowerFunction lower = new LowerFunction();
        AviatorObject result = lower.call(env, new AviatorString("Hello World"));
        assertEquals("hello world", result.getValue(env));
    }

    @Test
    void testLowerWithAlreadyLowercase() {
        LowerFunction lower = new LowerFunction();
        AviatorObject result = lower.call(env, new AviatorString("already lower"));
        assertEquals("already lower", result.getValue(env));
    }

    @Test
    void testLowerWithEmptyString() {
        LowerFunction lower = new LowerFunction();
        AviatorObject result = lower.call(env, new AviatorString(""));
        assertEquals("", result.getValue(env));
    }

    @Test
    void testLowerWithNull() {
        LowerFunction lower = new LowerFunction();
        AviatorObject result = lower.call(env, AviatorNil.NIL);
        assertNull(result.getValue(env));
    }

    // ========== TRIM Function Tests ==========

    @Test
    void testTrimWithLeadingAndTrailingSpaces() {
        TrimFunction trim = new TrimFunction();
        AviatorObject result = trim.call(env, new AviatorString("  hello  "));
        assertEquals("hello", result.getValue(env));
    }

    @Test
    void testTrimWithNoSpaces() {
        TrimFunction trim = new TrimFunction();
        AviatorObject result = trim.call(env, new AviatorString("hello"));
        assertEquals("hello", result.getValue(env));
    }

    @Test
    void testTrimWithInternalSpaces() {
        TrimFunction trim = new TrimFunction();
        AviatorObject result = trim.call(env, new AviatorString("  hello world  "));
        assertEquals("hello world", result.getValue(env));
    }

    @Test
    void testTrimWithTabsAndNewlines() {
        TrimFunction trim = new TrimFunction();
        AviatorObject result = trim.call(env, new AviatorString("\t\nhello\n\t"));
        assertEquals("hello", result.getValue(env));
    }

    @Test
    void testTrimWithEmptyString() {
        TrimFunction trim = new TrimFunction();
        AviatorObject result = trim.call(env, new AviatorString(""));
        assertEquals("", result.getValue(env));
    }

    @Test
    void testTrimWithNull() {
        TrimFunction trim = new TrimFunction();
        AviatorObject result = trim.call(env, AviatorNil.NIL);
        assertNull(result.getValue(env));
    }

    // ========== SUBSTRING Function Tests ==========

    @Test
    void testSubstringFromStart() {
        SubstringFunction substring = new SubstringFunction();
        AviatorObject result = substring.call(env, 
            new AviatorString("hello world"), 
            com.googlecode.aviator.runtime.type.AviatorLong.valueOf(0));
        assertEquals("hello world", result.getValue(env));
    }

    @Test
    void testSubstringFromMiddle() {
        SubstringFunction substring = new SubstringFunction();
        AviatorObject result = substring.call(env, 
            new AviatorString("hello world"), 
            com.googlecode.aviator.runtime.type.AviatorLong.valueOf(6));
        assertEquals("world", result.getValue(env));
    }

    @Test
    void testSubstringWithLength() {
        SubstringFunction substring = new SubstringFunction();
        AviatorObject result = substring.call(env, 
            new AviatorString("hello world"), 
            com.googlecode.aviator.runtime.type.AviatorLong.valueOf(0),
            com.googlecode.aviator.runtime.type.AviatorLong.valueOf(5));
        assertEquals("hello", result.getValue(env));
    }

    @Test
    void testSubstringWithLengthFromMiddle() {
        SubstringFunction substring = new SubstringFunction();
        AviatorObject result = substring.call(env, 
            new AviatorString("hello world"), 
            com.googlecode.aviator.runtime.type.AviatorLong.valueOf(6),
            com.googlecode.aviator.runtime.type.AviatorLong.valueOf(3));
        assertEquals("wor", result.getValue(env));
    }

    @Test
    void testSubstringOutOfBounds() {
        SubstringFunction substring = new SubstringFunction();
        AviatorObject result = substring.call(env, 
            new AviatorString("hello"), 
            com.googlecode.aviator.runtime.type.AviatorLong.valueOf(10));
        assertEquals("", result.getValue(env));
    }

    @Test
    void testSubstringWithNegativeIndex() {
        SubstringFunction substring = new SubstringFunction();
        AviatorObject result = substring.call(env, 
            new AviatorString("hello"), 
            com.googlecode.aviator.runtime.type.AviatorLong.valueOf(-1));
        assertEquals("o", result.getValue(env));
    }

    @Test
    void testSubstringWithNull() {
        SubstringFunction substring = new SubstringFunction();
        AviatorObject result = substring.call(env, 
            AviatorNil.NIL, 
            com.googlecode.aviator.runtime.type.AviatorLong.valueOf(0));
        assertNull(result.getValue(env));
    }

    // ========== REPLACE Function Tests ==========

    @Test
    void testReplaceSimple() {
        ReplaceFunction replace = new ReplaceFunction();
        AviatorObject result = replace.call(env, 
            new AviatorString("hello world"), 
            new AviatorString("world"),
            new AviatorString("there"));
        assertEquals("hello there", result.getValue(env));
    }

    @Test
    void testReplaceMultipleOccurrences() {
        ReplaceFunction replace = new ReplaceFunction();
        AviatorObject result = replace.call(env, 
            new AviatorString("hello hello"), 
            new AviatorString("hello"),
            new AviatorString("hi"));
        assertEquals("hi hi", result.getValue(env));
    }

    @Test
    void testReplaceNotFound() {
        ReplaceFunction replace = new ReplaceFunction();
        AviatorObject result = replace.call(env, 
            new AviatorString("hello world"), 
            new AviatorString("foo"),
            new AviatorString("bar"));
        assertEquals("hello world", result.getValue(env));
    }

    @Test
    void testReplaceWithEmptySearch() {
        ReplaceFunction replace = new ReplaceFunction();
        AviatorObject result = replace.call(env, 
            new AviatorString("hello"), 
            new AviatorString(""),
            new AviatorString("x"));
        assertEquals("hello", result.getValue(env));
    }

    @Test
    void testReplaceWithEmptyString() {
        ReplaceFunction replace = new ReplaceFunction();
        AviatorObject result = replace.call(env, 
            new AviatorString(""), 
            new AviatorString("hello"),
            new AviatorString("world"));
        assertEquals("", result.getValue(env));
    }

    @Test
    void testReplaceWithNull() {
        ReplaceFunction replace = new ReplaceFunction();
        AviatorObject result = replace.call(env, 
            AviatorNil.NIL, 
            new AviatorString("hello"),
            new AviatorString("world"));
        assertNull(result.getValue(env));
    }

    // ========== LENGTH Function Tests ==========

    @Test
    void testLengthSimple() {
        LengthFunction length = new LengthFunction();
        AviatorObject result = length.call(env, new AviatorString("hello"));
        assertEquals(5L, result.getValue(env));
    }

    @Test
    void testLengthWithSpaces() {
        LengthFunction length = new LengthFunction();
        AviatorObject result = length.call(env, new AviatorString("hello world"));
        assertEquals(11L, result.getValue(env));
    }

    @Test
    void testLengthEmptyString() {
        LengthFunction length = new LengthFunction();
        AviatorObject result = length.call(env, new AviatorString(""));
        assertEquals(0L, result.getValue(env));
    }

    @Test
    void testLengthWithWhitespace() {
        LengthFunction length = new LengthFunction();
        AviatorObject result = length.call(env, new AviatorString("  "));
        assertEquals(2L, result.getValue(env));
    }

    @Test
    void testLengthWithNull() {
        LengthFunction length = new LengthFunction();
        AviatorObject result = length.call(env, AviatorNil.NIL);
        assertNull(result.getValue(env));
    }

    // ========== CONCAT Function Tests ==========

    @Test
    void testConcatTwoStrings() {
        ConcatFunction concat = new ConcatFunction();
        AviatorObject result = concat.call(env, 
            new AviatorString("hello"), 
            new AviatorString(" world"));
        assertEquals("hello world", result.getValue(env));
    }

    @Test
    void testConcatThreeStrings() {
        ConcatFunction concat = new ConcatFunction();
        AviatorObject result = concat.call(env, 
            new AviatorString("hello"), 
            new AviatorString(" "),
            new AviatorString("world"));
        assertEquals("hello world", result.getValue(env));
    }

    @Test
    void testConcatSingleString() {
        ConcatFunction concat = new ConcatFunction();
        AviatorObject result = concat.call(env, new AviatorString("hello"));
        assertEquals("hello", result.getValue(env));
    }

    @Test
    void testConcatWithEmptyString() {
        ConcatFunction concat = new ConcatFunction();
        AviatorObject result = concat.call(env, 
            new AviatorString(""), 
            new AviatorString("world"));
        assertEquals("world", result.getValue(env));
    }

    @Test
    void testConcatWithNull() {
        ConcatFunction concat = new ConcatFunction();
        AviatorObject result = concat.call(env, 
            new AviatorString("hello"), 
            AviatorNil.NIL,
            new AviatorString("world"));
        assertEquals("hellonullworld", result.getValue(env));
    }

    // ========== SPLIT Function Tests ==========

    @Test
    void testSplitSimple() {
        SplitFunction split = new SplitFunction();
        AviatorObject result = split.call(env, 
            new AviatorString("hello,world"), 
            new AviatorString(","));
        @SuppressWarnings("unchecked")
        java.util.List<String> list = (java.util.List<String>) result.getValue(env);
        assertEquals(2, list.size());
        assertEquals("hello", list.get(0));
        assertEquals("world", list.get(1));
    }

    @Test
    void testSplitMultiple() {
        SplitFunction split = new SplitFunction();
        AviatorObject result = split.call(env, 
            new AviatorString("a-b-c"), 
            new AviatorString("-"));
        @SuppressWarnings("unchecked")
        java.util.List<String> list = (java.util.List<String>) result.getValue(env);
        assertEquals(3, list.size());
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
        assertEquals("c", list.get(2));
    }

    @Test
    void testSplitNotFound() {
        SplitFunction split = new SplitFunction();
        AviatorObject result = split.call(env, 
            new AviatorString("hello"), 
            new AviatorString(","));
        @SuppressWarnings("unchecked")
        java.util.List<String> list = (java.util.List<String>) result.getValue(env);
        assertEquals(1, list.size());
        assertEquals("hello", list.get(0));
    }

    @Test
    void testSplitWithTrailingDelimiter() {
        SplitFunction split = new SplitFunction();
        AviatorObject result = split.call(env, 
            new AviatorString("a,b,c,"), 
            new AviatorString(","));
        @SuppressWarnings("unchecked")
        java.util.List<String> list = (java.util.List<String>) result.getValue(env);
        assertEquals(4, list.size());
        assertEquals("", list.get(3));
    }

    @Test
    void testSplitEmptyString() {
        SplitFunction split = new SplitFunction();
        AviatorObject result = split.call(env, 
            new AviatorString(""), 
            new AviatorString(","));
        @SuppressWarnings("unchecked")
        java.util.List<String> list = (java.util.List<String>) result.getValue(env);
        assertEquals(1, list.size());
        assertEquals("", list.get(0));
    }

    @Test
    void testSplitWithNull() {
        SplitFunction split = new SplitFunction();
        AviatorObject result = split.call(env, 
            AviatorNil.NIL, 
            new AviatorString(","));
        assertNull(result.getValue(env));
    }
}

package com.github.bannmann.whisperjson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StringTest
{
    @Test
    public void parseString()
    {
        ExposedJson json = WhisperJson.parse("\"hello\"");
        assertTrue(json.isString());
        assertEquals("hello", json.asString());
    }

    @Test
    public void parseEmptyString()
    {
        ExposedJson json = WhisperJson.parse("  \"\"  ");
        assertTrue(json.isString());
        assertEquals("", json.asString());
    }

    @Test(expected = ParseException.class)
    public void parseOpenString()
    {
        WhisperJson.parse("\"hello");
    }

    @Test
    public void parseStringWithEscapedQuote()
    {
        ExposedJson json = WhisperJson.parse("\"hello \\\"quoted\\\" world\"");
        assertEquals("hello \"quoted\" world", json.asString());
    }

    @Test
    public void parseStringWithEscapedBackslash()
    {
        ExposedJson json = WhisperJson.parse("\"hello \\\\ world\"");
        assertEquals("hello \\ world", json.asString());
    }

    @Test
    public void parseStringWithEscapedSlash()
    {
        ExposedJson json = WhisperJson.parse("\"hello \\/ world\"");
        assertEquals("hello / world", json.asString());
    }

    @Test
    public void parseStringWithEscapedBackspace()
    {
        ExposedJson json = WhisperJson.parse("\"hello \\b world\"");
        assertEquals("hello \b world", json.asString());
    }

    @Test
    public void parseStringWithEscapedFormfeed()
    {
        ExposedJson json = WhisperJson.parse("\"hello \\f world\"");
        assertEquals("hello \f world", json.asString());
    }

    @Test
    public void parseStringWithEscapedNewline()
    {
        ExposedJson json = WhisperJson.parse("\"hello \\n world\"");
        assertEquals("hello \n world", json.asString());
    }

    @Test(expected = ParseException.class)
    public void parseStringWithUnescapedNewline()
    {
        WhisperJson.parse("\"hello \n world\"");
    }

    @Test
    public void parseStringWithEscapedCarriageReturn()
    {
        ExposedJson json = WhisperJson.parse("\"hello \\r world\"");
        assertEquals("hello \r world", json.asString());
    }

    @Test
    public void parseStringWithEscapedTab()
    {
        ExposedJson json = WhisperJson.parse("\"hello \\t world\"");
        assertEquals("hello \t world", json.asString());
    }

    @Test
    public void parseStringWithEscapedUnicode()
    {
        ExposedJson json = WhisperJson.parse("\"hello \\u2ebf world\"");
        assertEquals("hello \u2ebf world", json.asString());
    }

    @Test(expected = ParseException.class)
    public void parseStringWithBrokenUnicode()
    {
        WhisperJson.parse("\"hello \\u123 world\"");
    }

    @Test(expected = ParseException.class)
    public void parseStringWithNonHexUnicode()
    {
        WhisperJson.parse("\"hello \\uzzzz world\"");
    }

    @Test(expected = ParseException.class)
    public void parseStringWithControlChar()
    {
        WhisperJson.parse("\"hello \u0000 world\"");
    }
}

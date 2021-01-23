package com.github.bannmann.whisperjson;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.bannmann.whisperjson.text.StringText;

public class EscapeTest
{
    @Test
    public void escapeEmptyString()
    {
        assertEquals("", invokeEscape(""));
    }

    @Test
    public void escapeText()
    {
        String text = "th3 quick brown fox jumps ov3r th3 l4zy dog";
        assertEquals(text, invokeEscape(text));
    }

    @Test
    public void escapeQuote()
    {
        assertEquals("brown \\\" fox", invokeEscape("brown \" fox"));
    }

    @Test
    public void escapeBackslash()
    {
        assertEquals("brown \\\\ fox", invokeEscape("brown \\ fox"));
    }

    @Test
    public void escapeSlash()
    {
        assertEquals("brown / fox", invokeEscape("brown / fox"));
    }

    @Test
    public void escapeControlChars()
    {
        assertEquals("\\b\\f\\n\\r\\t", invokeEscape("\b\f\n\r\t"));
    }

    @Test
    public void escapeUnicode()
    {
        assertEquals("\\u2ebf", invokeEscape("\u2ebf"));
    }

    private String invokeEscape(String s)
    {
        return StringCodec.escape(new StringText(s))
            .asString();
    }
}

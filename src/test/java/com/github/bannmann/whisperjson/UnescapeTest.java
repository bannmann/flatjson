package com.github.bannmann.whisperjson;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.bannmann.whisperjson.text.StringText;

public class UnescapeTest
{
    @Test
    public void unescapeEmptyString()
    {
        assertEquals("", invokeUnescape(""));
    }

    @Test
    public void unescapeText()
    {
        String text = "th3 quick brown fox jumps ov3r th3 l4zy dog";
        assertEquals(text, invokeUnescape(text));
    }

    @Test
    public void unescapeQuote()
    {
        assertEquals("brown \" fox", invokeUnescape("brown \\\" fox"));
    }

    @Test
    public void unescapeBackslash()
    {
        assertEquals("brown \\ fox", invokeUnescape("brown \\\\ fox"));
    }

    @Test
    public void unescapeSlash()
    {
        assertEquals("brown / fox", invokeUnescape("brown \\/ fox"));
    }

    @Test
    public void unescapeControlChars()
    {
        assertEquals("\b\f\n\r\t", invokeUnescape("\\b\\f\\n\\r\\t"));
    }

    @Test
    public void unescapeUnicode()
    {
        assertEquals("\u2ebf", invokeUnescape("\\u2ebf"));
    }

    private String invokeUnescape(String s)
    {
        return StringCodec.unescape(new StringText(s))
            .asString();
    }
}

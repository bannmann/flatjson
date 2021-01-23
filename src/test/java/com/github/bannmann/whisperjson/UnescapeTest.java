package com.github.bannmann.whisperjson;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.github.bannmann.whisperjson.text.StringText;

public class UnescapeTest
{
    @Test(dataProvider = "noUnescapingNeeded")
    public void doNotUnescape(String label, String input)
    {
        String unescaped = invokeUnescape(input);
        assertThat(unescaped).isEqualTo(input);
    }

    @DataProvider
    public static Object[][] noUnescapingNeeded()
    {
        return new Object[][]{
            new Object[]{ "empty", "" }, new Object[]{ "text", "th3 quick brown fox jumps ov3r th3 l4zy dog" }
        };
    }

    @Test(dataProvider = "unescapeVariants")
    public void testUnescape(String label, String input, String expected)
    {
        String unescaped = invokeUnescape(input);
        assertThat(unescaped).isEqualTo(expected);
    }

    @DataProvider
    public static Object[][] unescapeVariants()
    {
        return new Object[][]{
            new Object[]{ "quote", "brown \\\" fox", "brown \" fox" },
            new Object[]{ "backslash", "brown \\\\ fox", "brown \\ fox" },
            new Object[]{ "slash", "brown \\/ fox", "brown / fox" },
            new Object[]{ "backspace", "\\b", "\b" },
            new Object[]{ "control chars", "\\b\\f\\n\\r\\t", "\b\f\n\r\t" },
            new Object[]{ "unicode", "\\u2ebf", "\u2ebf" }
        };
    }

    private String invokeUnescape(String s)
    {
        return StringCodec.unescape(new StringText(s))
            .asString();
    }
}

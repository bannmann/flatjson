package com.github.bannmann.whisperjson;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestUnescaping
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

    @Test
    public void unescapeInvalid()
    {
        assertThatThrownBy(() -> invokeUnescape("\\z")).isInstanceOf(JsonSyntaxException.class)
            .hasMessage("illegal escape char 'z' at index 1");
    }

    private String invokeUnescape(String s)
    {
        return new Text.Exposed(s).unescape()
            .asString();
    }
}

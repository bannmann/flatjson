package com.github.bannmann.whisperjson;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestEscaping
{
    @Test(dataProvider = "noEscapingNeeded")
    public void doNotEscape(String label, String input)
    {
        String escaped = invokeEscape(input);
        assertThat(escaped).isEqualTo(input);
    }

    @DataProvider
    public static Object[][] noEscapingNeeded()
    {
        return new Object[][]{
            new Object[]{ "empty", "" },
            new Object[]{ "text", "th3 quick brown fox jumps ov3r th3 l4zy dog" },
            new Object[]{ "slash", "brown / fox" }
        };
    }

    @Test(dataProvider = "escapeVariants")
    public void testEscape(String label, String input, String expected)
    {
        String escaped = invokeEscape(input);
        assertThat(escaped).isEqualTo(expected);
    }

    @DataProvider
    public static Object[][] escapeVariants()
    {
        return new Object[][]{
            new Object[]{ "quote", "brown \" fox", "brown \\\" fox" },
            new Object[]{ "backslash", "brown \\ fox", "brown \\\\ fox" },
            new Object[]{ "backspace", "\b", "\\b" },
            new Object[]{ "control chars", "\b\f\n\r\t", "\\b\\f\\n\\r\\t" },
            new Object[]{ "unicode", "\u2ebf", "\\u2ebf" },
            };
    }

    private String invokeEscape(String s)
    {
        return new Text.Exposed(s).escape()
            .asString();
    }
}

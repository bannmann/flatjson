package com.github.bannmann.whisperjson;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class StringTest
{
    @Test(dataProvider = "strings")
    public void parseString(String label, String input, String expected)
    {
        ExposedJson json = WhisperJson.parse(input);
        assertThat(json).returns(true, Json::isString)
            .returns(expected, ExposedJson::asString);
    }

    @DataProvider
    public static Object[][] strings()
    {
        return new Object[][]{
            new Object[]{ "string", "\"hello\"", "hello" },
            new Object[]{ "empty string", "  \"\"  ", "" },
            new Object[]{ "escaped quote", "\"hello \\\"quoted\\\" world\"", "hello \"quoted\" world" },
            new Object[]{ "escaped backslash", "\"hello \\\\ world\"", "hello \\ world" },
            new Object[]{ "escaped slash", "\"hello \\/ world\"", "hello / world" },
            new Object[]{ "escaped backspace", "\"hello \\b world\"", "hello \b world" },
            new Object[]{ "escaped form feed", "\"hello \\f world\"", "hello \f world" },
            new Object[]{ "escaped newline", "\"hello \\n world\"", "hello \n world" },
            new Object[]{ "escaped carriage return", "\"hello \\r world\"", "hello \r world" },
            new Object[]{ "escaped tab", "\"hello \\t world\"", "hello \t world" },
            new Object[]{ "escaped unicode", "\"hello \\u2ebf world\"", "hello \u2ebf world" }
        };
    }

    @Test(dataProvider = "malformedStrings", expectedExceptions = JsonSyntaxException.class)
    public void parseFailure(String label, String input)
    {
        WhisperJson.parse(input);
    }

    @DataProvider
    public static Object[][] malformedStrings()
    {
        return new Object[][]{
            new Object[]{ "open string", "\"hello" },
            new Object[]{ "unescaped newline", "\"hello \n world\"" },
            new Object[]{ "broken unicode", "\"hello \\u123 world\"" },
            new Object[]{ "non-hex unicode", "\"hello \\uzzzz world\"" },
            new Object[]{ "control char", "\"hello \u0000 world\"" }
        };
    }
}

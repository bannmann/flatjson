package com.github.bannmann.whisperjson;

import lombok.experimental.UtilityClass;

@UtilityClass
public class WhisperJson
{
    /**
     * Parses the given string.
     *
     * @param raw the string to parse
     *
     * @return an ExposedJson instance
     *
     * @throws ParseException if there is a JSON syntax error
     */
    public static ExposedJson parse(String raw)
    {
        return new Factory.Exposed().create(new Overlay.Exposed(raw), 0);
    }

    /**
     * Parses the given char array. The returned instance is backed by the given array. When the instance is closed, the
     * backing array is wiped.
     *
     * @param raw the characters to parse
     *
     * @return a SafeJson instance backed by the given array
     *
     * @throws ParseException if there is a JSON syntax error
     */
    public static SafeJson parse(char[] raw)
    {
        return new Factory.Safe().create(new Overlay.Safe(raw), 0);
    }
}

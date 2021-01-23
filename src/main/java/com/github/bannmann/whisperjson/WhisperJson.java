package com.github.bannmann.whisperjson;

import lombok.experimental.UtilityClass;

@UtilityClass
public class WhisperJson
{
    public static ExposedJson parse(String raw)
    {
        return new ExposedQuux().create(new Overlay(raw), 0);
    }

    public static SafeJson parse(char[] raw)
    {
        return new SafeQuux().create(new Overlay(raw), 0);
    }
}

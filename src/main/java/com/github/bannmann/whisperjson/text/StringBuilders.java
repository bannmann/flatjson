package com.github.bannmann.whisperjson.text;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringBuilders
{
    public char[] copyToCharArray(StringBuilder builder)
    {
        int length = builder.length();
        char[] result = new char[length];
        builder.getChars(0, length, result, 0);
        return result;
    }
}

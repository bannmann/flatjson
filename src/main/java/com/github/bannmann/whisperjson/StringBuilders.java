package com.github.bannmann.whisperjson;

import lombok.experimental.UtilityClass;

@UtilityClass
class StringBuilders
{
    public char[] copyToCharArray(StringBuilder builder)
    {
        int length = builder.length();
        char[] result = new char[length];
        builder.getChars(0, length, result, 0);
        return result;
    }
}

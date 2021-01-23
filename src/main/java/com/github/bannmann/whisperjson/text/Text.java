package com.github.bannmann.whisperjson.text;

public interface Text
{
    char charAt(int index);

    int length();

    Text getPart(int beginIndex, int endIndex);

    char[] asCharArray();

    String asString();
}

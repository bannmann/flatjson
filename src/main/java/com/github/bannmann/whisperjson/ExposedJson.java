package com.github.bannmann.whisperjson;

public interface ExposedJson extends Json<ExposedJson>
{
    default String asString()
    {
        throw new IllegalStateException("not a string");
    }
}

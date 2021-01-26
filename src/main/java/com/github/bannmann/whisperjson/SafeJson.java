package com.github.bannmann.whisperjson;

public interface SafeJson extends Json<SafeJson>, AutoCloseable
{
    @Override
    default void close()
    {
    }
}

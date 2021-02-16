package com.github.bannmann.whisperjson;

public class JsonSyntaxException extends RuntimeException
{
    JsonSyntaxException(String message, int index)
    {
        this(message, index, null);
    }

    JsonSyntaxException(String message, int index, Exception cause)
    {
        this(String.format("%s at index %d", message, index), cause);
    }

    JsonSyntaxException(String message, char foundChar, int index)
    {
        this(message, foundChar, index, null);
    }

    JsonSyntaxException(String message, char foundChar, int index, Exception cause)
    {
        this(String.format("%s '%s' at index %d", message, foundChar, index), cause);
    }

    JsonSyntaxException(String message, Exception cause)
    {
        super(message, cause);
    }
}

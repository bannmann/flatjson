package com.github.bannmann.whisperjson;

public class JsonSyntaxException extends RuntimeException
{
    JsonSyntaxException(String message, int index)
    {
        super(String.format("%s at index %d", message, index));
    }

    JsonSyntaxException(String message, char foundChar, int index)
    {
        super(String.format("%s '%s' at index %d", message, foundChar, index));
    }

    JsonSyntaxException(String message, Exception cause)
    {
        super(message, cause);
    }
}

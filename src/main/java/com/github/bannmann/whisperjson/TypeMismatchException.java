package com.github.bannmann.whisperjson;

/**
 * Thrown when the application attempted to read a JSON value that is not of the required type.
 */
public class TypeMismatchException extends RuntimeException
{
    TypeMismatchException(TypeLabel expected, Type actual, int index)
    {
        super(String.format("Type mismatch: expected %s, got %s at index %d",
            expected.getDisplayName(),
            actual.getLabel()
                .getDisplayName(),
            index));
    }
}

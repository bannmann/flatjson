package com.github.bannmann.whisperjson;

/**
 * TODO document
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

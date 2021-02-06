package com.github.bannmann.whisperjson;

public interface SafeJson extends Json<SafeJson>, AutoCloseable
{

    /**
     * @return a new array with a copy of the contents of this JSON string
     */
    default char[] asCharArray()
    {
        throw new IllegalStateException("not a string");
    }

    /**
     * Gets the contents of this string as a {@link SensitiveText}. The returned {@code SensitiveText} must be closed
     * {@link AutoCloseable#close() closed} separately from this instance.
     *
     * @return a copy of the contents of this JSON string. May be empty, but never {@code null}.
     *
     * @throws IllegalStateException if this JSON element does not represent a non-{@code null} string
     * @see Json#isString()
     * @see #asCharArray()
     */
    default SensitiveText asSensitiveText()
    {
        throw new IllegalStateException("not a string");
    }

    @Override
    default void close()
    {
    }
}

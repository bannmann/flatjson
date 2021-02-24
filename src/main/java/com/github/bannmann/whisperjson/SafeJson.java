package com.github.bannmann.whisperjson;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.NonNull;

/**
 * Element of a JSON tree that was parsed without creating {@link String} instances. <br>
 * <br>
 * See {@link Json} for usage information.
 */
public interface SafeJson extends Json<SafeJson>, AutoCloseable
{
    /**
     * @throws TypeMismatchException {@inheritDoc}
     * @throws IllegalStateException if this instance or the underlying JSON tree has been {@link #close() closed}.
     */
    @Override
    boolean asBoolean();

    /**
     * @throws TypeMismatchException {@inheritDoc}
     * @throws NumberFormatException {@inheritDoc}
     * @throws IllegalStateException if this instance or the underlying JSON tree has been {@link #close() closed}.
     */
    @Override
    int asInt();

    /**
     * @throws TypeMismatchException {@inheritDoc}
     * @throws NumberFormatException {@inheritDoc}
     * @throws IllegalStateException if this instance or the underlying JSON tree has been {@link #close() closed}.
     */
    @Override
    long asLong();

    /**
     * @throws TypeMismatchException {@inheritDoc}
     * @throws NumberFormatException {@inheritDoc}
     * @throws IllegalStateException if this instance or the underlying JSON tree has been {@link #close() closed}.
     */
    @Override
    float asFloat();

    /**
     * @throws TypeMismatchException {@inheritDoc}
     * @throws NumberFormatException {@inheritDoc}
     * @throws IllegalStateException if this instance or the underlying JSON tree has been {@link #close() closed}.
     */
    @Override
    double asDouble();

    /**
     * @throws TypeMismatchException {@inheritDoc}
     * @throws NumberFormatException {@inheritDoc}
     * @throws IllegalStateException if this instance or the underlying JSON tree has been {@link #close() closed}.
     */
    @Override
    BigInteger asBigInteger();

    /**
     * @throws TypeMismatchException {@inheritDoc}
     * @throws NumberFormatException {@inheritDoc}
     * @throws IllegalStateException if this instance or the underlying JSON tree has been {@link #close() closed}.
     */
    @Override
    BigDecimal asBigDecimal();

    /**
     * @throws TypeMismatchException {@inheritDoc}
     * @throws IllegalStateException if this instance or the underlying JSON tree has been {@link #close() closed}.
     */
    @Override
    List<SafeJson> asArray();

    /**
     * @throws TypeMismatchException {@inheritDoc}
     * @throws IllegalStateException if this instance or the underlying JSON tree has been {@link #close() closed}.
     */
    @Override
    Map<String, SafeJson> asObject();

    /**
     * @throws NullPointerException {@inheritDoc}
     * @throws TypeMismatchException {@inheritDoc}
     * @throws IllegalStateException if this instance or the underlying JSON tree has been {@link #close() closed}.
     */
    @Override
    Optional<SafeJson> getObjectProperty(@NonNull String name);

    /**
     * Gets the characters contained in this string. The returned array should be wiped immediately after use.
     *
     * @return a copy of the contents of this JSON string. May be empty, but never {@code null}.
     *
     * @throws IllegalStateException if this JSON element does not represent a non-{@code null} string
     * @throws TypeMismatchException if this JSON element does not represent a non-{@code null} string
     * @throws IllegalStateException if this instance or the underlying JSON tree has been {@link #close() closed}.
     * @see Json#isString()
     * @see SafeJson#asSensitiveText()
     * @see ExposedJson#asString()
     */
    char[] asCharArray();

    /**
     * Gets the contents of this string as a {@link SensitiveText}. The returned {@code SensitiveText} must be closed
     * {@link #close() closed} separately from this instance.
     *
     * @return a copy of the contents of this JSON string. May be empty, but never {@code null}.
     *
     * @throws TypeMismatchException if this JSON element does not represent a non-{@code null} string
     * @throws IllegalStateException if this instance or the underlying JSON tree has been {@link #close() closed}.
     * @see Json#isString()
     * @see #asCharArray()
     * @see ExposedJson#asString()
     */
    SensitiveText asSensitiveText();

    /**
     * Closes this JSON element, wiping all characters it holds exclusively. <br>
     * <br>
     * The element returned from the original call to {@link WhisperJson#parse(char[])} (or its overloads), the <b>root
     * element</b>, exclusively holds all characters of the JSON source text. If the root element is an object, array or
     * string, closing it will wipe those characters. It will also close any elements previously retrieved from it,
     * regardless whether that happened directly or via intermediate elements.<br>
     * <br>
     * Closing a non-root string element will wipe any characters cached by that instance, but will not affect other
     * elements or the JSON source text.<br>
     * <br>
     * Invoking this method on any other element will not have any effect and will not cause that instance to throw
     * {@link IllegalStateException IllegalStateExceptions} on subsequent calls of other methods.<br>
     * <br>
     * Calling {@code close()} repeatedly has no effect.
     */
    @Override
    void close();
}

package com.github.bannmann.whisperjson;

/**
 * Element of a JSON tree created by parsing a {@link String}. For sensitive data like passwords and other credentials,
 * {@link SafeJson} should be used instead.<br>
 * <br>
 * See {@link Json} for usage information.
 *
 * @see SafeJson
 */
public interface ExposedJson extends Json<ExposedJson>
{
    /**
     * Gets this string.
     *
     * @return the {@link String} value. May be empty, but never {@code null}.
     *
     * @throws IllegalStateException if this JSON element does not represent a non-{@code null} string
     * @see Json#isString()
     * @see SafeJson#asSensitiveText()
     * @see SafeJson#asCharArray()
     */
    String asString();
}

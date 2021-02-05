package com.github.bannmann.whisperjson;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface Json<J extends Json<J>>
{
    default boolean isNull()
    {
        return false;
    }

    default boolean isBoolean()
    {
        return false;
    }

    default boolean isNumber()
    {
        return false;
    }

    default boolean isString()
    {
        return false;
    }

    default boolean isArray()
    {
        return false;
    }

    default boolean isObject()
    {
        return false;
    }

    default boolean asBoolean()
    {
        throw new IllegalStateException("not a boolean");
    }

    default int asInt()
    {
        throw new IllegalStateException("not a number");
    }

    default long asLong()
    {
        throw new IllegalStateException("not a number");
    }

    default float asFloat()
    {
        throw new IllegalStateException("not a number");
    }

    default double asDouble()
    {
        throw new IllegalStateException("not a number");
    }

    default BigInteger asBigInteger()
    {
        throw new IllegalStateException("not a number");
    }

    default BigDecimal asBigDecimal()
    {
        throw new IllegalStateException("not a number");
    }

    /**
     * @return a new array with a copy of the contents of this JSON string
     */
    default char[] asCharArray()
    {
        throw new IllegalStateException("not a string");
    }

    /**
     * @return an immutable list with the contents of this array. May be empty, but never {@code null}.
     */
    default List<J> asArray()
    {
        throw new IllegalStateException("not an array");
    }

    /**
     * @return an immutable map with the contents of this object. May be empty, but never {@code null}.
     */
    default Map<String, J> asObject()
    {
        throw new IllegalStateException("not an object");
    }
}

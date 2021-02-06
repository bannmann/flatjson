package com.github.bannmann.whisperjson;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.NonNull;

public interface Json<J extends Json<J>>
{
    default boolean isNull()
    {
        return false;
    }

    default boolean isAnyNonNull()
    {
        return !isNull();
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

    default Optional<J> getObjectProperty(@NonNull String name)
    {
        return Optional.ofNullable(asObject().get(name))
            .filter(J::isAnyNonNull);
    }
}

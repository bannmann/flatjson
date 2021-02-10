package com.github.bannmann.whisperjson;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.NonNull;

public interface Json<J extends Json<J>>
{
    boolean isNull();

    boolean isAnyNonNull();

    boolean isBoolean();

    boolean isNumber();

    boolean isString();

    boolean isArray();

    boolean isObject();

    boolean asBoolean();

    int asInt();

    long asLong();

    float asFloat();

    double asDouble();

    BigInteger asBigInteger();

    BigDecimal asBigDecimal();

    /**
     * @return an immutable list with the contents of this array. May be empty, but never {@code null}.
     */
    List<J> asArray();

    /**
     * @return an immutable map with the contents of this object. May be empty, but never {@code null}.
     */
    Map<String, J> asObject();

    Optional<J> getObjectProperty(@NonNull String name);
}

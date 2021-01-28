package com.github.bannmann.whisperjson;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

abstract class Number<J extends Json<J>> implements Json<J>
{
    public static final class Exposed extends Number<ExposedJson> implements ExposedJson
    {
        public Exposed(String value)
        {
            super(value);
        }
    }

    public static final class Safe extends Number<SafeJson> implements SafeJson
    {
        public Safe(String value)
        {
            super(value);
        }
    }

    private final String value;

    protected Number(String value)
    {
        this.value = value;
    }

    @Override
    public boolean isNumber()
    {
        return true;
    }

    @Override
    public int asInt()
    {
        return Integer.parseInt(value);
    }

    @Override
    public long asLong()
    {
        return Long.parseLong(value);
    }

    @Override
    public float asFloat()
    {
        return Float.parseFloat(value);
    }

    @Override
    public double asDouble()
    {
        return Double.parseDouble(value);
    }

    @Override
    public BigInteger asBigInteger()
    {
        return new BigInteger(value);
    }

    @Override
    public BigDecimal asBigDecimal()
    {
        return new BigDecimal(value);
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof Number<?>)
        {
            Number<?> other = (Number<?>) o;
            return Objects.equals(other.asBigDecimal(), asBigDecimal());
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return asBigDecimal().hashCode();
    }
}

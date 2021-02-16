package com.github.bannmann.whisperjson;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

abstract class Number<J extends Json<J>, O extends Overlay<?>> extends Element<J, O>
{
    public static final class Exposed extends Number<ExposedJson, Overlay.Exposed> implements ExposedJson
    {
        public Exposed(Overlay.Exposed overlay, int element)
        {
            super(overlay, element);
        }
    }

    public static final class Safe extends Number<SafeJson, Overlay.Safe> implements SafeJson
    {
        public Safe(Overlay.Safe overlay, int element)
        {
            super(overlay, element);
        }
    }

    private String value;

    private Number(O overlay, int element)
    {
        super(overlay, element);
    }

    @Override
    public boolean isNumber()
    {
        return true;
    }

    protected final String getOrCreateValue()
    {
        if (value == null)
        {
            value = overlay.getJson(element)
                .asString();
        }
        return value;
    }

    @Override
    public int asInt()
    {
        return Integer.parseInt(getOrCreateValue());
    }

    @Override
    public long asLong()
    {
        return Long.parseLong(getOrCreateValue());
    }

    @Override
    public float asFloat()
    {
        return Float.parseFloat(getOrCreateValue());
    }

    @Override
    public double asDouble()
    {
        return Double.parseDouble(getOrCreateValue());
    }

    @Override
    public BigInteger asBigInteger()
    {
        return new BigInteger(getOrCreateValue());
    }

    @Override
    public BigDecimal asBigDecimal()
    {
        return new BigDecimal(getOrCreateValue());
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof Number<?, ?>)
        {
            Number<?, ?> other = (Number<?, ?>) o;
            return Objects.equals(other.getOrCreateValue(), getOrCreateValue());
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return getOrCreateValue().hashCode();
    }
}

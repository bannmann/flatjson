package com.github.bannmann.whisperjson;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class Json
{
    enum Type
    {
        NULL,
        TRUE,
        FALSE,
        NUMBER,
        STRING,
        STRING_ESCAPED,
        ARRAY,
        OBJECT
    }

    protected static Json create(Overlay overlay, int element)
    {
        Type type = overlay.getType(element);
        switch (type)
        {
            case NULL:
                return Literal.Null.INSTANCE;
            case TRUE:
                return Literal.Bool.TRUE;
            case FALSE:
                return Literal.Bool.FALSE;
            case NUMBER:
                return new Literal.Number(overlay.getJson(element)
                    .asString());
            case STRING_ESCAPED:
            case STRING:
                return new Parsed.Strng(overlay, element);
            case ARRAY:
                return new Parsed.Array(overlay, element);
            case OBJECT:
                return new Parsed.Object(overlay, element);
            default:
                throw new ParseException("unknown type: " + type);
        }
    }

    public static Json parse(String raw)
    {
        return create(new Overlay(raw), 0);
    }

    public static Json parse(char[] raw)
    {
        return create(new Overlay(raw), 0);
    }

    public boolean isNull()
    {
        return false;
    }

    public boolean isBoolean()
    {
        return false;
    }

    public boolean isNumber()
    {
        return false;
    }

    public boolean isString()
    {
        return false;
    }

    public boolean isArray()
    {
        return false;
    }

    public boolean isObject()
    {
        return false;
    }

    public boolean asBoolean()
    {
        throw new IllegalStateException("not a boolean");
    }

    public int asInt()
    {
        throw new IllegalStateException("not a number");
    }

    public long asLong()
    {
        throw new IllegalStateException("not a number");
    }

    public float asFloat()
    {
        throw new IllegalStateException("not a number");
    }

    public double asDouble()
    {
        throw new IllegalStateException("not a number");
    }

    public BigInteger asBigInteger()
    {
        throw new IllegalStateException("not a number");
    }

    public BigDecimal asBigDecimal()
    {
        throw new IllegalStateException("not a number");
    }

    public String asString()
    {
        throw new IllegalStateException("not a string");
    }

    public char[] asCharArray()
    {
        throw new IllegalStateException("not a string");
    }

    public List<Json> asArray()
    {
        throw new IllegalStateException("not an array");
    }

    public Map<String, Json> asObject()
    {
        throw new IllegalStateException("not an object");
    }
}

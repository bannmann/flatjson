package com.github.bannmann.whisperjson;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class Element<J extends Json<J>, O extends Overlay<?>> implements Json<J>
{
    protected final O overlay;
    protected final int element;

    @Override
    public boolean isNull()
    {
        return false;
    }

    @Override
    public boolean isAnyNonNull()
    {
        return !isNull();
    }

    @Override
    public boolean isBoolean()
    {
        return false;
    }

    @Override
    public boolean isNumber()
    {
        return false;
    }

    @Override
    public boolean isString()
    {
        return false;
    }

    @Override
    public boolean isArray()
    {
        return false;
    }

    @Override
    public boolean isObject()
    {
        return false;
    }

    @Override
    public boolean asBoolean()
    {
        throw typeMismatch(TypeLabel.BOOLEAN);
    }

    @Override
    public int asInt()
    {
        throw typeMismatch(TypeLabel.NUMBER);
    }

    @Override
    public long asLong()
    {
        throw typeMismatch(TypeLabel.NUMBER);
    }

    @Override
    public float asFloat()
    {
        throw typeMismatch(TypeLabel.NUMBER);
    }

    @Override
    public double asDouble()
    {
        throw typeMismatch(TypeLabel.NUMBER);
    }

    @Override
    public BigInteger asBigInteger()
    {
        throw typeMismatch(TypeLabel.NUMBER);
    }

    @Override
    public BigDecimal asBigDecimal()
    {
        throw typeMismatch(TypeLabel.NUMBER);
    }

    public String asString()
    {
        // Pre-override method which subclasses inherit from ExposedJson
        throw typeMismatch(TypeLabel.STRING);
    }

    public char[] asCharArray()
    {
        // Pre-override method which subclasses inherit from SafeJson
        throw typeMismatch(TypeLabel.STRING);
    }

    public SensitiveText asSensitiveText()
    {
        // Pre-override method which subclasses inherit from SafeJson
        throw typeMismatch(TypeLabel.STRING);
    }

    /**
     * @return an immutable list with the contents of this array. May be empty, but never {@code null}.
     */
    @Override
    public List<J> asArray()
    {
        throw typeMismatch(TypeLabel.ARRAY);
    }

    /**
     * @return an immutable map with the contents of this object. May be empty, but never {@code null}.
     */
    @Override
    public Map<String, J> asObject()
    {
        throw typeMismatch(TypeLabel.OBJECT);
    }

    public Optional<J> getObjectProperty(@NonNull String name)
    {
        return Optional.ofNullable(asObject().get(name))
            .filter(J::isAnyNonNull);
    }

    public void close()
    {
        // Pre-override method which subclasses inherit from SafeJson
    }

    protected TypeMismatchException typeMismatch(TypeLabel expected)
    {
        return new TypeMismatchException(expected, overlay.getType(element), overlay.getOffset(element));
    }
}

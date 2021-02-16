package com.github.bannmann.whisperjson;

abstract class Bool<J extends Json<J>, O extends Overlay<?>> extends Element<J, O>
{
    public static final class Exposed extends Bool<ExposedJson, Overlay.Exposed> implements ExposedJson
    {
        protected Exposed(Overlay.Exposed overlay, int element, boolean value)
        {
            super(value, overlay, element);
        }
    }

    public static final class Safe extends Bool<SafeJson, Overlay.Safe> implements SafeJson
    {
        protected Safe(Overlay.Safe overlay, int element, boolean value)
        {
            super(value, overlay, element);
        }
    }

    private final boolean value;

    protected Bool(boolean value, O overlay, int element)
    {
        super(overlay, element);
        this.value = value;
    }

    @Override
    public boolean isBoolean()
    {
        return true;
    }

    @Override
    public boolean asBoolean()
    {
        return value;
    }

    @Override
    @SuppressWarnings("java:S2162") // Json.equals() mandates equality across implementations (similar to Collections).
    public boolean equals(Object o)
    {
        if (o instanceof Bool<?, ?>)
        {
            Bool<?, ?> other = (Bool<?, ?>) o;
            return other.value == value;
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return Boolean.valueOf(value)
            .hashCode();
    }
}

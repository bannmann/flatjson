package com.github.bannmann.whisperjson;

abstract class Null<J extends Json<J>, O extends Overlay<?>> extends Element<J, O>
{
    public static final class Exposed extends Null<ExposedJson, Overlay.Exposed> implements ExposedJson
    {
        protected Exposed(Overlay.Exposed overlay, int element)
        {
            super(overlay, element);
        }
    }

    public static final class Safe extends Null<SafeJson, Overlay.Safe> implements SafeJson
    {
        protected Safe(Overlay.Safe overlay, int element)
        {
            super(overlay, element);
        }
    }

    protected Null(O overlay, int element)
    {
        super(overlay, element);
    }

    @Override
    public boolean isNull()
    {
        return true;
    }

    @Override
    public boolean equals(Object o)
    {
        return o instanceof Null<?, ?>;
    }

    @Override
    public int hashCode()
    {
        return getClass().getName()
            .hashCode();
    }
}

package com.github.bannmann.whisperjson;

abstract class Bool<J extends Json<J>> implements Json<J>
{
    public static final class Exposed extends Bool<ExposedJson> implements ExposedJson
    {
        public static final Exposed TRUE = new Exposed(true);
        public static final Exposed FALSE = new Exposed(false);

        private Exposed(boolean value)
        {
            super(value);
        }
    }

    public static final class Safe extends Bool<SafeJson> implements SafeJson
    {
        public static final Safe TRUE = new Safe(true);
        public static final Safe FALSE = new Safe(false);

        private Safe(boolean value)
        {
            super(value);
        }
    }

    private final boolean value;

    private Bool(boolean value)
    {
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
    public boolean equals(Object o)
    {
        if (o instanceof Json<?>)
        {
            Json<?> other = (Json<?>) o;
            return other.isBoolean() && other.asBoolean() == value;
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

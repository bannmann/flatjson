package com.github.bannmann.whisperjson;

abstract class Null<J extends Json<J>> implements Json<J>
{
    public static final class Exposed extends Null<ExposedJson> implements ExposedJson
    {
        public static final Exposed INSTANCE = new Exposed();

        private Exposed()
        {
        }
    }

    public static final class Safe extends Null<SafeJson> implements SafeJson
    {
        public static final Safe INSTANCE = new Safe();

        private Safe()
        {
        }
    }

    @Override
    public boolean isNull()
    {
        return true;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof Json<?>)
        {
            Json<?> other = (Json<?>) o;
            return other.isNull();
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return getClass().getName()
            .hashCode();
    }
}

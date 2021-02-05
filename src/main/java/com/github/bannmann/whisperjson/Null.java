package com.github.bannmann.whisperjson;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

abstract class Null<J extends Json<J>> implements Json<J>
{
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Exposed extends Null<ExposedJson> implements ExposedJson
    {
        public static final Exposed INSTANCE = new Exposed();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Safe extends Null<SafeJson> implements SafeJson
    {
        public static final Safe INSTANCE = new Safe();
    }

    @Override
    public boolean isNull()
    {
        return true;
    }

    @Override
    public boolean equals(Object o)
    {
        return o instanceof Null<?>;
    }

    @Override
    public int hashCode()
    {
        return getClass().getName()
            .hashCode();
    }
}

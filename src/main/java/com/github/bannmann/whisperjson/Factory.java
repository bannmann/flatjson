package com.github.bannmann.whisperjson;

@SuppressWarnings("java:S1610")
abstract class Factory<J extends Json<J>, O extends Overlay<?>, F extends Factory<J, O, F>>
{
    public static final class Exposed extends Factory<ExposedJson, Overlay.Exposed, Exposed>
    {
        @Override
        public ExposedJson createNull()
        {
            return Null.Exposed.INSTANCE;
        }

        @Override
        public ExposedJson createTrue()
        {
            return Bool.Exposed.TRUE;
        }

        @Override
        public ExposedJson createFalse()
        {
            return Bool.Exposed.FALSE;
        }

        @Override
        public ExposedJson createNumber(String asString)
        {
            return new Number.Exposed(asString);
        }

        @Override
        public ExposedJson createString(Overlay.Exposed overlay, int element)
        {
            return new Strng.Exposed(overlay, element);
        }

        @Override
        public ExposedJson createArray(Overlay.Exposed overlay, int element, Exposed factory)
        {
            return new Arry.Exposed(overlay, element, this);
        }

        @Override
        public ExposedJson createObject(Overlay.Exposed overlay, int element, Exposed factory)
        {
            return new Objct.Exposed(overlay, element, this);
        }

        @Override
        public Exposed getSelf()
        {
            return this;
        }
    }

    public static final class Safe extends Factory<SafeJson, Overlay.Safe, Safe>
    {
        @Override
        public SafeJson createNull()
        {
            return Null.Safe.INSTANCE;
        }

        @Override
        public SafeJson createTrue()
        {
            return Bool.Safe.TRUE;
        }

        @Override
        public SafeJson createFalse()
        {
            return Bool.Safe.FALSE;
        }

        @Override
        public SafeJson createNumber(String asString)
        {
            return new Number.Safe(asString);
        }

        @Override
        public SafeJson createString(Overlay.Safe overlay, int element)
        {
            return new Strng.Safe(overlay, element);
        }

        @Override
        public SafeJson createArray(Overlay.Safe overlay, int element, Safe factory)
        {
            return new Arry.Safe(overlay, element, this);
        }

        @Override
        public SafeJson createObject(Overlay.Safe overlay, int element, Safe factory)
        {
            return new Objct.Safe(overlay, element, this);
        }

        @Override
        public Safe getSelf()
        {
            return this;
        }
    }

    public final J create(O overlay, int element)
    {
        return overlay.getType(element)
            .create(overlay, element, getSelf());
    }

    public abstract J createNull();

    public abstract J createTrue();

    public abstract J createFalse();

    public abstract J createNumber(String asString);

    public abstract J createString(O overlay, int element);

    public abstract J createArray(O overlay, int element, F factory);

    public abstract J createObject(O overlay, int element, F factory);

    public abstract F getSelf();
}

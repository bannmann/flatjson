package com.github.bannmann.whisperjson;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("java:S1610")
abstract class Factory<J extends Json<J>, O extends Overlay<T>, F extends Factory<J, O, F, T>, T extends Text<T>>
{
    public static final class Exposed extends Factory<ExposedJson, Overlay.Exposed, Exposed, Text.Exposed>
    {
        @Override
        public ExposedJson createNull(Overlay.Exposed overlay, int element)
        {
            return new Null.Exposed(overlay, element);
        }

        @Override
        public ExposedJson createTrue(Overlay.Exposed overlay, int element)
        {
            return new Bool.Exposed(overlay, element, true);
        }

        @Override
        public ExposedJson createFalse(Overlay.Exposed overlay, int element)
        {
            return new Bool.Exposed(overlay, element, false);
        }

        @Override
        public ExposedJson createNumber(Overlay.Exposed overlay, int element)
        {
            return new Number.Exposed(overlay, element);
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

    public static final class Safe extends Factory<SafeJson, Overlay.Safe, Safe, Text.Safe> implements AutoCloseable
    {
        private final List<Strng.Safe> stringElements = new LinkedList<>();

        @Override
        public SafeJson createNull(Overlay.Safe overlay, int element)
        {
            return new Null.Safe(overlay, element);
        }

        @Override
        public SafeJson createTrue(Overlay.Safe overlay, int element)
        {
            return new Bool.Safe(overlay, element, true);
        }

        @Override
        public SafeJson createFalse(Overlay.Safe overlay, int element)
        {
            return new Bool.Safe(overlay, element, false);
        }

        @Override
        public SafeJson createNumber(Overlay.Safe overlay, int element)
        {
            return new Number.Safe(overlay, element);
        }

        @Override
        public SafeJson createString(Overlay.Safe overlay, int element)
        {
            Strng.Safe result = new Strng.Safe(overlay, element);
            stringElements.add(result);
            return result;
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

        @Override
        public void close()
        {
            for (Strng.Safe stringElement : stringElements)
            {
                stringElement.close();
            }
            stringElements.clear();
        }
    }

    public final J create(O overlay, int element)
    {
        return overlay.getType(element)
            .create(overlay, element, getSelf());
    }

    public abstract J createNull(O overlay, int element);

    public abstract J createTrue(O overlay, int element);

    public abstract J createFalse(O overlay, int element);

    public abstract J createNumber(O overlay, int element);

    public abstract J createString(O overlay, int element);

    public abstract J createArray(O overlay, int element, F factory);

    public abstract J createObject(O overlay, int element, F factory);

    public abstract F getSelf();
}

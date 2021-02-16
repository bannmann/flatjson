package com.github.bannmann.whisperjson;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableList;

abstract class Arry<J extends Json<J>, O extends Overlay<T>, F extends Factory<J, O, F, T>, T extends Text<T>>
    extends Structure<J, O, F, T>
{
    public static final class Exposed extends Arry<ExposedJson, Overlay.Exposed, Factory.Exposed, Text.Exposed>
        implements ExposedJson
    {
        public Exposed(Overlay.Exposed overlay, int element, Factory.Exposed factory)
        {
            super(overlay, element, factory);
        }
    }

    public static final class Safe extends Arry<SafeJson, Overlay.Safe, Factory.Safe, Text.Safe> implements SafeJson
    {
        public Safe(Overlay.Safe overlay, int element, Factory.Safe factory)
        {
            super(overlay, element, factory);
        }

        @Override
        public void close()
        {
            if (element == 0)
            {
                overlay.close();
                factory.close();
            }
        }
    }

    private ImmutableList<J> list;

    private Arry(O overlay, int element, F factory)
    {
        super(overlay, element, factory);
    }

    @Override
    public boolean isArray()
    {
        return true;
    }

    @Override
    public List<J> asArray()
    {
        if (list == null)
        {
            list = createList();
        }
        return list;
    }

    private ImmutableList<J> createList()
    {
        ImmutableList.Builder<J> result = ImmutableList.builder();
        int e = element + 1;
        while (e <= element + overlay.getChildCount(element))
        {
            result.add(factory.create(overlay, e));
            e += overlay.getChildCount(e) + 1;
        }
        return result.build();
    }

    @Override
    @SuppressWarnings("java:S2162") // Json.equals() mandates equality across implementations (similar to Collections).
    public boolean equals(Object o)
    {
        if (o instanceof Arry<?, ?, ?, ?>)
        {
            Arry<?, ?, ?, ?> other = (Arry<?, ?, ?, ?>) o;
            return Objects.equals(other.asArray(), asArray());
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return asArray().hashCode();
    }
}

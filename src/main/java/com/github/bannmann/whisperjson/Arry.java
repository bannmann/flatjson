package com.github.bannmann.whisperjson;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableList;

abstract class Arry<J extends Json<J>, O extends Overlay<?>, F extends Factory<J, O, F>> extends Structure<J, O, F>
{
    public static final class Exposed extends Arry<ExposedJson, Overlay.Exposed, Factory.Exposed> implements ExposedJson
    {
        public Exposed(Overlay.Exposed overlay, int element, Factory.Exposed factory)
        {
            super(overlay, element, factory);
        }
    }

    public static final class Safe extends Arry<SafeJson, Overlay.Safe, Factory.Safe> implements SafeJson
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

    private List<J> list;

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

    private List<J> createList()
    {
        ImmutableList.Builder<J> result = ImmutableList.builder();
        int e = element + 1;
        while (e <= element + overlay.getNested(element))
        {
            result.add(factory.create(overlay, e));
            e += overlay.getNested(e) + 1;
        }
        return result.build();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof Arry<?, ?, ?>)
        {
            Arry<?, ?, ?> other = (Arry<?, ?, ?>) o;
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

package com.github.bannmann.whisperjson;

import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;

abstract class Objct<J extends Json<J>, O extends Overlay<T>, F extends Factory<J, O, F, T>, T extends Text<T>>
    extends Structure<J, O, F, T>
{
    public static final class Exposed extends Objct<ExposedJson, Overlay.Exposed, Factory.Exposed, Text.Exposed>
        implements ExposedJson
    {
        public Exposed(Overlay.Exposed overlay, int element, Factory.Exposed factory)
        {
            super(overlay, element, factory);
        }
    }

    public static final class Safe extends Objct<SafeJson, Overlay.Safe, Factory.Safe, Text.Safe> implements SafeJson
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

    private Map<String, J> map;

    private Objct(O overlay, int element, F factory)
    {
        super(overlay, element, factory);
    }

    @Override
    public boolean isObject()
    {
        return true;
    }

    @Override
    public Map<String, J> asObject()
    {
        if (map == null)
        {
            map = createMap();
        }
        return map;
    }

    private Map<String, J> createMap()
    {
        ImmutableMap.Builder<String, J> result = ImmutableMap.builder();
        int e = element + 1;
        while (e <= element + overlay.getNested(element))
        {
            String key = overlay.getUnescapedText(e)
                .asString();
            result.put(key, factory.create(overlay, e + 1));
            e += overlay.getNested(e + 1) + 2;
        }
        return result.build();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof Objct<?, ?, ?, ?>)
        {
            Objct<?, ?, ?, ?> other = (Objct<?, ?, ?, ?>) o;
            return Objects.equals(other.asObject(), asObject());
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return asObject().hashCode();
    }
}

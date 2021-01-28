package com.github.bannmann.whisperjson;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

abstract class Objct<J extends Json<J>, O extends Overlay<?>, F extends Factory<J, O>> extends Structure<J, O, F>
{
    public static final class Exposed extends Objct<ExposedJson, Overlay.Exposed, Factory.Exposed>
        implements ExposedJson
    {
        public Exposed(Overlay.Exposed overlay, int element, Factory.Exposed factory)
        {
            super(overlay, element, factory);
        }
    }

    public static final class Safe extends Objct<SafeJson, Overlay.Safe, Factory.Safe> implements SafeJson
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
            }
        }
    }

    private Map<String, J> map;

    protected Objct(O overlay, int element, F factory)
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
        Map<String, J> result = new LinkedHashMap<>();
        int e = element + 1;
        while (e <= element + overlay.getNested(element))
        {
            String key = overlay.getUnescapedText(e)
                .asString();
            result.put(key, factory.create(overlay, e + 1));
            e += overlay.getNested(e + 1) + 2;
        }
        return result;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof Json<?>)
        {
            Json<?> other = (Json<?>) o;
            return other.isObject() && Objects.equals(other.asObject(), asObject());
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return asObject().hashCode();
    }
}

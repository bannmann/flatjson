package com.github.bannmann.whisperjson;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

abstract class Structure<J extends Json<?>, Q extends Quux<J>> implements Json<J>
{
    static class Array<J extends Json<?>, Q extends Quux<J>> extends Structure<J, Q>
    {
        private List<J> list;

        Array(Overlay overlay, int element, Q quux)
        {
            super(overlay, element, quux);
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
            List<J> result = new ArrayList<>();
            int e = element + 1;
            while (e <= element + overlay.getNested(element))
            {
                result.add(quux.create(overlay, e));
                e += overlay.getNested(e) + 1;
            }
            return result;
        }

        @Override
        public boolean equals(java.lang.Object o)
        {
            if (o instanceof Json<?>)
            {
                Json<?> other = (Json<?>) o;
                return other.isArray() && Objects.equals(other.asArray(), asArray());
            }
            return false;
        }

        @Override
        public int hashCode()
        {
            return asArray().hashCode();
        }
    }

    static class ExposedArray extends Array<ExposedJson, ExposedQuux> implements ExposedJson
    {
        ExposedArray(Overlay overlay, int element, ExposedQuux quux)
        {
            super(overlay, element, quux);
        }
    }

    static class SafeArray extends Array<SafeJson, SafeQuux> implements SafeJson
    {
        SafeArray(Overlay overlay, int element, SafeQuux quux)
        {
            super(overlay, element, quux);
        }
    }

    static class Object<J extends Json<?>, Q extends Quux<J>> extends Structure<J, Q>
    {
        private Map<String, J> map;

        Object(Overlay overlay, int element, Q quux)
        {
            super(overlay, element, quux);
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
                String key = overlay.getUnescapedString(e)
                    .asString();
                result.put(key, quux.create(overlay, e + 1));
                e += overlay.getNested(e + 1) + 2;
            }
            return result;
        }

        @Override
        public boolean equals(java.lang.Object o)
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

    static class ExposedObject extends Object<ExposedJson, ExposedQuux> implements ExposedJson
    {
        ExposedObject(Overlay overlay, int element, ExposedQuux quux)
        {
            super(overlay, element, quux);
        }
    }

    static class SafeObject extends Object<SafeJson, SafeQuux> implements SafeJson
    {
        SafeObject(Overlay overlay, int element, SafeQuux quux)
        {
            super(overlay, element, quux);
        }
    }

    protected final Overlay overlay;
    protected final int element;
    protected final Q quux;

    Structure(Overlay overlay, int element, Q quux)
    {
        this.overlay = overlay;
        this.element = element;
        this.quux = quux;
    }
}

package com.github.bannmann.whisperjson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.github.bannmann.whisperjson.text.Text;

class Parsed extends Json
{
    static class Strng extends Parsed
    {
        private Text text;

        Strng(Overlay overlay, int element)
        {
            super(overlay, element);
        }

        @Override
        public boolean isString()
        {
            return true;
        }

        private Text getText()
        {
            if (text == null)
            {
                text = overlay.getUnescapedString(element);
            }
            return text;
        }

        @Override
        public String asString()
        {
            return getText().asString();
        }

        @Override
        public char[] asCharArray()
        {
            return getText().asCharArray();
        }

        @Override
        public boolean equals(java.lang.Object o)
        {
            if (o instanceof Json)
            {
                Json other = (Json) o;
                return other.isString() && Arrays.equals(other.asCharArray(), asCharArray());
            }
            return false;
        }

        @Override
        public int hashCode()
        {
            return Arrays.hashCode(asCharArray());
        }
    }

    static class Array extends Parsed
    {
        private List<Json> list;

        Array(Overlay overlay, int element)
        {
            super(overlay, element);
        }

        @Override
        public boolean isArray()
        {
            return true;
        }

        @Override
        public List<Json> asArray()
        {
            if (list == null)
            {
                list = createList();
            }
            return list;
        }

        private List<Json> createList()
        {
            List<Json> result = new ArrayList<>();
            int e = element + 1;
            while (e <= element + overlay.getNested(element))
            {
                result.add(create(overlay, e));
                e += overlay.getNested(e) + 1;
            }
            return result;
        }

        @Override
        public boolean equals(java.lang.Object o)
        {
            if (o instanceof Json)
            {
                Json other = (Json) o;
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

    static class Object extends Parsed
    {
        private Map<String, Json> map;

        Object(Overlay overlay, int element)
        {
            super(overlay, element);
        }

        @Override
        public boolean isObject()
        {
            return true;
        }

        @Override
        public Map<String, Json> asObject()
        {
            if (map == null)
            {
                map = createMap();
            }
            return map;
        }

        private Map<String, Json> createMap()
        {
            Map<String, Json> result = new LinkedHashMap<>();
            int e = element + 1;
            while (e <= element + overlay.getNested(element))
            {
                String key = overlay.getUnescapedString(e)
                    .asString();
                result.put(key, create(overlay, e + 1));
                e += overlay.getNested(e + 1) + 2;
            }
            return result;
        }

        @Override
        public boolean equals(java.lang.Object o)
        {
            if (o instanceof Json)
            {
                Json other = (Json) o;
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

    protected final Overlay overlay;
    protected final int element;

    Parsed(Overlay overlay, int element)
    {
        this.overlay = overlay;
        this.element = element;
    }
}

package com.github.bannmann.whisperjson;

import java.util.List;
import java.util.Map;

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
    }

    static class Array extends Parsed
    {
        private List<Json> array;

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
            if (array == null)
            {
                array = createArray();
            }
            return array;
        }

        private List<Json> createArray()
        {
            List<Json> result = new JsonList<>();
            int e = element + 1;
            while (e <= element + overlay.getNested(element))
            {
                result.add(create(overlay, e));
                e += overlay.getNested(e) + 1;
            }
            return result;
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
            Map<String, Json> result = new JsonMap<>();
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
    }

    protected final Overlay overlay;
    protected final int element;

    Parsed(Overlay overlay, int element)
    {
        this.overlay = overlay;
        this.element = element;
    }

    @Override
    public void accept(Visitor visitor)
    {
        overlay.accept(element, visitor);
    }

    @Override
    public final String toString()
    {
        return getClass().getSimpleName();
    }
}

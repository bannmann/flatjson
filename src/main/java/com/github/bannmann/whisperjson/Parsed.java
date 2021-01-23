package com.github.bannmann.whisperjson;

import java.util.Arrays;

import com.github.bannmann.whisperjson.text.Text;

abstract class Parsed<J extends Json<?>> implements Json<J>
{
    private static class Strng<J extends Json<?>> extends Parsed<J>
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

        protected Text getText()
        {
            if (text == null)
            {
                text = overlay.getUnescapedString(element);
            }
            return text;
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
                Json<?> other = (Json<?>) o;
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

    static class ExposedStrng extends Strng<ExposedJson> implements ExposedJson
    {
        public ExposedStrng(Overlay overlay, int element)
        {
            super(overlay, element);
        }

        @Override
        public String asString()
        {
            return getText().asString();
        }
    }

    static class SafeStrng extends Strng<SafeJson> implements SafeJson
    {
        public SafeStrng(Overlay overlay, int element)
        {
            super(overlay, element);
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

package com.github.bannmann.whisperjson;

import java.util.Arrays;
import java.util.Optional;

import com.github.bannmann.whisperjson.text.CharArrayText;
import com.github.bannmann.whisperjson.text.StringText;
import com.github.bannmann.whisperjson.text.Text;

abstract class Parsed<J extends Json<?>, O extends Overlay<?>> implements Json<J>
{
    private static class Strng<J extends Json<?>, O extends Overlay<T>, T extends Text<T>> extends Parsed<J, O>
    {
        private T text;

        Strng(O overlay, int element)
        {
            super(overlay, element);
        }

        @Override
        public boolean isString()
        {
            return true;
        }

        protected final T getOrCreateText()
        {
            if (text == null)
            {
                text = overlay.getUnescapedText(element);
            }
            return text;
        }

        protected final Optional<T> getText()
        {
            return Optional.ofNullable(text);
        }

        @Override
        public char[] asCharArray()
        {
            return getOrCreateText().asCharArray();
        }

        @Override
        public boolean equals(java.lang.Object o)
        {
            if (o instanceof Strng)
            {
                Strng<?, ?, ?> other = (Strng<?, ?, ?>) o;
                return getOrCreateText().equals(other.getOrCreateText());
            }
            return false;
        }

        @Override
        public int hashCode()
        {
            return Arrays.hashCode(asCharArray());
        }
    }

    static class ExposedStrng extends Strng<ExposedJson, Overlay.Exposed, StringText> implements ExposedJson
    {
        public ExposedStrng(Overlay.Exposed overlay, int element)
        {
            super(overlay, element);
        }

        @Override
        public String asString()
        {
            return getOrCreateText().asString();
        }
    }

    static class SafeStrng extends Strng<SafeJson, Overlay.Safe, CharArrayText> implements SafeJson
    {
        public SafeStrng(Overlay.Safe overlay, int element)
        {
            super(overlay, element);
        }

        @Override
        public void close()
        {
            if (element == 0)
            {
                overlay.close();
            }

            getText().ifPresent(CharArrayText::close);
        }
    }

    protected final O overlay;
    protected final int element;

    Parsed(O overlay, int element)
    {
        this.overlay = overlay;
        this.element = element;
    }
}

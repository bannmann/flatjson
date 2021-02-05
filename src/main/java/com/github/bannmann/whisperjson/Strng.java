package com.github.bannmann.whisperjson;

import java.util.Optional;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
abstract class Strng<J extends Json<J>, O extends Overlay<T>, T extends Text<T>> implements Json<J>
{
    public static class Exposed extends Strng<ExposedJson, Overlay.Exposed, Text.Exposed> implements ExposedJson
    {
        public Exposed(Overlay.Exposed overlay, int element)
        {
            super(overlay, element);
        }

        @Override
        public String asString()
        {
            return getOrCreateText().asString();
        }
    }

    public static class Safe extends Strng<SafeJson, Overlay.Safe, Text.Safe> implements SafeJson
    {
        public Safe(Overlay.Safe overlay, int element)
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

            getText().ifPresent(Text.Safe::close);
        }
    }

    protected final O overlay;
    protected final int element;
    private T text;

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
    public boolean equals(Object o)
    {
        if (o instanceof Strng<?, ?, ?>)
        {
            Strng<?, ?, ?> other = (Strng<?, ?, ?>) o;
            return getOrCreateText().equals(other.getOrCreateText());
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return getOrCreateText().hashCode();
    }
}

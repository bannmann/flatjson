package com.github.bannmann.whisperjson;

import java.util.Optional;

import com.google.common.annotations.VisibleForTesting;

abstract class Strng<J extends Json<J>, O extends Overlay<T>, T extends Text<T>> extends Element<J, O>
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
        private boolean closed;

        public Safe(Overlay.Safe overlay, int element)
        {
            super(overlay, element);
        }

        @Override
        public char[] asCharArray()
        {
            return getOrCreateText().asCharArray();
        }

        @Override
        public SensitiveText asSensitiveText()
        {
            return getOrCreateText().asSensitiveText();
        }

        @Override
        public void close()
        {
            if (element == 0)
            {
                overlay.close();
            }

            getText().ifPresent(Text.Safe::close);

            closed = true;
        }

        @Override
        protected Text.Safe getOrCreateText()
        {
            if (closed)
            {
                throw new IllegalStateException();
            }
            return super.getOrCreateText();
        }
    }

    @VisibleForTesting
    T text;

    private Strng(O overlay, int element)
    {
        super(overlay, element);
    }

    @Override
    public boolean isString()
    {
        return true;
    }

    protected T getOrCreateText()
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

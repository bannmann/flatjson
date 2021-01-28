package com.github.bannmann.whisperjson;

import java.util.function.Function;

class TextBuilder implements AutoCloseable
{
    private final StringBuilder contents;

    public TextBuilder(int initialCapacity)
    {
        contents = new StringBuilder(initialCapacity);
    }

    public void append(char... characters)
    {
        contents.append(characters);
    }

    public void append(CharSequence charSequence)
    {
        contents.append(charSequence);
    }

    public <T extends Text<T>> T build(Function<char[], T> constructor)
    {
        return constructor.apply(StringBuilders.copyToCharArray(contents));
    }

    @Override
    public void close()
    {
        contents.setLength(0);
    }
}

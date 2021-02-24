package com.github.bannmann.whisperjson;

import java.io.IOException;
import java.io.Reader;
import java.util.function.Function;

class TextBuilder implements AutoCloseable
{
    private final StringBuilder contents;

    public TextBuilder(int initialCapacity)
    {
        contents = new StringBuilder(initialCapacity);
    }

    public TextBuilder append(char... characters)
    {
        contents.append(characters);
        return this;
    }

    public TextBuilder append(CharSequence charSequence)
    {
        contents.append(charSequence);
        return this;
    }

    public TextBuilder appendAll(Reader reader) throws IOException
    {
        char[] buffer = new char[100];
        try
        {
            int charsRead;
            while ((charsRead = reader.read(buffer)) != -1)
            {
                contents.append(buffer, 0, charsRead);
            }
            return this;
        }
        finally
        {
            Credentials.wipe(buffer);
        }
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

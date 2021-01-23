package com.github.bannmann.whisperjson.text;

public class TextBuilder implements AutoCloseable
{
    private final StringBuilder contents;

    public TextBuilder()
    {
        this(0);
    }

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

    public Text build()
    {
        int length = contents.length();
        char[] result = new char[length];
        contents.getChars(0, length, result, 0);
        return new CharArrayText(result);
    }

    @Override
    public void close()
    {
        contents.setLength(0);
    }
}

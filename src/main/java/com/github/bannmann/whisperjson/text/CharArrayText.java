package com.github.bannmann.whisperjson.text;

import com.github.bannmann.whisperjson.ParseException;

public class CharArrayText implements Text
{
    private final char[] contents;

    public CharArrayText(char[] contents)
    {
        if (contents == null)
        {
            throw new ParseException("cannot parse null");
        }
        this.contents = contents;
    }

    @Override
    public char charAt(int index)
    {
        return contents[index];
    }

    @Override
    public int length()
    {
        return contents.length;
    }

    @Override
    public Text getPart(int beginIndex, int endIndex)
    {
        int length = endIndex - beginIndex;
        char[] result = new char[length];

        System.arraycopy(contents, beginIndex, result, 0, length);
        return new CharArrayText(result);
    }

    @Override
    public char[] asCharArray()
    {
        return contents;
    }

    @Override
    public String asString()
    {
        return new String(contents);
    }
}

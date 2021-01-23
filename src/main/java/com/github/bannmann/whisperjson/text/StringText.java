package com.github.bannmann.whisperjson.text;

import com.github.bannmann.whisperjson.ParseException;

public class StringText implements Text
{
    private final String contents;

    public StringText(String contents)
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
        return contents.charAt(index);
    }

    @Override
    public int length()
    {
        return contents.length();
    }

    @Override
    public Text getPart(int beginIndex, int endIndex)
    {
        String substring = contents.substring(beginIndex, endIndex);
        return new StringText(substring);
    }

    @Override
    public char[] asCharArray()
    {
        return contents.toCharArray();
    }

    @Override
    public String asString()
    {
        return contents;
    }
}

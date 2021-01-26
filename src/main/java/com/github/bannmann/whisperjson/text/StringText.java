package com.github.bannmann.whisperjson.text;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.github.bannmann.whisperjson.ParseException;

public class StringText extends Text<StringText>
{
    private static class CharacterIterator implements Iterator<Character>
    {
        private final String string;
        private int position;

        private CharacterIterator(String string)
        {
            this.string = string;
        }

        @Override
        public boolean hasNext()
        {
            return position < string.length();
        }

        @Override
        public Character next()
        {
            try
            {
                return string.charAt(position++);
            }
            catch (IndexOutOfBoundsException e)
            {
                throw new NoSuchElementException(e.getMessage());
            }
        }
    }

    private final String contents;

    public StringText(String contents)
    {
        if (contents == null)
        {
            throw new ParseException("cannot parse null");
        }
        this.contents = contents;
    }

    public StringText(char[] contents)
    {
        if (contents == null)
        {
            throw new ParseException("cannot parse null");
        }
        this.contents = new String(contents);
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
    public StringText getPart(int beginIndex, int endIndex)
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

    @Override
    public Iterator<Character> getCharacters()
    {
        return new CharacterIterator(contents);
    }

    @Override
    protected StringText newInstance(char[] chars)
    {
        return new StringText(new String(chars));
    }

    @Override
    protected StringText self()
    {
        return this;
    }
}

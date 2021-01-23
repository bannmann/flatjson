package com.github.bannmann.whisperjson.text;

import java.util.Iterator;

import com.github.bannmann.whisperjson.ParseException;

public class CharArrayText extends Text
{
    private static class CharacterIterator implements Iterator<Character>
    {
        private final char[] array;
        private int position;

        private CharacterIterator(char[] array)
        {
            this.array = array;
        }

        @Override
        public boolean hasNext()
        {
            return position < array.length;
        }

        @Override
        public Character next()
        {
            return array[position++];
        }
    }

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

    @Override
    public Iterator<Character> getCharacters()
    {
        return new CharacterIterator(contents);
    }
}

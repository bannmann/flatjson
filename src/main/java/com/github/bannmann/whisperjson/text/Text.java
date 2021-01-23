package com.github.bannmann.whisperjson.text;

import java.util.Iterator;

public abstract class Text
{
    public abstract char charAt(int index);

    public abstract int length();

    public abstract Text getPart(int beginIndex, int endIndex);

    public abstract char[] asCharArray();

    public abstract String asString();

    public abstract Iterator<Character> getCharacters();

    @Override
    public final boolean equals(Object o)
    {
        if (o instanceof Text)
        {
            Iterator<Character> chars = this.getCharacters();
            Iterator<Character> otherChars = ((Text) o).getCharacters();
            while (chars.hasNext() && otherChars.hasNext())
            {
                if (!chars.next()
                    .equals(otherChars.next()))
                {
                    return false;
                }
            }
            return !chars.hasNext() && !otherChars.hasNext();
        }
        return false;
    }

    @Override
    public final int hashCode()
    {
        int result = 14;
        Iterator<Character> chars = getCharacters();
        while (chars.hasNext())
        {
            result = 37 * result + (int) chars.next();
        }
        return result;
    }
}

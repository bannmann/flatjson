package com.github.bannmann.whisperjson.text;

import java.util.Iterator;

public abstract class Text<T extends Text<T>>
{
    public abstract char charAt(int index);

    public abstract int length();

    public abstract T getPart(int beginIndex, int endIndex);

    /**
     * @return a new array with a copy of this Text's contents
     */
    public abstract char[] asCharArray();

    public abstract String asString();

    public abstract Iterator<Character> getCharacters();

    @Override
    public final boolean equals(Object o)
    {
        if (o instanceof Text)
        {
            Iterator<Character> chars = this.getCharacters();
            Iterator<Character> otherChars = ((Text<?>) o).getCharacters();
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

    public T escape()
    {
        try (TextBuilder result = new TextBuilder(length()))
        {
            boolean returnSelf = true;
            int i = 0;
            while (i < length())
            {
                char c = charAt(i);
                boolean didEscape = true;
                switch (c)
                {
                    case '\\':
                        result.append('\\', '\\');
                        break;
                    case '"':
                        result.append('\\', '\"');
                        break;
                    case '\b':
                        result.append('\\', 'b');
                        break;
                    case '\f':
                        result.append('\\', 'f');
                        break;
                    case '\n':
                        result.append('\\', 'n');
                        break;
                    case '\r':
                        result.append('\\', 'r');
                        break;
                    case '\t':
                        result.append('\\', 't');
                        break;
                    default:
                        if (c < 32 || c > 126)
                        {
                            result.append('\\', 'u');
                            result.append(Integer.toUnsignedString(c, 16));
                        }
                        else
                        {
                            didEscape = false;
                            result.append(c);
                        }
                }
                if (didEscape)
                {
                    returnSelf = false;
                }
                i++;
            }

            if (returnSelf)
            {
                return self();
            }

            return result.build(this::newInstance);
        }
    }

    protected abstract T self();

    protected abstract T newInstance(char[] chars);

    public T unescape()
    {
        try (TextBuilder result = new TextBuilder(length()))
        {
            boolean returnSelf = true;
            int i = 0;
            while (i < length())
            {
                if (charAt(i) == '\\')
                {
                    returnSelf = false;
                    i++;
                    switch (charAt(i))
                    {
                        case '\\':
                            result.append('\\');
                            break;
                        case '/':
                            result.append('/');
                            break;
                        case '"':
                            result.append('"');
                            break;
                        case 'b':
                            result.append('\b');
                            break;
                        case 'f':
                            result.append('\f');
                            break;
                        case 'n':
                            result.append('\n');
                            break;
                        case 'r':
                            result.append('\r');
                            break;
                        case 't':
                            result.append('\t');
                            break;
                        case 'u':
                            result.append(Character.toChars(Integer.parseInt(getPart(i + 1, i + 5).asString(), 16)));
                            i += 4;
                            break;
                    }
                }
                else
                {
                    result.append(charAt(i));
                }
                i++;
            }

            if (returnSelf)
            {
                return self();
            }

            return result.build(this::newInstance);
        }
    }
}

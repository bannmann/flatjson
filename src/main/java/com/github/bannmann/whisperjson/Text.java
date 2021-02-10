package com.github.bannmann.whisperjson;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import com.google.common.annotations.VisibleForTesting;

abstract class Text<T extends Text<T>>
{
    @RequiredArgsConstructor(access = AccessLevel.PUBLIC)
    public static class Exposed extends Text<Exposed>
    {
        @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
        private static class CharacterIterator implements Iterator<Character>
        {
            private final String string;
            private int position;

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

        @NonNull
        private final String contents;

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
        public Exposed getPart(int beginIndex, int endIndex)
        {
            String substring = contents.substring(beginIndex, endIndex);
            return new Exposed(substring);
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
        protected Exposed newInstance(char[] chars)
        {
            return new Exposed(new String(chars));
        }

        @Override
        protected Exposed self()
        {
            return this;
        }
    }

    public static class Safe extends Text<Safe> implements AutoCloseable
    {
        @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
        private static class CharacterIterator implements Iterator<Character>
        {
            private final char[] array;
            private int position;

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

        @VisibleForTesting
        char[] contents;

        public Safe(@NonNull char[] contents)
        {
            this.contents = contents;
        }

        @Override
        public char charAt(int index)
        {
            return obtainContents()[index];
        }

        @Override
        public int length()
        {
            return obtainContents().length;
        }

        @Override
        public Safe getPart(int beginIndex, int endIndex)
        {
            int length = endIndex - beginIndex;
            char[] result = new char[length];

            System.arraycopy(obtainContents(), beginIndex, result, 0, length);
            return new Safe(result);
        }

        public SensitiveText asSensitiveText()
        {
            return new SensitiveText(obtainContents());
        }

        @Override
        public char[] asCharArray()
        {
            return Arrays.copyOf(obtainContents(), obtainContents().length);
        }

        @Override
        public String asString()
        {
            return new String(obtainContents());
        }

        @Override
        public Iterator<Character> getCharacters()
        {
            return new CharacterIterator(obtainContents());
        }

        @Override
        protected Safe newInstance(@NonNull char[] chars)
        {
            return new Safe(chars);
        }

        @Override
        protected Safe self()
        {
            return this;
        }

        @Override
        public void close()
        {
            Credentials.wipe(contents);
            contents = null;
        }

        private char[] obtainContents()
        {
            if (contents == null)
            {
                throw new IllegalStateException();
            }
            return contents;
        }
    }

    /**
     * @throws IndexOutOfBoundsException if {@code index} is negative or not less than the length of this Text.
     */
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
                char c = charAt(i);
                if (c == '\\')
                {
                    returnSelf = false;
                    i++;
                    char escapeChar = charAt(i);
                    switch (escapeChar)
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
                        default:
                            throw new JsonSyntaxException("illegal escape char", escapeChar, i);
                    }
                }
                else
                {
                    result.append(c);
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

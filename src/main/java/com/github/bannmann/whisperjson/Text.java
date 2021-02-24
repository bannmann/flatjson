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
            @SuppressWarnings("java:S2272")
            public Character next()
            {
                try
                {
                    char result = string.charAt(position);
                    position++;
                    return result;
                }
                catch (IndexOutOfBoundsException e)
                {
                    throw createNoSuchElementException(e);
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
            @SuppressWarnings("java:S2272")
            public Character next()
            {
                try
                {
                    char result = array[position];
                    position++;
                    return result;
                }
                catch (ArrayIndexOutOfBoundsException e)
                {
                    throw createNoSuchElementException(e);
                }
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

    private static NoSuchElementException createNoSuchElementException(Exception cause)
    {
        NoSuchElementException exception = new NoSuchElementException(cause.getMessage());
        exception.initCause(cause);
        return exception;
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
}

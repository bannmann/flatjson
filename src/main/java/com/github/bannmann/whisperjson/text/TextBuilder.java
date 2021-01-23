package com.github.bannmann.whisperjson.text;

public class TextBuilder
{
    private final char[] contents;
    private int position;

    public TextBuilder(int capacity)
    {
        contents = new char[capacity];
        position = 0;
    }

    public void append(char... characters)
    {
        for (char character : characters)
        {
            contents[position++] = character;
        }
    }

    public void append(String string)
    {
        for (int i = 0; i < string.length(); i++)
        {
            append(string.charAt(i));
        }
    }

    public Text build()
    {
        int length = position;
        char[] result = new char[length];

        System.arraycopy(contents, 0, result, 0, length);
        return new CharArrayText(result);
    }
}

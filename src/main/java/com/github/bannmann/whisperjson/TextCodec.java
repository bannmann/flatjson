package com.github.bannmann.whisperjson;

import java.util.function.Function;

import lombok.experimental.UtilityClass;

import com.google.common.primitives.Chars;

@UtilityClass
class TextCodec
{
    private static final char[] ESCAPE_CHARS = new char[]{ '\\', '/', '"', 'b', 'f', 'n', 'r', 't' };
    private static final char[] UNESCAPED_CHARS = new char[]{ '\\', '/', '"', '\b', '\f', '\n', '\r', '\t' };

    public <T extends Text<T>> T unescape(T original, Function<char[], T> textConstructor)
    {
        try (TextBuilder builder = new TextBuilder(original.length()))
        {
            return unescape(original, builder, textConstructor);
        }
    }

    private <T extends Text<T>> T unescape(T original, TextBuilder builder, Function<char[], T> textConstructor)
    {
        boolean returnOriginal = true;
        int i = 0;
        while (i < original.length())
        {
            char c = original.charAt(i);
            if (c == '\\')
            {
                returnOriginal = false;
                i++;
                char escapeChar = original.charAt(i);
                char unescapedChar = getUnescapedChar(escapeChar);
                if (unescapedChar > 0)
                {
                    builder.append(unescapedChar);
                }
                else if (escapeChar == 'u')
                {
                    builder.append(parseUnicodeHex(original, i));
                    i += 4;
                }
                else
                {
                    throw new JsonSyntaxException("illegal escape char", escapeChar, i);
                }
            }
            else
            {
                builder.append(c);
            }
            i++;
        }

        if (returnOriginal)
        {
            return original;
        }

        return builder.build(textConstructor);
    }

    private char getUnescapedChar(char escapeChar)
    {
        int escapeCharIndex = Chars.indexOf(ESCAPE_CHARS, escapeChar);
        if (escapeCharIndex >= 0)
        {
            return UNESCAPED_CHARS[escapeCharIndex];
        }
        return 0;
    }

    private <T extends Text<T>> char[] parseUnicodeHex(T original, int i)
    {
        return Character.toChars(Integer.parseInt(original.getPart(i + 1, i + 5)
            .asString(), 16));
    }
}

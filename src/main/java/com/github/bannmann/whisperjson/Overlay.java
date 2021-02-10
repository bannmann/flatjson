package com.github.bannmann.whisperjson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.NonNull;

import com.google.common.annotations.VisibleForTesting;

abstract class Overlay<T extends Text<T>>
{
    public static final class Exposed extends Overlay<Text.Exposed>
    {
        public Exposed(@NonNull String raw)
        {
            super(new Text.Exposed(raw));
        }
    }

    public static final class Safe extends Overlay<Text.Safe> implements AutoCloseable
    {
        public Safe(@NonNull char[] raw)
        {
            super(new Text.Safe(raw));
        }

        public Safe(@NonNull Text.Safe text)
        {
            super(text);
        }

        @Override
        public void close()
        {
            text.close();

            for (int[] block : blocks)
            {
                Arrays.fill(block, 0);
            }
        }
    }

    @VisibleForTesting
    static int calculateBlockSize(int rawChars)
    {
        // make block size (in bytes) roughly equal to input size
        // (min block size is 64 B, max block size is 16 KB)
        return 4 * Math.min(Math.max(rawChars / 16, 4), 1024);
    }

    private static final int TYPE = 0;
    private static final int FROM = 1;
    private static final int TO = 2;
    private static final int NESTED = 3;

    protected final T text;
    protected final List<int[]> blocks = new ArrayList<>();
    protected final int blockSize;
    protected int element;

    private Overlay(@NonNull T text)
    {
        this.text = text;
        this.blockSize = calculateBlockSize(text.length());
        parse();
    }

    public Type getType(int element)
    {
        return Type.values()[getComponent(element, TYPE)];
    }

    public int getNested(int element)
    {
        return getComponent(element, NESTED);
    }

    public T getJson(int element)
    {
        return text.getPart(getComponent(element, FROM), getComponent(element, TO) + 1);
    }

    public int getOffset(int element)
    {
        return getComponent(element, FROM);
    }

    public T getUnescapedText(int element)
    {
        T value = text.getPart(getComponent(element, FROM) + 1, getComponent(element, TO));
        return (getType(element) == Type.STRING_ESCAPED) ? value.unescape() : value;
    }

    private void parse()
    {
        try
        {
            int last = skipWhitespace(parseValue(0));
            if (last != text.length())
            {
                throw new JsonSyntaxException("malformed json", Math.min(last, text.length()));
            }
        }
        catch (IndexOutOfBoundsException e)
        {
            throw new JsonSyntaxException("unbalanced json", e);
        }
    }

    private int parseValue(int i)
    {
        i = skipWhitespace(i);
        char c = text.charAt(i);
        switch (c)
        {
            case '"':
                return parseString(i);
            case '{':
                return parseObject(i);
            case '[':
                return parseArray(i);
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case '-':
                return parseNumber(i);
            case 't':
                return parseTrue(i);
            case 'f':
                return parseFalse(i);
            case 'n':
                return parseNull(i);
            default:
                throw new JsonSyntaxException("illegal char", c, i);
        }
    }

    private int parseNumber(int i)
    {
        int from = i;
        boolean minus = false;
        boolean leadingZero = false;
        boolean dot = false;
        boolean exponent = false;
        while (i < text.length())
        {
            char c = text.charAt(i);
            if (c == '-')
            {
                if (i > from)
                {
                    throw new JsonSyntaxException("minus inside number", i);
                }
                minus = true;
            }
            else if (c == 'e' || c == 'E')
            {
                if (exponent)
                {
                    throw new JsonSyntaxException("double exponents", i);
                }
                leadingZero = false;
                exponent = true;
                c = text.charAt(i + 1);
                if (c == '-' || c == '+')
                {
                    c = text.charAt(i + 2);
                    if (c < '0' || c > '9')
                    {
                        throw new JsonSyntaxException("invalid exponent", c, i);
                    }
                    i += 2;
                }
                else if (c >= '0' && c <= '9')
                {
                    i++;
                }
                else
                {
                    throw new JsonSyntaxException("invalid exponent", c, i);
                }
            }
            else if (c == '.')
            {
                if (dot)
                {
                    throw new JsonSyntaxException("multiple dots", i);
                }
                if (i == from || (minus && (i == from + 1)))
                {
                    throw new JsonSyntaxException("no digit before dot", i);
                }
                leadingZero = false;
                dot = true;
            }
            else if (c == '0')
            {
                if (i == from)
                {
                    leadingZero = true;
                }
            }
            else if (c >= '1' && c <= '9')
            {
                if (leadingZero)
                {
                    throw new JsonSyntaxException("leading zero", i);
                }
            }
            else
            {
                break;
            }
            i++;
        }
        if (minus && from == i - 1)
        {
            throw new JsonSyntaxException("isolated minus", i);
        }
        return createElement(Type.NUMBER, from, i - 1, 0);
    }

    private int parseString(int i)
    {
        boolean escaped = false;
        int from = i++;
        while (true)
        {
            char c = text.charAt(i);
            if (c == '"')
            {
                Type type = escaped ? Type.STRING_ESCAPED : Type.STRING;
                return createElement(type, from, i, 0);
            }
            else if (c < 32)
            {
                throw new JsonSyntaxException(String.format("illegal control char %d", (int) c), i);
            }
            else if (c == '\\')
            {
                escaped = true;
                c = text.charAt(i + 1);
                switch (c)
                {
                    case '"':
                    case '/':
                    case '\\':
                    case 'b':
                    case 'f':
                    case 'n':
                    case 'r':
                    case 't':
                        i++;
                        break;
                    case 'u':
                        expectHex(i + 2);
                        expectHex(i + 3);
                        expectHex(i + 4);
                        expectHex(i + 5);
                        i += 5;
                        break;
                    default:
                        throw new JsonSyntaxException("illegal escape char", c, i + 1);
                }
            }
            i++;
        }
    }

    private int parseArray(int i)
    {
        int count = 0;
        int e = element;
        createElement(Type.ARRAY, i);
        i++;
        while (true)
        {
            i = skipWhitespace(i);
            if (text.charAt(i) == ']')
            {
                return closeElement(e, i, element - e - 1);
            }
            if (count > 0)
            {
                expectChar(i, ',');
                i = skipWhitespace(i + 1);
            }
            i = parseValue(i);
            count++;
        }
    }

    private int parseObject(int i)
    {
        int count = 0;
        int e = element;
        createElement(Type.OBJECT, i);
        i++;
        while (true)
        {
            i = skipWhitespace(i);
            if (text.charAt(i) == '}')
            {
                return closeElement(e, i, element - e - 1);
            }
            if (count > 0)
            {
                expectChar(i, ',');
                i = skipWhitespace(i + 1);
            }
            expectChar(i, '"');
            i = parseString(i);
            i = skipWhitespace(i);
            expectChar(i, ':');
            i = skipWhitespace(i + 1);
            i = parseValue(i);
            count++;
        }
    }

    private int parseNull(int from)
    {
        int to = from;
        expectChar(++to, 'u');
        expectChar(++to, 'l');
        expectChar(++to, 'l');
        return createElement(Type.NULL, from, to, 0);
    }

    private int parseTrue(int from)
    {
        int to = from;
        expectChar(++to, 'r');
        expectChar(++to, 'u');
        expectChar(++to, 'e');
        return createElement(Type.TRUE, from, to, 0);
    }

    private int parseFalse(int from)
    {
        int to = from;
        expectChar(++to, 'a');
        expectChar(++to, 'l');
        expectChar(++to, 's');
        expectChar(++to, 'e');
        return createElement(Type.FALSE, from, to, 0);
    }

    private int skipWhitespace(int i)
    {
        while (i < text.length())
        {
            char c = text.charAt(i);
            if (c != ' ' && c != '\t' && c != '\n' && c != '\r')
            {
                break;
            }
            i++;
        }
        return i;
    }

    private void expectChar(int index, char expected)
    {
        try
        {
            char found = text.charAt(index);
            if (found != expected)
            {
                throw new JsonSyntaxException(String.format("expected char '%s', found '%s'", expected, found), index);
            }
        }
        catch (IndexOutOfBoundsException e)
        {
            throw new JsonSyntaxException(String.format("expected char '%s', found EOF", expected), index);
        }
    }

    private void expectHex(int i)
    {
        char c = text.charAt(i);
        if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'))
        {
            return;
        }
        throw new JsonSyntaxException("invalid hex char", c, i);
    }

    private int getComponent(int element, int offset)
    {
        return getBlock(element)[getBlockIndex(element) + offset];
    }

    private int createElement(Type type, int from)
    {
        return createElement(type, from, -1, -1);
    }

    private int createElement(Type type, int from, int to, int nested)
    {
        int currentBlock = (element * 4) / blockSize;
        if (currentBlock == blocks.size())
        {
            blocks.add(new int[blockSize]);
        }
        int[] block = blocks.get(currentBlock);
        int index = getBlockIndex(element);
        block[index] = type.ordinal();
        block[index + FROM] = from;
        block[index + TO] = to;
        block[index + NESTED] = nested;
        element++;
        return to + 1;
    }

    private int closeElement(int element, int to, int nested)
    {
        int[] block = getBlock(element);
        int index = getBlockIndex(element);
        block[index + TO] = to;
        block[index + NESTED] = nested;
        return to + 1;
    }

    private int[] getBlock(int element)
    {
        return blocks.get((element * 4) / blockSize);
    }

    private int getBlockIndex(int element)
    {
        return (element * 4) % blockSize;
    }
}

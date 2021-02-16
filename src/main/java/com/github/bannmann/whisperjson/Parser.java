package com.github.bannmann.whisperjson;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import com.google.common.base.CharMatcher;

class Parser<T extends Text<T>>
{
    private static final CharMatcher HEX_DIGIT = CharMatcher.inRange('0', '9')
        .or(CharMatcher.inRange('A', 'F'))
        .or(CharMatcher.inRange('a', 'f'))
        .precomputed();

    @RequiredArgsConstructor
    private class NumberParser
    {
        private final int from;

        private boolean minus;
        private boolean leadingZero;
        private boolean dot;
        private boolean exponent;
        private int exponentPos;
        private boolean exponentIncomplete;

        private void execute()
        {
            boolean finished = false;
            while (isPositionValid() && !finished)
            {
                char c = getCurrentChar();
                switch (c)
                {
                    case '+':
                        rejectPlusOutsideExponent();
                        rejectSignInsideExponent();
                        break;
                    case '-':
                        rejectMinusInsideNumber();
                        rejectSignInsideExponent();
                        minus = true;
                        break;
                    case 'e':
                    case 'E':
                        rejectAdditionalExponents();
                        leadingZero = false;
                        exponent = true;
                        exponentPos = pos;
                        exponentIncomplete = true;
                        break;
                    case '.':
                        rejectMultipleDots();
                        verifyDigitsBeforeDot();
                        rejectDotsInExponent();
                        leadingZero = false;
                        dot = true;
                        break;
                    case '0':
                        if (pos == from)
                        {
                            leadingZero = true;
                        }
                        if (exponent)
                        {
                            exponentIncomplete = false;
                        }
                        break;
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        rejectLeadingZero();
                        if (exponent)
                        {
                            exponentIncomplete = false;
                        }
                        break;
                    default:
                        // Our number ends here. Whether the character is valid (e.g. ',' or '[') is checked elsewhere.
                        finished = true;
                        break;
                }
                if (!finished)
                {
                    pos++;
                }
            }
            rejectIsolatedMinus();
            rejectIncompleteExponent();
            createLeafElement(Type.NUMBER, from, pos - 1);
        }

        private void rejectPlusOutsideExponent()
        {
            if (!exponent)
            {
                throw new JsonSyntaxException("invalid number character", getCurrentChar(), pos);
            }
        }

        private void rejectSignInsideExponent()
        {
            if (exponent && pos > exponentPos + 1)
            {
                throw new JsonSyntaxException("invalid exponent", getCurrentChar(), pos);
            }
        }

        private void rejectMinusInsideNumber()
        {
            if (pos > from && !exponent)
            {
                throw new JsonSyntaxException("minus inside number", pos);
            }
        }

        private void rejectAdditionalExponents()
        {
            if (exponent)
            {
                throw new JsonSyntaxException("additional exponent", pos);
            }
        }

        private void rejectMultipleDots()
        {
            if (dot)
            {
                throw new JsonSyntaxException("multiple dots", pos - 1);
            }
        }

        private void rejectDotsInExponent()
        {
            if (exponent)
            {
                throw new JsonSyntaxException("invalid exponent", getCurrentChar(), pos);
            }
        }

        private void verifyDigitsBeforeDot()
        {
            if (pos == from || (minus && (pos == from + 1)))
            {
                throw new JsonSyntaxException("no digit before dot", pos);
            }
        }

        private void rejectLeadingZero()
        {
            if (leadingZero)
            {
                throw new JsonSyntaxException("leading zero", pos - 1);
            }
        }

        private void rejectIsolatedMinus()
        {
            if (minus && pos == from + 1)
            {
                throw new JsonSyntaxException("isolated minus", pos - 1);
            }
        }

        private void rejectIncompleteExponent()
        {
            if (exponentIncomplete)
            {
                if (!isPositionValid())
                {
                    throw new JsonSyntaxException("unexpected end of exponent", pos);
                }
                throw new JsonSyntaxException("invalid exponent", getCurrentChar(), pos);
            }
        }
    }

    protected final T text;
    protected final Overlay<T> overlay;

    protected int nextElementNumber;
    private int pos;

    public Parser(@NonNull Overlay<T> overlay)
    {
        this.overlay = overlay;
        this.text = overlay.getText();
    }

    public void execute()
    {
        try
        {
            parseValue();
            skipWhitespace();
            if (pos != text.length())
            {
                throw new JsonSyntaxException("malformed json", Math.min(pos, text.length()));
            }
        }
        catch (IndexOutOfBoundsException e)
        {
            throw new JsonSyntaxException("unbalanced json", e);
        }
    }

    private void parseValue()
    {
        skipWhitespace();
        char c = getCurrentChar();
        switch (c)
        {
            case '"':
                parseString();
                break;
            case '{':
                parseObject();
                break;
            case '[':
                parseArray();
                break;
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
                parseNumber();
                break;
            case 't':
                parseTrue();
                break;
            case 'f':
                parseFalse();
                break;
            case 'n':
                parseNull();
                break;
            default:
                throw new JsonSyntaxException("illegal char", c, pos);
        }
    }

    private void parseNumber()
    {
        new NumberParser(pos).execute();
    }

    private void parseString()
    {
        Type type = Type.STRING;
        int from = pos;
        pos++;
        while (true)
        {
            char c = getCurrentChar();
            if (c == '"')
            {
                createLeafElement(type, from, pos);
                return;
            }
            else if (c < 32)
            {
                throw new JsonSyntaxException(String.format("illegal control char %d", (int) c), pos);
            }
            else if (c == '\\')
            {
                type = Type.STRING_ESCAPED;
                pos++;
                char escapeChar = getCurrentChar();
                switch (escapeChar)
                {
                    case '"':
                    case '/':
                    case '\\':
                    case 'b':
                    case 'f':
                    case 'n':
                    case 'r':
                    case 't':
                        break;
                    case 'u':
                        pos++;
                        expectFourHexDigits();
                        break;
                    default:
                        throw new JsonSyntaxException("illegal escape char", escapeChar, pos);
                }
            }
            pos++;
        }
    }

    private void parseArray()
    {
        int count = 0;
        int e = nextElementNumber;
        openStructureElement(Type.ARRAY);
        while (true)
        {
            skipWhitespace();
            if (getCurrentChar() == ']')
            {
                closeStructureElement(e, nextElementNumber - e - 1);
                return;
            }
            if (count > 0)
            {
                verifyCurrentChar(',');
                pos++;
                skipWhitespace();
            }
            parseValue();
            count++;
        }
    }

    private void parseObject()
    {
        int count = 0;
        int e = nextElementNumber;
        openStructureElement(Type.OBJECT);
        while (true)
        {
            skipWhitespace();
            if (getCurrentChar() == '}')
            {
                closeStructureElement(e, nextElementNumber - e - 1);
                return;
            }
            if (count > 0)
            {
                verifyCurrentChar(',');
                pos++;
                skipWhitespace();
            }
            verifyCurrentChar('"');
            parseString();
            skipWhitespace();

            verifyCurrentChar(':');
            pos++;

            skipWhitespace();
            parseValue();
            count++;
        }
    }

    private void parseNull()
    {
        int from = pos;
        moveToNextChar('u');
        moveToNextChar('l');
        moveToNextChar('l');
        createLeafElement(Type.NULL, from, pos);
    }

    private void moveToNextChar(char u)
    {
        pos++;
        verifyCurrentChar(u);
    }

    private void parseTrue()
    {
        int from = pos;
        moveToNextChar('r');
        moveToNextChar('u');
        moveToNextChar('e');
        createLeafElement(Type.TRUE, from, pos);
    }

    private void parseFalse()
    {
        int from = pos;
        moveToNextChar('a');
        moveToNextChar('l');
        moveToNextChar('s');
        moveToNextChar('e');
        createLeafElement(Type.FALSE, from, pos);
    }

    private void skipWhitespace()
    {
        while (isPositionValid())
        {
            char c = getCurrentChar();
            if (c != ' ' && c != '\t' && c != '\n' && c != '\r')
            {
                break;
            }
            pos++;
        }
    }

    private boolean isPositionValid()
    {
        return pos < text.length();
    }

    private void verifyCurrentChar(char expected)
    {
        try
        {
            char found = getCurrentChar();
            if (found != expected)
            {
                throw new JsonSyntaxException(String.format("expected char '%s', found '%s'", expected, found), pos);
            }
        }
        catch (IndexOutOfBoundsException e)
        {
            throw new JsonSyntaxException(String.format("expected char '%s', found EOF", expected), pos, e);
        }
    }

    private void expectFourHexDigits()
    {
        for (int end = pos + 4; pos < end; pos++)
        {
            expectHexDigit();
        }
    }

    private void expectHexDigit()
    {
        char c = getCurrentChar();
        if (!HEX_DIGIT.matches(c))
        {
            throw new JsonSyntaxException("invalid hex char", c, pos);
        }
    }

    private char getCurrentChar()
    {
        return text.charAt(pos);
    }

    private void createLeafElement(Type type, int from, int to)
    {
        overlay.createLeafElement(nextElementNumber, type, from, to);
        nextElementNumber++;
        pos = to + 1;
    }

    private void openStructureElement(Type type)
    {
        overlay.openStructureElement(nextElementNumber, type, pos);
        nextElementNumber++;
        pos++;
    }

    private void closeStructureElement(int element, int childCount)
    {
        overlay.closeStructureElement(element, pos, childCount);
        pos++;
    }
}

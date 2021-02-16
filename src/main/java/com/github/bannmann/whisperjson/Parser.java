package com.github.bannmann.whisperjson;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

class Parser<T extends Text<T>>
{
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
            createElement(Type.NUMBER, from, pos - 1, 0);
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
        boolean escaped = false;
        int from = pos++;
        while (true)
        {
            char c = getCurrentChar();
            if (c == '"')
            {
                Type type = escaped ? Type.STRING_ESCAPED : Type.STRING;
                createElement(type, from, pos, 0);
                return;
            }
            else if (c < 32)
            {
                throw new JsonSyntaxException(String.format("illegal control char %d", (int) c), pos);
            }
            else if (c == '\\')
            {
                escaped = true;
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
                        for (int end = pos + 4; pos < end; pos++)
                        {
                            expectHex();
                        }
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
        openElement(Type.ARRAY);
        while (true)
        {
            skipWhitespace();
            if (getCurrentChar() == ']')
            {
                closeElement(e, nextElementNumber - e - 1);
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
        openElement(Type.OBJECT);
        while (true)
        {
            skipWhitespace();
            if (getCurrentChar() == '}')
            {
                closeElement(e, nextElementNumber - e - 1);
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
        createElement(Type.NULL, from, pos, 0);
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
        createElement(Type.TRUE, from, pos, 0);
    }

    private void parseFalse()
    {
        int from = pos;
        moveToNextChar('a');
        moveToNextChar('l');
        moveToNextChar('s');
        moveToNextChar('e');
        createElement(Type.FALSE, from, pos, 0);
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
            throw new JsonSyntaxException(String.format("expected char '%s', found EOF", expected), pos);
        }
    }

    private void expectHex()
    {
        char c = getCurrentChar();
        if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'))
        {
            return;
        }
        throw new JsonSyntaxException("invalid hex char", c, pos);
    }

    private char getCurrentChar()
    {
        return text.charAt(pos);
    }

    private void openElement(Type type)
    {
        overlay.createElement(nextElementNumber++, type, pos, -1, -1);
        pos++;
    }

    private void createElement(Type type, int from, int to, int nested)
    {
        overlay.createElement(nextElementNumber++, type, from, to, nested);
        pos = to + 1;
    }

    private void closeElement(int element, int nested)
    {
        overlay.closeElement(element, pos, nested);
        pos++;
    }
}

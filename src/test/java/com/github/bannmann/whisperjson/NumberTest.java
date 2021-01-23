package com.github.bannmann.whisperjson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

public class NumberTest
{
    @Test
    public void parseZero()
    {
        ExposedJson json = WhisperJson.parse("0");
        assertTrue(json.isNumber());
        assertEquals(0, json.asLong());
    }

    @Test
    public void parseZeroWithExponent()
    {
        ExposedJson json = WhisperJson.parse("0e-23");
        assertTrue(json.isNumber());
        assertEquals(0, json.asDouble(), 0);
    }

    @Test(expected = ParseException.class)
    public void parseMinus()
    {
        WhisperJson.parse("-");
    }

    @Test
    public void parseNegativeZero()
    {
        ExposedJson json = WhisperJson.parse("-0");
        assertTrue(json.isNumber());
        assertEquals(0, json.asLong());
    }

    @Test
    public void parseNegativeZeroWithExponent()
    {
        ExposedJson json = WhisperJson.parse("-0e-2");
        assertTrue(json.isNumber());
        assertEquals(0, json.asDouble(), 0);
    }

    @Test
    public void parseSingleDigit()
    {
        ExposedJson json = WhisperJson.parse("3");
        assertTrue(json.isNumber());
        assertEquals(3, json.asLong());
    }

    @Test
    public void parseSingleDigitWithExponent()
    {
        ExposedJson json = WhisperJson.parse("3e+7");
        assertTrue(json.isNumber());
        assertEquals(3e+7, json.asDouble(), 0);
    }

    @Test(expected = ParseException.class)
    public void parseNumberWithLeadingZero()
    {
        WhisperJson.parse("023");
    }

    @Test
    public void parseInteger()
    {
        ExposedJson json = WhisperJson.parse("123");
        assertTrue(json.isNumber());
        assertEquals(123, json.asInt());
    }

    @Test
    public void parseLong()
    {
        ExposedJson json = WhisperJson.parse("100000000000000023");
        assertTrue(json.isNumber());
        assertEquals(100000000000000023L, json.asLong());
    }

    @Test
    public void parseBigDecimal()
    {
        ExposedJson json = WhisperJson.parse(
            "3.141592653589793238462643383279502884197169399375105820974944592307816406286");
        assertTrue(json.isNumber());
        assertEquals(new BigDecimal("3.141592653589793238462643383279502884197169399375105820974944592307816406286"),
            json.asBigDecimal());
    }

    @Test
    public void parseBigInteger()
    {
        ExposedJson json = WhisperJson.parse(
            "141592653589793238462643383279502884197169399375105820974944592307816406286");
        assertTrue(json.isNumber());
        assertEquals(new BigInteger("141592653589793238462643383279502884197169399375105820974944592307816406286"),
            json.asBigInteger());
    }

    @Test
    public void parseNegativeNumber()
    {
        ExposedJson json = WhisperJson.parse("-23");
        assertTrue(json.isNumber());
        assertEquals(-23, json.asLong());
    }

    @Test
    public void parseNegativeNumberWithExponent()
    {
        ExposedJson json = WhisperJson.parse("-2e-2");
        assertTrue(json.isNumber());
        assertEquals(-2e-2, json.asDouble(), 0);
    }

    @Test
    public void parseNegativeLongNumber()
    {
        ExposedJson json = WhisperJson.parse("-234567898765432");
        assertTrue(json.isNumber());
        assertEquals(-234567898765432L, json.asLong());
    }

    @Test
    public void parseNumberWithExponent()
    {
        ExposedJson json = WhisperJson.parse("33e12");
        assertTrue(json.isNumber());
        assertEquals(33e12, json.asDouble(), 0);
    }

    @Test
    public void parseNumberWithExponentUppercase()
    {
        ExposedJson json = WhisperJson.parse("33E12");
        assertTrue(json.isNumber());
        assertEquals(33e12, json.asDouble(), 0);
    }

    @Test
    public void parseNumberWithExponentPlus()
    {
        ExposedJson json = WhisperJson.parse("33E+12");
        assertTrue(json.isNumber());
        assertEquals(33e12, json.asDouble(), 0);
    }

    @Test
    public void parseNumberWithExponentMinus()
    {
        ExposedJson json = WhisperJson.parse("33E-12");
        assertTrue(json.isNumber());
        assertEquals(33e-12, json.asDouble(), 0);
    }

    @Test(expected = ParseException.class)
    public void parseNumberWithEmptyExponent()
    {
        WhisperJson.parse("33E");
    }

    @Test(expected = ParseException.class)
    public void parseNumberWithEmptyExponentPlus()
    {
        WhisperJson.parse("33E+");
    }

    @Test(expected = ParseException.class)
    public void parseNumberWithBrokenExponent()
    {
        WhisperJson.parse("33E++2");
    }

    @Test(expected = ParseException.class)
    public void parseNumberWithMultipleExponents()
    {
        WhisperJson.parse("33E2E4");
    }

    @Test
    public void parseFloat()
    {
        ExposedJson json = WhisperJson.parse("3.141");
        assertTrue(json.isNumber());
        assertEquals(3.141, json.asFloat(), 0.001);
    }

    @Test
    public void parseNegativeFloat()
    {
        ExposedJson json = WhisperJson.parse("-3.141");
        assertTrue(json.isNumber());
        assertEquals(-3.141, json.asFloat(), 0.001);
    }

    @Test
    public void parseFloatWithExponent()
    {
        ExposedJson json = WhisperJson.parse("-3.141e+4");
        assertTrue(json.isNumber());
        assertEquals(-3.141e4, json.asFloat(), 0.001);
    }

    @Test
    public void parseFloatWithLeadingZero()
    {
        ExposedJson json = WhisperJson.parse("0.33333333");
        assertTrue(json.isNumber());
        assertEquals(0.33333333, json.asFloat(), 0.001);
    }

    @Test
    public void parseFloatWithLeadingZeroAndExponent()
    {
        ExposedJson json = WhisperJson.parse("0.333e4");
        assertTrue(json.isNumber());
        assertEquals(0.333e4, json.asFloat(), 0.001);
    }

    @Test(expected = ParseException.class)
    public void parseFloatWithComma()
    {
        WhisperJson.parse("3,141");
    }

    @Test(expected = ParseException.class)
    public void parseFloatStartingWithDot()
    {
        WhisperJson.parse(".141");
    }

    @Test(expected = ParseException.class)
    public void parseNegativeFloatStartingWithDot()
    {
        WhisperJson.parse("-.141");
    }

    @Test(expected = ParseException.class)
    public void parseFloatWithDoubleDot()
    {
        WhisperJson.parse("111..333");
    }
}

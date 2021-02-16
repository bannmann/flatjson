package com.github.bannmann.whisperjson;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.within;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestNumberParsing
{
    @Test(dataProvider = "intVariants")
    public void parseInt(String label, String input, int expected)
    {
        ExposedJson json = WhisperJson.parse(input);

        assertThat(json).returns(true, Json::isNumber);
        assertThat(json.asInt()).isEqualTo(expected);
    }

    @DataProvider
    public static Object[][] intVariants()
    {
        return new Object[][]{
            new Object[]{ "zero", "0", 0 },
            new Object[]{ "negative zero", "-0", 0 },
            new Object[]{ "single digit", "3", 3 },
            new Object[]{ "positive number", "123", 123 },
            new Object[]{ "negative number", "-23", -23 }
        };
    }

    @Test(dataProvider = "longVariants")
    public void parseLong(String label, String input, long expected)
    {
        ExposedJson json = WhisperJson.parse(input);

        assertThat(json).returns(true, Json::isNumber);
        assertThat(json.asLong()).isEqualTo(expected);
    }

    @DataProvider
    public static Object[][] longVariants()
    {
        return new Object[][]{
            new Object[]{ "positive long", "100000000000000023", 100000000000000023L },
            new Object[]{ "negative long", "-234567898765432", -234567898765432L }
        };
    }

    @Test(dataProvider = "doubleVariants")
    public void parseDouble(String label, String input, double expected)
    {
        ExposedJson json = WhisperJson.parse(input);

        assertThat(json).returns(true, Json::isNumber);
        assertThat(json.asDouble()).isCloseTo(expected, within(0.0));
    }

    @DataProvider
    public static Object[][] doubleVariants()
    {
        return new Object[][]{
            new Object[]{ "zero with exponent", "0e-23", 0 },
            new Object[]{ "negative zero with exponent", "-0e-2", 0 },
            new Object[]{ "single digit with exponent", "3e+7", 3e+7 },
            new Object[]{ "negative number with exponent", "-2e-2", -2e-2 },
            new Object[]{ "number with exponent", "33e12", 33e12 },
            new Object[]{ "number with large exponent", "33e111", 33e111 },
            new Object[]{ "number with leading zero exponent", "33e0013", 33e13 },
            new Object[]{ "number with exponent uppercase", "33E12", 33e12 },
            new Object[]{ "number with exponent plus", "33E+12", 33e12 },
            new Object[]{ "number with exponent minus", "33E-12", 33e-12 }
        };
    }

    @Test(dataProvider = "floatVariants")
    public void parseFloat(String label, String input, double expected)
    {
        ExposedJson json = WhisperJson.parse("3.141");

        assertThat(json).returns(true, Json::isNumber);
        assertThat((double) json.asFloat()).isCloseTo(3.141, within(0.001));
    }

    @DataProvider
    public static Object[][] floatVariants()
    {
        return new Object[][]{
            new Object[]{ "float", "3.141", 3.141 },
            new Object[]{ "negative float", "-3.141", -3.141 },
            new Object[]{ "float with exponent", "-3.141e+4", -3.141e4 },
            new Object[]{ "float with leading zero", "0.33333333", 0.33333333 },
            new Object[]{ "float with leading zero and exponent", "0.333e4", 0.333e4 }
        };
    }

    @Test
    public void parseBigDecimal()
    {
        ExposedJson json = WhisperJson.parse(
            "3.141592653589793238462643383279502884197169399375105820974944592307816406286");

        assertThat(json).returns(true, Json::isNumber);
        assertThat(json.asBigDecimal()).isEqualTo(new BigDecimal(
            "3.141592653589793238462643383279502884197169399375105820974944592307816406286"));
    }

    @Test
    public void parseBigInteger()
    {
        ExposedJson json = WhisperJson.parse(
            "141592653589793238462643383279502884197169399375105820974944592307816406286");

        assertThat(json).returns(true, Json::isNumber);
        assertThat(json.asBigInteger()).isEqualTo(new BigInteger(
            "141592653589793238462643383279502884197169399375105820974944592307816406286"));
    }

    @Test(dataProvider = "malformedNumbers")
    public void parseMalformedNumbers(String label, String input, String expectedMessage)
    {
        assertThatExceptionOfType(JsonSyntaxException.class).isThrownBy(() -> WhisperJson.parse(input))
            .withMessage(expectedMessage);
    }

    @DataProvider
    public static Object[][] malformedNumbers()
    {
        return new Object[][]{
            new Object[]{ "minus", "-", "isolated minus at index 0" },
            new Object[]{ "additional minus", "--1", "minus inside number at index 1" },
            new Object[]{ "minus inside number", "12-34", "minus inside number at index 2" },
            new Object[]{ "plus sign", "+1", "illegal char '+' at index 0" },
            new Object[]{ "leading zero", "023", "leading zero at index 0" },
            new Object[]{ "illegal number character", "567B9", "malformed json at index 3" },
            new Object[]{ "plus inside number", "22+1", "invalid number character '+' at index 2" },
            new Object[]{ "empty exponent at EOF", "33E", "unexpected end of exponent at index 3" },
            new Object[]{ "empty exponent in array", "[33E]", "invalid exponent ']' at index 4" },
            new Object[]{ "plus-only exponent at EOF", "33E+", "unexpected end of exponent at index 4" },
            new Object[]{ "plus-only exponent in array", "[33E+]", "invalid exponent ']' at index 5" },
            new Object[]{ "additional plus in exponent", "33E++2", "invalid exponent '+' at index 4" },
            new Object[]{ "plus inside exponent", "7e1+2", "invalid exponent '+' at index 3" },
            new Object[]{ "minus inside exponent", "6481e1-2", "invalid exponent '-' at index 6" },
            new Object[]{ "exponent with dot", "33E+21.5", "invalid exponent '.' at index 6" },
            new Object[]{ "multiple exponents", "33E2E4", "additional exponent at index 4" },
            new Object[]{ "float with comma", "3,141", "malformed json at index 1" },
            new Object[]{ "float starting with dot", ".141", "illegal char '.' at index 0" },
            new Object[]{ "negative float starting with dot", "-.141", "no digit before dot at index 1" },
            new Object[]{ "float with double dot", "111..333", "multiple dots at index 3" }
        };
    }
}

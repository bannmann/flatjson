package com.github.bannmann.whisperjson;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.List;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class JsonTest
{
    @Test
    public void parseJsonNull()
    {
        ExposedJson json = WhisperJson.parse("null");

        assertThat(json).returns(true, Json::isNull);
    }

    @Test
    public void parseNullWithWhitespace()
    {
        ExposedJson json = WhisperJson.parse("   \r\n null \t");

        assertThat(json).returns(true, Json::isNull);
    }

    @Test
    public void parseTrue()
    {
        ExposedJson json = WhisperJson.parse("true");

        assertThat(json).returns(true, Json::isBoolean)
            .returns(true, Json::asBoolean);
    }

    @Test
    public void parseFalse()
    {
        ExposedJson json = WhisperJson.parse("false");

        assertThat(json).returns(true, Json::isBoolean)
            .returns(false, Json::asBoolean);
    }

    @Test
    public void parseEmptyArray()
    {
        ExposedJson json = WhisperJson.parse("[ ]");

        assertThat(json).returns(true, Json::isArray);
        assertThat(json.asArray()).isEmpty();
    }

    @Test
    public void parseBooleanArray()
    {
        ExposedJson json = WhisperJson.parse("[ true,false ]");

        assertThat(json).returns(true, Json::isArray);
        assertThat(json.asArray()).map(Json::asBoolean)
            .containsExactly(true, false);
    }

    @Test
    public void parseNumberArray()
    {
        ExposedJson json = WhisperJson.parse("[23,42e8,3.141]");

        assertThat(json).returns(true, Json::isArray);
        assertThat(json.asArray()).satisfiesExactly(element -> assertThat(element.asLong()).isEqualTo(23),
            element -> assertThat(element.asDouble()).isCloseTo(42e8, within(0.0)),
            element -> assertThat(element.asDouble()).isCloseTo(3.141, within(0.0)));
    }

    @Test
    public void parseMixedArray()
    {
        ExposedJson json = WhisperJson.parse("[42, true, \"hello\"]");

        assertThat(json).returns(true, Json::isArray);
        assertThat(json.asArray()).satisfiesExactly(element -> assertThat(element.asLong()).isEqualTo(42),
            element -> assertThat(element).returns(true, Json::asBoolean),
            element -> assertThat(element.asString()).isEqualTo("hello"));
    }

    @Test
    public void parseNestedArray()
    {
        ExposedJson json = WhisperJson.parse("[[[],[]]]");

        assertThat(json).returns(true, Json::isArray);

        List<ExposedJson> array = json.asArray();
        assertThat(array).hasSize(1);

        ExposedJson nested = array.get(0);
        assertThat(nested).returns(true, Json::isArray);

        List<ExposedJson> nestedArray = nested.asArray();
        assertThat(nestedArray).hasSize(2)
            .allSatisfy(element -> assertThat(element).returns(true, Json::isArray));
    }

    @Test
    public void parseEmptyObject()
    {
        ExposedJson json = WhisperJson.parse("{}");

        assertThat(json).returns(true, Json::isObject);
        assertThat(json.asObject()).isEmpty();
    }

    @Test
    public void parseObject()
    {
        ExposedJson json = WhisperJson.parse("{\"foo\": true ,\n   \"bar\": false   }");

        assertThat(json).returns(true, Json::isObject);

        Map<String, ExposedJson> properties = json.asObject();
        assertThat(properties).hasSize(2)
            .containsOnlyKeys("foo", "bar");
        assertThat(properties.get("foo")).returns(true, Json::asBoolean);
        assertThat(properties.get("bar")).returns(false, Json::asBoolean);
    }

    @Test
    public void parseObjectWithEscapedKey()
    {
        ExposedJson json = WhisperJson.parse("{\"\\noo\\b\": true }");

        assertThat(json).returns(true, Json::isObject);
        assertThat(json.asObject()).containsOnlyKeys("\noo\b");
    }

    @Test
    public void parseNestedObject()
    {
        ExposedJson json = WhisperJson.parse("{\"nested\": {\"foo\": 23 }, \"bar\": false , \"baz\": -1 }");

        assertThat(json).returns(true, Json::isObject);
        Map<String, ExposedJson> object = json.asObject();
        assertThat(object).hasSize(3);

        ExposedJson nested = object.get("nested");
        assertThat(nested).returns(true, Json::isObject);

        Map<String, ExposedJson> nestedObject = nested.asObject();
        assertThat(nestedObject.get("foo")
            .asLong()).isEqualTo(23);
    }

    @Test
    public void parseArrayOfObjects()
    {
        ExposedJson json = WhisperJson.parse("[{\"foo\": 23, \"bar\": 44}, {\"foo\": 11, \"bar\": 64}]");

        assertThat(json).returns(true, Json::isArray);

        List<ExposedJson> array = json.asArray();
        assertThat(array).hasSize(2)
            .allSatisfy(value -> {
                assertThat(value).returns(true, Json::isObject);
                assertThat(value.asObject()).containsOnlyKeys("foo", "bar");
            });
    }

    @Test(dataProvider = "malformedJson", expectedExceptions = ParseException.class)
    public void parseFailure(String label, String input)
    {
        WhisperJson.parse(input);
    }

    @DataProvider
    public static Object[][] malformedJson()
    {
        return new Object[][]{
            new Object[]{ "null", null },
            new Object[]{ "empty", "" },
            new Object[]{ "whitespace", "  \r\n  \t " },
            new Object[]{ "broken null", "nul" },
            new Object[]{ "array with leading comma", "[ , true]" },
            new Object[]{ "array with trailing comma", "[ true,]" },
            new Object[]{ "open array", "[ null," }
        };
    }
}

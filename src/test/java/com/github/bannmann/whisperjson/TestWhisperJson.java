package com.github.bannmann.whisperjson;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestWhisperJson
{
    private WhisperJson whisperJson;

    @BeforeMethod
    public void setUp()
    {
        whisperJson = new WhisperJson();
    }

    @Test
    public void parseJsonNull()
    {
        ExposedJson json = whisperJson.parse("null");

        assertThat(json).returns(true, Json::isNull);
    }

    @Test
    public void parseNullWithWhitespace()
    {
        ExposedJson json = whisperJson.parse("   \r\n null \t");

        assertThat(json).returns(true, Json::isNull);
    }

    @Test
    public void parseTrue()
    {
        ExposedJson json = whisperJson.parse("true");

        assertThat(json).returns(true, Json::isBoolean)
            .returns(true, Json::asBoolean);
    }

    @Test
    public void parseFalse()
    {
        ExposedJson json = whisperJson.parse("false");

        assertThat(json).returns(true, Json::isBoolean)
            .returns(false, Json::asBoolean);
    }

    @Test
    public void parseEmptyArray()
    {
        ExposedJson json = whisperJson.parse("[ ]");

        assertThat(json).returns(true, Json::isArray);
        assertThat(json.asArray()).isEmpty();
    }

    @Test
    public void parseBooleanArray()
    {
        ExposedJson json = whisperJson.parse("[ true,false ]");

        assertThat(json).returns(true, Json::isArray);
        assertThat(json.asArray()).map(Json::asBoolean)
            .containsExactly(true, false);
    }

    @Test
    public void parseNumberArray()
    {
        ExposedJson json = whisperJson.parse("[23,42e8,3.141]");

        assertThat(json).returns(true, Json::isArray);
        assertThat(json.asArray()).satisfiesExactly(element -> assertThat(element.asLong()).isEqualTo(23),
            element -> assertThat(element.asDouble()).isCloseTo(42e8, within(0.0)),
            element -> assertThat(element.asDouble()).isCloseTo(3.141, within(0.0)));
    }

    @Test
    public void parseString()
    {
        ExposedJson json = whisperJson.parse("\"hello\"");

        assertThat(json).returns(true, Json::isString);
        assertThat(json.asString()).isEqualTo("hello");
    }

    @Test(dataProvider = "parsingErrors")
    public void parseError(String label, String input, String message)
    {
        assertThatThrownBy(() -> whisperJson.parse(input)).isInstanceOf(JsonSyntaxException.class)
            .hasMessage(message);
    }

    @DataProvider
    public static Object[][] parsingErrors()
    {
        return new Object[][]{
            new Object[]{ "illegal escape", "\"example: \\z is invalid\"", "illegal escape char 'z' at index 11" },
            new Object[]{ "incomplete null: EOF", "nul", "expected char 'l', found EOF at index 3" },
            new Object[]{ "incomplete null: comma", "[nul, 42]", "expected char 'l', found ',' at index 4" },
            new Object[]{ "typo null", "noll", "expected char 'u', found 'o' at index 1" },
            new Object[]{ "array with leading comma", "[ , true]", "illegal char ',' at index 2" },
            new Object[]{ "array with trailing comma", "[ true,]", "illegal char ']' at index 7" },
            new Object[]{ "open array", "[ null,", "unbalanced json" }
        };
    }

    @Test
    public void parseMixedArray()
    {
        ExposedJson json = whisperJson.parse("[42, true, \"hello\"]");

        assertThat(json).returns(true, Json::isArray);
        assertThat(json.asArray()).satisfiesExactly(element -> assertThat(element.asLong()).isEqualTo(42),
            element -> assertThat(element).returns(true, Json::asBoolean),
            element -> assertThat(element.asString()).isEqualTo("hello"));
    }

    @Test
    public void parseNestedArray()
    {
        ExposedJson json = whisperJson.parse("[[[],[]]]");

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
        ExposedJson json = whisperJson.parse("{}");

        assertThat(json).returns(true, Json::isObject);
        assertThat(json.asObject()).isEmpty();
    }

    @Test
    public void parseTypeMismatch()
    {
        ExposedJson json = whisperJson.parse("{\"answer\": 42}");

        ExposedJson answer = json.getObjectProperty("answer")
            .orElseThrow(AssertionError::new);

        assertThatThrownBy(answer::asString).isInstanceOf(TypeMismatchException.class)
            .hasMessage("Type mismatch: expected string, got number at index 11");
    }

    @Test
    public void parseObject()
    {
        ExposedJson json = whisperJson.parse("{\"foo\": 3741 ,\n   \"bar\": false   }");

        assertThat(json).returns(true, Json::isObject);

        Map<String, ExposedJson> properties = json.asObject();
        assertThat(properties).hasSize(2)
            .containsOnlyKeys("foo", "bar");
        assertThat(properties.get("foo")).returns(3741, Json::asInt);
        assertThat(properties.get("bar")).returns(false, Json::asBoolean);
    }

    @Test
    public void parseObjectGetProperty()
    {
        ExposedJson json = whisperJson.parse("{\"many\":8413}");

        ExposedJson property = json.asObject()
            .get("many");
        
        assertThat(json.getObjectProperty("many")).isPresent()
            .get()
            .isEqualTo(property);
    }

    @Test
    public void parseObjectNullAndUnsetProperties()
    {
        ExposedJson json = whisperJson.parse("{\"link\":null}");

        assertThat(json.getObjectProperty("link")).isEmpty();
        assertThat(json.getObjectProperty("missing")).isEmpty();

        Map<String, ExposedJson> properties = json.asObject();
        assertThat(properties.get("link")).returns(true, Json::isNull);
        assertThat(properties.get("missing")).isNull();
        assertThat(properties.containsKey("missing")).isFalse();
    }

    @Test
    public void parseObjectWithEscapedKey()
    {
        ExposedJson json = whisperJson.parse("{\"\\noo\\b\": true }");

        assertThat(json).returns(true, Json::isObject);
        assertThat(json.asObject()).containsOnlyKeys("\noo\b");
    }

    @Test
    public void parseNestedObject()
    {
        ExposedJson json = whisperJson.parse("{\"nested\": {\"foo\": 23 }, \"bar\": false , \"baz\": -1 }");

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
        ExposedJson json = whisperJson.parse("[{\"foo\": 23, \"bar\": 44}, {\"foo\": 11, \"bar\": 64}]");

        assertThat(json).returns(true, Json::isArray);

        List<ExposedJson> array = json.asArray();
        assertThat(array).hasSize(2)
            .allSatisfy(value -> {
                assertThat(value).returns(true, Json::isObject);
                assertThat(value.asObject()).containsOnlyKeys("foo", "bar");
            });
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void parseNull()
    {
        whisperJson.parse((String) null);
    }

    @Test(dataProvider = "malformedJson", expectedExceptions = JsonSyntaxException.class)
    public void parseFailure(String label, String input)
    {
        whisperJson.parse(input);
    }

    @DataProvider
    public static Object[][] malformedJson()
    {
        return new Object[][]{
            new Object[]{ "empty", "" },
            new Object[]{ "whitespace", "  \r\n  \t " },
            new Object[]{ "broken null", "nul" },
            new Object[]{ "array with leading comma", "[ , true]" },
            new Object[]{ "array with trailing comma", "[ true,]" },
            new Object[]{ "open array", "[ null," }
        };
    }
}

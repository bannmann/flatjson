package com.github.bannmann.whisperjson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class JsonTest
{
    @Test(expected = ParseException.class)
    public void parseNull()
    {
        WhisperJson.parse((String) null);
    }

    @Test(expected = ParseException.class)
    public void parseEmpty()
    {
        WhisperJson.parse("");
    }

    @Test(expected = ParseException.class)
    public void parseWhitespace()
    {
        WhisperJson.parse("  \r\n  \t ");
    }

    @Test
    public void parseJsonNull()
    {
        assertTrue(WhisperJson.parse("null")
            .isNull());
    }

    @Test
    public void parseNullWithWhitespace()
    {
        assertTrue(WhisperJson.parse("   \r\n null \t")
            .isNull());
    }

    @Test(expected = ParseException.class)
    public void parseBrokenNull()
    {
        WhisperJson.parse("nul");
    }

    @Test
    public void parseTrue()
    {
        ExposedJson json = WhisperJson.parse("true");
        assertTrue(json.isBoolean());
        assertTrue(json.asBoolean());
    }

    @Test
    public void parseFalse()
    {
        ExposedJson json = WhisperJson.parse("false");
        assertTrue(json.isBoolean());
        assertFalse(json.asBoolean());
    }

    @Test
    public void parseEmptyArray()
    {
        ExposedJson json = WhisperJson.parse("[ ]");
        assertTrue(json.isArray());
        assertEquals(0,
            json.asArray()
                .size());
    }

    @Test(expected = ParseException.class)
    public void parseArrayWithLeadingComma()
    {
        WhisperJson.parse("[ , true]");
    }

    @Test(expected = ParseException.class)
    public void parseArrayWithTrailingComma()
    {
        WhisperJson.parse("[ true,]");
    }

    @Test(expected = ParseException.class)
    public void parseOpenArray()
    {
        WhisperJson.parse("[ null,");
    }

    @Test
    public void parseBooleanArray()
    {
        ExposedJson json = WhisperJson.parse("[ true,false ]");
        assertTrue(json.isArray());
        List<ExposedJson> array = json.asArray();
        assertEquals(2, array.size());
        assertTrue(array.get(0)
            .asBoolean());
        assertFalse(array.get(1)
            .asBoolean());
    }

    @Test
    public void parseNumberArray()
    {
        ExposedJson json = WhisperJson.parse("[23,42e8,3.141]");
        assertTrue(json.isArray());
        List<ExposedJson> array = json.asArray();
        assertEquals(3, array.size());
        assertEquals(23,
            array.get(0)
                .asLong());
        assertEquals(42e8,
            array.get(1)
                .asDouble(),
            0);
        assertEquals(3.141,
            array.get(2)
                .asDouble(),
            0);
    }

    @Test
    public void parseMixedArray()
    {
        ExposedJson json = WhisperJson.parse("[42, true, \"hello\"]");
        assertTrue(json.isArray());
        List<ExposedJson> array = json.asArray();
        assertEquals(3, array.size());
        assertEquals(42,
            array.get(0)
                .asLong());
        assertTrue(array.get(1)
            .asBoolean());
        assertEquals("hello",
            array.get(2)
                .asString());
    }

    @Test
    public void parseNestedArray()
    {
        ExposedJson json = WhisperJson.parse("[[[],[]]]");
        assertTrue(json.isArray());
        List<ExposedJson> array = json.asArray();
        assertEquals(1, array.size());
        ExposedJson nested = array.get(0);
        assertTrue(nested.isArray());
        List<ExposedJson> nestedArray = nested.asArray();
        assertEquals(2, nestedArray.size());
        assertTrue(nestedArray.get(0)
            .isArray());
        assertTrue(nestedArray.get(1)
            .isArray());
    }

    @Test
    public void parseEmptyObject()
    {
        ExposedJson json = WhisperJson.parse("{}");
        assertTrue(json.isObject());
        Map<String, ExposedJson> object = json.asObject();
        assertEquals(0, object.size());
    }

    @Test
    public void parseObject()
    {
        ExposedJson json = WhisperJson.parse("{\"foo\": true ,\n   \"bar\": false   }");
        assertTrue(json.isObject());
        Map<String, ExposedJson> object = json.asObject();
        assertEquals(2, object.size());
        assertTrue(object.containsKey("foo"));
        assertTrue(object.get("foo")
            .asBoolean());
        assertTrue(object.containsKey("bar"));
        assertFalse(object.get("bar")
            .asBoolean());
    }

    @Test
    public void parseObjectWithEscapedKey()
    {
        ExposedJson json = WhisperJson.parse("{\"\\noo\\b\": true }");
        assertTrue(json.isObject());
        Map<String, ExposedJson> object = json.asObject();
        assertEquals(1, object.size());
        assertTrue(object.containsKey("\noo\b"));
    }

    @Test
    public void parseNestedObject()
    {
        ExposedJson json = WhisperJson.parse("{\"nested\": {\"foo\": 23 }, \"bar\": false , \"baz\": -1 }");
        assertTrue(json.isObject());
        Map<String, ExposedJson> object = json.asObject();
        assertEquals(3, object.size());
        ExposedJson nested = object.get("nested");
        assertTrue(nested.isObject());
        Map<String, ExposedJson> nestedObject = nested.asObject();
        assertEquals(23,
            nestedObject.get("foo")
                .asLong());
    }

    @Test
    public void parseArrayOfObjects()
    {
        ExposedJson json = WhisperJson.parse("[{\"foo\": 23, \"bar\": 44}, {\"foo\": 11, \"bar\": 64}]");
        assertTrue(json.isArray());
        List<ExposedJson> array = json.asArray();
        assertEquals(2, array.size());
        for (ExposedJson value : array)
        {
            assertTrue(value.isObject());
            Set<String> keys = value.asObject()
                .keySet();
            assertTrue(keys.contains("foo"));
            assertTrue(keys.contains("bar"));
        }
    }
}

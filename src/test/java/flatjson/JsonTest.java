package flatjson;

import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class JsonTest {

    @Test public void parseNull() {
        assertTrue(Json.parse("null").isNull());
    }

    @Test public void parseNullWithWhitespace() {
        assertTrue(Json.parse("   \r\n null \t").isNull());
    }

    @Test public void parseBrokenNull() {
        try {
            Json.parse("nul");
            fail("should raise ParseException");
        } catch (ParseException expected) {}
    }

    @Test public void parseTrue() {
        JsonValue value = Json.parse("true");
        assertTrue(value.isBoolean());
        assertTrue(value.asBoolean());
    }

    @Test public void parseFalse() {
        JsonValue value = Json.parse("false");
        assertTrue(value.isBoolean());
        assertFalse(value.asBoolean());
    }

    @Test public void parseEmptyArray() {
        JsonValue value = Json.parse("[ ]");
        assertTrue(value.isArray());
        assertEquals(0, value.asArray().size());
    }

    @Test public void parseBrokenArray() {
        try {
            Json.parse("[ ,]");
            fail("should raise ParseException");
        } catch (ParseException expected) {}
    }

    @Test public void parseOpenArray() {
        try {
            Json.parse("[ null,");
            fail("should raise ParseException");
        } catch (ParseException expected) {}
    }

    @Test public void parseNestedArray() {
        JsonValue value = Json.parse("[ [42, 23]]");
        assertTrue(value.isArray());
        List<JsonValue> values = value.asArray();
        assertEquals(1, values.size());
        JsonValue nested = values.get(0);
        assertTrue(nested.isArray());
        List<JsonValue> nestedValues = nested.asArray();
        assertEquals(2, nestedValues.size());
        assertEquals(42, nestedValues.get(0).asLong());
        assertEquals(23, nestedValues.get(1).asLong());
    }

    @Test public void parseBooleanArray() {
        JsonValue value = Json.parse("[ true,false ]");
        assertTrue(value.isArray());
        List<JsonValue> values = value.asArray();
        assertEquals(2, values.size());
        assertTrue(values.get(0).asBoolean());
        assertFalse(values.get(1).asBoolean());
    }

    @Test public void parseNumberArray() {
        JsonValue value = Json.parse("[23,42e8,3.141]");
        assertTrue(value.isArray());
        List<JsonValue> values = value.asArray();
        assertEquals(3, values.size());
        assertEquals(23, values.get(0).asLong());
        assertEquals(42e8, values.get(1).asDouble(), 0);
        assertEquals(3.141, values.get(2).asDouble(), 0);
    }

    @Test public void parseEmptyObject() {
        JsonValue value = Json.parse("{}");
        assertTrue(value.isObject());
        Map<String, JsonValue> values = value.asObject();
        assertEquals(0, values.size());
    }

    @Test public void parseObject() {
        JsonValue value = Json.parse("{\"foo\": true }");
        assertTrue(value.isObject());
        Map<String, JsonValue> values = value.asObject();
        assertEquals(1, values.size());
        assertTrue(values.containsKey("foo"));
        assertTrue(values.get("foo").asBoolean());
    }

    @Test public void parseObjectWithEscapedKey() {
        JsonValue value = Json.parse("{\"\\noo\\b\": true }");
        assertTrue(value.isObject());
        Map<String, JsonValue> values = value.asObject();
        assertEquals(1, values.size());
        assertTrue(values.containsKey("\noo\b"));
    }

    @Test public void parseNestedObject() {
        JsonValue value = Json.parse("{\"nested\": {\"foo\": 23}, \"bar\": false }");
        assertTrue(value.isObject());
        Map<String, JsonValue> values = value.asObject();
        assertEquals(2, values.size());
        JsonValue nested = values.get("nested");
        assertTrue(nested.isObject());
        Map<String, JsonValue> nestedValues = nested.asObject();
        assertEquals(23, nestedValues.get("foo").asLong());
    }

    @Test public void parseArrayOfObjects() {
        JsonValue value = Json.parse("[{\"foo\": 23, \"bar\": 44}, {\"foo\": 11, \"bar\": 64}]");
        assertTrue(value.isArray());
        for (JsonValue v : value.asArray()) {
            assertTrue(v.isObject());
            Set<String> keys = v.asObject().keySet();
            assertTrue(keys.contains("foo"));
            assertTrue(keys.contains("bar"));
        }
    }

}

package com.github.bannmann.whisperjson;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import com.github.bannmann.whisperjson.text.CharArrayText;
import com.github.bannmann.whisperjson.text.StringText;
import com.github.bannmann.whisperjson.text.Text;

class Literal extends Json
{
    static class Null extends Literal
    {
        @Override
        public boolean isNull()
        {
            return true;
        }

        @Override
        public void accept(Visitor visitor)
        {
            visitor.visitNull();
        }

        @Override
        public String toString()
        {
            return "null";
        }
    }

    static class Bool extends Literal
    {
        private final boolean value;

        Bool(boolean value)
        {
            this.value = value;
        }

        @Override
        public boolean isBoolean()
        {
            return true;
        }

        @Override
        public boolean asBoolean()
        {
            return value;
        }

        @Override
        public void accept(Visitor visitor)
        {
            visitor.visitBoolean(value);
        }

        @Override
        public String toString()
        {
            return Boolean.toString(value);
        }
    }

    static class Number extends Literal
    {
        private final String value;

        Number(String value)
        {
            this.value = value;
        }

        @Override
        public boolean isNumber()
        {
            return true;
        }

        @Override
        public int asInt()
        {
            return Integer.valueOf(value);
        }

        @Override
        public long asLong()
        {
            return Long.valueOf(value);
        }

        @Override
        public float asFloat()
        {
            return Float.valueOf(value);
        }

        @Override
        public double asDouble()
        {
            return Double.valueOf(value);
        }

        @Override
        public BigInteger asBigInteger()
        {
            return new BigInteger(value);
        }

        @Override
        public BigDecimal asBigDecimal()
        {
            return new BigDecimal(value);
        }

        @Override
        public void accept(Visitor visitor)
        {
            visitor.visitNumber(value);
        }

        @Override
        public String toString()
        {
            return value;
        }
    }

    static class Strng extends Literal
    {
        private final Text text;

        Strng(String string)
        {
            this.text = new StringText(string);
        }

        Strng(char[] chars)
        {
            this.text = new CharArrayText(chars);
        }

        @Override
        public boolean isString()
        {
            return true;
        }

        @Override
        public String asString()
        {
            return text.asString();
        }

        @Override
        public char[] asCharArray()
        {
            return text.asCharArray();
        }

        @Override
        public void accept(Visitor visitor)
        {
            visitor.visitString(text.asCharArray());
        }

        @Override
        public String toString()
        {
            return String.format("\"%s\"", StringCodec.escape(text));
        }
    }

    static class Array extends Literal
    {
        private final List<Json> list;

        Array(List<Json> values)
        {
            this.list = new JsonList<>(values);
        }

        @Override
        public boolean isArray()
        {
            return true;
        }

        @Override
        public List<Json> asArray()
        {
            return list;
        }

        @Override
        public void accept(Visitor visitor)
        {
            visitor.beginArray();
            for (Json value : list)
            {
                value.accept(visitor);
            }
            visitor.endArray();
        }

        @Override
        public String toString()
        {
            return list.toString();
        }
    }

    static class Object extends Literal
    {
        private final Map<String, Json> map;

        Object()
        {
            this.map = new JsonMap<>();
        }

        @Override
        public boolean isObject()
        {
            return true;
        }

        @Override
        public Map<String, Json> asObject()
        {
            return map;
        }

        @Override
        public void accept(Visitor visitor)
        {
            visitor.beginObject();
            for (Map.Entry<String, Json> entry : map.entrySet())
            {
                visitor.visitString(entry.getKey()
                    .toCharArray());
                entry.getValue()
                    .accept(visitor);
            }
            visitor.endObject();
        }

        @Override
        public String toString()
        {
            return map.toString();
        }
    }
}

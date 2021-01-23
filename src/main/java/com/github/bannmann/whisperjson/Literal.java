package com.github.bannmann.whisperjson;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.github.bannmann.whisperjson.text.CharArrayText;
import com.github.bannmann.whisperjson.text.StringText;
import com.github.bannmann.whisperjson.text.Text;

class Literal extends Json
{
    static class Null extends Literal
    {
        public static final Null INSTANCE = new Null();

        private Null()
        {
        }

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

        @Override
        public boolean equals(java.lang.Object obj)
        {
            if (obj instanceof Json)
            {
                return ((Json) obj).isNull();
            }
            return false;
        }

        @Override
        public int hashCode()
        {
            return getClass().getName()
                .hashCode();
        }
    }

    static class Bool extends Literal
    {
        public static final Bool TRUE = new Bool(true);
        public static final Bool FALSE = new Bool(false);

        public static Bool valueOf(boolean value)
        {
            return value ? TRUE : FALSE;
        }

        private final boolean value;

        private Bool(boolean value)
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

        @Override
        public boolean equals(java.lang.Object o)
        {
            if (o instanceof Json)
            {
                Json other = (Json) o;
                return other.isBoolean() && other.asBoolean() == value;
            }
            return false;
        }

        @Override
        public int hashCode()
        {
            return Boolean.valueOf(value)
                .hashCode();
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
        public boolean equals(java.lang.Object o)
        {
            if (o instanceof Json)
            {
                Json other = (Json) o;
                return other.isNumber() && Objects.equals(other.asBigDecimal(), asBigDecimal());
            }
            return false;
        }

        @Override
        public int hashCode()
        {
            return asBigDecimal().hashCode();
        }
    }

    abstract static class SensitiveLiteral extends Literal
    {
        @Override
        public final String toString()
        {
            return getClass().getSimpleName();
        }
    }

    static class Strng extends SensitiveLiteral
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
        public boolean equals(java.lang.Object o)
        {
            if (o instanceof Json)
            {
                Json other = (Json) o;
                return other.isString() && Arrays.equals(other.asCharArray(), asCharArray());
            }
            return false;
        }

        @Override
        public int hashCode()
        {
            return Arrays.hashCode(asCharArray());
        }
    }

    static class Array extends Literal
    {
        private final List<Json> list;

        Array(List<Json> values)
        {
            this.list = new ArrayList<>(values);
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
        public boolean equals(java.lang.Object o)
        {
            if (o instanceof Json)
            {
                Json other = (Json) o;
                return other.isArray() && Objects.equals(other.asArray(), asArray());
            }
            return false;
        }

        @Override
        public int hashCode()
        {
            return asArray().hashCode();
        }
    }

    static class Object extends SensitiveLiteral
    {
        private final Map<String, Json> map = new LinkedHashMap<>();

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
        public boolean equals(java.lang.Object o)
        {
            if (o instanceof Json)
            {
                Json other = (Json) o;
                return other.isObject() && Objects.equals(other.asObject(), asObject());
            }
            return false;
        }

        @Override
        public int hashCode()
        {
            return asObject().hashCode();
        }
    }
}

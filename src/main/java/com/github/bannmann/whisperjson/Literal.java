package com.github.bannmann.whisperjson;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

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
            return Integer.parseInt(value);
        }

        @Override
        public long asLong()
        {
            return Long.parseLong(value);
        }

        @Override
        public float asFloat()
        {
            return Float.parseFloat(value);
        }

        @Override
        public double asDouble()
        {
            return Double.parseDouble(value);
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
        public String toString()
        {
            return value;
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
}

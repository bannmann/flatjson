package com.github.bannmann.whisperjson;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

class Literal<J extends Json<?>> implements Json<J>
{
    private static class Null<J extends Json<?>> extends Literal<J>
    {
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
        public boolean equals(java.lang.Object o)
        {
            if (o instanceof Json)
            {
                Json<?> other = (Json<?>) o;
                return other.isNull();
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

    public static class ExposedNull extends Null<ExposedJson> implements ExposedJson
    {
        public static final ExposedNull INSTANCE = new ExposedNull();

        private ExposedNull()
        {
        }
    }

    public static class SafeNull extends Null<SafeJson> implements SafeJson
    {
        public static final SafeNull INSTANCE = new SafeNull();

        private SafeNull()
        {
        }
    }

    private static class Bool<J extends Json<?>> extends Literal<J>
    {
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
                Json<?> other = (Json<?>) o;
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

    public static class ExposedBool extends Bool<ExposedJson> implements ExposedJson
    {
        public static final ExposedBool TRUE = new ExposedBool(true);
        public static final ExposedBool FALSE = new ExposedBool(false);

        public static ExposedBool valueOf(boolean value)
        {
            return value ? TRUE : FALSE;
        }

        private ExposedBool(boolean value)
        {
            super(value);
        }
    }

    public static class SafeBool extends Bool<SafeJson> implements SafeJson
    {
        public static final SafeBool TRUE = new SafeBool(true);
        public static final SafeBool FALSE = new SafeBool(false);

        public static SafeBool valueOf(boolean value)
        {
            return value ? TRUE : FALSE;
        }

        private SafeBool(boolean value)
        {
            super(value);
        }
    }

    private static class Number<J extends Json<?>> extends Literal<J>
    {
        private final String value;

        public Number(String value)
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
                Json<?> other = (Json<?>) o;
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

    public static class ExposedNumber extends Number<ExposedJson> implements ExposedJson
    {
        public ExposedNumber(String value)
        {
            super(value);
        }
    }

    public static class SafeNumber extends Number<SafeJson> implements SafeJson
    {
        public SafeNumber(String value)
        {
            super(value);
        }
    }
}

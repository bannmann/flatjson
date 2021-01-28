package com.github.bannmann.whisperjson;

@SuppressWarnings("java:S1610")
abstract class Factory<J extends Json<J>, O extends Overlay<?>>
{
    public static final class Exposed extends Factory<ExposedJson, Overlay.Exposed>
    {
        @Override
        public ExposedJson create(Overlay.Exposed overlay, int element)
        {
            Type type = overlay.getType(element);
            switch (type)
            {
                case NULL:
                    return Null.Exposed.INSTANCE;
                case TRUE:
                    return Bool.Exposed.TRUE;
                case FALSE:
                    return Bool.Exposed.FALSE;
                case NUMBER:
                    return new Number.Exposed(overlay.getJson(element)
                        .asString());
                case STRING_ESCAPED:
                case STRING:
                    return new Strng.Exposed(overlay, element);
                case ARRAY:
                    return new Arry.Exposed(overlay, element, this);
                case OBJECT:
                    return new Objct.Exposed(overlay, element, this);
                default:
                    throw new ParseException("unknown type: " + type);
            }
        }
    }

    public static final class Safe extends Factory<SafeJson, Overlay.Safe>
    {
        @Override
        public SafeJson create(Overlay.Safe overlay, int element)
        {
            Type type = overlay.getType(element);
            switch (type)
            {
                case NULL:
                    return Null.Safe.INSTANCE;
                case TRUE:
                    return Bool.Safe.TRUE;
                case FALSE:
                    return Bool.Safe.FALSE;
                case NUMBER:
                    return new Number.Safe(overlay.getJson(element)
                        .asString());
                case STRING_ESCAPED:
                case STRING:
                    return new Strng.Safe(overlay, element);
                case ARRAY:
                    return new Arry.Safe(overlay, element, this);
                case OBJECT:
                    return new Objct.Safe(overlay, element, this);
                default:
                    throw new ParseException("unknown type: " + type);
            }
        }
    }

    public abstract J create(O overlay, int element);
}

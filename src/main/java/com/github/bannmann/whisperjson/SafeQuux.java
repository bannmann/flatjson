package com.github.bannmann.whisperjson;

public class SafeQuux implements Quux<SafeJson>
{
    @Override
    public SafeJson create(Overlay overlay, int element)
    {
        Json.Type type = overlay.getType(element);
        switch (type)
        {
            case NULL:
                return Literal.SafeNull.INSTANCE;
            case TRUE:
                return Literal.SafeBool.TRUE;
            case FALSE:
                return Literal.SafeBool.FALSE;
            case NUMBER:
                return new Literal.SafeNumber(overlay.getJson(element)
                    .asString());
            case STRING_ESCAPED:
            case STRING:
                return new Parsed.SafeStrng(overlay, element);
            case ARRAY:
                return new Structure.SafeArray(overlay, element, this);
            case OBJECT:
                return new Structure.SafeObject(overlay, element, this);
            default:
                throw new ParseException("unknown type: " + type);
        }
    }
}

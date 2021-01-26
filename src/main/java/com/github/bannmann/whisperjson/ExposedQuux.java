package com.github.bannmann.whisperjson;

public class ExposedQuux implements Quux<ExposedJson, Overlay.Exposed>
{
    @Override
    public ExposedJson create(Overlay.Exposed overlay, int element)
    {
        Json.Type type = overlay.getType(element);
        switch (type)
        {
            case NULL:
                return Literal.ExposedNull.INSTANCE;
            case TRUE:
                return Literal.ExposedBool.TRUE;
            case FALSE:
                return Literal.ExposedBool.FALSE;
            case NUMBER:
                return new Literal.ExposedNumber(overlay.getJson(element)
                    .asString());
            case STRING_ESCAPED:
            case STRING:
                return new Parsed.ExposedStrng(overlay, element);
            case ARRAY:
                return new Structure.ExposedArray(overlay, element, this);
            case OBJECT:
                return new Structure.ExposedObject(overlay, element, this);
            default:
                throw new ParseException("unknown type: " + type);
        }
    }
}

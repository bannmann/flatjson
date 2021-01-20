package org.zalando.flatjson;

public interface Visitor
{
    void visitNull();

    void visitBoolean(boolean value);

    void visitNumber(String value);

    void visitString(char[] value);

    void beginArray();

    void endArray();

    void beginObject();

    void endObject();
}

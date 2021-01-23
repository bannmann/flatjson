package com.github.bannmann.whisperjson;

import java.util.LinkedHashMap;
import java.util.Map;

import com.github.bannmann.whisperjson.text.StringText;

class JsonMap<K, V> extends LinkedHashMap<K, V>
{
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder("{");
        int count = 0;
        for (Map.Entry<K, V> entry : entrySet())
        {
            if (count > 0)
            {
                result.append(",");
            }
            StringText unescapedKey = new StringText((String) entry.getKey());
            String escapedKey = StringCodec.escape(unescapedKey)
                .asString();
            result.append(String.format("\"%s\":%s", escapedKey, entry.getValue()));
            count++;
        }
        result.append("}");
        return result.toString();
    }
}

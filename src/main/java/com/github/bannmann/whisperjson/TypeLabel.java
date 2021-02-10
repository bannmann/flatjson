package com.github.bannmann.whisperjson;

import java.util.Locale;

enum TypeLabel
{
    NULL,
    BOOLEAN,
    NUMBER,
    STRING,
    ARRAY,
    OBJECT;

    public Object getDisplayName()
    {
        return name().toLowerCase(Locale.ROOT);
    }
}

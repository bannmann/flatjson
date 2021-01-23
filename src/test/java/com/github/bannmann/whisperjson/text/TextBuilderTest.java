package com.github.bannmann.whisperjson.text;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TextBuilderTest
{
    @Test
    public void test()
    {
        TextBuilder builder = new TextBuilder(10);
        builder.append("foo");
        builder.append('b', 'a', 'r');
        assertEquals("foobar",
            builder.build()
                .asString());
    }

    @Test
    public void testCapacity()
    {
        TextBuilder builder = new TextBuilder(2);
        builder.append("capacity increased");
        assertEquals("capacity increased",
            builder.build()
                .asString());
    }

    @Test
    public void buildRepeatedly()
    {
        TextBuilder builder = new TextBuilder(10);
        builder.append("baz");
        assertEquals(builder.build(), builder.build());
    }
}

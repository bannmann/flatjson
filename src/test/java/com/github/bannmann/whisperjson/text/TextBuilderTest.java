package com.github.bannmann.whisperjson.text;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

public class TextBuilderTest
{
    @Test
    public void test()
    {
        TextBuilder builder = new TextBuilder(10);
        builder.append("foo");
        builder.append('b', 'a', 'r');
        String result = getString(builder);

        assertThat(result).isEqualTo("foobar");
    }

    @Test
    public void testCapacity()
    {
        TextBuilder builder = new TextBuilder(2);
        builder.append("capacity increased");
        String result = getString(builder);

        assertThat(result).isEqualTo("capacity increased");
    }

    @Test
    public void buildRepeatedly()
    {
        TextBuilder builder = new TextBuilder(10);
        builder.append("baz");
        String first = getString(builder);
        String second = getString(builder);

        assertThat(first).isEqualTo(second);
    }

    private String getString(TextBuilder builder)
    {
        return builder.build(StringText::new)
            .asString();
    }
}

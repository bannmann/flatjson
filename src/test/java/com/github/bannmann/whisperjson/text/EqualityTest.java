package com.github.bannmann.whisperjson.text;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

public class EqualityTest
{
    @Test
    public void implementationEquality()
    {
        assertEquality("foo");
        assertEquality("");
    }

    private void assertEquality(String value)
    {
        Text charArrayText = new CharArrayText(value.toCharArray());
        Text stringText = new StringText(value);

        assertThat(charArrayText).isEqualTo(stringText);
    }
}

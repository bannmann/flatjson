package com.github.bannmann.whisperjson;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

public class TestTextEquality
{
    @Test
    public void implementationEquality()
    {
        assertEquality("foo");
        assertEquality("");
    }

    private void assertEquality(String value)
    {
        Text<?> safeText = new Text.Safe(value.toCharArray());
        Text<?> exposedText = new Text.Exposed(value);

        assertThat(safeText).isEqualTo(exposedText);
    }
}

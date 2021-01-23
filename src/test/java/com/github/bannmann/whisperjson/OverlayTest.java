package com.github.bannmann.whisperjson;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class OverlayTest
{
    @Test(dataProvider = "sizes")
    public void calculateBlockSize(String label, int input, int expected)
    {
        int actual = Overlay.calculateBlockSize(input);
        assertThat(actual).isEqualTo(expected);
    }

    @DataProvider
    public static Object[][] sizes()
    {
        return new Object[][]{
            new Object[]{ "2 B", 2, 4 * 4 },
            new Object[]{ "160 B", 160, 4 * 10 },
            new Object[]{ "1600 B", 1600, 4 * 100 },
            new Object[]{ "16 KB", 16 * 1024, 4 * 1024 },
            new Object[]{ "20 KB", 20 * 1024, 4 * 1024 },
            new Object[]{ "555 KB", 555 * 1024, 4 * 1024 }
        };
    }
}

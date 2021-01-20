package org.zalando.flatjson;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class OverlayTest
{
    @Test
    public void calculateBlockSize()
    {
        assertEquals(4 * 4, Overlay.calculateBlockSize(2));
        assertEquals(4 * 10, Overlay.calculateBlockSize(160));
        assertEquals(4 * 100, Overlay.calculateBlockSize(1600));
        assertEquals(4 * 1024, Overlay.calculateBlockSize(16 * 1024));
        assertEquals(4 * 1024, Overlay.calculateBlockSize(20 * 1024));
        assertEquals(4 * 1024, Overlay.calculateBlockSize(555 * 1024));
    }
}

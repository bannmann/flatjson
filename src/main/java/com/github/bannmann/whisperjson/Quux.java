package com.github.bannmann.whisperjson;

public interface Quux<J extends Json<?>>
{
    J create(Overlay overlay, int element);
}

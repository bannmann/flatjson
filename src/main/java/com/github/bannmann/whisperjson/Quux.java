package com.github.bannmann.whisperjson;

public interface Quux<J extends Json<?>, O extends Overlay<?>>
{
    J create(O overlay, int element);
}

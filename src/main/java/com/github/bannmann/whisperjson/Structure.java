package com.github.bannmann.whisperjson;

abstract class Structure<J extends Json<J>, O extends Overlay<?>, F extends Factory<J, O, F>> implements Json<J>
{
    protected final O overlay;
    protected final int element;
    protected final F factory;

    protected Structure(O overlay, int element, F factory)
    {
        this.overlay = overlay;
        this.element = element;
        this.factory = factory;
    }
}

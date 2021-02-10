package com.github.bannmann.whisperjson;

abstract class Structure<J extends Json<J>, O extends Overlay<?>, F extends Factory<J, O, F>> extends Element<J, O>
{
    protected final F factory;

    protected Structure(O overlay, int element, F factory)
    {
        super(overlay, element);
        this.factory = factory;
    }
}

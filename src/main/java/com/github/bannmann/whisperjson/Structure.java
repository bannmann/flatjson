package com.github.bannmann.whisperjson;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class Structure<J extends Json<J>, O extends Overlay<?>, F extends Factory<J, O, F>> implements Json<J>
{
    protected final O overlay;
    protected final int element;
    protected final F factory;
}

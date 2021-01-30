package com.github.bannmann.whisperjson;

enum Type
{
    NULL
        {
            @Override
            public <J extends Json<J>, O extends Overlay<?>, F extends Factory<J, O, F>> J create(
                O overlay, int element, F factory)
            {
                return factory.createNull();
            }
        },

    TRUE
        {
            @Override
            public <J extends Json<J>, O extends Overlay<?>, F extends Factory<J, O, F>> J create(
                O overlay, int element, F factory)
            {
                return factory.createTrue();
            }
        },

    FALSE
        {
            @Override
            public <J extends Json<J>, O extends Overlay<?>, F extends Factory<J, O, F>> J create(
                O overlay, int element, F factory)
            {
                return factory.createFalse();
            }
        },

    NUMBER
        {
            @Override
            public <J extends Json<J>, O extends Overlay<?>, F extends Factory<J, O, F>> J create(
                O overlay, int element, F factory)
            {
                return factory.createNumber(overlay.getJson(element)
                    .asString());
            }
        },

    STRING
        {
            @Override
            public <J extends Json<J>, O extends Overlay<?>, F extends Factory<J, O, F>> J create(
                O overlay, int element, F factory)
            {
                return factory.createString(overlay, element);
            }
        },

    STRING_ESCAPED
        {
            @Override
            public <J extends Json<J>, O extends Overlay<?>, F extends Factory<J, O, F>> J create(
                O overlay, int element, F factory)
            {
                return factory.createString(overlay, element);
            }
        },

    ARRAY
        {
            @Override
            public <J extends Json<J>, O extends Overlay<?>, F extends Factory<J, O, F>> J create(
                O overlay, int element, F factory)
            {
                return factory.createArray(overlay, element, factory);
            }
        },

    OBJECT
        {
            @Override
            public <J extends Json<J>, O extends Overlay<?>, F extends Factory<J, O, F>> J create(
                O overlay, int element, F factory)
            {
                return factory.createObject(overlay, element, factory);
            }
        };

    public abstract <J extends Json<J>, O extends Overlay<?>, F extends Factory<J, O, F>> J create(
        O overlay, int element, F factory);
}

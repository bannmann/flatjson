package com.github.bannmann.whisperjson;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
enum Type
{
    NULL(TypeLabel.NULL)
        {
            @Override
            public <J extends Json<J>, O extends Overlay<T>, F extends Factory<J, O, F, T>, T extends Text<T>> J create(
                O overlay, int element, F factory)
            {
                return factory.createNull(overlay, element);
            }
        },

    TRUE(TypeLabel.BOOLEAN)
        {
            @Override
            public <J extends Json<J>, O extends Overlay<T>, F extends Factory<J, O, F, T>, T extends Text<T>> J create(
                O overlay, int element, F factory)
            {
                return factory.createTrue(overlay, element);
            }
        },

    FALSE(TypeLabel.BOOLEAN)
        {
            @Override
            public <J extends Json<J>, O extends Overlay<T>, F extends Factory<J, O, F, T>, T extends Text<T>> J create(
                O overlay, int element, F factory)
            {
                return factory.createFalse(overlay, element);
            }
        },

    NUMBER(TypeLabel.NUMBER)
        {
            @Override
            public <J extends Json<J>, O extends Overlay<T>, F extends Factory<J, O, F, T>, T extends Text<T>> J create(
                O overlay, int element, F factory)
            {
                return factory.createNumber(overlay, element);
            }
        },

    STRING(TypeLabel.STRING)
        {
            @Override
            public <J extends Json<J>, O extends Overlay<T>, F extends Factory<J, O, F, T>, T extends Text<T>> J create(
                O overlay, int element, F factory)
            {
                return factory.createString(overlay, element);
            }
        },

    STRING_ESCAPED(TypeLabel.STRING)
        {
            @Override
            public <J extends Json<J>, O extends Overlay<T>, F extends Factory<J, O, F, T>, T extends Text<T>> J create(
                O overlay, int element, F factory)
            {
                return factory.createString(overlay, element);
            }
        },

    ARRAY(TypeLabel.ARRAY)
        {
            @Override
            public <J extends Json<J>, O extends Overlay<T>, F extends Factory<J, O, F, T>, T extends Text<T>> J create(
                O overlay, int element, F factory)
            {
                return factory.createArray(overlay, element, factory);
            }
        },

    OBJECT(TypeLabel.OBJECT)
        {
            @Override
            public <J extends Json<J>, O extends Overlay<T>, F extends Factory<J, O, F, T>, T extends Text<T>> J create(
                O overlay, int element, F factory)
            {
                return factory.createObject(overlay, element, factory);
            }
        };

    @Getter
    private final TypeLabel label;

    public abstract <J extends Json<J>, O extends Overlay<T>, F extends Factory<J, O, F, T>, T extends Text<T>> J create(
        O overlay, int element, F factory);
}
